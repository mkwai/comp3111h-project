package com.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.test2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EditTask extends Activity{
	private Button deleteButton;
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
	
	private String taskID;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.edittask);
		
		title_edit = (EditText) findViewById(R.id.edittask_title_edit);
		location = (EditText) findViewById(R.id.edittask_location_edit);
		reminder = (CheckBox) findViewById(R.id.edittask_reminder_checkBox);
		reminder_text = (TextView) findViewById(R.id.edittask_reminder_text);
		deadline = (CheckBox) findViewById(R.id.edittask_deadline_checkBox);
		e = (RadioGroup) findViewById(R.id.edittask_reminder_radio);
		
		// progress setting
		progressPercent = (TextView) findViewById(R.id.edittask_progressPercent);
		progressBar = (SeekBar) findViewById(R.id.edittask_progressBar);

		progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					public void onStopTrackingTouch(SeekBar seekBar) {}
					public void onStartTrackingTouch(SeekBar seekBar) {}
					public void onProgressChanged(SeekBar seekBar, int p, boolean fromUser) {
						progressPercent.setText(p + " % ");
						progress = p;
					}
		});

		// deadline setting
		deadlineDateButton = (Button) findViewById(R.id.edittask_deadlinedate_button);
		deadlineTimeButton = (Button) findViewById(R.id.edittask_deadlinetime_button);
		
		CharSequence currentYear = DateFormat.format("yyyy",currentDateCalendar);
		CharSequence currentMonth = DateFormat.format("MMM",currentDateCalendar);
		CharSequence currentDate = DateFormat.format("dd", currentDateCalendar);
		CharSequence currentHour = DateFormat.format("kk", currentDateCalendar);
		int currentMinute = Integer.parseInt((String) (DateFormat.format("mm",currentDateCalendar)));
		currentMinute = (currentMinute / 5) * 5;
		
		// convert the minutes in current calendar to 5 minute interval
		currentDateCalendar.set(Calendar.MINUTE, currentMinute);
		deadlineCalendar = currentDateCalendar;
	
		CharSequence currentMinuteConverted = currentMinute <= 5 ? 
				"0"+Integer.toString(currentMinute) : Integer.toString(currentMinute);

		deadlineDateButton.setText(currentMonth + " " + currentDate + " , " + currentYear);
		deadlineTimeButton.setText(currentHour + ":" + currentMinuteConverted);
		
		deadlineDateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				showDialog(DEADLINE_DATE_DIALOG);
			}
		});
		deadlineTimeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				showDialog(DEADLINE_TIME_DIALOG);
			}
		});

		deadline.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
		
		reminder.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (reminder.isChecked())
					e.setVisibility(0);
				if (!reminder.isChecked())
					e.setVisibility(8);
			}
		});
	

		// get params from prevous activity
		try{
			Bundle extras = getIntent().getExtras();
			taskID = extras.getString("taskID");
			JSONArray ja = AndroidCalendar2Activity.getDB().fetchAllNotes("TaskTable", new String[]{"taskID"}, new String[]{taskID});
			JSONObject taskJO = ja.getJSONObject(0);
			title_edit.setText(taskJO.getString("title"));
			location.setText(taskJO.getString("location"));
			progress = Integer.parseInt(taskJO.getString("progress"));
			progressPercent.setText(progress + " % ");
			progressBar.setProgress(progressBar.getMax()*progress/100);
			String tempdD = taskJO.getString("deadlineDate");
			String tempdT = taskJO.getString("deadlineTime");
			if(tempdD.equals("0") && tempdT.equals("0")){
				deadline.setChecked(false);
			}else{
				Date tempD = TheActDate(tempdD,tempdT);
				deadlineDateButton.setText(DateFormat.format("MMM dd , yyyy", tempD));
				deadlineTimeButton.setText(DateFormat.format("hh:mm", tempD));
			}
			if(taskJO.getString("reminder").equals("0"))
				reminder.setChecked(false);
			else
				reminder.setChecked(true);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// cancel/confirm button
		cancelButton = (Button) findViewById(R.id.edittask_cancel_button);
		confirmButton = (Button) findViewById(R.id.edittask_confirm_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});

		confirmButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				String title = title_edit.getText().toString();
				if (title.length() == 0) {
					Toast.makeText(EditTask.this,"You Should enter valid context/title",Toast.LENGTH_SHORT).show();
					return;
				}

				String deadlineTime = deadlineTimeButton.getText().toString();
				String deadlineDate = "";
				SimpleDateFormat df = new SimpleDateFormat("MMM dd , yyyy");
				try {
					Date ddate = df.parse(deadlineDateButton.getText().toString());
					deadlineDate = DateFormat.format("yyyyMMdd", ddate) + "";
				} catch (Exception e) {
					e.printStackTrace();
				}

				String locat = location.getText().toString();
				int reminderID = e.getCheckedRadioButtonId();
				//reminder_setting <- radio group choice
				switch(reminderID){
				case  R.id.addtask_5min: reminder_setting = 5; break;
				case  R.id.addtask_30min: reminder_setting = 30; break;
				case  R.id.addtask_1hour: reminder_setting = 60; break;
				case  R.id.addtask_2hour: reminder_setting = 120; break;
				case  R.id.addtask_6hour: reminder_setting = 360; break;
				case  R.id.addtask_24hour: reminder_setting = 1440; break;
				default: reminder_setting = 0;
				}
				
				if(!deadline.isChecked()){
					deadlineTime = "0";
					deadlineDate = "0";
					reminder_setting = 0;
				}
				if(!reminder.isChecked()){
					reminder_setting = 0;
				}
				
				// update
				String args[] = {title, deadlineDate,deadlineTime,
						locat,progress+"",reminder_setting+""};
				String fields[] = {"title","deadlineDate","deadlineTime",
						"location", "progress", "reminder"};
				String condition = " taskID = '"+taskID+"' ";
				AndroidCalendar2Activity.getDB().updateConditional("TaskTable", condition, fields, args);


				finish();
			}
		});
		
		// delete button
		deleteButton = (Button) findViewById(R.id.edittask_delete);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure to delete this event ?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						AndroidCalendar2Activity.getDB().delete("TaskTable", taskID);
						finish();
					}
				});
	
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
					}
				});
		
		deleteButton.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog alert = builder.create();
				alert.show();
			}
			
		});
		
	}
	
	// convert month or day
	private String getZero(int x) {
		if (String.valueOf(x).length() < 2) {
			return "0" + String.valueOf(x);
		}
		return String.valueOf(x);
	}
	
	// convert to date format
	public Date TheDate(String d) throws ParseException{
		return new SimpleDateFormat("yyyyMMdd").parse(d);
	}
	
	public Date TheActDate(String d, String hm) throws Exception{
		Date sd = new Date();
		String []HourMin = hm.split(":");
		long thetime = TheDate(d).getTime()+Integer.parseInt(HourMin[0])*60*60*1000+Integer.parseInt(HourMin[1])*60*1000;
		sd.setTime(thetime);
		return sd;
	}
	
	public int extractDay(Date d, int field){
		Calendar temp = Calendar.getInstance();
		temp.setTime(d);
		return temp.get(field);
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
		int intervals[] = new int[12];
		int startmin = 0;
		for (int i = 0; i < 11; i++) {
			intervals[i] = startmin;
			startmin += 5;
		}

		int nextMinute = 0;
		for (int i = 11; i >= 0; i--) {
			if (minute > intervals[i]) {
				nextMinute = intervals[i];
				break;
			}
		}

		return nextMinute;
	}

	
}
