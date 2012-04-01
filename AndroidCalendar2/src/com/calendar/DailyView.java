package com.calendar;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.commTimeCal.TimeCal;
import com.test2.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DailyView extends Activity {

	// initialize the day to today
	public static int dailyYear = Calendar.getInstance().get(Calendar.YEAR);
	public static int dailyMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
	public static int dailyDayOfMonth = Calendar.getInstance().get(
			Calendar.DATE);
	TextView dailyview_today;
	TextView twelve_am;
	RelativeLayout relativeLayout;
	Button bAddEvent, bPrevious, bNext;

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
		// set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.dailyview);
		relativeLayout = (RelativeLayout) findViewById(R.id.daily_relativelayout);

		// button AddEvent
		bAddEvent = (Button) findViewById(R.id.dailyview_bAddEvent);
		bAddEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// setContentView(R.layout.addevent);
				finish();
				startActivity(new Intent("com.calendar.ADDEVENT"));

			}

		});

		// text date
		dailyview_today = (TextView) findViewById(R.id.dailyview_today);
		dailyview_today.setText("Date: " + dailyDayOfMonth + " / " + dailyMonth
				+ " / " + dailyYear);

		twelve_am = (TextView) findViewById(R.id.twelve_am);
		setupDailyTimes(this);

		testAddLabels(this);

		// button previous
		bPrevious = (Button) findViewById(R.id.dailyview_bPrevious);
		bPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// setContentView(R.layout.addevent);
				Calendar temp = Calendar.getInstance();
				temp.set(dailyYear, dailyMonth, dailyDayOfMonth);
				temp.add(Calendar.DATE, -1);
				dailyYear = temp.get(Calendar.YEAR);
				dailyMonth = temp.get(Calendar.MONTH);
				dailyDayOfMonth = temp.get(Calendar.DATE);
				finish();
				startActivity(new Intent("com.calendar.DAILYVIEW"));

			}

		});

		// button next
		bNext = (Button) findViewById(R.id.dailyview_bNext);
		bNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// setContentView(R.layout.addevent);
				Calendar temp = Calendar.getInstance();
				temp.set(dailyYear, dailyMonth, dailyDayOfMonth);
				temp.add(Calendar.DATE, 1);
				dailyYear = temp.get(Calendar.YEAR);
				dailyMonth = temp.get(Calendar.MONTH);
				dailyDayOfMonth = temp.get(Calendar.DATE);

				finish();
				startActivity(new Intent("com.calendar.DAILYVIEW"));

			}

		});
		// change orientation

		
	}

	public void testAddLabels(Context t) {
		String sdate = dailyYear + "" + getZero(dailyMonth) + ""
				+ getZero(dailyDayOfMonth);
		String output[] = getInterval(sdate);
		if (output.length != 0) {
			EventItem events[] = new EventItem[output.length / 3];
			for (int i = 0; i < output.length; i += 3) {
				long stime = Long.parseLong(output[i + 1]);
				long etime = Long.parseLong(output[i + 2]);
				events[i / 3] = new EventItem(t, stime, etime, output[i]);
				relativeLayout.addView(events[i / 3]);
			}
		}
	}

	// input a day e.g. yyyyMMdd
	// return {eventID, start time, length, .. .. ..}
	public String[] getInterval(String sDate) {
		LinkedList<String> x = new LinkedList<String>();
		JSONArray ja;
		String condition = " startDate <= '" + sDate + "' AND endDate >= '"
				+ sDate + "' ";

		try {
			ja = AndroidCalendar2Activity.getDB().fetchConditional("TimeTable",
					condition);
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				Date initPoint = TheActDate(sDate, "00:00");
				Date startPoint;
				Date endPoint;

				if (jo.getString("startDate").compareTo(sDate) < 0) {
					startPoint = TheActDate(sDate, "00:00");
				} else {
					startPoint = TheActDate(sDate, jo.getString("startTime"));
				}
				if (jo.getString("endDate").compareTo(sDate) > 0) {
					endPoint = TheActDate(sDate, "00:00");
					endPoint = nextDay(endPoint);
				} else {
					endPoint = TheActDate(sDate, jo.getString("endTime"));
				}

				long slen = timeLength(initPoint, startPoint);
				long elen = timeLength(startPoint, endPoint);
				x.add(jo.getString("eventID"));
				x.add(String.valueOf(slen));
				x.add(String.valueOf(elen));
			}

		} catch (Exception e) {
			Log.i("error", e.toString());
		}

		return x.toArray(new String[x.size()]);
	}

	public long timeLength(Date actsdate, Date actedate) throws Exception {
		return (actedate.getTime() - actsdate.getTime()) / 1000 / 60 / 5;
	}

	public Date nextDay(Date a) {
		Date output = new Date();
		output.setTime(a.getTime() + 1000 * 60 * 60 * 24);
		return output;
	}

	public Date TheActDate(String d, String hm) throws Exception {
		Date sd = new Date();
		String[] HourMin = hm.split(":");
		long thetime = new SimpleDateFormat("yyyyMMdd").parse(d).getTime()
				+ Integer.parseInt(HourMin[0]) * 60 * 60 * 1000
				+ Integer.parseInt(HourMin[1]) * 60 * 1000;
		sd.setTime(thetime);
		return sd;
	}

	// setup daily view
	public void setupDailyTimes(Context t) {
		String[] times = { "1:00am", "2:00am", "3:00am", "4:00am", "5:00am",
				"6:00am", "7:00am", "8:00am", "9:00am", "10:00am", "11:00am",
				"12:00pm", "1:00pm", "2:00pm", "3:00pm", "4:00pm", "5:00pm",
				"6:00pm", "7:00pm", "8:00pm", "9:00pm", "10:00pm", "11:00pm" };

		for (int i = 0; i < times.length; i++) {
			TextView tv = new TextView(t);
			tv.setText(times[i]);
			tv.setX(0);
			float h = twelve_am.getY() + dp2px(t, 60 * (i + 1));
			tv.setY(h);
			relativeLayout.addView(tv);
			relativeLayout.setMinimumHeight((int) (h + dp2px(t, 61)));

			View v = new View(t);
			v.setX(100);
			v.setY(h + 15);
			v.setBackgroundColor(Color.GRAY);

			v.setMinimumHeight(2);
			v.setMinimumWidth(200);
			relativeLayout.addView(v);
		}

		// for the line @ 12:00 am
		View v = new View(t);
		v.setX(100);
		v.setY(15);
		v.setBackgroundColor(Color.GRAY);

		v.setMinimumHeight(2);
		v.setMinimumWidth(200);
		relativeLayout.addView(v);
	}

	private class EventItem extends TextView {
		String eventid;
		Context outside;

		public EventItem(Context t, long stime, long etime, String ID) {
			super(t);
			outside = t;
			eventid = ID;
			setBackgroundColor(randomColor());
			setHeight((int) dp2px(t, etime * 5));
			try {
				setText(getJO().getString("title"));
			} catch (Exception e) {
				Log.i("not get JO", e.toString());
			}
			setX(100);
			setY(twelve_am.getY() + dp2px(t, (stime * 5)));
			super.setWidth(300);

			super.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					try {
						JSONObject itemOB = getJO();
						String t = "Title:   " + itemOB.getString("title")
								+ "\n" + "Start: 	 "
								+ itemOB.getString("startTime") + "\n"
								+ "End:   " + itemOB.getString("endTime")
								+ "\n" + "Location:   "
								+ itemOB.getString("location") + "\n";
						Toast.makeText(outside, t, Toast.LENGTH_SHORT).show();
					} catch (Exception e) {

					}
				}

			});
		}

		// get entire record for this event
		public JSONObject getJO() throws Exception {
			JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional(
					"TimeTable", " eventID = '" + eventid + "' ");
			if (ja == null)
				return null;
			if (ja.length() != 1)
				return null;
			return ja.getJSONObject(0);
		}

		// produce random color for the item
		public int randomColor() {
			return Color.rgb(new Random().nextInt(255),
					new Random().nextInt(255), new Random().nextInt(255));
		}
	}

	// convert month or day
	private String getZero(int x) {
		if (String.valueOf(x).length() < 2) {
			return "0" + String.valueOf(x);
		}
		return String.valueOf(x);
	}

	// convert dp to px
	private float dp2px(Context t, float dp) {
		float scale = t.getResources().getDisplayMetrics().density;
		return (dp * scale + 0.5f);
	}
}
