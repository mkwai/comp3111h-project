package comp.location;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
	
	/*temporary use
	private final double UST_LAT = 22.336659;
	private final double UST_LNG = 114.266725;*/
	
	private LocationManager locationManager;
	private String bestProvider;
	
	private ListView mListView;
	private List<CityBean> list  = new ArrayList<CityBean>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.citylist);
		String args = getIntent().getExtras().getString(LocationDurationActivity.COUNTRY_CODE);
		list = Utils.getCityInfos(args);
		setupViews();
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
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		bestProvider = locationManager.getBestProvider(criteria, false);
		
		Location location = locationManager.getLastKnownLocation(bestProvider);
		Log.i("location!!!", location.getLatitude()+"");
		String latAndLng = list.get(position).getLat()+","+list.get(position).getLng();
		String destination = list.get(position).getCityName();
		String args = "http://maps.google.com/maps/api/directions/json?origin=" + latAndLng
			+ "&destination=" + location.getLatitude() + "," + location.getLongitude() + "&sensor=false";
		
		Intent intent = new Intent(this, LocationDurationActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(LocationDurationActivity.DURATION_INFOS, Utils.getDuration(args,destination));
		intent.putExtras(bundle);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

}
