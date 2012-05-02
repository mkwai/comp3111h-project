package comp.history;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.test2.R;

public class PopupMessage extends Service {
	
	static String site = "";
	
	public void onCreate()
	{
		super.onCreate();
		Log.i("SERVICE", "onCreate................");
		
		//Toast.makeText(PopupMessage.this, "onCreate................", Toast.LENGTH_LONG).show();
	}
	
	public void onStart(Intent intent, int startid){
		Log.i("SERVICE", "Start!!!!!!!!!!!!");
		
		//Toast.makeText(PopupMessage.this, "You should not access " + site + " in this time!" , Toast.LENGTH_LONG).show();
		showToast();
	}
	
	public void onDestroy()
	{
		Log.i("SERVICE", "onDestroy.................");
		//Toast.makeText(PopupMessage.this, "onDestroy.................", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public void showToast(){
    	LayoutInflater li=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View view=li.inflate(R.layout.toast,null);
    	//create a view by toast.xml
    	Toast toast=new Toast(this);
    	toast.setView(view);
    	//load toast.xml
    	TextView tv=(TextView)view.findViewById(R.id.tv1);
    	
    	//tell the reason why showing this message e.g. browsing ... 
    	tv.setText("You shounld not access " + site + " in this time!");
    	
    	//LENGTH_SHORT -> not prefer short duration
    	toast.setDuration(Toast.LENGTH_LONG);

    	toast.show();
    	toast.show();
    	toast.show();
    }
    
    static void setSite(String temp){
    	site = new String(temp);
    }
}
