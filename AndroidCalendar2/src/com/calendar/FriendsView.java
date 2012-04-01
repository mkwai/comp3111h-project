package com.calendar;

import java.text.ParseException;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
	private Button mPrev;
	private Button mNext;
	private TextView friendview;
	private TextView noon;
	private RelativeLayout DayLayout;
	private HorizontalScrollView SV;
	private ScrollView innerSV;
	private String myfriendid;
	private String today;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		
		Bundle extras = getIntent().getExtras();
		myfriendid = extras.getString("targetfriend");
		today = extras.getString("today");
		
		friendview = (TextView) findViewById(R.id.friendview);
		SV = (HorizontalScrollView) findViewById(R.id.hsv1);
		mPrev = (Button) findViewById(R.id.prev);
		mNext = (Button) findViewById(R.id.next);
		

		mNext.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String condition =" friendID = '"+myfriendid
						+"' AND ( startDate > "+today+" OR ( startDate < endDate AND "+today+" < endDate) ) ";
				JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional("FriendTimeTable", condition, "startDate");
				if(ja.length()==0){
					Toast.makeText(FriendsView.this, "no more record", Toast.LENGTH_SHORT).show();
				}else{
					try{
						JSONObject jo = ja.getJSONObject(0);
						if(jo.getString("endDate").compareTo(jo.getString("startDate"))>0){
							Date t = nextDay(TheDate(today));
							today = extractDay(t,Calendar.YEAR)+getZero(extractDay(t,Calendar.MONTH)+1)+
									getZero(extractDay(t,Calendar.DAY_OF_MONTH));
						}
						else{
							today = jo.getString("startDate");
						}
						
						System.out.println(today);
							
						Intent t = getIntent();
						t.putExtra("today", today);
						finish();
						startActivity(t);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		});
		
		mPrev.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String condition =" friendID = '"+myfriendid+"' AND startDate < "+today+" ";
				JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional("FriendTimeTable", condition, "startDate");
				if(ja.length()==0){
					Toast.makeText(FriendsView.this, "no more record", Toast.LENGTH_SHORT).show();
				}else{
					try{
						JSONObject jo = ja.getJSONObject(ja.length()-1);
						if(jo.getString("endDate").compareTo(jo.getString("startDate"))>0){
							Date t = prevDay(TheDate(today));
							today = extractDay(t,Calendar.YEAR)+getZero(extractDay(t,Calendar.MONTH)+1)+
									getZero(extractDay(t,Calendar.DAY_OF_MONTH));
						}
						else{
							today = jo.getString("startDate");
						}
						
						
						Intent t = getIntent();
						t.putExtra("today", today);
						finish();
						startActivity(t);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		});
		
		//compute before calculate
		if(today!=null){
			try {
				Date d = new Date();
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				d = df.parse(today);
				friendview.setText(getZero(d.getDate())+"/"+getZero(d.getMonth()+1)+"/"+(d.getYear()+1900));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			getToday();
		}

		
		//set
		setbackground(this);
		
		setupDailyTimes(this);
		
		testAddLabels(this);
		
	}
	
	public int extractDay(Date d, int field){
		Calendar temp = Calendar.getInstance();
		temp.setTime(d);
		return temp.get(field);
	}
	
	
	public void testAddLabels(Context t){
		JSONArray x = new JSONArray();
		
		String condition=" friendID = '"+myfriendid+"' AND startDate <= '"+today+"' AND endDate >= '"+today+"' ";
		
		
		try{
			JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional("FriendTimeTable", condition);
			for(int i = 0;i<ja.length();i++){
				JSONObject jo = ja.getJSONObject(i);
				Date initPoint = TheActDate(today,"00:00");
				Date startPoint;
				Date endPoint;
					
				if(jo.getString("startDate").compareTo(today)<0){
					startPoint = TheActDate(today,"00:00");
				}else{
					startPoint = TheActDate(today, jo.getString("startTime"));
				}
				if(jo.getString("endDate").compareTo(today)>0){
					endPoint = TheActDate(today,"00:00");
					endPoint = nextDay(endPoint);
				}else{
					endPoint = TheActDate(today, jo.getString("endTime"));
				}
				
				long slen = timeLength(initPoint,startPoint);
				long elen = timeLength(startPoint,endPoint);
				
				jo.put("len", String.valueOf(elen));
				jo.put("stime", String.valueOf(slen));
				x.put(jo);
			}
			
			}catch(Exception e){
				Log.i("error",e.toString()); 
			}
		
		
		
		if(x.length()!=0){
			EventItem events[] = new EventItem[x.length()];
			try{
			for(int i = 0;i<x.length();i++){
				events[i] = new EventItem(t,x.getJSONObject(i));
				DayLayout.addView(events[i]);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	public void getToday(){
		String condition = "friendID = '"+myfriendid+"' ";
		JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional("FriendTimeTable", condition);
		if(ja.length()==0){
			final Dialog s = new AlertDialog.Builder(FriendsView.this)
			.setTitle("No Single Record")
			.setPositiveButton("OK", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.cancel();
					finish();
				}
			}).create();
			s.show();
		}else{
			try{
				today = ja.getJSONObject(0).getString("startDate");
				Date d = new Date();
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				d = df.parse(today);
				friendview.setText(getZero(d.getDate())+"/"+getZero(d.getMonth()+1)+"/"+(d.getYear()+1900));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
		
	public long timeLength(Date actsdate, Date actedate) throws Exception{
		return (actedate.getTime()-actsdate.getTime())/1000/60/5;
	}
		
	public Date nextDay(Date a){
		Date output = new Date();
		output.setTime(a.getTime()+1000*60*60*24);
		return output;
	}
		
	public Date prevDay(Date a){
		Date output = new Date();
		output.setTime(a.getTime()-1000*60*60*24);
		return output;
	}
		
	public Date TheActDate(String d, String hm) throws Exception{
		Date sd = new Date();
		String []HourMin = hm.split(":");
		long thetime = new SimpleDateFormat("yyyyMMdd").parse(d).getTime()+Integer.parseInt(HourMin[0])*60*60*1000+Integer.parseInt(HourMin[1])*60*1000;
		sd.setTime(thetime);
		return sd;
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
	
	private class EventItem extends TextView{
		Context outside;
		JSONObject record;
		public EventItem(Context t, JSONObject jo){
			super(t);
			outside = t;
			record = jo;
			setBackgroundColor(randomColor());
			try{
				setHeight((int) dp2px(t,jo.getInt("len")*5));
				setText(jo.getString("title"));
				setX(100);
				setY(noon.getY()+ dp2px(t,(jo.getInt("stime")*5)));
			}catch (Exception e){
				Log.i("not get JO",e.toString());
			}
			super.setWidth(300);
			
			super.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					try{
					String t = "Title:   "+record.getString("title")+"\n" + 
							"Start: 	 "+record.getString("startTime")+"\n" + 
							"End:   "+record.getString("endTime")+"\n" + 
							"Location:   "+record.getString("location")+"\n";
					Toast.makeText(outside, t, Toast.LENGTH_SHORT).show();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
			});
		}
		//produce random color for the item
		public int randomColor(){
			return Color.rgb(new Random().nextInt(255),
							new Random().nextInt(255),
							new Random().nextInt(255));
		}
	}
	// convert to date format
		public Date TheDate(String d) throws ParseException{
			return new SimpleDateFormat("yyyyMMdd").parse(d);
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