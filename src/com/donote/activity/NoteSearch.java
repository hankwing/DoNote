package com.donote.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.baidu.mobstat.StatService;
import com.donote.activity.MainActivity;
import com.donote.adapter.MyCursorTreeAdapter;
import com.donote.adapter.MySimpleCursorAdapter;
import com.donote.adapter.NoteDbAdapter;
import com.donote.adapter.MySimpleCursorAdapter.ViewHolder;
import com.donote.alarm.AlarmReceiver;
import com.donote.imagehandler.ImageMemoryCache;
import com.wxl.donote.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class NoteSearch extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		StatService.onPause(this);
		super.onPause();
	}

	private static final int ACTIVITY_RESEARCH = 1;
	private EditText condition;
	private AlertDialog.Builder builder;
	private ImageButton confirm;
	private ImageButton returnButton;
	private TextView textView;
	private MySimpleCursorAdapter notes;
	private Cursor mNoteCursor;
	private String searchString;
	private View mainLayout;
	private ListView listView;
	private int Times = 0;
	private AlarmManager alarm_service;
	private PopupWindow popupWindow;
	private View menuView;// 批量弹出菜单
	private ImageButton batch_delete_button;
	private ImageButton batch_move_button;
	private ImageButton batch_lock;
	private ImageButton batch_unlock;
	private ArrayAdapter<String> allCatagory_adapter;
	private Spinner spinner_c;
	private List<String> allCatagory;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);
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
		if (MainActivity.mDbHelper == null || MainActivity.mDbHelper.isOpen())
		{
			MainActivity.mDbHelper = new NoteDbAdapter(this);
			MainActivity.mDbHelper.open();
		}
		mainLayout = findViewById(R.id.main_search);
		Bitmap temp = ImageMemoryCache.getBitmap((long) -1, "beijing");
		if (temp != null)
		{
			Drawable beijing = new BitmapDrawable(getResources(), temp);
			mainLayout.setBackgroundDrawable(beijing);
			mainLayout.getBackground().setAlpha(80);
		}
		MySimpleCursorAdapter.visflag = false;
		searchString = "No exit String Found!!";
		mNoteCursor = MainActivity.mDbHelper.find(searchString);
		startManagingCursor(mNoteCursor);
		notes = new MySimpleCursorAdapter(this, mNoteCursor, "");
		notes.notifyDataSetChanged();
		textView = (TextView) this.findViewById(R.id.search_empty);
		listView = (ListView) this.findViewById(R.id.search_list);
		listView.setEmptyView(findViewById(R.id.search_empty));
		listView.setOnItemClickListener(new ItemClickListener());
		condition = (EditText) this.findViewById(R.id.search_note);
		confirm = (ImageButton) this.findViewById(R.id.search_confirm);
		returnButton = (ImageButton) this.findViewById(R.id.search_return);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (doSearch(condition.getText().toString()) == 0)
				{
					textView.setText(getResources().getString(R.string.has_no_result));
				}
			}
		});
		returnButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NoteSearch.this, MainActivity.class);
				startActivity(intent);
			}
		});

		builder = new Builder(NoteSearch.this);
		builder.setMessage(getResources().getString(R.string.confirm_delete));
		builder.setTitle(getResources().getString(R.string.tip));
	}

	@SuppressWarnings("deprecation")
	public int doSearch(String condition) {
		// TODO Auto-generated method stub
		searchString = condition;
		alarm_service = (AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);
		mNoteCursor = MainActivity.mDbHelper.find(condition);
		startManagingCursor(mNoteCursor);
		notes = new MySimpleCursorAdapter(this, mNoteCursor, condition);
		listView.setAdapter(notes);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				actionClickMenuBatch();
				return false;
			}
		});
		return mNoteCursor.getCount();
	}

	private final class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (MySimpleCursorAdapter.visflag)
			{
				ViewHolder vh = (ViewHolder) view.getTag();
				vh.cb.performClick();
				return;
			} else
			{
				Cursor c = mNoteCursor;
				c.moveToPosition(position);

				int style = c.getInt(c
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE));
				if (style == 0)
				{

					Intent i = new Intent(NoteSearch.this, DisplayContent.class);
					i.putExtra(NoteDbAdapter.KEY_ROWID, id);
					i.putExtra(NoteDbAdapter.KEY_TITLE, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
					i.putExtra(NoteDbAdapter.KEY_BODY, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
					i.putExtra(NoteDbAdapter.KEY_CATAGORY, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_CATAGORY)));
					startActivityForResult(i, ACTIVITY_RESEARCH);

				} else if (style == 1)
				{
					Intent i = new Intent(NoteSearch.this, FreeEdit.class);
					i.putExtra(NoteDbAdapter.KEY_ROWID, id);
					i.putExtra(NoteDbAdapter.KEY_TITLE, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
					i.putExtra(NoteDbAdapter.KEY_BODY, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
					i.putExtra(NoteDbAdapter.KEY_CATAGORY, c.getString(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_CATAGORY)));
					startActivityForResult(i, ACTIVITY_RESEARCH);
				}

			}

		}
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
				Cursor cursor = MainActivity.mDbHelper.getnote(extras
						.getLong("ID"));
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
					Toast.makeText(NoteSearch.this,getResources().getString(R.string.time_passed),
							Toast.LENGTH_SHORT).show();
					return;
				}
				long alarmIdLong = MainActivity.mDbHelper.createAlarm(calendar,
						extras.getLong("ID"), getResources().getString(R.string.whole_note));
				intent.putExtra("alarmID", alarmIdLong);
				PendingIntent p_intent = PendingIntent.getBroadcast(this,
						(int) alarmIdLong, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				MainActivity.mDbHelper.updateAlarmflag(extras.getLong("ID"));
				notes.notifyDataSetChanged();
				// Schedule the alarm!
				alarm_service.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), p_intent);
				Toast.makeText(this, getResources().getString(R.string.new_alarm_has_set), Toast.LENGTH_LONG)
						.show();
				cursor.close();
			}// If
		}
	}

	/*
	 * private class OptionMenu implements OnCreateContextMenuListener {
	 * 
	 * @Override public void onCreateContextMenu(ContextMenu arg0, View arg1,
	 * ContextMenuInfo arg2) { arg0.add(0, R.id.menu_delete, 0, "删除");
	 * arg0.add(0, R.id.menu_move, 0, "移动到"); arg0.add(0, R.id.menu_share, 0,
	 * "分享"); arg0.add(0, R.id.menu_lock, 0, "锁定或解锁笔记"); arg0.add(0,
	 * R.id.menu_alarm_cancle, 0, "提醒管理"); arg0.add(0, R.id.menu_send, 0,
	 * "发送到桌面"); } }
	 * 
	 * @Override public boolean onMenuItemSelected(int featureId, MenuItem item)
	 * { // TODO Auto-generated method stub switch (item.getItemId()) { case
	 * R.id.menu_delete:// 删除笔记 AdapterContextMenuInfo info =
	 * (AdapterContextMenuInfo) item .getMenuInfo(); deleteShowDialog(info.id);
	 * return true; case R.id.menu_move: AdapterContextMenuInfo moveToinfo =
	 * (AdapterContextMenuInfo) item .getMenuInfo();
	 * moveTo_showDialog(moveToinfo.id); return true; case R.id.menu_share:
	 * AdapterContextMenuInfo share_info = (AdapterContextMenuInfo) item
	 * .getMenuInfo(); Cursor cursor =
	 * MainActivity.mDbHelper.getnote(share_info.id); cursor.moveToFirst();
	 * Intent intent = new Intent(Intent.ACTION_SEND);
	 * intent.setType("text/plain"); intent.putExtra(Intent.EXTRA_SUBJECT,
	 * "分享"); intent.putExtra( Intent.EXTRA_TEXT, "我想把这条笔记分享给你 \n标题 :" +
	 * cursor.getString(cursor .getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE))
	 * + "\n内容 :" + cursor.getString(cursor
	 * .getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY))); cursor.close();
	 * startActivity(Intent.createChooser(intent, getTitle())); return true;
	 * case R.id.menu_lock: AdapterContextMenuInfo note_info =
	 * (AdapterContextMenuInfo) item .getMenuInfo(); Cursor temp =
	 * MainActivity.mDbHelper.getnote(note_info.id); if
	 * (temp.getInt(temp.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK)) == 0) {
	 * MainActivity.mDbHelper.lockNote(note_info.id);
	 * Toast.makeText(NoteSearch.this, "锁定成功", Toast.LENGTH_SHORT).show(); }
	 * else { MainActivity.mDbHelper.unLockNote(note_info.id);
	 * Toast.makeText(NoteSearch.this, "解锁成功", Toast.LENGTH_SHORT).show(); }
	 * temp.close(); notes.notifyDataSetChanged(); return true; case
	 * R.id.menu_alarm_cancle: AdapterContextMenuInfo alarmInfo =
	 * (AdapterContextMenuInfo) item .getMenuInfo(); if
	 * (MainActivity.mDbHelper.findAlarmByID(alarmInfo.id).getCount() > 0) {
	 * Intent i = new Intent(NoteSearch.this, AlarmSet.class);
	 * i.putExtra("NoteID", alarmInfo.id); startActivity(i); } else {
	 * Toast.makeText(NoteSearch.this, "该条笔记无设置闹钟", Toast.LENGTH_SHORT) .show();
	 * }
	 * 
	 * case R.id.menu_send:
	 * 
	 * AdapterContextMenuInfo noteInfo = (AdapterContextMenuInfo) item
	 * .getMenuInfo(); sendToDesktop(noteInfo.id); return true; } return
	 * super.onMenuItemSelected(featureId, item); }
	 */

	protected void moveTo_showDialog(final Long id) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.movetodialog,
				(ViewGroup) findViewById(R.id.moveto_dialog));
		spinner_c = (Spinner) view.findViewById(R.id.moveToCatagory);
		Cursor cursor = MainActivity.mDbHelper.getAllCatagory();
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
		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.choose)).setView(view)
				.setPositiveButton(getResources().getString(R.string.confirm), new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						final String catagoryNameString = spinner_c
								.getSelectedItem().toString();
						MainActivity.mDbHelper.updata_catagory_single_notes(id,
								catagoryNameString);
						Toast.makeText(NoteSearch.this, getResources().getString(R.string.move_succeed),
								Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton(getResources().getString(R.string.cancel), null).create().show();
	}

	protected void batch_moveTo_showDialog() {
		// TODO Auto-generated method stub
		if (MainActivity.mDbHelper.isAllUnChacked())
		{
			Toast.makeText(NoteSearch.this, getResources().getString(R.string.have_no_choice), Toast.LENGTH_SHORT)
					.show();
			return;
		}
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.movetodialog,
				(ViewGroup) findViewById(R.id.moveto_dialog));
		spinner_c = (Spinner) view.findViewById(R.id.moveToCatagory);
		Cursor cursor = MainActivity.mDbHelper.getAllCatagory();
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

		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.choose)).setView(view)
				.setPositiveButton(getResources().getString(R.string.confirm), new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						final String catagoryNameString = spinner_c
								.getSelectedItem().toString();
						MainActivity.mDbHelper
								.updata_chacked_catagory_notes(catagoryNameString);
						MainActivity.mDbHelper.cancleALLChecked();
						Toast.makeText(NoteSearch.this, getResources().getString(R.string.move_succeed),
								Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton(getResources().getString(R.string.cancel), new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						MainActivity.mDbHelper.cancleALLChecked();
					}
				}).setOnKeyListener(new OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						// TODO Auto-generated method stub
						MainActivity.mDbHelper.cancleALLChecked();
						return false;
					}
				}).create().show();
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
		if (Times != 0)
		{
			notes.notifyDataSetChanged();
		}
		Times = 1;
		MainActivity.mDbHelper.cancleALLChecked();
	}

	@SuppressLint("ResourceAsColor")
	private void actionClickMenuBatch() {
		// TODO Auto-generated method stub {
		MySimpleCursorAdapter.visflag = true;
		if (popupWindow == null)
		{
			// TODO Auto-generated method stub
			menuView = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.main_batch_menu, null);
			menuView.setBackgroundColor(R.color.blue);
			// 设置menu的宽和高
			popupWindow = new PopupWindow(findViewById(R.id.main_search), 320,
					50);
			popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
			popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
			popupWindow.setContentView(menuView);
			popupWindow.showAtLocation(findViewById(R.id.main_search),
					Gravity.BOTTOM | Gravity.CENTER, 0, 0);
			popupWindow.setAnimationStyle(R.style.AnimationFade);
			popupWindow.update();
		} else
		{
			popupWindow.showAtLocation(findViewById(R.id.main_search),
					Gravity.BOTTOM, 0, 0);
		}
		batch_delete_button = (ImageButton) menuView
				.findViewById(R.id.batch_delete);
		batch_delete_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (MainActivity.mDbHelper.isAllUnChacked())
				{
					Toast.makeText(NoteSearch.this, getResources().getString(R.string.have_no_choice),
							Toast.LENGTH_SHORT).show();
					return;
				}
				MainActivity.mDbHelper.deleteChecked();
				MainActivity.mDbHelper.cancleALLChecked();
				MySimpleCursorAdapter.visflag = false;
				notes.notifyDataSetChanged();
				popupWindow.dismiss();
				Toast.makeText(NoteSearch.this, getResources().getString(R.string.delete_succeed), Toast.LENGTH_SHORT)
						.show();
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
				notes.notifyDataSetChanged();
				popupWindow.dismiss();
			}
		});

		batch_lock = (ImageButton) menuView.findViewById(R.id.batch_lock);
		batch_lock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.mDbHelper.lock_chacked_notes();
				Toast.makeText(NoteSearch.this, getResources().getString(R.string.lock_succeed), Toast.LENGTH_SHORT)
						.show();
				MainActivity.mDbHelper.cancleALLChecked();
				MySimpleCursorAdapter.visflag = false;
				MyCursorTreeAdapter.visflag = false;
				notes.notifyDataSetChanged();
				popupWindow.dismiss();

			}
		});

		batch_unlock = (ImageButton) menuView.findViewById(R.id.batch_unlock);
		batch_unlock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.mDbHelper.unlock_chacked_notes();
				Toast.makeText(NoteSearch.this, getResources().getString(R.string.unlock_succeed), Toast.LENGTH_SHORT)
						.show();
				MainActivity.mDbHelper.cancleALLChecked();
				MySimpleCursorAdapter.visflag = false;
				MyCursorTreeAdapter.visflag = false;
				notes.notifyDataSetChanged();
				popupWindow.dismiss();

			}
		});

		notes.notifyDataSetChanged();
	}

	/*
	 * private void actionClickMenuSetting() { Intent intent = new Intent(this,
	 * NoteSet.class); startActivity(intent); }
	 */

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (popupWindow != null && popupWindow.isShowing())
			{
				popupWindow.dismiss();
				MySimpleCursorAdapter.visflag = false;
				MainActivity.mDbHelper.cancleALLChecked();
				notes.notifyDataSetChanged();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * private void deleteShowDialog(final long id) { // TODO Auto-generated
	 * method stub Cursor temp = MainActivity.mDbHelper.getnote(id);
	 * if(temp.getInt(temp.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK))== 0) {
	 * builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * MainActivity.mDbHelper.deleteNote(id); notes.notifyDataSetChanged();
	 * Toast.makeText(NoteSearch.this, "删除成功", Toast.LENGTH_SHORT).show(); } });
	 * builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * return; } }); builder.create().show(); } else {
	 * Toast.makeText(NoteSearch.this, "该条笔记被锁定", Toast.LENGTH_SHORT).show(); }
	 * temp.close();
	 * 
	 * }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		notes.closeCursor();
		super.onDestroy();
	}

	/*
	 * private void sendToDesktop(long id) { Cursor temp =
	 * MainActivity.mDbHelper.getnote(id); Intent sender = new Intent(); Intent
	 * shortcutIntent = new Intent(this, MainActivity.class);
	 * shortcutIntent.putExtra(NoteDbAdapter.KEY_ROWID, id);
	 * shortcutIntent.putExtra(NoteDbAdapter.KEY_TITLE, temp.getString(temp
	 * .getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
	 * shortcutIntent.putExtra(NoteDbAdapter.KEY_BODY, temp.getString(temp
	 * .getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
	 * shortcutIntent.putExtra(NoteDbAdapter.KEY_CATAGORY, temp.getString(temp
	 * .getColumnIndexOrThrow(NoteDbAdapter.KEY_CATAGORY)));
	 * shortcutIntent.putExtra("style",
	 * temp.getInt(temp.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE)));
	 * shortcutIntent.setAction(Intent.ACTION_VIEW);
	 * sender.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
	 * sender.putExtra(Intent.EXTRA_SHORTCUT_NAME, temp.getString(temp
	 * .getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE))); Bitmap bitmap =
	 * ImageMemoryCache.getBitmap(id, null); if(bitmap != null) { Bitmap
	 * scaledBitmap = Bitmap.createScaledBitmap(bitmap, 72, 72, true);
	 * sender.putExtra(Intent.EXTRA_SHORTCUT_ICON, scaledBitmap); } else {
	 * sender.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
	 * Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher)); }
	 * sender.putExtra("duplicate", true);
	 * sender.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
	 * sendBroadcast(sender); }
	 */

}
