package com.Alarm;


import java.io.Serializable;

public class AlarmBean implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public AlarmBean(){}
	
	private int ID ;
	private String title;  
	private Long milliS;
	private boolean event;

	//private String eventID;
	
	public AlarmBean(int ID, String title, Long milliS, boolean event) {
		this.ID = ID;
		this.title = title;
		this.milliS = milliS;
		this.event = event;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getID() {
		return ID;
	}
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public boolean isEvent() {
		return event;
	}
	public void EventOrTask(boolean isEvent) {
		this.event = isEvent;
	}
	
	public Long getMillisecond() {
		return milliS;
	}
	public void setMillisecond(Long milliS) {
		this.milliS = milliS;
	}
	
}