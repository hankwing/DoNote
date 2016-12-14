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
	 * 根据经纬度获取天气信息
	 */
	// private Button getWeatherButtonByGEO;

	/**
	 * 根据地名获取天气信息
	 */
	// private Button getWeatherButton;

	/**
	 * 根据国家代码查询国家的行政区划省份信息
	 */
	// private Button getProvinceInfo;
	/**
     * LBS实例
     */
    private LBS lbs;
	private Message msg;
	private String lnglat;
	/**
	 * CommonService
	 */
	private CommonService cs;
	/**
	 * WeatherService实例
	 */
	private WeatherService weather;
	static boolean isCuccess = false;
	/**
	 * CityService实例
	 */
	//private CityService city;
	//private LocationUtils cityName;
	Context context;
	Handler handler;

	public Weather(Context context, final Handler handler) {
		// 初始化业务接口实例
		weather = CapabilityService.getWeatherServiceInstance();
		//city = CapabilityService.getCityServiceInstance();
		//cityName = new LocationUtils();
		this.context = context;
		this.handler = handler;
		// 实例化CommonService
		cs = CommonService.getInstance();
		 //初始化业务接口实例
        lbs = CapabilityService.getLBSInstance();
		// 应用ID，请去iMAX平台注册申请
		String appId = "8418448804";
		// 应用Key
		String appKey = "be3e9d3661048e69a51419caa65cee20";
		// 通过CommonService调用鉴权接口，在调用其它能力前必须保证鉴权初始化成功
		cs.init(context, appId, appKey, new ServiceCallback() {
			public void onComplete(String arg0) {
				// TODO Auto-generated method stub
				// 设置消息
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

	// 根据经纬度获取天气信息
	public void getWeather() {
		
		// 经度 
		//String longitude = "118.46"; 
		//纬度 
		//String latitude = "32.03";
		 
		// 调用getWeather根据经纬度获取天气信息
		 //经纬度
        
        
		weather.getWeather(MyLocationListener.longitude, MyLocationListener.latitude,
				new ServiceCallback() {
					public void onError(String arg0) {
						// api接口调用错误响应
						msg = new Message();
						msg.what = 3;
						msg.obj = arg0;
						handler.sendMessage(msg);
						// 设置消息
					}

					public void onComplete(String arg0) {
						// api接口调用成功响应
						isCuccess = true;
						msg = new Message();
						msg.what = 2;
						msg.obj = arg0;
						handler.sendMessage(msg);
						// 设置消息
					}
				});
	
	}
	
	public void getLocString () {
		
		lnglat = MyLocationListener.longitude + "," + MyLocationListener.latitude;
		lbs.getLocation(lnglat, new ServiceCallback()
        {
            public void onError(String arg0)
            {        
                //设置消息
                msg = new Message();
                msg.what = 8;
                msg.obj = arg0;
                handler.sendMessage(msg); 
            }
            
            public void onComplete(String arg0)
            {
                //设置消息
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
