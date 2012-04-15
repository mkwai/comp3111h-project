package com.Alarm;

import java.util.Calendar;

import com.test2.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class Test extends Activity {
    /** Called when the activity is first created. */
	//private TextView tv = null;
	private Button btn_set = null;

	private Calendar c = null;
	static public String eventTitle = "temp"; 
	static public int eventID = 10;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);
        //tv = (TextView) this.findViewById(R.id.setAlarmView);
        btn_set = (Button) this.findViewById(R.id.bAlarm);
        c = Calendar.getInstance();
        btn_set.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				c.setTimeInMillis(System.currentTimeMillis());
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				new TimePickerDialog(Test.this,new TimePickerDialog.OnTimeSetListener(){

					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						// TODO Auto-generated method stub
						c.setTimeInMillis(System.currentTimeMillis());
						c.set(Calendar.HOUR_OF_DAY, hourOfDay);
						c.set(Calendar.MINUTE, minute);
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);
						Intent intent = new Intent(Test.this,AlamrReceiver.class);
						PendingIntent pi = PendingIntent.getBroadcast(Test.this, 1, intent, 0);
						AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
						am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);//set Alarm
						
						//am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), (10*1000), pi);
						//tv.setText("Set alarm "+hourOfDay+":"+minute);
						eventID++;
						
						finish();
					}
					
				},hour,minute,true).show();
			}
        	
        });
        
    }
}
