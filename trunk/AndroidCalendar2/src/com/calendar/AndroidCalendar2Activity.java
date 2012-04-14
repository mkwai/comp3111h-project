package com.calendar;

import com.exina.android.calendar.CalendarActivity;

import com.googlesync.GoogleSync;
import com.localdb.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AndroidCalendar2Activity extends Activity {


	/** Called when the activity is first created. */
	// CalendarView calendarV;
	// Button bGoToToday, bAddEvent;
	private static MyDataBase mdb;
	private static GoogleSync mgs;
	
	public static MyDataBase getDB() { 
		return mdb;
	}

	public static GoogleSync getGS() {
		return mgs;
	}
	public static void clearGS() {
		mgs= new GoogleSync();
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set to full screen

		// first page showing is the monthly view
//		startActivity(new Intent("com.calendar.MONTHLYVIEW"));
		startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(null, CalendarActivity.MIME_TYPE));
		// create or connect database
		mdb = new MyDataBase(this);
		
		// get an instance of GoogleSync, username and password will be set later
		mgs = new GoogleSync();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	//	startActivity(new Intent("com.calendar.MONTHLYVIEW"));

	}



}