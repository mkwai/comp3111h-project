package com.calendar;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;

import com.google.gdata.data.DateTime;
import com.google.gdata.util.ServiceException;
import com.test2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddEvent extends Activity {
	private Button cancelButton;
	private Button confirmButton;
	private Button startingDateButton;
	private Button endingDateButton;
	private Button startingTimeButton;
	private Button endingTimeButton;
	private Button searchLoc;

	// for date set dialog
	public static final int STARTING_DATE_DIALOG = 0;
	public static final int STARTING_TIME_DIALOG = 1;
	public static final int ENDING_DATE_DIALOG = 2;
	public static final int ENDING_TIME_DIALOG = 3;

	static protected Calendar currentDateCalendar = Calendar.getInstance();
	// storing the event starting/ending calendar(date+time)
	private Calendar startingCalendar = Calendar.getInstance();
	private Calendar endingCalendar = Calendar.getInstance();


	private final static String TAG = " LocationDurationActivity";
	
	final static String DURATION_INFOS="duration_infos";
	final static String COUNTRY_CODE = "country_code";
	private final static int CODE =3;
	
	// variable for storing data
	private EditText content;
	private EditText location;
	private EditText contactPerson;
	private CheckBox reminder;
	private CheckBox privateEvent;
	private CheckBox restrictFacebook;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.addevent);

		content = (EditText) findViewById(R.id.addevent_content_edit);
		location = (EditText) findViewById(R.id.addevent_location_edit);
		contactPerson = (EditText) findViewById(R.id.addevent_contact_person_edit);
		reminder = (CheckBox) findViewById(R.id.reminder_checkBox);
		privateEvent = (CheckBox) findViewById(R.id.private_event_checkBox);
		restrictFacebook = (CheckBox) findViewById(R.id.restrict_checkBox);

		cancelButton = (Button) findViewById(R.id.addevent_cancel_button);
		confirmButton = (Button) findViewById(R.id.addevent_confirm_button);
		startingDateButton = (Button) findViewById(R.id.addevent_starting_date_button);
		startingTimeButton = (Button) findViewById(R.id.addevent_starting_time_button);
		endingDateButton = (Button) findViewById(R.id.addevent_ending_date_button);
		endingTimeButton = (Button) findViewById(R.id.addevent_ending_time_button);
		searchLoc = (Button) findViewById(R.id.SearchLoc);

		// setting up default starting/ending date/time

		CharSequence currentYear = DateFormat.format("yyyy", currentDateCalendar);
		CharSequence currentMonth = DateFormat.format("MMM", currentDateCalendar);
		CharSequence currentDate = DateFormat.format("dd", currentDateCalendar);
		CharSequence currentHour = DateFormat.format("kk", currentDateCalendar);
		int currentMinute = Integer.parseInt((String) (DateFormat.format("mm",
				currentDateCalendar)));
		currentMinute = (currentMinute / 5) * 5;

		// convert the minutes in current calendar to 5 minute interval
		currentDateCalendar.set(Calendar.MINUTE, currentMinute);
		startingCalendar = currentDateCalendar;
		endingCalendar = currentDateCalendar;

		CharSequence currentMinuteConverted = currentMinute <= 5 ? "0"
				+ Integer.toString(currentMinute) : Integer
				.toString(currentMinute);

		startingDateButton.setText(currentMonth + " " + currentDate + " , "
				+ currentYear);

		startingTimeButton.setText(currentHour + ":" + currentMinuteConverted);

		endingDateButton.setText(currentMonth + " " + currentDate + " , "
				+ currentYear);

		endingTimeButton.setText(currentHour + ":" + currentMinuteConverted);

		// setting up onClickListener for each button

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
			
				String eventid = AndroidCalendar2Activity.getDB().GiveEventID();
				final String title = content.getText().toString();
				if(title.length()==0){
					Toast.makeText(
							AddEvent.this,"You Should enter valid context/title",Toast.LENGTH_SHORT
							).show();
					return;
				}
				String startTime = startingTimeButton.getText().toString();
				String endTime = endingTimeButton.getText().toString();

				String startDate=""; String startDate2 = "";
				String endDate=""; String endDate2 = "";
				
				SimpleDateFormat df = new SimpleDateFormat("MMM dd , yyyy");
				try{
					Date sdate = df.parse(startingDateButton.getText().toString());
					startDate=DateFormat.format("yyyyMMdd",sdate)+"";
					startDate2=DateFormat.format("yyyy-MM-dd",sdate)+"";
					
					Date edate = df.parse(endingDateButton.getText().toString());
					endDate=DateFormat.format("yyyyMMdd",edate)+"";
					endDate2=DateFormat.format("yyyy-MM-dd",edate)+"";
				
				}catch(Exception e){
					Log.i("error",e.toString());
				}
				
				if(startDate.endsWith(endDate) && startTime.equals(endTime)){
					Toast.makeText(
							AddEvent.this,"Event should not have zero duration",Toast.LENGTH_SHORT
							).show();
					return;
				}
				
				
				String isPrivate = privateEvent.isChecked()?"1":"0";
				String locat = location.getText().toString();
				String remind = reminder.isChecked()?"1":"0";
				String args[] = {eventid,title,startDate,endDate,startTime,endTime,isPrivate,locat,remind};	
			
				Log.i("done",startDate+" "+endDate);
				
				AndroidCalendar2Activity.getDB().insert("TimeTable", args);
				
				//"2012-03-01T22:40:00"
				final String sdt= startDate2+ "T"+ startTime.substring(0, 2) + ":" + startTime.substring(3, 5)+ ":00";
				final String edt= endDate2+ "T"+ endTime.substring(0, 2) + ":" + endTime.substring(3, 5)+ ":00" ;
				
				if (AndroidCalendar2Activity.getGS().isGoogleConnected()){

					new Thread(new Runnable() {
						public void run() {
							try {
								AndroidCalendar2Activity.getGS().insert(title,DateTime.parseDateTime(sdt), 
										DateTime.parseDateTime(edt));
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ServiceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
						}
							
					}).start();
				}	
				
				finish();
			
			}
		});

		startingDateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(STARTING_DATE_DIALOG);
			}
		});
		startingTimeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(STARTING_TIME_DIALOG);
			}
		});

		endingDateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog(ENDING_DATE_DIALOG);
			}
		});

		endingTimeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showDialog(ENDING_TIME_DIALOG);
			}
		});
		
		searchLoc.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				if(location.getText().length()==0){
					Toast.makeText(AddEvent.this, "No Place to search", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String place = location.getText().toString();
				

				String temp = place.replace('\n', '+');
				String args = "http://maps.googleapis.com/maps/api/geocode/json?address=" 
						+ temp.replace(' ', '+') + "&sensor=false";
				
				Intent intent = new Intent(AddEvent.this, CityListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(AddEvent.COUNTRY_CODE, args);
				
				intent.putExtras(bundle);
			    startActivityForResult(intent, CODE);
			}			
		});
		
	}

	// call when create dialog
	protected Dialog onCreateDialog(int id) {

		// listener for setting date in starting date dialog
		OnDateSetListener startingDateSetListener = new OnDateSetListener() {

			// use when "set" press
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// store the date set by the dialog to startingCalendar variable
				startingCalendar.set(year, monthOfYear, dayOfMonth);
				// renew endingCalendar (ending > starting)
				if (startingCalendar.after(endingCalendar)) {
					endingCalendar.set(year, monthOfYear, dayOfMonth);
					endingDateButton.setText(DateFormat.format("MMM",
							startingCalendar)
							+ " "
							+ DateFormat.format("dd", startingCalendar)
							+ " , "
							+ DateFormat.format("yyyy", startingCalendar));
				}
				startingDateButton.setText(DateFormat.format("MMM",
						startingCalendar)
						+ " "
						+ DateFormat.format("dd", startingCalendar)
						+ " , "
						+ DateFormat.format("yyyy", startingCalendar));

			}
		};

		// listener for setting date in ending date dialog
		OnDateSetListener endingDateSetListener = new OnDateSetListener() {

			// use when "set" press
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// compareCalendar is temp, if valid input then compare ->
				// ending
				Calendar compareCalendar = Calendar.getInstance();
				compareCalendar.set(year, monthOfYear, dayOfMonth);
				compareCalendar.set(Calendar.HOUR_OF_DAY,
						endingCalendar.get(Calendar.HOUR_OF_DAY));
				compareCalendar.set(Calendar.MINUTE,
						endingCalendar.get(Calendar.MINUTE));

				// store the date set by the dialog to endingCalendar variable
				// renew endingCalendar (ending > starting)
				if (compareCalendar.after(startingCalendar)) {

					endingCalendar.set(year, monthOfYear, dayOfMonth);
					endingDateButton.setText(DateFormat.format("MMM",
							startingCalendar)
							+ " "
							+ DateFormat.format("dd", startingCalendar)
							+ " , "
							+ DateFormat.format("yyyy", startingCalendar));
				}

			}
		};

		// listener for setting date in starting time dialog
		OnTimeSetListener startingTimeSetListener = new OnTimeSetListener() {

			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				startingCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				startingCalendar.set(Calendar.MINUTE, minute);

				if (startingCalendar.after(endingCalendar)) {
					endingCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
					endingCalendar.set(Calendar.MINUTE, minute);
					endingTimeButton.setText(DateFormat.format("kk",
							endingCalendar)
							+ ":"
							+ DateFormat.format("mm", endingCalendar));
				}

				startingTimeButton.setText(DateFormat.format("kk",
						startingCalendar)
						+ ":"
						+ DateFormat.format("mm", startingCalendar));
			}
		};

		// listener for setting date in ending time dialog
		OnTimeSetListener endingTimeSetListener = new OnTimeSetListener() {

			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				Calendar compareCalendar = Calendar.getInstance();

				// use to get dd/mm/yy
				compareCalendar.setTimeInMillis(endingCalendar
						.getTimeInMillis());
				compareCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
				compareCalendar.set(Calendar.MINUTE, minute);

				if (compareCalendar.after(startingCalendar)) {
					endingCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
					endingCalendar.set(Calendar.MINUTE, minute);
					endingTimeButton.setText(DateFormat.format("kk",
							endingCalendar)
							+ ":"
							+ DateFormat.format("mm", endingCalendar));
				}

			}
		};

		// select which dialog to show by verifying which button was press
		switch (id) {
		case STARTING_DATE_DIALOG:
			return new DatePickerDialog(this, startingDateSetListener,
					startingCalendar.get(Calendar.YEAR),
					startingCalendar.get(Calendar.MONTH),
					startingCalendar.get(Calendar.DAY_OF_MONTH));
		case ENDING_DATE_DIALOG:
			return new DatePickerDialog(this, endingDateSetListener,
					endingCalendar.get(Calendar.YEAR),
					endingCalendar.get(Calendar.MONTH),
					endingCalendar.get(Calendar.DAY_OF_MONTH));
		case STARTING_TIME_DIALOG:
			return new MyTimePickerDialog(this, startingTimeSetListener,
					startingCalendar.get(Calendar.HOUR_OF_DAY),
					startingCalendar.get(Calendar.MINUTE), true);
		case ENDING_TIME_DIALOG:
			return new MyTimePickerDialog(this, endingTimeSetListener,
					endingCalendar.get(Calendar.HOUR_OF_DAY),
					endingCalendar.get(Calendar.MINUTE), true);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Activity.RESULT_CANCELED){
			
			return;
		}
		
		if (requestCode == CODE && resultCode == Activity.RESULT_OK) {
			
			String placename = data.getExtras().getString("placename");
			String lat = data.getExtras().getString("placelat");
			String lng = data.getExtras().getString("placelng");
			
			location.setText(placename);
			
			/*
			new AlertDialog.Builder(AddEvent.this).
			setTitle(title).setMessage(message).
			  setPositiveButton( "OK" ,new DialogInterface.OnClickListener() {  
				public void onClick(DialogInterface dialoginterface, int i){	}          
			}).show();    
			*/
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

	// the following is get function for getting data
	public Calendar getStartingCalendar() {
		return startingCalendar;
	}

	public Calendar getEndingCalendar() {
		return endingCalendar;
	}

	public Editable getContent() {
		return content.getText();
	}

	public Editable getLocation() {
		return location.getText();
	}

	public Editable getContactPerson() {
		return contactPerson.getText();
	}

	public boolean getReminder() {
		return reminder.isChecked();
	}

	public boolean getPrivateEvent() {
		return privateEvent.isChecked();
	}

	public boolean getRestrictFacebook() {
		return restrictFacebook.isChecked();
	}
}
