package com.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.location.Utils;
import com.test2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TodoList extends Activity {
	// storing today
	protected Calendar currentDateCalendar = Calendar.getInstance();
	Button addTask, sorting;
	TextView today;
	JSONArray titleT;

	LinearLayout linear;
	ListView listview;
	RelativeLayout relative;

	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	TextView tempText;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.todolist);

		today = (TextView) findViewById(R.id.todolist_today);
		addTask = (Button) findViewById(R.id.todolist_addTask);
		sorting = (Button) findViewById(R.id.todolist_sorting);
		//linear = (LinearLayout) findViewById(R.id.todolist_linear);

		// showing the current date
		CharSequence currentYear = DateFormat.format("yyyy",
				currentDateCalendar);
		CharSequence currentMonth = DateFormat.format("MMM",
				currentDateCalendar);
		CharSequence currentDate = DateFormat.format("dd", currentDateCalendar);

		today.setText(currentDate + " " + currentMonth + " , " + currentYear);

		addTask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				startActivity(new Intent("com.calendar.ADDTASK"));
				// finish();
			}

		});

		sorting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg1) {

				Log.i("sort", "test");

				titleT = AndroidCalendar2Activity.getDB().fetchConditional(
						"TaskTable", "");
				/*
				 * titleT =
				 * AndroidCalendar2Activity.getDB().fetchAllNotes("TaskTable",
				 * new String[]{"title","deadlineDate","deadlineTime",
				 * "location","reminder"}, new String[]{""} ) ;
				 */ArrayList<String> TDL = new ArrayList<String>();
				Log.i("JSON Length", titleT.length() + "");

				if (titleT.length() > 0) {
					Log.i("sort", ">0");
					String tempTitle = "";
					String tempProgress = "";
					try {
						for (int i = 0; i < titleT.length(); i++) {
							tempTitle = titleT.getJSONObject(i).getString(
									"title");
							tempProgress = titleT.getJSONObject(i).getString(
									"progress");
							TDL.add((tempTitle.length() > 15 ? tempTitle
									.substring(0, 15) : tempTitle)
									+ " "
									+ tempProgress + "%");
							Log.i("listView", TDL.get(i));
						}

					} catch (Exception e) {
						Log.i("h", e.toString());
					}

				} else
					Log.i("sort", "else");
			}

		});

		listview = new ListView(this);
		addTaskToList(this);
		linear.addView(listview);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		list.clear();
		addTaskToList(this);
	}

	private void addTaskToList(final Context t) {
		JSONArray temp = AndroidCalendar2Activity.getDB().fetchAllNotes(
				"TaskTable", null, null);
		Log.i("JSON Length", temp.length() + "");

		if (temp.length() > 0) {
			Log.i("list", ">0");
			String tempTitle = "";
			String tempProgress = "";
			String tempLocation = "";
			try {

				for (int i = 0; i < temp.length(); i++) {
					HashMap<String, String> item = new HashMap<String, String>();

					tempTitle = temp.getJSONObject(i).getString("title");
					tempProgress = temp.getJSONObject(i).getString("progress");
					tempLocation = temp.getJSONObject(i).getString("location");
					item.put("title", tempTitle);
					item.put("progress", tempProgress + " %");
					item.put("location", tempLocation);

					list.add(item);
					Log.i("title + progress" + " " + i, tempTitle + " "
							+ tempProgress + " " + tempLocation);
				}

				final SimpleAdapter adapter = new SimpleAdapter(this, list,
						R.layout.mylistview,
						new String[] { "title", "progress" }, new int[] {
								R.id.mylistiview_textView1,
								R.id.mylistview_textView2 });

				listview.setAdapter(adapter);
				listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						// dialog

						AlertDialog.Builder builder = new AlertDialog.Builder(t);
						final String info = "Title:      	"
								+ list.get(arg2).get("title") + "\n"
								+ "Progress:   "
								+ list.get(arg2).get("progress") + "\n"
								+ "Location:   "
								+ list.get(arg2).get("location") + "\n";
						builder.setMessage(info);
						builder.setCancelable(true);
 
						builder.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

									}
								});
						builder.setNegativeButton("Edit",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
									}
								});
						AlertDialog alert = builder.create();
						alert.show();

						// append list view
						/*
						 * if (tempText != null) { tempText.setText("");
						 * tempText.setTextSize(1); listview.invalidateViews();
						 * } String loc = list.get(arg2).get("location");
						 * 
						 * tempText = (TextView) arg0.getChildAt(arg2)
						 * .findViewById(R.id.mylistview_textView3);
						 * tempText.setTextSize(25);
						 * tempText.setText("location: " + loc);
						 */
					}

				});
				// **** allow search function ***
				// e.g. press "a" on the keyboard
				listview.setTextFilterEnabled(true);

			} catch (Exception e) {
				Log.i("h", e.toString());
			}

		} else
			Log.i("list", "else");
	}

}
