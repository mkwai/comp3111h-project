package com.calendar;

import com.localdb.*;
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

public class AndroidCalendar2Activity extends Activity {


	/** Called when the activity is first created. */
	// CalendarView calendarV;
	// Button bGoToToday, bAddEvent;
	private static MyDataBase mdb;

	public static MyDataBase getDB() {
		return mdb;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// first page showing is the monthly view
		startActivity(new Intent("com.calendar.MONTHLYVIEW"));

		// create or connect database
		mdb = new MyDataBase(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startActivity(new Intent("com.calendar.MONTHLYVIEW"));

	}



}