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

public class AlarmService extends Service{

	
	static String site = "";
	
	public void onCreate()
	{
		super.onCreate();
		Log.i("SERVICE", "onCreate................");
		
		//Toast.makeText(PopupMessage.this, "onCreate................", Toast.LENGTH_LONG).show();
	}
	
	public void onStart(Intent intent, int startid){
		Log.i("SERVICE", "Start!!!!!!!!!!!!");
		
		//Toast.makeText(PopupMessage.this, "You should not access " + site + " in this time!" , Toast.LENGTH_LONG).show();
		Alert();
	}
	
	public void onDestroy()
	{
		Log.i("SERVICE", "onDestroy.................");
		//Toast.makeText(PopupMessage.this, "onDestroy.................", Toast.LENGTH_LONG).show();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void Alert(){
		Log.i("duck", "left");
		
		int temp = Alarms.getCount();
		String title = Alarms.getTitle(temp);
		int id = Alarms.getID(temp);
		String top =  Alarms.isEvent(temp)? "Event " : "Task ";
		String end = Alarms.isEvent(temp)? " starts." : " needs to be finished!";
		Log.i("right", title + " "+ id);
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		//Notification 
		String tickerText = top + title + end;
		tickerText = tickerText+"\n"+tickerText+"\n"+tickerText;
		//Notification picture
		int icon = Alarms.isEvent(temp) ? R.drawable.event : R.drawable.task;
		 
		String contentTitle="My Daily Assistant notification";
		String contentText=top + title + end;
		 
		//Notification for activity
		Intent notificationIntent = new Intent(AlarmService.this, this.getClass());
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(AlarmService.this, 0, 
			notificationIntent, 0);
		 
		//bulid Notifcation
		Notification notification = new Notification(icon, tickerText, System.currentTimeMillis());
		
		//Notification.FLAG_AUTO_CANCEL -> Notification disappear after clicked
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(AlarmService.this, contentTitle, contentText, contentIntent);
		//show notification
		mNotificationManager.notify(id, notification);
		
		Toast.makeText(AlarmService.this, contentText, Toast.LENGTH_LONG).show();  
		
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
