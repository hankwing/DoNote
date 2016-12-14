package com.donote.activity;

import java.util.Calendar;

import com.baidu.mobstat.StatActivity;
import com.baidu.mobstat.StatService;
import com.donote.adapter.MyDetectAlarmAdapter;
import com.donote.adapter.NoteDbAdapter;
import com.donote.alarm.AlarmReceiver;
import com.donote.alarm.ContentTimePicker;
import com.wxl.donote.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DetectAlarmManager extends Activity {
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
		StatService.onResume(this);
		// TODO Auto-generated method stub
		super.onResume();
	}

	private Cursor[] cursor;
	private Cursor idListCursor;
	private long[] idList = new long[100];
	private MyDetectAlarmAdapter alarmAdapter;
	private ListView listView;
	private Button cancle;
	private Button confirm;
	private Bundle extrasBundle;
	private AlarmManager alarm_service;
	private long noteID;
	private int number;
	private View footerView;

	public final class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			idListCursor.moveToPosition(position);
			Long alarmID = idListCursor.getLong(idListCursor
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
			Intent time_picker;
			time_picker = new Intent(DetectAlarmManager.this,
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
				Cursor alarmCursor = MainActivity.mDbHelper.getalarm(extras
						.getLong("ID"));
				if (alarmCursor != null)
				{
					long noteID = alarmCursor.getLong(alarmCursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALARMID));
					Cursor noteCursor = MainActivity.mDbHelper.getnote(noteID);
					int year = extras.getInt("year");
					int month = extras.getInt("month");
					int day = extras.getInt("day");
					int hour = extras.getInt("hour");
					int minute = extras.getInt("minute");
					Intent intent = new Intent(this, AlarmReceiver.class);
					intent.putExtra("title", noteCursor.getString(noteCursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
					intent.putExtra("Position", extras.getInt("Position"));
					intent.putExtra("ID", noteID);
					Calendar calendar = Calendar.getInstance();
					Calendar anotherCalendar = Calendar.getInstance();
					anotherCalendar.set(year, month-1, day, hour, minute, 0);
					if (calendar.compareTo(anotherCalendar) == -1)
					{
						calendar.set(year, month-1, day, hour, minute, 0);
					} else
					{
						Toast.makeText(DetectAlarmManager.this, getResources().getString(R.string.time_passed),
								Toast.LENGTH_SHORT).show();
						return;
					}
					long alarmId = alarmCursor.getLong(alarmCursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
					MainActivity.mDbHelper.updateAlarm(alarmId, noteID,
							calendar, extras.getString("content"));
					alarmCursor = MainActivity.mDbHelper.getalarm(extras
							.getLong("ID"));
					intent.putExtra("body", alarmCursor.getString(alarmCursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMCATA)));
					intent.putExtra("alarmID", alarmId);
					PendingIntent p_intent = PendingIntent.getBroadcast(this,
							(int) alarmId, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					// Schedule the alarm!
					alarm_service.set(AlarmManager.RTC_WAKEUP,
							calendar.getTimeInMillis(), p_intent);
					for (int i = 1; i <= number; i++)
					{
						cursor[i - 1] = MainActivity.mDbHelper
								.findAlarmByAlarmID(idList[i - 1]);
					}
					idListCursor = new MergeCursor(cursor);
					alarmAdapter = new MyDetectAlarmAdapter(
							DetectAlarmManager.this, idListCursor, idList,
							number);
					listView.setAdapter(alarmAdapter);
					alarmCursor.close();
					noteCursor.close();
				} else
				{
					Toast.makeText(DetectAlarmManager.this, getResources().getString(R.string.alarm_not_exist),
							Toast.LENGTH_SHORT).show();
				}
			}// If
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getResources().getString(R.string.detect_alarm));
		setContentView(R.layout.activity_alarm_manager);
		extrasBundle = getIntent().getExtras();
		if(extrasBundle != null) {
			idList = extrasBundle.getBundle("idlist").getLongArray("idlist");
			number = extrasBundle.getInt("number");
			cursor = new Cursor[number];
		
			for (int i = 1; i <= number; i++)
			{
				cursor[i - 1] = MainActivity.mDbHelper
						.findAlarmByAlarmID(idList[i - 1]);
			}
		}
		idListCursor = new MergeCursor(cursor);
		noteID = extrasBundle.getLong("NoteID");
		footerView = ((LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.detect_alarm_footer, null);
		alarm_service = (AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);
		cancle = (Button) footerView.findViewById(R.id.alarm_confirm);
		cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!MainActivity.mDbHelper.isAlarmAllUnChacked())
				{			
					MainActivity.mDbHelper.deleteAlarmChecked();
				}
				if (MainActivity.mDbHelper.findAlarmByID(noteID).getCount() > 0)
				{
					MainActivity.mDbHelper.updateAlarmflag(noteID);
				}
				finish();
			}
		});

		confirm = (Button) footerView.findViewById(R.id.alarm_batch_cancle);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				idListCursor.moveToFirst();
				while (!idListCursor.isAfterLast())
				{				
					long alarmID = idListCursor.getLong(idListCursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
					MainActivity.mDbHelper.deleteAlarm(alarmID);
					idListCursor.moveToNext();
				}

				if (MainActivity.mDbHelper.findAlarmByID(noteID).getCount() > 0)
				{
					MainActivity.mDbHelper.updateAlarmflag(noteID);
					finish();
				} else
				{
					MainActivity.mDbHelper.cancleAlarmflag(noteID);
					finish();
				}

			}
		});

		listView = (ListView) findViewById(R.id.alarm_list);
		listView.setOnItemClickListener(new ItemClickListener());
		listView.addFooterView(footerView);
		alarmAdapter = new MyDetectAlarmAdapter(DetectAlarmManager.this,
				idListCursor, idList, number);
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
		if (idListCursor != null)
		{
			idListCursor.close();
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
