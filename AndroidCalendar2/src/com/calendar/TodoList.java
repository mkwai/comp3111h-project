package com.calendar;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;

import com.test2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TodoList extends Activity {
	//storing today
	protected Calendar currentDateCalendar = Calendar.getInstance();
	Button addTask, sorting;
	TextView today;
	JSONArray titleT;
	private LinearLayout listLayout;
	//ListView list = (ListView) findViewById(R.id.todoList);  
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.todolist);
		
		today = (TextView) findViewById(R.id.todolist_today);
		addTask = (Button) findViewById(R.id.todolist_addTask);
		sorting = (Button) findViewById(R.id.todolist_sorting);
		
		
		//showing the current date
		CharSequence currentYear = DateFormat.format("yyyy", currentDateCalendar);
		CharSequence currentMonth = DateFormat.format("MMM", currentDateCalendar);
		CharSequence currentDate = DateFormat.format("dd", currentDateCalendar);
		
		today.setText(currentDate + " " + currentMonth + " , " + currentYear);
		
		addTask.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.calendar.ADDTASK"));
			}
			
		});
		
		sorting.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View arg1) {
				
				Log.i("sort", "test");
				Bundle extras = getIntent().getExtras();
				
//		        try {
		        	titleT = AndroidCalendar2Activity.getDB().fetchConditional("TaskTable","");
//				} catch (JSONException e) {Log.i("json",e.toString());}
				
				if (titleT.length()>0) {
					Log.i("sort", ">0");
					try{
						for(int i = 0;i<titleT.length();i++)
							Log.i(i+"", titleT.getJSONObject(i).getString("title"));
						//To-do...
					}catch(Exception e){
			        	Log.i("h",e.toString());
			        }
/*			        try{
			        	for(int i = 0;i<titleT.length();i++){
			        		TextView TV = new TextView(this);
			        		TV.setText(titleT.getJSONObject(i).getString("name"));
			        		listLayout.addView(TV,i);
			        	}
			        	
			        }catch(Exception e){
			        	Log.i("h",e.toString());
			        }
*/				
				}
				else Log.i("sort", "else");
			}

		});
			
	}
}
