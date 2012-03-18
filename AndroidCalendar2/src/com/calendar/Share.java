package com.calendar;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.calendar.Synchronous.FDAdapter.ViewHolder;
import com.test2.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import android.widget.TextView;


public class Share extends Activity{
	private Button mShare;
	private Button mStartDate;
	private Button mEndDate;
	private LinearLayout listLayout;
	private JSONArray shareFriends;
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
        
        
	}

	
}