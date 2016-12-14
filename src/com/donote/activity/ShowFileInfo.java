package com.donote.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.baidu.mobstat.StatService;
import com.donote.util.ShowIcon;
import com.wxl.donote.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class ShowFileInfo extends Activity {
	
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

	private TextView nameInfo;
	private TextView sizeInfo;
	private TextView pathInfo;
	private TextView timeInfo;
	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		nameInfo = (TextView) findViewById(R.id.info_name);
		sizeInfo = (TextView) findViewById(R.id.info_size);
		pathInfo = (TextView) findViewById(R.id.info_path);
		timeInfo = (TextView) findViewById(R.id.info_time);
		
		Bundle information = getIntent().getExtras();
		String fileString = information.getString("file");
		
		Log.i("info", fileString);
		
		File file = new File(fileString);
		//获取文件名
		nameInfo.setText(file.getName());
		sizeInfo.setText(String.valueOf(ShowIcon.FormetFileSize(file.length())));
		//获取绝对路径
		pathInfo.setText(file.getAbsolutePath());
		//获取当前文件最后修改时间
		long time = file.lastModified();
		Calendar cal = Calendar.getInstance(); 
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		cal.setTimeInMillis(time);
		timeInfo.setText(formatter.format(cal.getTime()));
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {	
		ShowFileInfo.this.finish();
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

}
