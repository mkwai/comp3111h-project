package com.calendar;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.LoginButton;
import com.facebook.DoTask;
import com.facebook.FbHandler;
import com.test2.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Synchronous extends Activity implements OnItemClickListener{
	private Button mDownButton;

    private LoginButton mLoginButton;
    private Button mRefreshButton;
    private Button mShareButton;
    private FbHandler FB;
	private String myid;
	private ListView listLayout;
	private JSONArray myFriends = new JSONArray();
	private boolean checked[];
	private ProgressDialog progress;
	private static int downloadcount=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.synchronous);
		
		
        listLayout = (ListView) findViewById(R.id.fdlist);
        
        mLoginButton = (LoginButton) findViewById(R.id.login);
        FB = new FbHandler(this);
        mLoginButton.init(this, FB.mFacebook);
        
        mRefreshButton = (Button) findViewById(R.id.refresh);
        
        mRefreshButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!FB.IsLogin()){
					Toast.makeText(Synchronous.this, "Not Yet Login!", Toast.LENGTH_SHORT).show();
					return;
				}
				UpdateFdList();
				ShowFdList();
			}
        	
        });
        
        mShareButton = (Button) findViewById(R.id.share);
        mShareButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				if(!FB.IsLogin()){
					Toast.makeText(Synchronous.this, "Not Yet Login!", Toast.LENGTH_SHORT).show();
					return;
				}
				
				
				JSONArray ja = new JSONArray();
				try{
					for(int i = 0;i<checked.length;i++){
						if(checked[i]==true)
							ja.put(myFriends.getJSONObject(i));
					}
				}catch(Exception e){
					Log.i("js",e.toString());
				}
				
				if(ja.length()==0){
					Toast.makeText(Synchronous.this, "No target to share! ", Toast.LENGTH_SHORT).show();
					return;
				}
				
                Intent myIntent = new Intent(getApplicationContext(), Share.class);
                myIntent.putExtra("sharefriends", ja.toString());
                startActivity(myIntent);
				
			}

        });

        mDownButton = (Button) findViewById(R.id.share_download_button);
        mDownButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {					
				progress = new ProgressDialog(Synchronous.this);
			    progress.setMessage("Loading...");
			    progress.show();
				new Thread(new Runnable(){
					public void run() {
						if(checked==null || checked.length==0){
							progress.cancel();
							Looper.prepare();
							Toast.makeText(Synchronous.this, "no target to download", Toast.LENGTH_SHORT).show();
							Looper.loop();
							return;
						}
						
						try {
							myid = FbHandler.getMine().getString("id");
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						JSONArray ja = new JSONArray();
						
						try{
							for(int i = 0;i<checked.length;i++){
								if(checked[i]==true)
									ja.put(myFriends.getJSONObject(i));
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						try{
							for(int i = 0;i<ja.length();i++){
								final String sid = ja.getJSONObject(i).getString("id");
								new DownloadThread(sid).start();
							}
						}catch(Exception e){
							Log.i("err",e.toString());
						}
						while(downloadcount>0);
						progress.cancel();
					}				
				}).start();
			}
        });
        
        //UpdateFdList();
        
        //ShowFdList();
        
	}
	
	
	public void UpdateFdList(){
		if(!FB.IsLogin())
			return;	
			progress = new ProgressDialog(this);
	    	progress.setMessage("Loading...");
	    	progress.show();
	    	new Thread (new Runnable(){
				public void run() {
					try{
						myFriends=FbHandler.getFdList();
					}catch(Exception e){
						e.printStackTrace();
					}
								        
					runOnUiThread(new Runnable(){
						public void run() {
							ShowFdList();
							progress.cancel();
						}
					});
					
					if(myFriends.length()==0) return;
					checked = new boolean[myFriends.length()];
					for(int i = 0;i<checked.length;i++){
						checked[i]=false;
					}
				}
	    	}).start();
	    	    
	}

	public void ShowFdList(){
		if(myFriends.length()==0) return;
		listLayout.setOnItemClickListener(Synchronous.this);      
		listLayout.setOnItemClickListener(this);
        listLayout.setAdapter(new FDAdapter(this));
	}
	
	public class DownloadThread extends Thread{
		private int downNumbe=++downloadcount;
		private String downloadTarget;
		public DownloadThread(String sid){
			downloadTarget=sid;
		}
		public void run(){
			ArrayList<NameValuePair> input = new ArrayList<NameValuePair>();
			input.add(new BasicNameValuePair("check","1"));
			input.add(new BasicNameValuePair("token", FbHandler.mFacebook.getAccessToken()));
			input.add(new BasicNameValuePair("readydown","1"));
			input.add(new BasicNameValuePair("myid",myid));
			input.add(new BasicNameValuePair("sid", downloadTarget));
			String response = Share.RequestShare(input);
			if(!response.equals("null")){
				JSONArray ja = new JSONArray();
				try {
					ja=new JSONArray(response);
					for(int i = 0;i<ja.length();i++){
						insert(ja.getJSONObject(i));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			downloadcount--;
		}
		public void insert(JSONObject jo){
			try{
				String fid = jo.getString("friendID");
				String tit = jo.getString("title");
				String sdate = jo.getString("startdate");
				String edate = jo.getString("enddate");
				String stime = jo.getString("starttime");
				String etime = jo.getString("endtime");
				String locat = jo.getString("location");
				String ins[]={fid, tit, sdate, edate, stime, etime, locat};
				AndroidCalendar2Activity.getDB().insert("FriendTimeTable", ins);
			}catch(Exception e){
				Log.i("err",e.toString());
			}
		}
	}
	
	public class FDAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context context;
		
		public class ViewHolder {
			ImageView iconLine;
			TextView textLine;
			CheckBox CheckBox;
		}
		
		public FDAdapter(Context t){
			context = t;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return myFriends.length();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			JSONObject aFriend = null;
			try {
				aFriend = myFriends.getJSONObject(position);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.listinsynchronous, null);
				holder = new ViewHolder();
				holder.textLine = (TextView) convertView.findViewById(R.id.textLine);
				holder.iconLine = (ImageView) convertView.findViewById(R.id.iconLine);
				holder.CheckBox = (CheckBox) convertView.findViewById(R.id.checkBox);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			try {
				holder.textLine.setText(aFriend.getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			holder.textLine.setOnClickListener(new OnClickListener(){
				public void onClick(View v) {
					try{
						Intent myIntent = new Intent(getApplicationContext(), FriendsView.class);
						myIntent.putExtra("targetfriend",myFriends.getJSONObject(position).getString("id"));
						startActivity(myIntent);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
			});
			
			holder.CheckBox.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					if(checked[position]){
						checked[position]=false;
					}else{
						checked[position]=true;
					}
				}
				
			});
			
			holder.CheckBox.setChecked(false);
			
			if(checked[position]==true){
				holder.CheckBox.setChecked(true);
			}
			 
			return convertView;
			
		}

		
	}
	
	// convert dp to px
	private float dp2px(Context t, float dp){
		float scale = t.getResources().getDisplayMetrics().density;
		return (dp*scale +0.5f);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		// TODO Auto-generated method stub
		Toast.makeText(this, position+"", Toast.LENGTH_SHORT).show();
	}
}
