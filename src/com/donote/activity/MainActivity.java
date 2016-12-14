package com.donote.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mobstat.StatService;
import com.donote.adapter.GroupListAdapter;
import com.donote.adapter.MyCursorTreeAdapter;
import com.donote.adapter.MySimpleCursorAdapter;
import com.donote.adapter.MySimpleCursorAdapter.ViewHolder;
import com.donote.adapter.NoteDbAdapter;
import com.donote.alarm.AlarmReceiver;
import com.donote.imagehandler.ImageMemoryCache;
import com.donote.implement.ImageButtonOnTouch;
import com.donote.util.BaiduAccessTokenKeeper;
import com.donote.util.BaiduApiUtil;
import com.donote.util.GroupListView;
import com.donote.util.HttpUtil;
import com.donote.util.MyLocationListener;
import com.donote.util.MyShakeBootService;
import com.donote.util.RefreshableLinearLayout;
import com.donote.util.RefreshableLinearLayout.RefreshListener;
import com.donote.util.ShowIcon;
import com.donote.util.Weather;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.wxl.donote.R;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity{

	private View mainLayoutView;
	private HttpEntity entity;
	private String syncResultString = null;
	// 一些笔记的说明
	private static String hello = "hello，欢迎使用Do笔记。Do笔记能为您做些什么呢Face:f032 \n"
			+ "1. 同步功能。使用Do笔记账号或者第三方账号登陆，同时绑定百度云盘，即可享受资料云端备份，笔记内容和附件永不丢失。\n"
			+ "2. 智能提醒。只要在笔记里添加日程安排，保存时即可自动提醒生成提醒。支持几乎所有格式！Face:f053 \n"
			+ "3. 强大的富文本编辑。能够在笔记里添加图片，涂鸦，声音，视频，文件，语音识别，插入位置，表格。各种多媒体内容可直接打开。Face:f044 \n"
			+ "4. 笔记自由编辑模式。自由编辑模式里能够识别图形（矩形，圆形，表格），图形可添加文字，拖动和缩放。同样可插入多媒体资源！Face:f053\n"
			+ "5. 友好的交互。Face:f003 摇动新建笔记，桌面插件，主界面音量键调整亮度，普通编辑界面和浏览界面音量键改变字体大小。\n"
			+ "6. 定制您的Do笔记。可设置背景，字体大小，笔记排序方式，闹钟铃声。\n"
			+ "现在起，记录一切。\n";
	private static String hi = "     Face:f000 各位评委们好"
			+ "非常高兴参加中国软件杯这么一个能够激发我们热情的比赛。本着体验良好和功能实用性的原则，我们不仅完成了比赛要求的功能，更加入了自己的想法，"
			+ "云备份和百度云盘的结合，更加完善的智能提醒，笔记自由编辑模式，友好的交互和扁平化界面的设计，使我们在各大市场的累计下载量已超10万。"
			+ "感谢中国软件杯这个平台，丰富了我们的开发经验，将学到的东西真正用于实际。"
			+ "最后祝各位评委们身体健康，万事如意！\n\n" + "             ---Domen团队";
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int ACTIVITY_ALARM = 4;
	public static int cur_bright = 0;// 系统当前亮度
	public static int textsize = 1;// 系统当前字体大小
	private long exit_time = 0;// 用于双击退出
	private Long cRowId;// 笔记ID
	@SuppressWarnings("unused")
	private ShowIcon show;// 显示图标方法
	//private int normalBright;
	private int times = 0;
	private AlertDialog.Builder builder;
	public static NoteDbAdapter mDbHelper;// NoteDbAdapter类用于数据库操作
	private Cursor mNoteCursor;
	private long startTime;// 为亮度调节window显示的开始时间赋值
	public static Cursor group_names = null;// 所有分组名游标
	private ViewPager mPager;
	private ArrayList<View> Views;
	private int offset = 0;
	private int currIndex = 0;
	private int bmpW;
	public static GroupListAdapter adapter;
	private OnChildClickListener onChildClickListener;
	private ImageView cursor;
	private TextView tNote, tSetting;
	private ImageButton add_button;
	private ImageButton search_button;
	private ImageButton batch_delete_button;
	private ImageButton batch_move_button;
	private ImageButton batch_lock;
	private ImageButton batch_unlock;
	private RefreshableLinearLayout refreshableLinearLayout;
	private RefreshableLinearLayout groupRefreshableLinearLayout;
	private TextView normalStyle;
	private TextView freeStyle;
	private SeekBar seekBar;
	private PopupWindow seekBarPopupWindow = null;
	private PopupWindow stylePopupWindow = null;
	private OnClickListener add_button_listener = null;
	private ListView listView;
	private GroupListView exList;
	public static MySimpleCursorAdapter notes;
	private String catagory_OldString;// 旧分组名
	private AlarmManager alarm_service;
	private PopupWindow popupWindow;
	private View menuView;// 批量弹出菜单
	private ArrayAdapter<String> allCatagory_adapter;
	private Spinner spinner_c;
	private List<String> allCatagory;
	private Weather weather;// 用于获取天气
	private Thread weatherThread;
	private Thread shareThread;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = null;
	public static float Height = 0;// 屏幕高度
	public static float Width = 0;// 屏幕宽度
	public static float density;// 屏幕密度
	private String sync_url = "http://1.hankwing.duapp.com/sync";
	private String sync_from_web = "http://1.hankwing.duapp.com/syncToAndroid";
	private HttpResponse httpResponse;
	private ProgressDialog myDialog = null;
	private SharedPreferences account = null;
	private BaiduApiUtil baiduUtil;
	private SlidingMenu menu;
	private TextView account_name;
	private ImageButton slide_menu_sync;
	private ImageButton slide_menu_set;
	private ProgressBar proBar; 
	private TextView slide_menu_batch;
	private TextView slide_menu_logout;
	private TextView slide_menu_tieBaidu;
	private TextView slide_menu_baidu_username;
	private TextView slide_baidu_space;
	private TextView slide_baidu_upload;
	private TextView slide_baidu_download;
	private TextView slide_menu_suggess;
	private ProgressBar upload_bar;
	private ProgressBar download_bar;
	//private LinearLayout interGELinearlayout;
	boolean time = false;//判断是否开启屏幕广告

	/**
	 * 天气，分享，线程的信息处理
	 */
	private Handler handler = new Handler() {
		int time = 0;
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub  
			switch (msg.what)
			{
			case 0:
				Cursor cursor = mDbHelper.getnote((Long) msg.obj);
				edit_Enter(cursor, (Long) msg.obj);
				break;
			case 1:
				weatherThread = new Thread(new weatherLoadThread());
				weatherThread.setDaemon(true);
				weatherThread.start();// 启动线程
				break;
			case 3:
				if (time < 4)
				{
					weather.getWeather();
					time++;
				} else
				{
					Toast.makeText(MainActivity.this, "天气信息不可用",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 4:
				Toast.makeText(MainActivity.this, "无法获取位置信息",
						Toast.LENGTH_SHORT).show();
				break;
			case 5:
				startActivityForResult((Intent) msg.obj, ACTIVITY_CREATE);
				// overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				break;
			case 6:
				startActivityForResult((Intent) msg.obj, ACTIVITY_CREATE);
				// overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				break;
			case 8:
				if (time < 4)
				{
					weather.getLocString();
					time++;
				} else
				{
					Toast.makeText(MainActivity.this, "天气信息不可用",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 9:
				Toast.makeText(MainActivity.this,getResources().getString(R.string.interner_error), Toast.LENGTH_SHORT)
				.show();
				handler.removeCallbacks(weatherThread);
				break;
			case 10:
				Intent i = new Intent(MainActivity.this, CommonEdit.class);
				startActivityForResult(i, ACTIVITY_CREATE);
				break;
			case 11:
				seekBarPopupWindow.dismiss();
				break;
			case 12:
				startActivity((Intent) msg.obj);
				break;
			case 13:
				myDialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.please_wait),
						getResources().getString(R.string.syncing));

				new Thread(new Runnable() {
					public void run() {
						HttpPost httpRequest = new HttpPost(sync_from_web);
						httpRequest.addHeader("account_name",
								account.getString("account_name", null));
						try
						{
							DefaultHttpClient httpClient = new DefaultHttpClient();
							httpClient.getParams().setParameter(
									CoreConnectionPNames.CONNECTION_TIMEOUT,
									10000);
							httpClient.getParams().setParameter(
									CoreConnectionPNames.SO_TIMEOUT, 10000);
							HttpResponse httpResponse = httpClient
									.execute(httpRequest);
							if (httpResponse == null)
							{
								Looper.prepare();
								Toast.makeText(MainActivity.this,
										getResources().getString(R.string.interner_error), Toast.LENGTH_SHORT)
										.show();
								Looper.loop();
								return;
							}
							if (httpResponse.getStatusLine().getStatusCode() == 200)
							{
								// mDbHelper.deleteAllNotesAndCat();//清空数据库
								HttpEntity entity = httpResponse.getEntity();
								if (entity != null)
								{
									String allJson = EntityUtils.toString(
											entity, "UTF-8");
									allJson = allJson.substring(allJson
											.indexOf("{"));
									ArrayList<HashMap<String, Object>> temp = new ArrayList<HashMap<String, Object>>();
									temp = HttpUtil.AnalysisNotes(allJson);
									for (int i = 0; i < temp.size(); i++)
									{
										HashMap<String, Object> map = temp
												.get(i);
										// logger.log(Level.INFO, "succeed");
										String sql = null;
										if (map.containsKey("title"))
										{
											if (mDbHelper
													.getnote(
															Integer.parseInt((String) map
																	.get("_id")))
																	.isAfterLast())
											{
												sql = "INSERT INTO "
														+ "note(_id, title, body,catagory,created,content,modify,alarmflag,ischecked,"
														+ "isclocked,isexpend,style) VALUES "
														+ "("
														+ (String) map
														.get("_id")
														+ ",'"
														+ (String) map
														.get("title")
														+ "','"
														+ (String) map
														.get("body")
														+ "','"
														+ (String) map
														.get("catagory")
														+ "','"
														+ (String) map
														.get("created")
														+ "','"
														+ (String) map
														.get("content")
														+ "','"
														+ (String) map
														.get("modify")
														+ "',"
														+ (String) map
														.get("alarmflag")
														+ ","
														+ (String) map
														.get("ischecked")
														+ ","
														+ (String) map
														.get("isclocked")
														+ ","
														+ (String) map
														.get("isexpend")
														+ ","
														+ (String) map
														.get("style")
														+ ")";
												mDbHelper.executeSql(sql);
											}
										} else if (mDbHelper.getCatagory(
												Integer.parseInt((String) map
														.get("_id")))
														.isAfterLast())
										{
											sql = "insert into catagory(_id, name) values"
													+ "("
													+ (String) map.get("_id")
													+ ",'"
													+ (String) map.get("name")
													+ "')";
											mDbHelper.executeSql(sql);
										}
									}
								}
							}
						} catch (ClientProtocolException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JSONException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally
						{
							myDialog.dismiss();
							Looper.prepare();
							Toast.makeText(MainActivity.this, getResources().getString(R.string.sync_success),
									Toast.LENGTH_SHORT).show();
							Message msg;
							msg = new Message();
							msg.what = 14;
							handler.sendMessage(msg);
							Looper.loop();
						}
					}
				}).start();
				break;
			case 14:
				notes.notifyDataSetChanged();
				adapter.notifyDataSetChanged();
				break;
			case 15:  
				slide_baidu_space.setText((String) msg.obj);
				break;
			case 16:
				slide_menu_baidu_username.setText(BaiduApiUtil.access_token
						.getUserName());
				slide_menu_baidu_username.setVisibility(View.VISIBLE);
				slide_menu_tieBaidu.setText(getResources().getString(R.string.baidu_cloud_has_binding));
				slide_menu_tieBaidu.setClickable(false);
				slide_baidu_download.setClickable(true);
				slide_baidu_upload.setClickable(true);
				slide_baidu_space.setTextColor(getResources().getColor(R.color.dark_white));
				slide_baidu_space.setCompoundDrawablesWithIntrinsicBounds(getResources().
						getDrawable(R.drawable.slide_menu_used), null, null, null);
				slide_baidu_download.setTextColor(getResources().getColor(R.color.dark_white));
				slide_baidu_download.setCompoundDrawablesWithIntrinsicBounds(getResources().
						getDrawable(R.drawable.slide_menu_download), null, null, null);
				slide_baidu_upload.setTextColor(getResources().getColor(R.color.dark_white));
				slide_baidu_upload.setCompoundDrawablesWithIntrinsicBounds(getResources().
						getDrawable(R.drawable.slide_menu_upload), null, null, null);
				baiduUtil.getQuota();
				break;
			case 17:
				if(msg.arg1 == -1) {
					slide_baidu_upload.setClickable(true);
					slide_baidu_upload.setText(getResources().getString(R.string.upload_notes_accessory));
					upload_bar.setVisibility(View.GONE);
					Toast.makeText(MainActivity.this,getResources().getString(R.string.upload_notes_accessory_success),Toast.LENGTH_SHORT).show();
				}
				else {
					slide_baidu_upload.setText((String)msg.obj);
					upload_bar.setProgress(msg.arg1);
				}
				break;
			case 18:
				if(msg.arg1 == -1) {
					slide_baidu_download.setClickable(true);
					slide_baidu_download.setText(getResources().getString(R.string.download_notes_accessory));
					download_bar.setVisibility(View.GONE);
					Toast.makeText(MainActivity.this,getResources().getString(R.string.download_notes_accessory_success),Toast.LENGTH_SHORT).show();
				}
				else {
					slide_baidu_download.setText((String)msg.obj);
					download_bar.setProgress(msg.arg1);
				}
				break;

			case 21:
				if( com.donote.widget.DoNoteWidgetProvider.note_id != 0){
					//判断自由还是编辑
					if(com.donote.widget.DoNoteWidgetProvider.note_style == 0){
						Intent intent2 = new Intent(MainActivity.this, CommonEdit.class);
						Bundle bundle = new Bundle();
						bundle.putString("wxl","widget");
						intent2.putExtras(bundle);
						startActivityForResult(intent2, ACTIVITY_CREATE);
					}
					if(com.donote.widget.DoNoteWidgetProvider.note_style == 1){
						Intent intent3 = new Intent(MainActivity.this, FreeEdit.class);
						Bundle bundle = new Bundle();
						bundle.putString("wxl","widget");
						intent3.putExtras(bundle);
						startActivityForResult(intent3, ACTIVITY_CREATE);
					}
				}
				break;
			}
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		try
		{
			getWindow().addFlags(
					WindowManager.LayoutParams.class.getField(
							"FLAG_NEEDS_MENU_KEY").getInt(null));
		} catch (NoSuchFieldException e)
		{
			// Ignore since this field won't exist in most versions of Android
		} catch (IllegalAccessException e)
		{
			// Log.w(TAG,
			// "Could not access FLAG_NEEDS_MENU_KEY in addLegacyOverflowButton()",
			// e);
		}
		mainLayoutView = findViewById(R.id.main_layout);
		// 获得背景图片
		Bitmap temp = ImageMemoryCache.getBitmap((long) -1, "beijing");
		if (temp != null)
		{
			Log.i("bae", "background!");
			Drawable beijing = new BitmapDrawable(getResources(), temp);
			mainLayoutView.setBackgroundDrawable(beijing);
			//mainLayoutView.getBackground().setAlpha(80);
		}
		// weather_icon = (ImageView) findViewById(R.id.weather_info);
		myListener = new MyLocationListener(handler);
		builder = new Builder(MainActivity.this);
		builder.setMessage(getResources().getString(R.string.confirm_delete));
		builder.setTitle(getResources().getString(R.string.tip));
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		setLocationOption();
		DisplayMetrics Win = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(Win);
		density = Win.density;
		Width = Win.widthPixels;
		Height = Win.heightPixels;
		show = new ShowIcon(MainActivity.this, Width, Height);
		mLocationClient.registerLocationListener(myListener);
		// 设置添加按钮的响应
		add_button_listener = new OnClickListener() {
			public void onClick(View v) {
				PopupWindow popupWindow = stylePopupWindow(MainActivity.this);
				popupWindow.showAsDropDown(add_button);
			}
		};

		alarm_service = (AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);

		// 配置数据库，并加入默认数据
		mDbHelper = new NoteDbAdapter(this);
		mDbHelper.open();
		group_names = mDbHelper.getAllCatagory();
		if (group_names.getCount() == 0)
		{
			mDbHelper.createCatagory(getResources().getString(R.string.default_group));
			mDbHelper.createCatagory(getResources().getString(R.string.free_group));
			mDbHelper.createCatagory(getResources().getString(R.string.common_group));
		}

		if (mDbHelper.getCount() == 0)
		{
			// for(int i =0;i<1000;i++) {
			mDbHelper.createNote("欢迎使用Do智能笔记", hello, 0, getResources().getString(R.string.default_group));
			// }
			mDbHelper.createNote("来自Domen团队", hi, 0,getResources().getString(R.string.default_group));
		}

		// 以下为滑动屏幕及便签功能
		mPager = (ViewPager) this.findViewById(R.id.vPager);
		Views = new ArrayList<View>();
		LayoutInflater mInflater = LayoutInflater.from(this);
		Views.add(mInflater.inflate(R.layout.item, null));
		Views.add(mInflater.inflate(R.layout.main_group, null));
		mPager.setAdapter(new TabPagerAdapter(Views));
		mPager.setCurrentItem(0);
		// 初始化滑动菜单
		baiduUtil = new BaiduApiUtil(this,handler);
		account = getSharedPreferences("account", 0);
		initSlidingMenu();
		//interGELinearlayout = (LinearLayout)findViewById(R.id.interGELinearlayout);
		//new AdView(this, interGELinearlayout).DisplayAd();
		add_button = (ImageButton) findViewById(R.id.add);
		add_button.setOnClickListener(add_button_listener);
		search_button = (ImageButton) findViewById(R.id.query);
		add_button.setOnTouchListener(new ImageButtonOnTouch(add_button,
				"#3f000000", "#0076BBD8"));
		search_button.setOnTouchListener(new ImageButtonOnTouch(search_button,
				"#3f000000", "#0076BBD8"));
		search_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, NoteSearch.class);
				startActivity(intent);
			}
		});
		// 第二屏点击子项进入编辑界面
		onChildClickListener = new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				if (MyCursorTreeAdapter.visflag)
				{
					CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox);
					cb.performClick();
					return true;
				} else
				{
					group_names.moveToPosition(groupPosition);
					Cursor c = adapter.getChildrenCursor(group_names);
					group_names.moveToFirst();
					c.moveToPosition(childPosition);
					edit_Enter(c, id);
				}
				return true;
			}
		};

		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = getResources().getDrawable(R.drawable.tab_color).getIntrinsicWidth();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;
		offset = (screenW / 2 - bmpW) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);

		tNote = (TextView) this.findViewById(R.id.tNote);
		tSetting = (TextView) this.findViewById(R.id.tGroup);
		TabClickListener tCl0 = new TabClickListener(0, tNote);
		TabClickListener tCl1 = new TabClickListener(1, tSetting);
		tNote.setOnTouchListener(tCl0);
		tNote.setOnClickListener(tCl0);
		tSetting.setOnTouchListener(tCl1);
		tSetting.setOnClickListener(tCl1);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		SharedPreferences settings = getSharedPreferences("textsize", 0);
		switch (settings.getInt("size", 1))
		{
		case 0:
			textsize = 0;
			break;
		case 2:
			textsize = 2;
			break;
		case 3:
			textsize = 3;
			break;
		default:
			break;
		}
		shareThread = new Thread(new shareLoadThread());
		shareThread.setDaemon(true);
		shareThread.start();// 启动线程


	}

	/**
	 * 点击笔记进入浏览界面
	 * 
	 * @param c
	 *            笔记游标
	 * @param id
	 *            笔记ID
	 */
	public void edit_Enter(Cursor c, Long id) {
		int style = c.getInt(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE));
		Intent i = null;
		if (style == 0)
		{
			i = new Intent(MainActivity.this, DisplayContent.class);
		} else if (style == 1)
		{
			i = new Intent(MainActivity.this, FreeEdit.class);
		}
		i.putExtra(NoteDbAdapter.KEY_ROWID, id);
		i.putExtra(NoteDbAdapter.KEY_TITLE,
				c.getString(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
		i.putExtra(NoteDbAdapter.KEY_BODY,
				c.getString(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
		i.putExtra(NoteDbAdapter.KEY_CATAGORY, c.getString(c
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_CATAGORY)));
		startActivityForResult(i, ACTIVITY_EDIT);
		c.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 2 && resultCode == RESULT_OK)
		{
			Bundle extras = data.getExtras();
			if (extras != null)
			{
				Cursor cursor = mDbHelper.getnote(extras.getLong("ID"));
				int year = extras.getInt("year");
				int month = extras.getInt("month") - 1;
				int day = extras.getInt("day");
				int hour = extras.getInt("hour");
				int minute = extras.getInt("minute");
				Intent intent = new Intent(this, AlarmReceiver.class);
				intent.putExtra("title", cursor.getString(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
				intent.putExtra("body", cursor.getString(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
				intent.putExtra("Position", extras.getInt("Position"));
				intent.putExtra("ID",
						notes.getItemId(extras.getInt("Position")));
				intent.putExtra("flag", 1);// 设定标志位，说明该alarm来自第一屏
				Calendar calendar = Calendar.getInstance();
				Calendar anotherCalendar = Calendar.getInstance();
				anotherCalendar.set(year, month, day, hour, minute, 0);
				if (calendar.compareTo(anotherCalendar) == -1)
				{
					calendar.set(year, month, day, hour, minute, 0);
				} else
				{
					Toast.makeText(MainActivity.this, getResources().getString(R.string.time_passed),
							Toast.LENGTH_SHORT).show();
					return;
				}
				long alarmIdLong = mDbHelper.createAlarm(calendar,
						extras.getLong("ID"), getResources().getString(R.string.whole_note));
				intent.putExtra("alarmID", alarmIdLong);
				PendingIntent p_intent = PendingIntent.getBroadcast(this,
						(int) alarmIdLong, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				mDbHelper.updateAlarmflag(extras.getLong("ID"));
				notes.notifyDataSetChanged();
				adapter.notifyDataSetChanged();
				// Schedule the alarm
				alarm_service.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), p_intent);
				Toast.makeText(this, getResources().getString(R.string.new_alarm_has_set), Toast.LENGTH_LONG)
				.show();
				cursor.close();
			}
		}

		else if (requestCode == 3 && resultCode == RESULT_OK)
		{
			Bundle extras = data.getExtras();
			if (extras != null)
			{
				int listPos = 0;
				Cursor cursor = mDbHelper.getnote(extras.getLong("ID"));
				int year = extras.getInt("year");
				int month = extras.getInt("month") - 1;
				int day = extras.getInt("day");
				int hour = extras.getInt("hour");
				int minute = extras.getInt("minute");
				for (int j = 0; j <= extras.getInt("groupPosition") - 1; j++)
				{
					listPos += adapter.getChildrenCount(j);
				}
				Intent intent = new Intent(this, AlarmReceiver.class);
				intent.putExtra("listPos",
						listPos + extras.getInt("childPosition"));
				intent.putExtra("title", cursor.getString(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
				intent.putExtra("body", cursor.getString(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
				intent.putExtra("groupPosition", extras.getInt("groupPosition"));
				intent.putExtra("childPosition", extras.getInt("childPosition"));
				intent.putExtra("ID", adapter.getChildId(
						extras.getInt("groupPosition"),
						extras.getInt("childPosition")));
				intent.putExtra("flag", 2);// 设定标志位，说明该alarm来自第二屏
				Calendar calendar = Calendar.getInstance();
				Calendar anotherCalendar = Calendar.getInstance();
				anotherCalendar.set(year, month, day, hour, minute, 0);
				if (calendar.compareTo(anotherCalendar) == -1)
				{
					calendar.set(year, month, day, hour, minute, 0);
				} else
				{
					Toast.makeText(MainActivity.this, getResources().getString(R.string.time_passed),
							Toast.LENGTH_SHORT).show();
					return;
				}
				long alarmIdLong = mDbHelper.createAlarm(calendar,
						extras.getLong("ID"), getResources().getString(R.string.whole_note));
				intent.putExtra("alarmID", alarmIdLong);
				PendingIntent p_intent = PendingIntent.getBroadcast(this,
						(int) alarmIdLong, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				MainActivity.mDbHelper.updateAlarmflag(extras.getLong("ID"));
				// Schedule the alarm!
				notes.notifyDataSetChanged();
				adapter.notifyDataSetChanged();
				alarm_service.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), p_intent);
				Toast.makeText(this,getResources().getString(R.string.new_alarm_has_set), Toast.LENGTH_LONG)
				.show();
				cursor.close();
			}
		}

		else if (requestCode == ACTIVITY_ALARM)
		{
			if (resultCode == RESULT_FIRST_USER)
			{
				adapter.notifyDataSetChanged();
			}
		} else if (requestCode == 51 && resultCode != RESULT_CANCELED)
		{
			menu.toggle(true);
			Bitmap temp = ImageMemoryCache.getBitmap((long) -1, "beijing");
			if (temp != null)
			{
				Drawable beijing = new BitmapDrawable(getResources(), temp);
				mainLayoutView.setBackgroundDrawable(beijing);
				//mainLayoutView.getBackground().setAlpha(80);
			} // 設置透明度
			else
			{
				mainLayoutView.setBackgroundResource(R.drawable.beijing2);
			}
		}
	}

	// 便签功能
	private class TabClickListener implements OnClickListener, OnTouchListener {
		private int index = 0;
		private TextView bt;

		public TabClickListener(int i, TextView bt) {
			index = i;
			this.bt = bt;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				bt.setBackgroundColor(Color.parseColor("#ffC2CBCB"));
			}
			if (event.getAction() == MotionEvent.ACTION_UP)
			{
				bt.setBackgroundColor(Color.parseColor("#afC2CBCB"));
			}
			return false;
		}
	}

	// 滑动
	public class TabPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public TabPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		// 子屏幕初始化函数
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			switch (arg1)
			{
			case 0:
				listView = (ListView) arg0.findViewById(R.id.item_list);
				listView.setOnItemClickListener(new ItemClickListener());
				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						actionClickMenuBatch();
						return false;
					}
				});
				listView.setEmptyView(findViewById(R.id.empty));
				RefreshListener listener = new RefreshListener() {
					@Override
					public void onRefresh(RefreshableLinearLayout view) {
						referesh(1);
					}
				};
				refreshableLinearLayout = (RefreshableLinearLayout) findViewById(R.id.refreshListView);
				refreshableLinearLayout.setRefreshListener(listener);
				refreshableLinearLayout.setListView(listView);
				initListView();// 初始化第一屏幕
				break;
			case 1:
				initGroupsView();// 初始化第二屏幕
				break;
			}
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0)
			{
			case 0:
				menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				if (currIndex == 1)
				{
					if (MyCursorTreeAdapter.visflag == true)
					{
						notes.notifyDataSetChanged();
					}
					animation = new TranslateAnimation(one, 0, 0, 0);
				}
				break;
			case 1:
				menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				if (currIndex == 0)
				{
					if (MySimpleCursorAdapter.visflag == true)
					{
						adapter.notifyDataSetChanged();
					}
					animation = new TranslateAnimation(0, one, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	// 第二屏子项目长按弹出菜单
	private final class childOptionMenu implements OnCreateContextMenuListener {
		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
			int type = ExpandableListView
					.getPackedPositionType(info.packedPosition);
			int groupPos = GroupListView
					.getPackedPositionGroup(info.packedPosition);
			Cursor c = group_names;
			c.moveToPosition(groupPos);
			if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
			{
				actionClickMenuBatch();
			} else if (!c.getString(
					c.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME)).equals(
							getResources().getString(R.string.default_group)))
			{
				menu.add(0, R.id.catagory_delete, 0, MainActivity.this.getResources().getText(R.string.delete));
				menu.add(0, R.id.catagory_rename, 0, getResources().getString(R.string.rename));
			}

		}
	}

	// 第二屏长按菜单响应
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId())
		{

		case R.id.catagory_delete:// 删除分组
			ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
			.getMenuInfo();
			catagory_delete_showDialog(info);
			return true;
		case R.id.catagory_rename:
			ExpandableListContextMenuInfo info2 = (ExpandableListContextMenuInfo) item
			.getMenuInfo();
			Cursor cursor = group_names;
			int groupPos = GroupListView
					.getPackedPositionGroup(info2.packedPosition);
			cursor.moveToPosition(groupPos);
			catagory_OldString = cursor.getString(cursor
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME));
			cRowId = info2.id;
			showDialog();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * 重命名分组显示对话框
	 */
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
				if (cRowId != null && !name.equals(""))
				{
					mDbHelper.updateCatagory(cRowId, name);
					mDbHelper.updata_catagory_notes(catagory_OldString,
							name);
					group_names = mDbHelper.getAllCatagory();
					Toast.makeText(MainActivity.this, getResources().getString(R.string.rename_success),
							Toast.LENGTH_SHORT).show();
				} else
				{
					Toast.makeText(MainActivity.this, getResources().getString(R.string.cannot_name_empty),
							Toast.LENGTH_SHORT).show();
				}
				Intent mIntent = new Intent();
				setResult(RESULT_OK, mIntent);
				notes.notifyDataSetChanged();
				adapter.notifyDataSetChanged();

			}
		}).setNegativeButton(getResources().getString(R.string.cancel), null).create().show();

	}

	// 点击条目进入浏览
	public final class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (MySimpleCursorAdapter.visflag)
			{
				ViewHolder vh = (ViewHolder) view.getTag();
				vh.cb.performClick();
				return;
			} else
			{
				Cursor c = mDbHelper.getAllNotes();
				c.moveToPosition(position);
				edit_Enter(c, id);
			}
		}
	}

	/*// 主页面菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
		case R.id.menu_setting:
			actionClickMenuSetting();
			break;
		case R.id.menu_batch:
			actionClickMenuBatch();
			break;
		case R.id.menu_bind:
			baiduUtil.login();
			break;
		case R.id.menu_sync:
			mNoteCursor = mDbHelper.getAllNotes();
			baiduUtil.sync(mNoteCursor,handler);
			break;
		case R.id.menu_download:
			mNoteCursor = mDbHelper.getAllNotes();
			baiduUtil.downLoad(mNoteCursor);
			break;
		}

		return super.onOptionsItemSelected(item);
	}*/

	@SuppressLint("ResourceAsColor")
	private void actionClickMenuBatch() {
		// TODO Auto-generated method stub {
		MySimpleCursorAdapter.visflag = true;
		MyCursorTreeAdapter.visflag = true;
		if (popupWindow == null)
		{
			// TODO Auto-generated method stub
			menuView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.main_batch_menu, null);
			// menuView.setBackgroundColor(R.color.marine_blue);
			// 设置menu的宽和高
			popupWindow = new PopupWindow(MainActivity.this);
			popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setContentView(menuView);
			popupWindow.showAtLocation(findViewById(R.id.main_layout),
					Gravity.BOTTOM | Gravity.CENTER, 0, 0);
			popupWindow.setAnimationStyle(R.style.AnimationFade);
			popupWindow.update();
		} else
		{
			popupWindow.showAtLocation(findViewById(R.id.main_layout),
					Gravity.BOTTOM, 0, 0);
		}
		batch_delete_button = (ImageButton) menuView
				.findViewById(R.id.batch_delete);
		batch_delete_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mDbHelper.isAllUnChacked())
				{
					Toast.makeText(MainActivity.this, getResources().getString(R.string.have_no_choice),
							Toast.LENGTH_SHORT).show();
					return;
				}

				builder.setPositiveButton(getResources().getString(R.string.confirm),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						mDbHelper.deleteChecked();
						mDbHelper.cancleALLChecked();
						MySimpleCursorAdapter.visflag = false;
						MyCursorTreeAdapter.visflag = false;
						notes.notifyDataSetChanged();
						adapter.notifyDataSetChanged();
						popupWindow.dismiss();
						Toast.makeText(MainActivity.this, getResources().getString(R.string.delete_succeed),
								Toast.LENGTH_SHORT).show();
					}
				});
				builder.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						return;
					}
				});
				builder.create().show();

			}
		});

		batch_move_button = (ImageButton) menuView
				.findViewById(R.id.batch_move);
		batch_move_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				batch_moveTo_showDialog();
				MySimpleCursorAdapter.visflag = false;
				MyCursorTreeAdapter.visflag = false;
				notes.notifyDataSetChanged();
				adapter.notifyDataSetChanged();
				popupWindow.dismiss();
			}
		});

		batch_lock = (ImageButton) menuView.findViewById(R.id.batch_lock);
		batch_lock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.mDbHelper.lock_chacked_notes();
				Toast.makeText(MainActivity.this, getResources().getString(R.string.lock_succeed), Toast.LENGTH_SHORT)
				.show();
				mDbHelper.cancleALLChecked();
				MySimpleCursorAdapter.visflag = false;
				MyCursorTreeAdapter.visflag = false;
				notes.notifyDataSetChanged();
				adapter.notifyDataSetChanged();
				popupWindow.dismiss();

			}
		});

		batch_unlock = (ImageButton) menuView.findViewById(R.id.batch_unlock);
		batch_unlock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.mDbHelper.unlock_chacked_notes();
				Toast.makeText(MainActivity.this, getResources().getString(R.string.unlock_succeed), Toast.LENGTH_SHORT)
				.show();
				mDbHelper.cancleALLChecked();
				MySimpleCursorAdapter.visflag = false;
				MyCursorTreeAdapter.visflag = false;
				notes.notifyDataSetChanged();
				adapter.notifyDataSetChanged();
				popupWindow.dismiss();

			}
		});

		notes.notifyDataSetChanged();
		adapter.notifyDataSetChanged();
	}

	/**
	 * 批量移动
	 */
	protected void batch_moveTo_showDialog() {
		// TODO Auto-generated method stub
		if (mDbHelper.isAllUnChacked())
		{
			Toast.makeText(MainActivity.this, getResources().getString(R.string.have_no_choice), Toast.LENGTH_SHORT)
			.show();
			return;
		}
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.movetodialog,
				(ViewGroup) findViewById(R.id.moveto_dialog));
		spinner_c = (Spinner) view.findViewById(R.id.moveToCatagory);
		Cursor cursor = mDbHelper.getAllCatagory();
		allCatagory = new ArrayList<String>();
		while (cursor.moveToNext())
		{
			allCatagory.add(cursor.getString(cursor
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME)));
		}
		cursor.close();
		allCatagory_adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, allCatagory);
		allCatagory_adapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_c.setAdapter(allCatagory_adapter);

		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.please_wait)).setView(view)
		.setPositiveButton(getResources().getString(R.string.confirm), new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				final String catagoryNameString = spinner_c
						.getSelectedItem().toString();
				mDbHelper
				.updata_chacked_catagory_notes(catagoryNameString);
				Toast.makeText(MainActivity.this, getResources().getString(R.string.move_succeed),
						Toast.LENGTH_SHORT).show();
				mDbHelper.cancleALLChecked();
				notes.notifyDataSetChanged();
				adapter.notifyDataSetChanged();
			}
		}).setNegativeButton(getResources().getString(R.string.cancel), new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				mDbHelper.cancleALLChecked();
				notes.notifyDataSetChanged();
			}
		}).setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				mDbHelper.cancleALLChecked();
				notes.notifyDataSetChanged();
				return false;
			}
		}).create().show();
	}

	private void actionClickMenuSetting() {
		Intent intent = new Intent(this, NoteSet.class);
		startActivityForResult(intent, 51);
		// overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
	}

	// 点击返回键退出
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(menu.isMenuShowing()) {
				menu.toggle(true);
				return true;
			}
			if (popupWindow != null && popupWindow.isShowing())
			{
				popupWindow.dismiss();
				MySimpleCursorAdapter.visflag = false;
				MyCursorTreeAdapter.visflag = false;
				mDbHelper.cancleALLChecked();
				notes.notifyDataSetInvalidated();
				adapter.notifyDataSetInvalidated();
				return true;
			}

			else if ((System.currentTimeMillis() - exit_time) > 2000)
			{
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.press_again),
						Toast.LENGTH_SHORT).show();
				exit_time = System.currentTimeMillis();
			} else
			{
				this.finish();
			}
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_HOME)
		{
			PackageManager pm = getPackageManager();
			ResolveInfo homeInfo = pm.resolveActivity(new Intent(
					Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);

			ActivityInfo ai = homeInfo.activityInfo;
			Intent startIntent = new Intent(Intent.ACTION_MAIN);
			startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			startIntent
			.setComponent(new ComponentName(ai.packageName, ai.name));
			startActivity(startIntent);
			// overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
		{
			startTime = System.currentTimeMillis();
			windowDispear mThread = new windowDispear();
			mThread.start();
			if (seekBarPopupWindow == null || !seekBarPopupWindow.isShowing())
			{
				lightPopupWindow(MainActivity.this);
				seekBarPopupWindow.showAtLocation(
						MainActivity.this.findViewById(R.id.main_layout),
						Gravity.CENTER_HORIZONTAL, 0, (int) (0 - Height / 4));
				int normal = 0;
				normal = Settings.System.getInt(getContentResolver(),
						Settings.System.SCREEN_BRIGHTNESS ,-1);
				// 进度条绑定当前亮度
				seekBar.setProgress(normal);
				return true;
			} else
			{
				cur_bright = seekBar.getProgress() - 40;
				seekBar.setProgress(cur_bright);
				// 根据当前进度改变亮度
				WindowManager.LayoutParams wl = getWindow().getAttributes();
				float tmpFloat = (float) cur_bright / 255;
				if (tmpFloat > 0 && tmpFloat <= 1)
				{
					wl.screenBrightness = tmpFloat;
				}
				getWindow().setAttributes(wl);
				return true;
			}
		}

		else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
		{
			startTime = System.currentTimeMillis();
			windowDispear mThread = new windowDispear();
			mThread.start();
			if (seekBarPopupWindow == null || !seekBarPopupWindow.isShowing())
			{
				lightPopupWindow(MainActivity.this);
				seekBarPopupWindow.showAtLocation(
						MainActivity.this.findViewById(R.id.main_layout),
						Gravity.CENTER_HORIZONTAL, 0, (int) (0 - Height / 4));
				int normal = 0;
				normal = Settings.System.getInt(getContentResolver(),
						Settings.System.SCREEN_BRIGHTNESS ,-1);
				// 进度条绑定当前亮度
				seekBar.setProgress(normal);
				return true;
			} else
			{

				cur_bright = seekBar.getProgress() + 40;
				seekBar.setProgress(cur_bright);
				// 根据当前进度改变亮度
				WindowManager.LayoutParams wl = getWindow().getAttributes();
				float tmpFloat = (float) cur_bright / 255;
				if (tmpFloat > 0 && tmpFloat <= 1)
				{
					wl.screenBrightness = tmpFloat;
				}
				getWindow().setAttributes(wl);
				return true;
			}
		}
		else if (keyCode == KeyEvent.KEYCODE_MENU) {
			menu.toggle(true);
		}

		return super.onKeyDown(keyCode, event);
	}

	// 第二屏滑动初始化
	protected void initGroupsView() {

		group_names = mDbHelper.getAllCatagory();
		exList = (GroupListView) findViewById(R.id.home_expandableListView);
		exList.setChildDivider(getResources().getDrawable(R.drawable.divider)); 
		exList.setHeaderView(getLayoutInflater().inflate(R.layout.group_header,
				exList, false));
		adapter = new GroupListAdapter(MainActivity.this, exList, group_names);
		exList.setAdapter(adapter);
		// 设置条目点击事件
		exList.setOnChildClickListener(onChildClickListener);
		// 长按条目事件
		exList.setOnCreateContextMenuListener(new childOptionMenu());
		RefreshListener groupListener = new RefreshListener() {
			@Override
			public void onRefresh(RefreshableLinearLayout view) {
				referesh(2);
			}
		};
		groupRefreshableLinearLayout = (RefreshableLinearLayout) findViewById(R.id.groupRefreshLinearLayout);
		groupRefreshableLinearLayout.setRefreshListener(groupListener);
		groupRefreshableLinearLayout.setGroupListView(exList);
	}

	public NoteDbAdapter getDbAdaper() {
		return mDbHelper;
	}

	/**
	 * 初始化listview
	 */
	protected void initListView() {

		mNoteCursor = mDbHelper.getAllNotes();
		notes = new MySimpleCursorAdapter(MainActivity.this, mNoteCursor, "");
		listView.setAdapter(notes);
	}

	/**
	 * 分组下的笔记删除
	 * 
	 * @param info
	 */
	protected void catagory_delete_showDialog(
			final ExpandableListContextMenuInfo info) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.catagory_delete_dialog,
				(ViewGroup) findViewById(R.id.catagory_delete_dialog));
		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.confirm_delete)).setView(view)
		.setPositiveButton(getResources().getString(R.string.confirm), new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				mDbHelper.deleteCatagory(info.id);
				notes.notifyDataSetChanged();// 刷新
				Toast.makeText(MainActivity.this, getResources().getString(R.string.delete_succeed),
						Toast.LENGTH_SHORT).show();
				adapter.notifyDataSetChanged();
			}
		}).setNegativeButton(getResources().getString(R.string.cancel), null).create().show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		mDbHelper.cancleALLChecked();
		mDbHelper.cancleALLExpend();
		/*
		 * if( !weather.isCuccess) { weather.getWeather(); }
		 */
		if (popupWindow != null && popupWindow.isShowing())
		{
			MySimpleCursorAdapter.visflag = false;
			MyCursorTreeAdapter.visflag = false;
			notes.notifyDataSetInvalidated();
			adapter.notifyDataSetInvalidated();
			popupWindow.dismiss();
		}

		notes.notifyDataSetChanged();
		adapter.notifyDataSetChanged();
		super.onRestart();
	}

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

		if (times != 0)
		{
			mDbHelper.cancleALLExpend();
			notes.notifyDataSetChanged();
			adapter.notifyDataSetChanged();
			// initGroupsView();
		}
		mDbHelper.cancleALLChecked();
		times = 1;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		StatService.onPause(this);
		// TODO Auto-generated method stub
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(seekBarPopupWindow != null && seekBarPopupWindow.isShowing()) {
			seekBarPopupWindow.dismiss();
		}
		if (mNoteCursor != null)
		{
			mNoteCursor.close();
		}
		if (group_names != null)
		{
			group_names.close();
		}
		/*
		 * if (mDbHelper != null) { mDbHelper.close(); }
		 */
		if (notes != null)
		{
			notes.closeCursor();
		}
		mDbHelper.cancleALLExpend();
		mLocationClient.stop();
		mDbHelper.close();
		//恢复屏幕亮度
		WindowManager.LayoutParams wl = getWindow().getAttributes();
		wl.screenBrightness = -1;
		getWindow().setAttributes(wl);
		startService(new Intent(MainActivity.this, MyShakeBootService.class));
		super.onDestroy();
	}

	class weatherLoadThread implements Runnable {
		public void run() {
			weather = new Weather(MainActivity.this, handler);

		}
	}
	/*
	 *//**
	 * 判断监听振动服务是否开启
	 * 
	 * @return
	 *//*
	public boolean isWorked() {
		ActivityManager myManager = (ActivityManager) MainActivity.this
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(30);
		for (int i = 0; i < runningService.size(); i++)
		{
			if (runningService.get(i).service.getClassName().toString()
					.equals("com.wxl.donote.MyShakeBootService"))
			{
				return true;
			}
		}
		return false;
	}
	  */
	/**
	 * 分享线程
	 * 
	 * @author hankwing
	 * 
	 */
	class shareLoadThread implements Runnable {
		public void run() {
			Intent it = getIntent();
			Bundle shar_extras = it.getExtras();
			if (shar_extras != null && shar_extras.getBoolean("sync") == true)
			{
				Message msg;
				msg = new Message();
				msg.what = 13;
				handler.sendMessage(msg);
			} else if (shar_extras != null && shar_extras.getString("wxl") != null && shar_extras.getString("wxl").equals("widget")) {

				Message msg;
				msg = new Message();
				msg.what = 21;
				handler.sendMessage(msg);
				Log.i("wxl","success");
			} else if (shar_extras != null && shar_extras.getInt("flag") == 1)// 从闹钟界面进入的
			{
				Message msg;
				msg = new Message();
				msg.what = 0;
				msg.obj = shar_extras.getLong(NoteDbAdapter.KEY_ROWID);
				handler.sendMessage(msg);
			} else if (shar_extras != null && shar_extras.getInt("flag") == 2)
			{
				Message msg;
				msg = new Message();
				msg.what = 10;
				handler.sendMessage(msg);
			} else if (it != null && it.getAction() != null
					&& it.getAction().equals(Intent.ACTION_SEND))
			{
				if (shar_extras.containsKey("android.intent.extra.STREAM"))
				{
					Uri uri = (Uri) shar_extras
							.get("android.intent.extra.STREAM");
					Intent i = new Intent(MainActivity.this, CommonEdit.class);
					String bodyString = getPath(MainActivity.this, uri);
					if (shar_extras.getString(Intent.EXTRA_TEXT) == null) {
						i.putExtra("body", bodyString);
					}
					else {
						i.putExtra("body", bodyString + shar_extras.getString(Intent.EXTRA_TEXT));
					}
					i.putExtra("share", 1);
					Message msg;
					msg = new Message();
					msg.what = 5;
					msg.obj = i;
					handler.sendMessage(msg);
				}
				else {
					Intent i = new Intent(MainActivity.this, CommonEdit.class);
					i.putExtra("body", shar_extras.getString(Intent.EXTRA_TEXT));
					i.putExtra("share", 1);
					Message msg;
					msg = new Message();
					msg.what = 5;
					msg.obj = i;
					handler.sendMessage(msg);
				}
			} else if (it != null && it.getAction() != null
					&& it.getAction().equals(Intent.ACTION_VIEW))
			{
				Intent i = null;
				if (shar_extras.getInt("style") == 0)
				{
					i = new Intent(MainActivity.this, DisplayContent.class);
				} else
				{
					i = new Intent(MainActivity.this, FreeEdit.class);
				}
				i.putExtra(NoteDbAdapter.KEY_ROWID,
						shar_extras.getLong(NoteDbAdapter.KEY_ROWID));
				i.putExtra(NoteDbAdapter.KEY_TITLE,
						shar_extras.getString(NoteDbAdapter.KEY_TITLE));
				i.putExtra(NoteDbAdapter.KEY_BODY,
						shar_extras.getString(NoteDbAdapter.KEY_BODY));
				i.putExtra(NoteDbAdapter.KEY_CATAGORY,
						shar_extras.getString(NoteDbAdapter.KEY_CATAGORY));
				Message msg;
				msg = new Message();
				msg.what = 12;
				msg.obj = i;
				handler.sendMessage(msg);
			}

		}
	}

	/**
	 * 由文件获得URI
	 * 
	 * @param context
	 * @param imageFile
	 * @return
	 */
	public Uri getUri(Context context, File imageFile) {
		String filePath = imageFile.getAbsolutePath();
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media._ID },
				MediaStore.Images.Media.DATA + "=? ",
				new String[] { filePath }, null);
		if (cursor != null)
		{
			cursor.moveToFirst();
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.MediaColumns._ID));
			Uri baseUri = Uri.parse("content://media/external/images/media");
			return Uri.withAppendedPath(baseUri, "" + id);
		}
		return null;
	}

	/**
	 * 由URI获取文件地址
	 * 
	 * @param context
	 * @param myUri
	 * @return
	 */
	public String getPath(Context context, Uri myUri) {
		String myImageUrl = myUri.toString();
		Uri uri = Uri.parse(myImageUrl);
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor actualimagecursor = MainActivity.this.managedQuery(uri, proj,
				null, null, null);
		if (actualimagecursor == null) {
			return "Picture^_^[" + myUri.getPath() + "]^_^ ";
		}
		else {
			int actual_image_column_index = actualimagecursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			String img_path = actualimagecursor
					.getString(actual_image_column_index);
			return "Picture^_^[" + img_path + "]^_^ ";
		}
	}

	// 设置定位相关参数
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

	/**
	 * 调节屏幕亮度窗口
	 * 
	 * @param cx
	 * @return
	 */
	private PopupWindow lightPopupWindow(Context cx) {
		if (seekBarPopupWindow == null)
		{
			seekBarPopupWindow = new PopupWindow(cx);
			View contentView = LayoutInflater.from(this).inflate(
					R.layout.seekbar, null);
			seekBarPopupWindow.setContentView(contentView);
			seekBarPopupWindow.setWidth(LayoutParams.WRAP_CONTENT);
			seekBarPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			seekBar = (SeekBar) contentView.findViewById(R.id.seek);
			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					startTime = System.currentTimeMillis();
					windowDispear mThread = new windowDispear();
					mThread.start();
					// 取得当前进度
					cur_bright = seekBar.getProgress();

					// 当进度小于30时，设置成30，防止太黑看不见的后果。
					if (cur_bright < 60)
					{
						cur_bright = 60;
					}
					WindowManager.LayoutParams wl = getWindow().getAttributes();
					float tmpFloat = (float) cur_bright / 255;
					if (tmpFloat > 0 && tmpFloat <= 1)
					{
						wl.screenBrightness = tmpFloat;
					}
					getWindow().setAttributes(wl);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
				}
			});
			seekBarPopupWindow.setTouchable(true); // 设置PopupWindow可触摸
			seekBarPopupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
		}
		return seekBarPopupWindow;
	}

	/**
	 * 亮度调节窗口消失计时线程
	 * 
	 * @author hankwing
	 * 
	 */
	private class windowDispear extends Thread {
		public void run() {
			try
			{
				Thread.sleep(2500);
				long endTime = System.currentTimeMillis();

				if (seekBarPopupWindow != null
						&& endTime - startTime - 2500 > 0)
				{

					if (seekBarPopupWindow.isShowing())
					{
						Message message = new Message();
						message.what = 11;
						handler.sendMessage(message);
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		};
	}

	/**
	 * 选择笔记类型
	 * 
	 * @param cx
	 * @return
	 */
	private PopupWindow stylePopupWindow(Context cx) {
		if (stylePopupWindow == null)
		{
			stylePopupWindow = new PopupWindow(cx);
			View contentView = LayoutInflater.from(this).inflate(
					R.layout.choosestylewindow, null);
			stylePopupWindow.setContentView(contentView);
			// ColorDrawable dw = new ColorDrawable(-00000);
			// stylePopupWindow.setBackgroundDrawable(dw);
			stylePopupWindow.setWidth(LayoutParams.WRAP_CONTENT);
			stylePopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			normalStyle = (TextView) contentView
					.findViewById(R.id.normal_style);
			normalStyle.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					Intent i = new Intent(MainActivity.this, CommonEdit.class);
					stylePopupWindow.dismiss();
					startActivityForResult(i, ACTIVITY_CREATE);
				}
			});
			freeStyle = (TextView) contentView.findViewById(R.id.define_style);
			freeStyle.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(MainActivity.this, FreeEdit.class);
					stylePopupWindow.dismiss();
					startActivityForResult(i, ACTIVITY_CREATE);
				}
			});
			// 设置PopupWindow外部区域是否可触摸
			stylePopupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
			stylePopupWindow.setTouchable(true); // 设置PopupWindow可触摸
			stylePopupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
			return stylePopupWindow;
		} else
		{
			return stylePopupWindow;
		}
	}

	/**
	 * 同步
	 * 
	 * @param isGroupListView
	 *            1 listView 2 groupListView 3 slide_menu
	 */
	protected void referesh(final int isGroupListView) {
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {
				try
				{
					ArrayList<Map<String, Object>> jsonMap = new ArrayList<Map<String, Object>>();
					mNoteCursor = mDbHelper.getAllNotes();
					mNoteCursor.moveToFirst();
					while (!mNoteCursor.isAfterLast())
					{
						HashMap<String, Object> tempMap = new HashMap<String, Object>();
						tempMap.put(
								"_id",
								mNoteCursor.getInt(mNoteCursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID)));
						tempMap.put(
								"title",
								mNoteCursor.getString(mNoteCursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
						tempMap.put("body", mNoteCursor.getString(mNoteCursor
								.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
						tempMap.put(
								"catagory",
								mNoteCursor.getString(mNoteCursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_CATAGORY)));
						tempMap.put(
								"created",
								mNoteCursor.getString(mNoteCursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_CREATED)));
						tempMap.put(
								"modify",
								mNoteCursor.getString(mNoteCursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_MODIFY)));
						tempMap.put(
								"content",
								mNoteCursor.getString(mNoteCursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_CONTENT)));
						tempMap.put(
								"alarmflag",
								mNoteCursor.getInt(mNoteCursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALARMFLAG)));
						tempMap.put(
								"ischecked",
								mNoteCursor.getInt(mNoteCursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_ISCHECKED)));
						tempMap.put("isclocked", mNoteCursor.getInt(mNoteCursor
								.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK)));
						tempMap.put(
								"isexpend",
								mNoteCursor.getInt(mNoteCursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_ISEXPEND)));
						tempMap.put(
								"style",
								mNoteCursor.getInt(mNoteCursor
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE)));
						mNoteCursor.moveToNext();
						jsonMap.add(tempMap);
					}

					group_names = mDbHelper.getAllCatagory();
					group_names.moveToFirst();
					while (!group_names.isAfterLast())
					{
						HashMap<String, Object> tempMap = new HashMap<String, Object>();
						tempMap.put(
								"_id",
								group_names.getInt(group_names
										.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID)));
						tempMap.put("name", group_names.getString(group_names
								.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME)));
						jsonMap.add(tempMap);
						group_names.moveToNext();
					}
					group_names.moveToFirst();
					mNoteCursor.moveToFirst();
					// account = getSharedPreferences("account", 0);
					httpResponse = HttpUtil.makeRequest(sync_url, jsonMap,
							account.getString("account_name", null));
					if (httpResponse == null)
					{
						Toast.makeText(MainActivity.this, getResources().getString(R.string.interner_error),
								Toast.LENGTH_SHORT).show();
						return null;
					}
					entity = httpResponse.getEntity();
					syncResultString = EntityUtils.toString(entity);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (isGroupListView == 2)
				{
					groupRefreshableLinearLayout.finishRefresh();
				} else if (isGroupListView == 1)
				{
					refreshableLinearLayout.finishRefresh();
				} else
				{
					proBar.setVisibility(View.GONE);
					slide_menu_sync.setVisibility(View.VISIBLE);
					menu.toggle(true);
				}
				if (httpResponse == null)
				{
					Toast.makeText(MainActivity.this, getResources().getString(R.string.interner_error),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK
						&& entity != null)
				{
					if (syncResultString.equals("OK"))
					{
						Toast.makeText(MainActivity.this, getResources().getString(R.string.sync_success),
								Toast.LENGTH_SHORT).show();
					} else
					{
						Toast.makeText(MainActivity.this, getResources().getString(R.string.sync_fail_login),
								Toast.LENGTH_SHORT).show();
					}
				} else
				{
					Toast.makeText(MainActivity.this, getResources().getString(R.string.sync_fail_internet),
							Toast.LENGTH_SHORT).show();
				}
			}
		}.execute();
	}

	/**
	 * 初始化滑动菜单
	 */
	private void initSlidingMenu() {

		// 设置滑动菜单的属性值
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// 设置滑动菜单的视图界面
		menu.setMenu(R.layout.slide_menu);
		account_name = (TextView) findViewById(R.id.account_name);
		upload_bar = (ProgressBar) findViewById(R.id.slide_menu_upload_pro);
		if ( account.getString("account_name", " ").startsWith("sina")) {
			account_name.setText(getResources().getString(R.string.login_with_sina));
		}
		else if( account.getString("account_name", " ").length() > 20) {

			account_name.setText(getResources().getString(R.string.qq_account_to_login));
		}
		else {
			account_name.setText(account.getString("account_name", " "));
		}
		proBar = (ProgressBar) findViewById(R.id.slide_menu_pro);
		download_bar = (ProgressBar) findViewById(R.id.slide_menu_download_pro);
		slide_menu_sync = (ImageButton) findViewById(R.id.slide_menu_sync);
		slide_menu_set = (ImageButton) findViewById(R.id.slide_menu_set);
		slide_menu_batch = (TextView) findViewById(R.id.slide_menu_batch);
		slide_menu_logout = (TextView) findViewById(R.id.slide_menu_logout);
		slide_menu_sync.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				proBar.setVisibility(View.VISIBLE);
				slide_menu_sync.setVisibility(View.GONE);
				referesh(3);

			}
		});

		slide_menu_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				actionClickMenuSetting();

			}
		});

		slide_menu_batch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				menu.toggle(true);
				actionClickMenuBatch();
			}
		});

		slide_menu_logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				account.edit().clear().commit();
				BaiduAccessTokenKeeper.clear(MainActivity.this);
				Intent intent = new Intent(MainActivity.this, Login.class);
				startActivity(intent);
				finish();
			}
		});
		slide_menu_tieBaidu = (TextView) findViewById(R.id.slide_menu_tieBaidu);
		slide_menu_tieBaidu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				baiduUtil.login();
			}
		});
		slide_menu_baidu_username = (TextView) findViewById(R.id.slide_menu_baidu_username);
		if (BaiduApiUtil.access_token.getUserName() != "")
		{
			slide_menu_baidu_username.setText(BaiduApiUtil.access_token
					.getUserName());
			slide_menu_baidu_username.setVisibility(View.VISIBLE);
			slide_menu_tieBaidu.setText(getResources().getString(R.string.baidu_cloud_has_binding));
			slide_menu_tieBaidu.setClickable(false);
		}

		slide_baidu_space = (TextView) findViewById(R.id.slide_menu_baidu_space);
		baiduUtil.getQuota();
		slide_baidu_upload = (TextView) findViewById(R.id.slide_menu_baidu_upload);
		slide_baidu_upload.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				slide_baidu_upload.setClickable(false);
				upload_bar.setVisibility(View.VISIBLE);
				mNoteCursor = mDbHelper.getAllNotes();
				baiduUtil.sync(mNoteCursor ,handler);
			}
		});

		slide_baidu_download = (TextView) findViewById(R.id.slide_menu_baidu_download);
		slide_baidu_download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				slide_baidu_download.setClickable(false);
				download_bar.setVisibility(View.VISIBLE);
				mNoteCursor = mDbHelper.getAllNotes();
				baiduUtil.downLoad(mNoteCursor ,handler);
			}
		});

		if( BaiduApiUtil.mbOauth == "") {
			slide_baidu_download.setClickable(false);
			slide_baidu_upload.setClickable(false);
			slide_baidu_space.setTextColor(getResources().getColor(R.color.light_white));
			slide_baidu_space.setCompoundDrawablesWithIntrinsicBounds(getResources().
					getDrawable(R.drawable.slide_menu_used_n), null, null, null);
			slide_baidu_download.setTextColor(getResources().getColor(R.color.light_white));
			slide_baidu_download.setCompoundDrawablesWithIntrinsicBounds(getResources().
					getDrawable(R.drawable.slide_menu_download_n), null, null, null);
			slide_baidu_upload.setTextColor(getResources().getColor(R.color.light_white));
			slide_baidu_upload.setCompoundDrawablesWithIntrinsicBounds(getResources().
					getDrawable(R.drawable.slide_menu_upload_n), null, null, null);
		}
	}
}
