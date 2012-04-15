package com.Alarm;

import java.io.IOException;

import com.test2.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * 
 * setup for alert user 
 *
 */
public class AlamrReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		//Notification 
		String tickerText = "Event " + Test.eventTitle + " start.";
		tickerText = tickerText+"\n"+tickerText+"\n"+tickerText;
		//Notification picture
		int icon = R.drawable.ic_launcher;
		 
		String contentTitle="My Daily Assistant notification";
		String contentText="Event " + Test.eventTitle + " start.";
		 
		//Notification for activity
		Intent notificationIntent = new Intent(context, context.getClass());
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, 
			notificationIntent, 0);
		 
		//bulid Notifcation
		Notification notification = new Notification(icon, tickerText, System.currentTimeMillis());
		
		//Notification.FLAG_AUTO_CANCEL -> Notification disappear after clicked
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		//show notification
		mNotificationManager.notify(Test.eventID, notification);
		
		Toast.makeText(context, contentText, Toast.LENGTH_LONG).show();  
		
		AssetManager assetMgr = context.getAssets();
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
		
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(new long[]{2000,1000,2000,1000}, 0);
		
	}

}

