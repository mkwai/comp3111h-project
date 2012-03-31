package com.googlesync;

import java.util.Calendar;

import com.calendar.AndroidCalendar2Activity;
import com.test2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

public class GoogleSyncActivity extends Activity {

	private int pastdayID;
	private int past;
	private int futuredayID;
	private int future;
	private String year;
	private String month;
	private String date;

	private String username = "";
	private String password = "";
	static protected Calendar currentDateCalendar = Calendar.getInstance();

	private Button sync;
	private Button disconnect;
	private EditText ET_username;
	private EditText ET_password;
	private RadioGroup rgpast;
	private RadioGroup rgfuture;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.googlesync);
		findViews();
		setListeners();

		if (AndroidCalendar2Activity.getGS() != null) {
			ET_username.setText(AndroidCalendar2Activity.getGS().getUserName());
			ET_password.setText(AndroidCalendar2Activity.getGS()
					.getUserPassword());
		}
		sync.setOnClickListener(sync_listener);
		disconnect.setOnClickListener(disconnect_listener);

	}

	private void findViews() {
		ET_username = (EditText) findViewById(R.id.gs_username);
		ET_password = (EditText) findViewById(R.id.gs_password);
		sync = (Button) findViewById(R.id.gs_sync);
		disconnect = (Button) findViewById(R.id.gs_disconnect);
		rgpast = (RadioGroup) findViewById(R.id.gs_rgpast);
		rgfuture = (RadioGroup) findViewById(R.id.gs_rgfuture);

	}

	// Listen for button clicks
	private void setListeners() {
		sync.setOnClickListener(sync_listener);
		disconnect.setOnClickListener(disconnect_listener);
	}

	private OnClickListener disconnect_listener = new OnClickListener() {
		public void onClick(View v) {
			ET_password.setText("");
			AndroidCalendar2Activity.clearGS();
			ShowMsgDialog("System", "Disconnected.");
		}
	};

	private OnClickListener sync_listener = new OnClickListener() {
		public void onClick(View v) {
			username = ET_username.getText().toString();
			password = ET_password.getText().toString();

			year = DateFormat.format("yyyy", currentDateCalendar).toString();
			month = DateFormat.format("MMM", currentDateCalendar).toString();
			date = DateFormat.format("dd", currentDateCalendar).toString();

			if (username.length() == 0) {
				Context context = getApplicationContext();
				CharSequence text = "Please fill in user name.";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			} else if (password.length() == 0) {
				Context context = getApplicationContext();
				CharSequence text = "Please fill in password.";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			} else {
				new Thread(new Runnable() {
					public void run() {

						AndroidCalendar2Activity.getGS().setUserInfo(username,
								password);
						pastdayID = rgpast.getCheckedRadioButtonId();

						if (pastdayID == R.id.gs_past7)
							past = 7;
						if (pastdayID == R.id.gs_past30)
							past = 30;
						if (pastdayID == R.id.gs_past60)
							past = 60;
						if (pastdayID == R.id.gs_past90)
							past = 90;
						if (pastdayID == R.id.gs_past180)
							past = 180;
						if (pastdayID == R.id.gs_past365)
							past = 365;

						futuredayID = rgfuture.getCheckedRadioButtonId();

						if (futuredayID == R.id.gs_future7)
							future = 7;
						if (futuredayID == R.id.gs_future30)
							future = 30;
						if (futuredayID == R.id.gs_future60)
							future = 60;
						if (futuredayID == R.id.gs_future90)
							future = 90;
						if (futuredayID == R.id.gs_future180)
							future = 180;
						if (futuredayID == R.id.gs_future365)
							future = 365;

						if (AndroidCalendar2Activity.getGS().GoogleLogin() == false) {
							AndroidCalendar2Activity.getGS().isGoogleConnected(
									false);
							Looper.prepare();
							ShowMsgDialog("System",
									"User name and password not match.");
							Looper.loop();
						} else {
							AndroidCalendar2Activity.getGS().isGoogleConnected(
									true);
							Looper.prepare();
							ShowMsgDialog("System", "Connected Successfully.");
							AndroidCalendar2Activity.getGS().getRangeEvents2(
									(year + "-" + month + "-" + date), past,
									future);
							Looper.loop();
						}
					}
				}).start();
			}
		}
	};

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
