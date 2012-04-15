package com.Alarm;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import com.calendar.AndroidCalendar2Activity;
import com.test2.R;

import android.app.Service;
import android.content.Intent;

import android.os.IBinder;
import android.util.Log;

public class LocationBasedAlarm extends Service{
	private Timer timer = new Timer();
	
	public void onCreate()
	{
		super.onCreate();
		Log.i("SERVICE", "onCreate................");
	}
	
	public void onStart(Intent intent, int startid){
		Log.i("SERVICE", "Start!!!!!!!!!!!!");
		timer.scheduleAtFixedRate(new trackLocationAlert(), 0, 10000);
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
				.fetchConditional("TimeTable", "location <> '' AND reminder = '1' AND milliS > '" + System.currentTimeMillis()+"'");
			if(latObject != null){
				for(int i = 0; i<latObject.length();i++){
					JSONObject temp = (JSONObject) latObject.opt(i);
					try{
					Log.i("locationBaseAlarm", temp.getString("title"));
					}catch(Exception e){}
				}
			}
		}
	}
}
