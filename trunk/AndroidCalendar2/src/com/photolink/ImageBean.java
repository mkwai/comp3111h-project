package com.photolink;

public class ImageBean {
	
	private static final long serialVersionUID = 1L;

	public ImageBean(){}
	
	private String imageName;  
	private int ID;
	
	public ImageBean(String imageName, int ID) {
		super();
		this.imageName = imageName;
		this.ID = ID;
	}

	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public Integer getID() {
		return ID;
	}
	public void setID(int ID) {
		this.ID = ID;
	}
}
