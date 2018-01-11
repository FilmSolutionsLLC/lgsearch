package com.fps.domain;

import java.io.Serializable;

public class ImageMeta implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long projectID;
	private Long imageID;
	private String photographer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProjectID() {
		return projectID;
	}

	public void setProjectID(Long projectID) {
		this.projectID = projectID;
	}

	public Long getImageID() {
		return imageID;
	}

	public void setImageID(Long imageID) {
		this.imageID = imageID;
	}

	public String getPhotographer() {
		return photographer;
	}

	public void setPhotographer(String photographer) {
		this.photographer = photographer;
	}

	public ImageMeta() {
		// TODO Auto-generated constructor stub
	}

	public ImageMeta(Long projectID, Long imageID, String photographer) {
		this.projectID = projectID;
		this.imageID = imageID;
		this.photographer = photographer;
	}

}
