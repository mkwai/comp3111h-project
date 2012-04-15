package com.Alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;


public class Alarms {
	
	static private ArrayList<AlarmBean> alarmInfo = new ArrayList<AlarmBean>();
	static private ArrayList<String> alarmID =  new ArrayList<String>();
	static private int assignId = 0;
	static private int eventCount = 0;
		
    static private void setAlarm(Context context, AlarmBean alarmAdd){
    		
    		Log.i("Alarm", alarmAdd.getTitle()+"  "+ alarmAdd.getID() 
    				+ "  " + alarmAdd.getMillisecond() + "  " + System.currentTimeMillis());
			Intent intent = new Intent(context, AlamrReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(context, 
					alarmAdd.getID(), intent, 0);
			AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
			//set Alarm
			am.set(AlarmManager.RTC_WAKEUP, alarmAdd.getMillisecond(), pi);
    }
    
    static public void addAlarm(Context context, 
    		String eventID, String title, long milliS, boolean event){
    	
    	if(!alarmID.contains(eventID) && (milliS - System.currentTimeMillis())>0){
    		AlarmBean alarmAdd = new AlarmBean(assignId++, title, milliS, event);
    		alarmInfo.add(alarmAdd);
    		alarmID.add(eventID);
    		setAlarm(context, alarmAdd);
    	}
    	else {
    		Log.i("alarm", "add fail");
    	}
    }
    
    static public int getCount(){
    	return eventCount++;
    }
    
    static public String getTitle(int position){
    	return alarmInfo.get(position).getTitle();
    }
    
    static public int getID (int position){
    	return alarmInfo.get(position).getID();
    }
    
    static public boolean isEvent(int position){
    	return alarmInfo.get(position).isEvent();
    }

}
