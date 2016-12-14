package com.donote.alarm;


import com.donote.activity.MainActivity;
import com.donote.adapter.NoteDbAdapter;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;

@SuppressWarnings("deprecation")
public class AlarmReceiver extends BroadcastReceiver {

	private Vibrator mVibrator01;
	public NoteDbAdapter mDbHelper;
	@SuppressLint("Wakelock")
	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		/* context.startService(new Intent(context, NotifyService.class)); */
		Intent in = new Intent();	
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock mWakeLock = pm.newWakeLock(
				PowerManager.FULL_WAKE_LOCK
						| PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.ON_AFTER_RELEASE, "My Tag");
		// in onResume() call
		mWakeLock.acquire(30000);
		mVibrator01 = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		mVibrator01.vibrate(new long[] { 100, 10, 100, 1000 }, -1);
		Bundle extras = arg1.getExtras();
		if(MainActivity.mDbHelper != null && MainActivity.mDbHelper.isOpen()) {
			MainActivity.mDbHelper.deleteAlarm(extras.getLong("alarmID"));
			if ( MainActivity.mDbHelper.findAlarmByID(extras.getLong("ID")).getCount() == 0) {
				MainActivity.mDbHelper.cancleAlarmflag(extras.getLong("ID"));
			}
			
		}
		else {
			mDbHelper = new NoteDbAdapter(context);
			mDbHelper.open();
			mDbHelper.deleteAlarm(extras.getLong("alarmID"));
			if ( mDbHelper.findAlarmByID(extras.getLong("ID")).getCount() == 0) {
				mDbHelper.cancleAlarmflag(extras.getLong("ID"));
			}
			mDbHelper.close();
		}
		
		in.putExtra("flag", extras.getInt("flag"));
		in.putExtra("listPos", extras.getInt("listPos"));
		in.putExtra("Position", extras.getInt("Position"));
		in.putExtra("groupPosition", extras.getInt("groupPosition"));
		in.putExtra("childPosition", extras.getInt("childPosition"));
		in.putExtra("title", extras.getString("title"));
		in.putExtra("body", extras.getString("body"));
		in.putExtra("ID", extras.getLong("ID"));
		in.putExtra("alarmID", extras.getLong("alarmID"));
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		in.setClass(context, AlarmDialog.class);
		context.startActivity(in);	
	}
	
}
