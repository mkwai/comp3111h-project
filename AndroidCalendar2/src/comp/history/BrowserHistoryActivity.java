package comp.history;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.test2.R;


public class BrowserHistoryActivity extends Activity{
	
	private ArrayList<String> blockWebsites = new ArrayList<String>();
	private String tempSite;
	private Intent PopupMessageService = new Intent();
	private int numOfLoop = 0;
	static private boolean turn = false; 
	private ListView webAddressList;
   	private Timer timer = new Timer();  
   	private EditText siteInput;
   	ArrayAdapter<String> adapter;
   	
   	Button turnOnOff, siteAdd;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("BrowserHistoryActivity", "start");
        setContentView(R.layout.browserhistory);

        siteInput = (EditText) findViewById(R.id.siteAddress);
        PopupMessageService.setAction("PopupMessage");
        
        siteAdd= (Button) findViewById(R.id.siteAdd);
        siteAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String siteAddress =  siteInput.getText().toString();
				tempSite = siteAddress.toLowerCase();
				Log.i("web", siteAddress);

				/* bugs need to fix*/
				if(siteAddress.length()<3) 
					Toast.makeText(BrowserHistoryActivity.this, "Input address is too short!", Toast.LENGTH_SHORT).show();

				else if(blockWebsites.contains(tempSite))
					Toast.makeText(BrowserHistoryActivity.this, "Input address exists!", Toast.LENGTH_SHORT).show();
				else{
					
					if(siteAddress.length()>=7){
						if(siteAddress.substring(0, 7).equalsIgnoreCase("http://")){
							Toast.makeText(BrowserHistoryActivity.this, "No need to enter http//", Toast.LENGTH_SHORT).show();
							return;
						}
						else if (siteAddress.length()>=8){
							if (siteAddress.substring(0, 8).equalsIgnoreCase("https://")){
								Toast.makeText(BrowserHistoryActivity.this, "No need to enter https//", Toast.LENGTH_SHORT).show();
								return;
							}
						}
					}

					
					Log.i("Web", "adding");
					addAndRefresh();
				}
				

				//else Toast.makeText(BrowserHistoryActivity.this, "Please enter a valid address!", Toast.LENGTH_SHORT).show();
				/*--------------------*/
			} 

		});
        
        turnOnOff = (Button) findViewById(R.id.setOnOff);
        
		if(turn) turnOnOff.setText("Off");
		else turnOnOff.setText("On");
		
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
        
        setupViews();
        timer.scheduleAtFixedRate(new CheckWeb(), 0, 10000);
    }
    
    private void setupViews(){
    	webAddressList = (ListView) findViewById(R.id.webAddressList);
    	adapter = new ArrayAdapter<String>(this,R.layout.playlist, blockWebsites);
    	webAddressList.setAdapter(adapter);
    	webAddressList.setOnItemClickListener(operation);
    }
    
    private void addAndRefresh(/*String temp*/){
    	Log.i("Web", "before");
	    Handler handler=new Handler();
	    handler.postDelayed(add,1500);
		Log.i("Web", "after");
    }
    
    Runnable add=new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
        	blockWebsites.add(tempSite);
            adapter.notifyDataSetChanged();
            Toast.makeText(BrowserHistoryActivity.this, "Address added", Toast.LENGTH_SHORT).show();
        }       
    };
    
    Runnable remove=new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
        	blockWebsites.remove(tempSite);
            adapter.notifyDataSetChanged();
            Toast.makeText(BrowserHistoryActivity.this, "Address removed", Toast.LENGTH_SHORT).show();
        }       
    };

    private OnItemClickListener operation=new OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position,
                long is) {
        	
        	tempSite = blockWebsites.get(position);
        	final String temp = tempSite;
        	
        	new AlertDialog.Builder(BrowserHistoryActivity.this).
			setTitle("Website").setMessage(temp).
			  setPositiveButton( "OK" ,new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialoginterface, int i){} }).
			  setNegativeButton( "Remove", new DialogInterface.OnClickListener(){
				  public void onClick(DialogInterface dialoginterface, int i){
					    Handler handler=new Handler();
					    handler.postDelayed(remove, 1500);
				  }
			  }).show();
        }

    };
    
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
					    		startService(PopupMessageService);
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
