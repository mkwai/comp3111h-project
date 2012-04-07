
package com.exina.android.calendar;

import java.util.Calendar;

import com.calendar.*;
import com.exina.android.calendar.CalendarView.GrayCell;
import com.test2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarActivity extends Activity implements
		CalendarView.OnCellTouchListener {
	public static final String MIME_TYPE = "vnd.android.cursor.dir/vnd.exina.android.calendar.date";
	CalendarView mView = null;
	TextView mHit;
	Handler mHandler = new Handler();

	LinearLayout calendarL;
	Button bGoTo, bAddEvent, bSynchronous, bDaily, bMonthlyb, bTodoList,
			bGoogle, bPrevious, bNext;
	TextView monthlyview_month;
	public static final int SET_DATE_DIALOG = 0;

	Drawable mDecoration = null;
	int newYear = Calendar.getInstance().get(Calendar.YEAR);
	int newMonth = Calendar.getInstance().get(Calendar.MONTH);
	int newDay = Calendar.getInstance().get(Calendar.DATE);

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.monthlyview);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

		mView = (CalendarView) findViewById(R.id.calendar);
		mView.setOnCellTouchListener(this);

		// ///
		calendarL = (LinearLayout) findViewById(R.id.linear_calendar);
		// button AddEvent
		bAddEvent = (Button) findViewById(R.id.bAddEvent);
		bAddEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// setContentView(R.layout.addevent);
				startActivity(new Intent("com.calendar.ADDEVENT"));
			}

		});

		// button synchronous
		bSynchronous = (Button) findViewById(R.id.bSynchronous);
		bSynchronous.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.calendar.SYNCHRONOUS"));
			}
		});

		// button GoTo
		bGoTo = (Button) findViewById(R.id.bGoTo);
		bGoTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(SET_DATE_DIALOG);

			}
		});

		// button Daily
		bDaily = (Button) findViewById(R.id.bDaily);
		bDaily.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.calendar.DAILYVIEW"));
			}
		});

		// button TodoList
		bTodoList = (Button) findViewById(R.id.bTodoList);
		bTodoList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.calendar.TODOLIST"));
			}
		});

		// button Google
		bGoogle = (Button) findViewById(R.id.bGoogle);
		bGoogle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent("com.calendar.GOOGLESYNC"));
			}
		});
		// ///
		// button Previous
		bPrevious = (Button) findViewById(R.id.bPrevious);
		bPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mView.previousMonth();
				monthlyview_month.setText(newDay
						+ " "
						+ DateUtils.getMonthString(mView.getMonth(),
								DateUtils.LENGTH_LONG) + " " + mView.getYear());
			}
		});
		// button Next
		bNext = (Button) findViewById(R.id.bNext);
		bNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mView.nextMonth();
				monthlyview_month.setText(newDay
						+ " "
						+ DateUtils.getMonthString(mView.getMonth(),
								DateUtils.LENGTH_LONG) + " " + mView.getYear());
			}
		});
		monthlyview_month = (TextView) findViewById(R.id.monthlyview_month);
		monthlyview_month.setText(newDay + " "
				+ DateUtils.getMonthString(newMonth, DateUtils.LENGTH_LONG)
				+ " " + newYear);
		mDecoration = this.getResources().getDrawable(
				R.drawable.typeb_calendar_today);
	}

	// Calendar View
	public void onTouch(Cell cell) {
		Intent intent = getIntent();
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_PICK)
				|| action.equals(Intent.ACTION_GET_CONTENT)) {
			newYear = mView.getYear();
			newMonth = mView.getMonth();
			newDay = cell.getDayOfMonth();

			// FIX issue 6: make some correction on month and year
			if (cell instanceof CalendarView.GrayCell) {
				// oops, not pick current month...
				if (newDay < 15) {
					// pick one beginning day? then a next month day
					if (newMonth == 11) {
						newMonth = 0;
						newYear++;
					} else {
						newMonth++;
					}

				} else {
					// otherwise, previous month
					if (newMonth == 0) {
						newMonth = 11;
						newYear--;
					} else {
						newMonth--;
					}
				}
			}

			Intent ret = new Intent();
			ret.putExtra("year", newYear);
			ret.putExtra("month", newMonth);
			ret.putExtra("day", newDay);
			this.setResult(RESULT_OK, ret);
			finish();
			return;
		}
		int day = cell.getDayOfMonth();

		if (cell instanceof GrayCell) {
			if (day <= 14)
				mView.nextMonth();
			else
				mView.previousMonth();
		}
		/*
		 * if (mView.firstDay(day)) mView.previousMonth(); else if
		 * (mView.lastDay(day)) mView.nextMonth(); else return;
		 */
		monthlyview_month.setText(cell.getDayOfMonth()
				+ "  "
				+ DateUtils.getMonthString(mView.getMonth(),
						DateUtils.LENGTH_LONG) + " " + mView.getYear());
		mDecoration.setBounds(cell.getBound());

		DailyView.setDailyYear(mView.getYear());
		DailyView.setDailyMonth(mView.getMonth());
		DailyView.setDailyDayOfMonth(cell.getDayOfMonth());

		AddEvent.currentDateCalendar.set(Calendar.YEAR, mView.getYear());
		AddEvent.currentDateCalendar.set(Calendar.MONTH, mView.getMonth());
		AddEvent.currentDateCalendar.set(Calendar.DAY_OF_MONTH,
				cell.getDayOfMonth());
		/*
		 * mHandler.post(new Runnable() { public void run() { Toast.makeText(
		 * CalendarActivity.this, DateUtils.getMonthString(mView.getMonth(),
		 * DateUtils.LENGTH_LONG) + " " + mView.getYear(),
		 * Toast.LENGTH_SHORT).show(); } });
		 */
	}

	// call when create dialog, for GoTo use
	protected Dialog onCreateDialog(int id) {

		// listener for setting date in starting date dialog
		OnDateSetListener goToDateSetListener = new OnDateSetListener() {

			// use when "set" press
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				if (year < 1950 || year > 2050) {
					ShowMsgDialog("ALERT", "The selected date is out of range!");
				} else {

					Calendar date = Calendar.getInstance();
					date.set(year, monthOfYear, dayOfMonth);
					mView.goTo(dayOfMonth, monthOfYear, year);

					newYear = year;
					newMonth = monthOfYear;
					newDay = dayOfMonth;

					DailyView.setDailyYear(newYear);
					DailyView.setDailyMonth(newMonth);
					DailyView.setDailyDayOfMonth(newDay);

					AddEvent.currentDateCalendar.set(Calendar.YEAR, newYear);
					AddEvent.currentDateCalendar.set(Calendar.MONTH, newMonth);
					AddEvent.currentDateCalendar.set(Calendar.DAY_OF_MONTH,
							newDay);

					monthlyview_month.setText(newDay
							+ " "
							+ DateUtils.getMonthString(newMonth,
									DateUtils.LENGTH_LONG) + " " + newYear);

				}
			}
		};

		return new DatePickerDialog(this, goToDateSetListener, Calendar
				.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(
				Calendar.MONTH), Calendar.getInstance().get(
				Calendar.DAY_OF_MONTH));
	}

	// FOR menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_addEvent:
			startActivity(new Intent("com.calendar.ADDEVENT"));
			return true;
		case R.id.menu_synchronous:
			startActivity(new Intent("com.calendar.SYNCHRONOUS"));
			return true;
		case R.id.menu_import:
			// startActivity(new Intent("com.calendar.IMPORT"));
			return true;
		case R.id.menu_export:
			// startActivity(new Intent("com.calendar.EXPORT"));
			return true;
		}
		return false;

	}

	// for exception, selected date
	private void ShowMsgDialog(String title, String msg) {
		AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(this);
		MyAlertDialog.setTitle(title);
		MyAlertDialog.setMessage(msg);
		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// no action
			}
		};
		MyAlertDialog.setNeutralButton("Okay", OkClick);
		MyAlertDialog.show();
	}

}