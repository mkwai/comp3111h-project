package comp.location;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class Utils {

	private final static String TAG ="Utils";
	
	/**
	 * get data
	 * @param args
	 * @return
	 */
	public final static InputStream getStream(String args) {
		InputStream stream = null;
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(args);
		try {
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				stream = entity.getContent();
			}
			return stream;
		} catch (Exception e) {
			e.printStackTrace();
			return stream;
		}
		
	}
	
	/**
	 * city infos
	 * @param countryCode
	 * @return
	 */
	public static List<CityBean> getCityInfos(String countryCode) {
		List<CityBean> cityList = new ArrayList<CityBean>();
		//
		StringBuilder sBuilder = new StringBuilder();
		BufferedReader bReader = new BufferedReader(new InputStreamReader(getStream(countryCode))); 
		try {
			for (String s = bReader.readLine(); s != null; s = bReader.readLine()) { 
				sBuilder.append(s); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		try {
			JSONObject jsonObject = new JSONObject(sBuilder.toString()); 
			JSONArray jsonArray = jsonObject.getJSONArray("results"); 
			
			for (int i = 0; i < jsonArray.length(); i++) {
				CityBean cityBean = new CityBean();
				JSONObject jsonObj = (JSONObject) jsonArray.opt(i);
				JSONObject jsonGeo = jsonObj.getJSONObject("geometry");
				JSONObject jsonLocation = jsonGeo.getJSONObject("location");
				
				cityBean.setCityName(jsonObj.getString("formatted_address"));
				cityBean.setLat(jsonLocation.getDouble("lat"));
				cityBean.setLon(jsonLocation.getDouble("lng"));
				cityList.add(cityBean);
				Log.i(TAG, "name="+cityBean.getCityName()+";lat="+cityBean.getLat()+";lng="+cityBean.getLng());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return cityList;
	}

	

	public static TravelingDuration getDuration(String args, String destination){
		
		TravelingDuration TD = new TravelingDuration(destination);
		
		StringBuilder sBuilder = new StringBuilder();
		BufferedReader bReader = new BufferedReader(new InputStreamReader(getStream(args))); 
		
		try {
			for (String s = bReader.readLine(); s != null; s = bReader.readLine()) { 
				sBuilder.append(s); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		try {
			JSONObject jsonObject = new JSONObject(sBuilder.toString()); 
			JSONArray routesArray = jsonObject.getJSONArray("routes"); 
			Log.i("length", ""+ routesArray.length());
			
			if(routesArray.length()>0){
				JSONObject routes = (JSONObject) routesArray.opt(0);
				
				JSONArray legsArray = routes.getJSONArray("legs");
				JSONObject legs = (JSONObject) legsArray.opt(0);
				
				JSONObject duration = legs.getJSONObject("duration");
	
				TD.setTimeTaken(duration.getString("text"));
				TD.setTotalSecond(duration.getInt("value"));
				Log.i(TAG, "TimeTaken="+TD.getTimeTaken()+";Total Second="+TD.getTotalSecond());
			}
			
			else{
				TD.setTimeTaken("Cannot be calculated");
				TD.setTotalSecond(-1);
				Log.i(TAG, "TimeTaken="+TD.getTimeTaken()+";Total Second="+TD.getTotalSecond());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return TD;
	}
	
}