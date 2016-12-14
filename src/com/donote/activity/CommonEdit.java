package com.donote.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mobstat.StatService;
import com.donote.adapter.MySimpleCursorAdapter;
import com.donote.adapter.NoteDbAdapter;
import com.donote.alarm.CreateAlarm;
import com.donote.filebrowser.FileView;
import com.donote.filebrowser.OpenFiles;
import com.donote.imagehandler.ImageHandle;
import com.donote.imagehandler.ImageMemoryCache;
import com.donote.util.Expressions;
import com.donote.util.GesView;
import com.donote.util.MyLocationListener;
import com.donote.util.ShowIcon;
import com.donote.widget.DoNoteWidgetProvider;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.wxl.donote.R;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class CommonEdit extends Activity {
	private int table_end = 0;
	private int table_start = 0;
	private static String patternString = "(Photo|Video|Gesture|Record|Draw|File|Picture|Table){1}\\^_\\^\\[(.*?)\\]\\^_\\^";
	private SensorManager sensorManager;
	private Vibrator vibrator;
	private boolean isshake = false;
	private boolean isshow = false;
	private int shake_info;
	private SharedPreferences settings;
	private ShowIcon showIcon;
	private Context mCon;
	private ViewPager facePager;
	private GridView gView1;
	private GridView gView2;
	private GridView gView3;
	private ArrayList<GridView> grids;
	private ImageView page0;
	private ImageView page1;
	private ImageView page2;
	private View mianLayout;
	private int[] expressionImages;
	private PopupWindow window;
	private Paint mPaint;
	private PopupWindow ges_window;

	private GesView gesView;
	private boolean ges_state = false;

	private String[] expressionImageNames;
	private int[] expressionImages1;
	private String[] expressionImageNames1;
	private int[] expressionImages2;
	private String[] expressionImageNames2;
	private Cursor mNoteCursor;
	private EditText mTitleText;
	private Button view_table;
	public static EditText mBodyText;
	private float textSize;
	private TextView catagory_TextView;// 显示分类名称
	private Long mRowId;// 文章列表ID
	private Long cRowId;// 分类列表ID
	private String exit_title = "";
	private String exit_body = "";
	private String exit_catagory = "";
	private ImageButton edit_return_button;
	private ImageButton confirmButton;
	private ImageButton catagory_add_Button;
	private ImageButton recordButton;
	private ImageButton photoButton;
	private ImageButton faceButton;
	private ImageButton moreButton;
	private Button pictureButton;
	private Button additionButton;
	private Button movieButton;
	private Button insertLocation;
	private Button voiceButton;
	private Button drawButton;
	private Button gestureButton;
	private ListView listView;
	private String photofile;
	private String videofile;
	private Bitmap picture = null;
	private Bitmap face;
	private Bitmap draw;
	private String catagory_OldString;  
	public static String pathPhoto = Environment.getExternalStorageDirectory()
			.getPath() + "/" + "DoNote" + "/" + "photo" + "/";
	public static String pathPicture = Environment
			.getExternalStorageDirectory().getPath()
			+ "/"
			+ "DoNote"
			+ "/"
			+ "picture" + "/";
	private String pathDraw = Environment.getExternalStorageDirectory()
			.getPath() + "/" + "DoNote" + "/" + "draw" + "/";
	private String pathVideo = Environment.getExternalStorageDirectory()
			.getPath() + "/" + "DoNote" + "/" + "video" + "/";
	private InputMethodManager imm;
	private CreateAlarm detect;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = null;
	private AlertDialog.Builder builder;
	private String drawfile = null;
	private String facefile = null;
	private LinearLayout page_select;
	private SpannableString ps;
	private Matcher photoMatcher;
	private Thread imageThread;
	private Bundle extrasBundle;
	ConnectivityManager connManager;
	private View footerView;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what)
			{
			case 1:
				int index = mBodyText.getSelectionStart();
				if (msg.obj != null)
				{
					String text = (String) msg.obj;
					Editable edit = mBodyText.getEditableText();
					if (index < 0 || index >= edit.length())
					{
						edit.append(text);
					} else
					{
						edit.insert(index, text);// 光标所在位置插入文字
					}
				} else
				{
					Toast.makeText(CommonEdit.this, getResources().getText(R.string.interner_error),
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 2:
				Editable mbody = mBodyText.getText();
				mbody.clear();
				mbody.insert(0, ps);
				break;
			case 3:
				handler.removeCallbacks(imageThread);
				break;
			case 4:
				Toast.makeText(CommonEdit.this, getResources().getText(R.string.gps_error), Toast.LENGTH_SHORT)
				.show();
				break;
			}
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit);


		mianLayout = findViewById(R.id.edit_layout);


		Bitmap temp = ImageMemoryCache.getBitmap((long) -1, "beijing");
		if (temp != null)
		{
			Drawable beijing = new BitmapDrawable(getResources(), temp);
			mianLayout.setBackgroundDrawable(beijing);
			mianLayout.getBackground().setAlpha(80);
		}
		connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		footerView = ((LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(
						R.layout.catagory_footer, null);
		listView = (ListView) findViewById(R.id.catagory_list);
		listView.addFooterView(footerView);
		listView.setOnItemClickListener(new ItemClickListener());
		listView.setOnCreateContextMenuListener(new OptionMenu());
		renderListView();
		catagory_add_Button = (ImageButton) footerView
				.findViewById(R.id.catagory_add);
		catagory_add_Button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showDialog();
			}
		});
		settings = getSharedPreferences("shake_info", 0);
		shake_info = settings.getInt("isshake", 1);
		mCon = CommonEdit.this;
		detect = new CreateAlarm();
		expressionImages = Expressions.expressionImgs;
		expressionImageNames = Expressions.expressionImgNames;
		expressionImages1 = Expressions.expressionImgs1;
		expressionImageNames1 = Expressions.expressionImgNames1;
		expressionImages2 = Expressions.expressionImgs2;
		expressionImageNames2 = Expressions.expressionImgNames2;
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		page0 = (ImageView) findViewById(R.id.page0_select);
		page1 = (ImageView) findViewById(R.id.page1_select);
		page2 = (ImageView) findViewById(R.id.page2_select);
		page_select = (LinearLayout) findViewById(R.id.page_select);
		facePager = (ViewPager) findViewById(R.id.facepager);
		mTitleText = (EditText) findViewById(R.id.title);
		mNoteCursor = MainActivity.mDbHelper.getAllCatagory();
		catagory_TextView = (TextView) findViewById(R.id.catagory_textview);
		mBodyText = (EditText) findViewById(R.id.body);
		mBodyText.setSelection(mBodyText.getText().length(), mBodyText
				.getText().length());
		confirmButton = (ImageButton) findViewById(R.id.edit_save);
		recordButton = (ImageButton) findViewById(R.id.menu_record);
		photoButton = (ImageButton) findViewById(R.id.menu_photo);

		mRowId = null;// 笔记ID
		cRowId = null;// 分组ID
		myListener = new MyLocationListener(handler);
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		setLocationOption();

		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		// 初始化按钮
		initButton();
		// initShake
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		showIcon = new ShowIcon(CommonEdit.this, MainActivity.Width,
				MainActivity.Height);
		// 用于获得从mainActivity里获得的数据
		extrasBundle = getIntent().getExtras();
		if (extrasBundle != null)
		{
			exit_catagory = extrasBundle.getString(NoteDbAdapter.KEY_CATAGORY);
			exit_title = extrasBundle.getString(NoteDbAdapter.KEY_TITLE);
			exit_body = extrasBundle.getString(NoteDbAdapter.KEY_BODY);
			mRowId = extrasBundle.getLong(NoteDbAdapter.KEY_ROWID);

			if(extrasBundle.getString("wxl")!=null && extrasBundle.getString("wxl").equals("widget")){
				//打开数据库从数据库中得出信息
				mRowId = DoNoteWidgetProvider.note_id;
				Cursor cursor = MainActivity.mDbHelper.getnote(mRowId);
				exit_catagory = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_CATAGORY));
				exit_title = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE));
				exit_body = cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY));
			}
			if (extrasBundle.getInt("share") == 1)
			{
				mRowId = null;
			}
			if (exit_title == null)
			{
				exit_title = "";
			}
			if (exit_catagory == null)
			{
				exit_catagory = getResources().getString(R.string.default_group);
			}
			if (exit_title != null && !exit_title.equals(getResources().getString(R.string.without_title)))
			{
				switch (MainActivity.textsize)
				{
				case 0:
					mTitleText.setTextSize(16);
					break;
				case 1:
					mTitleText.setTextSize(18);
					break;
				case 2:
					mTitleText.setTextSize(21);
					break;
				case 3:
					mTitleText.setTextSize(24);
					break;
				default:
					break;
				}
				mTitleText.setText(exit_title);
			}
			initEdit(exit_body);
		}// If

		switch (MainActivity.textsize)
		{
		case 0:
			mBodyText.setTextSize(16);
			textSize = 16;
			break;
		case 1:
			mBodyText.setTextSize(18);
			textSize = 18;
			break;
		case 2:
			mBodyText.setTextSize(21);
			textSize = 21;
			break;
		case 3:
			mBodyText.setTextSize(24);
			textSize = 24;
			break;
		default:
			break;
		}

		gesView = (GesView) findViewById(R.id.ges_view);
		gesView.setVisibility(View.GONE);
		listView.setVisibility(View.GONE);
		initViewPager();
		mBodyText.setSelection(0);

	}// OnCreate

	private void initEdit(String exit_body){
		if (exit_body != null)
		{
			ps = new SpannableString(exit_body);
			// 判断Photo
			Pattern photoPattern = Pattern.compile(patternString);
			photoMatcher = photoPattern.matcher(exit_body);
			while (photoMatcher.find())
			{
				if (photoMatcher.group(1).equals("Photo"))
				{
					ps.setSpan(showIcon.getDefaultImage(photoMatcher),
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				// 判断录音
				else if (photoMatcher.group(1).equals("Record"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Record^_^[") + 10,
							idString.indexOf("]^_^"));
					ps.setSpan(
							showIcon.getRecordImage(photoMatcher, idString),
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				// 判断画图
				else if (photoMatcher.group(1).equals("Draw"))
				{
					ps.setSpan(showIcon.getDefaultImage(photoMatcher),
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				else if (photoMatcher.group(1).equals("Gesture")){
					ps.setSpan(showIcon.getDefaultImage(photoMatcher),
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				// 判断附件
				else if (photoMatcher.group(1).equals("File"))
				{

					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("File^_^[") + 8,
							idString.indexOf("]^_^"));
					ps.setSpan(
							showIcon.getFileImage(photoMatcher, idString),
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				// 判断图片
				else if (photoMatcher.group(1).equals("Picture"))
				{
					ps.setSpan(showIcon.getDefaultImage(photoMatcher),
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				// 判断视频
				else if (photoMatcher.group(1).equals("Video"))
				{
					ps.setSpan(com.donote.util.ShowIcon
							.getDefaultVideoImage(photoMatcher),
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}

				else if (photoMatcher.group(1).equals("Table"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Table^_^[") + 9,
							idString.indexOf("]^_^"));
					ps.setSpan(com.donote.util.ShowIcon.getTableImage(
							photoMatcher, idString), photoMatcher.start(),
							photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
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
				switch (Integer.parseInt(idString)
						/ expressionImages1.length)
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
				ImageSpan faceSpan = new ImageSpan(mCon, face,
						ImageSpan.ALIGN_BASELINE);
				ps.setSpan(faceSpan, faceMatcher.start(),
						faceMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

		}
		Editable mbody = mBodyText.getText();
		mbody.insert(0, ps);
		if (exit_catagory != null)
		{
			catagory_TextView.setText(exit_catagory);
		}

		imageThread = new Thread(new imageLoadThread());
		imageThread.setDaemon(true);
		imageThread.start();// 启动线程
	}

	// hankwing添加于2013/4/23 8:00
	private void initButton() {

		mTitleText.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				listView.setVisibility(View.GONE);
				catagory_add_Button.setVisibility(View.GONE);
				facePager.setVisibility(View.GONE);
				page_select.setVisibility(View.GONE);
				return false;
			}
		});

		mBodyText.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				listView.setVisibility(View.GONE);
				catagory_add_Button.setVisibility(View.GONE);
				facePager.setVisibility(facePager.GONE);
				page_select.setVisibility(View.GONE);
				int selectionStart = mBodyText.getSelectionStart();
				Spanned s = mBodyText.getText();
				ImageSpan[] imageSpans = s.getSpans(0, s.length(),
						ImageSpan.class);
				for (ImageSpan span : imageSpans)
				{
					String spanString = span.getSource();
					int start = s.getSpanStart(span);
					int end = s.getSpanEnd(span);

					if (selectionStart >= start && selectionStart <= end
							&& spanString != null)// 找到图片
					{
						imm.hideSoftInputFromWindow(mBodyText.getWindowToken(),
								0);
						if (spanString.substring(0, 4).equals("File"))
						{
							// 取得文件名
							String filePathString = spanString.substring(
									spanString.indexOf("File^_^[") + 8,
									spanString.indexOf("]^_^"));
							File file = new File(filePathString);
							if (!file.exists())
							{
								Toast.makeText(mCon, getResources().getString(R.string.file_not_found_error),
										Toast.LENGTH_SHORT).show();
								return;
							}
							if (checkEndsWithInStringArray(
									filePathString,
									getResources().getStringArray(
											R.array.fileEndingWebText)))
							{
								Intent intent = OpenFiles
										.getHtmlFileIntent(file);
								startActivity(intent);

							} else if (checkEndsWithInStringArray(
									filePathString,
									getResources().getStringArray(
											R.array.fileEndingExcel)))
							{
								Intent intent = OpenFiles
										.getExcelFileIntent(file);
								startActivity(intent);

							} else if (checkEndsWithInStringArray(
									filePathString,
									getResources().getStringArray(
											R.array.fileEndingPdf)))
							{
								Intent intent = OpenFiles
										.getPdfFileIntent(file);
								startActivity(intent);
							} else if (checkEndsWithInStringArray(
									filePathString,
									getResources().getStringArray(
											R.array.fileEndingPPT)))
							{
								Intent intent = OpenFiles
										.getPPTFileIntent(file);
								startActivity(intent);

							} else if (checkEndsWithInStringArray(
									filePathString,
									getResources().getStringArray(
											R.array.fileEndingText)))
							{
								Intent intent = OpenFiles
										.getTextFileIntent(file);
								startActivity(intent);
							} else if (checkEndsWithInStringArray(
									filePathString,
									getResources().getStringArray(
											R.array.fileEndingWord)))
							{
								Intent intent = OpenFiles
										.getWordFileIntent(file);
								startActivity(intent);

							} else
							{
								Intent intent = new Intent(CommonEdit.this,
										ShowFileInfo.class);
								Bundle bundle = new Bundle();
								bundle.putString("file", filePathString);
								intent.putExtras(bundle);
								startActivity(intent);
							}

						} else if (spanString.startsWith("Photo"))
						{

							String filePathString = spanString.substring(
									spanString.indexOf("Photo^_^[") + 9,
									spanString.indexOf("]^_^"));
							File file = new File(filePathString);
							Intent intent = OpenFiles.getImageFileIntent(file);
							startActivity(intent);
						} else if (spanString.startsWith("Record"))
						{
							String filePathString = spanString.substring(
									spanString.indexOf("Record^_^[") + 10,
									spanString.indexOf("]^_^"));
							playSoundFile(filePathString);
						} else if (spanString.startsWith("Picture"))
						{
							String filePathString = spanString.substring(
									spanString.indexOf("Picture^_^[") + 11,
									spanString.indexOf("]^_^"));
							File file = new File(filePathString);
							Intent intent = OpenFiles.getImageFileIntent(file);
							startActivity(intent);
						} else if (spanString.startsWith("Draw"))
						{
							String filePathString = spanString.substring(
									spanString.indexOf("Draw^_^[") + 8,
									spanString.indexOf("]^_^"));
							File file = new File(filePathString);
							Intent intent = OpenFiles.getImageFileIntent(file);
							startActivity(intent);

						} else if (spanString.startsWith("Video"))
						{
							String filePathString = spanString.substring(
									spanString.indexOf("Video^_^[") + 9,
									spanString.indexOf("]^_^"));
							File file = new File(filePathString);
							Intent intent = OpenFiles.getVideoFileIntent(file);
							startActivity(intent);
						} else if (spanString.startsWith("Table"))
						{
							String content = spanString.substring(
									spanString.indexOf("Table^_^[") + 9,
									spanString.indexOf("]^_^"));
							//删除原来的文本
							table_start = start;
							table_end = end;
							Bundle bundle = new Bundle();
							bundle.putBoolean("iswrite", true);
							bundle.putString("content", content);
							Intent intent = new Intent(CommonEdit.this,
									TableEdit.class);
							intent.putExtras(bundle);
							startActivityForResult(intent, 140);
						}
						mBodyText.requestFocus();
						mBodyText.clearFocus();
					}

				}

			}
		});

		mBodyText.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				listView.setVisibility(View.GONE);
				catagory_add_Button.setVisibility(View.GONE);
				facePager.setVisibility(View.GONE);
				page_select.setVisibility(View.GONE);
				return false;
			}
		});

		// 返回按钮
		edit_return_button = (ImageButton) findViewById(R.id.edit_return);
		edit_return_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String title = mTitleText.getText().toString();

				String body = mBodyText.getText().toString();
				String catagory = catagory_TextView.getText().toString();
				if (("".equals(title) && "".equals(body))
						|| (exit_title.equals(title) && exit_body.equals(body) && exit_catagory
								.equals(catagory))
								|| (exit_title.equals(getResources().getString(R.string.without_title)) && title.equals("")
										&& exit_body.equals(body) && exit_catagory
										.equals(catagory)))
				{
					finish();
				} else
				{
					exitDialgo(CommonEdit.this);
				}
			}
		});

		// 保存按钮响应
		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String catagory = catagory_TextView.getText().toString();
				String title = mTitleText.getText().toString();
				String body = mBodyText.getText().toString();

				RemoteViews views = new RemoteViews(CommonEdit.this.getPackageName(), R.layout.widget_layout);

				if (mRowId != null)
				{
					if (title.equals(""))
					{
						MainActivity.mDbHelper.updateNote(mRowId, getResources().getString(R.string.without_title), body,
								0, catagory);
						MySimpleCursorAdapter.imageCache.detectImage(body,
								mRowId);
						detect.create(mRowId, body, CommonEdit.this);

						if(DoNoteWidgetProvider.note_id == mRowId){
							views.setTextViewText(R.id.text_title,getResources().getString(R.string.without_title) );
							views.setTextViewText(R.id.text_body, getEditContent(body));
							DoNoteWidgetProvider.isdelete = false;
						}

					} else
					{
						MainActivity.mDbHelper.updateNote(mRowId, title, body,
								0, catagory);
						MySimpleCursorAdapter.imageCache.detectImage(body,
								mRowId);
						detect.create(mRowId, body, CommonEdit.this);

						if(DoNoteWidgetProvider.note_id == mRowId){
							views.setTextViewText(R.id.text_title, title );
							views.setTextViewText(R.id.text_body, getEditContent(body));
							DoNoteWidgetProvider.isdelete = false;
						}

					}


				} else if (!("".equals(title) && "".equals(body)))
				{
					if (title.equals(""))
					{
						long id = MainActivity.mDbHelper.createNote(getResources().getString(R.string.without_title),
								body, 0, catagory);
						MySimpleCursorAdapter.imageCache.detectImage(body, id);
						detect.create(id, body, CommonEdit.this);


					} else
					{
						long id = MainActivity.mDbHelper.createNote(title,
								body, 0, catagory);
						MySimpleCursorAdapter.imageCache.detectImage(body, id);
						detect.create(id, body, CommonEdit.this);

					}
				} else
				{
					Toast.makeText(CommonEdit.this, getResources().getString(R.string.blank_note), Toast.LENGTH_SHORT)
					.show();
				}

				ComponentName widget  = new ComponentName(CommonEdit.this,DoNoteWidgetProvider.class);
				AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
				manager.updateAppWidget(widget, views);


			}
		});

		// 展开分类列表响应
		catagory_TextView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (listView.getVisibility() == View.GONE)
				{
					listView.setVisibility(View.VISIBLE);
					catagory_add_Button.setVisibility(View.VISIBLE);
				} else
				{
					listView.setVisibility(View.GONE);
					catagory_add_Button.setVisibility(View.GONE);
					facePager.setVisibility(View.GONE);
					page_select.setVisibility(View.GONE);
				}
			}
		});

		moreButton = (ImageButton) findViewById(R.id.menu_more);

		moreButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(ges_state){
					if(mBodyText.length()!=0){
						boolean isdelete = false;
						Editable edit = mBodyText.getEditableText();
						int selectionStart = mBodyText.getSelectionStart();
						Spanned s = mBodyText.getText();
						ImageSpan[] imageSpans = s.getSpans(0, s.length(),
								ImageSpan.class);
						for (ImageSpan span : imageSpans)
						{
							String spanString = span.getSource();
							int start = s.getSpanStart(span);
							int end = s.getSpanEnd(span);

							if (selectionStart >= start && selectionStart == end
									&& spanString != null){
								edit.delete(start, end);
								mBodyText.setSelection(start);
								isdelete = true;
							}
						}
						if(!isdelete){
							edit.delete(edit.length()-1, edit.length());
						}
					}
				}
				else {
					// imm.hideSoftInputFromWindow(mBodyText.getWindowToken(), 0);
					PopupWindow popupWindow = menuPopupwindow(CommonEdit.this);
					// int yoff = popupWindow.getHeight() - moreButton.getHeight();
					// popupWindow.showAsDropDown(moreButton, 0, -yoff/8);
					popupWindow.showAtLocation(mianLayout, Gravity.RIGHT
							| Gravity.BOTTOM, 0, moreButton.getHeight());
				}
			}
		});

		recordButton = (ImageButton) findViewById(R.id.menu_record);
		recordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ges_state){

					ges_state = false;

					gesView.setVisibility(View.GONE);

					recordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_record));

					photoButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_shot));

					faceButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_face));

					moreButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_more));

					Toast.makeText(getApplicationContext(), "退出手写", Toast.LENGTH_SHORT).show();
				}
				else{
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED))
					{
						Intent intent = new Intent(CommonEdit.this,
								NoteRecord.class);
						startActivityForResult(intent, 14);
					} else
					{
						Toast.makeText(CommonEdit.this, getResources().getString(R.string.without_disk), Toast.LENGTH_LONG)
						.show();
					}
				}
			}
		});

		faceButton = (ImageButton) findViewById(R.id.menu_face);

		faceButton.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ges_state){
					Editable edit = mBodyText.getEditableText();
					int selectionStart = mBodyText.getSelectionStart();
					edit.insert(selectionStart, "\n");
				}
				else{
					if (facePager.isShown())
					{
						facePager.setVisibility(facePager.GONE);
						page_select.setVisibility(page_select.GONE);
					} else
					{
						facePager.setVisibility(facePager.VISIBLE);
						page_select.setVisibility(page_select.VISIBLE);
					}
				}
				imm.hideSoftInputFromWindow(mBodyText.getWindowToken(), 0);

			}
		});

		photoButton = (ImageButton) findViewById(R.id.menu_photo);
		photoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ges_state){
					Editable edit = mBodyText.getEditableText();
					int selectionStart = mBodyText.getSelectionStart();
					edit.insert(selectionStart, " ");
				}
				else {
					String status = Environment.getExternalStorageState();
					// 判断sdcard是否存在
					if (status.equals(Environment.MEDIA_MOUNTED))
					{
						try
						{
							File dir = new File(pathPhoto);
							if (!dir.exists())
								dir.mkdirs();
							new DateFormat();
							String namePhoto = DateFormat.format("yyyyMMdd_hhmmss",
									Calendar.getInstance(Locale.CHINA)) + ".jpg";
							photofile = pathPhoto + namePhoto;
							File file = new File(photofile);
							Uri u = Uri.fromFile(file);
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
							startActivityForResult(intent, 21);
						} catch (ActivityNotFoundException e)
						{
							// TODO Auto-generated catch block
							Toast.makeText(CommonEdit.this, getResources().getString(R.string.cannot_find_path),
									Toast.LENGTH_LONG).show();
						}
					} else
					{
						Toast.makeText(CommonEdit.this, getResources().getString(R.string.without_disk), Toast.LENGTH_LONG)
						.show();
					}
				}
			}
		});

	}

	private PopupWindow menuPopupwindow(Context cx) {
		if (window == null)
		{
			window = new PopupWindow(cx);
			View contentView = LayoutInflater.from(this).inflate(
					R.layout.edit_menu, null);
			window.setContentView(contentView);
			window.setWidth(LayoutParams.WRAP_CONTENT);
			window.setHeight(LayoutParams.WRAP_CONTENT);
			insertLocation = (Button) contentView.findViewById(R.id.menu_site);
			insertLocation.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (isOpenNetwork())
					{
						Toast.makeText(CommonEdit.this,getResources().getString(R.string.locating),
								Toast.LENGTH_SHORT).show();
						if (mLocationClient != null
								&& mLocationClient.isStarted())
						{
							mLocationClient.requestLocation();
						} else
						{
							mLocationClient.start();
						}
						window.dismiss();
					} else
					{
						Toast.makeText(CommonEdit.this, getResources().getString(R.string.interner_error),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			view_table = (Button) contentView.findViewById(R.id.menu_table);
			view_table.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Bundle bundle = new Bundle();
					bundle.putBoolean("iswrite", false);
					Intent intent = new Intent(CommonEdit.this, TableEdit.class);
					intent.putExtras(bundle);
					startActivityForResult(intent, 70);
					window.dismiss();
				}

			});
			additionButton = (Button) contentView
					.findViewById(R.id.menu_addition);
			additionButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(CommonEdit.this, FileView.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, 42);
					window.dismiss();
				}
			});

			gestureButton = (Button) contentView.findViewById(R.id.menu_gesture);
			gestureButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					imm.hideSoftInputFromWindow(mBodyText.getWindowToken(),
							0);
					gesView.setVisibility(View.VISIBLE);
					gesView.setFocusable(true);	

					ges_state = true;

					recordButton.setImageDrawable(getResources().getDrawable(R.drawable.ges_ret));

					photoButton.setImageDrawable(getResources().getDrawable(R.drawable.ges_blank));

					faceButton.setImageDrawable(getResources().getDrawable(R.drawable.ges_enter));

					moreButton.setImageDrawable(getResources().getDrawable(R.drawable.ges_delete));

					window.dismiss();

					Toast.makeText(getApplicationContext(), "进入手写", Toast.LENGTH_SHORT).show();
				}
			});

			movieButton = (Button) contentView.findViewById(R.id.menu_movie);
			movieButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String status = Environment.getExternalStorageState();
					// 判断sdcard是否存在
					if (status.equals(Environment.MEDIA_MOUNTED))
					{
						try
						{
							File dir = new File(pathVideo);
							if (!dir.exists())
								dir.mkdirs();
							new DateFormat();
							String nameVideo = DateFormat.format(
									"yyyyMMdd_hhmmss",
									Calendar.getInstance(Locale.CHINA))
									+ ".mp4";
							videofile = pathVideo + nameVideo;
							File file = new File(videofile);
							Uri u = Uri.fromFile(file);
							Intent intent = new Intent(
									MediaStore.ACTION_VIDEO_CAPTURE);
							intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
							intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
							startActivityForResult(intent, 22);
						} catch (ActivityNotFoundException e)
						{
							// TODO Auto-generated catch block
							Toast.makeText(CommonEdit.this,getResources().getString(R.string.cannot_find_path),
									Toast.LENGTH_LONG).show();
						}
					} else
					{
						Toast.makeText(CommonEdit.this,getResources().getString(R.string.without_disk),
								Toast.LENGTH_LONG).show();
					}
					window.dismiss();
				}
			});

			pictureButton = (Button) contentView
					.findViewById(R.id.menu_picture);
			pictureButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED))
					{
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("image/*");
						if (intent != null)
						{
							startActivityForResult(intent, 49);
						}
					} else
					{
						Toast.makeText(CommonEdit.this,getResources().getString(R.string.without_disk),
								Toast.LENGTH_SHORT).show();
					}
					window.dismiss();
				}
			});

			drawButton = (Button) contentView.findViewById(R.id.menu_draw);
			drawButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// TODO Auto-generated method
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED))
					{
						Intent intent = new Intent(CommonEdit.this, Draw.class);
						startActivityForResult(intent, 35);
					} else
					{
						Toast.makeText(CommonEdit.this, getResources().getString(R.string.without_disk),
								Toast.LENGTH_SHORT).show();
					}
					window.dismiss();
				}
			});

			voiceButton = (Button) contentView.findViewById(R.id.menu_voice);
			voiceButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					window.dismiss();
					XsconCreateDialog("appid=51b122a7").show();
					window.dismiss();
				}
			});

			// 设置PopupWindow外部区域是否可触摸
			window.setFocusable(true); // 设置PopupWindow可获得焦点
			window.setTouchable(true); // 设置PopupWindow可触摸
			window.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
			// window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			// window.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		}
		return window;
	}

	private void initViewPager() {
		LayoutInflater inflater = LayoutInflater.from(this);
		grids = new ArrayList<GridView>();
		gView1 = (GridView) inflater.inflate(R.layout.grid1, null);
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		// 生成24个表情

		for (int i = 0; i < 24; i++)
		{
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", expressionImages[i]);
			listItems.add(listItem);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(mCon, listItems,
				R.layout.singleexpression, new String[] { "image" },
				new int[] { R.id.faceImage });
		gView1.setAdapter(simpleAdapter);
		gView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bitmap bitmap = null;
				bitmap = BitmapFactory.decodeResource(getResources(),
						expressionImages[arg2 % expressionImages.length]);
				ImageSpan imageSpan = new ImageSpan(mCon, bitmap,
						ImageSpan.ALIGN_BASELINE);

				facefile = "Face:"
						+ expressionImageNames[arg2].substring(1,
								expressionImageNames[arg2].length() - 1);

				SpannableString spannableString = new SpannableString(facefile);

				spannableString.setSpan(imageSpan, 0, facefile.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				SetCursor(spannableString);

			}
		});

		grids.add(gView1);

		gView2 = (GridView) inflater.inflate(R.layout.grid2, null);
		grids.add(gView2);

		gView3 = (GridView) inflater.inflate(R.layout.grid3, null);
		grids.add(gView3);

		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return grids.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(grids.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(grids.get(position));
				return grids.get(position);
			}
		};

		facePager.setAdapter(mPagerAdapter);

		facePager.setOnPageChangeListener(new GuidePageChangeListener());
	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageSelected(int arg0) {
			switch (arg0)
			{
			case 0:
				page0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_focused));
				page1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				break;
			case 1:
				page1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_focused));
				page0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				page2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
				// 生成24个表情
				for (int i = 0; i < 24; i++)
				{
					Map<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("image", expressionImages1[i]);
					listItems.add(listItem);
				}

				SimpleAdapter simpleAdapter = new SimpleAdapter(mCon,
						listItems, R.layout.singleexpression,
						new String[] { "image" }, new int[] { R.id.faceImage });
				gView2.setAdapter(simpleAdapter);
				gView2.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Bitmap bitmap = null;
						bitmap = BitmapFactory.decodeResource(getResources(),
								expressionImages1[arg2
								                  % expressionImages1.length]);
						ImageSpan imageSpan = new ImageSpan(mCon, bitmap,
								ImageSpan.ALIGN_BASELINE);

						facefile = "Face:"
								+ expressionImageNames1[arg2]
										.substring(1,
												expressionImageNames1[arg2]
														.length() - 1);

						SpannableString spannableString = new SpannableString(
								facefile);
						spannableString.setSpan(imageSpan, 0,
								facefile.length(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						// 编辑框设置数据
						SetCursor(spannableString);
					}
				});
				break;
			case 2:
				page2.setImageDrawable(getResources().getDrawable(
						R.drawable.page_focused));
				page1.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				page0.setImageDrawable(getResources().getDrawable(
						R.drawable.page_unfocused));
				List<Map<String, Object>> listItems1 = new ArrayList<Map<String, Object>>();
				// 生成24个表情
				for (int i = 0; i < 24; i++)
				{
					Map<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("image", expressionImages2[i]);
					listItems1.add(listItem);
				}

				SimpleAdapter simpleAdapter1 = new SimpleAdapter(mCon,
						listItems1, R.layout.singleexpression,
						new String[] { "image" }, new int[] { R.id.faceImage });
				gView3.setAdapter(simpleAdapter1);
				gView3.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Bitmap bitmap = null;
						bitmap = BitmapFactory.decodeResource(getResources(),
								expressionImages2[arg2
								                  % expressionImages2.length]);

						ImageSpan imageSpan = new ImageSpan(mCon, bitmap,
								ImageSpan.ALIGN_BASELINE);

						facefile = "Face:"
								+ expressionImageNames2[arg2]
										.substring(1,
												expressionImageNames2[arg2]
														.length() - 1);

						SpannableString spannableString = new SpannableString(
								facefile);
						spannableString.setSpan(imageSpan, 0,
								facefile.length(),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						// 编辑框设置数据
						SetCursor(spannableString);
					}
				});
				break;

			}
		}
	}

	protected Dialog XsconCreateDialog(String id) {
		RecognizerDialog recognizerDialog = new RecognizerDialog(
				CommonEdit.this, id);
		recognizerDialog.setEngine("sms", null, null);
		recognizerDialog.setListener(new RecognizerDialogListener() {
			@Override
			public void onResults(ArrayList<RecognizerResult> results,
					boolean arg1) {
				StringBuffer result = new StringBuffer();
				for (RecognizerResult r : results)
				{
					result.append(r.text);
				}
				Editable edit = mBodyText.getEditableText();
				int index = mBodyText.getSelectionStart();
				if (index < 0 || index >= edit.length())
				{
					edit.append(result.toString());
				} else
				{
					edit.insert(index, result.toString());// 光标所在位置插入文字
				}
			}

			@Override
			public void onEnd(SpeechError arg0) {

			}
		});
		return recognizerDialog;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1)
		{
			Intent mIntent = new Intent();
			setResult(RESULT_OK, mIntent);
			Intent intent = new Intent(CommonEdit.this, MainActivity.class);
			startActivity(intent);
		}
		// 添加Record
		else if (requestCode == 14 && resultCode != RESULT_CANCELED)
		{
			Bundle bundle = data.getExtras();
			String nameString = bundle.getString("name");
			String filePath = "Record^_^[" + nameString + "]^_^ ";
			SetCursor(showIcon.showVoice(nameString, filePath));
		}

		// 添加Draw
		else if (requestCode == 35 && resultCode != RESULT_CANCELED)
		{
			draw = null;
			Bundle bundle = data.getExtras();
			drawfile = pathDraw + bundle.getString("draw");
			draw = BitmapFactory.decodeFile(drawfile);
			String information = "Draw^_^[" + drawfile + "]^_^ ";
			draw = com.donote.util.ShowIcon.zoomBitmap(draw);
			Drawable drawable = new BitmapDrawable(getResources(), draw);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			ImageSpan picSpan = new ImageSpan(drawable, information,
					ImageSpan.ALIGN_BASELINE);
			SpannableString spanPicture = new SpannableString(information);
			spanPicture.setSpan(picSpan, 0, spanPicture.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			SetCursor(spanPicture);
		}

		// 添加Photo(拍照)
		else if (requestCode == 21 && resultCode != RESULT_CANCELED)
		{
			Intent intent = new Intent(this, ImageHandle.class);
			intent.putExtra("filepath", photofile);
			startActivityForResult(intent, 50);
		}

		// 添加录像
		else if (requestCode == 22 && resultCode != RESULT_CANCELED)
		{
			SetCursor(showIcon.showVideo(videofile));
		}

		// 添加File
		else if (requestCode == 42 && resultCode != RESULT_CANCELED)
		{

			Bundle bundle = data.getExtras();
			File file = new File(bundle.getString("file"));
			String filePathString = file.getAbsolutePath();
			if (checkEndsWithInStringArray(filePathString, getResources()
					.getStringArray(R.array.fileEndingImage)))
			{
				// 附件为图片
				Intent intent = new Intent(this, ImageHandle.class);
				intent.putExtra("filepath", filePathString);
				startActivityForResult(intent, 50);
			} else if (checkEndsWithInStringArray(filePathString,
					getResources().getStringArray(R.array.fileEndingAudio)))
			{
				// 附件为音频
				String filePath = "Record^_^[" + filePathString + "]^_^ ";
				SetCursor(showIcon.showVoice(filePathString, filePath));

			} else if (checkEndsWithInStringArray(filePathString,
					getResources().getStringArray(R.array.fileEndingVideo)))
			{
				// 附件为视频
				SetCursor(showIcon.showVideo(filePathString));
			} else
			{
				// 其他
				SetCursor(showIcon.showFiles(file.getName(), filePathString));
			}
		}

		// 添加Picture
		else if (requestCode == 50 && resultCode != RESULT_CANCELED)
		{
			// 获取路径
			Drawable drawable;
			String filePath = data.getExtras().getString("filepath");
			String information;
			if (filePath.startsWith(getExternalCacheDir().getAbsolutePath()))
			{
				File oldFile = new File(filePath);
				File dir = new File(pathPicture);
				if (!dir.exists())
					dir.mkdirs();
				filePath = pathPicture + oldFile.getName();
				File newFile = new File(filePath);
				com.donote.imagehandler.ImageHandleGlobal.CopyFile(oldFile,
						newFile);
				filePath = newFile.getAbsolutePath();
				information = "Picture^_^[" + newFile.getAbsolutePath()
						+ "]^_^ ";
			} else
			{
				information = "Picture^_^[" + filePath + "]^_^ ";
			}
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, opt);
			picture = com.donote.util.ShowIcon.readBitmapAutoSize(filePath,
					MainActivity.Width, (float) opt.outHeight
					* ((float) MainActivity.Width / opt.outWidth),
					com.donote.util.ShowIcon.readPictureDegree(filePath));
			if (picture != null)
			{
				picture = com.donote.util.ShowIcon.zoomBitmap(picture);
				drawable = new BitmapDrawable(getResources(), picture);
			} else
			{
				drawable = mCon.getResources().getDrawable(
						R.drawable.ic_default_image);
			}
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			ImageSpan picSpan = new ImageSpan(drawable, information,
					ImageSpan.ALIGN_BASELINE);
			SpannableString spanPicture = new SpannableString(information);
			spanPicture.setSpan(picSpan, 0, spanPicture.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			SetCursor(spanPicture);
		}

		else if (requestCode == 49 && resultCode != RESULT_CANCELED)
		{
			Uri pictureUri = data.getData();
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(pictureUri, proj, null, null, null);
			cursor.moveToFirst();
			Intent intent = new Intent(this, ImageHandle.class);
			intent.putExtra("filepath", cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA)));
			startActivityForResult(intent, 50);
		}

		else if (requestCode == 70 && resultCode != RESULT_CANCELED)
		{
			Bundle bundle = data.getExtras();
			String idString = bundle.getString("content");

			String information = "Table^_^[" + idString + "]^_^ ";
			String title = idString.substring(
					idString.indexOf("<--title:") + 9, idString.indexOf("-->"));
			if (title.equals(""))
			{
				title = getResources().getString(R.string.without_title);
			}
			SetCursor(showIcon.showTable(title, information));
		}else if (requestCode == 77 && resultCode != RESULT_CANCELED)
		{
			Bundle bundle = data.getExtras();
			String idString = bundle.getString("content");

			String information = "Table^_^[" + idString + "]^_^ ";
			String title = idString.substring(
					idString.indexOf("<--title:") + 9, idString.indexOf("-->"));
			if (title.equals(""))
			{
				title = getResources().getString(R.string.without_title);
			}
			SetCursor(showIcon.showTable(title, information));
		}

		else if (requestCode == 140 && resultCode != RESULT_CANCELED){

			Bundle bundle = data.getExtras();
			String idString = bundle.getString("content");

			String information = "Table^_^[" + idString + "]^_^ ";
			Editable editable = mBodyText.getEditableText();
			editable.delete(table_start, table_end);
			editable.insert(table_start, information);

			String content = mBodyText.getText().toString();

			editable.clear();

			initEdit(content);

			//调用函数wxlwxl

		}

	}

	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 19;
			if ((Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math
					.abs(z) > medumValue)
					&& isshake == false
					&& shake_info != 0)
			{
				vibrator.vibrate(200);
				isshake = true;
				isshow = true;
				builder = new Builder(CommonEdit.this);

				builder.setMessage(getResources().getString(R.string.confirm_rewrite));

				builder.setTitle(getResources().getString(R.string.tip));

				builder.setPositiveButton(getResources().getString(R.string.rewrite),
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						mBodyText.setText("");
						dialog.dismiss();
						isshake = false;
						isshow = false;
					}
				});

				builder.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss();
						isshake = false;
						isshow = false;
					}
				});
				builder.create().show();
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	// hankwing添加于2013/4/16*************************************
	@Override
	protected void onResume() {
		StatService.onResume(this);
		if (sensorManager != null)
		{// 注册监听器
			sensorManager.registerListener(sensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
			// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
		}

		super.onResume();
	}

	private void playSoundFile(String temp) {
		Intent intent = new Intent(CommonEdit.this, PlayMusic.class);
		Bundle bundle = new Bundle();
		bundle.putString("playRec", temp);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		listView.setVisibility(View.GONE);
		catagory_add_Button.setVisibility(View.GONE);
		facePager.setVisibility(View.GONE);
		page_select.setVisibility(View.GONE);
		return super.onTouchEvent(event);
	}

	// 长按分类列表弹出菜单
	private final class OptionMenu implements OnCreateContextMenuListener {
		@Override
		public void onCreateContextMenu(ContextMenu arg0, View arg1,
				ContextMenuInfo arg2) {
			Log.i("display", "enter");
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) arg2;
			Cursor cursor = mNoteCursor;
			cursor.moveToPosition(info.position);
			if (!cursor.getString(
					cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME))
					.equals(getResources().getString(R.string.default_group)))
			{
				arg0.add(0, R.id.catagory_delete, 0, getResources().getString(R.string.delete));
				arg0.add(0, R.id.catagory_rename, 0, getResources().getString(R.string.rename));
			}
		}
	}

	// 分类列表菜单响应
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Cursor cursor = mNoteCursor;
		cursor.moveToPosition(info.position);
		switch (item.getItemId())
		{
		case R.id.catagory_delete:
			catagory_delete_showDialog(info);
			return true;
		case R.id.catagory_rename:
			catagory_OldString = cursor.getString(cursor
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME));
			showDialog();
			cRowId = info.id;
			renderListView();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	// 选中分类响应
	public final class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Cursor c = mNoteCursor;
			c.moveToPosition(position);
			catagory_TextView.setText(c.getString(c
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME)));
			listView.setVisibility(View.GONE);
			catagory_add_Button.setVisibility(View.GONE);
			facePager.setVisibility(View.GONE);
			page_select.setVisibility(View.GONE);
		}
	}

	// 键盘返回键响应
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			String catagory = catagory_TextView.getText().toString();
			String title = mTitleText.getText().toString();
			String body = mBodyText.getText().toString();
			if (("".equals(title) && "".equals(body))
					|| (exit_title.equals(title) && exit_body.equals(body) && exit_catagory
							.equals(catagory))
							|| (exit_title.equals(getResources().getString(R.string.without_title)) && title.equals("")
									&& exit_body.equals(body) && exit_catagory
									.equals(catagory)))
			{
				finish();
			} else if (mRowId == null) {
				confirmButton.performClick();
			}else 
			{
				exitDialgo(CommonEdit.this);
			}
		}

		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
		{
			textSize += 4;
			mTitleText.setTextSize(textSize);
			mBodyText.setTextSize(textSize);
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
		{
			textSize -= 4;
			mTitleText.setTextSize(textSize);
			mBodyText.setTextSize(textSize);
			return true;
		}
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	// 提示对话框
	public void exitDialgo(Context context) {
		// TODO Auto-generated method stub

		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(getResources().getString(R.string.weather_to_save));
		builder.setTitle(getResources().getString(R.string.exit));
		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent(CommonEdit.this, MainActivity.class);
				startActivity(intent);
			}
		});

		builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String catagory = catagory_TextView.getText().toString();
				String title = mTitleText.getText().toString();
				String body = mBodyText.getText().toString();
				if (mRowId != null)
				{
					if (title.equals(""))
					{
						MainActivity.mDbHelper.updateNote(mRowId, getResources().getString(R.string.without_title), body,
								0, catagory);
						MySimpleCursorAdapter.imageCache.detectImage(body,
								mRowId);
						detect.create(mRowId, body, CommonEdit.this);
					} else
					{
						MainActivity.mDbHelper.updateNote(mRowId, title, body,
								0, catagory);
						MySimpleCursorAdapter.imageCache.detectImage(body,
								mRowId);
						detect.create(mRowId, body, CommonEdit.this);
					}
				} else if (!("".equals(title) && "".equals(body)))
				{
					if (title.equals(""))
					{
						long id = MainActivity.mDbHelper.createNote(getResources().getString(R.string.without_title),
								body, 0, catagory);
						MySimpleCursorAdapter.imageCache.detectImage(body, id);
						detect.create(id, body, CommonEdit.this);

					} else
					{
						long id = MainActivity.mDbHelper.createNote(title,
								body, 0, catagory);
						MySimpleCursorAdapter.imageCache.detectImage(body, id);
						detect.create(id, body, CommonEdit.this);
					}
				}
			}
		});
		builder.create().show();
	}

	// 刷新分类ListView
	@SuppressWarnings("deprecation")
	public void renderListView() {

		mNoteCursor = MainActivity.mDbHelper.getAllCatagory();
		String[] from = new String[] { NoteDbAdapter.KEY_NAME };
		int[] to = new int[] { R.id.item2 };
		SimpleCursorAdapter notes = new SimpleCursorAdapter(CommonEdit.this,
				R.layout.catagory_setting_lists, mNoteCursor, from, to);
		listView.setAdapter(notes);

	}

	// 长按分类列表对话框
	protected void showDialog() {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.edit_dialog,
				(ViewGroup) findViewById(R.id.edit_dialog));
		final EditText et = (EditText) view.findViewById(R.id.et);
		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.edit)).setView(view)
		.setPositiveButton(getResources().getString(R.string.confirm), new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				String name = et.getText().toString();
				if (cRowId != null)
				{
					MainActivity.mDbHelper.updateCatagory(cRowId, name);
					MainActivity.mDbHelper.updata_catagory_notes(
							catagory_OldString, name);
					catagory_TextView.setText(name);
				} else if (!name.equals(""))
				{
					MainActivity.mDbHelper.createCatagory(name);
					catagory_TextView.setText(name);
				} else
				{
					Toast.makeText(CommonEdit.this, getResources().getString(R.string.cannot_add_empty_group),
							Toast.LENGTH_SHORT).show();
				}
				Intent mIntent = new Intent();
				setResult(RESULT_OK, mIntent);
				renderListView();

				listView.setVisibility(View.GONE);
				catagory_add_Button.setVisibility(View.GONE);
				facePager.setVisibility(View.GONE);
				page_select.setVisibility(View.GONE);
			}
		}).setNegativeButton(getResources().getString(R.string.cancel), null).create().show();

	}

	protected void catagory_delete_showDialog(final AdapterContextMenuInfo info) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.catagory_delete_dialog,
				(ViewGroup) findViewById(R.id.catagory_delete_dialog));
		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.confirm_delete)).setView(view)
		.setPositiveButton(getResources().getString(R.string.confirm), new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				MainActivity.mDbHelper.deleteCatagory(info.id);
				renderListView();
				listView.setVisibility(View.GONE);
				catagory_add_Button.setVisibility(View.GONE);
				facePager.setVisibility(View.GONE);
				page_select.setVisibility(View.GONE);
			}
		}).setNegativeButton(getResources().getString(R.string.cancel), null).create().show();

	}

	@Override
	protected void onRestart() {
		listView.setVisibility(View.GONE);
		facePager.setVisibility(View.GONE);
		page_select.setVisibility(View.GONE);
		super.onRestart();
	}

	// 通过文件名判断是什么类型的文件
	public static boolean checkEndsWithInStringArray(String checkItsEnd,
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
			Matcher photoMatcher = photoPattern.matcher(exit_body);
			while (photoMatcher.find())
			{
				if (photoMatcher.group(1).equals("Photo"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Photo^_^[") + 9,
							idString.indexOf("]^_^"));
					ps.setSpan(showIcon.getImage(photoMatcher, idString),
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					Message message = new Message();
					message.what = 2;
					message.obj = ps;
					handler.sendMessage(message);
				}

				else if (photoMatcher.group(1).equals("Picture"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Picture^_^[") + 11,
							idString.indexOf("]^_^"));

					ps.setSpan(showIcon.getImage(photoMatcher, idString),
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					Message mes = new Message();
					mes.what = 2;
					mes.obj = ps;
					handler.sendMessage(mes);
				}
				else if (photoMatcher.group(1).equals("Gesture")){

					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Gesture^_^[") + 11,
							idString.indexOf("]^_^"));
					Bitmap bitmap = BitmapFactory.decodeFile(idString);
					Drawable drawable = new BitmapDrawable(getResources(),bitmap);
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight());
					ImageSpan gesSpan = new ImageSpan(drawable, photoMatcher.group());
					ps.setSpan(gesSpan,
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

					Message mes = new Message();
					mes.what = 2;
					mes.obj = ps;
					handler.sendMessage(mes);
				}
				else if (photoMatcher.group(1).equals("Draw"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Draw^_^[") + 8,
							idString.indexOf("]^_^"));

					ps.setSpan(showIcon.getImage(photoMatcher, idString),
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

					Message mes = new Message();
					mes.what = 2;
					mes.obj = ps;
					handler.sendMessage(mes);
				} else if (photoMatcher.group(1).equals("Video"))
				{
					String idString = photoMatcher.group();
					idString = idString.substring(
							idString.indexOf("Video^_^[") + 9,
							idString.indexOf("]^_^"));
					ps.setSpan(showIcon.getVideoImage(photoMatcher, idString),
							photoMatcher.start(), photoMatcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					Message message = new Message();
					message.what = 2;
					message.obj = ps;
					handler.sendMessage(message);
				}

			}
			Message message = new Message();
			message.what = 3;
			handler.sendMessage(message);// 结束线程

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		StatService.onPause(this);
		sensorManager.unregisterListener(sensorEventListener);
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		if (isshake == true)
		{
			isshow = true;
		} else if (isshow == false)
		{
			isshake = false;
		}
		if (imageThread != null)
		{
			Thread dummy = imageThread;
			imageThread = null;
			dummy.interrupt();
		}
		sensorManager.unregisterListener(sensorEventListener);
		super.onStop();
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
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		if (isshow == false)
		{
			isshake = false;
		} else if (isshow == true)
		{
			isshake = true;
		}
		super.onStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mNoteCursor.close();
		super.onDestroy();
	}

	private boolean isOpenNetwork() {
		if (connManager.getActiveNetworkInfo() != null)
		{
			return connManager.getActiveNetworkInfo().isAvailable();
		}
		return false;
	}

	public static void SetCursor(SpannableString span) {
		int start = mBodyText.getSelectionStart();
		Editable mbody = mBodyText.getText();
		mbody.insert(start, span);
		mbody.insert(start + span.length(), " ");

	}

	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(500); // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		// option.setScanSpan(3000);
		option.setAddrType("all");
		option.setPriority(LocationClientOption.NetWorkFirst); // 设置网络优先
		option.setPoiNumber(10);
		option.disableCache(false);
		mLocationClient.setLocOption(option);
	}

	private String getEditContent(String bodyString) {
		bodyString = bodyString.replaceAll(
				"(Photo\\^\\_\\^\\[(.*?)\\]\\^\\_\\^)", "[图]");
		bodyString = bodyString.replaceAll(
				"(Record\\^\\_\\^\\[(.*?)\\]\\^\\_\\^)", "[录音]");
		bodyString = bodyString.replaceAll(
				"(Draw\\^\\_\\^\\[(.*?)\\]\\^\\_\\^)", "[绘画]");
		bodyString = bodyString.replaceAll(
				"(File\\^\\_\\^\\[(.*?)\\]\\^\\_\\^)", "[文件]");
		bodyString = bodyString.replaceAll(
				"(Picture\\^\\_\\^\\[(.*?)\\]\\^\\_\\^)", "[图]");
		bodyString = bodyString.replaceAll(
				"(Video\\^\\_\\^\\[(.*?)\\]\\^\\_\\^)", "[视频]");
		bodyString = bodyString.replaceAll(
				"(Text\\^\\_\\^\\[(.*?)\\]\\^\\_\\^)", "[图形]");
		bodyString = bodyString.replaceAll(
				"(Table\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)", "[表格]");
		bodyString = bodyString.replaceAll(
				"(Cloud\\^\\_\\^\\[(.*?)\\]\\[(.*?)\\]\\^\\_\\^)", "[图形]");
		bodyString = bodyString.replace(" ", "");
		bodyString = bodyString.replace("\n", " ");
		bodyString = bodyString.replaceAll("Face:f" + "\\w{3}", "[表情]");
		bodyString = bodyString.replaceAll("(Face\\^\\_\\^\\[(.*?)\\]\\[(.*?)\\]\\^\\_\\^)","[表情]");
		return bodyString;
	}

}// activity
