package com.facebook;

import org.json.JSONArray;
import org.json.JSONObject;

import com.test2.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;


public class FriendListActivity extends Activity{

	
	@Override
    public void onCreate(Bundle savedInstanceState) {       
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.friends);
		try{
			JSONArray b = new JSONArray(this.getIntent().getExtras().getString("fdList"));
			Log.i("got ",b.length()+"");
		}catch(Exception e){
			Log.i("got",e.toString());
		}
		
	}
	
	 
    private class LoadFdTask extends DoTask {	

    	public LoadFdTask(ProgressDialog progress) {
			super(progress);
			// TODO Auto-generated constructor stub
		}

		@Override
    	protected Void doInBackground(Void... arg0) {
    		// TODO Auto-generated method stub
			
    		return null;
    	}
    }
    
    
}