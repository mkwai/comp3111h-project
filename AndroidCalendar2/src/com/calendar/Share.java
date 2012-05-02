package com.calendar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FbHandler;
import com.test2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class Share extends Activity{
	private Button mShare;
	private Button mStartDate;
	private Button mEndDate;
	private LinearLayout listLayout;
	private JSONArray shareFriends;
	private String myid;
	private String response;
	private ProgressDialog progress;
	static protected Calendar currentDateCalendar = Calendar.getInstance();
	private Calendar startingCalendar = Calendar.getInstance();
	private Calendar endingCalendar = Calendar.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);
		
		mShare = (Button) findViewById(R.id.ToShare);
		mStartDate = (Button) findViewById(R.id.share_starting_date_button);
		mEndDate = (Button) findViewById(R.id.share_ending_date_button);

		listLayout = (LinearLayout) findViewById(R.id.shareLayout);
		
		mStartDate.setText(DateFormat.format("dd", currentDateCalendar)+
				"/"+DateFormat.format("MM", currentDateCalendar)+
				"/"+DateFormat.format("yyyy", currentDateCalendar));
		mEndDate.setText(DateFormat.format("dd", currentDateCalendar)+
				"/"+DateFormat.format("MM", currentDateCalendar)+
				"/"+DateFormat.format("yyyy", currentDateCalendar));
		
        
        Bundle extras = getIntent().getExtras();
        try {
			shareFriends = new JSONArray(extras.getString("sharefriends"));
		} catch (JSONException e) {
			Log.i("json",e.toString());
		}
        try{
        	for(int i = 0;i<shareFriends.length();i++){
        		TextView TV = new TextView(this);
        		TV.setText(shareFriends.getJSONObject(i).getString("name"));
        		TV.setTextColor(Color.BLACK);
        		listLayout.addView(TV,i);
        	}
        	
        }catch(Exception e){
        	Log.i("h",e.toString());
        }
        
        
        mStartDate.setOnClickListener(new OnClickListener(){
        	
			public void onClick(View arg0) {
				showDialog(1);
			}
        });
        
        mEndDate.setOnClickListener(new OnClickListener(){
        	
			public void onClick(View arg0) {
				showDialog(2);
			}
        });
        
        mShare.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
		        progress = new ProgressDialog(Share.this);
		    	progress.setMessage("Loading...");
		    	progress.setMax(100);
		    	progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		    	progress.show();
				
		    	// thread to share
		    	new Thread(new Runnable(){
					public void run() {
						try {
							myid = FbHandler.getMine().getString("id");
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						progress.incrementProgressBy(10);
						
						String startDate="";
						String endDate="";
						SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						try{
							Date sdate = df.parse(mStartDate.getText().toString());
							startDate=DateFormat.format("yyyyMMdd",sdate)+"";
							Date edate = df.parse(mEndDate.getText().toString());
							endDate=DateFormat.format("yyyyMMdd",edate)+"";
						}catch(Exception e){
							Log.i("error",e.toString());
						}
						

						JSONArray shareevents = new JSONArray(); //array of event record
						String condition=" startDate >= '"+startDate+"' AND startDate <= '"+endDate+"' AND private = '0' ";
						try{
							JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional("TimeTable", condition);
							System.out.println(ja.length());
							if(ja.length()==0){
								progress.cancel();
								Looper.prepare();
								Toast.makeText(Share.this, "no events to share in this period",
										Toast.LENGTH_SHORT).show();
								Looper.loop();
								return;
							}
							
							for(int i = 0;i<ja.length();i++){
								JSONObject jo = ja.getJSONObject(i);
								if(jo.getString("private")!="1"){
									jo.remove("private");jo.remove("reminder");jo.remove("eventID");
									jo.remove("milliS"); jo.remove("contact");
									shareevents.put(jo);
								}
							}
						}catch(Exception e){
							Log.i("error",e.toString()); 
						}
						progress.incrementProgressBy(20);
						
						String idlist = ""; //string of idlist
						try{
							for(int i = 0;i<shareFriends.length();i++){
								idlist += shareFriends.getJSONObject(i).getString("id");
								if(i!=shareFriends.length()-1){
									idlist+="*";
								}
							}	
						} catch (JSONException e) {
							e.printStackTrace();
						}
						progress.incrementProgressBy(10);

						ArrayList<NameValuePair> input = new ArrayList<NameValuePair>();
						input.add(new BasicNameValuePair("check","1"));
						input.add(new BasicNameValuePair("myid",myid));
						input.add(new BasicNameValuePair("token", FbHandler.mFacebook.getAccessToken()));
						input.add(new BasicNameValuePair("readyshare","1"));
						input.add(new BasicNameValuePair("idlist",idlist));
						input.add(new BasicNameValuePair("eventlist",shareevents.toString()));
						response = RequestShare(input);
						if(response=="-1"){
							progress.cancel();
							Looper.prepare();
							Toast.makeText(Share.this, "Problem found! plz connect to server agent",
							Toast.LENGTH_SHORT).show();
							Looper.loop();
							return;	
						}
						
						if(progress.isShowing()){
							progress.setProgress(progress.getMax());
							progress.cancel();
						}
						
						Looper.prepare();
						final Dialog s = new AlertDialog.Builder(Share.this)
						.setTitle("Thanks for Sharing!")
						.setPositiveButton("OK", new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.cancel();
								finish();
							}
							
						}).create();
						s.show();
						Looper.loop();
					}
		    		
		    	}).start();
		    	
			}
        	
        });
        
	}
	
	
	public static String RequestShare(ArrayList<NameValuePair> nameValuePairs){

		InputStream is = null;
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("https://falling-wind-9540.herokuapp.com/port.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		}catch(Exception e){
			Log.e("log_tag", "Error in http connection "+e.toString());
		}
				
		String result = "";
		try{
		       BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		       StringBuilder sb = new StringBuilder();
		       String line = null;
		       while ((line = reader.readLine()) != null) {
		               sb.append(line);
	        }
		       is.close();
			 
		       result=sb.toString();
		}catch(Exception e){
	        Log.e("log_tag", "Error converting result "+e.toString());
		}
				
		Log.i("response", result+" ");
				
		return result;
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
					mEndDate.setText(DateFormat.format("dd",startingCalendar)
							+ "/" + DateFormat.format("MM", startingCalendar)
							+ "/" + DateFormat.format("yyyy", startingCalendar));
				}
				mStartDate.setText(DateFormat.format("dd",startingCalendar)
						+ "/" + DateFormat.format("MM", startingCalendar)
						+ "/" + DateFormat.format("yyyy", startingCalendar));
				Log.i("done",mStartDate.getText().toString());
				
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
					mEndDate.setText(DateFormat.format("dd", endingCalendar)
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