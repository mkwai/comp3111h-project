package com.calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import com.Alarm.AlarmService;
import com.commTimeCal.TimeCal;
import com.test2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DailyView extends Activity {

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();	
	}

	// initialize the day to today
	public static int dailyYear = Calendar.getInstance().get(Calendar.YEAR);
	public static int dailyMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
	public static int dailyDayOfMonth = Calendar.getInstance().get(
			Calendar.DATE);
	TextView dailyview_today;
	TextView twelve_am;
	RelativeLayout relativeLayout;
	Button bAddEvent, bPrevious, bNext, bExportjpg, bRefresh;
	LinearLayout linearLayout;

	int count = 1;
	Calendar calendar = Calendar.getInstance();
	int hView = 1;


	public static void setDaily(int year,int month, int dayOfMonth) {
		dailyYear = year;
		dailyMonth = month + 1;
		dailyDayOfMonth = dayOfMonth;
		
	}

	public static void setDaily(Calendar c) {
		dailyYear = c.get(Calendar.YEAR);
		dailyMonth = c.get(Calendar.MONTH) +1;
		dailyDayOfMonth = c.get(Calendar.DATE);
		
	}
	public static void setDailyYear(int year) {
		dailyYear = year;
	}

	public static void setDailyMonth(int month) {
		dailyMonth = month + 1;
	}

	public static void setDailyDayOfMonth(int dayOfMonth) {
		dailyDayOfMonth = dayOfMonth;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set to full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.dailyview);
		relativeLayout = (RelativeLayout) findViewById(R.id.daily_relativelayout);
		linearLayout = (LinearLayout) findViewById(R.id.daily_linearlayout);
		calendar.set(dailyYear, dailyMonth - 1, dailyDayOfMonth);

		// button AddEvent
		bAddEvent = (Button) findViewById(R.id.dailyview_bAddEvent);
		bAddEvent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// setContentView(R.layout.addevent);
				finish();
				startActivity(new Intent("com.calendar.ADDEVENT"));

			}

		});

		// button export jpg
		bExportjpg = (Button) findViewById(R.id.exportjpg);
		bExportjpg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				LinearLayout layout = (LinearLayout) findViewById(R.id.daily_linearlayout);
				Bitmap screenBitmap = Bitmap.createBitmap(
						layout.getMeasuredWidth(), layout.getMeasuredHeight(),
						Bitmap.Config.ARGB_8888);

				Canvas canvas = new Canvas(screenBitmap);
				// canvas.scale(1, 2);
				layout.draw(canvas);

				// layout.

				FileOutputStream fos;
				try {
					File exportDirectory = new File(
							"/sdcard/dailyassistant_export/");
					System.out.println("open file directory");
					if (!exportDirectory.exists())
						exportDirectory.mkdirs();
					
					
					String exportYear = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
					
					String exportMonth1=Integer.toString(Calendar.getInstance().get(Calendar.MONTH) + 1);
					String exportMonth = exportMonth1.length()<2 ? "0"+ exportMonth1: exportMonth1;
					
					String exportDay1= Integer.toString(Calendar.getInstance().get(Calendar.DATE));
					String exportDay = exportDay1.length()<2 ? "0"+ exportDay1 : exportDay1;
					
					String exportHour1 = Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
					String exportHour = exportHour1.length()<2 ? "0"+ exportHour1 : exportHour1;
					
					String exportMin1 = Integer.toString(Calendar.getInstance().get(Calendar.MINUTE));
					String exportMin = exportMin1.length()<2 ? "0"+ exportMin1 : exportMin1;
					
					String exportSec1 = Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
					String exportSec = exportSec1.length()<2 ? "0"+ exportSec1 : exportSec1;
					
					String subname= exportYear+ exportMonth +exportDay +"_"+ exportHour+exportMin+exportSec;
					String filename = "dailyview_" + subname + ".jpg";

					System.out.println("output file dailyview.jpg");
					File outputFile = new File(exportDirectory, filename);

					fos = new FileOutputStream(outputFile);

					screenBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

					fos.flush();
					fos.close();
					fos = null;

					Toast.makeText(DailyView.this, "Finish", Toast.LENGTH_SHORT)
							.show();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});

		/* button refresh
		bRefresh = (Button) findViewById(R.id.dailyview_refresh);
		bRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				System.out.println("(daily) Refresh");
				dailyview_today.invalidate();
			}
		});*/

		// text date
		dailyview_today = (TextView) findViewById(R.id.dailyview_today);
		dailyview_today.setText(dailyDayOfMonth + " / " + dailyMonth + " / "
				+ dailyYear);

		twelve_am = (TextView) findViewById(R.id.twelve_am);
		setupDailyTimes(this);

		testAddLabels(this);

		// implement horizontal view ( 3 days to be shown)
		Display display = ((WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int rotation = display.getRotation();

		if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
			Calendar day2, day3;
			// first day
			testAddLabels(this);
			// second day
			hView = 2;
			calendar.add(Calendar.DATE, 1);
			day2 = (Calendar) calendar.clone();
			dailyYear = calendar.get(Calendar.YEAR);
			dailyMonth = calendar.get(Calendar.MONTH) + 1;
			dailyDayOfMonth = calendar.get(Calendar.DATE);
			testAddLabels(this);
			// third day
			hView = 3;
			calendar.add(Calendar.DATE, 1);
			day3 = (Calendar) calendar.clone();
			dailyYear = calendar.get(Calendar.YEAR);
			dailyMonth = calendar.get(Calendar.MONTH) + 1;
			dailyDayOfMonth = calendar.get(Calendar.DATE);
			testAddLabels(this);
			hView = 1;

			// reset variable
			calendar.add(Calendar.DATE, -2);
			dailyYear = calendar.get(Calendar.YEAR);
			dailyMonth = calendar.get(Calendar.MONTH) + 1;
			dailyDayOfMonth = calendar.get(Calendar.DATE);

			// text view at the top
			dailyview_today.setText(dailyDayOfMonth + "/" + dailyMonth + "/"
					+ dailyYear + "     "
					+ DateFormat.format("dd/MM/yyyy", day2) + "     "
					+ DateFormat.format("dd/MM/yyyy", day3));

		}

		// button previous
		bPrevious = (Button) findViewById(R.id.dailyview_bPrevious);
		bPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// setContentView(R.layout.addevent);
				calendar.set(dailyYear, dailyMonth - 1, dailyDayOfMonth);

				calendar.add(Calendar.DATE, -1);
				dailyYear = calendar.get(Calendar.YEAR);
				dailyMonth = calendar.get(Calendar.MONTH) + 1;
				dailyDayOfMonth = calendar.get(Calendar.DATE);
				finish();
				startActivity(new Intent("com.calendar.DAILYVIEW"));

			}

		});

		// button next
		bNext = (Button) findViewById(R.id.dailyview_bNext);
		bNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// setContentView(R.layout.addevent);
				calendar.set(dailyYear, dailyMonth - 1, dailyDayOfMonth);

				calendar.add(Calendar.DATE, 1);
				dailyYear = calendar.get(Calendar.YEAR);
				dailyMonth = calendar.get(Calendar.MONTH) + 1;
				dailyDayOfMonth = calendar.get(Calendar.DATE);

				finish();
				startActivity(new Intent("com.calendar.DAILYVIEW"));

			}

		});

	}

	public void testAddLabels(Context t) {
		String sdate = dailyYear + "" + getZero(dailyMonth) + ""
				+ getZero(dailyDayOfMonth);
		String output[] = getInterval(sdate);
		if (output.length != 0) {
			EventItem events[] = new EventItem[output.length / 3];
			for (int i = 0; i < output.length; i += 3) {
				int pos=0;
				long stime = Long.parseLong(output[i + 1]);
				long etime = Long.parseLong(output[i + 2]);
				// overlap
				for (int j = 0; j < output.length; j+=3) {
					if(i==j) continue;
					long st = Long.parseLong(output[j + 1]);
					long et = Long.parseLong(output[j + 2]);
					Log.i("for loop", j +" "+ output[i+1] + " " + output[j+1]+ " " +output[j+2]);
					if ((stime >= st && stime <= st + et)||(st>=stime &&st <=stime + etime)){
						count++;
						Log.i("for loop-if", count +"");
						
					}
				}
				for (int k = 0; k < i; k+=3) {
					long st = Long.parseLong(output[k + 1]);
					long et = Long.parseLong(output[k + 2]);
					Log.i("for loop", k +" "+ output[i+1] + " " + output[k+1]+ " " +output[k+2]);
					if ((stime >= st && stime <= st + et)||(st>=stime &&st <=stime + etime)){
						pos++;
						Log.i("for loop-if", count +"");
						
					}
				}
				//
				events[i / 3] = new EventItem(t, stime, etime, output[i],pos);
				// relativeLayout.addView(events[i / 3]);
				count = 1;
			}
		}
	}

	// input a day e.g. yyyyMMdd
	// return {eventID, start time, length, .. .. ..}
	public String[] getInterval(String sDate) {
		LinkedList<String> x = new LinkedList<String>();
		JSONArray ja;
		String condition = " startDate <= '" + sDate + "' AND endDate >= '"
				+ sDate + "' order by startDate, startTime";

		try {
			ja = AndroidCalendar2Activity.getDB().fetchConditional("TimeTable",
					condition);
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				Date initPoint = TheActDate(sDate, "00:00");
				Date startPoint;
				Date endPoint;

				if (jo.getString("startDate").compareTo(sDate) < 0) {
					startPoint = TheActDate(sDate, "00:00");
				} else {
					startPoint = TheActDate(sDate, jo.getString("startTime"));
				}
				if (jo.getString("endDate").compareTo(sDate) > 0) {
					endPoint = TheActDate(sDate, "00:00");
					endPoint = nextDay(endPoint);
				} else {
					endPoint = TheActDate(sDate, jo.getString("endTime"));
				}

				long slen = timeLength(initPoint, startPoint);
				long elen = timeLength(startPoint, endPoint);
				x.add(jo.getString("eventID"));
				x.add(String.valueOf(slen));
				x.add(String.valueOf(elen));
			}

		} catch (Exception e) {
			Log.i("error", e.toString());
		}

		return x.toArray(new String[x.size()]);
	}

	public long timeLength(Date actsdate, Date actedate) throws Exception {
		return (actedate.getTime() - actsdate.getTime()) / 1000 / 60 / 5;
	}

	public Date nextDay(Date a) {
		Date output = new Date();
		output.setTime(a.getTime() + 1000 * 60 * 60 * 24);
		return output;
	}

	public Date TheActDate(String d, String hm) throws Exception {
		Date sd = new Date();
		String[] HourMin = hm.split(":");
		long thetime = new SimpleDateFormat("yyyyMMdd").parse(d).getTime()
				+ Integer.parseInt(HourMin[0]) * 60 * 60 * 1000
				+ Integer.parseInt(HourMin[1]) * 60 * 1000;
		sd.setTime(thetime);
		return sd;
	}

	// setup daily view
	public void setupDailyTimes(Context t) {
		String[] times = { "1:00am", "2:00am", "3:00am", "4:00am", "5:00am",
				"6:00am", "7:00am", "8:00am", "9:00am", "10:00am", "11:00am",
				"12:00pm", "1:00pm", "2:00pm", "3:00pm", "4:00pm", "5:00pm",
				"6:00pm", "7:00pm", "8:00pm", "9:00pm", "10:00pm", "11:00pm",
				"12:00am" };

		for (int i = 0; i < times.length; i++) {
			TextView tv = new TextView(t);
			tv.setText(times[i]);
			// tv.setTextColor(R.color.text_color);
			tv.setTextColor(Color.BLACK);

			float h = twelve_am.getBottom() + dp2px(t, 60 * (i + 1));

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.topMargin = (int) h;
			params.leftMargin = 0;
			relativeLayout.addView(tv, params);
			relativeLayout.setMinimumHeight((int) (h + dp2px(t, 61)));

			// horizontal line
			View v1 = new View(t);
			View v2 = new View(t);
			RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params1.leftMargin = 100;

			params1.topMargin = (int) h + 15;

			RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params2.leftMargin = 100;
			params2.topMargin = (int) h - 30;

			v1.setBackgroundColor(Color.BLACK);
			v2.setBackgroundColor(Color.GRAY);
			v1.setMinimumHeight(2);
			v2.setMinimumHeight(2);
			v1.setMinimumWidth(200);
			v2.setMinimumWidth(200);
			relativeLayout.addView(v1, params1);
			relativeLayout.addView(v2, params2);
		}

		// for the line @ 12:00 am
		View v1 = new View(t);

		RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params3.leftMargin = 100;
		params3.topMargin = 15;

		v1.setBackgroundColor(Color.BLACK);

		v1.setMinimumHeight(2);
		v1.setMinimumWidth(200);
		relativeLayout.addView(v1, params3);

		// for the line @ 12:30 am
		View v2 = new View(t);
		RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params4.leftMargin = 100;
		params4.topMargin = 60;

		v2.setBackgroundColor(Color.GRAY);

		v2.setMinimumHeight(2);
		v2.setMinimumWidth(200);
		relativeLayout.addView(v2, params4);
	}

	private class EventItem extends TextView {
		String eventid;
		Context outside;

		public EventItem(final Context t, long stime, long etime, String ID, int pos) {
			super(t);
			outside = t;
			eventid = ID;
			setBackgroundColor(randomColor());
			setHeight((int) dp2px(t, etime * 5));
			try {
				setText(getJO().getString("title"));
				this.setTextColor(Color.BLACK);
			} catch (Exception e) {
				Log.i("not get JO", e.toString());
			}

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			if (hView == 2)
				params.leftMargin = 325 + pos*200/count;
			else if (hView == 3)
				params.leftMargin = 550 + pos*200/count;
			else
				params.leftMargin = 100 + pos*200/count;

			params.topMargin = (int) (15 + dp2px(t, (stime * 5)));
			// count = count + 50;
			// if(count ==200)
			// count =0;
			super.setWidth(200/count);
			
		//	if((hView ==2 || hView == 3) &&count > 150)
		//		super.setWidth(300-count); 

			// overlap event configuration

			// //

			relativeLayout.addView(this, params);
			super.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					try {
						// build a dialog to show the information of the event
						final JSONObject itemOB = getJO();
						AlertDialog.Builder builder = new AlertDialog.Builder(t);
						String startDate = itemOB.getString("startDate");
						Calendar s = Calendar.getInstance();

						s.set(Integer.parseInt(startDate.substring(0, 4)),
								Integer.parseInt(startDate.substring(4, 6)) - 1,
								Integer.parseInt(startDate.substring(6, 8)));
						String st = DateFormat.format("dd-MMM-yyyy", s) + "";

						String endDate = itemOB.getString("endDate");
						Log.i("endDateDB", endDate);
						Calendar e = Calendar.getInstance();
						e.set(Integer.parseInt(endDate.substring(0, 4)),
								Integer.parseInt(endDate.substring(4, 6)) - 1,
								Integer.parseInt(endDate.substring(6, 8)));
						String en = DateFormat.format("dd-MMM-yyyy", e) + "";

						final String info = "Title: 	"
								+ itemOB.getString("title") + "\n" + "Start:	"
								+ st + "   " + itemOB.getString("startTime")
								+ "\n" + "End:		" + en + "   "
								+ itemOB.getString("endTime") + "\n"
								+ "Location:   " + itemOB.getString("location")
								+ "\n" + "Contact:	"
								+ itemOB.getString("contact");
						builder.setMessage(info);
						builder.setCancelable(true);

						builder.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

									}
								});

						builder.setNeutralButton("View photo",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										try {
											Intent i = new Intent(
													"com.calendar.PHOTOLINK");
											i.putExtra("title",
													itemOB.getString("title"));
											i.putExtra("startDate", itemOB
													.getString("startDate"));
											i.putExtra("endDate",
													itemOB.getString("endDate"));
											i.putExtra("startTime", itemOB
													.getString("startTime"));
											i.putExtra("endTime",
													itemOB.getString("endTime"));
											i.putExtra("location", itemOB
													.getString("location"));
											startActivity(i);
										} catch (Exception e) {
										}
									}
								});

						builder.setNegativeButton("Edit",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										try {
											Log.i("123", "000111123");
											// pass the info to the edit event
											// page
											Intent i = new Intent(
													"com.calendar.EDITEVENT");
											i.putExtra("eventid", eventid);
											i.putExtra("title",
													itemOB.getString("title"));
											i.putExtra("startDate", itemOB
													.getString("startDate"));
											i.putExtra("endDate",
													itemOB.getString("endDate"));
											i.putExtra("startTime", itemOB
													.getString("startTime"));
											i.putExtra("endTime",
													itemOB.getString("endTime"));
											i.putExtra("location", itemOB
													.getString("location"));
											i.putExtra("contact",
													itemOB.getString("contact"));

											Log.i("123", "111123");
											startActivity(i);
											finish();
										} catch (Exception e) {

										}
									}
								});

						AlertDialog alert = builder.create();
						alert.show();

					} catch (Exception e) {

					}
				}

			});
		}

		// get entire record for this event
		public JSONObject getJO() throws Exception {
			JSONArray ja = AndroidCalendar2Activity.getDB().fetchConditional(
					"TimeTable", " eventID = '" + eventid + "' ");
			if (ja == null)
				return null;
			if (ja.length() != 1)
				return null;
			return ja.getJSONObject(0);
		}

		// produce random color for the item
		public int randomColor() {
			return Color.rgb(new Random().nextInt(100) + 155,
					new Random().nextInt(100) + 155,
					new Random().nextInt(100) + 155);
		}
	}

	// convert month or day
	private String getZero(int x) {
		if (String.valueOf(x).length() < 2) {
			return "0" + String.valueOf(x);
		}
		return String.valueOf(x);
	}

	// convert dp to px
	private float dp2px(Context t, float dp) {
		float scale = t.getResources().getDisplayMetrics().density;
		return (dp * scale + 0.5f);
	}
}
