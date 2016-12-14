package com.donote.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.mobstat.StatService;
import com.donote.adapter.NoteDbAdapter;
import com.donote.alarm.AlarmReceiver;
import com.donote.alarm.AlarmSet;
import com.donote.alarm.ListTimePicker;
import com.donote.filebrowser.OpenFiles;
import com.donote.imagehandler.ImageMemoryCache;
import com.donote.util.Expressions;
import com.donote.util.ShareUtil;
import com.donote.util.ShowIcon;
import com.iflytek.speech.SynthesizerPlayer;
import com.wxl.donote.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.Html.ImageGetter;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class DisplayContent extends Activity {

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
	private static final String image_detect = "(Photo|Video|Picture|Draw){1}\\^_\\^\\[(.*?)\\]{1,2}\\^_\\^";
	private static String patternString = "(Photo|Gesture|Video|Record|Draw|File|Picture|Table){1}\\^_\\^\\[(.*?)\\]\\^_\\^";
	@SuppressWarnings("unused")
	private ShowIcon show;
	private View mainLayout;
	private int[] expressionImages;
	private int[] expressionImages1;
	private int[] expressionImages2;
	private LinearLayout parent;
	private Bundle extrasBundle;
	private Long mRowId;
	private Matcher photoMatcher;
	private String exit_title = "";
	private String exit_body = "";
	private TextView catagory_TextView;
	private String exit_catagory = "";
	private TextView mTitle;
	private LayoutInflater inflater;
	private Drawable face;
	private Thread imageThread;
	private ArrayList<ImageView> imageGroup;
	private ImageButton delete;
	private ImageButton share;
	private PopupWindow window;
	private ImageButton add_alarm;
	private ImageButton modify;
	private ImageButton moreMenuButton;
	// private ImageButton manage_alarm;
	// private ImageButton movoto;
	private ImageButton returnButton;
	// private ImageButton lock_or_unlock;
	// private ImageButton sendToDeskButton;
	private Button lockOrUnlockButton;
	private Button move;
	private Button manager;
	private Button listenButton;
	private Button sendToDest;
	private AlertDialog.Builder builder;
	private int textSize;
	private SynthesizerPlayer player = null;

	private AlarmManager alarm_service;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what)
			{
			case 1:
				ImageView tempImageView = imageGroup.get(0);
				tempImageView.setImageBitmap((Bitmap) msg.obj);
				imageGroup.remove(0);
				break;
			case 2:
				handler.removeCallbacks(imageThread);
			}
		}
	};

	Html.ImageGetter imgGetter = new Html.ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			Log.i("bae", "enter");
			try {
				Integer.parseInt(source);
			} catch (NumberFormatException e) {
				// TODO: handle exception
				Bitmap bitmap = BitmapFactory.decodeFile(source);
				Drawable drawable = new BitmapDrawable(getResources(), bitmap);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				return drawable;
			}
			face = null;
			switch (Integer.parseInt(source) / expressionImages1.length)
			{
			case 0:
				face = getResources().getDrawable(
						expressionImages[Integer.parseInt(source)
						                 % expressionImages.length]);
				break;
			case 1:
				face = getResources().getDrawable(
						expressionImages1[Integer.parseInt(source)
						                  % expressionImages1.length]);
				break;
			case 2:
				face = getResources().getDrawable(
						expressionImages2[Integer.parseInt(source)
						                  % expressionImages2.length]);
				break;
			}

			face.setBounds(0, 0, face.getIntrinsicWidth(),
					face.getIntrinsicHeight());
			return face;
		}
	};

	Html.ImageGetter gesGetter = new Html.ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			Bitmap bitmap = BitmapFactory.decodeFile(source);
			Drawable drawable =  new BitmapDrawable(bitmap);
			return drawable;
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_display);
		mainLayout = findViewById(R.id.display_main);
		Bitmap temp = ImageMemoryCache.getBitmap((long) -1, "beijing");
		if (temp != null)
		{
			Drawable beijing = new BitmapDrawable(getResources(), temp);
			mainLayout.setBackgroundDrawable(beijing);
			mainLayout.getBackground().setAlpha(80);
		}
		parent = (LinearLayout) findViewById(R.id.display_body);
		extrasBundle = getIntent().getExtras();
		mTitle = (TextView) findViewById(R.id.display_title);
		catagory_TextView = (TextView) findViewById(R.id.display_catagory_textview);
		expressionImages = Expressions.expressionImgs;
		expressionImages1 = Expressions.expressionImgs1;
		expressionImages2 = Expressions.expressionImgs2;
		imageGroup = new ArrayList<ImageView>();
		inflater = LayoutInflater.from(this);
		alarm_service = (AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);
		initButton();
		if (extrasBundle != null)
		{
			exit_catagory = extrasBundle.getString(NoteDbAdapter.KEY_CATAGORY);
			exit_title = extrasBundle.getString(NoteDbAdapter.KEY_TITLE);
			exit_body = extrasBundle.getString(NoteDbAdapter.KEY_BODY);
			mRowId = extrasBundle.getLong(NoteDbAdapter.KEY_ROWID);
			if (exit_title == null)
			{
				exit_title = "";
			}
			if (exit_title != null)
			{
				switch (MainActivity.textsize)
				{
				case 0:
					mTitle.setTextSize(16);
					break;
				case 1:
					mTitle.setTextSize(18);
					break;
				case 2:
					mTitle.setTextSize(21);
					break;
				case 3:
					mTitle.setTextSize(24);
					break;
				default:
					break;
				}
				mTitle.setText(exit_title);
			}
			if (exit_catagory != null)
			{
				catagory_TextView.setText(exit_catagory);
			}
			exit_body.replace(" ", "");
			Pattern photoPattern = Pattern.compile(patternString);
			photoMatcher = photoPattern.matcher(exit_body);
			int end = 0;
			while (photoMatcher.find())
			{
				if (photoMatcher.group(1).equals("Photo"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Photo^_^[") + 9,
							idString.indexOf("]^_^"));
					showFace(exit_body.substring(end, photoMatcher.start()));

					View iv = inflater.inflate(R.layout.enterfirstimageview,
							null);
					ImageView image = (ImageView) iv
							.findViewById(R.id.display_image);
					image.setImageResource(R.drawable.ic_default_image);
					imageGroup.add(image);
					setonClickListener(image, idString, 1);
					parent.addView(iv);
					end = photoMatcher.end();
				}
				// 判断录音
				else if (photoMatcher.group(1).equals("Record"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Record^_^[") + 10,
							idString.indexOf("]^_^"));
					showFace(exit_body.substring(end, photoMatcher.start()));

					View iv = inflater.inflate(R.layout.enterfirstimageview,
							null);
					ImageView image = (ImageView) iv
							.findViewById(R.id.display_image);
					image.setImageDrawable(ShowIcon.RecordIcon(idString,
							MainActivity.Width));
					setonClickListener(image, idString, 2);
					parent.addView(iv);
					end = photoMatcher.end();
				}

				// 判断画图
				else if (photoMatcher.group(1).equals("Draw"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Draw^_^[") + 8,
							idString.indexOf("]^_^"));
					showFace(exit_body.substring(end, photoMatcher.start()));

					View iv = inflater.inflate(R.layout.enterfirstimageview,
							null);
					ImageView image = (ImageView) iv
							.findViewById(R.id.display_image);
					image.setImageResource(R.drawable.ic_default_image);
					imageGroup.add(image);
					setonClickListener(image, idString, 3);
					parent.addView(iv);
					end = photoMatcher.end();
				}

				// 判断附件
				else if (photoMatcher.group(1).equals("File"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("File^_^[") + 8,
							idString.indexOf("]^_^"));
					showFace(exit_body.substring(end, photoMatcher.start()));

					View iv = inflater.inflate(R.layout.enterfirstimageview,
							null);
					ImageView image = (ImageView) iv
							.findViewById(R.id.display_image);
					image.setImageDrawable(ShowIcon.FileIcon(idString,
							MainActivity.Width));
					setonClickListener(image, idString, 4);
					parent.addView(iv);
					end = photoMatcher.end();
				}

				// 判断图片
				else if (photoMatcher.group(1).equals("Picture"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Picture^_^[") + 11,
							idString.indexOf("]^_^"));
					showFace(exit_body.substring(end, photoMatcher.start()));

					View iv = inflater.inflate(R.layout.enterfirstimageview,
							null);
					ImageView image = (ImageView) iv
							.findViewById(R.id.display_image);
					image.setImageResource(R.drawable.ic_default_image);
					imageGroup.add(image);
					setonClickListener(image, idString, 5);
					parent.addView(iv);
					end = photoMatcher.end();
				}

				// 判断视频
				else if (photoMatcher.group(1).equals("Video"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Video^_^[") + 9,
							idString.indexOf("]^_^"));
					showFace(exit_body.substring(end, photoMatcher.start()));

					View iv = inflater.inflate(R.layout.enterfirstimageview,
							null);
					ImageView image = (ImageView) iv
							.findViewById(R.id.display_image);

					image.setImageResource(R.drawable.ic_default_image);
					imageGroup.add(image);
					setonClickListener(image, idString, 6);
					parent.addView(iv);
					end = photoMatcher.end();
				} else if (photoMatcher.group(1).equals("Table"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Table^_^[") + 9,
							idString.indexOf("]^_^"));
					showFace(exit_body.substring(end, photoMatcher.start()));

					View iv = inflater.inflate(R.layout.enterfirstimageview,
							null);
					ImageView image = (ImageView) iv
							.findViewById(R.id.display_image);
					String title = idString.substring(
							idString.indexOf("<--title:") + 9,
							idString.indexOf("-->"));
					if (title.equals(""))
					{
						title = getResources()
								.getString(R.string.without_title);
					}
					image.setImageDrawable(ShowIcon.tableIcon(title,
							MainActivity.Width));
					setonClickListener(image, idString, 7);
					parent.addView(iv);
					end = photoMatcher.end();
				}
			}

			if (exit_body.substring(end).length() >= 1)
			{
				showFace(exit_body.substring(end));

				exit_body = null;
			}

			switch (MainActivity.textsize)
			{
			case 0:
				setFontSize(parent, 16);
				textSize = 16;
				break;
			case 1:
				setFontSize(parent, 18);
				textSize = 18;
				break;
			case 2:
				setFontSize(parent, 21);
				textSize = 21;
				break;
			case 3:
				setFontSize(parent, 24);
				textSize = 24;
				break;
			default:
				break;
			}

		}// If

		if (extrasBundle != null)
		{
			imageThread = new Thread(new imageLoadThread());
			imageThread.setDaemon(true);
			imageThread.start();// 启动线程
		}

	}// OnCreate

	private void initButton() {
		// TODO Auto-generated method stub
		delete = (ImageButton) findViewById(R.id.display_delete);
		moreMenuButton = (ImageButton) findViewById(R.id.edit_more);
		share = (ImageButton) findViewById(R.id.display_share);
		add_alarm = (ImageButton) findViewById(R.id.display_add_alarm);
		modify = (ImageButton) findViewById(R.id.display_edit);
		returnButton = (ImageButton) findViewById(R.id.display_return);

		moreMenuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopupWindow popupWindow = menuPopupwindow(DisplayContent.this);
				popupWindow.showAsDropDown(moreMenuButton);
			}
		});

		returnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Cursor temp = MainActivity.mDbHelper.getnote(mRowId);
				if (temp.getInt(temp
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK)) == 0)
				{
					builder = new Builder(DisplayContent.this);
					builder.setMessage(getResources().getString(
							R.string.confirm_delete));
					builder.setTitle(getResources().getString(R.string.tip));
					builder.setPositiveButton(
							getResources().getString(R.string.confirm),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MainActivity.mDbHelper.deleteNote(mRowId);
									Toast.makeText(
											DisplayContent.this,
											getResources().getString(
													R.string.delete_succeed),
													Toast.LENGTH_SHORT).show();
									finish();
								}
							});
					builder.setNegativeButton(
							getResources().getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							});
					builder.create().show();
				} else
				{
					Toast.makeText(DisplayContent.this,
							getResources().getString(R.string.note_locked),
							Toast.LENGTH_SHORT).show();
				}
				temp.close();
			}
		});

		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String bodyString = extrasBundle
						.getString(NoteDbAdapter.KEY_BODY);
				
				bodyString = bodyString.replaceAll("Face:f" + "\\w{3}", "");
				bodyString = bodyString.replace(" ", "");
				Matcher imageMatcher;
				Pattern imagePattern = Pattern.compile(image_detect);
				imageMatcher = imagePattern.matcher(bodyString);
				Context mContext = DisplayContent.this;
				if (imageMatcher.find())
				{
					String idString = imageMatcher.group();
					if (imageMatcher.group(2).contains("]["))
					{
						idString = idString.substring(
								idString.indexOf("Photo^_^[") + 9,
								idString.indexOf("]["));
					} else
					{
						idString = imageMatcher.group(2);
					}
					bodyString = bodyString.replaceAll(patternString, "");
					ShareUtil.shareMsg(
							mContext,
							((Activity) mContext).getTitle().toString(),
							mContext.getResources().getString(
									R.string.note_share), bodyString, idString);
					return;
				}
				bodyString = bodyString.replaceAll(patternString, "");
				ShareUtil.shareMsg(
						mContext,
						((Activity) mContext).getTitle().toString(),
						mContext.getResources().getString(
								R.string.note_share), bodyString, null);
			}
		});

		add_alarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent time_picker;
				time_picker = new Intent(DisplayContent.this,
						ListTimePicker.class);
				time_picker.putExtra("ID", mRowId);
				startActivityForResult(time_picker, 2);
			}
		});

		modify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Cursor temp = MainActivity.mDbHelper.getnote(mRowId);
				if (temp.getInt(temp
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK)) == 0)
				{
					Intent i = new Intent(DisplayContent.this, CommonEdit.class);
					i.putExtra(NoteDbAdapter.KEY_ROWID, mRowId);
					i.putExtra(NoteDbAdapter.KEY_TITLE, exit_title);
					i.putExtra(NoteDbAdapter.KEY_BODY, exit_body);
					i.putExtra(NoteDbAdapter.KEY_CATAGORY, exit_catagory);
					startActivity(i);
					finish();
				} else
				{
					Toast.makeText(DisplayContent.this,
							getResources().getString(R.string.note_locked),
							Toast.LENGTH_SHORT).show();
				}
				temp.close();
			}
		});
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
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2 && resultCode == RESULT_OK)
		{
			Bundle extras = data.getExtras();
			if (extras != null)
			{
				int year = extras.getInt("year");
				int month = extras.getInt("month") - 1;
				int day = extras.getInt("day");
				int hour = extras.getInt("hour");
				int minute = extras.getInt("minute");
				Intent intent = new Intent(this, AlarmReceiver.class);
				intent.putExtra("title", exit_title);
				intent.putExtra("body", exit_body);
				intent.putExtra("ID", mRowId);
				Calendar calendar = Calendar.getInstance();
				Calendar anotherCalendar = Calendar.getInstance();
				anotherCalendar.set(year, month, day, hour, minute, 0);
				if (calendar.compareTo(anotherCalendar) == -1)
				{
					calendar.set(year, month, day, hour, minute, 0);
				} else
				{
					Toast.makeText(DisplayContent.this,
							getResources().getString(R.string.time_passed),
							Toast.LENGTH_SHORT).show();
					return;
				}
				long alarmIdLong = MainActivity.mDbHelper.createAlarm(calendar,
						extras.getLong("ID"),
						getResources().getString(R.string.whole_note));
				intent.putExtra("alarmID", alarmIdLong);
				PendingIntent p_intent = PendingIntent.getBroadcast(this,
						(int) alarmIdLong, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				// Schedule the alarm!
				alarm_service.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), p_intent);
				MainActivity.mDbHelper.updateAlarmflag(mRowId);
				Toast.makeText(this,
						getResources().getString(R.string.new_alarm_has_set),
						Toast.LENGTH_LONG).show();
			}// If
		}
	}

	public void setonClickListener(ImageView imageView,
			final String filePathString, final int cata) {
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file = new File(filePathString);
				if (!file.exists() && cata != 7)
				{
					Toast.makeText(
							DisplayContent.this,
							getResources().getString(
									R.string.file_not_found_error),
									Toast.LENGTH_SHORT).show();
					return;
				}
				if (cata == 1 || cata == 3 || cata == 5)
				{
					Intent intent = OpenFiles.getImageFileIntent(file);
					startActivity(intent);
				} else if (cata == 7)
				{
					Bundle bundle = new Bundle();
					bundle.putBoolean("iswrite", true);
					bundle.putInt("can_write", 2);//不能编辑表格
					bundle.putString("content", filePathString);
					Intent intent = new Intent(DisplayContent.this,
							TableEdit.class);
					intent.putExtras(bundle);
					startActivity(intent);
				} else if (cata == 2)
				{
					playSoundFile(filePathString);
				} else if (cata == 4)
				{
					// 根据文件名来判断文件类型，设置不同的监听
					if (checkEndsWithInStringArray(
							filePathString,
							getResources().getStringArray(
									R.array.fileEndingWebText)))
					{
						Intent intent = OpenFiles.getHtmlFileIntent(file);
						try {
							startActivity(intent);
						}
						catch(ActivityNotFoundException e) {
							Toast.makeText(DisplayContent.this, "未找到可以打开该文件的程序", Toast.LENGTH_SHORT).show();
						}

					} else if (checkEndsWithInStringArray(
							filePathString,
							getResources().getStringArray(
									R.array.fileEndingExcel)))
					{
						Intent intent = OpenFiles.getExcelFileIntent(file);
						try {
							startActivity(intent);
						}
						catch(ActivityNotFoundException e) {
							Toast.makeText(DisplayContent.this, "未找到可以打开该文件的程序", Toast.LENGTH_SHORT).show();
						}

					} else if (checkEndsWithInStringArray(filePathString,
							getResources()
							.getStringArray(R.array.fileEndingPdf)))
					{
						Intent intent = OpenFiles.getPdfFileIntent(file);
						try {
							startActivity(intent);
						}
						catch(ActivityNotFoundException e) {
							Toast.makeText(DisplayContent.this, "未找到可以打开该文件的程序", Toast.LENGTH_SHORT).show();
						}
					} else if (checkEndsWithInStringArray(filePathString,
							getResources()
							.getStringArray(R.array.fileEndingPPT)))
					{
						Intent intent = OpenFiles.getPPTFileIntent(file);
						try {
							startActivity(intent);
						}
						catch(ActivityNotFoundException e) {
							Toast.makeText(DisplayContent.this, "未找到可以打开该文件的程序", Toast.LENGTH_SHORT).show();
						}

					} else if (checkEndsWithInStringArray(
							filePathString,
							getResources().getStringArray(
									R.array.fileEndingText)))
					{
						Intent intent = OpenFiles.getTextFileIntent(file);
						try {
							startActivity(intent);
						}
						catch(ActivityNotFoundException e) {
							Toast.makeText(DisplayContent.this, "未找到可以打开该文件的程序", Toast.LENGTH_SHORT).show();
						}
					} else if (checkEndsWithInStringArray(
							filePathString,
							getResources().getStringArray(
									R.array.fileEndingWord)))
					{
						Intent intent = OpenFiles.getWordFileIntent(file);
						try {
							startActivity(intent);
						}
						catch(ActivityNotFoundException e) {
							Toast.makeText(DisplayContent.this, "未找到可以打开该文件的程序", Toast.LENGTH_SHORT).show();
						}

					} else
					{
						Intent intent = new Intent(DisplayContent.this,
								ShowFileInfo.class);
						Bundle bundle = new Bundle();
						bundle.putString("file", filePathString);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				} else if (cata == 6)
				{
					Intent intent = OpenFiles.getVideoFileIntent(file);
					startActivity(intent);
				}
			}
		});

	}

	private void playSoundFile(String temp) {
		Intent intent = new Intent(DisplayContent.this, PlayMusic.class);
		Bundle bundle = new Bundle();
		bundle.putString("playRec", temp);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void showFace(String body) {
		// 判断表情
		Pattern facePattern = Pattern.compile("Face:f" + "\\w{3}");
		Matcher faceMatcher = facePattern.matcher(body);
		
		String source = "";
		View tv = inflater.inflate(R.layout.enterfirsttextview, null);
		TextView text = (TextView) tv.findViewById(R.id.display_text);
		text.setAutoLinkMask(Linkify.ALL);
		int end = 0;

		while (faceMatcher.find())
		{
			String idString = faceMatcher.group();
			idString = idString.substring(idString.indexOf(":") + 2,
					idString.length());
			String bodyString = body.substring(end, faceMatcher.start())
					.replaceAll("\r|\n", "<br>");
			//Log.i("bae", bodyString);
			source += "<pre>" + bodyString + "</pre>" + "<img src='" + idString
					+ "'/>";
			end = faceMatcher.end();
		}
		if (source == "")
		{
			showGesture(body);
		} else
		{
			String bodyString = body.substring(end)
					.replaceAll("\r|\n", "<br>");
			//Log.i("bae", bodyString);
			source += bodyString;
			showGesture(source);
		}

	}
	
	private void showGesture(String body) {
		
		Pattern gesPattern = Pattern.compile("Gesture\\^_\\^\\[(.*?)\\]\\^_\\^");
		Matcher gesMatcher = gesPattern.matcher(body);
		String ges_source = "";
		View ges_tv = inflater.inflate(R.layout.enterfirsttextview, null);
		TextView ges_text = (TextView) ges_tv.findViewById(R.id.display_text);
		ges_text.setAutoLinkMask(Linkify.ALL);
		int ges_end = 0;

		while (gesMatcher.find())
		{
			String idString = gesMatcher.group();
			idString = idString.substring(
					idString.indexOf("Gesture^_^[") + 11,
					idString.indexOf("]^_^"));
			String bodyString = body.substring(ges_end, gesMatcher.start())
					.replaceAll("\r|\n", "<br>");
			//Log.i("bae", bodyString);
			ges_source += "<pre>" + bodyString + "</pre>" + "<img src='" + idString
					+ "'/>";
			ges_end = gesMatcher.end();

		}
		if (ges_source == "")
		{
			ges_text.setText(body);
		} else
		{
			String bodyString = body.substring(ges_end)
					.replaceAll("\r|\n", "<br>");
			//Log.i("bae", bodyString);
			ges_source += bodyString;
			Spanned gesText = Html.fromHtml(ges_source, imgGetter, null);
			ges_text.setText(gesText);
		}
		parent.addView(ges_tv);
	}

	// 通过文件名判断是什么类型的文件
	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {

		for (String aEnd : fileEndings)
		{
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;

	}

	// 图片刷新线程
	class imageLoadThread implements Runnable {
		public void run() {
			// 判断Photo
			Pattern photoPattern = Pattern.compile(patternString);
			exit_body = extrasBundle.getString(NoteDbAdapter.KEY_BODY);
			Matcher photoMatcher = photoPattern.matcher(exit_body);
			while (photoMatcher.find())
			{
				if (photoMatcher.group(1).equals("Photo"))
				{
					BitmapFactory.Options opt = new BitmapFactory.Options();
					opt.inJustDecodeBounds = true;
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Photo^_^[") + 9,
							idString.indexOf("]^_^"));
					BitmapFactory.decodeFile(idString, opt);
					Message message = new Message();
					message.what = 1;
					if (opt.outHeight > opt.outWidth
							|| ShowIcon.readPictureDegree(idString) == 0)
					{
						message.obj = ShowIcon
								.readBitmapAutoSize(
										idString,
										MainActivity.Width,
										(float) opt.outHeight
										* ((float) MainActivity.Width / opt.outWidth),
										ShowIcon.readPictureDegree(idString));
					} else
					{
						message.obj = ShowIcon
								.readBitmapAutoSize(
										idString,
										MainActivity.Width,
										(float) opt.outWidth
										* ((float) MainActivity.Width / opt.outHeight),
										ShowIcon.readPictureDegree(idString));
					}
					handler.sendMessage(message);
				}

				else if (photoMatcher.group(1).equals("Draw"))
				{
					BitmapFactory.Options opt = new BitmapFactory.Options();
					opt.inJustDecodeBounds = true;
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Draw^_^[") + 8,
							idString.indexOf("]^_^"));
					BitmapFactory.decodeFile(idString, opt);
					Message mes = new Message();
					mes.what = 1;
					if (opt.outHeight > opt.outWidth
							|| ShowIcon.readPictureDegree(idString) == 0)
					{
						mes.obj = ShowIcon
								.readBitmapAutoSize(
										idString,
										MainActivity.Width,
										(float) opt.outHeight
										* ((float) MainActivity.Width / opt.outWidth),
										ShowIcon.readPictureDegree(idString));
					} else
					{
						mes.obj = ShowIcon
								.readBitmapAutoSize(
										idString,
										MainActivity.Width,
										(float) opt.outWidth
										* ((float) MainActivity.Width / opt.outHeight),
										ShowIcon.readPictureDegree(idString));
					}
					handler.sendMessage(mes);
				}

				else if (photoMatcher.group(1).equals("Picture"))
				{
					BitmapFactory.Options opt = new BitmapFactory.Options();
					opt.inJustDecodeBounds = true;
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Picture^_^[") + 11,
							idString.indexOf("]^_^"));
					BitmapFactory.decodeFile(idString, opt);
					Message mes = new Message();
					mes.what = 1;
					if (opt.outHeight > opt.outWidth
							|| ShowIcon.readPictureDegree(idString) == 0)
					{
						mes.obj = ShowIcon
								.readBitmapAutoSize(
										idString,
										MainActivity.Width,
										(float) opt.outHeight
										* ((float) MainActivity.Width / opt.outWidth),
										ShowIcon.readPictureDegree(idString));
					} else
					{
						mes.obj = ShowIcon
								.readBitmapAutoSize(
										idString,
										MainActivity.Width,
										(float) opt.outWidth
										* ((float) MainActivity.Width / opt.outHeight),
										ShowIcon.readPictureDegree(idString));
					}
					handler.sendMessage(mes);
				} else if (photoMatcher.group(1).equals("Video"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Video^_^[") + 9,
							idString.indexOf("]^_^"));
					Bitmap video = ThumbnailUtils.createVideoThumbnail(
							idString,
							MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
					if (video == null)
					{
						BitmapDrawable bd = (BitmapDrawable) getResources()
								.getDrawable(R.drawable.ic_default_image);
						video = bd.getBitmap();
					}
					if (video.getHeight() < video.getWidth())
					{
						video = ShowIcon.zoomBitmapTospe(video,
								MainActivity.Width,
								(float) (MainActivity.Height / 2.5));
					} else
					{
						video = ShowIcon.zoomBitmapTospe(video,
								MainActivity.Width,
								(float) (MainActivity.Height / 1.5));
					}
					video = ShowIcon.VideoIcon(video);
					Message message = new Message();
					message.what = 1;
					message.obj = video;
					handler.sendMessage(message);
				}
			}
			Message message = new Message();
			message.what = 2;
			handler.sendMessage(message);// 结束线程
		}
	}

	protected void moveTo_showDialog(final Long id) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.movetodialog,
				(ViewGroup) findViewById(R.id.moveto_dialog));
		final Spinner spinner_c = (Spinner) view
				.findViewById(R.id.moveToCatagory);
		Cursor cursor = MainActivity.mDbHelper.getAllCatagory();
		ArrayList<String> allCatagory = new ArrayList<String>();
		while (cursor.moveToNext())
		{
			allCatagory.add(cursor.getString(cursor
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME)));
		}
		cursor.close();
		ArrayAdapter<String> allCatagory_adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, allCatagory);
		allCatagory_adapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_c.setAdapter(allCatagory_adapter);

		new AlertDialog.Builder(this)
		.setTitle(getResources().getString(R.string.choose))
		.setView(view)
		.setPositiveButton(getResources().getString(R.string.confirm),
				new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				final String catagoryNameString = spinner_c
						.getSelectedItem().toString();
				MainActivity.mDbHelper
				.updata_catagory_single_notes(id,
						catagoryNameString);
				catagory_TextView.setText(catagoryNameString);
				Toast.makeText(
						DisplayContent.this,
						getResources().getString(
								R.string.move_succeed),
								Toast.LENGTH_SHORT).show();
			}
		})
		.setNegativeButton(getResources().getString(R.string.cancel),
				null).create().show();
	}

	/**
	 * 改变字体
	 * 
	 * @param v
	 * @param fontSize
	 */
	public void setFontSize(View v, float fontSizeValue) {
		if (v instanceof TextView)
		{
			((TextView) v).setTextSize(fontSizeValue);
		} else if (!(v instanceof ImageView))
		{
			int vChildCount = ((ViewGroup) v).getChildCount();
			for (int i = 0; i < vChildCount; i++)
			{
				View v1 = ((ViewGroup) v).getChildAt(i);
				setFontSize(v1, fontSizeValue);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
		{
			textSize += 4;
			setFontSize(parent, textSize);
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
		{
			textSize -= 4;
			setFontSize(parent, textSize);
			return true;
		}

		return super.onKeyDown(keyCode, event);
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (player != null)
		{
			player.pause();
		}
		super.onStop();
	}

	private void sendToDesktop() {

		Intent sender = new Intent();
		Intent shortcutIntent = new Intent(this, MainActivity.class);
		shortcutIntent.putExtra(NoteDbAdapter.KEY_ROWID, mRowId);
		shortcutIntent.putExtra(NoteDbAdapter.KEY_TITLE, exit_title);
		shortcutIntent.putExtra(NoteDbAdapter.KEY_BODY, exit_body);
		shortcutIntent.putExtra(NoteDbAdapter.KEY_CATAGORY, exit_catagory);
		shortcutIntent.putExtra("style", 0);
		shortcutIntent.setAction(Intent.ACTION_VIEW);
		sender.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		sender.putExtra(Intent.EXTRA_SHORTCUT_NAME, exit_title);
		Bitmap bitmap = ImageMemoryCache.getBitmap(mRowId, null);
		if (bitmap != null)
		{
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 72, 72,
					true);
			sender.putExtra(Intent.EXTRA_SHORTCUT_ICON, scaledBitmap);
		} else
		{
			sender.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					Intent.ShortcutIconResource.fromContext(this,
							R.drawable.ic_launcher));
		}
		sender.putExtra("duplicate", true);
		sender.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		sendBroadcast(sender);
	}

	private PopupWindow menuPopupwindow(Context cx) {
		if (window == null)
		{
			window = new PopupWindow(cx);
			View contentView = LayoutInflater.from(this).inflate(
					R.layout.display_menu, null);
			window.setContentView(contentView);
			window.setWidth(LayoutParams.WRAP_CONTENT);
			window.setHeight(LayoutParams.WRAP_CONTENT);

			lockOrUnlockButton = (Button) contentView
					.findViewById(R.id.menu_lock_or_unlock);
			lockOrUnlockButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Cursor temp = MainActivity.mDbHelper.getnote(mRowId);
					if (temp.getInt(temp
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK)) == 0)
					{
						MainActivity.mDbHelper.lockNote(mRowId);
						Toast.makeText(
								DisplayContent.this,
								getResources().getString(R.string.lock_succeed),
								Toast.LENGTH_SHORT).show();
					} else
					{
						MainActivity.mDbHelper.unLockNote(mRowId);
						Toast.makeText(
								DisplayContent.this,
								getResources().getString(
										R.string.unlock_succeed),
										Toast.LENGTH_SHORT).show();
					}
					temp.close();
					window.dismiss();
				}
			});
			move = (Button) contentView.findViewById(R.id.menu_moveto);
			move.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					window.dismiss();
					moveTo_showDialog(mRowId);

				}

			});
			manager = (Button) contentView.findViewById(R.id.menu_manager);
			manager.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (MainActivity.mDbHelper.findAlarmByID(mRowId).getCount() > 0)
					{
						Intent i = new Intent(DisplayContent.this,
								AlarmSet.class);
						i.putExtra("NoteID", mRowId);
						startActivity(i);
					} else
					{
						Toast.makeText(
								DisplayContent.this,
								getResources().getString(
										R.string.the_note_hasnot_alarm),
										Toast.LENGTH_SHORT).show();
					}
					window.dismiss();
				}
			});

			listenButton = (Button) contentView.findViewById(R.id.menu_listen);
			listenButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Cursor temp = MainActivity.mDbHelper.getnote(mRowId);
					Toast.makeText(DisplayContent.this, getResources().getString(R.string.decoding),
							Toast.LENGTH_SHORT).show();
					player = SynthesizerPlayer.createSynthesizerPlayer(
							DisplayContent.this, "appid=51b122a7");
					player.setVoiceName("xiaoyan");
					player.playText(temp.getString(temp
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_CONTENT)),
							"ent=vivi21,bft=5", null);
					window.dismiss();
				}
			});

			sendToDest = (Button) contentView
					.findViewById(R.id.menu_sendtodesk);
			sendToDest.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sendToDesktop();
					window.dismiss();
				}
			});

			// 设置PopupWindow外部区域是否可触摸
			window.setFocusable(true); // 设置PopupWindow可获得焦点
			window.setTouchable(true); // 设置PopupWindow可触摸
			window.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		}
		return window;
	}

}
