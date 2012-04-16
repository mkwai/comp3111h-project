package com.photolink;

import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.calendar.AddEvent;
import com.calendar.CityListActivity;
import com.test2.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
 
public class PhotolinkActivity extends Activity /*implements OnItemClickListener*/{
	private static final String LIST_STATE = "listState";
	private Parcelable mListState = null;
	ListView imagelist;
	Cursor imagecursor;
	int image_column_index;
	int count;
	private TextView total;

	int timezone = 8;

	public boolean isLinkReady() {
		return (startYear >= 1900 && endYear >= 1900 && startMonth >= 1 && endMonth >= 1);
	}

	// set Starts
	public void setStart(int year, int month, int day, int hour, int min) {
		startYear = year;
		startMonth = month;
		startDay = day;
		startHour = hour;
		startMin = min;
	}

	public void setStartYear(int year) {
		startYear = year;
	}

	public void setStartMonth(int month) {
		startMonth = month;
	}

	public void setStartDay(int day) {
		startDay = day;
	}

	public void setStartHour(int hour) {
		startHour = hour;
	}

	public void setStartMin(int min) {
		startMin = min;
	}

	// set Ends
	public void setEnd(int year, int month, int day, int hour, int min) {
		endYear = year;
		endMonth = month;
		endDay = day;
		endHour = hour;
		endMin = min;
	}

	public void setEndYear(int year) {
		endYear = year;
	}

	public void setEndMonth(int month) {
		endMonth = month;
	}

	public void setEndDay(int day) {
		endDay = day;
	}

	public void setEndHour(int hour) {
		endHour = hour;
	}

	public void setEndMin(int min) {
		endMin = min;
	}

	private String title = "";
	private int startYear = 2010;
	private int startMonth = 4;
	private int startDay = 1;
	private int startHour = 0;
	private int startMin = 0;

	private int endYear = 2013;
	private int endMonth = 4;
	private int endDay = 1;
	private int endHour = 2;
	private int endMin = 0;

	
	private ArrayList<ImageBean> arrayList = new ArrayList<ImageBean>();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.gc();
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		title = extras.getString("title");
		String startDate= extras.getString("startDate");
		String startTime= extras.getString("startTime");
		
		
		startYear = Integer.parseInt(extras.getString("startDate").substring(0,
				4));
		startMonth = Integer.parseInt(extras.getString("startDate").substring(
				4, 6));
		startDay = Integer.parseInt(extras.getString("startDate").substring(6,
				8));

		endYear = Integer.parseInt(extras.getString("endDate").substring(0, 4));
		endMonth = Integer
				.parseInt(extras.getString("endDate").substring(4, 6));
		endDay = Integer.parseInt(extras.getString("endDate").substring(6, 8));

		startHour = Integer.parseInt(extras.getString("startTime").substring(0,
				2));
		startMin = Integer.parseInt(extras.getString("startTime").substring(3,
				5));

		endHour = Integer.parseInt(extras.getString("endTime").substring(0, 2));
		endMin = Integer.parseInt(extras.getString("endTime").substring(3, 5));
	
		setContentView(R.layout.photolink);

		total = (TextView) findViewById(R.id.pl_TOTAL2);
		
		//total.setText(startYear+"-"+startMonth+"-"+startDay
		//		+ startTime+"\n"+ startHour+":"+startMin);
		
		//total.setText(startDate+"\n"+ startYear+"-"+startMonth+"-"+startDay
		//+ startTime+"\n"+ startHour+":"+startMin);
		
