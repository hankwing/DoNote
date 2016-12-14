package com.donote.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.mobstat.StatService;
import com.donote.adapter.MySimpleCursorAdapter;
import com.donote.adapter.NoteDbAdapter;
import com.donote.alarm.CreateAlarm;
import com.donote.filebrowser.FileView;
import com.donote.filebrowser.OpenFiles;
import com.donote.freestyle.DefineMenu;
import com.donote.freestyle.DefineShape;
import com.donote.freestyle.GraphText;
import com.donote.freestyle.ViewSite;
import com.donote.imagehandler.ImageHandle;
import com.donote.imagehandler.ImageMemoryCache;
import com.donote.util.Expressions;
import com.donote.util.ShowIcon;
import com.donote.widget.DoNoteWidgetProvider;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.wxl.donote.R;
import com.wxl.donote.R.drawable;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.appwidget.AppWidgetManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.FloatMath;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SimpleCursorAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint({ "HandlerLeak", "FloatMath" })
@SuppressWarnings("deprecation")
public class FreeEdit extends Activity implements OnTouchListener {

	// 自定义长按实现
	private int mLastMotionX, mLastMotionY;
	// 是否移动了
	private int style = 1;
	private View mainLayout;
	private boolean isMoved;
	private int appoint = 0;
	private Thread imageThread;
	// 是否释放了
	private boolean isReleased;// 计数器，防止多次点击导致最后一次形成longpress的时间变短
	private int mCounter = 0;
	// 移动的阈值
	private static final int TOUCH_SLOP = 2;
	// 添加TextView
	private static List<ViewSite> viewList;
	private ViewPager facePager;
	//双击
	private long bfirick = 0l;
	private long bsecick = 0l;
	// 双击
	private long firtick = 0l;
	private long sectick = 0l;

	private long distance = 0l;
	private EditText titleView;
	private TextView catagoryView;// 显示分类名称
	private View bottomView;
	private ListView listView;
	private CreateAlarm detect;
	private ScrollView scrollView;
	private int textColor = 1, textSize = 18;
	private String voiceString = "";
	private GridView gView1;
	private GridView gView2;
	private GridView gView3;
	private ArrayList<GridView> grids;
	private ImageView page0;
	private ImageView page1;
	private ImageView page2;
	private LinearLayout page_select;
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private PointF mid = new PointF();
	private int mode = NONE;
	private float oldDist;
	private float newDist;
	private Bundle bundle; 
	private String tString;
	private int CenterX; 
	private int CenterY;  
	private int nWidth;
	private int nHeight; 
	private float width = 0;
	private float height = 0;
	private Long mRowId;// 笔记ID
	private String exit_title = "";
	private String exit_body = "";
	private String exit_catagory = "";
	private Bundle extrasBundle;
	private PopupWindow window;
	private float nX, nY;
	private int left = 0;
	private int right = 0;
	private int bottom = 0;
	private int top = 0;
	private Time time = new Time();
	private String facefile = null;
	private int[] expressionImages;
	private String[] expressionImageNames;
	private int[] expressionImages1;
	private String[] expressionImageNames1;
	private int[] expressionImages2;
	private String[] expressionImageNames2;
	private String pathPhoto = Environment.getExternalStorageDirectory()
			.getPath() + "/" + "DoNote" + "/" + "photo" + "/";
	private String pathPicture = Environment.getExternalStorageDirectory()
			.getPath() + "/" + "DoNote" + "/" + "picture" + "/";
	private String pathVideo = Environment.getExternalStorageDirectory()
			.getPath() + "/" + "DoNote" + "/" + "video" + "/";
	private String pathDraw = Environment.getExternalStorageDirectory()
			.getPath() + "/" + "DoNote" + "/" + "draw" + "/";

