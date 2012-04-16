package com.Alarm;

import java.io.IOException;

import com.test2.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class LocationAlarmService extends Service{
	
	public void onCreate()
	{
		super.onCreate();
		Log.i("SERVICE", "onCreate................");
		
	}
	
	public void onStart(Intent intent, int startid){
		Log.i("SERVICE", "Start!!!!!!!!!!!!");
		
		Alert();
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
	
	private void Alert(){
		Log.i("duck", "right");
		
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		//Notification 
		String tickerText = /*top + title + end*/ "LAlarm";
		tickerText = tickerText+"\n"+tickerText+"\n"+tickerText;
		//Notification picture
		int icon = R.drawable.event;
		 
		String contentTitle="My Daily Assistant notification";
		String contentText = /*top + title + end*/ "LAlarm";
		 
		//Notification for activity
		Intent notificationIntent = new Intent(LocationAlarmService.this, this.getClass());
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(LocationAlarmService.this, 0, 
			notificationIntent, 0);
		 
		//bulid Notifcation
		Notification notification = new Notification(icon, tickerText, System.currentTimeMillis());
		
		//Notification.FLAG_AUTO_CANCEL -> Notification disappear after clicked
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(LocationAlarmService.this, contentTitle, contentText, contentIntent);
		//show notification
		mNotificationManager.notify(2147483646, notification);
		
		Toast.makeText(LocationAlarmService.this, contentText, Toast.LENGTH_LONG).show();  
		
		AssetManager assetMgr = this.getAssets();
		MediaPlayer mediaPlayer = new MediaPlayer();
		
		try {
			// get media file
			AssetFileDescriptor afd = assetMgr.openFd("ICQ.mp3");
			mediaPlayer.reset();
			// get the media resource
			mediaPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
			
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(new long[]{2000,1000,2000,1000}, 0);
		
	}

}
