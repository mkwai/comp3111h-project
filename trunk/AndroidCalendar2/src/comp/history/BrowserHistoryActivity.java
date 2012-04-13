package comp.history;
/*
import java.util.Calendar;
import java.util.Timer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;*/

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test2.R;


public class BrowserHistoryActivity extends Activity {
	
	private ArrayList<String> blockWebsites = new ArrayList<String>();
	private Intent testing = new Intent();
	private int numOfLoop = 0;
	static private boolean turn = false; 
	
   	Timer timer = new Timer();  
   	private EditText siteInput;
   	
   	Button turnOnOff, siteAdd;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BrowserHistoryActivity", "start");
        setContentView(R.layout.browserhistory);
        //blockWebsites.add("facebook.com");
        siteInput = (EditText) findViewById(R.id.siteAddress);
        testing.setAction("PopupMessage");
        
        siteAdd= (Button) findViewById(R.id.siteAdd);
        siteAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String siteAddress =  siteInput.getText().toString();
				if(siteAddress.matches("[a-zA-Z0-9]+(.[a-zA-Z0-9])*")){
					if(siteAddress.length()<3) 
						Toast.makeText(BrowserHistoryActivity.this, "Input address is too short!", Toast.LENGTH_SHORT).show();
					else if(blockWebsites.contains(siteAddress))
						Toast.makeText(BrowserHistoryActivity.this, "Input address exists!", Toast.LENGTH_SHORT).show();
					else{
						blockWebsites.add(siteAddress);
						Toast.makeText(BrowserHistoryActivity.this, "Address added", Toast.LENGTH_SHORT).show();
					}
				}
				else Toast.makeText(BrowserHistoryActivity.this, "Please enter a valid address!", Toast.LENGTH_SHORT).show();

			} 

		});
        
        turnOnOff = (Button) findViewById(R.id.setOnOff);
        turnOnOff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				turn = !turn;
				
				if(turn) {
					turnOnOff.setText("Off");
					Toast.makeText(BrowserHistoryActivity.this, "Function start!", Toast.LENGTH_SHORT).show();
				}
				else {
					turnOnOff.setText("On");
					Toast.makeText(BrowserHistoryActivity.this, "Function stop!", Toast.LENGTH_SHORT).show();
				}
			}

		});
        
        timer.scheduleAtFixedRate(new CheckWeb(), 0, 10000);
    }
    
   
    private class CheckWeb extends TimerTask{

		public void run() {
			if(turn){
	        	Log.i("i",numOfLoop+"");
	        	numOfLoop++;
	        	
		    	if(!blockWebsites.isEmpty()){
		    		
		    		boolean hindering = false;
					String stringURL = null; 
					String stringTitle = null; 
					long stringDate = 0;
					
					long within = System.currentTimeMillis() - 15000L;
					
					ContentResolver contentResolver = getContentResolver(); 
					Cursor cursor = contentResolver.query(Uri.parse("content://browser/bookmarks"), 
							new String[]{"title","url","date"}, "date >"+ within, null, null);
			
					while (cursor != null && cursor.moveToNext() && !hindering) { 
						stringTitle = cursor.getString(cursor.getColumnIndex("title")); 
						stringURL = cursor.getString(cursor.getColumnIndex("url")); 
						stringDate = cursor.getLong(cursor.getColumnIndex("date")); 
					    Log.d("debug",  stringDate + " : " + stringTitle+"  ~~~  "+stringURL);
					    for(int i = 0; (i < blockWebsites.size() ) && !hindering; i++){
					    	
					    	if(stringURL.contains(blockWebsites.get(i))) {
					    		hindering = true;
					    		Log.i("conflict", blockWebsites.get(i));
					    		PopupMessage.setSite(blockWebsites.get(i));
					    		startService(testing);
					    	}
					    }	
					    
					}
		    	}
		    	
		        Log.i("end", "end");
			}
			else Log.i("BrowserHistoryActivity","no check");
		}
    }
    
}
