package com.donote.activity;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import com.baidu.mobstat.StatService;
import com.wxl.donote.R;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

public class NoteRecord extends Activity {

	// ================================录音文本=======================
	private String path = null;
	private MediaRecorder mediaRecorder = null;
	private File myFile = null; // 指定的文件
	private File mydirFile = null; // 指定的文件夹
	private Button recordStart = null;
	private String name = null;
	private Vibrator vibrator;
	private boolean isClick = false;

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

	// ============================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_record);
		// Make dictionary!
		path = Environment.getExternalStorageDirectory().getPath() + "/"
				+ "DoNote" + "/" + "record" + "/";
		mydirFile = new File(path);
		mydirFile.mkdirs();

		// Button Listener!
		recordStart = (Button) this.findViewById(R.id.record_start);
		recordStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 实例化 Bundle，设置需要传递的参数
				if(!isClick){
					vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					recordStart.setText("结束录音");
					try {
						Toast.makeText(NoteRecord.this, getResources().getString(R.string.begin_record), Toast.LENGTH_SHORT)
						.show();
						vibrator.vibrate(200);
						new DateFormat();
						name = DateFormat.format("yyyyMMdd_hhmmss",
								Calendar.getInstance(Locale.CHINA))
								+ ".amr";
						myFile = new File(path + name);
						myFile.createNewFile();

						mediaRecorder = new MediaRecorder();
						if (mediaRecorder != null) {
							mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
							mediaRecorder
							.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
							mediaRecorder
							.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
							mediaRecorder.setOutputFile(myFile.getAbsolutePath());
							mediaRecorder.prepare();
							mediaRecorder.start();
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch(IllegalStateException e) {
						Toast.makeText(NoteRecord.this, "录音失败 请重试", Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
					isClick = true;
				}else{
					if ( mediaRecorder != null) {
						try
						{
							mediaRecorder.stop();
						} catch (Exception e)
						{
							Toast.makeText(NoteRecord.this, "录音失败 请重试", Toast.LENGTH_SHORT).show();
							e.printStackTrace();
							// TODO: handle exception
						}

						mediaRecorder.release();
						mediaRecorder = null;
						Toast.makeText(NoteRecord.this, getResources().getString(R.string.end_record), Toast.LENGTH_SHORT)
						.show();
						NoteRecord.this.finish();
					}
					NoteRecord.this.finish();
				}
			}
		});
		super.onCreate(savedInstanceState);
	}


	@Override
	public void finish() {
		if (myFile == null) {
			NoteRecord.this.setResult(0);
		} else {
			Bundle bundle = new Bundle();

			bundle.putString("name", myFile.getAbsolutePath());

			NoteRecord.this.setResult(14, NoteRecord.this.getIntent()
					.putExtras(bundle));
		}
		// TODO Auto-generated method stub
		super.finish();
	}
}
