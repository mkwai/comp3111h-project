package com.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationAlarmReceiver extends BroadcastReceiver{
	private Intent LAlert = new Intent();
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		LAlert.setAction("AlermService");
		context.startService(LAlert);

	}
}
