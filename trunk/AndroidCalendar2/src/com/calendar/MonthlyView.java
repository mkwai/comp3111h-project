package com.calendar;

import com.test2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;

public class MonthlyView extends Activity {

	CalendarView calendarV;
	Button bGoToToday, bAddEvent, bSynchronous;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

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
				// setContentView(R.layout.addevent);
				startActivity(new Intent("com.calendar.ADDEVENT"));
			}

		});

		// button synchronous
		bSynchronous = (Button)findViewById(R.id.bSynchronous);
		bSynchronous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.calendar.SYNCHRONOUS"));

			}
			
		});
		
		// calendar view
		calendarV = (CalendarView) findViewById(R.id.calendarV);
		calendarV.setEnabled(true);
		calendarV.setShowWeekNumber(false);
		calendarV.setOnDateChangeListener(new OnDateChangeListener() {

			@Override
			public void onSelectedDayChange(CalendarView arg0, int year,
					int month, int dayOfMonth) {
				startActivity(new Intent("com.calendar.DAILYVIEW"));
				DailyView.setDailyYear(year);
				DailyView.setDailyMonth(month);
				DailyView.setDailyDayOfMonth(dayOfMonth);
			}

		});

	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);
		return true;
	}
 
	public boolean onOptionItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_addEvent:
			startActivity(new Intent("com.calendar.ADDEVENT"));
			return true;
		case R.id.menu_dailyView:
			startActivity(new Intent("com.calendar.DAILYVIEW"));
					return true;
		}
		return false;

	}
}
