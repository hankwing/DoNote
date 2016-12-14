package com.donote.alarm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.mobstat.StatService;
import com.donote.activity.CommonEdit;
import com.donote.activity.MainActivity;
import com.donote.activity.SplashActivity;
import com.donote.adapter.NoteDbAdapter;
import com.donote.util.Expressions;
import com.donote.util.ShowIcon;
import com.wxl.donote.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

@SuppressWarnings("deprecation")
@SuppressLint("HandlerLeak")
public class AlarmDialog extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		StatService.onResume(this);
		super.onResume();
	}

	// private KeyguardLock mKeyguardLock;
	private TextView title;
	private TextView body;
	private ImageButton exitButton;
	private ShowIcon showIcon;
	// private KeyguardManager mKeyguardManager = null;
	/*
	 * private Bitmap photo; private Bitmap picture;
	 */
	// private Bitmap draw;
	private Bitmap face;
	private int[] expressionImages;
	private int[] expressionImages1;
	private int[] expressionImages2;
	/*
	 * private String pathPhoto =
	 * Environment.getExternalStorageDirectory().getPath() + "/" + "DoNote" +
	 * "/" + "photo" + "/"; private String pathRecord =
	 * Environment.getExternalStorageDirectory().getPath() + "/" + "DoNote" +
	 * "/" + "record" + "/"; private String pathPicture =
	 * Environment.getExternalStorageDirectory().getPath() + "/" + "DoNote" +
	 * "/" + "picture" + "/"; private String pathDraw =
	 * Environment.getExternalStorageDirectory().getPath() + "/" + "DoNote" +
	 * "/" + "draw" + "/";
	 */
	private String notify_title;
	private float Height = 0;
	private float Width = 0;
	private SpannableString ps;
	private Matcher photoMatcher;
	private Thread imageThread;
	private String exit_body;
	private Bundle extras;
	private NotificationManager mNotificationManager;
	private RelativeLayout mainLayout;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what)
			{
			case 1:
				body.setText((SpannableString) msg.obj);
				break;
			case 2:
				handler.removeCallbacks(imageThread);
			}
		}
	};

	// ActivityManager am;

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarm);
		mainLayout = (RelativeLayout) findViewById(R.id.alarm_dialog);
		extras = getIntent().getExtras();
		title = (TextView) findViewById(R.id.alarm_title);
		body = (TextView) findViewById(R.id.alarm_body);
		exitButton = (ImageButton) findViewById(R.id.alarm_exit);
		exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mNotificationManager.cancel((int) extras.getLong("alarmID"));
				finish();
			}
		});
		DisplayMetrics Win = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(Win);
		android.view.ViewGroup.LayoutParams params = mainLayout
				.getLayoutParams();
		params.width = (int) (Win.widthPixels * 0.8);
		params.height = (int) (Win.heightPixels * 0.6);
		mainLayout.setLayoutParams(params);
		Width = ShowIcon.dip2px(this, (int) (Win.widthPixels * 0.8));
		Height = ShowIcon.dip2px(this, (int) (Win.heightPixels * 0.6));
		exit_body = extras.getString(NoteDbAdapter.KEY_BODY);
		showIcon = new ShowIcon(AlarmDialog.this, Width, Height);
		/*
		 * mKeyguardManager = (KeyguardManager) this
		 * .getSystemService(Context.KEYGUARD_SERVICE); mKeyguardLock =
		 * mKeyguardManager.newKeyguardLock("donote");
		 * mKeyguardLock.disableKeyguard();
		 */
		expressionImages = Expressions.expressionImgs;
		// expressionImageNames = Expressions.expressionImgNames;
		expressionImages1 = Expressions.expressionImgs1;
		// expressionImageNames1 = Expressions.expressionImgNames1;
		expressionImages2 = Expressions.expressionImgs2;
		// expressionImageNames2 = Expressions.expressionImgNames2;
		exit_body = exit_body.replaceAll(
				"((Text|Table){1}\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)", "");
		if (exit_body != null)
		{
			ps = new SpannableString(exit_body);
			// 判断Photo
			Pattern photoPattern = Pattern
					.compile("(Photo\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)");
			photoMatcher = photoPattern.matcher(exit_body);
			while (photoMatcher.find())
			{
				ps.setSpan(showIcon.getDefaultImage(photoMatcher),
						photoMatcher.start(), photoMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			body.setText(ps);

			// 判断录音
			Pattern recordPattern = Pattern
					.compile("(Record\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)");
			Matcher recordMatcher = recordPattern.matcher(exit_body);
			while (recordMatcher.find())
			{
				String idString = recordMatcher.group();
				idString = idString.substring(
						idString.indexOf("Record^_^[") + 10,
						idString.indexOf("]^_^"));

				ps.setSpan(showIcon.getRecordImage(recordMatcher, idString),
						recordMatcher.start(), recordMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			body.setText(ps);

			// 判断画图*********************************需要修改
			Pattern drawPattern = Pattern
					.compile("(Draw\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)");
			Matcher drawMatcher = drawPattern.matcher(exit_body);
			while (drawMatcher.find())
			{
				String idString = drawMatcher.group();
				idString = idString.substring(idString.indexOf("Draw^_^[") + 8,
						idString.indexOf("]^_^"));
				ps.setSpan(showIcon.getDefaultImage(drawMatcher),
						drawMatcher.start(), drawMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			body.setText(ps);

			// 判断附件
			Pattern filePattern = Pattern
					.compile("(File\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)");
			Matcher fileMatcher = filePattern.matcher(exit_body);
			while (fileMatcher.find())
			{
				String idString = fileMatcher.group();
				idString = idString.substring(idString.indexOf("File^_^[") + 8,
						idString.indexOf("]^_^"));
				ps.setSpan(showIcon.getFileImage(fileMatcher, idString),
						fileMatcher.start(), fileMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			body.setText(ps);

			// 判断图片
			Pattern picturePattern = Pattern
					.compile("(Picture\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)");
			Matcher pictureMatcher = picturePattern.matcher(exit_body);
			while (pictureMatcher.find())
			{
				ps.setSpan(showIcon.getDefaultImage(pictureMatcher),
						pictureMatcher.start(), pictureMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			body.setText(ps);

			// 判断手写
			Pattern gesturePattern = Pattern
					.compile("(Gesture\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)");
			Matcher gestureMatcher = gesturePattern.matcher(exit_body);
			while (gestureMatcher.find())
			{
				ps.setSpan(showIcon.getDefaultImage(gestureMatcher),
						gestureMatcher.start(), gestureMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			body.setText(ps);

			// 判断视频
			Pattern videoPattern = Pattern
					.compile("(Video\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)");
			Matcher videoMatcher = videoPattern.matcher(exit_body);
			while (videoMatcher.find())
			{
				ps.setSpan(com.donote.util.ShowIcon
						.getDefaultVideoImage(videoMatcher), videoMatcher
						.start(), videoMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			body.setText(ps);

			Pattern freeFacePattern = Pattern
					.compile("(Face\\^\\_\\^\\[(.*?)\\]\\[(.*?)\\]\\^\\_\\^)");
			Matcher freeFaceMatcher = freeFacePattern.matcher(exit_body);
			while (freeFaceMatcher.find())
			{
				String idString = freeFaceMatcher.group();
				idString = idString.substring(idString.indexOf("Face^_^[") + 9,
						idString.indexOf("]["));

				face = null;
				switch (Integer.parseInt(idString) / expressionImages1.length)
				{
				case 0:
					face = BitmapFactory.decodeResource(getResources(),
							expressionImages[Integer.parseInt(idString)
									% expressionImages.length]);
					break;
				case 1:
					face = BitmapFactory.decodeResource(getResources(),
							expressionImages1[Integer.parseInt(idString)
									% expressionImages1.length]);
					break;
				case 2:
					face = BitmapFactory.decodeResource(getResources(),
							expressionImages2[Integer.parseInt(idString)
									% expressionImages2.length]);
					break;
				}
				Drawable drawable = new BitmapDrawable(getResources(), face);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				ImageSpan faceSpan = new ImageSpan(AlarmDialog.this, face);
				ps.setSpan(faceSpan, freeFaceMatcher.start(),
						freeFaceMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			}

			// 判断表情
			Pattern facePattern = Pattern.compile("Face:f" + "\\w{3}");
			Matcher faceMatcher = facePattern.matcher(exit_body);
			while (faceMatcher.find())
			{
				String idString = faceMatcher.group();
				idString = idString.substring(idString.indexOf(":") + 2,
						idString.length());
				face = null;
				switch (Integer.parseInt(idString) / expressionImages1.length)
				{
				case 0:
					face = BitmapFactory.decodeResource(getResources(),
							expressionImages[Integer.parseInt(idString)
									% expressionImages.length]);
					break;
				case 1:
					face = BitmapFactory.decodeResource(getResources(),
							expressionImages1[Integer.parseInt(idString)
									% expressionImages1.length]);
					break;
				case 2:
					face = BitmapFactory.decodeResource(getResources(),
							expressionImages2[Integer.parseInt(idString)
									% expressionImages2.length]);
					break;
				}
				Drawable drawable = new BitmapDrawable(getResources(), face);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				ImageSpan faceSpan = new ImageSpan(AlarmDialog.this, face);
				ps.setSpan(faceSpan, faceMatcher.start(), faceMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			body.setText(ps);

		}
		imageThread = new Thread(new imageLoadThread());
		imageThread.start();// 启动线程
		title.setText(extras.getString("title"));
		notify_title = extras.getString("title");
		title.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Intent mainIntent = new Intent(AlarmDialog.this,
						MainActivity.class);
				ActivityManager am = (ActivityManager) AlarmDialog.this
						.getSystemService(ACTIVITY_SERVICE);
				List<RunningTaskInfo> appTask = am.getRunningTasks(1);
				mNotificationManager.cancel((int) extras.getLong("alarmID"));

				if (appTask.size() > 0
						&& appTask.get(0).baseActivity.equals(mainIntent
								.getComponent()))
				{
					Cursor c = MainActivity.mDbHelper.getnote(extras
							.getLong("ID"));
					Intent in = new Intent(AlarmDialog.this, CommonEdit.class);
					in.putExtra(NoteDbAdapter.KEY_ROWID, extras.getLong("ID"));
					in.putExtra(NoteDbAdapter.KEY_TITLE, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
					in.putExtra(NoteDbAdapter.KEY_BODY, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
					in.putExtra(NoteDbAdapter.KEY_CATAGORY, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_CATAGORY)));
					startActivityForResult(in, 1);
					c.close();
					return false;
				}
				Intent i = new Intent(AlarmDialog.this, MainActivity.class);
				i.putExtra(NoteDbAdapter.KEY_ROWID, extras.getLong("ID"));
				i.putExtra("flag", 1);
				startActivityForResult(i, 1);
				return false;
			}
		});

		body.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Intent mainIntent = new Intent(AlarmDialog.this,
						MainActivity.class);
				ActivityManager am = (ActivityManager) AlarmDialog.this
						.getSystemService(ACTIVITY_SERVICE);
				List<RunningTaskInfo> appTask = am.getRunningTasks(1);
				mNotificationManager.cancel((int) extras.getLong("alarmID"));

				if (appTask.size() > 0
						&& appTask.get(0).baseActivity.equals(mainIntent
								.getComponent()))
				{

					Cursor c = MainActivity.mDbHelper.getnote(extras
							.getLong("ID"));

					Intent in = new Intent(AlarmDialog.this, CommonEdit.class);
					in.putExtra(NoteDbAdapter.KEY_ROWID, extras.getLong("ID"));
					in.putExtra(NoteDbAdapter.KEY_TITLE, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
					in.putExtra(NoteDbAdapter.KEY_BODY, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
					in.putExtra(NoteDbAdapter.KEY_CATAGORY, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_CATAGORY)));
					startActivityForResult(in, 1);
					c.close();
					return false;
				}
				Intent i = new Intent(AlarmDialog.this, MainActivity.class);
				i.putExtra(NoteDbAdapter.KEY_ROWID, extras.getLong("ID"));
				i.putExtra("flag", 1);
				startActivityForResult(i, 1);
				return false;
			}
		});
		NotifyService();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// mKeyguardLock.reenableKeyguard();
		StatService.onPause(this);
		super.onPause();
	}

	private void NotifyService() {
		// TODO Auto-generated method stub
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_notification; // 通知图标
		CharSequence tickerText = getResources()
				.getString(R.string.alarm_title); // 状态栏(Status Bar)显示的通知文本提示
		long when = System.currentTimeMillis(); // 通知产生的时间，会在通知信息里显示
		Notification notification = new Notification(icon, tickerText, when);
		CharSequence contentTitle = tickerText;
		CharSequence contentText = notify_title;
		Intent notificationIntent = new Intent(AlarmDialog.this,
				SplashActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(AlarmDialog.this, contentTitle,
				contentText, contentIntent);
		notification.tickerText = tickerText;
		// LED灯闪烁
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.ledARGB = 0xff00ff00;
		notification.ledOnMS = 500;
		notification.ledOffMS = 400;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_INSISTENT;
		SharedPreferences settings = getSharedPreferences("alarm_sound_info", 0);
		if (settings.getString("alarm_info", "").equals(""))
		{
			notification.defaults |= Notification.DEFAULT_SOUND;
		} else
		{
			notification.sound = Uri.parse("file://"
					+ settings.getString("alarm_info", ""));
		}

		mNotificationManager.notify((int) extras.getLong("alarmID"),
				notification);

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
		if (requestCode == 1)
		{
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 图片刷新线程
	class imageLoadThread implements Runnable {
		public void run() {
			// 判断Photo
			Pattern photoPattern = Pattern
					.compile("(Photo\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)");
			Matcher photoMatcher = photoPattern.matcher(exit_body);

			while (photoMatcher.find())
			{
				String idString = photoMatcher.group();
				if (photoMatcher.group(2).contains("]["))
				{
					idString = idString.substring(
							idString.indexOf("Photo^_^[") + 9,
							idString.indexOf("]["));
				} else
				{
					idString = photoMatcher.group(2);
				}
				ps.setSpan(showIcon.getImage(photoMatcher, idString),
						photoMatcher.start(), photoMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				Message message = new Message();
				message.what = 1;
				message.obj = ps;
				handler.sendMessage(message);
			}

			Pattern picturePattern = Pattern
					.compile("(Picture\\^\\_\\^\\[(.*?)\\]\\^\\_\\^)");
			Matcher pictureMatcher = picturePattern.matcher(exit_body);

			while (pictureMatcher.find())
			{
				String idString = pictureMatcher.group();
				if (pictureMatcher.group(2).contains("]["))
				{
					idString = idString.substring(
							idString.indexOf("Picture^_^[") + 11,
							idString.indexOf("]["));
				} else
				{
					idString = pictureMatcher.group(2);
				}

				ps.setSpan(showIcon.getImage(pictureMatcher, idString),
						pictureMatcher.start(), pictureMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				Message mes = new Message();
				mes.what = 1;
				mes.obj = ps;
				handler.sendMessage(mes);
			}

			Pattern DrawPattern = Pattern
					.compile("(Draw\\^\\_\\^\\[(.*?)\\]\\^\\_\\^)");
			Matcher DrawMatcher = DrawPattern.matcher(exit_body);

			while (DrawMatcher.find())
			{
				String idString = DrawMatcher.group();
				if (DrawMatcher.group(2).contains("]["))
				{
					idString = idString.substring(
							idString.indexOf("Draw^_^[") + 8,
							idString.indexOf("]["));
				} else
				{
					idString = DrawMatcher.group(2);
				}

				ps.setSpan(showIcon.getImage(DrawMatcher, idString),
						DrawMatcher.start(), DrawMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				Message mes = new Message();
				mes.what = 1;
				mes.obj = ps;
				handler.sendMessage(mes);
			}
			Pattern gesturePattern = Pattern
					.compile("(Gesture\\^\\_\\^\\[(.*?)\\]\\^\\_\\^)");
			Matcher gestureMatcher = gesturePattern.matcher(exit_body);
			while (gestureMatcher.find()) {

				String idString = gestureMatcher.group();
				idString = idString.substring(
						idString.indexOf("Gesture^_^[") + 11,
						idString.indexOf("]^_^"));
				Bitmap bitmap = BitmapFactory.decodeFile(idString);
				Drawable drawable = new BitmapDrawable(getResources(),bitmap);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				ImageSpan gesSpan = new ImageSpan(drawable, gestureMatcher.group());
				ps.setSpan(gesSpan,
						gestureMatcher.start(), gestureMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				Message mes = new Message();
				mes.what = 1;
				mes.obj = ps;
				handler.sendMessage(mes);
			}

			Pattern videoPattern = Pattern
					.compile("(Video\\^\\_\\^\\[(.*?)\\]\\^\\_\\^)");
			Matcher videoMatcher = videoPattern.matcher(exit_body);
			while (videoMatcher.find())
			{
				String idString = videoMatcher.group();
				if (videoMatcher.group(2).contains("]["))
				{
					idString = idString.substring(
							idString.indexOf("Video^_^[") + 9,
							idString.indexOf("]["));
				} else
				{
					idString = videoMatcher.group(2);
				}
				ps.setSpan(showIcon.getVideoImage(videoMatcher, idString),
						videoMatcher.start(), videoMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				Message message = new Message();
				message.what = 1;
				message.obj = ps;
				handler.sendMessage(message);
			}
			Message message = new Message();
			message.what = 2;
			handler.sendMessage(message);// 结束线程
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