		init_phone_image_grid();

	}

	private ListView mListView;
	
	private void init_phone_image_grid() {
		System.gc();

		if (isLinkReady() == false) {
			ShowMsgDialog("System", "Please fill in valid date and time.");
			return;
		}
		final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		String[] proj = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATE_ADDED };
		
		Date start = new Date(startYear - 1900, startMonth - 1, startDay,
				startHour, startMin);
		long startSec = start.getTime() / 1000l;

		Date end = new Date(endYear - 1900, endMonth - 1, endDay, endHour,
				endMin);
		long endSec = end.getTime() / 1000l;

		String selection = MediaStore.Images.Media.DATE_ADDED + " > "
				+ startSec + " AND " + MediaStore.Images.Media.DATE_ADDED
				+ " < " + endSec;

		String selection2 = null; // for testing
		String[] selectionArgs = null;
		final String sortOrder = MediaStore.Images.Media.DEFAULT_SORT_ORDER;

		imagelist = (ListView) findViewById(R.id.pl_PhoneImagesList);
		imagecursor = managedQuery(uri, proj, selection, selectionArgs, sortOrder);
		count = imagecursor.getCount();
		total.setText(Integer.toString(count));

		imagelist = (ListView) findViewById(R.id.pl_PhoneImagesList);
		imagelist.setAdapter(new ImagesAdapter(getApplicationContext()));

		imagelist.setOnItemClickListener(imagegridlistener);

		
		/*
		
		total.setText(Integer.toString(count));
			
		while (imagecursor != null && imagecursor.moveToNext()) { 
			String imageName = imagecursor.getString(imagecursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
			int id = imagecursor.getInt(imagecursor.getColumnIndex("_id"));
			Log.i("hahahaha", imageName + "   " + id);
			ImageBean temp = new ImageBean(imageName, id);
			arrayList.add(temp);
		}
		*/
		
		//imagelist.setAdapter(new ArrayAdapter<ImageBean>(this, R.layout.photolink, arrayList));
		//imagelist.setOnItemClickListener(this);
		
		
		//imagelist.setAdapter(new ImagesAdapter(getApplicationContext()));

		//imagelist.setOnItemClickListener(imagegridlistener);
	}
	
	/*
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position,long id) {
		
		
		Thread t = new Thread(new Runnable(){
			public void run() {
				String imageName= arrayList.get(position).getImageName()+"";
				int ID= arrayList.get(position).getID()+"";
				
				
				//String destination = list.get(position).getCityName();
				
				/*Intent intent = new Intent(PhotolinkActivity.this, AddEvent.class);
				Bundle bundle = new Bundle();
				bundle.putString("placename", destination);
				bundle.putString("placelat", lat);
				bundle.putString("placelng", lng);
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				finish();
				
			}
		});
		
		t.start();
		
	}
	*/
	private OnItemClickListener imagegridlistener = new OnItemClickListener() {
		public void onItemClick(AdapterView parent, View v, int position,
				long id) {
			System.gc();
			image_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			imagecursor.moveToPosition(position);
			String filename = imagecursor.getString(image_column_index);

			Intent i = new Intent("com.calendar.VIEWIMAGE");
			i.putExtra("filename", filename);

			startActivity(i);
		}
	};

	public class ImagesAdapter extends BaseAdapter {
		private Context mContext;

		public ImagesAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			System.gc();
			TextView tv = new TextView(mContext.getApplicationContext());
			String id = null;
			if (convertView == null) {
				image_column_index = imagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
				imagecursor.moveToPosition(position);

				// filename
				id = "File name: " + imagecursor.getString(image_column_index);

				/*
				 * file size image_column_index = imagecursor
				 * .getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
				 * imagecursor.moveToPosition(position); id += "Size(KB): " +
				 * imagecursor.getString(image_column_index)+ "\n";
			

				// date created
				image_column_index = imagecursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
				imagecursor.moveToPosition(position);

				long ms = Long.parseLong(imagecursor
						.getString(image_column_index));

				Date d = new Date((long) (1000L * ms));

				int dd = d.getDate();
				int mm = d.getMonth() + 1;
				int yyyy = d.getYear() + 1900;
				int hr = d.getHours();
				int min = d.getMinutes();
				String mina = "";
				if (min < 10)
					mina = "0";
				int sec = d.getSeconds();

				String resultdate = Integer.toString(dd) + "-"
						+ Integer.toString(mm) + "-" + Integer.toString(yyyy)
						+ "  " + Integer.toString(hr) + ":" + mina
						+ Integer.toString(min);

				id += "Date Created: " + resultdate;
				// id += " \nMS:" + ms;
					 */
				
				tv.setText(id);
			} else
				tv = (TextView) convertView;
			return tv;
		}
	}

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