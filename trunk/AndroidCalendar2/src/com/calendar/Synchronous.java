package com.calendar;

import org.json.JSONArray;

import com.facebook.LoginButton;
import com.facebook.DoTask;
import com.facebook.FbHandler;
import com.facebook.FriendListActivity;
import com.test2.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Synchronous extends Activity{
   	
    private LoginButton mLoginButton;
    private FbHandler FB;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.synchronous);
        mLoginButton = (LoginButton) findViewById(R.id.login);
        

        FB = new FbHandler(this);
        mLoginButton.init(this, FB.mFacebook);
	}

	   private class LoadFdTask extends DoTask {	

	    	public LoadFdTask(ProgressDialog progress) {
				super(progress);
				// TODO Auto-generated constructor stub
			}

			@Override
	    	protected Void doInBackground(Void... arg0) {
	    		// TODO Auto-generated method stub

				Intent i = new Intent(Synchronous.this, FriendListActivity.class);
				JSONArray json = null;
				try{
					json=FbHandler.getFdList();
				}catch(Exception e){
					Log.i("error",e.toString());
				}
				if(json==null)return null;
				i.putExtra("fdList", json.toString());
				startActivity(i);
				
				
	    		return null;
	    	}
	    }
}
