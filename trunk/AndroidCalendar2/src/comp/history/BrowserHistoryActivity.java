package comp.history;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;


public class BrowserHistoryActivity extends Activity {
	
	ArrayList<String> blockWebsites = new ArrayList<String>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        blockWebsites.add("ust.hk");
        int i = 0;
       	while(i<10){
        	Log.i("start", "start");
        	this.loop();
        	i++;
        }
    	Log.i("start", "start");
    	this.loop();
    }
    
    public void loop(){
    	
    	boolean hindering = false;
    	
    	if(!blockWebsites.isEmpty()){
			String stringURL = null; 
			String stringTitle = null; 
			long stringDate = 0;
			
			Calendar current = Calendar.getInstance();
			long within = current.getTimeInMillis() - 500000L;
			
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
			    		hindering=true;
			    		Log.i("conflict", blockWebsites.get(i));
			    	}
			    }	
			    
			}
    	}
    	
    	if(hindering){
    		Log.i("pop","up");
    		showPopupWindow();
    	}
    	
        try{
            Thread.sleep(1000);
            Log.i("1","1");
            Thread.sleep(1000);
            Log.i("2","2");
            Thread.sleep(1000);
            Log.i("3","3");
            Thread.sleep(1000);
            Log.i("4","4");
            Thread.sleep(1000);
            Log.i("5","5");
            Thread.sleep(1000);
            Log.i("6","6");
            Thread.sleep(1000);
            Log.i("7","7");
            Thread.sleep(1000);
            Log.i("8","8");
            Thread.sleep(1000);
            Log.i("9","9");
            Thread.sleep(1000);
            Log.i("10","10");
        }catch(Exception e){}
            
            
        Log.i("end", "end");

    }
    
    public void addBlockWebsite(String address){
		if(!blockWebsites.contains(address)){
			blockWebsites.add(address);
		}
    }
    
	private void showPopupWindow() {
/*		Context mContext = BrowserHistoryActivity.this;
		LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View music_popunwindwow = mLayoutInflater.inflate(R.layout.music_popwindow, null);
		PopupWindow mPopupWindow = new PopupWindow(music_popunwindwow,LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0);*/
	}
}
