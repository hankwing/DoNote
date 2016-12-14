package com.donote.alarm;

import com.baidu.mobstat.StatService;
import com.donote.activity.MainActivity;
import com.donote.adapter.NoteDbAdapter;
import com.wxl.donote.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ContentTimePicker extends Activity {

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

	private TextView timeText;
	private TextView dateText;
	private EditText content;
	private ImageButton confirm;
	private ImageButton cancle;
	private String initTimeString;
	private Bundle extras;
	private int year, month, day, hour, minute1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contenttimepicker);
		dateText = (TextView) findViewById(R.id.content_date);
		timeText = (TextView) findViewById(R.id.content_time);
		confirm = (ImageButton) findViewById(R.id.content_time_confirm);
		cancle = (ImageButton) findViewById(R.id.content_time_cancle);
		content = (EditText) findViewById(R.id.content_content);
		extras = getIntent().getExtras();
		Cursor alarmCursor = MainActivity.mDbHelper.getalarm(extras
				.getLong("ID"));
		if (alarmCursor != null)
		{
			String dateAndtimeString = alarmCursor.getString(alarmCursor
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMTIME));
			timeText.setText(dateAndtimeString.substring(9, 14));
			year = 2000 + Integer.valueOf(dateAndtimeString.substring(0, 2));
			month = Integer.valueOf(dateAndtimeString.substring(3, 5));
			day = Integer.valueOf(dateAndtimeString.substring(6, 8));

			initTimeString = (String) timeText.getText();
			content.setText(alarmCursor.getString(alarmCursor
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMCATA)));
			alarmCursor.close();

			dateText.setText(year + getResources().getString(R.string.year)
					+ month + getResources().getString(R.string.month) + day
					+ getResources().getString(R.string.day));
			hour = Integer.valueOf(initTimeString.substring(0, 2));
			minute1 = Integer.valueOf(initTimeString.substring(3, 5));
			cancle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			dateText.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new DatePickerDialog(ContentTimePicker.this,
							new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									dateText.setText(year + getResources().getString(R.string.year)
											+ (monthOfYear + 1) + getResources().getString(R.string.month)
											+ dayOfMonth + getResources().getString(R.string.day));
									ContentTimePicker.this.year = year;
									month = monthOfYear + 1;
									day = dayOfMonth;
								}
							}, year, month - 1, day).show();
				}
			});

			timeText.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					new TimePickerDialog(ContentTimePicker.this,
							new TimePickerDialog.OnTimeSetListener() {
								@Override
								public void onTimeSet(TimePicker view,
										int hourOfDay, int minute) {
									// TODO Auto-generated method stub
									timeText.setText(hourOfDay + ":" + minute);
									hour = hourOfDay;
									minute1 = minute;
								}
							}, hour, minute1, true).show();
				}

			});

			confirm.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Long ID = extras.getLong("ID");
					int position = extras.getInt("Position");
					int groupPosition = extras.getInt("GroupPosition");
					int childPosition = extras.getInt("ChildPosition");
					Intent intent = new Intent();
					intent.putExtra("year", year);
					intent.putExtra("month", month);
					intent.putExtra("day", day);
					intent.putExtra("hour", hour);
					intent.putExtra("minute", minute1);
					intent.putExtra("ID", ID);
					intent.putExtra("Position", position);
					intent.putExtra("content", content.getText().toString());
					intent.putExtra("groupPosition", groupPosition);
					intent.putExtra("childPosition", childPosition);
					// ·µ»Øintent
					setResult(RESULT_OK, intent);
					finish();
				}
			});
		} else
		{
			Toast.makeText(ContentTimePicker.this, getResources().getString(R.string.alarm_not_exist), Toast.LENGTH_SHORT)
					.show();
			finish();
		}
	}

}
