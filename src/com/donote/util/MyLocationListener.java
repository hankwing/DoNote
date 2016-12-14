package com.donote.util;

import com.baidu.location.*;
import android.os.Handler;
import android.os.Message;

public class MyLocationListener implements BDLocationListener {
	public static String longitude = "";
	public static String latitude = "";
	private Handler handler;
	private Message msg;
	public static String Address = null;
	public MyLocationListener( Handler handler) {
		this.handler = handler;
	}
	@Override
	public void onReceiveLocation(BDLocation location) {

		longitude = String.valueOf(location.getLongitude());
		latitude = String.valueOf(location.getLatitude());
		Address = location.getAddrStr();
		
		if (Address != null) {
			msg = new Message();
			msg.what = 1;
			msg.obj = Address;
		}
		else {
			msg = new Message();
			msg.what = 4;
		}
		handler.sendMessage(msg);
		
	}

	public void onReceivePoi(BDLocation poiLocation) {

	}
}