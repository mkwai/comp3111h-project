package com.calendar;


import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import com.Alarm.Alarms;
import com.Alarm.LocationBasedAlarm;
import com.google.gdata.data.DateTime;
import com.location.CityBean;

import com.test2.R;

import android.app.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
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
	private Button searchContact;
	private CityBean CB = new CityBean();
	// for date set dialog
	public static final int STARTING_DATE_DIALOG = 0;
	public static final int STARTING_TIME_DIALOG = 1;
	public static final int ENDING_DATE_DIALOG = 2;
	public static final int ENDING_TIME_DIALOG = 3;

	public static Calendar currentDateCalendar = Calendar.getInstance();
	// storing the event starting/ending calendar(date+time)
	private Calendar startingCalendar = Calendar.getInstance();
	private Calendar endingCalendar = Calendar.getInstance();

	private final static String TAG = " LocationDurationActivity";

	final static String DURATION_INFOS = "duration_infos";
	final static String COUNTRY_CODE = "country_code";
	private final static int CODE = 3;
	private final static int CONTACT_CODE = 4;
	
	
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
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.addevent);

		content = (EditText) findViewById(R.id.addevent_content_edit);
		location = (EditText) findViewById(R.id.addevent_location_edit);
		contactPerson = (EditText) findViewById(R.id.addevent_contact_person_edit);
		reminder = (CheckBox) findViewById(R.id.addevent_reminder_checkBox);
		privateEvent = (CheckBox) findViewById(R.id.addevent_private_event_checkBox);
		restrictFacebook = (CheckBox) findViewById(R.id.addevent_restrict_checkBox);

		cancelButton = (Button) findViewById(R.id.addevent_cancel_button);
		confirmButton = (Button) findViewById(R.id.addevent_confirm_button);
		startingDateButton = (Button) findViewById(R.id.addevent_starting_date_button);
		startingTimeButton = (Button) findViewById(R.id.addevent_starting_time_button);
		endingDateButton = (Button) findViewById(R.id.addevent_ending_date_button);
		endingTimeButton = (Button) findViewById(R.id.addevent_ending_time_button);
		searchLoc = (Button) findViewById(R.id.SearchLoc);
		searchContact= (Button) findViewById(R.id.addevent_search_contact);
		// setting up default starting/ending date/time

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
		startingCalendar = (Calendar) currentDateCalendar.clone();
		endingCalendar = (Calendar) currentDateCalendar.clone();
		endingCalendar.set(Calendar.HOUR_OF_DAY,
				currentDateCalendar.get(Calendar.HOUR_OF_DAY) + 1);
		CharSequence currentMinuteConverted = currentMinute <= 5 ? "0"
				+ Integer.toString(currentMinute) : Integer
				.toString(currentMinute);

		startingDateButton.setText(currentMonth + " " + currentDate + " , "
				+ currentYear);

		startingTimeButton.setText(currentHour + ":" + currentMinuteConverted);

		endingDateButton.setText(currentMonth + " " + currentDate + " , "
				+ currentYear);

		endingTimeButton.setText(getZero(endingCalendar
				.get(Calendar.HOUR_OF_DAY)) + ":" + currentMinuteConverted);

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

				final String eventid = AndroidCalendar2Activity.getDB().GiveEventID();
				final String title = content.getText().toString();
				if (title.length() == 0) {
					Toast.makeText(AddEvent.this,
							"You should enter a valid title",
							Toast.LENGTH_SHORT).show();
					return;
				}
				// length of title must < 30
				if (title.length() > 30) {
					Toast.makeText(AddEvent.this,
							"Maximum 30 characters in title",
							Toast.LENGTH_SHORT).show();
					return;
				}
				String startTime = startingTimeButton.getText().toString();
				String endTime = endingTimeButton.getText().toString();

				String startDate = DateFormat.format("yyyyMMdd",
						startingCalendar) + "";
				String endDate = DateFormat.format("yyyyMMdd", endingCalendar)
						+ "";
				String startDate2 = DateFormat.format("yyyy-MM-dd",
						startingCalendar) + "";
				String endDate2 = DateFormat.format("yyyy-MM-dd",
						endingCalendar) + "";

				// String startDate = ""; String startDate2 = ""; String endDate
				// = ""; String endDate2 = "";

				SimpleDateFormat df = new SimpleDateFormat("MMM dd , yyyy");
				try {
					Date sdate = df.parse(startingDateButton.getText()
							.toString());
					startDate = DateFormat.format("yyyyMMdd", sdate) + "";
					startDate2 = DateFormat.format("yyyy-MM-dd", sdate) + "";

					Date edate = df
							.parse(endingDateButton.getText().toString());
					endDate = DateFormat.format("yyyyMMdd", edate) + "";
					endDate2 = DateFormat.format("yyyy-MM-dd", edate) + "";

				} catch (Exception e) {
					Log.i("error", e.toString());
				}

				Log.i("not yet done", startDate + " " + startTime + " "
						+ endDate + " " + endTime);

				// check if the duration is 0
				if (startingCalendar.equals(endingCalendar)) {
					Toast.makeText(AddEvent.this,
							"A event should not has zero duration",
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				String isPrivate = privateEvent.isChecked() ? "1" : "0";
				
				String locat = location.getText().toString();
				//***should be not necessary to search?***
				if (!(locat.length()==0 || locat.equals(CB.getCityName()))) {
					Toast.makeText(AddEvent.this,
							"Search valid address before add!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				String remind = reminder.isChecked() ? "1" : "0";
				
				
				
				int dayHour = Integer.parseInt(startTime.substring(0, 2));
				int dayMin = Integer.parseInt(startTime.substring(3, 5));
				startingCalendar.set(Calendar.HOUR_OF_DAY, dayHour);
				startingCalendar.set(Calendar.MINUTE, dayMin);
				final long milliSecond = startingCalendar.getTimeInMillis();
				
				String contact = contactPerson.getText().toString();
				
				String args[] = { eventid, title, startDate, endDate,
						startTime, endTime, isPrivate, locat, remind, milliSecond+"" ,contact};

				Log.i("done", startDate + " " + startTime + " " + endDate + " "
						+ endTime);
				
				/*!!!!!!!!!!!!! Alert User !!!!!!!!!!!!!!!*/
				if(remind.equals("1") && locat.length()<=0){
					final String ID = eventid;
					new Thread(new Runnable() {
						public void run() {
							
								Calendar temp = (Calendar) endingCalendar.clone();
								Log.i("temp!!", temp.getTimeInMillis()+"");
								Alarms.addAlarm(AddEvent.this, ID, title, milliSecond, true);
													
						}
					}).start();
				}
				AndroidCalendar2Activity.getDB().insert("TimeTable", args);
				
				if(locat.length()>0){
					LocationBasedAlarm.addList(CB);
				}
				//For google sync. eg, "2012-03-01T22:40:00" 
				final String sdt = startDate2 + "T" + startTime.substring(0, 2)
						+ ":" + startTime.substring(3, 5) + ":00";
				final String edt = endDate2 + "T" + endTime.substring(0, 2)
						+ ":" + endTime.substring(3, 5) + ":00";

				// if the connection with google calendar is started 
				if (AndroidCalendar2Activity.getGS() != null
						&& AndroidCalendar2Activity.getGS().isGoogleConnected()) {
					
					new Thread(new Runnable() {
						public void run() {
							// insert to google calendar and get googleEventID
							String googleEventID= AndroidCalendar2Activity.getGS().insert(
									title,
									DateTime.parseDateTime(sdt),
									DateTime.parseDateTime(edt)
								);
							// if successful
							System.out.println("OLD ID= "+ eventid);
							if (googleEventID!= ""){
								String fields[] = {"eventID"};
								String args[] = {googleEventID};
								String condition=" eventID = '"+eventid+"' ";
								AndroidCalendar2Activity.getDB().updateConditional("TimeTable", condition, fields, args);
							}
							System.out.println("NEW ID= "+ googleEventID);
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

		searchLoc.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (location.getText().length() == 0) {
					Toast.makeText(AddEvent.this, "No Place to search",
							Toast.LENGTH_SHORT).show();
					return;
				}

				String place = location.getText().toString();

				String temp = place.replace('\n', '+');
				String args = "http://maps.googleapis.com/maps/api/geocode/json?address="
						+ temp.replace(' ', '+') + "&sensor=false";

				Intent intent = new Intent(AddEvent.this,
						CityListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(AddEvent.COUNTRY_CODE, args);

				intent.putExtras(bundle);
				startActivityForResult(intent, CODE);
			}
		});

		searchContact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			
				Intent intent = new Intent(Intent.ACTION_PICK,
						People.CONTENT_URI);

				startActivityForResult(intent,CONTACT_CODE);
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
				if (startingCalendar.getTimeInMillis() > endingCalendar
						.getTimeInMillis()) {
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
				if (compareCalendar.getTimeInMillis() >= startingCalendar
						.getTimeInMillis()) {

					endingCalendar.set(year, monthOfYear, dayOfMonth);
					endingDateButton.setText(DateFormat.format("MMM",
							endingCalendar)
							+ " "
							+ DateFormat.format("dd", endingCalendar)
							+ " , "
							+ DateFormat.format("yyyy", endingCalendar));
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
		private int currentHour = 0;

		public MyTimePickerDialog(Context context, OnTimeSetListener callBack,
				int hourOfDay, int minute, boolean is24HourView) {
			super(context, callBack, hourOfDay, minute, is24HourView);
			// TODO Auto-generated constructor stub
			currentMinute = restoreMinute(minute);
			currentHour = hourOfDay;
		}

		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			updateDisplay(view, hourOfDay, minute);
		}

		private void updateDisplay(TimePicker timePicker, int hourOfDay,
				int minute) {

			// change the next number to +/- 5 minute
			if (currentMinute == 0 && minute == 59) {
				currentMinute = 55;
			} else
				currentMinute = ((currentMinute - minute) > 0) ? currentMinute - 5
						: ((currentMinute - minute) < 0 ? currentMinute + 5
								: currentMinute);

			timePicker
					.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
						public void onTimeChanged(TimePicker view,
								int hourOfDay, int minute) {
						}
					});

			// set minute
			if(currentMinute<0 || currentMinute >=60 )
				currentMinute = 0;
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

		if (resultCode == Activity.RESULT_CANCELED) {

			return;
		}

		if (requestCode == CODE && resultCode == Activity.RESULT_OK) {

			String placename = data.getExtras().getString("placename");
			double lat = data.getExtras().getDouble("placelat");
			double lng = data.getExtras().getDouble("placelng");
			
			CB.setCityName(placename);
			CB.setLat(lat);
			CB.setLon(lng);
			
			location.setText(placename);

			/*
			 * new AlertDialog.Builder(AddEvent.this).
			 * setTitle(title).setMessage(message). setPositiveButton( "OK" ,new
			 * DialogInterface.OnClickListener() { public void
			 * onClick(DialogInterface dialoginterface, int i){ } }).show();
			 */
		}
		
		if (requestCode == CONTACT_CODE && resultCode == Activity.RESULT_OK) {
			Uri contactData = data.getData();
			Cursor c = managedQuery(contactData,null,null,null,null);
			if(c.moveToFirst()){
				String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				
				contactPerson.setText(name);
			}
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
