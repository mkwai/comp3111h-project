package com.calendar;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.LoginButton;
import com.facebook.DoTask;
import com.facebook.FbHandler;
import com.facebook.Util;
import com.facebook.Utility;
import com.test2.R;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Synchronous extends Activity implements OnItemClickListener{
   	
    private LoginButton mLoginButton;
    private Button mRefreshButton;
    private FbHandler FB;
	ListView listLayout;
	private JSONArray myFriends = new JSONArray();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
			}
        	
        });
        
        getFdFromDB();
        
        ShowFdList();
        
        
        
	}
	
	public void getFdFromDB(){
		myFriends = AndroidCalendar2Activity.getDB().fetchAllNotes("FriendTable", null, null);
	}
	
	public void UpdateFdList(){
		if(FB.IsLogin()){
        	ProgressDialog progress = new ProgressDialog(this);
    		progress.setMessage("Loading...");
    		new LoadFdTask(progress).execute();	
		}
        
	}

	public void ShowFdList(){
		if(myFriends.length()==0) return;
		listLayout.setOnItemClickListener(Synchronous.this);      
		listLayout.setOnItemClickListener(this);
        listLayout.setAdapter(new FDAdapter(this));
	}
	
	
	public class FDAdapter extends BaseAdapter implements Filterable {
		private LayoutInflater mInflater;
		private Context context;
		
		public class ViewHolder {
			ImageView iconLine;
			TextView textLine;
			CheckBox CheckBox;
		}
		
		ViewHolder holders[];
		
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
				// TODO Auto-generated catch block
				Log.i("afd",e.toString());
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
			
			String picurl = "";
			try {
				holder.textLine.setText(aFriend.getString("name")+ "\nLast updated: "+aFriend.getString("lastUpdate"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.i("fd",e.toString());
			}
			 
			return convertView;
			
		}

		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			return null;
		}

		
	}
	
	
	private class LoadFdTask extends DoTask {
		
		public LoadFdTask(ProgressDialog progress) {
			super(progress);
			
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try{
				myFriends=FbHandler.getFdList();
			}catch(Exception e){
				Log.i("error",e.toString());	
			}
			
			Synchronous.this.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					for(int i = 0;i<myFriends.length();i++){
						String fid ="";
						String fn = "";
						String piclink = "";
						try {
							fid = myFriends.getJSONObject(i).getString("id");
							fn = myFriends.getJSONObject(i).getString("name");
							piclink = myFriends.getJSONObject(i).getString("picture");
						} catch (JSONException e) {
							Log.i("json",e.toString());
						}
						Date d = new Date();
						String time = d.getDate()+"/"+(d.getMonth()+1)+"/"+(d.getYear()+1900);
						
						String condition = " friendID = '"+fid+"' ";
						JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional("FriendTable", condition);
						if(ja.length()==0){
							AndroidCalendar2Activity.getDB().insert("FriendTable", 
									new String[] {fid, fn, "0", time, piclink});
						}
						
					}

			        getFdFromDB();
					
					ShowFdList();
					
					
				}
			});
			
			return null;
		}
		
		@Override
		public void onPostExecute(Void unused) {
			progress.dismiss();
		}
	}

	// convert dp to px
	private float dp2px(Context t, float dp){
		float scale = t.getResources().getDisplayMetrics().density;
		return (dp*scale +0.5f);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}
