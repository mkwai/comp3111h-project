package com.calendar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

import com.calendar.Synchronous.FDAdapter.ViewHolder;
import com.facebook.DoTask;
import com.facebook.FbHandler;
import com.test2.R;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);
		
		mShare = (Button) findViewById(R.id.ToShare);
		mStartDate = (Button) findViewById(R.id.share_starting_date_button);
		mEndDate = (Button) findViewById(R.id.share_ending_date_button);

		listLayout = (LinearLayout) findViewById(R.id.shareLayout);
        
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
        		listLayout.addView(TV,i);
        	}
        	
        }catch(Exception e){
        	Log.i("h",e.toString());
        }
        
        mShare.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
		        progress = new ProgressDialog(Share.this);
		    	progress.setMessage("Loading...");
		    	progress.setMax(100);
		    	progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		    	progress.show();
				
		    	// thread for register the system
				Thread t = new Thread(){
					public void run(){
						
						if(checkIsFirst()){
						
						ArrayList<NameValuePair> input = new ArrayList<NameValuePair>();
						input.add(new BasicNameValuePair("newbaby","1"));
						input.add(new BasicNameValuePair("myid",myid));
						response = RequestShare(input);
						if(response.equals("-1")){
							progress.cancel();
							Looper.prepare();
							Toast.makeText(Share.this, "Problem found! plz connect to server agent",
									Toast.LENGTH_SHORT).show();
							Looper.loop();
							return ;
						}

						String ins[] = response.split(" ");
						if(!ins[1].equals(myid)){
							progress.cancel();
							Looper.prepare();
							Toast.makeText(Share.this, "ID Exist! plz connect to server agent",
									Toast.LENGTH_SHORT).show();
							Looper.loop();
							return ;
						}
						
						AndroidCalendar2Activity.getDB().insert("OtherTable",
								new String [] {ins[1],ins[0]});
						
						progress.incrementProgressBy(10);
						}

						if(progress.isShowing()){
							progress.setProgress(progress.getMax());
							progress.cancel();
							
						}
						
					}
				};
				t.start();
				
			}
        	
        });
        
	}
	
	public boolean checkIsFirst() {
		try {
			myid = FbHandler.getMine().getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONArray ja=AndroidCalendar2Activity.getDB().fetchConditional("OtherTable", " key ='"+myid+"'");
		if (ja.length()==0) return true;
		else {
			return false;
		}
	}
	
	public String RequestShare(ArrayList<NameValuePair> nameValuePairs){

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
				
		Log.i("response", result);
				
		return result;
	}
	
	
}