package com.location;

import java.io.Serializable;

/**
 * @author Wai
 */
public class TravelingDuration implements Serializable{

	private static final long serialVersionUID = 1L;
	private String destination;
	private String timeTaken; 
	private int totalSecond; 		
	private int day;
	private int hour;
	private int minute;
	private int second;
	
	
	public TravelingDuration(String destination) {
		super();
		this.destination =  destination;
	}
	
	public TravelingDuration(TravelingDuration TD){
		this.destination = TD.getDestination();
		this.timeTaken = TD.getTimeTaken();
		this.totalSecond = TD.getTotalSecond();
		this.day = TD.getDay();
		this.hour = TD.getHour();
		this.minute = TD.getMinute();
		this.second = TD.getSecond();
	}
	
	public String getDestination() {
		return destination;
	}
	public String getTimeTaken() {
		return timeTaken;
	}
	public void setTimeTaken(String timeTaken) {
		this.timeTaken = timeTaken;
	}

	public int getTotalSecond() {
		return totalSecond;
	}
	public int getSecond() {
		return second;
	}
	public int getMinute() {
		return minute;
	}
	public int getHour() {
		return hour;
	}
	public int getDay() {
		return day;
	}

	public void setTotalSecond(int totalSecond) {
		
		if(totalSecond>=0){
		this.totalSecond = totalSecond;
		this.second = totalSecond%60;
		
		int totalMin = totalSecond/60;
		this.minute = totalMin%60;
		
		int totalHour = totalMin/60;
		this.hour = totalHour%24;
		
		this.day =  totalHour/24;
		}
		else{
			this.totalSecond = totalSecond;
			this.second = 0;
			this.minute = 0;
			this.hour = 0;
			this.day = 0;
			
		}
	}
}
