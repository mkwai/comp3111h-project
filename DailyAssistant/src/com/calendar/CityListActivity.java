package com.calendar;


import java.util.ArrayList;
import java.util.List;

import com.location.CityBean;
import com.location.Utils;
import com.test2.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;

public class CityListActivity extends Activity implements OnItemClickListener{

	private final static String TAG = "CityListActivity";
	private LocationManager locationManager;
	private String bestProvider;
	
	private ListView mListView;
	private List<CityBean> list  = new ArrayList<CityBean>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.citylist);
		
		final String args = getIntent().getExtras().getString(AddEvent.COUNTRY_CODE);

		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("Loading...");
		pd.show();
		new Thread(new Runnable(){
			public void run() {
				list = Utils.getCityInfos(args);
				CityListActivity.this.runOnUiThread(new Runnable(){
					public void run() {
						setupViews();	
						pd.cancel();
					}
				});
			}
		}).start();
	}
	
	private void setupViews(){
		mListView = (ListView) findViewById(R.id.cityList);
		
		ArrayList<String> arrayList = new ArrayList<String>();
		
		int arraySize = list.size();
		for(int i=0;i<arraySize;i++){
			arrayList.add(list.get(i).getCityName());
			Log.i(TAG, "CityName ="+list.get(i).getCityName());
		}
		
		mListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList));
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position,long id) {
		
		
		Thread t = new Thread(new Runnable(){
			public void run() {
				String lat= list.get(position).getLat()+"";
				String lng= list.get(position).getLng()+"";
				String destination = list.get(position).getCityName();
				
				Intent intent = new Intent(CityListActivity.this, AddEvent.class);
				Bundle bundle = new Bundle();
				bundle.putString("placename", destination);
				bundle.putString("placelat", lat);
				bundle.putString("placelng", lng);
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				finish();
				
			}
		});
		
		t.start();
		
		
	}
	@Override
	public void onBackPressed() {
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

}
