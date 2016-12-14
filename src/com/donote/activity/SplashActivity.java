package com.donote.activity;


import com.baidu.mobstat.StatService;
import com.wxl.donote.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashActivity extends Activity {
@SuppressWarnings("deprecation")
@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		setContentView(R.layout.splash_screen);
		//Display the current version number
		new Handler().postDelayed(new Runnable() {
			public void run() {
				SharedPreferences settings = getSharedPreferences("runTime", 0);
				SharedPreferences account = getSharedPreferences("account", 0);
				
				if(settings.getInt("time", 0)==0) {
					Intent intent = new Intent(SplashActivity.this, GuideView.class);
					SplashActivity.this.startActivity(intent);
					settings.edit().putInt("time", 1).commit();
					SplashActivity.this.finish();
					overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
				}
//				else if(account.getString("account_name", null) == null) {
//					Intent mainIntent = new Intent(SplashActivity.this, Login.class);
//					startActivity(mainIntent);
//					finish();
//					overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
//				}
				else {
					Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
					if(getIntent().getExtras() != null) {
						mainIntent.putExtra("flag", getIntent().getExtras().getInt("flag"));
					}
					SplashActivity.this.startActivity(mainIntent);
					SplashActivity.this.finish();
					//overridePendingTransition(R.anim.zoomin, R.anim.zoomout);  
					//overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
					overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
				}
			}
		}, 380);
	}

/* (non-Javadoc)
 * @see android.app.Activity#onPause()
 */
@Override
protected void onPause() {
	// TODO Auto-generated method stub
	StatService.onPause(this);
	super.onPause();
}

/* (non-Javadoc)
 * @see android.app.Activity#onResume()
 */
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	StatService.onResume(this);
	super.onResume();
}
}