package com.calendar;

import com.localdb.*;
import com.test2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;

public class AndroidCalendar2Activity extends Activity {
	/** Called when the activity is first created. */
	CalendarView calendarV;
	Button bGoToToday, bAddEvent;
	private static MyDataBase mdb;
	
	public static MyDataBase getDB(){
		return mdb;
	}
	
	// testing123
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// create or connect database
		mdb = new MyDataBase(this);
		
		// button GoToToday
		bGoToToday = (Button) findViewById(R.id.bGoToToday);
		bGoToToday.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				calendarV.setDate(System.currentTimeMillis());
			}

		});
		
		// button AddEvent
		bAddEvent = (Button) findViewById(R.id.bAddEvent);
		bAddEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//setContentView(R.layout.addevent);
				startActivity(new Intent("com.calendar.ADDEVENT"));
			}

		});

		// calendar view
		calendarV = (CalendarView) findViewById(R.id.calendarV);
		calendarV.setEnabled(true);
		calendarV.setShowWeekNumber(false);

	}

}