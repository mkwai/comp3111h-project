package com.calendar;

import java.util.Calendar;


import com.test2.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.CalendarView.OnDateChangeListener;

public class MonthlyView extends Activity {

	CalendarView calendarV;
	Button bGoTo, bAddEvent, bSynchronous,bDaily,bMonthlyb,bTodoList,bGoogle;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);

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
		bSynchronous = (Button) findViewById(R.id.bSynchronous);
		bSynchronous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.calendar.SYNCHRONOUS"));
			}
		});
	
		//button GoTo
		bGoTo = (Button) findViewById(R.id.bGoTo);
		bGoTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(1);
			}
		});

		//button Daily
		bDaily = (Button) findViewById(R.id.bDaily);
		bDaily.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.calendar.DAILYVIEW"));
			}
		});
		
		//button TodoList
		bTodoList = (Button) findViewById(R.id.bTodoList);
		bTodoList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.calendar.TODOLIST"));
			}
		});
		
		//button Google
		bGoogle = (Button) findViewById(R.id.bGoogle);
		bGoogle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.calendar.GOOGLESYNC"));
			}
		});
			
		
		// calendar view
		calendarV = (CalendarView) findViewById(R.id.calendarV);
		calendarV.setEnabled(true);
		calendarV.setShowWeekNumber(false);
		
		//setting the minimum date of the calendar
		Calendar minDate = Calendar.getInstance();
		minDate.set(1950, 0, 1, 0, 0, 0);			//0 = Jan
		calendarV.setMinDate(minDate.getTimeInMillis());
		
		//setting the maximum date of the calendar
		Calendar maxDate = Calendar.getInstance();
		maxDate.set(2050, 11, 31, 23, 59, 59);		//11 = Dec
		calendarV.setMaxDate(maxDate.getTimeInMillis());
		
		// call when the selected date changes		
		calendarV.setOnDateChangeListener(new OnDateChangeListener() {

			@Override
			public void onSelectedDayChange(CalendarView arg0, int year,
					int month, int dayOfMonth) {
				//for dailyview
				DailyView.setDailyYear(year);
				DailyView.setDailyMonth(month);
				DailyView.setDailyDayOfMonth(dayOfMonth);
				//for add event
				AddEvent.currentDateCalendar.set(Calendar.YEAR, year);
				AddEvent.currentDateCalendar.set(Calendar.MONTH, month);
				AddEvent.currentDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

			}
		});
		
	}

	// call when create dialog, for GoTo use
	protected Dialog onCreateDialog(int id) {

		// listener for setting date in starting date dialog
		OnDateSetListener goToDateSetListener = new OnDateSetListener() {

			// use when "set" press
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar date = Calendar.getInstance();
				date.set(year, monthOfYear, dayOfMonth);
				calendarV.setDate(date.getTimeInMillis());

			}
		};
		
		return  new DatePickerDialog(this, goToDateSetListener,
				Calendar.getInstance().get(Calendar.YEAR),
				Calendar.getInstance().get(Calendar.MONTH),
				Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
	}
	
	
//FOR menu	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_addEvent:
			startActivity(new Intent("com.calendar.ADDEVENT"));
			return true;
		case R.id.menu_synchronous:
			startActivity(new Intent("com.calendar.SYNCHRONOUS"));
			return true;
		case R.id.menu_import:
//			startActivity(new Intent("com.calendar.IMPORT"));
			return true;
		case R.id.menu_export:
//			startActivity(new Intent("com.calendar.EXPORT"));
			return true;
		}
		return false;

	}
}
