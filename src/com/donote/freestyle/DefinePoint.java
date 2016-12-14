package com.donote.freestyle;

public class DefinePoint {
	float x;
	float y;
	float dis = 0;

	public DefinePoint(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	
	public float getDis() {
		return dis;
	}

	
	public void setDis(float dis) {
		this.dis = dis;
	}

}
