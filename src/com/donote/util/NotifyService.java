package com.donote.util;

import com.donote.activity.NoteSet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotifyService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Intent in = new Intent();  
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
		in.setClass(getApplicationContext(),NoteSet.class);  
	}
}
