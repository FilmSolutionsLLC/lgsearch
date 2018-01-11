package com.fps.domain;
import java.io.Serializable;



public class ImageSubjectKeyword implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/** Property imageID */
	private Long imageID;

	/** Property projectID */
	private Long projectID;

	/** Property subject */
	private String subject;

	/** Property keyword */
	private String keyword;

	/**
	 * Constructor
	 */
	public ImageSubjectKeyword() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 */
	public ImageSubjectKeyword(ImageSubjectKeyword imageSubjectKeyword) {
		// TODO Auto-generated constructor stub

		this.imageID = imageSubjectKeyword.getImageID();
		this.keyword = imageSubjectKeyword.getKeyword();
		this.subject = imageSubjectKeyword.getSubject();
	}

	/**
	 * Gets the projectID
	 */
	public Long getProjectID() {
		return this.projectID;
	}

	/**
	 * Sets the projectID
	 */
	public void setProjectID(Long value) {
		this.projectID = value;
	}

	/**
	 * Gets the imageID
	 */
	public Long getImageID() {
		return this.imageID;
	}

	/**
	 * Sets the imageID
	 */
	public void setImageID(Long value) {
		this.imageID = value;
	}

	/**
	 * Gets the subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * Sets the subject
	 */
	public void setSubject(String value) {
		this.subject = value;
	}

	/**
	 * Gets the keyword
	 */
	public String getKeyword() {
		return this.keyword;
	}

	/**
	 * Sets the keyword
	 */
	public void setKeyword(String value) {
		this.keyword = value;
	}

	public ImageSubjectKeyword(Long projectID, Long imageID, String subject,
			String keyword) {
		super();
		this.projectID = projectID;
		this.imageID = imageID;
		this.subject = subject;
		this.keyword = keyword;

	}

	@Override
	public String toString() {
		return "ImageSubjectKeyword [projectID=" + projectID + ", imageID="
				+ imageID + ", subject=" + subject + ", keyword=" + keyword
				+ "]";
	}

	public void checkForNull() {
		if (subject == null) {
			subject = "NOT AVAILABLE";

		}
		if (keyword == null) {
			keyword = "NOT AVAILABLE";

		}
	}

}
