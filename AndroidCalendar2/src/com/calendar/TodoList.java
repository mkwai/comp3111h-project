package com.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class TodoList extends Activity {
	// storing today
	protected Calendar currentDateCalendar = Calendar.getInstance();
	Button addTask;
	Spinner sorting;
	TextView today;
	JSONArray titleT;
	CheckBox showFinishTask;

	LinearLayout linear;
	ListView listview;
	SimpleAdapter adapter;
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.todolist);

		today = (TextView) findViewById(R.id.todolist_today);
		addTask = (Button) findViewById(R.id.todolist_addTask);
		sorting = (Spinner) findViewById(R.id.todolist_sorting);
		linear = (LinearLayout) findViewById(R.id.todolist_linear);
		showFinishTask = (CheckBox) findViewById(R.id.todolist_show_checkbox);

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

		// sorting function
		final ArrayAdapter<String> a = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] { "title",
						"progress", "deadline" });
		a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sorting.setAdapter(a);
		
		sorting.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Log.i("selected", a.getItem(arg2).toString());
				// alvin: implement sort (a.getItem(arg2).toString() =
				// title,progress or deadline)
				if (a.getItem(arg2).toString() == "title")
					Collections.sort(list, new byTitle());
				if (a.getItem(arg2).toString() == "progress")
					Collections.sort(list, new byProgress());
				if (a.getItem(arg2).toString() == "deadline")
					Collections.sort(list, new byDeadline());
				listview.invalidateViews();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				Log.i("NOT selected", "aaa");
			}

		});

		// initialize list view
		listview = new ListView(this);
		addTaskToList(this);
		linear.addView(listview);
		// sort the list view by Title after initialize it
		Collections.sort(list, new byTitle());
		
		showFinishTask
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (!showFinishTask.isChecked()) {
							for (int i = 0; i < list.size(); i++) {
								Log.i("checking", i + "");
								if (list.get(i).get("progress").equals("100 %")) {
									list.remove(i);
									Log.i("remove", i + "");
								}
							}
							listview.invalidateViews();
						}

						if (showFinishTask.isChecked()) {
							list.clear();
							addTaskToList(TodoList.this);
							listview.invalidateViews();
						}
					}

				});

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
			String tempId = "";
			String tempTitle = "";
			String tempProgress = "";
			String tempLocation = "";
			String tempDeadlineDate = "";
			String tempDeadlineTime = "";
			String tempReminder = "";
			try {

				for (int i = 0; i < temp.length(); i++) {
					HashMap<String, String> item = new HashMap<String, String>();
					tempId = temp.getJSONObject(i).getString("taskID");
					tempTitle = temp.getJSONObject(i).getString("title");
					tempProgress = temp.getJSONObject(i).getString("progress");
					tempLocation = temp.getJSONObject(i).getString("location");
					tempDeadlineDate = temp.getJSONObject(i).getString(
							"deadlineDate");
					tempDeadlineTime = temp.getJSONObject(i).getString(
							"deadlineTime");
					tempReminder = temp.getJSONObject(i).getString("reminder");

					item.put("taskID", tempId);
					item.put("title", tempTitle);
					item.put("progress", tempProgress + " %");
					item.put("location", tempLocation);
					item.put("deadlineDate", tempDeadlineDate);
					item.put("deadlineTime", tempDeadlineTime);
					item.put("reminder", tempReminder);

					list.add(item);
					Log.i("title + progress" + " " + i, tempTitle + " "
							+ tempProgress + " " + tempLocation + " "
							+ tempDeadlineDate + " " + tempDeadlineTime + " "
							+ tempReminder);
				}

				adapter = new SimpleAdapter(this, list, R.layout.mylistview,
						new String[] { "title", "progress" }, new int[] {
								R.id.mylistiview_textView1,
								R.id.mylistview_textView2 });

				listview.setAdapter(adapter);
				listview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						AlertDialog.Builder builder = new AlertDialog.Builder(t);
						final String tid = list.get(arg2).get("taskID");
						String title = list.get(arg2).get("title");
						String progress = list.get(arg2).get("progress");
						String location = list.get(arg2).get("location");
						String deadlineDate = list.get(arg2)
								.get("deadlineDate");
						String deadlineTime = list.get(arg2)
								.get("deadlineTime");
						String reminder = list.get(arg2).get("reminder");
						Log.i("re", reminder);

						if (deadlineDate.length() != 8) {
							deadlineDate = "NA";
							deadlineTime = "";

						} else {
							Calendar c = Calendar.getInstance();
							c.set(Integer
									.parseInt(deadlineDate.substring(0, 4)),
									Integer.parseInt(deadlineDate.substring(4,
											6)) - 1, Integer
											.parseInt(deadlineDate.substring(6,
													8)));
							deadlineDate = DateFormat
									.format("MMM dd , yyyy", c) + "";
						}

						switch (Integer.parseInt(reminder)) {
						case 5:
							reminder = "5 minutes";
							break;
						case 30:
							reminder = "30 minutes";
							break;
						case 60:
							reminder = "1 hour";
							break;
						case 120:
							reminder = "2 hours";
							break;
						case 360:
							reminder = "6 hours";
							break;
						case 1440:
							reminder = "24 hours";
							break;
						default:
							reminder = "NA";
						}

						String info = "Title:        	 " + title + "\n"
								+ "Progress:   " + progress + "\n"
								+ "Location:   " + location + "\n"
								+ "Deadline:	" + deadlineDate + " "
								+ deadlineTime + "\n" + "Reminder:	" + reminder;

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

										Intent i = new Intent(
												"com.calendar.EDITTASK");
										i.putExtra("taskID", tid);
										startActivity(i);
										finish();
									}
								});
						AlertDialog alert = builder.create();
						alert.show();

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

	private class byTitle implements Comparator {

		@Override
		public int compare(Object arg0, Object arg1) {
			// TODO Auto-generated method stub
			String t0 = (String) ((HashMap) arg0).get("title");
			String t1 = (String) ((HashMap) arg1).get("title");

			return t0.compareTo(t1);
		}

	}

	private class byProgress implements Comparator {

		@Override
		public int compare(Object arg0, Object arg1) {
			// TODO Auto-generated method stub
			String t0 = (String) ((HashMap) arg0).get("progress");
			String t1 = (String) ((HashMap) arg1).get("progress");

			return t0.compareTo(t1);
		}

	}

	private class byDeadline implements Comparator {

		@Override
		public int compare(Object arg0, Object arg1) {
			// TODO Auto-generated method stub
			String t0 = (String) ((HashMap) arg0).get("deadlineDate");
			String t1 = (String) ((HashMap) arg1).get("deadlineDate");

			if (t0.compareTo(t1) != 0)
				return t0.compareTo(t1);

			String t2 = (String) ((HashMap) arg0).get("deadlineTime");
			String t3 = (String) ((HashMap) arg1).get("deadlineTime");
			return t2.compareTo(t3);

		}

	}
}
