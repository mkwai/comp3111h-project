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




public class BrowserHistoryActivity extends Activity {
	
	static private ArrayList<String> blockWebsites = new ArrayList<String>();
	private Intent testing = new Intent();
	private int numOfLoop = 0;
   	Timer timer = new Timer();  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        blockWebsites.add("facebook.com");
        
        testing.setAction("PopupMessage");
        Log.i("start", "start");

        timer.scheduleAtFixedRate(new CheckWeb(), 0, 10000);
        

    }
    
    static public void addBlockWebsite(String address){
		if(!blockWebsites.contains(address)){
			blockWebsites.add(address);
		}
    }
    
    static public void clearList(){
    	blockWebsites.clear();
    }
    
    static public void removeItem(int i){
    	if (i<blockWebsites.size()) blockWebsites.remove(i);
    }
    
    private class CheckWeb extends TimerTask{

		public void run() {

        	Log.i("i",numOfLoop+"");
        	numOfLoop++;
        	
	    	if(!blockWebsites.isEmpty()){
	    		
	    		boolean hindering = false;
				String stringURL = null; 
				String stringTitle = null; 
				long stringDate = 0;
				
				long within = System.currentTimeMillis() - 11000L;
				
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
    }
    
}
