package com.Alarm;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.calendar.AndroidCalendar2Activity;
import com.location.CityBean;
import com.location.TravelingDuration;
import com.location.Utils;
import com.test2.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationBasedAlarm extends Service{
/*	final static String TIMEREQUIRED = "timeRequired";
	final static String DESTINATION = "destination";*/
	private Timer timer1 = new Timer();
	private Timer timer2 = new Timer();
	final static int LocID = 2147483647;
	private double mylat = 0.0;
	private double mylng = 0.0;
	static private String STitle;
	static private String SDesAddress;
	static private String STimeRequired;
	String args;
	TravelingDuration TD;
	private Intent intent;
		
	public void onCreate()
	{
		super.onCreate();
		Log.i("SERVICE", "onCreate................");
	}
	
	public void onStart(Intent intent, int startid){
		Log.i("SERVICE", "Start!!!!!!!!!!!!");
		this.intent = intent;
		timer1.scheduleAtFixedRate(new trackLocationAlert(), 0, 10000);
		timer2.scheduleAtFixedRate(new currentLocation(), 0, 300000);
		
	}
	
	public void onDestroy()
	{
		Log.i("SERVICE", "onDestroy.................");
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	

	private class trackLocationAlert extends TimerTask{
		
		public void run() {
			Log.v("Loc", "run");
			
			JSONArray latObject = AndroidCalendar2Activity.getDB()
				.fetchConditional("TimeTable", "location <> '' AND reminder = '1' AND milliS > '"+ System.currentTimeMillis()+"'", "milliS");
			if(latObject != null){
				
				Log.v("Loc", "latObject != null");
				for(int i = 0 ; i<latObject.length();i++){
					try{
						if(latObject.getJSONObject(i)!=null){
							Log.v("Loc", "JSONObject(" + i + ")!=null");
							
							JSONObject tempObj = (JSONObject) latObject.getJSONObject(0);
							
							String title = tempObj.getString("title");
							String des = tempObj.getString("location");
							long milliS = tempObj.getLong("milliS");
							Log.v("Loc", title +" "+ des +" "+ milliS);
							
							//Core part!!!
							TravelingDuration TD = getTD(des);

							Log.i("Loc TD in run", TD.getTimeRequired());

							
							if( TD.getTotalSecond() != -1 && 
									(milliS <= System.currentTimeMillis() + TD.getMilliS()) ){
								Intent intent = new Intent(LocationBasedAlarm.this, LocationAlarmReceiver.class);
								
								/*transfer Info*/
								/*Bundle bundle = new Bundle();
								bundle.putString(Alarms.TITLE, title);
								bundle.putString(TIMEREQUIRED, TD.getTimeRequired());
								bundle.putString(DESTINATION, TD.getDestination());
								intent.putExtras(bundle);*/
								
								setTitle(title);
								
								setDestination(TD.getDestination());
								
								setTimeRequired(TD.getTimeRequired());
								
								PendingIntent pi = PendingIntent.getBroadcast(LocationBasedAlarm.this, 
										LocID, intent, 0);
								AlarmManager am = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
								//set Alarm
								am.set(AlarmManager.RTC_WAKEUP, milliS - TD.getMilliS() , pi);
								
								break;
							}
						}
						else{
							Log.v("Loc", "JSONObject(0)==null");
							//cancel alarm if currently no object
							Intent intent = new Intent(LocationBasedAlarm.this,LocationAlarmReceiver.class);
							PendingIntent pi = PendingIntent.getBroadcast(LocationBasedAlarm.this, LocID, intent, 0);
							AlarmManager am = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
							am.cancel(pi);
						}
					}catch(Exception e){e.printStackTrace();}
				}
			}
		}
		
		private TravelingDuration getTD(String des){
			TravelingDuration TD = new TravelingDuration(des);
			String inputAddress = des.replace(' ', '+');
			
			if(mylat != 0.0 && mylng != 0.0){
				args = "http://maps.google.com/maps/api/directions/json?origin=" + inputAddress
				+ "&destination=" + mylat + "," + mylng + "&sensor=false";
				
				Log.i("Loc", "in MY" + " " + args);
			}
			else{			
				//between ust
				args = "http://maps.google.com/maps/api/directions/json?origin=" + inputAddress
				+ "&destination=" + 22.336659+ "," + 114.266725 + "&sensor=false";

				Log.i("Loc", "testing" + " " + args);
			}
			
			TD = Utils.getDuration(args, des);
			Log.i("Loc TD", TD.getTimeRequired());
			
			return TD;
		}
	}
	
	static public String getTitle(){
		return STitle;
	}
	
	static public String getDestination(){
		return SDesAddress;
	}
	
	static public String getTimeRequired(){
		return STimeRequired;
	}
	
	static public void setTitle(String title){
		STitle = title;
	}
	
	static public void setDestination(String destination){
		SDesAddress = destination;
	}
	
	static public void setTimeRequired(String time){
		STimeRequired = time;
	}
	
	private class currentLocation extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			Log.i("currentLocation", "in");
			
			Criteria criteria = new Criteria();
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			String bestProvider = locationManager.getBestProvider(criteria, false);
			if(bestProvider != null){
				Location location = locationManager.getLastKnownLocation(bestProvider);
				if(location != null){
					
					Log.i("currentLocation", "get");
					mylat = location.getLatitude();
					mylng = location.getLongitude();
				}
				else{
					Log.i("currentLocation", "not get");
				}
			}
			else Log.i("currentLocation", "bestProvider = null");
			
			
		}
		
	}
}
