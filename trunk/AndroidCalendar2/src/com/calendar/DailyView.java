package com.calendar;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.commTimeCal.TimeCal;
import com.test2.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DailyView extends Activity {

	//initialize the day to today
	public static int dailyYear =Calendar.getInstance().get(Calendar.YEAR); 
	public static int dailyMonth=Calendar.getInstance().get(Calendar.MONTH) + 1;
	public static int dailyDayOfMonth=Calendar.getInstance().get(Calendar.DATE);
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
		dailyview_today.setText("Date: " + dailyDayOfMonth + " / "
				+ dailyMonth + " / " + dailyYear);

		twelve_am = (TextView) findViewById(R.id.twelve_am);
		setupDailyTimes(this);
		
		testAddLabels(this);
		
		

	}
	
	
	public void testAddLabels(Context t){
		String sdate = dailyYear + "" + getZero(dailyMonth) + "" +getZero(dailyDayOfMonth);
		String output[] = TimeCal.getInterval(sdate);
		if(output.length!=0){
			EventItem events[] = new EventItem[output.length/3];
			for(int i = 0;i<output.length;i+=3){
				long stime = Long.parseLong(output[i+1]);
				long etime = Long.parseLong(output[i+2]);
				events[i/3] = new EventItem(t,stime,etime,output[i]);
				relativeLayout.addView(events[i/3]);
			}
		}
	}
	
	// setup daily view
	public void setupDailyTimes(Context t){
		String [] times = {"1:00am","2:00am","3:00am",
				"4:00am","5:00am","6:00am","7:00am",
				"8:00am","9:00am","10:00am","11:00am",
				"12:00pm","1:00pm","2:00pm","3:00pm",
				"4:00pm","5:00pm","6:00pm","7:00pm",
				"8:00pm","9:00pm","10:00pm","11:00pm"
		};	
		
		for(int i = 0;i<times.length;i++){
			TextView tv = new TextView(t);
			tv.setText(times[i]);
			tv.setX(0);
			float h = twelve_am.getY()+dp2px(t,60*(i+1));
			tv.setY(h);
			relativeLayout.addView(tv);
			relativeLayout.setMinimumHeight((int) (h));
		}
		
	}

	private class EventItem extends TextView{
		String eventid;
		Context outside;
		public EventItem(Context t, long stime, long etime, String ID){
			super(t);
			outside = t;
			eventid = ID;
			setBackgroundColor(randomColor());
			setHeight((int) dp2px(t,etime*5));
			try{
				setText(getJO().getString("title"));
			}catch (Exception e){
				Log.i("not get JO",e.toString());
			}
			setX(100);
			setY(twelve_am.getY()+ dp2px(t,(stime*5)));
			super.setWidth(300);
			
			super.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					try{
					JSONObject itemOB = getJO();
					String t = "Title:   "+itemOB.getString("title")+"\n" + 
							"Start: 	 "+itemOB.getString("startTime")+"\n" + 
							"End:   "+itemOB.getString("endTime")+"\n" + 
							"Location:   "+itemOB.getString("location")+"\n";
					Toast.makeText(outside, t, Toast.LENGTH_SHORT).show();
					}catch(Exception e){
						
					}
				}
				
			});
		}
		
		//get entire record for this event
		public JSONObject getJO() throws Exception{
			JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional("TimeTable", " eventID = '"+eventid+"' ");
			if(ja==null) return null;
			if(ja.length()!=1) return null;
			return ja.getJSONObject(0);
		}
		
		//produce random color for the item
		public int randomColor(){
			return Color.rgb(new Random().nextInt(255),
							new Random().nextInt(255),
							new Random().nextInt(255));
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
