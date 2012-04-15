package com.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;

import com.Alarm.Alarms;
import com.test2.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddTask extends Activity {
	private Button deadlineDateButton;
	private Button deadlineTimeButton;
	private Button cancelButton;
	private Button confirmButton;
	private EditText title_edit;
	private EditText location;
	private CheckBox reminder;

	private RadioGroup e;
	private CheckBox deadline;
	private TextView reminder_text;
	//reminder in terms of MINUTES
	private int reminder_setting;
	
	private Calendar currentDateCalendar = Calendar.getInstance();
	private Calendar deadlineCalendar = Calendar.getInstance();

	public static final int DEADLINE_DATE_DIALOG = 0;
	public static final int DEADLINE_TIME_DIALOG = 1;

	private SeekBar progressBar;
	private TextView progressPercent;
	private int progress = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.addtask);

		title_edit = (EditText) findViewById(R.id.addtask_title_edit);
		location = (EditText) findViewById(R.id.addtask_location_edit);
		reminder = (CheckBox) findViewById(R.id.addtask_reminder_checkBox);
		reminder_text = (TextView) findViewById(R.id.addtask_reminder_text);
		
		deadline = (CheckBox) findViewById(R.id.addtask_deadline_checkBox);

		e = (RadioGroup) findViewById(R.id.addtask_reminder_radio);

		// progress setting
		progressPercent = (TextView) findViewById(R.id.addtask_progressPercent);
		progressBar = (SeekBar) findViewById(R.id.addtask_progressBar);

		progressBar
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar, int p,
							boolean fromUser) {
						// TODO Auto-generated method stub
						progressPercent.setText(p + " % ");
						progress = p;
					}
				});

		// deadline setting
		deadlineDateButton = (Button) findViewById(R.id.addtask_deadlinedate_button);
		deadlineTimeButton = (Button) findViewById(R.id.addtask_deadlinetime_button);

		CharSequence currentYear = DateFormat.format("yyyy",
				currentDateCalendar);
		CharSequence currentMonth = DateFormat.format("MMM",
				currentDateCalendar);
		CharSequence currentDate = DateFormat.format("dd", currentDateCalendar);
		CharSequence currentHour = DateFormat.format("kk", currentDateCalendar);
		int currentMinute = Integer.parseInt((String) (DateFormat.format("mm",
				currentDateCalendar)));

		currentMinute = (currentMinute / 5) * 5;

		// convert the minutes in current calendar to 5 minute interval
		currentDateCalendar.set(Calendar.MINUTE, currentMinute);
		deadlineCalendar = currentDateCalendar;

		CharSequence currentMinuteConverted = currentMinute <= 5 ? "0"
				+ Integer.toString(currentMinute) : Integer
				.toString(currentMinute);

		deadlineDateButton.setText(currentMonth + " " + currentDate + " , "
				+ currentYear);

		deadlineTimeButton.setText(currentHour + ":" + currentMinuteConverted);
		deadlineDateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(DEADLINE_DATE_DIALOG);
			}
		});
		deadlineTimeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(DEADLINE_TIME_DIALOG);
			}
		});

		// deadline is optional

		deadline.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (deadline.isChecked()) {
					deadlineDateButton.setVisibility(0);
					deadlineTimeButton.setVisibility(0);
					reminder.setVisibility(0);
					reminder_text.setVisibility(0);
				}
				if (!deadline.isChecked()) {
					deadlineDateButton.setVisibility(8);
					deadlineTimeButton.setVisibility(8);
					reminder.setVisibility(8);
					reminder_text.setVisibility(8);
				}
			}

		});
		// reminder setting

		reminder.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (reminder.isChecked())
					e.setVisibility(0);
				if (!reminder.isChecked())
					e.setVisibility(8);
			}

		});

		// cancel/confirm button
		cancelButton = (Button) findViewById(R.id.addtask_cancel_button);
		confirmButton = (Button) findViewById(R.id.addtask_confirm_button);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				finish();
			}
		});

		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// TODO Auto-generated method stub
				// sent data to database

				String taskid = AndroidCalendar2Activity.getDB().GiveEventID();

				String title = title_edit.getText().toString();
				if (title.length() == 0 || title.startsWith(" ")) {
					Toast.makeText(AddTask.this,
							"You Should enter a valid title",
							Toast.LENGTH_SHORT).show();
					return;
				}

				String deadlineTime = deadlineTimeButton.getText().toString();

				String deadlineDate = "";

				SimpleDateFormat df = new SimpleDateFormat("MMM dd , yyyy");
				try {
					Date ddate = df.parse(deadlineDateButton.getText()
							.toString());
					deadlineDate = DateFormat.format("yyyyMMdd", ddate) + "";
				} catch (Exception e) {
					Log.i("error", e.toString());
				}

				String locat = location.getText().toString();
				int reminderID = e.getCheckedRadioButtonId();
				//reminder_setting <- radio group choice
				switch(reminderID){
				case  R.id.addtask_5min:
					reminder_setting = 5;
					break;
				case  R.id.addtask_30min:
					reminder_setting = 30;
					break;
				case  R.id.addtask_1hour:
					reminder_setting = 60;
					break;
				case  R.id.addtask_2hour:
					reminder_setting = 120;
					break;
				case  R.id.addtask_6hour:
					reminder_setting = 360;
					break;
				case  R.id.addtask_24hour:
					reminder_setting = 1440;
					break;
					default:
						reminder_setting = 0;
				}
				

				final long milliSecond = deadlineCalendar.getTimeInMillis()
										- reminder_setting*60000L;
				
				//do not store deadline if deadline checked not checked
				if (!deadline.isChecked()) {
					String args[] = { taskid, title, 0 + "",0 + "", locat,
							progress + "", 0 +"" , milliSecond+""};

					AndroidCalendar2Activity.getDB().insert("TaskTable", args);

				} 
				else {
					
					if(reminder.isChecked()){
						String args[] = { taskid, title, deadlineDate,
								deadlineTime, locat, progress + "", reminder_setting +"" , milliSecond+""};
						AndroidCalendar2Activity.getDB().insert("TaskTable", args);
					}
					else{
						//set the reminder = 0 if reminder is not checked
						String args[] = { taskid, title, deadlineDate,
								deadlineTime, locat, progress + "", 0 + "" , milliSecond+""};
						AndroidCalendar2Activity.getDB().insert("TaskTable", args);
					}
				}

				/*!!!!!!!!!!!!! Alert User !!!!!!!!!!!!!!!*/
				if(reminder_setting>0 && progress<100){
					final String ID = taskid;
					final String taskTitle = title; 
					new Thread(new Runnable() {
						public void run() {
								Log.i("temp!!", milliSecond +"");
								Alarms.addAlarm(AddTask.this, ID, taskTitle, milliSecond, false);
						}
					}).start();
				}
				finish();

			}

		});

	}

	// call when create dialog
	protected Dialog onCreateDialog(int id) {

		// listener for setting date in deadline date dialog
		OnDateSetListener deadlineDateSetListener = new OnDateSetListener() {

			// use when "set" press
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// store the date set by the dialog to deadlineCalendar variable
				deadlineCalendar.set(year, monthOfYear, dayOfMonth);
				deadlineDateButton.setText(DateFormat.format("MMM",
						deadlineCalendar)
						+ " "
						+ DateFormat.format("dd", deadlineCalendar)
						+ " , "
						+ DateFormat.format("yyyy", deadlineCalendar));
			}
		};

		// listener for setting date in deadline time dialog
		OnTimeSetListener deadlineTimeSetListener = new OnTimeSetListener() {

			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				deadlineCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				deadlineCalendar.set(Calendar.MINUTE, minute);
				deadlineTimeButton.setText(DateFormat.format("kk",
						deadlineCalendar)
						+ ":"
						+ DateFormat.format("mm", deadlineCalendar));
			}
		};

		// select which dialog to show by verifying which button was press
		switch (id) {
		case DEADLINE_DATE_DIALOG:
			return new DatePickerDialog(this, deadlineDateSetListener,
					deadlineCalendar.get(Calendar.YEAR),
					deadlineCalendar.get(Calendar.MONTH),
					deadlineCalendar.get(Calendar.DAY_OF_MONTH));
		case DEADLINE_TIME_DIALOG:
			return new MyTimePickerDialog(this, deadlineTimeSetListener,
					deadlineCalendar.get(Calendar.HOUR_OF_DAY),
					deadlineCalendar.get(Calendar.MINUTE), true);
		}

		return null;
	}

	private class MyTimePickerDialog extends TimePickerDialog {

		private int currentMinute = 0;

		public MyTimePickerDialog(Context context, OnTimeSetListener callBack,
				int hourOfDay, int minute, boolean is24HourView) {
			super(context, callBack, hourOfDay, minute, is24HourView);
			// TODO Auto-generated constructor stub
			currentMinute = restoreMinute(minute);
		}

		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			updateDisplay(view, hourOfDay, minute);
		}

		private void updateDisplay(TimePicker timePicker, int hourOfDay,
				int minute) {

			// do calculation of next time
			if (currentMinute == 0 && minute == 59) {
				currentMinute = 55;
			} else
				currentMinute = ((currentMinute - minute) > 0) ? currentMinute - 5
						: currentMinute + 5;

			timePicker
					.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
						public void onTimeChanged(TimePicker view,
								int hourOfDay, int minute) {
						}
					});

			// set minute
			timePicker.setCurrentMinute(currentMinute);
			if (currentMinute == 60)
				timePicker.setCurrentHour(hourOfDay + 1);
			currentMinute = timePicker.getCurrentMinute();

			timePicker
					.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
						public void onTimeChanged(TimePicker view,
								int hourOfDay, int minute) {
							updateDisplay(view, hourOfDay, minute);
						}
					});

		}

	}

	// lower bound
	private int restoreMinute(int minute) {
		// initialize intervals[] to {0,5,10,...,55}
		int intervals[] = new int[12];
		int startmin = 0;
		for (int i = 0; i < 12; i++) {
			intervals[i] = startmin;
			startmin = startmin + 5;
		}

		// find closest & smaller value of minute in interval

		for (int i = 11; i >= 0; i--) {
			if (minute > intervals[i]) {
				return intervals[i];

			}
		}
		return 0;
	}


}
