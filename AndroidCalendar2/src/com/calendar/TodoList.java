package com.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.location.Utils;
import com.test2.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
	
	LinearLayout linear ;
	ListView listview ;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.todolist);
		
		today = (TextView) findViewById(R.id.todolist_today);
		addTask = (Button) findViewById(R.id.todolist_addTask);
		sorting = (Button) findViewById(R.id.todolist_sorting);
		linear = (LinearLayout) findViewById(R.id.todolist_linear);

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
				
		        titleT = AndroidCalendar2Activity.getDB().fetchConditional("TaskTable","");
/*		        titleT = AndroidCalendar2Activity.getDB().fetchAllNotes("TaskTable",
        		new String[]{"title","deadlineDate","deadlineTime", "location","reminder"}, 
				new String[]{""}
				) ;
*/		        ArrayList<String> TDL = new ArrayList<String>();
		        Log.i("JSON Length", titleT.length()+"");
		        
				if (titleT.length()>0) {
					Log.i("sort", ">0");
					String tempTitle = "";
					String tempProgress = "";
					try{
						for(int i = 0; i<titleT.length();i++){
							tempTitle =  titleT.getJSONObject(i).getString("title");
							tempProgress =  titleT.getJSONObject(i).getString("progress");
							TDL.add((tempTitle.length()>15? tempTitle.substring(0, 15): tempTitle)+ " "  + tempProgress + "%");
							Log.i("listView", TDL.get(i));
						}

					}catch(Exception e){
			        	Log.i("h",e.toString());
			        }
						
				}
				else Log.i("sort", "else");
			}

		});
		
		listview = new ListView(this);
		addTaskToList(this);
		linear.addView(listview);
			
	}
	
	 private void addTaskToList(Context t){
		 JSONArray temp = AndroidCalendar2Activity.getDB().fetchAllNotes("TaskTable",null,null);
	        Log.i("JSON Length", temp.length()+"");
	        
			if (temp.length()>0) {
				Log.i("list", ">0");
				String tempTitle = "";
				String tempProgress = "";
				try{
					
					List<String> data = new ArrayList<String>();
					
					for(int i = 0; i<temp.length();i++){
						tempTitle =  temp.getJSONObject(i).getString("title");
						Log.i("title", tempTitle);
						tempProgress =  temp.getJSONObject(i).getString("progress");
						data.add(tempTitle + " " + tempProgress);
					}
					listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,data));
					
				}catch(Exception e){
		        	Log.i("h",e.toString());
		        }
					
			}
			else Log.i("list", "else");
	 }

}
