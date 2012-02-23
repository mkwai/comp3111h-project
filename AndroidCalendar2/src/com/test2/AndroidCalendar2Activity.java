package com.test2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;

public class AndroidCalendar2Activity extends Activity {
    /** Called when the activity is first created. */
	CalendarView view;
	Button b1;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        b1 = (Button) findViewById(R.id.b1);
        b1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				view.setDate(System.currentTimeMillis());
			}
        	
        });
        view = (CalendarView) findViewById(R.id.view2);
        view.setEnabled(true);
        view.setShowWeekNumber(false);
 
	}
	

	
}