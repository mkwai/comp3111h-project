package com.calendar;

import com.test2.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class DailyView extends Activity {

	public static int dailyYear, dailyMonth, dailyDayOfMonth;
	TextView dailyview_today;
	TextView event1, event2, event3;
	TextView twelve_am;
	RelativeLayout relativeLayout;

	protected static void setDailyYear(int year) {
		dailyYear = year;
	}

	protected static void setDailyMonth(int month) {
		dailyMonth = month + 1;
	}

	protected static void setDailyDayOfMonth(int dayOfMonth) {
		dailyDayOfMonth = dayOfMonth;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dailyview);
		relativeLayout = (RelativeLayout) findViewById(R.id.daily_relativelayout);
		dailyview_today = (TextView) findViewById(R.id.dailyview_today);
		dailyview_today.setText("Today is " + dailyDayOfMonth + " / "
				+ dailyMonth + " / " + dailyYear);

		twelve_am = (TextView) findViewById(R.id.twelve_am);

		event1 = new TextView(this);
		event2 = new TextView(this);
		event3 = new TextView(this);

		relativeLayout.addView(event1);
		relativeLayout.addView(event2);
		relativeLayout.addView(event3);

		// get event content + location
		event1.setText("event1");
		event2.setText("event2");
		event3.setText("event3");

		event1.setX(100);
		event1.setY(twelve_am.getY());

		event2.setX(100);
		event2.setY(twelve_am.getY()/* + event time changed to minutes */);
		
		event3.setX(100);
		event3.setY(twelve_am.getY()/* + event time changed to minutes */);

	}

}
