package com.facebook;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AsyncFacebookRunner;
import com.facebook.BaseRequestListener;
import com.facebook.Facebook;
import com.facebook.FacebookError;
import com.facebook.SessionEvents;
import com.facebook.SessionStore;
import com.facebook.Util;
import com.facebook.SessionEvents.AuthListener;
import com.facebook.SessionEvents.LogoutListener;


public class FbHandler{

    public static final String APP_ID = "290728740995107";
   

	public static Facebook mFacebook = new Facebook(APP_ID);
    public static AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(mFacebook);
    
    public FbHandler(Activity t){
        SessionStore.restore(mFacebook, t);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
    }
    
    public boolean IsLogin(){return mFacebook.isSessionValid();}
    
    public static JSONArray getFdList() throws Exception{
    	
    
    	if(mFacebook.isSessionValid()){
			SampleRequestListener getter = new SampleRequestListener();
            Bundle params = new Bundle();
            params.putString("fields", "id, name, picture");
			
			mAsyncRunner.request("me/friends",params,getter);
			return	getter.result.getJSONArray("data");
    	}
    	
    	return null;
		
    }
    
    public static class SampleAuthListener implements AuthListener {

        public void onAuthSucceed() {
        	//mFacebook.getAccessToken();
        }

        public void onAuthFail(String error) {
        }
    }

    public static class SampleLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
        }

        public void onLogoutFinish() {
        }
    }
    
    
    public static class SampleRequestListener extends BaseRequestListener {
    	
    	public JSONObject result;
    	
    	public SampleRequestListener(){
    		
    	}

        public void onComplete(final String response, final Object state) {
            try {
                JSONObject json = Util.parseJson(response);
                result = json;
                
            } catch (Exception e) {
                Log.w("Facebook-Example", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
            }
        }
    }
}