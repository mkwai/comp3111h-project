package com.calendar;

import java.util.Calendar;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddTask extends Activity {
	private Button deadlineDateButton;
	private Button deadlineTimeButton;
	
	
	private Calendar currentDateCalendar = Calendar.getInstance();
	private Calendar deadlineCalendar = Calendar.getInstance();

	public static final int DEADLINE_DATE_DIALOG = 0;
	public static final int DEADLINE_TIME_DIALOG = 1;

	private SeekBar progressBar;
	private TextView progressPercent;
	private int progress;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		
		//progress setting
		setContentView(R.layout.addtask);
		progressPercent = (TextView)findViewById(R.id.addtask_progressPercent);
		progressBar = (SeekBar) findViewById(R.id.addtask_progressBar);
		
		progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
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
		
		
		//deadline setting
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
