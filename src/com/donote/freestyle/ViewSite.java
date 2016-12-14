package com.donote.freestyle;

public class ViewSite {
	private float width = 0;
	private float height = 0;
	private float locate_X = 0;
	private float locate_Y = 0;
	private float center_X = 0;
	private float center_Y = 0;
	private float radius = 0;
	private int mark;
	private String content;
	private Boolean isRecord = false;
	private Boolean isPicture = false;
	private Boolean isPhoto = false;
	private Boolean isFile = false;
	private Boolean isFace = false;
	private Boolean isText = false;
	private Boolean isVideo = false;
	private Boolean isOther = false;
	private Boolean isTable = false;
	private Boolean isDraw = false;
	private Boolean isCloude = false;
	public ViewSite() {
		
	}

	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(float width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
	}

	/**
	 * @return the locate_X
	 */
	public float getLocate_X() {
		return locate_X;
	}

	/**
	 * @param locate_X
	 *            the locate_X to set
	 */
	public void setLocate_X(float locate_X) {
		this.locate_X = locate_X;
		this.center_X = this.locate_X + width / 2;
	}

	/**
	 * @return the locaet_Y
	 */
	public float getLocate_Y() {
		return locate_Y;
	}

	/**
	 * @param locaet_Y
	 *            the locaet_Y to set
	 */
	public void setLocate_Y(float locate_Y) {
		this.locate_Y = locate_Y;
		this.center_Y = this.locate_Y + height / 2;
	}

	/**
	 * @return the mark
	 */
	public int getMark() {
		return mark;
	}

	/**
	 * @param mark
	 *            the mark to set
	 */
	public void setMark(int mark) {
		this.mark = mark;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the center_X
	 */
	public float getCenter_X() {
		return center_X;
	}

	/**
	 * @return the center_Y
	 */
	public float getCenter_Y() {
		return center_Y;
	}

	/**
	 * @return the isRecord
	 */
	public Boolean getIsRecord() {
		return isRecord;
	}

	/**
	 * @param isRecord the isRecord to set
	 */
	public void setIsRecord(Boolean isRecord) {
		this.isRecord = isRecord;
	}

	/**
	 * @return the isPicture
	 */
	public Boolean getIsPicture() {
		return isPicture;
	}

	/**
	 * @param isPicture the isPicture to set
	 */
	public void setIsPicture(Boolean isPicture) {
		this.isPicture = isPicture;
	}

	/**
	 * @return the isPhoto
	 */
	public Boolean getIsPhoto() {
		return isPhoto;
	}

	/**
	 * @param isPhoto the isPhoto to set
	 */
	public void setIsPhoto(Boolean isPhoto) {
		this.isPhoto = isPhoto;
	}

	/**
	 * @return the isFile
	 */
	public Boolean getIsFile() {
		return isFile;
	}

	/**
	 * @param isFile the isFile to set
	 */
	public void setIsFile(Boolean isFile) {
		this.isFile = isFile;
	}

	/**
	 * @return the isFace
	 */
	public Boolean getIsFace() {
		return isFace;
	}

	/**
	 * @param isFace the isFace to set
	 */
	public void setIsFace(Boolean isFace) {
		this.isFace = isFace;
	}

	/**
	 * @return the isText
	 */
	public Boolean getIsText() {
		return isText;
	}

	/**
	 * @param isText the isText to set
	 */
	public void setIsText(Boolean isText) {
		this.isText = isText;
	}

	/**
	 * @return the isVideo
	 */
	public Boolean getIsVideo() {
		return isVideo;
	}

	/**
	 * @param isVideo the isVideo to set
	 */
	public void setIsVideo(Boolean isVideo) {
		this.isVideo = isVideo;
	}

	/**
	 * @return the isOther
	 */
	public Boolean getIsOther() {
		return isOther;
	}

	/**
	 * @param isOther the isOther to set
	 */
	public void setIsOther(Boolean isOther) {
		this.isOther = isOther;
	}

	/**
	 * @return the radius
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(float radius) {
		this.radius = radius;
	}

	/**
	 * @return the isTable
	 */
	public Boolean getIsTable() {
		return isTable;
	}

	/**
	 * @param isTable the isTable to set
	 */
	public void setIsTable(Boolean isTable) {
		this.isTable = isTable;
	}

	/**
	 * @return the isDraw
	 */
	public Boolean getIsDraw() {
		return isDraw;
	}

	/**
	 * @param isDraw the isDraw to set
	 */
	public void setIsDraw(Boolean isDraw) {
		this.isDraw = isDraw;
	}

	/**
	 * @return the isCloude
	 */
	public Boolean getIsCloude() {
		return isCloude;
	}

	/**
	 * @param isCloude the isCloude to set
	 */
	public void setIsCloude(Boolean isCloude) {
		this.isCloude = isCloude;
	}
	
	
	
	
}
