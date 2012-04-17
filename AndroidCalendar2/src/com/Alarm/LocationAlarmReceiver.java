package com.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationAlarmReceiver extends BroadcastReceiver{
	private Intent LAlert = new Intent();
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.v("LOC", "receive");
		LAlert.setAction("LAlermService");
		
		LAlert.putExtras(intent.getExtras());
		context.startService(LAlert);

	}
}
