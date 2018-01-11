package com.fps.domain;

import java.io.Serializable;

public class ImageDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long ID;
	private String address;

	public Long getID() {
		return ID;
	}

	public void setID(Long iD) {
		ID = iD;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
