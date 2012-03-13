package com.calendar;

import java.lang.reflect.Array;
import java.util.Date;

import com.commTimeCal.TimeCal;
import com.test2.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class DailyView extends Activity {

	public static int dailyYear, dailyMonth, dailyDayOfMonth;
	TextView dailyview_today;
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
		
		testAddLabels(this);
		
		/* get event content + location
		event1.setText("event1");
		event1.setX(100);
		event1.setY(twelve_am.getY());
		*/

	}
	
	public void testAddLabels(Context t){
		String sdate = dailyYear + "" + getZero(dailyMonth) + "" +getZero(dailyDayOfMonth);
		String output[] = TimeCal.getInterval(sdate);
		if(output.length!=0){
			EventItem events[] = new EventItem[output.length/3];
			for(int i = 0;i<output.length;i+=3){
				long stime = Long.parseLong(output[i+1]);
				long etime = Long.parseLong(output[i+2]);
				events[i/3] = new EventItem(t,stime,etime);
				relativeLayout.addView(events[i/3]);
			}
		}
	}

	private class EventItem extends TextView{
		public EventItem(Context t, long stime, long etime){
			super(t);
			setBackgroundColor(Color.rgb(4, 26, 55));
			setHeight((int) dp2px(t,etime*5));
			setText("s: "+stime+"  e: "+etime);
			setX(100);
			setY(twelve_am.getY()+ dp2px(t,(stime*5)));
		}
	}
	
	// convert month or day
	private String getZero(int x){
		if(String.valueOf(x).length()<2){
			return "0"+String.valueOf(x);
		}
		return String.valueOf(x);
	}
	
	// convert dp to px
	private float dp2px(Context t, float dp){
		float scale = t.getResources().getDisplayMetrics().density;
		return (dp*scale +0.5f);
	}
}
