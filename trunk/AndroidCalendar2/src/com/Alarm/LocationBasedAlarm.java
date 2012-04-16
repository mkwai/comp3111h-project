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
import android.os.IBinder;
import android.util.Log;

public class LocationBasedAlarm extends Service{
	private Timer timer1 = new Timer();
//	private Timer timer2 = new Timer();
//	private LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	final private int LocID = 2147483647;
	private double mylat = 0.0;
	private double mylng = 0.0;
	static private ArrayList<CityBean> locationList = new ArrayList<CityBean>();
	static private ArrayList<String> locationControl = new ArrayList<String>();
	private String title;
	private String desAddress;
	private long milliSecond;
	String args = new String();
	TravelingDuration TD;
	
	public void onCreate()
	{
		super.onCreate();
		Log.i("SERVICE", "onCreate................");
	}
	
	public void onStart(Intent intent, int startid){
		Log.i("SERVICE", "Start!!!!!!!!!!!!");
		timer1.scheduleAtFixedRate(new trackLocationAlert(), 0, 10000);
		//timer2.scheduleAtFixedRate(new currentLocation(), 0, 300000);
		
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
			JSONArray latObject = AndroidCalendar2Activity.getDB()
							.fetchConditional("TimeTable", "location <> '' AND reminder = '1' ", "milliS");
//				.fetchConditional("TimeTable", "location <> '' AND reminder = '1' AND milliS > '"+ System.currentTimeMillis()+"'", "milliS");
			
			if(latObject != null){
				JSONObject temp = (JSONObject) latObject.opt(0);
				if(temp != null){
					try{
						title = temp.getString("title");
						milliSecond = temp.getLong("milliS");
						desAddress = temp.getString("location");
						Log.i("locationBaseAlarm", title + " "+ desAddress + " "+ milliSecond);
					}catch(Exception e){
						Log.i("LA", "here");
						e.printStackTrace();
					}
					
					desAddress = desAddress.replace(' ', '+');
					
/*					if(mylat != 0.0 && mylng != 0.0){
						args = "http://maps.google.com/maps/api/directions/json?origin=" + desAddress
						+ "&destination=" + mylat + "," + mylng + "&sensor=false";
						
						Log.i("LA", "in MY");
					}
					else{		*/		
						//between ust
						String args = "http://maps.google.com/maps/api/directions/json?origin=" + desAddress
						+ "&destination=" + 22.336659+ "," + 114.266725 + "&sensor=false";
//					}
					
					TD = new TravelingDuration(Utils.getDuration(args, desAddress));
					Log.i("LocAlarm", "ms " + milliSecond 
							+"TD  "+ TD.getTotalSecond()*1000 + 
							"current  " + System.currentTimeMillis());
//					if(TD.getTotalSecond() != -1 && 
//							(milliSecond <= System.currentTimeMillis() + TD.getTotalSecond()*1000 /*to millisecond*/)){
			    		Log.i("LocAlarm", title +"  "+ TD.getTotalSecond() + "  " + System.currentTimeMillis());
						Intent intent = new Intent(LocationBasedAlarm.this, LocationAlarmReceiver.class);
						PendingIntent pi = PendingIntent.getBroadcast(LocationBasedAlarm.this, 
								/*LocID*/0, intent, 0);
						AlarmManager am = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
						//set Alarm
						am.set(AlarmManager.RTC_WAKEUP, /*System.currentTimeMillis() - TD.getTotalSecond()*/0 , pi);
//					}
				}
				
			}
			else{
/*				//cancel alarm 
				Intent intent = new Intent(LocationBasedAlarm.this,LocationAlarmReceiver.class);
				PendingIntent pi = PendingIntent.getBroadcast(LocationBasedAlarm.this, LocID, intent, 0);
				AlarmManager am = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
				am.cancel(pi);
				
				Log.i("Loc Alarm", "cancel");*/
			}
		}
	}
	
	public static void addList(CityBean CB){
		if(!locationControl.contains(CB.getCityName())){
			locationControl.add(CB.getCityName());
			locationList.add(CB);
		}
	}
/*	
	private class currentLocation extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Criteria criteria = new Criteria();
			String bestProvider = locationManager.getBestProvider(criteria, false);
			Location location = locationManager.getLastKnownLocation(bestProvider);
			if(location != null){
				mylat = location.getLatitude();
				mylng = location.getLongitude();
			}
			
			
		}
		
	}*/
}
