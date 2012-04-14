package com.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.calendar.Synchronous.FDAdapter.ViewHolder;
import com.commTimeCal.TimeBoard;
import com.commTimeCal.TimeCal;
import com.test2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class CommTime extends Activity{

	static protected Calendar currentDateCalendar = Calendar.getInstance();
	private Calendar startingCalendar = Calendar.getInstance();
	private Calendar endingCalendar = Calendar.getInstance();
	
	private JSONArray friends = new JSONArray();
	private LinearLayout timeslayout;
	private LinearLayout nameslayout;
	private NameSlot[] fnames;
	private Button mStartPeriod;
	private Button mEndPeriod;
	private Button mCal;
	private Button mFil;
	
	private JSONArray friendTimeRecord = new JSONArray(); 
	private JSONArray myTimeRecord = new JSONArray();
	private FilOptions TimeFilter;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commtime);

		TimeFilter = new FilOptions(this);
		timeslayout = (LinearLayout) findViewById(R.id.comm_timelist);
		nameslayout = (LinearLayout) findViewById(R.id.comm_namelist);
		
		Bundle extras = getIntent().getExtras();
		try {
			friends =  new JSONArray(extras.getString("findfriends"));
			fnames = new NameSlot[friends.length()];
			setFriendList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mStartPeriod = (Button) findViewById(R.id.comm_starting_date_button);
		mEndPeriod = (Button) findViewById(R.id.comm_ending_date_button);
		mCal = (Button) findViewById(R.id.comm_caltime);
		mFil = (Button) findViewById(R.id.comm_filter);
		
		mStartPeriod.setText(DateFormat.format("dd", currentDateCalendar)+
				"/"+DateFormat.format("MM", currentDateCalendar)+
				"/"+DateFormat.format("yyyy", currentDateCalendar));
		mEndPeriod.setText(DateFormat.format("dd", currentDateCalendar)+
				"/"+DateFormat.format("MM", currentDateCalendar)+
				"/"+DateFormat.format("yyyy", currentDateCalendar));
		
		
        mStartPeriod.setOnClickListener(new OnClickListener(){
        	
			public void onClick(View arg0) {
				showDialog(1);
			}
        });
        
        mEndPeriod.setOnClickListener(new OnClickListener(){
        	
			public void onClick(View arg0) {
				showDialog(2);
			}
        });
        
		mCal.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				try {
					boolean done = getAllTimeRecord(mStartPeriod.getText().toString(),mEndPeriod.getText().toString());
					timeslayout.removeAllViews();
					if(done)
						DoListing(combineJA(friendTimeRecord,myTimeRecord));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	
		mFil.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				TimeFilter.removeAllViews();
				TimeFilter = new FilOptions(CommTime.this);
				final AlertDialog.Builder builder = new AlertDialog.Builder(CommTime.this);
				builder.setView(TimeFilter);
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface arg0, int arg1) {
		            	
		        }});
				builder.show();
			}
		});
	}
	
	public void setFriendList() throws Exception{
		for(int i = 0;i<friends.length();i++){
			fnames[i]= new NameSlot(this,friends.getJSONObject(i).getString("id")
					,friends.getJSONObject(i).getString("name"));
		}
	}
	
	public JSONArray combineJA(JSONArray recJA1, JSONArray recJA2) throws Exception{
		JSONArray ja = new JSONArray();
		for(int i = 0;i<recJA1.length();i++){
			ja.put(recJA1.getJSONObject(i));
		}
		for(int i = 0;i<recJA2.length();i++){
			ja.put(recJA2.getJSONObject(i));
		}
		return ja;
	}
	
	// convert to date format
	public Date TheDate(String d) throws ParseException{
		return new SimpleDateFormat("yyyyMMdd").parse(d);
	}
	
	public int extractDay(Date d, int field){
		Calendar temp = Calendar.getInstance();
		temp.setTime(d);
		return temp.get(field);
	}
	
	public Date TheActDate(String d, String hm) throws Exception{
		Date sd = new Date();
		String []HourMin = hm.split(":");
		long thetime = TheDate(d).getTime()+Integer.parseInt(HourMin[0])*60*60*1000+Integer.parseInt(HourMin[1])*60*1000;
		sd.setTime(thetime);
		return sd;
	}
	
	public int HowManyDays(Date sd, Date ed) throws Exception{
		int num = (int) ((ed.getTime()-sd.getTime())/1000/60/60/24);
		return num;
	}
	
	public Date nextDay(Date a){
		Date output = new Date();
		output.setTime(a.getTime()+1000*60*60*24);
		return output;
	}
	
	public long timeLength(Date actsdate, Date actedate) throws Exception{
		return (actedate.getTime()-actsdate.getTime())/1000/60/5;
	}
	
	// ja has startDate, startTime, endDate, endTime
	public String DoListing(JSONArray ja) throws Exception{
		if(ja.length()==0){
			Toast.makeText(this, "all days free !", Toast.LENGTH_SHORT).show();
			return null;
		}
		String buttons = dateFromButton(mStartPeriod.getText().toString(),"dd/MM/yyyy");
		String buttone = dateFromButton(mEndPeriod.getText().toString(),"dd/MM/yyyy");
		Date sd = TheDate(buttons);
		Date ed = TheDate(buttone);
		
		int numDays = HowManyDays(TheDate(buttons), TheDate(buttone));
		timeBoard TheDays = new timeBoard(TheDate(buttons),numDays+1);
		for(int i = 0;i<ja.length();i++){
			String jasdate = ja.getJSONObject(i).getString("startDate");
			String jastime = ja.getJSONObject(i).getString("startTime");
			String jaedate = ja.getJSONObject(i).getString("endDate");
			String jaetime = ja.getJSONObject(i).getString("endTime");
			
			Date s = TheActDate(jasdate,jastime);
			Date e = TheActDate(jaedate,jaetime);

			TheDays.addTime(s, timeLength(s,e));
		}
		
		// Apply Filter
		sd = TheDate(buttons);
		ed = TheDate(buttone);
		while(sd.compareTo(ed)<=0){
			TimeFilter.AddFilterOnTimeBoard(sd, TheDays);
			sd = nextDay(sd);
		}
		
		
		sd = TheDate(buttons);
		ed = TheDate(buttone);
		while(sd.compareTo(ed)<=0){
			String thisd = extractDay(sd,Calendar.DATE)+"/"+(extractDay(sd,Calendar.MONTH)+1)
					+"/"+extractDay(sd, Calendar.YEAR)+" ("+Week2Str(sd)+") ";
			
			String thisd2 = extractDay(sd, Calendar.YEAR)+getZero(extractDay(sd,Calendar.MONTH)+1)
					+getZero(extractDay(sd,Calendar.DATE));
			if(TimeFilter.shouldBlockDate(thisd2)){
				sd = nextDay(sd);
				continue;
			}

			String [] result = TheDays.getFreeTime(sd);
			if(result.length==0){
				sd = nextDay(sd);
				continue;
			}
			
			new FreeSlot(this, thisd).randomColor();
			
			for(int i = 0;i<result.length;i+=2){
				new FreeSlot(this, result[i]+" to "+result[i+1]);
			}
			
			sd = nextDay(sd);
		}
		
		return null;
	}
	
	// ja has startDate, startTime, endDate, endTime
	public String DoListing2(JSONArray ja) throws Exception{
		if(ja.length()==0){
			Toast.makeText(this, "all days free !", Toast.LENGTH_SHORT).show();
			return null;
		}
		String buttons = dateFromButton(mStartPeriod.getText().toString(),"dd/MM/yyyy");
		String buttone = dateFromButton(mEndPeriod.getText().toString(),"dd/MM/yyyy");
		
		String minsd = ja.getJSONObject(0).getString("startDate");
		for(int i = 0;i<ja.length();i++){
			String temp = ja.getJSONObject(i).getString("startDate");
			if(temp.compareTo(minsd) < 0)
				minsd = temp;
		}
		String maxed = ja.getJSONObject(0).getString("endDate");
		for(int i = 0;i<ja.length();i++){
			String temp = ja.getJSONObject(i).getString("endDate");
			if(temp.compareTo(maxed) > 0)
				maxed = temp;
		}
		
		int numDays = HowManyDays(TheDate(minsd), TheDate(maxed));
		timeBoard TheDays = new timeBoard(TheDate(minsd),numDays+1);
		for(int i = 0;i<ja.length();i++){
			String jasdate = ja.getJSONObject(i).getString("startDate");
			String jastime = ja.getJSONObject(i).getString("startTime");
			String jaedate = ja.getJSONObject(i).getString("endDate");
			String jaetime = ja.getJSONObject(i).getString("endTime");
			
			Date s = TheActDate(jasdate,jastime);
			Date e = TheActDate(jaedate,jaetime);

			TheDays.addTime(s, timeLength(s,e));
		}
		
		
		if(minsd.compareTo(buttons)<0)
			minsd = buttons;
		if(maxed.compareTo(buttone)>0)
			maxed = buttone;
		
		Date sd = TheDate(buttons);
		Date ed = TheDate(buttone);
		while(sd.compareTo(ed)<=0){
			String thisd = extractDay(sd,Calendar.DATE)+"/"+(extractDay(sd,Calendar.MONTH)+1)
					+"/"+extractDay(sd, Calendar.YEAR);
			String thisd2 = extractDay(sd, Calendar.YEAR)+getZero(extractDay(sd,Calendar.MONTH)+1)
					+getZero(extractDay(sd,Calendar.DATE));
			if(TimeFilter.shouldBlockDate(thisd2)){
				sd = nextDay(sd);
				continue;
			}
			new FreeSlot(this, thisd).randomColor();
			
			
			if(sd.compareTo(TheDays.initDay)<0){
				sd = nextDay(sd);
				new FreeSlot(this,"00:00 to 00:00");
				continue;
			}
			if(sd.compareTo(TheDate(maxed))>0){
				sd = nextDay(sd);
				new FreeSlot(this,"00:00 to 00:00");
				continue;
			}
			
			String [] result = TheDays.getFreeTime(sd);
			for(int i = 0;i<result.length;i+=2){
				new FreeSlot(this, result[i]+" to "+result[i+1]);
			}
			
			sd = nextDay(sd);
		}
		
		
		
		return null;
	}
	
	public String Week2Str(Date d){
		return DateFormat.format("EEE",d)+"";
		
	}
	
	public class timeBoard{
		Date initDay;
		oneDay days[];
		public class oneDay{
			int timeslots[][] = new int[24][12]; 
			public oneDay(){
				
			}			
			// {time, time, ...}
			public String [] outputTimelist(){
				LinkedList<String> output = new LinkedList<String>();
				
				boolean isstart=true;
				for(int i = 0;i<24 ;i++ ){
					for(int j = 0;j<12;j++){
						if(isstart && timeslots[i][j]==0){
							output.add(String.format("%02d", i)+":"+String.format("%02d", j*5));
							isstart=false;
						}
						if(!isstart && timeslots[i][j]!=0){
							output.add(String.format("%02d", i)+":"+String.format("%02d", j*5));
							isstart=true;
						}
					}
				}
				if(!isstart){
					output.add("24:00");
				}
				
				return output.toArray(new String[output.size()]);
			}
		}
		public timeBoard(Date sdate, int numOfDay){
			initDay = sdate;
			days = new oneDay[numOfDay];
			for(int i = 0;i<numOfDay;i++){
				days[i]=new oneDay();
			}
		}
		
		public void addTime(Date actdate, long length){
			try {
				int pos = HowManyDays(initDay, actdate);
				if(pos<0){
					length = length-timeLength(actdate, initDay);
					actdate = initDay;
					pos=0;
				}
				int hr = extractDay(actdate,Calendar.HOUR_OF_DAY);
				int min = extractDay(actdate,Calendar.MINUTE)/5;
				int tl = (int) length;
				while(tl>0){
					days[pos].timeslots[hr][min]++;
					
					min++;
					if(min==12) {
						min=0;hr++;
					}
					if(hr==24) {
						pos++;hr=0;
					}
					if(pos==days.length){
						break;
					}
					tl--;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		public String[] getFreeTime(Date nextday){
			
			try {
				int pos = HowManyDays(initDay, nextday);
				if(pos<days.length)
					return days[pos].outputTimelist();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
	public boolean getAllTimeRecord(String sd, String ed) throws Exception{
		JSONArray fns = getCheckedFriend();
		if(fns.length()==0){
			Toast.makeText(this, "whose common time?", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		String sdate = dateFromButton(sd,"dd/MM/yyyy");
		String edate = dateFromButton(ed,"dd/MM/yyyy");
		String condition=" ( ( startDate between '"+sdate+"' and '"+edate+"' ) " 
						+" OR ( endDate between '"+sdate+"' and '"+edate+"' ) "
						+" OR ( startDate <= '"+sdate+"' AND endDate >= '"+edate+"' ) ) "
						+" AND ";
		
		String allnamesCondi = " ( ";
		for(int i = 0;i<fns.length();i++){
			allnamesCondi +=" friendID ='"+fns.getJSONObject(i).getString("id")+"'";
			if(i!=(fns.length()-1))
				allnamesCondi+=" OR ";
		}
		allnamesCondi+=" ) ";
		condition+=allnamesCondi;
		friendTimeRecord = AndroidCalendar2Activity.getDB().fetchConditional("FriendTimeTable",condition);
		
		condition=" ( startDate between '"+sdate+"' and '"+edate+"' ) " 
				+" OR ( endDate between '"+sdate+"' and '"+edate+"' ) "
				+" OR ( startDate <= '"+sdate+"' AND endDate >= '"+edate+"' ) ";
		myTimeRecord=AndroidCalendar2Activity.getDB().fetchConditional("TimeTable", condition);
		
		return true;
		
	}
	
	// list of id
	public JSONArray getCheckedFriend() throws Exception{
		JSONArray ja = new JSONArray();
		for(int i = 0;i<fnames.length;i++){
			if(fnames[i].checked){
				JSONObject jo = new JSONObject();
				jo.put("id", fnames[i].id);
				ja.put(jo);
			}
		}
		return ja;
	}
	
	// return array of distinct key element in ja
	public static String[] getNumKey(JSONArray ja, String key) throws Exception{
		ArrayList<String> x = new ArrayList<String>();
		for(int i = 0;i<ja.length();i++){
			String g = ja.getJSONObject(i).getString(key);
			if(!x.contains(g))
				x.add(g);
		}
		return x.toArray(new String[x.size()]);
	}
	
	private String dateFromButton(String inputdate, String formatOfButton){
		String returnDate="";
		SimpleDateFormat df = new SimpleDateFormat(formatOfButton);
		try{
			Date sdate = df.parse(inputdate);
			returnDate=DateFormat.format("yyyyMMdd",sdate)+"";
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnDate;
	}
	
	private class FilOptions extends LinearLayout{
		public final int size = 6;
		Context t;
		CheckBox[] Cs = new CheckBox[size];
		String[] opsName={"MidNight 00:00 to 06:00","Morning 06:00 to 12:00",
				"AfterNoon 12:00 to 18:00","Night 18:00 to 24:00","Saturday","Sunday"};
		public FilOptions(Context context) {
			super(context);
			t=context;
			super.setLayoutParams( new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
	                LinearLayout.LayoutParams.FILL_PARENT));
			super.setOrientation(1);
			for(int i = 0;i<size;i++){
				Cs[i] = new CheckBox(t);
				Cs[i].setChecked(false);
				Cs[i].setText(opsName[i]);
				super.addView(Cs[i]);
			}
		}
		
		public void AddFilterOnTimeBoard(Date thedate, timeBoard tb){
			String tempd = extractDay(thedate, Calendar.YEAR)+getZero(extractDay(thedate,Calendar.MONTH)+1)
					+getZero(extractDay(thedate,Calendar.DATE));
			try {
				if(Cs[0].isChecked()){
					Date sd = TheActDate(tempd,"00:00");
					Date ed = TheActDate(tempd, "06:00");
					tb.addTime(sd, timeLength(sd,ed));
				}
				if(Cs[1].isChecked()){
					Date sd = TheActDate(tempd,"06:00");
					Date ed = TheActDate(tempd, "12:00");
					tb.addTime(sd, timeLength(sd,ed));
				}
				if(Cs[2].isChecked()){
					Date sd = TheActDate(tempd,"12:00");
					Date ed = TheActDate(tempd, "18:00");
					tb.addTime(sd, timeLength(sd,ed));
				}
				if(Cs[3].isChecked()){
					Date sd = TheActDate(tempd,"18:00");
					Date ed = TheActDate(tempd, "24:00");
					tb.addTime(sd, timeLength(sd,ed));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// input yyyyMMhh
 		public boolean shouldBlockDate(String d){
			Date day;
			try {
				day = TheDate(d);
				if(day.getDay()==6 && Cs[4].isChecked())
					return true;
				if(day.getDay()==0 && Cs[5].isChecked())
					return true;
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return false;
		}	
	}
	
	private class FreeSlot extends TextView{
		FreeSlot(Context t, String content){
			super(t);
			super.setText(content);
			timeslayout.addView(this);
		}
		
		public FreeSlot randomColor(){
			super.setBackgroundColor(Color.rgb(new Random().nextInt(255),
							new Random().nextInt(255),
							new Random().nextInt(255))
							);
			return this;
		}
	}
	
	private class NameSlot extends TextView{
		String id;
		String name;
		boolean checked = true;
		NameSlot(Context t, String _id, String _name){
			super(t);
			id=_id;
			name=_name;
			super.setText(name);
			super.setTextColor(Color.rgb(0, 255, 0));
			super.setOnClickListener(new OnClickListener(){
				public void onClick(View arg0) {
					checked=!checked;
					if(checked==true)
						NameSlot.this.setTextColor(Color.rgb(0, 255, 0));
					else
						NameSlot.this.setTextColor(Color.rgb(255, 0, 0));
				}				
			});
			nameslayout.addView(this);
			
		}
	}
	
	// convert month or day
	private String getZero(int x) {
		if (String.valueOf(x).length() < 2) {
			return "0" + String.valueOf(x);
		}
		return String.valueOf(x);
	}
		
	protected Dialog onCreateDialog(int id) {
		
		OnDateSetListener startingDateSetListener = new OnDateSetListener() {

			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// store the date set by the dialog to startingCalendar variable
				startingCalendar.set(year, monthOfYear, dayOfMonth);
				// renew endingCalendar (ending > starting)
				if (startingCalendar.after(endingCalendar)) {
					endingCalendar.set(year, monthOfYear, dayOfMonth);
					mEndPeriod.setText(DateFormat.format("dd",startingCalendar)
							+ "/" + DateFormat.format("MM", startingCalendar)
							+ "/" + DateFormat.format("yyyy", startingCalendar));
				}
				mStartPeriod.setText(DateFormat.format("dd",startingCalendar)
						+ "/" + DateFormat.format("MM", startingCalendar)
						+ "/" + DateFormat.format("yyyy", startingCalendar));
				
			}
			
		};
		
		OnDateSetListener endingDateSetListener = new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// compareCalendar is temp, if valid input then compare ->
				// ending
				Calendar compareCalendar = Calendar.getInstance();
				compareCalendar.set(year, monthOfYear, dayOfMonth);
				
				// store the date set by the dialog to endingCalendar variable
				// renew endingCalendar (ending > starting)
				if (compareCalendar.after(startingCalendar)) {

					endingCalendar.set(year, monthOfYear, dayOfMonth);
					mEndPeriod.setText(DateFormat.format("dd", endingCalendar)
							+ "/" + DateFormat.format("MM", endingCalendar)
							+ "/" + DateFormat.format("yyyy", endingCalendar));
				}
			}
			
		};
		
		switch(id){
			case 1:
				return new DatePickerDialog(this, startingDateSetListener,
						startingCalendar.get(Calendar.YEAR),
						startingCalendar.get(Calendar.MONTH),
						startingCalendar.get(Calendar.DAY_OF_MONTH));
			case 2:
				return new DatePickerDialog(this, endingDateSetListener,
						endingCalendar.get(Calendar.YEAR),
						endingCalendar.get(Calendar.MONTH),
						endingCalendar.get(Calendar.DAY_OF_MONTH));
		}
		
		return null;
	}
	
}