package com.googlesync; 

import com.test2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class GoogleSyncActivity extends Activity {
	/** Called when the activity is first created. */
	private Button sync;
	private Button disconnect;
	private GoogleSync temp;
	private EditText ET_username;
	private EditText ET_password;
	private String username;
	private String password;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlesync);
		
		temp= new GoogleSync();
		sync= (Button) findViewById(R.id.gs_sync);
		sync.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ET_username= (EditText) findViewById(R.id.gs_username);
				ET_password= (EditText) findViewById(R.id.gs_password);
				username= ET_username.getText().toString();
				password= ET_password.getText().toString();
				
				System.out.println(username);
				System.out.println(password);
				
				if (username.length()==0){
					Context context = getApplicationContext();
					CharSequence text = "Please fill in user name.";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
				else if (password.length()==0){
					Context context = getApplicationContext();
					CharSequence text = "Please fill in password.";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
				else{ 
					temp.setUserInfo(username, password);
					if (temp.GoogleLogin()== false)
						ShowMsgDialog("System","User name and password not match.");	
					else
						ShowMsgDialog("System","Connected Successfully.");	
				}
					
			}
		});
		
		disconnect = (Button) findViewById(R.id.gs_disconnect);
		disconnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
	}
	
	private void ShowMsgDialog(String title, String msg) {
		AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(this);
		MyAlertDialog.setTitle(title);
		MyAlertDialog.setMessage(msg);
		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which) {
				// no action
			}
		};
		MyAlertDialog.setNeutralButton("Okay", OkClick);
		MyAlertDialog.show();
	}

}
