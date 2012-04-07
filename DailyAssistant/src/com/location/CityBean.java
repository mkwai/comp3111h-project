package com.location;


import java.io.Serializable;

public class CityBean implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public CityBean(){}
	
	public CityBean(String cityName, Double lat, Double lng) {
		super();
		this.cityName = cityName;
		this.lat = lat;
		this.lng = lng;
	}

	private String cityName;  
	private Double lat; 
	private Double lng; 
	
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLng() {
		return lng;
	}
	public void setLon(Double lng) {
		this.lng = lng;
	}
	
}