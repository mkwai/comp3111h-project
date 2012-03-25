package com.calendar;

import java.util.Calendar;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.test2.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsView extends Activity{

	public static int dailyYear; 
	public static int dailyMonth;
	public static int dailyDayOfMonth;
	private TextView friendview;
	private TextView noon;
	private RelativeLayout DayLayout;
	private HorizontalScrollView SV;
	private ScrollView innerSV;
	private String myfriendid;
	private int numOfDays = 0;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		
		Bundle extras = getIntent().getExtras();
		myfriendid = extras.getString("targetfriend");
		
		friendview = (TextView) findViewById(R.id.friendview);
		SV = (HorizontalScrollView) findViewById(R.id.hsv1);
		
		//compute before calculate
		
		
		
		//set
		setbackground(this);
		
		setupDailyTimes(this);
		
	}
	
	public void setbackground(Context t){
		DayLayout = new RelativeLayout(t);
		innerSV = new ScrollView(t);
		SV.addView(innerSV);
		innerSV.addView(DayLayout);
		noon = new TextView(t);
		noon.setText("12:00am");
		DayLayout.addView(noon);
	}
	
	public int DateExist(Context t){
		int i = 0;
		JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional("FriendTable",  " friendID = '"+myfriendid+"' ");
		
		return i;
	}
	
	private class EventItem extends TextView{
		String friendid;
		Context outside;
		public EventItem(Context t, long stime, long etime, String ID){
			super(t);
			outside = t;
			friendid = ID;
			setBackgroundColor(randomColor());
			setHeight((int) dp2px(t,etime*5));
			try{
				setText(getJO().getString("title"));
			}catch (Exception e){
				Log.i("not get JO",e.toString());
			}
			setX(100);
			setY(noon.getY()+ dp2px(t,(stime*5)));
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
			JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional("FriendTable", " friendID = '"+friendid+"' ");
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
			float h = noon.getY()+dp2px(t,60*(i+1));
			tv.setY(h);
			DayLayout.addView(tv);
			DayLayout.setMinimumHeight((int) (h+dp2px(t,61)));
		}
		
	}
	
	// convert dp to px
	private float dp2px(Context t, float dp){
		float scale = t.getResources().getDisplayMetrics().density;
		return (dp*scale +0.5f);
	}
}