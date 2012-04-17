package com.googlesync;

import java.io.IOException;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.calendar.AndroidCalendar2Activity;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.util.ServiceException;
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
		// set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.googlesync);
		findViews();
		setListeners();

		if (AndroidCalendar2Activity.getGS() != null) {
			ET_username.setText(AndroidCalendar2Activity.getGS().getUserName());
			ET_password.setText(AndroidCalendar2Activity.getGS().getUserPassword());
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
			if (AndroidCalendar2Activity.getGS() != null) {
				AndroidCalendar2Activity.clearGS();
			}
			ShowMsgDialog("System", "Disconnected.");
		}
	};

	private OnClickListener sync_listener = new OnClickListener() {
		public void onClick(View v) {
			username = ET_username.getText().toString();
			password = ET_password.getText().toString();

			
			year = DateFormat.format("yyyy", currentDateCalendar).toString();
			month = DateFormat.format("MM", currentDateCalendar).toString();
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
				if (AndroidCalendar2Activity.getGS() == null) {
					Context context = getApplicationContext();
					CharSequence text = "Connection Unavailable";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				} else {
					new Thread(new Runnable() {
						public void run() {
							username= "klhoab@gmail.com";
							password= "977026a1";
							
							AndroidCalendar2Activity.getGS().setUserInfo(username, password);
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
								AndroidCalendar2Activity.getGS().isGoogleConnected(false);
								Looper.prepare();
								ShowMsgDialog("System","User name and password not match.");
								Looper.loop();
							} else {
								AndroidCalendar2Activity.getGS().isGoogleConnected(true);
								
								Looper.prepare();
								ShowMsgDialog("System","Connected Successfully.");
								
								JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional("GoogleTable","");
								System.out.println("# of (unsync)=" + ja.length());
								
								for (int i = 0; i < ja.length(); i++) {
									JSONObject jo;
									try {
										jo = ja.getJSONObject(i);
										
										String eventid= jo.getString("eventID");
										System.out.println("Eventid (unsync)=" + eventid);
										
										JSONArray temp= AndroidCalendar2Activity.getDB().fetchAllNotes(
											"TimeTable", new String[]{"eventid"}, new String[] {eventid} ) ; 
									
										for (int j=0; j<temp.length(); j++){
											JSONObject tempo = temp.getJSONObject(j);
											String title= tempo.getString("title");
											String startDate= tempo.getString("startDate");
											String startTime= tempo.getString("startTime");
											String endDate= tempo.getString("endDate");
											String endTime= tempo.getString("endTime");
											
											System.out.println("Title(unsync)=" + title);
											
											startDate= startDate.substring(0,4)+ "-"
													+ startDate.substring(4,6)+ "-" 
													+startDate.substring(6,8);
											
											endDate= endDate.substring(0,4)+ "-"
													+ endDate.substring(4,6)+ "-" 
													+endDate.substring(6,8);
										
											final String sdt = startDate + "T" + startTime.substring(0, 2)
													+ ":" + startTime.substring(3, 5) + ":00";
											final String edt = endDate + "T" + endTime.substring(0, 2)
													+ ":" + endTime.substring(3, 5) + ":00";
											
											//sdt(unsync)=20120418T01:25:00
											//edt(unsync)=20120418T02:25:00

											// DateTime Format = "2012-03-01T22:40:00" 
											System.out.println("sdt(unsync)=" + sdt);
											System.out.println("edt(unsync)=" + edt);
											
											String googleEventID= AndroidCalendar2Activity.getGS().insert(
													title,
													DateTime.parseDateTime(sdt),
													DateTime.parseDateTime(edt)
												);
											if (googleEventID!= ""){
												String fields[] = {"eventID"};
												String args[] = {googleEventID};
												String condition=" eventID = '"+eventid+"' ";
												AndroidCalendar2Activity.getDB().updateConditional("TimeTable", condition, fields, args);
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								
								AndroidCalendar2Activity.getGS().getRangeEvents2(
												(year + "-" + month + "-" + date), past, future);
								
								//AndroidCalendar2Activity.getGS().temp2();
								
								//temp= AndroidCalendar2Activity.getDB().getTable("TimeTable");
								
								Looper.loop();
							}
						}
					}).start();
				}
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
