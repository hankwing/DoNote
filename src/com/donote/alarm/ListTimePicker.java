package com.donote.alarm;

import java.util.Calendar;

import com.baidu.mobstat.StatService;
import com.wxl.donote.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

public class ListTimePicker extends Activity {
	
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

	Calendar calendar;
	ImageButton confirmButton;
	ImageButton cancleButton;
	private TextView timeText;
	private TextView dateText;
	private Bundle extras;
	private int year, month, day, hour, minute1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.time_picker);
		calendar = Calendar.getInstance();
		timeText = (TextView)findViewById(R.id.time_time);
		dateText = (TextView)findViewById(R.id.time_date);
		confirmButton = (ImageButton) findViewById(R.id.time_confirm);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH)+1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute1 = calendar.get(Calendar.MINUTE);
		dateText.setText(year + getResources().getString(R.string.year) + month + getResources().getString(R.string.month)
				+ day + getResources().getString(R.string.day));
		timeText.setText(hour + ":" + minute1);
		
		confirmButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				extras = getIntent().getExtras();
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
                intent.putExtra("groupPosition", groupPosition);
                intent.putExtra("childPosition", childPosition);
				// ·µ»Øintent
                setResult(RESULT_OK, intent);
                finish();
			}
		});
		
		cancleButton = (ImageButton) findViewById(R.id.time_cancle);
		cancleButton.setOnClickListener(new OnClickListener(){
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
				new DatePickerDialog(ListTimePicker.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								dateText.setText(year + getResources().getString(R.string.year) + (monthOfYear+1) + getResources().getString(R.string.month)
										+ dayOfMonth + getResources().getString(R.string.day));
								ListTimePicker.this.year = year;
								month = monthOfYear+1;
								day = dayOfMonth;
							}
						}, year, month -1, day).show();
			}
		});

		timeText.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new TimePickerDialog(ListTimePicker.this,
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

	}

}