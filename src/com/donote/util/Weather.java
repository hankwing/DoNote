package com.donote.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.imax.vmall.sdk.android.common.adapter.ServiceCallback;
import com.imax.vmall.sdk.android.entry.CapabilityService;
import com.imax.vmall.sdk.android.entry.CommonService;
import com.imax.vmall.sdk.android.huawei.LBS.LBS;
import com.imax.vmall.sdk.android.huawei.weather.WeatherService;
import com.wxl.donote.R;

public class Weather { 

	// private String TAG = "MainActivity";

	/**
	 * ���ݾ�γ�Ȼ�ȡ������Ϣ
	 */
	// private Button getWeatherButtonByGEO;

	/**
	 * ���ݵ�����ȡ������Ϣ
	 */
	// private Button getWeatherButton;

	/**
	 * ���ݹ��Ҵ����ѯ���ҵ���������ʡ����Ϣ
	 */
	// private Button getProvinceInfo;
	/**
     * LBSʵ��
     */
    private LBS lbs;
	private Message msg;
	private String lnglat;
	/**
	 * CommonService
	 */
	private CommonService cs;
	/**
	 * WeatherServiceʵ��
	 */
	private WeatherService weather;
	static boolean isCuccess = false;
	/**
	 * CityServiceʵ��
	 */
	//private CityService city;
	//private LocationUtils cityName;
	Context context;
	Handler handler;

	public Weather(Context context, final Handler handler) {
		// ��ʼ��ҵ��ӿ�ʵ��
		weather = CapabilityService.getWeatherServiceInstance();
		//city = CapabilityService.getCityServiceInstance();
		//cityName = new LocationUtils();
		this.context = context;
		this.handler = handler;
		// ʵ����CommonService
		cs = CommonService.getInstance();
		 //��ʼ��ҵ��ӿ�ʵ��
        lbs = CapabilityService.getLBSInstance();
		// Ӧ��ID����ȥiMAXƽ̨ע������
		String appId = "8418448804";
		// Ӧ��Key
		String appKey = "be3e9d3661048e69a51419caa65cee20";
		// ͨ��CommonService���ü�Ȩ�ӿڣ��ڵ�����������ǰ���뱣֤��Ȩ��ʼ���ɹ�
		cs.init(context, appId, appKey, new ServiceCallback() {
			public void onComplete(String arg0) {
				// TODO Auto-generated method stub
				// ������Ϣ
			}

			@Override
			public void onError(String arg0) {
				// TODO Auto-generated method stub
				msg = new Message();
				msg.what = 9;
				handler.sendMessage(msg);
			
			}
		});	

		if (MyLocationListener.longitude.equals(""))
		{
			
			msg = new Message();
			msg.what = 4;
			handler.sendMessage(msg);
		}
		if (!MyLocationListener.latitude.equals(""))
		{
			getWeather();
			getLocString();
		} else
		{
			msg = new Message();
			msg.what = 4;
			handler.sendMessage(msg);
		}
	}

	public int getIcon(int code) {
		switch (code)
		{
		case 31:
		case 32:
		case 33:
		case 34:
		case 36:
			return (R.drawable.weather_sunny);
		case 19:
		case 20:
		case 21:
		case 22:
			return (R.drawable.weather_foggy);
		case 5:
		case 6:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
			return (R.drawable.weather_rain);
		case 3:
		case 4:
			return R.drawable.weather_harvyrain;
		case 26:
		case 29:
		case 30:
		case 44:
			return R.drawable.weather_cloudy;
		case 7:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 41:
		case 42:
		case 43:
			return R.drawable.weather_snow;
		case 28:
		case 27:
			return R.drawable.weather_mostcloud;
		case 45:
		case 46:
		case 47:
		case 2:
		case 1:
			return R.drawable.weather_thou;
		case 0:
		case 23:
		case 24:
			return R.drawable.weather_wind;
		default:
			return R.drawable.weather_default;
		}
	}

	// ���ݾ�γ�Ȼ�ȡ������Ϣ
	public void getWeather() {
		
		// ���� 
		//String longitude = "118.46"; 
		//γ�� 
		//String latitude = "32.03";
		 
		// ����getWeather���ݾ�γ�Ȼ�ȡ������Ϣ
		 //��γ��
        
        
		weather.getWeather(MyLocationListener.longitude, MyLocationListener.latitude,
				new ServiceCallback() {
					public void onError(String arg0) {
						// api�ӿڵ��ô�����Ӧ
						msg = new Message();
						msg.what = 3;
						msg.obj = arg0;
						handler.sendMessage(msg);
						// ������Ϣ
					}

					public void onComplete(String arg0) {
						// api�ӿڵ��óɹ���Ӧ
						isCuccess = true;
						msg = new Message();
						msg.what = 2;
						msg.obj = arg0;
						handler.sendMessage(msg);
						// ������Ϣ
					}
				});
	
	}
	
	public void getLocString () {
		
		lnglat = MyLocationListener.longitude + "," + MyLocationListener.latitude;
		lbs.getLocation(lnglat, new ServiceCallback()
        {
            public void onError(String arg0)
            {        
                //������Ϣ
                msg = new Message();
                msg.what = 8;
                msg.obj = arg0;
                handler.sendMessage(msg); 
            }
            
            public void onComplete(String arg0)
            {
                //������Ϣ
                msg = new Message();
                msg.what = 7;
                msg.obj = arg0;
                handler.sendMessage(msg); 
            }
        });
	}
	
	class LocationLoadThread implements Runnable {
		public void run() {
			
		}
	}

}
