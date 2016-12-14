package com.donote.util;

import com.donote.activity.SplashActivity;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.FloatMath;

@SuppressWarnings("deprecation")
public class MyShakeBootService extends Service implements SensorEventListener {
	SensorManager sm = null;
	SharedPreferences settings;
	/**
	 * 检测的时间间隔
	 */
	static final int UPDATE_INTERVAL = 70;
	/**
	 * 上一次检测的时间
	 */
	long mLastUpdateTime;
	/**
	 * 上一次检测时，加速度在x、y、z方向上的分量，用于和当前加速度比较求差。
	 */
	float mLastX, mLastY, mLastZ;
	public int shakeThreshold = 1000;
	private MyBinder myBinder = new MyBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		settings = getSharedPreferences("shake_info", 0);
		if(settings.getInt("isshakeboot", 1) == 1) {
			sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		else {
			stopSelf();
		}
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public class MyBinder extends Binder {
		MyShakeBootService getService() {
			return MyShakeBootService.this;
		}
	}
	private void StartGesture() {
		Intent intent = new Intent(MyShakeBootService.this,
				SplashActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("flag", 2);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sm.unregisterListener(this);
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@SuppressLint("FloatMath")
	@Override
	public void onSensorChanged(SensorEvent event) {
		shakeThreshold = settings.getInt("sensity", 2000);
		long currentTime = System.currentTimeMillis();
		long diffTime = currentTime - mLastUpdateTime;
		if (diffTime < UPDATE_INTERVAL)
			return;
		mLastUpdateTime = currentTime;
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		float deltaX = x - mLastX;
		float deltaY = y - mLastY;
		float deltaZ = z - mLastZ;
		mLastX = x;
		mLastY = y;
		mLastZ = z;
		float delta = FloatMath.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
				* deltaZ)
				/ diffTime * 10000;
		if (delta > shakeThreshold)
		{ // 当加速度的差值大于指定的阈值，认为这是一个摇晃
			//Log.i("bae", " " + shakeThreshold);
			StartGesture();
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
