package com.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
		context.startService(Alert);

	}

}

