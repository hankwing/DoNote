package com.donote.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.baidu.mobstat.StatService;
import com.donote.util.ShowIcon;
import com.wxl.donote.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.SeekBar;

@SuppressLint("NewApi")
public class PlayMusic extends Activity implements SeekBar.OnSeekBarChangeListener {
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		StatService.onPause(this);
		super.onPause();
	}

	private ImageButton recButton;
	private boolean recplay = true;
	private MediaPlayer mPlayer;
	private TextView recInfo;
	private int fileLen;
	private SeekBar recBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_play);
		Intent intent = getIntent();
		final String playRec = intent.getStringExtra("playRec");
		mPlayer = new MediaPlayer();
		try
		{
			mPlayer.setDataSource(playRec);
			mPlayer.prepare();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		recButton = (ImageButton) findViewById(R.id.recButton);
		recInfo = (TextView) findViewById(R.id.recInfo);
		recBar = (SeekBar) findViewById(R.id.reckBar);
		// 获取文件大小
		File dF = new File(playRec);
		FileInputStream fis;
		try
		{
			fis = new FileInputStream(dF);
			fileLen = fis.available();
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// 设置最大值为录音的长度
		recBar.setMax(mPlayer.getDuration());
		recBar.setOnSeekBarChangeListener(this);
		recInfo.setText(getResources().getString(R.string.file_size)+" " + ShowIcon.FormetFileSize(fileLen));
		super.onCreate(savedInstanceState);

	}

	Handler handler = new Handler();

	Runnable updateThread = new Runnable() {
		public void run() {
			// 获得歌曲现在播放位置并设置成播放进度条的值
			recBar.setProgress(mPlayer.getCurrentPosition());
			// 每次延迟100毫秒再启动线程
			handler.postDelayed(updateThread, 100);
		}
	};

	@Override
	protected void onResume() {
		StatService.onResume(this);
		mPlayer.start();
		handler.post(updateThread);
		recplay = false;
		recButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (recplay == true)
				{
					Drawable stop = getResources().getDrawable(
							R.drawable.play_stop);
					recButton.setImageDrawable(stop);
					mPlayer.start();
					handler.post(updateThread);
					recplay = false;
				} else
				{
					Drawable start = getResources().getDrawable(
							R.drawable.play_start);
					recButton.setImageDrawable(start);
					mPlayer.pause();
					handler.removeCallbacks(updateThread);
					recplay = true;

				}

			}
		});
		// TODO Auto-generated method stub

		mPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				PlayMusic.this.finish();
			}
		});
		super.onResume();
	}

	// 拖动中
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (fromUser == true)
		{
			mPlayer.seekTo(progress);
		}
	}

	// 开始拖动
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	// 结束拖动
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			PlayMusic.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		// 结束时记得结束线程
		handler.removeCallbacks(updateThread);
		mPlayer.release();
		mPlayer = null;
		// TODO Auto-generated method stub
		super.finish();
	}

}
