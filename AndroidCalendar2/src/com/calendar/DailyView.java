package com.calendar;


import com.test2.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

public class DailyView extends Activity {

	public static int dailyYear, dailyMonth, dailyDayOfMonth;
	TextView dailyview_today;
	TableLayout dailyV;
	
	
	protected static void setDailyYear(int year) {
		dailyYear = year;
	}

	protected static void setDailyMonth(int month) {
		dailyMonth = month;
	}

	protected static void setDailyDayOfMonth(int dayOfMonth) {
		dailyDayOfMonth = dayOfMonth +1;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dailyview);
		dailyview_today = (TextView) findViewById(R.id.dailyview_today);
		dailyview_today.setText("Today is " + dailyDayOfMonth + " / " + dailyMonth + " / " +dailyYear);
		dailyV = (TableLayout)findViewById(R.id.dailyV);
		dailyV.setColumnShrinkable(2, true);
		dailyV.setColumnStretchable(2, true);
	}

}
