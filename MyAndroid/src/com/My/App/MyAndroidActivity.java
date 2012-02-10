//here is the modified part

package com.My.App;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class MyAndroidActivity extends Activity {
	

	private SQLiteDatabase db;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /*
        GridView gridview = (GridView) findViewById(R.id.gridView1);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(MyAndroidActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        */
        final TextView textview = (TextView) findViewById(R.id.text1);
        final Button savebutton = (Button) findViewById(R.id.button1);
        final Button loadbutton = (Button) findViewById(R.id.button2);
        final Button clearbutton = (Button) findViewById(R.id.button3);
        final EditText edittext = (EditText) findViewById(R.id.editText1);
        final DigitalClock digitC=(DigitalClock) findViewById(R.id.digitalClock1);
        
        
        
        edittext.setOnKeyListener(new View.OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					
					return true;
				}
				return false;
			}
		});
                
        savebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	try{
            		FileOutputStream out = openFileOutput("testfile.txt",MODE_APPEND);
            		OutputStreamWriter osw = new OutputStreamWriter(out);
            		
            		osw.append(edittext.getText().toString()+"\n");
            		osw.flush();
            		osw.close();

                	textview.setText("save: "+edittext.getText());
                	edittext.setText(null);
            		
            	}catch(Exception e){
            		Log.e("Save File",e.toString());
            	}
            	
            }
        });
        
        loadbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try{
					FileInputStream fIn = openFileInput("testfile.txt");
					InputStreamReader isr = new InputStreamReader(fIn);
					
					char[] inputBuffer = new char[10];
                    String s = "";

                    int charRead;
                    while ((charRead = isr.read(inputBuffer))>0)
                    {
                        String readString = String.copyValueOf(inputBuffer, 0, charRead);
                        s += readString;
                        inputBuffer = new char[10];
                    }
                    
                    textview.setText("load: \n"+s+" in "+getFilesDir());
                    
                    
				}catch(Exception e){
					Log.e("load File",e.toString());
				}
			}
		});
        
        clearbutton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				try{
					FileOutputStream out = openFileOutput("testfile.txt",MODE_PRIVATE);
            		OutputStreamWriter osw = new OutputStreamWriter(out);
            		osw.write("");
            		osw.flush();
            		osw.close();
            		
            		textview.setText("cleared");
				}catch(Exception e){
					Log.e("clear File",e.toString());
				}
			}
		});
        
        
    }
    
    public void uploadData() throws Exception{
    	JSONObject data=new JSONObject();
    	data.put("id", "1000");
    	
    	
    	
    }
    
    public String connectRemoteDB(String returnString){
    	Log.i("log_tag","start connecting");
    	InputStream is = null;
        
    	   String result = "";
    	    //the year data to send
    	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	    nameValuePairs.add(new BasicNameValuePair("id","1000"));

    	    //http post
    	    try{
    	            HttpClient httpclient = new DefaultHttpClient();
    	            HttpPost httppost = new HttpPost("http://ihome.ust.hk/~kwhon/cgi-bin/about.php");
    	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	            HttpResponse response = httpclient.execute(httppost);
    	            HttpEntity entity = response.getEntity();
    	            is = entity.getContent();

    	    }catch(Exception e){
    	            Log.e("log_tag", "Error in http connection "+e.toString());
    	    }
    	    
    	    
    	    //convert response to string
    	    try{
    	    		
    	            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
    	            StringBuilder sb = new StringBuilder();
    	            String line = null;
    	            while ((line = reader.readLine()) != null) {
    	                    sb.append(line + "\n");
    	            }
    	            is.close();
    	            result=sb.toString();
    	    }catch(Exception e){
    	            Log.e("log_tag", "Error converting result "+e.toString());
    	    }
    	    
    	    
    	    
    	    Log.i("result","result: "+result);
    	    
    	    //parse json data
    	    try{
    	            JSONArray jArray = new JSONArray(result);
    	            for(int i=0;i<jArray.length();i++){
    	                    JSONObject json_data = jArray.getJSONObject(i);
    	                    Log.i("log_tag","id: "+json_data.getInt("id")+
    	                            ", username: "+json_data.getString("username")
    	                    );
    	                    //Get an output to the screen
    	                    returnString += "\n\t" + jArray.getJSONObject(i); 
    	            }
    	    }catch(JSONException e){
    	            Log.e("log_tag", "Error parsing data "+e.toString());
    	    }
    	    return returnString; 
    }
    
}