	private String photofile;
	private Bitmap picture = null;
	private int screenWidth;
	private int screenHeight;
	private View footerView;
	private AbsoluteLayout showLayout;
	private ImageButton menu_face;
	private ImageButton moreButton;
	private Button menu_addition;
	private ImageButton menu_shibie;
	//private TextView catagoryButton;
	private ImageButton catagory_add_Button;
	private Button pictureButton;
	private Button movieButton;
	private Button voiceButton;
	private Button drawButton;
	private Button sendButton;
	private TextView catagory_TextView;// 显示分类名称
	private Cursor mNoteCursor;
	private Long cRowId;// 分类列表ID
	private Button edit_record;
	private String catagory_OldString;
	private ImageButton edit_shot;
	private ImageButton edit_save;
	private ImageButton edit_return_button;
	private String videofile;

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

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what)

			{
			case 1:
				MessageNote messageNote = (MessageNote) msg.obj;
				final TextView textView = (TextView) FreeEdit.this
						.findViewById(messageNote.id);
				textView.setBackgroundDrawable(messageNote.drawable);
				break;
			case 2:
				handler.removeCallbacks(imageThread);
			}

		}
	};

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_define);
		mainLayout = findViewById(R.id.define_layout);
		Bitmap temp = ImageMemoryCache.getBitmap((long) -1, "beijing");
		if (temp != null)
		{
			Drawable beijing = new BitmapDrawable(getResources(), temp);
			mainLayout.setBackgroundDrawable(beijing);
			mainLayout.getBackground().setAlpha(80);
		}
		new PopupWindow(
				getLayoutInflater().inflate(R.layout.define_menu, null),
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if (MainActivity.mDbHelper == null || !MainActivity.mDbHelper.isOpen())
		{
			MainActivity.mDbHelper = new NoteDbAdapter(this);
			MainActivity.mDbHelper.open();
		}

		mNoteCursor = MainActivity.mDbHelper.getAllCatagory();
		showLayout = (AbsoluteLayout) this.findViewById(R.id.show_note);
		catagory_TextView = (TextView) findViewById(R.id.catagory_textview);
		viewList = new ArrayList<ViewSite>();
		detect = new CreateAlarm();
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
		mRowId = null;// 笔记ID
		expressionImages = Expressions.expressionImgs;
		expressionImageNames = Expressions.expressionImgNames;
		expressionImages1 = Expressions.expressionImgs1;
		expressionImageNames1 = Expressions.expressionImgNames1;
		expressionImages2 = Expressions.expressionImgs2;
		expressionImageNames2 = Expressions.expressionImgNames2;

		scrollView = (ScrollView) this.findViewById(R.id.define_scroll);

		bottomView = (View) this.findViewById(R.id.bottom_view);
		titleView = (EditText) this.findViewById(R.id.title);
		titleView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listView.setVisibility(View.GONE);
				catagory_add_Button.setVisibility(View.GONE);
				facePager.setVisibility(View.GONE);
				page_select.setVisibility(View.GONE);

			}
		});

		moreButton = (ImageButton) findViewById(R.id.menu_more);
		catagoryView = (TextView) this.findViewById(R.id.catagory_textview);
		//catagoryButton = (TextView) findViewById(R.id.edit_catagory);
		menu_shibie = (ImageButton) this.findViewById(R.id.menu_shibie);
		menu_face = (ImageButton) this.findViewById(R.id.menu_face);
		edit_shot = (ImageButton) this.findViewById(R.id.menu_shot);
		edit_save = (ImageButton) this.findViewById(R.id.edit_save);
		edit_return_button = (ImageButton) findViewById(R.id.edit_return);
		page0 = (ImageView) findViewById(R.id.page0_select);
		page1 = (ImageView) findViewById(R.id.page1_select);
		page2 = (ImageView) findViewById(R.id.page2_select);
		page_select = (LinearLayout) findViewById(R.id.page_select);
		facePager = (ViewPager) findViewById(R.id.facepager);
		cRowId = null;// 分组ID
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
				titleView.setText(exit_title);
			}
			if (exit_body != null)
			{
				Pattern pattern = Pattern
						.compile("((Photo|Table|Record|File|Picture|Video|Text|"
								+ "Face|Draw|Cloud){1}"
								+ "\\^\\_\\^\\[(.*?)\\]\\[(.*?)\\]\\^\\_\\^)");
				Matcher matcher = pattern.matcher(exit_body);
				while (matcher.find())
				{
					if (matcher.group(2).equals("Photo"))
					{
						String idString = matcher.group();
						String[] strings = idString.split(":");
						String photo_path = idString.substring(
								idString.indexOf("Photo^_^[") + 9,
								idString.indexOf("]["));
						float photo_x = Float.valueOf(strings[1]);
						float photo_y = Float.valueOf(strings[2]);
						float photo_width = Float.valueOf(strings[3]);
						float photo_height = Float.valueOf(strings[4]);
						int photo_id = Integer.valueOf(strings[5]);
						Drawable photo = FreeEdit.this.getResources()
								.getDrawable(R.drawable.ic_default_image);
						photo.setBounds(0, 0, (int) photo_x, (int) photo_y);
						// 添加新的TextView
						final TextView note = new TextView(
								getApplicationContext());
						note.setTextColor(Color.BLACK);
						note.setGravity(Gravity.CENTER);
						note.setId(photo_id);
						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
								(int) photo_width, (int) photo_height,
								(int) photo_x, (int) photo_y);
						showLayout.addView(note, params);
						note.setBackgroundDrawable(photo);
						note.setOnTouchListener(this);
						ViewSite newSite = new ViewSite();
						newSite.setIsPhoto(true);
						newSite.setWidth(photo_width);
						newSite.setHeight(photo_height);
						newSite.setLocate_X(photo_x);
						newSite.setLocate_Y(photo_y);
						newSite.setContent(photo_path);
						newSite.setMark(photo_id);
						viewList.add(newSite);
					} else if (matcher.group(2).equals("Table"))
					{

						String idString = matcher.group();
						String tempString = idString.substring(
								idString.indexOf("][") + 2,
								idString.indexOf("]^_^"));
						String[] strings = tempString.split(":");
						String table_content = idString.substring(
								idString.indexOf("Table^_^[") + 9,
								idString.indexOf("]["));
						float table_x = Float.valueOf(strings[1]);
						float table_y = Float.valueOf(strings[2]);
						float table_width = Float.valueOf(strings[3]);
						float table_height = Float.valueOf(strings[4]);
						int table_id = Integer.valueOf(strings[5]);

						final TextView note = new TextView(
								getApplicationContext());
						note.setTextColor(Color.BLACK);
						note.setGravity(Gravity.CENTER);
						note.setId(table_id);

						Pattern titlePattern = Pattern
								.compile("(<\\-\\-title\\:(.*?)\\-\\-\\>)");
						Matcher titleMatcher = titlePattern
								.matcher(table_content);
						String title = null;
						while (titleMatcher.find())
						{
							title = idString.substring(
									idString.indexOf("<--title:") + 9,
									idString.indexOf("-->"));
							if (title.equals(""))
							{
								title =getResources().getString(R.string.without_title);
							}
						}
						Drawable drawable = FreeEdit.this.getResources()
								.getDrawable(R.drawable.ic_recimage);
						drawable = com.donote.util.ShowIcon.tableIcon(title,
								MainActivity.Width / 2);
						drawable.setBounds(0, 0, (int) table_width,
								(int) table_height);
						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
								(int) table_width, (int) table_height,
								(int) table_x, (int) table_y);
						showLayout.addView(note, params);
						note.setBackgroundDrawable(drawable);
						note.setOnTouchListener(this);
						ViewSite newSite = new ViewSite();
						newSite.setIsTable(true);
						newSite.setWidth(table_width);
						newSite.setHeight(table_height);
						newSite.setLocate_X(table_x);
						newSite.setLocate_Y(table_y);
						newSite.setContent(table_content);
						newSite.setMark(table_id);

						viewList.add(newSite);
					} else if (matcher.group(2).equals("Record"))
					{
						String idString = matcher.group();
						String[] strings = idString.split(":");
						String record_path = idString.substring(
								idString.indexOf("Record^_^[") + 10,
								idString.indexOf("]["));
						float record_x = Float.valueOf(strings[1]);
						float record_y = Float.valueOf(strings[2]);
						float record_width = Float.valueOf(strings[3]);
						float record_height = Float.valueOf(strings[4]);
						int record_id = Integer.valueOf(strings[5]);
						Drawable drawable = FreeEdit.this.getResources()
								.getDrawable(R.drawable.ic_recimage);
						drawable = com.donote.util.ShowIcon.RecordIcon(
								record_path, MainActivity.Width / 2);
						drawable.setBounds(0, 0, (int) record_width,
								(int) record_height);
						final TextView note = new TextView(
								getApplicationContext());
						note.setTextColor(Color.BLACK);
						note.setGravity(Gravity.CENTER);
						note.setId(record_id);
						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
								(int) record_width, (int) record_height,
								(int) record_x, (int) record_y);
						showLayout.addView(note, params);
						note.setBackgroundDrawable(drawable);
						note.setOnTouchListener(this);
						ViewSite newSite = new ViewSite();
						newSite.setIsRecord(true);
						newSite.setWidth(record_width);
						newSite.setHeight(record_height);
						newSite.setLocate_X(record_x);
						newSite.setLocate_Y(record_y);
						newSite.setContent(record_path);
						newSite.setMark(record_id);
						viewList.add(newSite);
					} else if (matcher.group(2).equals("Cloud"))
					{
						String idString = matcher.group();
						String tempString = idString.substring(
								idString.indexOf("][") + 2,
								idString.indexOf("]^_^"));

						String[] strings = tempString.split(":");

						String cloude_content = idString.substring(
								idString.indexOf("Cloud^_^[") + 9,
								idString.indexOf("]["));

						float cloude_x = Float.valueOf(strings[1]);
						float cloude_y = Float.valueOf(strings[2]);
						float cloude_width = Float.valueOf(strings[3]);
						float cloude_height = Float.valueOf(strings[4]);
						int cloude_id = Integer.valueOf(strings[5]);

						Drawable drawable = getCloudeBack();
						final TextView note = new TextView(
								getApplicationContext());

						note.setBackgroundDrawable(drawable);
						note.setTextColor(Color.BLACK);
						note.setGravity(Gravity.CENTER);
						note.setId(cloude_id);
						note.setTextSize(textSize);

						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
								(int) cloude_width, (int) cloude_height,
								(int) cloude_x, (int) cloude_y);

						showLayout.addView(note, params);
						note.setOnTouchListener(this);
						note.setText(cloude_content);
						ViewSite newSite = new ViewSite();
						newSite.setIsCloude(true);
						newSite.setWidth(cloude_width);
						newSite.setHeight(cloude_height);
						newSite.setLocate_X(cloude_x);
						newSite.setLocate_Y(cloude_y);
						newSite.setContent(cloude_content);
						newSite.setMark(cloude_id);
						viewList.add(newSite);

					} else if (matcher.group(2).equals("Draw"))
					{
						String idString = matcher.group();
						String[] strings = idString.split(":");
						String draw_path = idString.substring(
								idString.indexOf("Draw^_^[") + 8,
								idString.indexOf("]["));
						float draw_x = Float.valueOf(strings[1]);
						float draw_y = Float.valueOf(strings[2]);
						float draw_width = Float.valueOf(strings[3]);
						float draw_height = Float.valueOf(strings[4]);
						int draw_id = Integer.valueOf(strings[5]);
						Drawable drawable = FreeEdit.this.getResources()
								.getDrawable(R.drawable.ic_default_image);
						drawable.setBounds(0, 0, (int) draw_width,
								(int) draw_height);
						// 添加新的TextView
						final TextView note = new TextView(
								getApplicationContext());
						note.setTextColor(Color.BLACK);
						note.setGravity(Gravity.CENTER);
						note.setId(draw_id);
						note.setBackgroundDrawable(drawable);
						note.setOnTouchListener(this);
						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
								(int) draw_width, (int) draw_height,
								(int) draw_x, (int) draw_y);
						showLayout.addView(note, params);
						ViewSite newSite = new ViewSite();
						newSite.setIsDraw(true);
						newSite.setWidth(draw_width);
						newSite.setHeight(draw_height);
						newSite.setLocate_X(draw_x);
						newSite.setLocate_Y(draw_y);
						newSite.setContent(draw_path);
						newSite.setMark(draw_id);
						viewList.add(newSite);
					} else if (matcher.group(2).equals("File"))
					{
						String idString = matcher.group();
						String[] strings = idString.split(":");
						String file_path = idString.substring(
								idString.indexOf("File^_^[") + 8,
								idString.indexOf("]["));

						float file_x = Float.valueOf(strings[1]);
						float file_y = Float.valueOf(strings[2]);
						float file_width = Float.valueOf(strings[3]);
						float file_height = Float.valueOf(strings[4]);
						int file_id = Integer.valueOf(strings[5]);
						Drawable drawable = com.donote.util.ShowIcon.FileIcon(
								file_path, MainActivity.Width / 2);
						drawable.setBounds(0, 0, (int) file_width,
								(int) file_height);

						final TextView note = new TextView(
								getApplicationContext());
						note.setTextColor(Color.BLACK);
						note.setGravity(Gravity.CENTER);
						note.setId(file_id);

						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
								(int) file_width, (int) file_height,
								(int) file_x, (int) file_y);

						showLayout.addView(note, params);

						note.setBackgroundDrawable(drawable);

						note.setOnTouchListener(this);

						ViewSite newSite = new ViewSite();

						newSite.setIsFile(true);
						newSite.setWidth(file_width);
						newSite.setHeight(file_height);

						newSite.setLocate_X(file_x);
						newSite.setLocate_Y(file_y);

						newSite.setContent(file_path);
						newSite.setMark(file_id);

						viewList.add(newSite);
					}

					else if (matcher.group(2).equals("Picture"))
					{
						String idString = matcher.group();
						String[] strings = idString.split(":");
						String picture_path = idString.substring(
								idString.indexOf("Picture^_^[") + 11,
								idString.indexOf("]["));
						float picture_x = Float.valueOf(strings[1]);
						float picture_y = Float.valueOf(strings[2]);
						float picture_width = Float.valueOf(strings[3]);
						float picture_height = Float.valueOf(strings[4]);
						int picture_id = Integer.valueOf(strings[5]);
						Drawable picture = FreeEdit.this.getResources()
								.getDrawable(R.drawable.ic_default_image);
						picture.setBounds(0, 0, (int) picture_width,
								(int) picture_height);
						// 添加新的TextView
						final TextView note = new TextView(
								getApplicationContext());
						note.setTextColor(Color.BLACK);
						note.setGravity(Gravity.CENTER);
						note.setId(picture_id);
						note.setBackgroundDrawable(picture);
						note.setOnTouchListener(this);
						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
								(int) picture_width, (int) picture_height,
								(int) picture_x, (int) picture_y);
						showLayout.addView(note, params);
						ViewSite newSite = new ViewSite();
						newSite.setIsPicture(true);
						newSite.setWidth(picture_width);
						newSite.setHeight(picture_height);
						newSite.setLocate_X(picture_x);
						newSite.setLocate_Y(picture_y);
						newSite.setContent(picture_path);
						newSite.setMark(picture_id);
						viewList.add(newSite);

					} else if (matcher.group(2).equals("Video"))
					{
						String idString = matcher.group();
						String[] strings = idString.split(":");
						String video_path = idString.substring(
								idString.indexOf("Video^_^[") + 9,
								idString.indexOf("]["));
						float video_x = Float.valueOf(strings[1]);
						float video_y = Float.valueOf(strings[2]);
						float video_width = Float.valueOf(strings[3]);
						float video_height = Float.valueOf(strings[4]);
						int video_id = Integer.valueOf(strings[5]);
						Drawable drawable = getResources().getDrawable(
								R.drawable.ic_default_video_iamge);
						drawable.setBounds(0, 0, (int) video_x, (int) video_y);
						final TextView note = new TextView(
								getApplicationContext());
						note.setTextColor(Color.BLACK);
						note.setGravity(Gravity.CENTER);
						note.setId(video_id);

						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
								(int) video_width, (int) video_height,
								(int) video_x, (int) video_y);
						showLayout.addView(note, params);
						note.setBackgroundDrawable(drawable);
						ViewSite newSite = new ViewSite();
						note.setOnTouchListener(this);
						newSite.setIsVideo(true);
						newSite.setWidth(video_width);
						newSite.setHeight(video_height);
						newSite.setLocate_X(video_x);
						newSite.setLocate_Y(video_y);
						newSite.setContent(video_path);
						newSite.setMark(video_id);
						viewList.add(newSite);
					} else if (matcher.group(2).equals("Text"))
					{

						String idString = matcher.group();

						String tempString = idString.substring(
								idString.indexOf("][") + 2,
								idString.indexOf("]^_^"));

						String[] strings = tempString.split(":");

						String text_content = idString.substring(
								idString.indexOf("Text^_^[") + 8,
								idString.indexOf("]["));

						float text_x = Float.valueOf(strings[1]);
						float text_y = Float.valueOf(strings[2]);
						float text_width = Float.valueOf(strings[3]);
						float text_height = Float.valueOf(strings[4]);
						float text_radius = Float.valueOf(strings[5]);
						int text_id = Integer.valueOf(strings[6]);
						if (text_radius == 0)
						{
							Drawable drawable = getRectBack();
							final TextView note = new TextView(
									getApplicationContext());
							note.setTextColor(Color.BLACK);
							note.setGravity(Gravity.CENTER);
							note.setId(text_id);
							note.setText(text_content);
							note.setTextSize(textSize);
							note.setBackgroundDrawable(drawable);
							note.setOnTouchListener(FreeEdit.this);

							AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
									(int) text_width, (int) text_height,
									(int) text_x, (int) text_y);
							showLayout.addView(note, params);
							ViewSite newSite = new ViewSite();
							newSite.setIsText(true);
							newSite.setWidth(text_width);
							newSite.setHeight(text_height);
							newSite.setLocate_X(text_x);
							newSite.setLocate_Y(text_y);
							newSite.setContent(text_content);
							newSite.setMark(text_id);
							viewList.add(newSite);
						} else
						{
							Drawable drawable = getCircleBack();
							// 添加新的TextView
							final TextView note = new TextView(
									getApplicationContext());
							note.setTextColor(Color.BLACK);
							note.setGravity(Gravity.CENTER);
							note.setId(text_id);
							note.setBackgroundDrawable(drawable);
							AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
									(int) (2 * text_radius),
									(int) (2 * text_radius), (int) text_x,
									(int) text_y);
							showLayout.addView(note, params);
							note.setOnTouchListener(this);
							note.setText(text_content);
							note.setTextSize(textSize);
							ViewSite newSite = new ViewSite();
							newSite.setIsText(true);
							newSite.setWidth(text_width);
							newSite.setHeight(text_height);
							newSite.setRadius(text_radius);
							newSite.setLocate_X(text_x);
							newSite.setLocate_Y(text_y);
							newSite.setContent(text_content);
							newSite.setMark(text_id);
							viewList.add(newSite);
						}
					} else if (matcher.group(2).equals("Face"))
					{
						String idString = matcher.group();
						String face_path = idString.substring(
								idString.indexOf("Face^_^[") + 9,
								idString.indexOf("]["));
						String[] strings = idString.split(":");

						float face_x = Float.valueOf(strings[1]);
						float face_y = Float.valueOf(strings[2]);
						float face_width = Float.valueOf(strings[3]);
						float face_height = Float.valueOf(strings[4]);
						int face_id = Integer.valueOf(strings[5]);
						Bitmap face = null;
						Log.i("face", face_path);
						switch (Integer.parseInt(face_path)
								/ expressionImages1.length)
								{
								case 0:
									face = BitmapFactory.decodeResource(
											getResources(),
											expressionImages[Integer
											                 .parseInt(face_path)
											                 % expressionImages.length]);
									break;
								case 1:
									face = BitmapFactory.decodeResource(
											getResources(),
											expressionImages1[Integer
											                  .parseInt(face_path)
											                  % expressionImages1.length]);
									break;
								case 2:
									face = BitmapFactory.decodeResource(
											getResources(),
											expressionImages2[Integer
											                  .parseInt(face_path)
											                  % expressionImages2.length]);
									break;
								}

						Drawable drawable = new BitmapDrawable(getResources(),
								face);
						drawable.setBounds(0, 0, (int) face_width,
								(int) face_height);

						final TextView note = new TextView(
								getApplicationContext());

						note.setGravity(Gravity.CENTER);
						note.setId(face_id);
						note.setBackgroundDrawable(drawable);
						note.setOnTouchListener(this);

						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
								(int) face_width, (int) face_width,
								(int) face_x, (int) face_y);

						showLayout.addView(note, params);

						ViewSite newSite = new ViewSite();

						newSite.setIsFace(true);
						newSite.setWidth(face_width);
						newSite.setHeight(face_height);

						newSite.setLocate_X(face_x);
						newSite.setLocate_Y(face_y);

						newSite.setContent("f" + face_path);
						newSite.setMark(face_id);
						viewList.add(newSite);
					}
				}
			}

			if (exit_catagory != null)
			{
				catagory_TextView.setText(exit_catagory);
			}

			imageThread = new Thread(new imageLoadThread());
			imageThread.setDaemon(true);
			imageThread.start();// 启动线程
		}

		// 展开分类列表响应
		/*catagoryButton.setOnClickListener(new View.OnClickListener() {
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
		});*/

		edit_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listView.setVisibility(View.GONE);
				catagory_add_Button.setVisibility(View.GONE);
				facePager.setVisibility(View.GONE);
				page_select.setVisibility(View.GONE);
				String body = "";
				for (int i = 0; i < viewList.size(); i++)
				{
					ViewSite viewSite = viewList.get(i);
					if (viewSite.getIsFace())
					{
						body += "Face^_^[" + viewSite.getContent() + "][:"
								+ String.valueOf(viewSite.getLocate_X()) + ":"
								+ String.valueOf(viewSite.getLocate_Y()) + ":"
								+ String.valueOf(viewSite.getWidth()) + ":"
								+ String.valueOf(viewSite.getHeight()) + ":"
								+ String.valueOf(viewSite.getMark())
								+ ":]^_^  ";
					}
					if (viewSite.getIsPhoto())
					{
						body += "Photo^_^[" + viewSite.getContent() + "][:"
								+ String.valueOf(viewSite.getLocate_X()) + ":"
								+ String.valueOf(viewSite.getLocate_Y()) + ":"
								+ String.valueOf(viewSite.getWidth()) + ":"
								+ String.valueOf(viewSite.getHeight()) + ":"
								+ String.valueOf(viewSite.getMark())
								+ ":]^_^  ";
					}
					if (viewSite.getIsRecord())
					{
						body += "Record^_^[" + viewSite.getContent() + "][:"
								+ String.valueOf(viewSite.getLocate_X()) + ":"
								+ String.valueOf(viewSite.getLocate_Y()) + ":"
								+ String.valueOf(viewSite.getWidth()) + ":"
								+ String.valueOf(viewSite.getHeight()) + ":"
								+ String.valueOf(viewSite.getMark()) + ":]^_^ ";
					}
					if (viewSite.getIsText())
					{
						body += "Text^_^[" + viewSite.getContent() + "][:"
								+ String.valueOf(viewSite.getLocate_X()) + ":"
								+ String.valueOf(viewSite.getLocate_Y()) + ":"
								+ String.valueOf(viewSite.getWidth()) + ":"
								+ String.valueOf(viewSite.getHeight()) + ":"
								+ String.valueOf(viewSite.getRadius()) + ":"
								+ String.valueOf(viewSite.getMark())
								+ ":]^_^  ";
					}
					if (viewSite.getIsVideo())
					{
						body += "Video^_^[" + viewSite.getContent() + "][:"
								+ String.valueOf(viewSite.getLocate_X()) + ":"
								+ String.valueOf(viewSite.getLocate_Y()) + ":"
								+ String.valueOf(viewSite.getWidth()) + ":"
								+ String.valueOf(viewSite.getHeight()) + ":"
								+ String.valueOf(viewSite.getMark())
								+ ":]^_^  ";
					}
					if (viewSite.getIsPicture())
					{
						body += "Picture^_^[" + viewSite.getContent() + "][:"
								+ String.valueOf(viewSite.getLocate_X()) + ":"
								+ String.valueOf(viewSite.getLocate_Y()) + ":"
								+ String.valueOf(viewSite.getWidth()) + ":"
								+ String.valueOf(viewSite.getHeight()) + ":"
								+ String.valueOf(viewSite.getMark())
								+ ":]^_^  ";
					}
					if (viewSite.getIsTable())
					{
						body += "Table^_^[" + viewSite.getContent() + "][:"
								+ String.valueOf(viewSite.getLocate_X()) + ":"
								+ String.valueOf(viewSite.getLocate_Y()) + ":"
								+ String.valueOf(viewSite.getWidth()) + ":"
								+ String.valueOf(viewSite.getHeight()) + ":"
								+ String.valueOf(viewSite.getMark())
								+ ":]^_^  ";
					}
					if (viewSite.getIsFile())
					{
						body += "File^_^[" + viewSite.getContent() + "][:"
								+ String.valueOf(viewSite.getLocate_X()) + ":"
								+ String.valueOf(viewSite.getLocate_Y()) + ":"
								+ String.valueOf(viewSite.getWidth()) + ":"
								+ String.valueOf(viewSite.getHeight()) + ":"
								+ String.valueOf(viewSite.getMark())
								+ ":]^_^  ";
					}
					if (viewSite.getIsDraw())
					{
						body += "Draw^_^[" + viewSite.getContent() + "][:"
								+ String.valueOf(viewSite.getLocate_X()) + ":"
								+ String.valueOf(viewSite.getLocate_Y()) + ":"
								+ String.valueOf(viewSite.getWidth()) + ":"
								+ String.valueOf(viewSite.getHeight()) + ":"
								+ String.valueOf(viewSite.getMark())
								+ ":]^_^  ";
					}
					if (viewSite.getIsCloude())
					{
						body += "Cloud^_^[" + viewSite.getContent() + "][:"
								+ String.valueOf(viewSite.getLocate_X()) + ":"
								+ String.valueOf(viewSite.getLocate_Y()) + ":"
								+ String.valueOf(viewSite.getWidth()) + ":"
								+ String.valueOf(viewSite.getHeight()) + ":"
								+ String.valueOf(viewSite.getMark())
								+ ":]^_^  ";
					}
				}

				DefineSave(body);

			}
		});



		menu_shibie.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listView.setVisibility(View.GONE);
				catagory_add_Button.setVisibility(View.GONE);
				facePager.setVisibility(View.GONE);
				page_select.setVisibility(View.GONE);
				Intent intent = new Intent(FreeEdit.this, DefineShape.class);
				startActivityForResult(intent, 7);
			}
		});

		moreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopupWindow popupWindow = menuPopupWindow(FreeEdit.this);
				popupWindow.showAtLocation(mainLayout, Gravity.RIGHT
						| Gravity.BOTTOM, 0, moreButton.getHeight());

			}
		});
		catagory_TextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
		menu_face.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listView.setVisibility(View.GONE);
				catagory_add_Button.setVisibility(View.GONE);
				if (facePager.isShown())
				{
					facePager.setVisibility(View.GONE);
					page_select.setVisibility(View.GONE);
				} else
				{
					facePager.setVisibility(View.VISIBLE);
					page_select.setVisibility(View.VISIBLE);
				}

			}
		});

		edit_shot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listView.setVisibility(View.GONE);
				catagory_add_Button.setVisibility(View.GONE);
				facePager.setVisibility(View.GONE);
				page_select.setVisibility(View.GONE);
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
						startActivityForResult(intent, 28);
					} catch (ActivityNotFoundException e)
					{
						// TODO Auto-generated catch block
						Toast.makeText(FreeEdit.this, getResources().getString(R.string.cannot_find_path),
								Toast.LENGTH_LONG).show();
					}

				} else
				{
					Toast.makeText(FreeEdit.this,getResources().getString(R.string.without_disk), Toast.LENGTH_LONG)
					.show();
				}
			}
		});


		bottomView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				listView.setVisibility(View.GONE);
				catagory_add_Button.setVisibility(View.GONE);
				facePager.setVisibility(View.GONE);
				page_select.setVisibility(View.GONE);

				Log.i("wxl", "onTouch");

				switch (event.getActionMasked()) {

				case MotionEvent.ACTION_DOWN:
					if(viewList.size() == 0) {
						
						Bundle bundle = new Bundle();
						bundle.putInt("size", textSize);
						bundle.putInt("color", textColor);
						bundle.putString("text", "");
						bundle.putString("cloud", "create");
						Intent intent = new Intent(FreeEdit.this,
								GraphText.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, 14);	
						
					}else{
						
						// 双击事件
						if (bfirick == 0l)
						{
							bfirick = System.currentTimeMillis();// 前一次点击的时间
							Log.i("wxl", "bfrick:" + bfirick);
						} 
						else if (bsecick == 0l)
						{// 后一次点击时间

							bsecick = System.currentTimeMillis();

							distance = bsecick - bfirick;

							if (distance > 0l && distance < 280l)
							{
								//双击屏幕
								bfirick = 0l; 
								bsecick = 0l;

								Bundle bundle = new Bundle();
								bundle.putInt("size", textSize);
								bundle.putInt("color", textColor);
								bundle.putString("text", "");
								bundle.putString("cloud", "create");
								Intent intent = new Intent(FreeEdit.this,
										GraphText.class);
								intent.putExtras(bundle);

								startActivityForResult(intent, 14);			

							}else
							{
								// 不是连续点击
								bfirick = System.currentTimeMillis();// 重新获取前一次点击的时间
								bsecick = 0l;
							}
						}
					}
					break;
				default:
					break;
				}

				return false;
			}
		});

		edit_return_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				exitDialgo(FreeEdit.this);
			}
		});
		listView.setVisibility(View.GONE);
		initViewPager();
	}

	protected Dialog XsconCreateDialog(String id) {
		RecognizerDialog recognizerDialog = new RecognizerDialog(FreeEdit.this,
				id);
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
				voiceString += result.toString();
				time.setToNow();
				tString = String.valueOf(time.month)
						+ String.valueOf(time.monthDay)
						+ String.valueOf(time.hour)
						+ String.valueOf(time.minute)
						+ String.valueOf(time.second);
				Drawable drawable = getCloudeBack();
				final TextView note = new TextView(getApplicationContext());
				note.setTextColor(Color.BLACK);
				note.setBackgroundDrawable(drawable);
				note.setGravity(Gravity.CENTER);
				note.setId(Integer.valueOf(tString));
				float Height = drawable.getIntrinsicHeight();
				float Width = drawable.getIntrinsicWidth();
				CenterX = (int) (showLayout.getWidth() / 2 - Width / 2);
				CenterY = (int) (scrollView.getScrollY() + 210);
				AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
						(int) Width, (int) Height, CenterX, CenterY);
				initSite((int) (CenterX - Width / 2),
						(int) (CenterY - Height / 2),
						(int) (CenterX + Width / 2),
						(int) (CenterY + Height / 2));
				showLayout.addView(note, params);
				note.setOnTouchListener(FreeEdit.this);
				note.setText(voiceString);
				ViewSite newSite = new ViewSite();
				newSite.setIsCloude(true);
				newSite.setWidth(Width);
				newSite.setHeight(Height);
				newSite.setLocate_X(CenterX);
				newSite.setLocate_Y(CenterY);
				newSite.setContent(voiceString);
				newSite.setMark(Integer.valueOf(tString));
				viewList.add(newSite);
			}

			@Override
			public void onEnd(SpeechError arg0) {

			}

		});
		return recognizerDialog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		listView.setVisibility(View.GONE);
		facePager.setVisibility(View.GONE);
		page_select.setVisibility(View.GONE);
		super.onRestart();
	}

	public void DefineSave(String body) {
		String catagory = catagoryView.getText().toString();
		String title = titleView.getText().toString();

		RemoteViews views = new RemoteViews(FreeEdit.this.getPackageName(), R.layout.widget_layout);

		if (mRowId != null)
		{
			if (title.equals(""))
			{
				MainActivity.mDbHelper.updateNote(mRowId, getResources().getString(R.string.without_title), body, style,
						catagory);
				MySimpleCursorAdapter.imageCache.detectImage(body, mRowId);
				detect.create(mRowId, body, FreeEdit.this);

				if(DoNoteWidgetProvider.note_id == mRowId){
					views.setTextViewText(R.id.text_title,getResources().getString(R.string.without_title) );
					views.setTextViewText(R.id.text_body, getEditContent(body));
					DoNoteWidgetProvider.isdelete = false;
				}

			} else
			{
				MainActivity.mDbHelper.updateNote(mRowId, title, body, style,
						catagory);
				MySimpleCursorAdapter.imageCache.detectImage(body, mRowId);
				detect.create(mRowId, body, FreeEdit.this);

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
				long id = MainActivity.mDbHelper.createNote(getResources().getString(R.string.without_title), body, style,
						catagory);
				MySimpleCursorAdapter.imageCache.detectImage(body, id);
				detect.create(id, body, FreeEdit.this);
			} else
			{
				long id = MainActivity.mDbHelper.createNote(title, body, style,
						catagory);
				MySimpleCursorAdapter.imageCache.detectImage(body, id);
				detect.create(id, body, FreeEdit.this);
			}
		} else
		{
			Toast.makeText(FreeEdit.this, getResources().getString(R.string.blank_note), Toast.LENGTH_SHORT).show();
		}

		ComponentName widget  = new ComponentName(FreeEdit.this,DoNoteWidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
		manager.updateAppWidget(widget, views);
		/*
		 * Intent mIntent = new Intent(); setResult(RESULT_OK, mIntent); Intent
		 * intent = new Intent(FreeEdit.this, MainActivity.class);
		 * startActivity(intent);
		 */
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

		SimpleAdapter simpleAdapter = new SimpleAdapter(FreeEdit.this,
				listItems, R.layout.singleexpression, new String[] { "image" },
				new int[] { R.id.faceImage });
		gView1.setAdapter(simpleAdapter);
		gView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				time.setToNow();
				tString = String.valueOf(time.month)
						+ String.valueOf(time.monthDay)
						+ String.valueOf(time.hour)
						+ String.valueOf(time.minute)
						+ String.valueOf(time.second);

				Bitmap bitmap = null;
				bitmap = BitmapFactory.decodeResource(getResources(),
						expressionImages[arg2 % expressionImages.length]);
				Drawable drawable = new BitmapDrawable(getResources(), bitmap);

				float Width = drawable.getIntrinsicWidth() * 2;
				float Height = drawable.getIntrinsicHeight() * 2;

				facefile = expressionImageNames[arg2].substring(1,
						expressionImageNames[arg2].length() - 1);

				final TextView note = new TextView(getApplicationContext());
				note.setTextColor(Color.BLACK);
				setTextStyle(note, textSize, textColor);
				note.setGravity(Gravity.CENTER);
				note.setId(Integer.valueOf(tString));

				note.setOnTouchListener(FreeEdit.this);
				note.setBackgroundDrawable(drawable);

				CenterX = (int) (showLayout.getWidth() / 2 - Width / 2);
				CenterY = (int) (showLayout.getHeight() / 2 - Height / 2);

				AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
						(int) Width, (int) Height, CenterX, CenterY);

				showLayout.addView(note, params);

				initSite((int) (CenterX - Width / 2),
						(int) (CenterY - Height / 2),
						(int) (CenterX + Width / 2),
						(int) (CenterY + Height / 2));

				ViewSite newSite = new ViewSite();
				newSite.setIsFace(true);

				newSite.setWidth(Width);
				newSite.setHeight(Height);

				newSite.setLocate_X(CenterX);
				newSite.setLocate_Y(CenterY);

				newSite.setContent(facefile);
				newSite.setMark(Integer.valueOf(tString));
				viewList.add(newSite);

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

			time.setToNow();
			tString = String.valueOf(time.month)
					+ String.valueOf(time.monthDay) + String.valueOf(time.hour)
					+ String.valueOf(time.minute) + String.valueOf(time.second);

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

				SimpleAdapter simpleAdapter = new SimpleAdapter(FreeEdit.this,
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

						Drawable drawable = new BitmapDrawable(getResources(),
								bitmap);

						float Width = drawable.getIntrinsicWidth() * 2;
						float Height = drawable.getIntrinsicHeight() * 2;

						facefile = expressionImageNames1[arg2].substring(1,
								expressionImageNames1[arg2].length() - 1);

						final TextView note = new TextView(
								getApplicationContext());
						note.setTextColor(Color.BLACK);
						setTextStyle(note, textSize, textColor);
						note.setGravity(Gravity.CENTER);
						note.setId(Integer.valueOf(tString));

						note.setOnTouchListener(FreeEdit.this);
						note.setBackgroundDrawable(drawable);

						CenterX = (int) (showLayout.getWidth() / 2 - Width / 2);
						CenterY = (int) (showLayout.getHeight() / 2 - Height / 2);

						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
								(int) Width, (int) Height, CenterX, CenterY);

						initSite((int) (CenterX - Width / 2),
								(int) (CenterY - Height / 2),
								(int) (CenterX + Width / 2),
								(int) (CenterY + Height / 2));

						showLayout.addView(note, params);

						ViewSite newSite = new ViewSite();
						newSite.setIsFace(true);

						newSite.setWidth(Width);
						newSite.setHeight(Height);

						newSite.setLocate_X(CenterX);
						newSite.setLocate_Y(CenterY);

						newSite.setContent(facefile);
						newSite.setMark(Integer.valueOf(tString));
						viewList.add(newSite);

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

				SimpleAdapter simpleAdapter1 = new SimpleAdapter(FreeEdit.this,
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

						Drawable drawable = new BitmapDrawable(getResources(),
								bitmap);

						float Width = drawable.getIntrinsicWidth() * 2;
						float Height = drawable.getIntrinsicHeight() * 2;

						facefile = expressionImageNames2[arg2].substring(1,
								expressionImageNames2[arg2].length() - 1);

						final TextView note = new TextView(
								getApplicationContext());
						note.setTextColor(Color.BLACK);
						setTextStyle(note, textSize, textColor);
						note.setGravity(Gravity.CENTER);
						note.setId(Integer.valueOf(tString));

						note.setOnTouchListener(FreeEdit.this);
						note.setBackgroundDrawable(drawable);

						CenterX = (int) (showLayout.getWidth() / 2 - Width / 2);
						CenterY = (int) (showLayout.getHeight() / 2 - Height / 2);

						AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
								(int) Width, (int) Height, CenterX, CenterY);

						initSite((int) (CenterX - Width / 2),
								(int) (CenterY - Height / 2),
								(int) (CenterX + Width / 2),
								(int) (CenterY + Height / 2));

						showLayout.addView(note, params);

						ViewSite newSite = new ViewSite();
						newSite.setIsFace(true);

						newSite.setWidth(Width);
						newSite.setHeight(Height);

						newSite.setLocate_X(CenterX);
						newSite.setLocate_Y(CenterY);

						newSite.setContent(facefile);
						newSite.setMark(Integer.valueOf(tString));
						viewList.add(newSite);

					}
				});
				break;

			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// TODO Auto-generated method stub

		time.setToNow();
		tString = String.valueOf(time.month) + String.valueOf(time.monthDay)
				+ String.valueOf(time.hour) + String.valueOf(time.minute)
				+ String.valueOf(time.second);
		if (requestCode == 1)
		{
			finish();
		}
		if (requestCode == 7 && resultCode != RESULT_CANCELED)
		{
			bundle = data.getExtras();
			if (bundle.getBoolean("isOK"))
			{
				if (bundle.getBoolean("isTable"))
				{
					int row = bundle.getInt("rows");
					int column = bundle.getInt("columns");
					Intent intent = new Intent(FreeEdit.this, TableEdit.class);
					Bundle bundle = new Bundle();
					bundle.putBoolean("iswrite", false);
					bundle.putInt("row", row);
					bundle.putInt("column", column);
					intent.putExtras(bundle);
					startActivityForResult(intent, 70);
				}

				if (bundle.getBoolean("isCircle"))
				{
					float Radius = bundle.getFloat("Radius");
					// 添加新的TextView

					Drawable drawable = getCircleBack();

					final TextView note = new TextView(getApplicationContext());

					note.setTextColor(Color.BLACK);
					note.setBackgroundDrawable(drawable);

					note.setGravity(Gravity.CENTER);
					note.setId(Integer.valueOf(tString));

					CenterX = (int) (showLayout.getWidth() / 2 - Radius);
					CenterY = (int) (scrollView.getScrollY() + 210);

					AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
							(int) (2 * Radius), (int) (2 * Radius), CenterX,
							CenterY);

					initSite((int) (CenterX - Radius),
							(int) (CenterY - Radius), (int) (CenterX + Radius),
							(int) (CenterY + Radius));

					showLayout.addView(note, params);

					note.setOnTouchListener(this);

					ViewSite newSite = new ViewSite();

					newSite.setIsText(true);

					newSite.setRadius(Radius);

					newSite.setWidth(2 * Radius);
					newSite.setHeight(2 * Radius);

					newSite.setLocate_X(CenterX);
					newSite.setLocate_Y(CenterY);

					newSite.setContent("");

					newSite.setMark(Integer.valueOf(tString));
					viewList.add(newSite);

				}
				if (bundle.getBoolean("isRect"))
				{
					// 方形，矩形
					Drawable drawable = getRectBack();

					final TextView note = new TextView(getApplicationContext());
					note.setTextColor(Color.BLACK);
					note.setBackgroundDrawable(drawable);

					note.setGravity(Gravity.CENTER);
					note.setId(Integer.valueOf(tString));

					float Width = bundle.getFloat("Width");
					float Height = bundle.getFloat("Height");

					CenterX = (int) (showLayout.getWidth() / 2 - Width);
					CenterY = (int) (scrollView.getScrollY() + 210);

					AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
							(int) Width, (int) Height, CenterX, CenterY);

					initSite((int) (CenterX - Width / 2),
							(int) (CenterY - Height / 2),
							(int) (CenterX + Width / 2),
							(int) (CenterY + Height / 2));

					showLayout.addView(note, params);
					note.setOnTouchListener(this);

					ViewSite newSite = new ViewSite();
					newSite.setIsText(true);

					newSite.setWidth(Width);
					newSite.setHeight(Height);

					newSite.setLocate_X(CenterX);
					newSite.setLocate_Y(CenterY);

					newSite.setContent("");
					newSite.setMark(Integer.valueOf(tString));
					viewList.add(newSite);

				}
			}

		}

		else if (requestCode == 14 && resultCode != RESULT_CANCELED)
		{
			boolean crea_cloud = false;
			boolean iswrite = false;
			bundle = data.getExtras();
			String text = bundle.getString("note");
			crea_cloud = bundle.getBoolean("crea_cloud");
			iswrite = bundle.getBoolean("iswrite");
			if(iswrite){
				if(crea_cloud){

					tString = String.valueOf(time.month)
							+ String.valueOf(time.monthDay) + String.valueOf(time.hour)
							+ String.valueOf(time.minute) + String.valueOf(time.second);

					Drawable drawable = getCloudeBack();

					float Height = drawable.getIntrinsicHeight();
					float Width = drawable.getIntrinsicWidth();

					final TextView note = new TextView(getApplicationContext());

					note.setTextColor(Color.BLACK);
					note.setBackgroundDrawable(drawable);

					note.setGravity(Gravity.CENTER);
					note.setId(Integer.valueOf(tString));
					note.setOnTouchListener(FreeEdit.this);
					note.setText(text);

					CenterX = (int) (showLayout.getWidth() / 2 - Width / 2);
					CenterY = (int) (scrollView.getScrollY() + 210);
					AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
							(int) Width, (int) Height, CenterX, CenterY);
					initSite((int) (CenterX - Width / 2),
							(int) (CenterY - Height / 2),
							(int) (CenterX + Width / 2),
							(int) (CenterY + Height / 2));
					showLayout.addView(note, params);

					ViewSite newSite = new ViewSite();
					newSite.setIsCloude(true);
					newSite.setWidth(Width);
					newSite.setHeight(Height);
					newSite.setLocate_X(CenterX);
					newSite.setLocate_Y(CenterY);
					newSite.setContent(text);
					newSite.setMark(Integer.valueOf(tString));
					viewList.add(newSite);

				}
				else{

					int id = bundle.getInt("id");
					final TextView note = (TextView) findViewById(id);
					note.setText(text);
					note.setMaxEms(8);

					ViewSite temp = null;
					for (int i = 0; i < viewList.size(); i++)
					{
						temp = viewList.get(i);
						if (id == temp.getMark())
						{
							temp.setContent(text);
							viewList.set(i, temp);
						}
					}
				}
			}
		}

		else if (requestCode == 21 && resultCode != RESULT_CANCELED)
		{
			// 添加录音
			bundle = data.getExtras();
			String nameString = bundle.getString("name");
			float Width;
			float Height;
			Drawable drawable = FreeEdit.this.getResources().getDrawable(
					R.drawable.ic_recimage);
			drawable = com.donote.util.ShowIcon.RecordIcon(nameString,
					MainActivity.Width / 2);
			Width = drawable.getIntrinsicWidth();
			Height = drawable.getIntrinsicHeight();
			drawable.setBounds(0, 0, (int) Width, (int) Height);
			final TextView note = new TextView(getApplicationContext());
			note.setBackgroundDrawable(drawable);
			CenterX = (int) (showLayout.getWidth() / 2 - Width / 2);
			CenterY = (int) (scrollView.getScrollY() + 210);
			AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
					(int) Width, (int) Height, CenterX, CenterY);
			showLayout.addView(note, params);
			note.setOnTouchListener(this);
			note.setId(Integer.valueOf(tString));
			ViewSite newSite = new ViewSite();
			newSite.setIsRecord(true);
			newSite.setWidth(Width);
			newSite.setHeight(Height);
			newSite.setLocate_X(CenterX);
			newSite.setLocate_Y(CenterY);
			newSite.setContent(nameString);
			newSite.setMark(Integer.valueOf(tString));
			viewList.add(newSite);
		}

		else if (requestCode == 28 && resultCode != RESULT_CANCELED)
		{
			Intent intent = new Intent(this, ImageHandle.class);
			intent.putExtra("filepath", photofile);
			startActivityForResult(intent, 50);
		}

		else if (requestCode == 22 && resultCode != RESULT_CANCELED)
		{
			Bitmap video = ThumbnailUtils.createVideoThumbnail(videofile,
					MediaStore.Images.Thumbnails.MICRO_KIND);
			float Width;
			float Height;
			Drawable drawable = null;
			if (video == null)
			{
				Resources res = getResources();
				drawable = res.getDrawable(R.drawable.ic_default_video_iamge);
			} else
			{
				video = com.donote.util.ShowIcon.zoomBitmap2(video);
				video = com.donote.util.ShowIcon.VideoIcon(video);
				drawable = new BitmapDrawable(getResources(), video);
			}
			Width = drawable.getIntrinsicWidth();
			Height = drawable.getIntrinsicHeight();
			final TextView note = new TextView(getApplicationContext());
			note.setBackgroundDrawable(drawable);
			CenterX = (int) (showLayout.getWidth() / 2 - Width / 2);
			CenterY = (int) (scrollView.getScrollY() + 210);
			AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
					(int) Width, (int) Height, CenterX, CenterY);
			showLayout.addView(note, params);
			ViewSite newSite = new ViewSite();
			note.setOnTouchListener(this);
			note.setId(Integer.valueOf(tString));
			newSite.setWidth(Width);
			newSite.setHeight(Height);
			newSite.setLocate_X(CenterX);
			newSite.setIsVideo(true);
			newSite.setLocate_Y(CenterY);
			newSite.setContent(videofile);
			newSite.setMark(Integer.valueOf(tString));
			viewList.add(newSite);
		}

		else if (requestCode == 42 && resultCode != RESULT_CANCELED)
		{
			// 添加附件
			float Width;
			float Height;

			bundle = data.getExtras();

			File file = new File(bundle.getString("file"));
			String filePathString = file.getAbsolutePath();

			Drawable temp = null;

			ViewSite newSite = new ViewSite();

			if (checkEndsWithInStringArray(filePathString, getResources()
					.getStringArray(R.array.fileEndingImage)))
			{
				// 附件为相片
				/*
				 * Bitmap photo = com.wxl.donote.showIcon.readBitmapAutoSize(
				 * filePathString, MainActivity.Width, MainActivity.Height,
				 * com.wxl.donote.showIcon .readPictureDegree(filePathString));
				 * photo = com.wxl.donote.showIcon.zoomBitmap(photo); Drawable
				 * drawable = new BitmapDrawable(getResources(),photo); temp =
				 * drawable;
				 * 
				 * newSite.setIsPhoto(true);
				 */
				Intent intent = new Intent(this, ImageHandle.class);
				intent.putExtra("filepath", filePathString);
				startActivityForResult(intent, 50);
				return;
			} else if (checkEndsWithInStringArray(filePathString,
					getResources().getStringArray(R.array.fileEndingAudio)))
			{
				// 附件为音频
				temp = FreeEdit.this.getResources().getDrawable(
						R.drawable.ic_recimage);
				temp = com.donote.util.ShowIcon.RecordIcon(
						file.getAbsolutePath(), MainActivity.Width / 2);

				newSite.setIsRecord(true);

			} else if (checkEndsWithInStringArray(filePathString,
					getResources().getStringArray(R.array.fileEndingVideo)))
			{
				// 附件为视频

				Bitmap video = ThumbnailUtils
						.createVideoThumbnail(filePathString,
								MediaStore.Images.Thumbnails.MICRO_KIND);

				if (video != null)
				{
					video = com.donote.util.ShowIcon.zoomBitmap(video);
					video = com.donote.util.ShowIcon.VideoIcon(video);
					Drawable drawable = new BitmapDrawable(getResources(),
							video);
					temp = drawable;
					newSite.setIsVideo(true);
				} else
				{
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.cannot_create_thumbnail),
							Toast.LENGTH_SHORT).show();
				}

			} else
			{
				// 其他
				temp = com.donote.util.ShowIcon.FileIcon(filePathString,
						MainActivity.Width / 2);
				temp.setBounds(0, 0, (int) MainActivity.Width,
						(int) MainActivity.Height);
				newSite.setIsFile(true);
			}

			Width = temp.getIntrinsicWidth();
			Height = temp.getIntrinsicHeight();

			final TextView note = new TextView(getApplicationContext());

			note.setBackgroundDrawable(temp);

			CenterX = (int) (showLayout.getWidth() / 2 - Width / 2);
			CenterY = (int) (scrollView.getScrollY() + 210);

			AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
					(int) Width, (int) Height, CenterX, CenterY);
			showLayout.addView(note, params);

			note.setOnTouchListener(this);

			note.setId(Integer.valueOf(tString));

			newSite.setWidth(Width);

			newSite.setHeight(Height);

			newSite.setLocate_X(CenterX);

			newSite.setLocate_Y(CenterY);

			newSite.setContent(filePathString);

			newSite.setMark(Integer.valueOf(tString));
			viewList.add(newSite);

		} else if (requestCode == 49 && resultCode != RESULT_CANCELED)
		{
			Uri pictureUri = data.getData();
			String[] proj = { MediaStore.Images.Media.DATA };

			Cursor cursor = managedQuery(pictureUri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			pathPicture = cursor.getString(column_index);
			Intent intent = new Intent(this, ImageHandle.class);
			intent.putExtra("filepath", pathPicture);
			startActivityForResult(intent, 50);

		}

		else if (requestCode == 56 && resultCode != RESULT_CANCELED)
		{

			Bundle bundle = data.getExtras();
			boolean isdelete = bundle.getBoolean("isdelete");
			boolean isedit = bundle.getBoolean("isedit");
			boolean isshadow = bundle.getBoolean("isshadow");

			if (isdelete == true)
			{

				showLayout.removeView((TextView) this.findViewById(viewList
						.get(appoint).getMark()));
				viewList.remove(appoint);

			}
			if (isedit == true)
			{
				ViewSite noteView = viewList.get(appoint);
				openFile(noteView);
			}
			if (isshadow == true)
			{
				// 打开图片管理器
				String status = Environment.getExternalStorageState();

				if (status.equals(Environment.MEDIA_MOUNTED))
				{
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					if (intent != null)
					{
						startActivityForResult(intent, 63);
					}
				} else
				{
					Toast.makeText(FreeEdit.this,getResources().getString(R.string.without_disk), Toast.LENGTH_SHORT)
					.show();
				}

			}

		} else if (requestCode == 63 && resultCode != RESULT_CANCELED)
		{

			Uri pictureUri = data.getData();
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(pictureUri, proj, null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			pathPicture = cursor.getString(column_index);

			Intent intent = new Intent(this, ImageHandle.class);
			intent.putExtra("filepath", pathPicture);
			startActivityForResult(intent, 50);
		}

		else if (requestCode == 70 && resultCode != RESULT_CANCELED)
		{
			Bundle bundle = data.getExtras();
			String content = bundle.getString("content");
			String title = null;
			// 提取出标题
			Pattern titlePattern = Pattern
					.compile("(<\\-\\-title\\:(.*?)\\-\\-\\>)");
			Matcher titleMatcher = titlePattern.matcher(content);
			while (titleMatcher.find())
			{
				String idString = titleMatcher.group();
				title = idString.substring(idString.indexOf("<--title:") + 9,
						idString.indexOf("-->"));
			}
			if (title.equals(""))
			{
				title = getResources().getString(R.string.without_title);
			}

			Drawable drawable = FreeEdit.this.getResources().getDrawable(
					R.drawable.ic_recimage);
			drawable = com.donote.util.ShowIcon.tableIcon(title,
					MainActivity.Width / 2);

			float Width = drawable.getIntrinsicWidth();
			float Height = drawable.getIntrinsicHeight();
			drawable.setBounds(0, 0, (int) Width, (int) Height);

			ViewSite newSite = new ViewSite();
			final TextView note = new TextView(getApplicationContext());
			note.setBackgroundDrawable(drawable);
			CenterX = (int) (showLayout.getWidth() / 2 - Width / 2);
			CenterY = (int) (scrollView.getScrollY() + 210);
			AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
					(int) Width, (int) Height, CenterX, CenterY);
			showLayout.addView(note, params);
			note.setOnTouchListener(this);
			note.setId(Integer.valueOf(tString));
			note.setTextColor(Color.BLACK);
			note.setText("");
			newSite.setWidth(Width);
			newSite.setHeight(Height);
			newSite.setLocate_X(CenterX);
			newSite.setLocate_Y(CenterY);
			newSite.setContent(content);
			newSite.setIsTable(true);
			newSite.setMark(Integer.valueOf(tString));
			viewList.add(newSite);

		} else if (requestCode == 77 && resultCode != RESULT_CANCELED)
		{
			Bundle bundle = data.getExtras();
			String content = bundle.getString("content");

			int id = bundle.getInt("id");
			String title = null;
			Pattern titlePattern = Pattern
					.compile("(<\\-\\-title\\:(.*?)\\-\\-\\>)");
			Matcher titleMatcher = titlePattern.matcher(content);
			while (titleMatcher.find())
			{
				String idString = titleMatcher.group();
				title = idString.substring(idString.indexOf("<--title:") + 9,
						idString.indexOf("-->"));
			}
			ViewSite temp = null;
			for (int i = 0; i < viewList.size(); i++)
			{
				temp = viewList.get(i);
				if (id == temp.getMark())
				{
					temp.setContent(content);
					showLayout.removeView((TextView) this.findViewById(temp.getMark()));

					final TextView note = new TextView(getApplicationContext());

					Drawable drawable = FreeEdit.this.getResources().getDrawable(
							R.drawable.ic_recimage);
					drawable = com.donote.util.ShowIcon.tableIcon(title,
							MainActivity.Width / 2);

					float Width = drawable.getIntrinsicWidth();
					float Height = drawable.getIntrinsicHeight();

					drawable.setBounds(0, 0, (int) Width, (int) Height);
					AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
							(int) Width, (int) Height, (int)temp.getLocate_X(), (int)temp.getLocate_Y());

					note.setBackgroundDrawable(drawable);
					note.setOnTouchListener(this);
					note.setId(Integer.valueOf(temp.getMark()));
					note.setTextColor(Color.BLACK);

					showLayout.addView(note, params);

					viewList.set(i, temp);

				}
			}
		}

		// 添加Picture
		else if (requestCode == 50 && resultCode != RESULT_CANCELED)
		{
			// 获取路径
			Drawable drawable;
			String filePath = data.getExtras().getString("filepath");
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
				drawable = getResources().getDrawable(
						R.drawable.ic_default_image);
			}
			float Width = drawable.getIntrinsicWidth();
			float Height = drawable.getIntrinsicHeight();
			final TextView note = new TextView(getApplicationContext());
			note.setBackgroundDrawable(drawable);
			CenterX = (int) (showLayout.getWidth() / 2 - Width / 2);
			CenterY = (int) (scrollView.getScrollY() + 210);
			AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
					(int) Width, (int) Height, CenterX, CenterY);
			showLayout.addView(note, params);
			note.setOnTouchListener(this);
			note.setId(Integer.valueOf(tString));
			ViewSite newSite = new ViewSite();
			newSite.setWidth(Width);
			newSite.setHeight(Height);
			newSite.setLocate_X(CenterX);
			newSite.setLocate_Y(CenterY);
			newSite.setIsPhoto(true);
			newSite.setContent(filePath);
			newSite.setMark(Integer.valueOf(tString));
			viewList.add(newSite);

		}

		else if (requestCode == 84 && resultCode != RESULT_CANCELED)
		{

			Bundle bundle = data.getExtras();
			String drawfile = pathDraw + bundle.getString("draw");
			Bitmap draw = BitmapFactory.decodeFile(drawfile);
			draw = ShowIcon.zoomBitmap(draw);
			Drawable drawable = new BitmapDrawable(getResources(), draw);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			ViewSite newSite = new ViewSite();
			float Height = Float
					.valueOf((float) (drawable.getIntrinsicHeight() * 1.4));
			float Width = Float
					.valueOf((float) (drawable.getIntrinsicWidth() * 1.4));
			final TextView note = new TextView(getApplicationContext());
			CenterX = (int) (showLayout.getWidth() / 2 - Width / 2);
			CenterY = (int) (scrollView.getScrollY() + 210);
			AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
					(int) Width, (int) Height, CenterX, CenterY);
			showLayout.addView(note, params);
			note.setOnTouchListener(this);
			note.setId(Integer.valueOf(tString));
			note.setTextColor(Color.BLACK);
			note.setBackgroundDrawable(drawable);
			newSite.setWidth(Width);
			newSite.setHeight(Height);
			newSite.setLocate_X(CenterX);
			newSite.setLocate_Y(CenterY);
			newSite.setContent(drawfile);
			newSite.setIsDraw(true);
			newSite.setMark(Integer.valueOf(tString));
			viewList.add(newSite);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setTextStyle(TextView note, int textSize, int textColor) {
		switch (textSize)
		{
		case 0:
			note.setTextSize(40f);
			break;
		case 1:
			note.setTextSize(20f);
			break;
		case 2:
			note.setTextSize(10f);
			break;

		default:
			break;
		}
		switch (textColor)
		{
		case 0:
			note.setTextColor(Color.RED);
			break;
		case 1:
			note.setTextColor(Color.BLACK);
			break;
		case 2:
			note.setTextColor(Color.BLUE);
			break;
		case 3:
			note.setTextColor(Color.GREEN);
			break;
		default:
			break;
		}
	}

	float lastX, lastY;

	public void performLongClick() {
		Intent intent = new Intent(FreeEdit.this, DefineMenu.class);
		startActivityForResult(intent, 56);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		listView.setVisibility(View.GONE);
		catagory_add_Button.setVisibility(View.GONE);
		facePager.setVisibility(View.GONE);
		page_select.setVisibility(View.GONE);
		screenWidth = showLayout.getWidth();
		screenHeight = showLayout.getHeight();

		// 找出哪个view
		int i = 0;
		ViewSite noteView = null;
		for (i = 0; i < viewList.size(); i++)
		{
			noteView = viewList.get(i);
			if (v.getId() == noteView.getMark())
			{
				v.bringToFront();
				break;
			}
		}

		// TODO Auto-generated method stub

		switch (event.getActionMasked())
		{

		case MotionEvent.ACTION_DOWN:

			mLastMotionX = (int) event.getX();
			mLastMotionY = (int) event.getY();
			mCounter++;
			isReleased = false;
			isMoved = false;
			handler.postDelayed(mLongPressRunnable, 700);
			appoint = i;

			lastX = (int) event.getX();
			lastY = (int) event.getY();

			mode = DRAG;

			// 双击事件
			if (firtick == 0l)
			{
				firtick = System.currentTimeMillis();// 前一次点击的时间
			} else if (sectick == 0l)
			{// 后一次点击时间

				sectick = System.currentTimeMillis();

				distance = sectick - firtick;

				if (distance > 0l && distance < 300l)
				{
					firtick = 0l;
					sectick = 0l;
					// 文本
					if (noteView.getIsText() == true)
					{
						Bundle bundle = new Bundle();
						bundle.putInt("size", textSize);
						bundle.putInt("color", textColor);
						bundle.putInt("id", v.getId());
						bundle.putString("text", noteView.getContent()
								.toString());
						Intent intent = new Intent(FreeEdit.this,
								GraphText.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, 14);
					}
					if (noteView.getIsCloude() == true)
					{
						Bundle bundle = new Bundle();
						bundle.putInt("size", textSize);
						bundle.putInt("color", textColor);
						bundle.putInt("id", v.getId());
						bundle.putString("text", noteView.getContent()
								.toString());
						Intent intent = new Intent(FreeEdit.this,
								GraphText.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, 14);
					}
					// 录音
					if (noteView.getIsRecord() == true)
					{
						Intent intent = new Intent(FreeEdit.this,
								PlayMusic.class);
						Bundle bundle = new Bundle();
						if (!new File(noteView.getContent()).exists()) {
							Toast.makeText(FreeEdit.this, getResources().getString(R.string.file_not_found_error),
									Toast.LENGTH_SHORT).show();
							return true;
						}
						bundle.putString("playRec", noteView.getContent());
						intent.putExtras(bundle);
						startActivity(intent);
					}
					if (noteView.getIsFile() == true)
					{
						String pathString = noteView.getContent();
						File file = new File(pathString);
						if (!file.exists()) {
							Toast.makeText(FreeEdit.this,getResources().getString(R.string.file_not_found_error),
									Toast.LENGTH_SHORT).show();
							return true;
						}
						if (checkEndsWithInStringArray(
								pathString,
								getResources().getStringArray(
										R.array.fileEndingWebText)))
						{
							Intent intent = OpenFiles.getHtmlFileIntent(file);
							startActivity(intent);

						} else if (checkEndsWithInStringArray(
								pathString,
								getResources().getStringArray(
										R.array.fileEndingExcel)))
						{
							Intent intent = OpenFiles.getExcelFileIntent(file);
							startActivity(intent);

						} else if (checkEndsWithInStringArray(
								pathString,
								getResources().getStringArray(
										R.array.fileEndingPdf)))
						{
							Intent intent = OpenFiles.getPdfFileIntent(file);
							startActivity(intent);
						} else if (checkEndsWithInStringArray(
								pathString,
								getResources().getStringArray(
										R.array.fileEndingPPT)))
						{
							Intent intent = OpenFiles.getPPTFileIntent(file);
							startActivity(intent);

						} else if (checkEndsWithInStringArray(
								pathString,
								getResources().getStringArray(
										R.array.fileEndingText)))
						{
							Intent intent = OpenFiles.getTextFileIntent(file);
							startActivity(intent);
						} else if (checkEndsWithInStringArray(
								pathString,
								getResources().getStringArray(
										R.array.fileEndingWord)))
						{
							Intent intent = OpenFiles.getWordFileIntent(file);
							startActivity(intent);

						} else
						{
							Intent intent = new Intent(FreeEdit.this,
									ShowFileInfo.class);
							Bundle bundle = new Bundle();
							bundle.putString("file", pathString);
							intent.putExtras(bundle);
							startActivity(intent);
						}
					}
					// 照片
					if (noteView.getIsPhoto() == true)
					{
						File file = new File(noteView.getContent());
						if (!file.exists()) {
							Toast.makeText(FreeEdit.this, getResources().getString(R.string.file_not_found_error),
									Toast.LENGTH_SHORT).show();
							return true;
						}
						Intent intent = OpenFiles.getImageFileIntent(file);
						startActivity(intent);
					}
					// 视频
					if (noteView.getIsVideo() == true)
					{
						File file = new File(noteView.getContent());
						if (!file.exists()) {
							Toast.makeText(FreeEdit.this,getResources().getString(R.string.file_not_found_error),
									Toast.LENGTH_SHORT).show();
							return true;
						}
						Intent intent = OpenFiles.getVideoFileIntent(file);
						startActivity(intent);
					}

					// 图片
					if (noteView.getIsPicture())
					{
						File file = new File(noteView.getContent());
						if (!file.exists()) {
							Toast.makeText(FreeEdit.this, getResources().getString(R.string.file_not_found_error),
									Toast.LENGTH_SHORT).show();
							return true;
						}
						Intent intent = OpenFiles.getImageFileIntent(file);
						startActivity(intent);
					}

					if (noteView.getIsTable())
					{
						Intent intent = new Intent(FreeEdit.this,
								TableEdit.class);
						Bundle bundle = new Bundle();
						bundle.putBoolean("iswrite", true);
						bundle.putString("content", noteView.getContent());
						bundle.putInt("id", noteView.getMark());
						intent.putExtras(bundle);
						startActivityForResult(intent, 77);
					}

				} else
				{
					// 不是连续点击
					firtick = System.currentTimeMillis();// 重新获取前一次点击的时间
					sectick = 0l;
				}
			}

			break;

		case MotionEvent.ACTION_POINTER_DOWN:// 多点触控

			oldDist = this.spacing(event);
			if (oldDist > 10f)
			{
				midPoint(mid, event);
				mode = ZOOM;
				height = noteView.getHeight();
				width = noteView.getWidth();
			}
			break;

		case MotionEvent.ACTION_POINTER_UP:
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			mode = NONE;
			break;

		case MotionEvent.ACTION_UP:
			mCounter = 0;
			isReleased = true;
			handler.removeCallbacks(mLongPressRunnable);
			break;

		case MotionEvent.ACTION_MOVE:

			if (Math.abs(mLastMotionX - event.getX()) > TOUCH_SLOP

					|| Math.abs(mLastMotionY - event.getY()) > TOUCH_SLOP)
			{

				isMoved = true;

			}
			// 放大缩小
			if (mode == ZOOM && noteView.getIsRecord() == false
					&& noteView.getIsFile() == false
					&& noteView.getIsTable() == false)
			{

				newDist = this.spacing(event);
				float scale = newDist / oldDist;

				nHeight = (int) (height * scale);
				nWidth = (int) (width * scale);
				nX = noteView.getCenter_X() - nWidth / 2;
				nY = noteView.getCenter_Y() - nHeight / 2;
				AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
						nWidth, nHeight, (int) nX, (int) nY);

				v.setLayoutParams(params);
				noteView.setWidth(nWidth);
				noteView.setHeight(nHeight);
				if(noteView.getRadius() != 0){
					noteView.setRadius(nWidth /2);
				}
				noteView.setLocate_X(nX);
				noteView.setLocate_Y(nY);
				viewList.set(i, noteView);

			} else if (mode == DRAG)
			{

				int dx = (int) (event.getX() - lastX);
				int dy = (int) (event.getY() - lastY);
				left = v.getLeft() + dx;
				top = v.getTop() + dy;
				right = v.getRight() + dx;
				bottom = v.getBottom() + dy;
				if (left < 0)
				{
					left = 0;
					right = v.getWidth();
				}

				if (top < 0)
				{
					top = 0;
					bottom = v.getHeight();
				}

				if (right > screenWidth)
				{
					right = screenWidth;
					left = screenWidth - v.getWidth();
				}

				if (bottom > screenHeight)
				{
					bottom = screenHeight;
					top = screenHeight - v.getHeight();
				}

				v.layout(left, top, right, bottom);

				nX = left;
				nY = top;

				AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
						(int) noteView.getWidth(), (int) noteView.getHeight(),
						(int) nX, (int) nY);
				v.setLayoutParams(params);

				noteView.setLocate_X(nX);
				noteView.setLocate_Y(nY);

				viewList.set(i, noteView);

				handler.postDelayed(mbroadLoadRunnable, 10);
			}
			break;
		}
		return true;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {

		for (String aEnd : fileEndings)
		{
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	public void initSite(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		handler.removeCallbacks(mLongPressRunnable);
		handler.removeCallbacks(mbroadLoadRunnable);
		super.finish();
	}

	Runnable mLongPressRunnable = new Runnable() {

		@Override
		public void run() {
			mCounter--;
			if (mCounter > 0 || isReleased || isMoved)
				return;
			performLongClick();
		}

	};

	Runnable mbroadLoadRunnable = new Runnable() {

		@Override
		public void run() {

			float mlimit = (float) bottomView.getHeight();
			float mbottom = viewList.get(0).getHeight()
					+ viewList.get(0).getLocate_Y();
			for (int i = 1; i < viewList.size(); i++)
			{

				ViewSite viewSite = viewList.get(i);

				if (mbottom < (viewSite.getHeight() + viewSite.getLocate_Y()))
				{
					mbottom = viewSite.getHeight() + viewSite.getLocate_Y();
				}

			}
			if (mbottom > (mlimit - 70))
			{

				AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
						LayoutParams.FILL_PARENT, bottomView.getHeight() + 350,
						0, 0);

				bottomView.setLayoutParams(params);

			}

			if (mbottom < (mlimit - 700))
			{
				AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
						LayoutParams.FILL_PARENT, bottomView.getHeight() - 350,
						0, 0);

				bottomView.setLayoutParams(params);
			}

		}
	};

	class MessageNote {
		public Drawable drawable;
		public int id;

		public MessageNote(Drawable drawable, int id) {
			this.id = id;
			this.drawable = drawable;
		}
	}

	class imageLoadThread implements Runnable {
		public void run() {

			for (int i = 0; i < viewList.size(); i++)
			{
				ViewSite viewSite = viewList.get(i);
				if (viewSite.getIsPhoto())
				{
					Bitmap photo = com.donote.util.ShowIcon
							.readBitmapAutoSize(viewSite.getContent(),
									(int) viewSite.getWidth(), (int) viewSite
									.getHeight(),
									com.donote.util.ShowIcon
									.readPictureDegree(viewSite
											.getContent()));
					photo = com.donote.util.ShowIcon.zoomBitmap(photo);
					Drawable drawable = new BitmapDrawable(getResources(),
							photo);
					MessageNote messageNote = new MessageNote(drawable,
							viewSite.getMark());
					Message message = new Message();
					message.what = 1;
					message.obj = messageNote;
					handler.sendMessage(message);

				} else if (viewSite.getIsPicture())
				{

					Bitmap picture = com.donote.util.ShowIcon
							.readBitmapAutoSize(viewSite.getContent(),
									(int) viewSite.getWidth(), (int) viewSite
									.getHeight(),
									com.donote.util.ShowIcon
									.readPictureDegree(viewSite
											.getContent()));
					picture = com.donote.util.ShowIcon.zoomBitmap(picture);
					Drawable drawable = new BitmapDrawable(getResources(),
							picture);

					MessageNote messageNote = new MessageNote(drawable,
							viewSite.getMark());

					Message message = new Message();
					message.what = 1;
					message.obj = messageNote;
					handler.sendMessage(message);

				} else if (viewSite.getIsVideo())
				{
					Bitmap video = ThumbnailUtils.createVideoThumbnail(
							viewSite.getContent(),
							MediaStore.Images.Thumbnails.MICRO_KIND);
					Drawable drawable = null;
					if (video != null) {
						video = com.donote.util.ShowIcon.zoomBitmap2(video);
						video = com.donote.util.ShowIcon.VideoIcon(video);
						drawable = new BitmapDrawable(getResources(),
								video);
					}
					else {
						drawable = getResources().getDrawable(
								R.drawable.ic_default_image);
					}
					drawable.setBounds(0, 0, (int) viewSite.getWidth(),
							(int) viewSite.getHeight());
					MessageNote messageNote = new MessageNote(drawable,
							viewSite.getMark());
					Message message = new Message();
					message.what = 1;
					message.obj = messageNote;
					handler.sendMessage(message);
				} else if (viewSite.getIsDraw())
				{
					Bitmap draw = com.donote.util.ShowIcon
							.readBitmapAutoSize(viewSite.getContent(),
									(int) viewSite.getWidth(), (int) viewSite
									.getHeight(),
									com.donote.util.ShowIcon
									.readPictureDegree(viewSite
											.getContent()));
					//haha
					draw = com.donote.util.ShowIcon.zoomBitmap(draw);
					Drawable drawable = new BitmapDrawable(getResources(),
							draw);

					MessageNote messageNote = new MessageNote(drawable,
							viewSite.getMark());

					Message message = new Message();
					message.what = 1;
					message.obj = messageNote;
					handler.sendMessage(message);
				}
			}
			Message message = new Message();
			message.what = 2;
			handler.sendMessage(message);// 结束线程
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			exitDialgo(FreeEdit.this);
		}
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	public void exitDialgo(Context context) {
		// TODO Auto-generated method stub

		String catagory = catagory_TextView.getText().toString();
		String title = titleView.getText().toString();
		String body = "";
		for (int i = 0; i < viewList.size(); i++)
		{
			ViewSite viewSite = viewList.get(i);
			if (viewSite.getIsFace())
			{
				body += "Face^_^[" + viewSite.getContent() + "][:"
						+ String.valueOf(viewSite.getLocate_X()) + ":"
						+ String.valueOf(viewSite.getLocate_Y()) + ":"
						+ String.valueOf(viewSite.getWidth()) + ":"
						+ String.valueOf(viewSite.getHeight()) + ":"
						+ String.valueOf(viewSite.getMark()) + ":]^_^  ";
			} else if (viewSite.getIsPhoto())
			{
				body += "Photo^_^[" + viewSite.getContent() + "][:"
						+ String.valueOf(viewSite.getLocate_X()) + ":"
						+ String.valueOf(viewSite.getLocate_Y()) + ":"
						+ String.valueOf(viewSite.getWidth()) + ":"
						+ String.valueOf(viewSite.getHeight()) + ":"
						+ String.valueOf(viewSite.getMark()) + ":]^_^  ";
			} else if (viewSite.getIsRecord())
			{
				body += "Record^_^[" + viewSite.getContent() + "][:"
						+ String.valueOf(viewSite.getLocate_X()) + ":"
						+ String.valueOf(viewSite.getLocate_Y()) + ":"
						+ String.valueOf(viewSite.getWidth()) + ":"
						+ String.valueOf(viewSite.getHeight()) + ":"
						+ String.valueOf(viewSite.getMark()) + ":]^_^ ";
			} else if (viewSite.getIsText())
			{
				body += "Text^_^[" + viewSite.getContent() + "][:"
						+ String.valueOf(viewSite.getLocate_X()) + ":"
						+ String.valueOf(viewSite.getLocate_Y()) + ":"
						+ String.valueOf(viewSite.getWidth()) + ":"
						+ String.valueOf(viewSite.getHeight()) + ":"
						+ String.valueOf(viewSite.getRadius()) + ":"
						+ String.valueOf(viewSite.getMark()) + ":]^_^  ";
			} else if (viewSite.getIsVideo())
			{
				body += "Video^_^[" + viewSite.getContent() + "][:"
						+ String.valueOf(viewSite.getLocate_X()) + ":"
						+ String.valueOf(viewSite.getLocate_Y()) + ":"
						+ String.valueOf(viewSite.getWidth()) + ":"
						+ String.valueOf(viewSite.getHeight()) + ":"
						+ String.valueOf(viewSite.getMark()) + ":]^_^  ";
			} else if (viewSite.getIsPicture())
			{
				body += "Picture^_^[" + viewSite.getContent() + "][:"
						+ String.valueOf(viewSite.getLocate_X()) + ":"
						+ String.valueOf(viewSite.getLocate_Y()) + ":"
						+ String.valueOf(viewSite.getWidth()) + ":"
						+ String.valueOf(viewSite.getHeight()) + ":"
						+ String.valueOf(viewSite.getMark()) + ":]^_^  ";
			} else if (viewSite.getIsTable())
			{
				body += "Table^_^[" + viewSite.getContent() + "][:"
						+ String.valueOf(viewSite.getLocate_X()) + ":"
						+ String.valueOf(viewSite.getLocate_Y()) + ":"
						+ String.valueOf(viewSite.getWidth()) + ":"
						+ String.valueOf(viewSite.getHeight()) + ":"
						+ String.valueOf(viewSite.getMark()) + ":]^_^  ";
			} else if (viewSite.getIsFile())
			{
				body += "File^_^[" + viewSite.getContent() + "][:"
						+ String.valueOf(viewSite.getLocate_X()) + ":"
						+ String.valueOf(viewSite.getLocate_Y()) + ":"
						+ String.valueOf(viewSite.getWidth()) + ":"
						+ String.valueOf(viewSite.getHeight()) + ":"
						+ String.valueOf(viewSite.getMark()) + ":]^_^  ";
			} else if (viewSite.getIsDraw())
			{
				body += "Draw^_^[" + viewSite.getContent() + "][:"
						+ String.valueOf(viewSite.getLocate_X()) + ":"
						+ String.valueOf(viewSite.getLocate_Y()) + ":"
						+ String.valueOf(viewSite.getWidth()) + ":"
						+ String.valueOf(viewSite.getHeight()) + ":"
						+ String.valueOf(viewSite.getMark()) + ":]^_^  ";
			} else if (viewSite.getIsCloude())
			{
				body += "Cloud^_^[" + viewSite.getContent() + "][:"
						+ String.valueOf(viewSite.getLocate_X()) + ":"
						+ String.valueOf(viewSite.getLocate_Y()) + ":"
						+ String.valueOf(viewSite.getWidth()) + ":"
						+ String.valueOf(viewSite.getHeight()) + ":"
						+ String.valueOf(viewSite.getMark()) + ":]^_^  ";
			}
		}
		final String finalBodyString = body;
		if (("".equals(title) && "".equals(body))
				|| (exit_title.equals(title) && exit_body.equals(body) && exit_catagory
						.equals(catagory))
						|| (exit_title.equals(getResources().getString(R.string.without_title)) && title.equals("")
								&& exit_body.equals(body) && exit_catagory
								.equals(catagory)))
		{
			finish();
		}else if (mRowId == null) {
			DefineSave(finalBodyString);
		} else
		{
			AlertDialog.Builder builder = new Builder(context);
			builder.setMessage(getResources().getString(R.string.weather_to_save));
			builder.setTitle(getResources().getString(R.string.exit));
			builder.setNegativeButton(getResources().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});

			builder.setPositiveButton(getResources().getString(R.string.confirm),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					DefineSave(finalBodyString);
				}
			});
			builder.create().show();
		}

	}

	/*
	 * private PopupWindow cameraPopupWindow(Context cx) { if (cameraWindow ==
	 * null) { cameraWindow = new PopupWindow(cx); View contentView =
	 * LayoutInflater.from(this).inflate( R.layout.camerapopwindow, null);
	 * cameraWindow.setContentView(contentView); ColorDrawable dw = new
	 * ColorDrawable(-00000); cameraWindow.setBackgroundDrawable(dw);
	 * cameraWindow.setWidth(LayoutParams.WRAP_CONTENT);
	 * cameraWindow.setHeight(LayoutParams.WRAP_CONTENT); camera_shot = (Button)
	 * contentView.findViewById(R.id.camera_shot);
	 * camera_shot.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub listView.setVisibility(View.GONE);
	 * catagory_add_Button.setVisibility(View.GONE);
	 * facePager.setVisibility(View.GONE); page_select.setVisibility(View.GONE);
	 * String status = Environment.getExternalStorageState(); // 判断sdcard是否存在 if
	 * (status.equals(Environment.MEDIA_MOUNTED)) { try { File dir = new
	 * File(pathPhoto); if (!dir.exists()) dir.mkdirs(); new DateFormat();
	 * String namePhoto = DateFormat.format( "yyyyMMdd_hhmmss",
	 * Calendar.getInstance(Locale.CHINA)) + ".jpg"; photofile = pathPhoto +
	 * namePhoto; File file = new File(photofile); Uri u = Uri.fromFile(file);
	 * Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
	 * intent.putExtra( MediaStore.Images.Media.ORIENTATION, 0);
	 * intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
	 * startActivityForResult(intent, 28); } catch (ActivityNotFoundException e)
	 * { // TODO Auto-generated catch block Toast.makeText(FreeEdit.this,
	 * "没有找到储存目录", Toast.LENGTH_LONG).show(); }
	 * 
	 * } else { Toast.makeText(FreeEdit.this, "没有储存卡", Toast.LENGTH_LONG)
	 * .show(); } cameraWindow.dismiss(); } });
	 * 
	 * camera_video = (Button) contentView.findViewById(R.id.camera_video);
	 * camera_video.setOnClickListener(new VideoClickListener()); //
	 * 设置PopupWindow外部区域是否可触摸 cameraWindow.setFocusable(true); //
	 * 设置PopupWindow可获得焦点 cameraWindow.setTouchable(true); // 设置PopupWindow可触摸
	 * cameraWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸 return
	 * cameraWindow; } else { return cameraWindow; } }
	 */

	/*
	 * private class VideoClickListener implements OnClickListener {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub listView.setVisibility(View.GONE);
	 * catagory_add_Button.setVisibility(View.GONE);
	 * facePager.setVisibility(View.GONE); page_select.setVisibility(View.GONE);
	 * String status = Environment.getExternalStorageState(); // 判断sdcard是否存在 if
	 * (status.equals(Environment.MEDIA_MOUNTED)) { try { File dir = new
	 * File(pathVideo); if (!dir.exists()) dir.mkdirs(); new DateFormat();
	 * String nameVideo = DateFormat.format("yyyyMMdd_hhmmss",
	 * Calendar.getInstance(Locale.CHINA)) + ".mp4"; videofile = pathVideo +
	 * nameVideo; File file = new File(videofile); Uri u = Uri.fromFile(file);
	 * Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	 * intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
	 * intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
	 * startActivityForResult(intent, 22); } catch (ActivityNotFoundException e)
	 * { // TODO Auto-generated catch block Toast.makeText(FreeEdit.this,
	 * "没有找到储存目录", Toast.LENGTH_LONG) .show(); } } else {
	 * Toast.makeText(FreeEdit.this, "没有储存卡", Toast.LENGTH_LONG).show(); }
	 * cameraWindow.dismiss(); } }
	 */

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
					Toast.makeText(FreeEdit.this, getResources().getString(R.string.cannot_add_empty_group),
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

	// 刷新分类ListView
	public void renderListView() {

		mNoteCursor = MainActivity.mDbHelper.getAllCatagory();
		String[] from = new String[] { NoteDbAdapter.KEY_NAME };
		int[] to = new int[] { R.id.item2 };
		SimpleCursorAdapter notes = new SimpleCursorAdapter(FreeEdit.this,
				R.layout.catagory_setting_lists, mNoteCursor, from, to);
		listView.setAdapter(notes);

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

	private void sendToDesktop() {
		if (mRowId == null)
		{
			Toast.makeText(FreeEdit.this, getResources().getString(R.string.save_note), Toast.LENGTH_SHORT).show();
		} else
		{
			Intent sender = new Intent();
			Intent shortcutIntent = new Intent(this, MainActivity.class);
			shortcutIntent.putExtra(NoteDbAdapter.KEY_ROWID, mRowId);
			shortcutIntent.putExtra(NoteDbAdapter.KEY_TITLE, exit_title);
			shortcutIntent.putExtra(NoteDbAdapter.KEY_BODY, exit_body);
			shortcutIntent.putExtra(NoteDbAdapter.KEY_CATAGORY, exit_catagory);
			shortcutIntent.putExtra("style", 1);
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
	}

	/*
	 * private PopupWindow drawPopupWindow(Context cx) { if(drawWindow == null)
	 * { drawWindow = new PopupWindow(cx); View contentView =
	 * LayoutInflater.from(this).inflate( R.layout.drawpopwindow, null);
	 * drawWindow.setContentView(contentView); ColorDrawable dw = new
	 * ColorDrawable(-00000);
	 * 
	 * drawWindow.setBackgroundDrawable(dw);
	 * drawWindow.setWidth(LayoutParams.WRAP_CONTENT);
	 * drawWindow.setHeight(LayoutParams.WRAP_CONTENT); draw_scrawl = (Button)
	 * contentView.findViewById(R.id.draw_scrawl);
	 * draw_scrawl.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { // TODO Auto-generated method
	 * stub
	 * 
	 * String status = Environment.getExternalStorageState(); if
	 * (status.equals(Environment.MEDIA_MOUNTED)) { Intent intent = new
	 * Intent(FreeEdit.this, Draw.class); startActivityForResult(intent, 84); }
	 * else { Toast.makeText(FreeEdit.this, "没有储存卡", Toast.LENGTH_SHORT)
	 * .show(); } drawWindow.dismiss(); } }); // 设置PopupWindow外部区域是否可触摸
	 * drawWindow.setFocusable(true); // 设置PopupWindow可获得焦点
	 * drawWindow.setTouchable(true); // 设置PopupWindow可触摸
	 * drawWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸 } return
	 * drawWindow;
	 * 
	 * }
	 */

	private PopupWindow menuPopupWindow(Context cx) {

		if (window == null)
		{
			window = new PopupWindow(cx);
			View contentView = LayoutInflater.from(this).inflate(
					R.layout.free_menu, null);
			window.setContentView(contentView);
			ColorDrawable dw = new ColorDrawable(-00000);
			window.setBackgroundDrawable(dw);
			window.setWidth(LayoutParams.WRAP_CONTENT);
			window.setHeight(LayoutParams.WRAP_CONTENT);

			voiceButton = (Button) contentView.findViewById(R.id.menu_voice);
			voiceButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					XsconCreateDialog("appid=51b122a7").show();
					window.dismiss();
				}
			});

			sendButton = (Button) contentView.findViewById(R.id.menu_sent);
			sendButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					sendToDesktop();
					window.dismiss();
				}
			});

			movieButton = (Button) contentView.findViewById(R.id.menu_movie);
			movieButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					listView.setVisibility(View.GONE);
					catagory_add_Button.setVisibility(View.GONE);
					facePager.setVisibility(View.GONE);
					page_select.setVisibility(View.GONE);
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
							Toast.makeText(FreeEdit.this,getResources().getString(R.string.cannot_find_path),
									Toast.LENGTH_LONG).show();
						}
					} else
					{
						Toast.makeText(FreeEdit.this, getResources().getString(R.string.without_disk),
								Toast.LENGTH_LONG).show();
					}
					window.dismiss();
				}
			});

			drawButton = (Button) contentView.findViewById(R.id.menu_draw);
			drawButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED))
					{
						Intent intent = new Intent(FreeEdit.this, Draw.class);
						startActivityForResult(intent, 84);
					} else
					{
						Toast.makeText(FreeEdit.this, getResources().getString(R.string.without_disk),
								Toast.LENGTH_SHORT).show();
					}
					window.dismiss();
				}
			});

			edit_record = (Button) contentView.findViewById(R.id.menu_rec);
			edit_record.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					listView.setVisibility(View.GONE);
					catagory_add_Button.setVisibility(View.GONE);
					facePager.setVisibility(View.GONE);
					page_select.setVisibility(View.GONE);
					String status = Environment.getExternalStorageState();
					if (status.equals(Environment.MEDIA_MOUNTED))
					{
						Intent intent = new Intent(FreeEdit.this,
								NoteRecord.class);
						startActivityForResult(intent, 21);
					} else
					{
						Toast.makeText(FreeEdit.this, getResources().getString(R.string.without_disk),
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
					listView.setVisibility(View.GONE);
					catagory_add_Button.setVisibility(View.GONE);
					facePager.setVisibility(View.GONE);
					page_select.setVisibility(View.GONE);
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
						Toast.makeText(FreeEdit.this, getResources().getString(R.string.without_disk),
								Toast.LENGTH_SHORT).show();
					}
					window.dismiss();
				}
			});

			menu_addition = (Button) contentView
					.findViewById(R.id.menu_addition);
			menu_addition.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					listView.setVisibility(View.GONE);
					catagory_add_Button.setVisibility(View.GONE);
					facePager.setVisibility(View.GONE);
					page_select.setVisibility(View.GONE);
					Intent intent = new Intent(FreeEdit.this, FileView.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(intent, 42);
					window.dismiss();
				}
			});
			// 设置PopupWindow外部区域是否可触摸
			window.setFocusable(true); // 设置PopupWindow可获得焦点
			window.setTouchable(true); // 设置PopupWindow可触摸
			window.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
			return window;
		} else
		{
			return window;
		}
	}

	public String getEditContent(String body){
		String bodyString = body;
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


	private boolean openFile( ViewSite noteView) {
		if (noteView.getIsText() == true)
		{
			Bundle bundle = new Bundle();
			bundle.putInt("size", textSize);
			bundle.putInt("color", textColor);
			bundle.putInt("id", noteView.getMark());
			bundle.putString("text", noteView.getContent()
					.toString());
			Intent intent = new Intent(FreeEdit.this,
					GraphText.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, 14);
			return true;
		}
		// 录音
		if (noteView.getIsRecord() == true)
		{
			Intent intent = new Intent(FreeEdit.this,
					PlayMusic.class);
			Bundle bundle = new Bundle();
			if (!new File(noteView.getContent()).exists()) {
				Toast.makeText(FreeEdit.this, getResources().getString(R.string.file_not_found_error),
						Toast.LENGTH_SHORT).show();
				return true;
			}
			bundle.putString("playRec", noteView.getContent());
			intent.putExtras(bundle);
			startActivity(intent);
			return true;
		}
		if (noteView.getIsFile() == true)
		{
			String pathString = noteView.getContent();
			File file = new File(pathString);
			if (!file.exists()) {
				Toast.makeText(FreeEdit.this,getResources().getString(R.string.file_not_found_error),
						Toast.LENGTH_SHORT).show();
				return true;
			}
			if (checkEndsWithInStringArray(
					pathString,
					getResources().getStringArray(
							R.array.fileEndingWebText)))
			{
				Intent intent = OpenFiles.getHtmlFileIntent(file);
				startActivity(intent);

			} else if (checkEndsWithInStringArray(
					pathString,
					getResources().getStringArray(
							R.array.fileEndingExcel)))
			{
				Intent intent = OpenFiles.getExcelFileIntent(file);
				startActivity(intent);

			} else if (checkEndsWithInStringArray(
					pathString,
					getResources().getStringArray(
							R.array.fileEndingPdf)))
			{
				Intent intent = OpenFiles.getPdfFileIntent(file);
				startActivity(intent);
			} else if (checkEndsWithInStringArray(
					pathString,
					getResources().getStringArray(
							R.array.fileEndingPPT)))
			{
				Intent intent = OpenFiles.getPPTFileIntent(file);
				startActivity(intent);

			} else if (checkEndsWithInStringArray(
					pathString,
					getResources().getStringArray(
							R.array.fileEndingText)))
			{
				Intent intent = OpenFiles.getTextFileIntent(file);
				startActivity(intent);
			} else if (checkEndsWithInStringArray(
					pathString,
					getResources().getStringArray(
							R.array.fileEndingWord)))
			{
				Intent intent = OpenFiles.getWordFileIntent(file);
				startActivity(intent);

			} else
			{
				Intent intent = new Intent(FreeEdit.this,
						ShowFileInfo.class);
				Bundle bundle = new Bundle();
				bundle.putString("file", pathString);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
		// 照片
		if (noteView.getIsPhoto() == true)
		{
			File file = new File(noteView.getContent());
			if (!file.exists()) {
				Toast.makeText(FreeEdit.this, getResources().getString(R.string.file_not_found_error),
						Toast.LENGTH_SHORT).show();
				return true;
			}
			Intent intent = OpenFiles.getImageFileIntent(file);
			startActivity(intent);
		}
		// 视频
		if (noteView.getIsVideo() == true)
		{
			File file = new File(noteView.getContent());
			if (!file.exists()) {
				Toast.makeText(FreeEdit.this,getResources().getString(R.string.file_not_found_error),
						Toast.LENGTH_SHORT).show();
				return true;
			}
			Intent intent = OpenFiles.getVideoFileIntent(file);
			startActivity(intent);
		}

		// 图片
		if (noteView.getIsPicture())
		{
			File file = new File(noteView.getContent());
			if (!file.exists()) {
				Toast.makeText(FreeEdit.this, getResources().getString(R.string.file_not_found_error),
						Toast.LENGTH_SHORT).show();
				return true;
			}
			Intent intent = OpenFiles.getImageFileIntent(file);
			startActivity(intent);
		}

		if (noteView.getIsTable())
		{
			Intent intent = new Intent(FreeEdit.this,
					TableEdit.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("iswrite", true);
			bundle.putString("content", noteView.getContent());
			bundle.putInt("id", noteView.getMark());
			intent.putExtras(bundle);
			startActivityForResult(intent, 77);
		}
		return true;
	}

	public Drawable getRectBack(){
		Random random = new Random();
		Drawable back = null;

		switch (Math.abs(random.nextInt())%5) {
		case 0:
			back = this.getResources()
			.getDrawable(R.drawable.ic_rect);
			break;
		case 1:
			back = this.getResources()
			.getDrawable(R.drawable.ic_rect1);
			break;
		case 2:
			back = this.getResources()
			.getDrawable(R.drawable.ic_rect2);
			break;
		case 3:
			back = this.getResources()
			.getDrawable(R.drawable.ic_rect3);
			break;
		case 4:
			back = this.getResources()
			.getDrawable(R.drawable.ic_rect4);
			break;

		} 
		return back;

	}

	public Drawable getCircleBack(){
		Random random = new Random();
		Drawable back = null;

		switch (Math.abs(random.nextInt())%5) {
		case 0:
			back = this.getResources()
			.getDrawable(R.drawable.ic_circle);
			break;
		case 1:
			back = this.getResources()
			.getDrawable(R.drawable.ic_circle1);
			break;
		case 2:
			back = this.getResources()
			.getDrawable(R.drawable.ic_circle2);
			break;
		case 3:
			back = this.getResources()
			.getDrawable(R.drawable.ic_circle3);
			break;
		case 4:
			back = this.getResources()
			.getDrawable(R.drawable.ic_circle4);
			break;

		} 
		return back;

	}

	public Drawable getCloudeBack(){
		Random random = new Random();
		Drawable back = null;

		switch (Math.abs(random.nextInt())%5) {
		case 0:
			back = this.getResources()
			.getDrawable(R.drawable.ic_cloude);
			break;
		case 1:
			back = this.getResources()
			.getDrawable(R.drawable.ic_cloude1);
			break;
		case 2:
			back = this.getResources()
			.getDrawable(R.drawable.ic_cloude2);
			break;
		case 3:
			back = this.getResources()
			.getDrawable(R.drawable.ic_cloude3);
			break;
		case 4:
			back = this.getResources()
			.getDrawable(R.drawable.ic_cloude4);
			break;

		} 
		return back;

	}


}