package comp.location;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

public class LocationDurationActivity extends Activity implements OnClickListener{
    
	private final static String TAG = " LocationDurationActivity";
	
	final static String DURATION_INFOS="duration_infos";
	final static String COUNTRY_CODE = "country_code";
	private final static int CODE =3;
	
	private Button search;
	private EditText contryCode;
	
//	private ListView countryList;
//	private String[] countryValues;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        setupViews();
    }
    
	private void setupViews(){
		search = (Button) findViewById(R.id.search);
		contryCode = (EditText) findViewById(R.id.country_code);
		
		search.setOnClickListener(this);		
	}
	
	public void search(String code){
		String temp = code.replace('\n', '+');
		String args = "http://maps.googleapis.com/maps/api/geocode/json?address=" 
				+ temp.replace(' ', '+') + "&sensor=false";
		
		Intent intent = new Intent(this, CityListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(COUNTRY_CODE, args);
		
		intent.putExtras(bundle);
		startActivityForResult(intent, CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == CODE) {
			TravelingDuration TD = (TravelingDuration)data.getSerializableExtra(DURATION_INFOS);
			String message;
			String title;
			if(TD.getTotalSecond() == -1) {
				message = "The traveling time " + TD.getTimeTaken();
				title = "Warning!";
			}
			else {
				message = "Traveling from current location to " +TD.getDestination()
									+ " takes "+ TD.getTimeTaken();
				title = "Traveling Time";
			}
			new AlertDialog.Builder(LocationDurationActivity.this).
			setTitle(title).setMessage(message).
			  setPositiveButton( "OK" ,new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialoginterface, int i){	}          
			}).show();    
			
		}
	}
	@Override
	public void onClick(View v) {
		search(contryCode.getText().toString());
	}

	
}