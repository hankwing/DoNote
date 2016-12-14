package com.donote.alarm;

import java.util.Calendar;

import com.baidu.mobstat.StatService;
import com.donote.activity.MainActivity;
import com.donote.adapter.MyAlarmSimpleCursorAdapter;
import com.donote.adapter.NoteDbAdapter;
import com.wxl.donote.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AlarmSet extends Activity {

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

	private Cursor alarmCursor;
	private MyAlarmSimpleCursorAdapter alarmAdapter;
	private ListView listView;
	private Button cancle;
	private Button confirm;
	private Bundle extrasBundle;
	private AlarmManager alarm_service;
	private long noteID;
	private View footerView;

	public final class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			alarmCursor.moveToPosition(position);
			Long alarmID = alarmCursor.getLong(alarmCursor
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
			Intent time_picker;
			time_picker = new Intent(AlarmSet.this,
					ContentTimePicker.class);
			time_picker.putExtra("Position", position);
			time_picker.putExtra("ID", alarmID);
			startActivityForResult(time_picker, 2);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == 2 && resultCode == RESULT_OK)
		{
			Bundle extras = data.getExtras();
			if (extras != null)
			{
				Cursor cursor = MainActivity.mDbHelper.getalarm(extras
						.getLong("ID"));
				if (cursor != null)
				{
					long noteID = cursor.getLong(cursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALARMID));
					Cursor noteCursor = MainActivity.mDbHelper.getnote(noteID);
					int year = extras.getInt("year");
					int month = extras.getInt("month");
					int day = extras.getInt("day");
					int hour = extras.getInt("hour");
					int minute = extras.getInt("minute");
					Calendar calendar = Calendar.getInstance();
					Calendar anotherCalendar = Calendar.getInstance();
					anotherCalendar.set(year, month-1, day, hour, minute, 0);
					Intent intent = new Intent(this, AlarmReceiver.class);
					intent.putExtra("title", noteCursor.getString(noteCursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
					long alarmId = cursor.getLong(cursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
					if(calendar.compareTo(anotherCalendar)==-1) {
						calendar.set(year, month-1, day, hour, minute, 0);
					}
					else {
						Toast.makeText(AlarmSet.this, getResources().getString(R.string.time_passed),
								Toast.LENGTH_SHORT).show();
						return;
					}
					
					MainActivity.mDbHelper.updateAlarm(alarmId, noteID,
							calendar ,extras.getString("content"));
					cursor = MainActivity.mDbHelper.getalarm(extras
							.getLong("ID"));
					if (cursor
							.getString(
									cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMCATA))
							.equals(getResources().getString(R.string.whole_note)))
					{				
						intent.putExtra("body", noteCursor.getString(noteCursor
								.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
					} else
					{
						//Log.i("alarm", "body " + cursor.getString(cursor
								//.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMCATA)));
						intent.putExtra(
								"body",
								cursor.getString(cursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMCATA)));
					}
					intent.putExtra("Position", extras.getInt("Position"));
					intent.putExtra("ID", noteID);				
					intent.putExtra("alarmID", alarmId);
					PendingIntent p_intent = PendingIntent.getBroadcast(this,
							(int) alarmId, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					// Schedule the alarm!
					alarm_service.set(AlarmManager.RTC_WAKEUP,
							calendar.getTimeInMillis(), p_intent);
					Toast.makeText(this, getResources().getString(R.string.alarm_modified), Toast.LENGTH_LONG).show();
					alarmCursor = MainActivity.mDbHelper.findAlarmByID(noteID);
					alarmAdapter = new MyAlarmSimpleCursorAdapter(
							AlarmSet.this, alarmCursor);
					listView.setAdapter(alarmAdapter);
					cursor.close();
					noteCursor.close();
				} else
				{
					Toast.makeText(AlarmSet.this, getResources().getString(R.string.alarm_not_exist),
							Toast.LENGTH_SHORT).show();
				}
			}// If
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarm_manager);

		extrasBundle = getIntent().getExtras();
		noteID = extrasBundle.getLong("NoteID");

		footerView = ((LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.alarm_footer, null);

		alarm_service = (AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);
		cancle = (Button) footerView.findViewById(R.id.alarm_return);
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.mDbHelper.cancleAlarmALLChecked();
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		confirm = (Button) footerView.findViewById(R.id.alarm_batch_cancle);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!MainActivity.mDbHelper.isAlarmAllUnChacked())
				{
					Cursor cursor = MainActivity.mDbHelper.getCheckedAlarm();
					while (!cursor.isAfterLast())
					{
						Intent i = new Intent(AlarmSet.this,
								AlarmReceiver.class);
						PendingIntent pi = PendingIntent.getBroadcast(
								AlarmSet.this,
								(int) cursor.getLong(cursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID)),
								i, 0);
						alarm_service.cancel(pi);// 取消闹钟
						cursor.moveToNext();
					}
					MainActivity.mDbHelper.deleteAlarmChecked();

					if (MainActivity.mDbHelper.findAlarmByID(noteID).getCount() == 0)
					{
						cursor.close();
						Toast.makeText(AlarmSet.this, getResources().getString(R.string.cancel_succeed),
								Toast.LENGTH_SHORT).show();
						if (extrasBundle.getInt("alarmFlag") == 1)
						{// 说明此修改闹钟请求来自第二屏，此时应该取消闹钟图标
							Intent intent = new Intent();
							intent.putExtra("groupPosition",
									extrasBundle.getInt("groupPosition"));
							intent.putExtra("childPosition",
									extrasBundle.getInt("childPosition"));
							MainActivity.mDbHelper.cancleAlarmflag(noteID);
							setResult(RESULT_OK, intent);
							finish();
						} else
						{
							MainActivity.mDbHelper.cancleAlarmflag(noteID);
							Intent intent = new Intent();
							setResult(RESULT_FIRST_USER, intent);
							finish();
						}
					} else
					{
						cursor.close();
						Toast.makeText(AlarmSet.this, getResources().getString(R.string.cancel_succeed),
								Toast.LENGTH_SHORT).show();
						setResult(RESULT_CANCELED);
						finish();
					}
				} else
				{
					Toast.makeText(AlarmSet.this, getResources().getString(R.string.choose_alarm),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		listView = (ListView) findViewById(R.id.alarm_list);
		listView.addFooterView(footerView);
		listView.setOnItemClickListener(new ItemClickListener());
		alarmCursor = MainActivity.mDbHelper.findAlarmByID(extrasBundle
				.getLong("NoteID"));
		alarmAdapter = new MyAlarmSimpleCursorAdapter(AlarmSet.this,
				alarmCursor);
		listView.setAdapter(alarmAdapter);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (alarmCursor != null)
		{
			alarmCursor.close();
		}
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			MainActivity.mDbHelper.cancleAlarmALLChecked();
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * private void initListView() {
	 * 
	 * alarmCursor = MainActivity.mDbHelper.findAlarmByID(extrasBundle
	 * .getLong("NoteID")); alarmAdapter = new
	 * myAlarmSimpleCursorAdapter(alarmManager.this, alarmCursor);
	 * listView.setAdapter(alarmAdapter); }
	 */

}
