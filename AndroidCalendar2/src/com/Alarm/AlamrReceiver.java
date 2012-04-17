package com.Alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 
 * setup for alert user 
 *
 */
public class AlamrReceiver extends BroadcastReceiver {
	private Intent Alert = new Intent();
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Alert.setAction("AlermService");
		
		/*transfer info*/
		Bundle bundle = new Bundle();
		bundle.putString(Alarms.TITLE, intent.getExtras().getString(Alarms.TITLE));
		bundle.putBoolean(Alarms.ISEVENT, intent.getExtras().getBoolean(Alarms.ISEVENT));
		
		Alert.putExtras(bundle);
		context.startService(Alert);

	}

}

