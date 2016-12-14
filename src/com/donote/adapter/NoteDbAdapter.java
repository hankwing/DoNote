package com.donote.adapter;

import java.util.Calendar;
import com.donote.activity.MainActivity;
import com.donote.alarm.AlarmReceiver;
import com.donote.widget.DoNoteWidgetProvider;
import com.wxl.donote.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.RemoteViews;

public class NoteDbAdapter {

	public static final String KEY_TITLE = "title";
	public static final String KEY_NAME = "name";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CATAGORY = "catagory";
	public static final String KEY_CREATED = "created";
	public static final String KEY_ALARMFLAG = "alarmflag";
	public static final String KEY_ISEXPEND = "isexpend";
	public static final String KEY_ISCHECKED = "ischecked";
	public static final String KEY_MODIFY = "modify";
	public static final String KEY_STYLE = "style";
	public static final String KEY_CONTENT = "content";
	public static final String KEY_LOCK = "isclocked";
	public static final String KEY_ALARMID = "noteid";// 闹钟附属于笔记的ID号
	public static final String KEY_ALAMCATA = "alarmcatagory";// 闹钟类型
	public static final String KEY_ALAMTIME = "alarmtime";// 闹钟类型
	private static String sortWay;
	private AlarmManager alarm_service;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final static String DATABASE_CREATE = "create table note (_id integer primary key autoincrement, "
			+ "title text not null, body text not null, catagory text not null, created text not null,content text not null,"
			+ " modify text not null , alarmflag INTEGER, ischecked INTEGER , isclocked INTEGER , isexpend INTEGER ,style INTEGER);";
	private final static String DATABASE_CREATE_CATAGORY = "create table catagory (_id integer primary key autoincrement, "
			+ "name text not null);";
	private final static String DATABASE_CREATE_ALARM = "create table alarm (_id integer primary key autoincrement, "
			+ "noteid integer not null, alarmcatagory text not null, alarmtime text not null ,ischecked INTEGER);";
	private static final String DATABASE_NAME = "database";
	private static final String DATABASE_TABLE = "note";
	private static final String DATABASE_CATAGORY = "catagory";
	private static final String DATABASE_ALARM = "alarm";
	private static final int DATABASE_VERSION = 3; // 数据库升级,2.0版本为3
	private static Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE_CATAGORY);
			db.execSQL(DATABASE_CREATE_ALARM);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// 更新到1.3版本，更新数据表，添加闹钟表
			if (oldVersion == 1)
			{
				db.execSQL("DROP TABLE IF EXISTS alarm");
				db.execSQL(DATABASE_CREATE_ALARM);
				db.execSQL("ALTER TABLE note ADD COLUMN isclocked INTEGER");
				db.execSQL("ALTER TABLE note ADD COLUMN isexpend INTEGER default(0)");
				db.execSQL("ALTER TABLE note ADD COLUMN style INTEGER default(0)");
			}

			if (oldVersion == 2)// 升级到2.0版本，增加笔记的锁定标记
			{
				db.execSQL("ALTER TABLE note ADD COLUMN isclocked INTEGER default(0)");
				db.execSQL("ALTER TABLE note ADD COLUMN isexpend INTEGER default(0)");
				db.execSQL("ALTER TABLE note ADD COLUMN style INTEGER default(0)");
			}

		}
	}

	public NoteDbAdapter(Context ctx) {
		NoteDbAdapter.mCtx = ctx;
		alarm_service = (AlarmManager) mCtx
				.getSystemService(android.content.Context.ALARM_SERVICE);
		SharedPreferences settings = mCtx.getSharedPreferences("sortWay", 0);
		if (settings.getString("sort", "").equals(""))
		{
			sortWay = KEY_CREATED + " desc";
		} else if (settings.getString("sort", "").equals("c"))
		{
			sortWay = KEY_CREATED + " desc";
		} else
		{
			sortWay = KEY_MODIFY + " desc";
		}
	}

	public static void changeSortWay() {
		SharedPreferences settings = mCtx.getSharedPreferences("sortWay", 0);
		if (settings.getString("sort", "").equals(""))
		{
			sortWay = KEY_CREATED + " desc";
		} else if (settings.getString("sort", "").equals("c"))
		{
			sortWay = KEY_CREATED + " desc";
		} else
		{
			sortWay = KEY_MODIFY + " desc";
		}
	}

	public int getCount() {
		Cursor cursor = getAllNotes();
		return cursor.getCount();
	}

	public int getAlarmCount() {
		Cursor cursor = getAllAlarm();
		return cursor.getCount();
	}

	public Cursor getAllAlarm() {

		return mDb.query(DATABASE_ALARM, new String[] { KEY_ROWID, KEY_ALARMID,
				KEY_ALAMCATA, KEY_ALAMTIME, KEY_ISCHECKED }, null, null, null,
				null, null);

	}

	public Cursor findAlarmByID(long ID) {

		Cursor cursor = mDb.query(DATABASE_ALARM, new String[] { KEY_ROWID,
				KEY_ALARMID, KEY_ALAMCATA, KEY_ALAMTIME, KEY_ISCHECKED },
				KEY_ALARMID + "=" + ID, null, null, null, null);
		if (cursor != null)
		{
			cursor.moveToFirst();
		}
		return cursor;

	}

	public Cursor findAlarmByAlarmID(long ID) {

		Cursor cursor = mDb.query(DATABASE_ALARM, new String[] { KEY_ROWID,
				KEY_ALARMID, KEY_ALAMCATA, KEY_ALAMTIME, KEY_ISCHECKED },
				KEY_ROWID + "=" + ID, null, null, null, null);
		if (cursor != null)
		{
			cursor.moveToFirst();
		}
		return cursor;

	}

	public Cursor findAlarmByAlarmList(Long[] ID) {

		Cursor cursor = mDb.query(DATABASE_ALARM, new String[] { KEY_ROWID,
				KEY_ALARMID, KEY_ALAMCATA, KEY_ALAMTIME, KEY_ISCHECKED },
				KEY_ROWID + ">" + ID, null, null, null, null);
		if (cursor != null)
		{
			cursor.moveToFirst();
		}
		return cursor;

	}

	public NoteDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {

		mDbHelper.close();
		mDb.close();
	}

	public void executeSql( String sql) {
		mDb.execSQL(sql);
	}


	public boolean isOpen() {
		if (mDb.isOpen())
		{
			return true;
		} else
		{
			return false;
		}

	}

	public Cursor find(String condition) {

		Cursor cursor = mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_BODY, KEY_CATAGORY, KEY_CREATED, KEY_CONTENT,
				KEY_ALARMFLAG, KEY_ISCHECKED, KEY_MODIFY ,KEY_LOCK ,KEY_ISEXPEND ,KEY_STYLE}, KEY_TITLE
				+ " like ? " + "or content like ?", new String[] {
				"%" + condition + "%", "%" + condition + "%" }, null, null,
				sortWay);
		/*
		 * mDb.query(DATABASE_TABLE, new String[] { KEY_TITLE,KEY_BODY ,},
		 * KEY_ROWID + "=" + , selectionArgs, groupBy, having, orderBy);
		 */

		/*
		 * mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE,
		 * KEY_BODY, KEY_CATAGORY, KEY_CREATED, KEY_ALARMFLAG, KEY_ISCHECKED },
		 * null, null, null, null, KEY_CREATED);
		 */

		/*
		 * Cursor cursor = mDb.rawQuery("select * from note where title like ? "
		 * + "or body like ?", new String[] { "%" + condition + "%", "%" +
		 * condition + "%" });
		 */
		if (cursor != null)
		{
			cursor.moveToFirst();
		}
		return cursor;
	}

	public void updata_catagory_notes(String oldString, String newString) {
		String[] args = { oldString };
		ContentValues values = new ContentValues();
		values.put(DATABASE_CATAGORY, newString);
		mDb.update(DATABASE_TABLE, values, KEY_CATAGORY + "=?", args);
	}

	public void updata_catagory_single_notes(Long id, String newString) {
		ContentValues values = new ContentValues();
		values.put(DATABASE_CATAGORY, newString);
		mDb.update(DATABASE_TABLE, values, KEY_ROWID + "=" + id, null);
	}

	public void updata_chacked_catagory_notes(String newString) {
		ContentValues values = new ContentValues();
		values.put(DATABASE_CATAGORY, newString);
		mDb.update(DATABASE_TABLE, values, KEY_ISCHECKED + "=" + 1, null);
	}

	public void lock_chacked_notes() {
		ContentValues values = new ContentValues();
		values.put(KEY_LOCK, 1);
		mDb.update(DATABASE_TABLE, values, KEY_ISCHECKED + "=" + 1, null);
	}

	public void unlock_chacked_notes() {
		ContentValues values = new ContentValues();
		values.put(KEY_LOCK, 0);
		mDb.update(DATABASE_TABLE, values, KEY_ISCHECKED + "=" + 1, null);
	}

	public Cursor findChilds(String condition) {

		Cursor cursor = mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_BODY, KEY_CATAGORY, KEY_CREATED, KEY_CONTENT,
				KEY_ALARMFLAG, KEY_ISCHECKED, KEY_MODIFY ,KEY_LOCK ,KEY_ISEXPEND,KEY_STYLE}, KEY_CATAGORY
				+ " like ? ", new String[] { "%" + condition + "%" }, null,
				null, sortWay);
		if (cursor != null)
		{
			cursor.moveToFirst();
		}
		return cursor;

	}

	public boolean findAlarmByContent(long ID, String condition,
			Calendar calendar) {

		Cursor cursor = null;
		int year = calendar.get(Calendar.YEAR) % 100;
		String month, day, hour, minute;
		month = calendar.get(Calendar.MONTH) < 9 ? "0"
				+ (calendar.get(Calendar.MONTH) + 1) : String.valueOf(calendar
						.get(Calendar.MONTH) + 1);
				day = calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0"
						+ calendar.get(Calendar.DAY_OF_MONTH) : String.valueOf(calendar
								.get(Calendar.DAY_OF_MONTH));
						hour = calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0"
								+ calendar.get(Calendar.HOUR_OF_DAY) : String.valueOf(calendar
										.get(Calendar.HOUR_OF_DAY));
								minute = calendar.get(Calendar.MINUTE) < 10 ? "0"
										+ calendar.get(Calendar.MINUTE) : String.valueOf(calendar
												.get(Calendar.MINUTE));
										// Integer day = calendar.get(calendar.get(Calendar.DAY_OF_MONTH));
										String created = year + "/" + month + "/" + day + " " + hour + ":"
												+ minute;
										cursor = mDb.query(DATABASE_ALARM, new String[] { KEY_ROWID,
												KEY_ALARMID, KEY_ALAMCATA, KEY_ALAMTIME, KEY_ISCHECKED },
												KEY_ALAMCATA + "=" + "'" + condition + "'"
														+ " and alarmtime = " + "'" + created + "'" + " and "
														+ KEY_ALARMID + " = " + ID, null, null, null, null);
										if (cursor.getCount() != 0)
										{
											return true;
										}
										return false;
	}

	public long createNote(String title, String body, int style,String catagory) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
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
				"(Gesture\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)", "[手写]");
		bodyString = bodyString.replaceAll(
				"(Cloud\\^\\_\\^\\[(.*?)\\]\\[(.*?)\\]\\^\\_\\^)", "[图形]");
		bodyString = bodyString.replace(" ", "");
		bodyString = bodyString.replace("\n", " ");
		bodyString = bodyString.replaceAll("Face:f" + "\\w{3}", "[表情]");
		bodyString = bodyString.replaceAll("(Face\\^\\_\\^\\[(.*?)\\]\\[(.*?)\\]\\^\\_\\^)","[表情]");
		initialValues.put(KEY_BODY, body);
		initialValues.put(KEY_CONTENT, bodyString);
		initialValues.put(KEY_CATAGORY, catagory);
		initialValues.put(KEY_ALARMFLAG, 0);
		initialValues.put(KEY_ISCHECKED, 0);
		initialValues.put(KEY_STYLE, style);
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR) % 100;
		String month, day, hour, minute, second;
		month = calendar.get(Calendar.MONTH) < 9 ? "0"
				+ (calendar.get(Calendar.MONTH) + 1) : String.valueOf(calendar
						.get(Calendar.MONTH) + 1);
				day = calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0"
						+ calendar.get(Calendar.DAY_OF_MONTH) : String.valueOf(calendar
								.get(Calendar.DAY_OF_MONTH));
						hour = calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0"
								+ calendar.get(Calendar.HOUR_OF_DAY) : String.valueOf(calendar
										.get(Calendar.HOUR_OF_DAY));
								minute = calendar.get(Calendar.MINUTE) < 10 ? "0"
										+ calendar.get(Calendar.MINUTE) : String.valueOf(calendar
												.get(Calendar.MINUTE));
										second = calendar.get(Calendar.SECOND) < 10 ? "0"
												+ calendar.get(Calendar.SECOND) : String.valueOf(calendar
														.get(Calendar.SECOND));
												// Integer day = calendar.get(calendar.get(Calendar.DAY_OF_MONTH));
												String created = year + "/" + month + "/" + day + "时/" + hour + "/"
														+ minute + "/" + second;
												initialValues.put(KEY_CREATED, created);
												initialValues.put(KEY_MODIFY, created);
												initialValues.put(KEY_LOCK, 0);
												initialValues.put(KEY_ISEXPEND, 0);
												return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	public long createCatagory(String name) {
		MainActivity.group_names = getAllCatagory();
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		return mDb.insert(DATABASE_CATAGORY, null, initialValues);
	}

	public long createAlarm(Calendar calendar, long ID, String alarmcatagory) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ALARMID, ID);  
		initialValues.put(KEY_ALAMCATA, alarmcatagory);
		initialValues.put(KEY_ISCHECKED, 0);
		int year = calendar.get(Calendar.YEAR) % 100;
		String month, day, hour, minute;
		month = calendar.get(Calendar.MONTH) < 9 ? "0"
				+ (calendar.get(Calendar.MONTH) + 1) : String.valueOf(calendar
						.get(Calendar.MONTH) + 1);
				day = calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0"
						+ calendar.get(Calendar.DAY_OF_MONTH) : String.valueOf(calendar
								.get(Calendar.DAY_OF_MONTH));
						hour = calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0"
								+ calendar.get(Calendar.HOUR_OF_DAY) : String.valueOf(calendar
										.get(Calendar.HOUR_OF_DAY));
								minute = calendar.get(Calendar.MINUTE) < 10 ? "0"
										+ calendar.get(Calendar.MINUTE) : String.valueOf(calendar
												.get(Calendar.MINUTE));
										// Integer day = calendar.get(calendar.get(Calendar.DAY_OF_MONTH));
										String created = year + "/" + month + "/" + day + " " + hour + ":"
												+ minute;
										initialValues.put(KEY_ALAMTIME, created);
										return mDb.insert(DATABASE_ALARM, null, initialValues);
	}

	public boolean deleteNote(long rowId) {
		Cursor mCursor = getnote(rowId);

		Log.i("wxl", "rowId" + rowId);

		Log.i("wxl", "note_id" + DoNoteWidgetProvider.note_id);

		if(DoNoteWidgetProvider.note_id == rowId && 
				mCursor.getInt(mCursor.getColumnIndexOrThrow(KEY_LOCK)) == 0){
			DoNoteWidgetProvider.isdelete = true;

			//添加代码
			RemoteViews views = new RemoteViews("com.donote.adapter.NoteDbAdapter", R.layout.widget_layout);

			views.setTextViewText(R.id.text_title, "无标题" );
			views.setTextViewText(R.id.text_body, "已经删除啦");

			ComponentName widget  = new ComponentName(NoteDbAdapter.mCtx,DoNoteWidgetProvider.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(NoteDbAdapter.mCtx);
			manager.updateAppWidget(widget, views);

		}
		deleteNoteAlarm(rowId);
		mCursor.close();
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId + " and "+ KEY_LOCK +"=" + 0, null) > 0;
	}

	public boolean deleteAlarm(long rowId) {
		Intent i = new Intent(mCtx, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(mCtx, (int) rowId, i, 0);
		alarm_service.cancel(pi);// 取消闹钟
		return mDb.delete(DATABASE_ALARM, KEY_ROWID + "=" + rowId, null) > 0;
	}

	// 删除特定笔记ID的所有闹钟
	public boolean deleteNoteAlarm(long rowId) {
		Cursor temp = findAlarmByID(rowId);
		while (!temp.isAfterLast())
		{
			Intent i = new Intent(mCtx, AlarmReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(mCtx,
					(int) temp.getLong(temp.getColumnIndexOrThrow(KEY_ROWID)),
					i, 0);
			alarm_service.cancel(pi);// 取消闹钟
			temp.moveToNext();
		}
		temp.close();
		return mDb.delete(DATABASE_ALARM, KEY_ALARMID + "=" + rowId, null) > 0;
	}

	public boolean deleteChecked() {
		Cursor temp = getCheckedNote();
		temp.moveToFirst();
		while (!temp.isAfterLast())
		{
			deleteNoteAlarm(temp.getLong(temp.getColumnIndexOrThrow(KEY_ROWID)));
			deleteNote(temp.getLong(temp.getColumnIndexOrThrow(KEY_ROWID)));
			temp.moveToNext();
		}
		temp.close();
		return mDb.delete(DATABASE_TABLE, KEY_ISCHECKED + "=" + 1 + " and "+ KEY_LOCK +"=" + 0, null) > 0;
	}

	public boolean deleteAlarmChecked() {
		Cursor cursor = getCheckedAlarm();
		while (!cursor.isAfterLast())
		{
			Intent i = new Intent(mCtx, AlarmReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(mCtx, (int) cursor
					.getLong(cursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID)),
							i, 0);
			alarm_service.cancel(pi);// 取消闹钟
			cursor.moveToNext();
		}
		cursor.close();
		return mDb.delete(DATABASE_ALARM, KEY_ISCHECKED + "=" + 1, null) > 0;
	}

	public boolean deleteCatagory(long rowId) {

		Cursor mCursor = getCatagory(rowId);
		String nameString = mCursor.getString(mCursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME));
		Cursor temp = findChilds(nameString);
		while (!temp.isAfterLast())
		{
			deleteNote(temp.getLong(temp.getColumnIndexOrThrow(KEY_ROWID)));
			temp.moveToNext();
		}
		temp.close();
		mCursor.close();
		return mDb.delete(DATABASE_CATAGORY, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public Cursor getAllNotes() {
		if (mDb.isOpen())
		{
			return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID,
					KEY_TITLE, KEY_BODY, KEY_CATAGORY, KEY_CREATED,
					KEY_CONTENT, KEY_ALARMFLAG, KEY_ISCHECKED, KEY_MODIFY ,KEY_LOCK ,KEY_ISEXPEND ,KEY_STYLE},
					null, null, null, null, sortWay);
		} else
		{
			mDb = mDbHelper.getWritableDatabase();
			return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID,
					KEY_TITLE, KEY_BODY, KEY_CATAGORY, KEY_CREATED,
					KEY_CONTENT, KEY_ALARMFLAG, KEY_ISCHECKED, KEY_MODIFY ,KEY_LOCK ,KEY_ISEXPEND,KEY_STYLE},
					null, null, null, null, sortWay);
		}

	}

	public Cursor getAllCatagory() {

		return mDb.query(DATABASE_CATAGORY,
				new String[] { KEY_ROWID, KEY_NAME }, null, null, null, null,
				null);
	}

	public Cursor getnote(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_CATAGORY, KEY_CREATED,
				KEY_CONTENT, KEY_ALARMFLAG, KEY_ISCHECKED, KEY_MODIFY ,KEY_LOCK ,KEY_ISEXPEND ,KEY_STYLE},
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getalarm(long rowId) throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_ALARM, new String[] {
				KEY_ROWID, KEY_ALARMID, KEY_ALAMCATA, KEY_ALAMTIME,
				KEY_ISCHECKED }, KEY_ROWID + "=" + rowId, null, null, null,
				null, null);
		if (mCursor.getCount() > 0)
		{
			mCursor.moveToFirst();
		} else
		{
			return null;
		}
		return mCursor;
	}

	public Cursor getCheckedAlarm() throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_ALARM, new String[] {
				KEY_ROWID, KEY_ALARMID, KEY_ALAMCATA, KEY_ALAMTIME,
				KEY_ISCHECKED }, KEY_ISCHECKED + "=" + 1, null, null, null,
				null, null);
		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getCheckedNote() throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_CATAGORY, KEY_CREATED,
				KEY_CONTENT, KEY_ALARMFLAG, KEY_ISCHECKED, KEY_MODIFY ,KEY_LOCK ,KEY_ISEXPEND},
				KEY_ISCHECKED + "=" + 1, null, null, null, null, null);
		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getCatagory(long rowId) throws SQLException {
		Cursor mCursor = mDb.query(true, DATABASE_CATAGORY,
				new String[] { KEY_NAME }, KEY_ROWID + "=" + rowId, null, null,
				null, null, null);
		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean updateNote(long rowId, String title, String body,int style,
			String catagory) {
		ContentValues args = new ContentValues();
		args.put(KEY_TITLE, title);
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
				"(Gesture\\^\\_\\^\\[(.*?)\\]{1,2}\\^\\_\\^)", "[手写]");
		bodyString = bodyString.replaceAll(
				"(Cloud\\^\\_\\^\\[(.*?)\\]\\[(.*?)\\]\\^\\_\\^)", "[图形]");
		bodyString = bodyString.replace(" ", "");
		bodyString = bodyString.replace("\n", " ");
		bodyString = bodyString.replaceAll("Face:f" + "\\w{3}", "[表情]");
		bodyString = bodyString.replaceAll("(Face\\^\\_\\^\\[(.*?)\\]\\[(.*?)\\]\\^\\_\\^)","[表情]");

		args.put(KEY_BODY, body);
		args.put(KEY_CONTENT, bodyString);
		args.put(KEY_CATAGORY, catagory);
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR) % 100;
		String month, day, hour, minute, second;
		month = calendar.get(Calendar.MONTH) < 9 ? "0"
				+ (calendar.get(Calendar.MONTH) + 1) : String.valueOf(calendar
						.get(Calendar.MONTH) + 1);
				day = calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0"
						+ calendar.get(Calendar.DAY_OF_MONTH) : String.valueOf(calendar
								.get(Calendar.DAY_OF_MONTH));
						hour = calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0"
								+ calendar.get(Calendar.HOUR_OF_DAY) : String.valueOf(calendar
										.get(Calendar.HOUR_OF_DAY));
								minute = calendar.get(Calendar.MINUTE) < 10 ? "0"
										+ calendar.get(Calendar.MINUTE) : String.valueOf(calendar
												.get(Calendar.MINUTE));
										second = calendar.get(Calendar.SECOND) < 10 ? "0"
												+ calendar.get(Calendar.SECOND) : String.valueOf(calendar
														.get(Calendar.SECOND));
												// Integer day = calendar.get(calendar.get(Calendar.DAY_OF_MONTH));
												String modify = year + "/" + month + "/" + day + "时/" + hour + "/"
														+ minute + "/" + second;
												args.put(KEY_MODIFY, modify);
												args.put(KEY_ISEXPEND, 0);
												args.put(KEY_STYLE, style);
												return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;

	}

	public boolean updateAlarm(long alarmID, long noteID, Calendar calendar,
			String content) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ALARMID, noteID);
		if (content != null)
		{
			initialValues.put(KEY_ALAMCATA, content);
		}
		int year = calendar.get(Calendar.YEAR) % 100;
		String month, day, hour, minute;
		month = calendar.get(Calendar.MONTH) < 9 ? "0"
				+ (calendar.get(Calendar.MONTH) + 1) : String.valueOf(calendar
						.get(Calendar.MONTH) + 1);
				day = calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0"
						+ calendar.get(Calendar.DAY_OF_MONTH) : String.valueOf(calendar
								.get(Calendar.DAY_OF_MONTH));
						hour = calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0"
								+ calendar.get(Calendar.HOUR_OF_DAY) : String.valueOf(calendar
										.get(Calendar.HOUR_OF_DAY));
								minute = calendar.get(Calendar.MINUTE) < 10 ? "0"
										+ calendar.get(Calendar.MINUTE) : String.valueOf(calendar
												.get(Calendar.MINUTE));
										String created = year + "/" + month + "/" + day + " " + hour + ":"
												+ minute;
										initialValues.put(KEY_ALAMTIME, created);
										return mDb.update(DATABASE_ALARM, initialValues, KEY_ROWID + "="
												+ alarmID, null) > 0;
	}

	public boolean updateAlarmflag(long rowId) {
		ContentValues args = new ContentValues();
		args.put(KEY_ALARMFLAG, 1);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean cancleAlarmflag(long rowId) {
		ContentValues args = new ContentValues();
		args.put(KEY_ALARMFLAG, 0);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateIsChecked(long rowId) {
		ContentValues args = new ContentValues();
		args.put(KEY_ISCHECKED, 1);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateIsExpend(long rowId) {
		ContentValues args = new ContentValues();
		args.put(KEY_ISEXPEND, 1);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateAlarmIsChecked(long rowId) {
		ContentValues args = new ContentValues();
		args.put(KEY_ISCHECKED, 1);
		return mDb.update(DATABASE_ALARM, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean cancleAlarmIsChecked(long rowId) {
		ContentValues args = new ContentValues();
		args.put(KEY_ISCHECKED, 0);
		return mDb.update(DATABASE_ALARM, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean cancleALLChecked() {
		ContentValues args = new ContentValues();
		args.put(KEY_ISCHECKED, 0);
		if (mDb.isOpen())
		{
			return mDb.update(DATABASE_TABLE, args, null, null) > 0;
		} else
		{
			mDb = mDbHelper.getWritableDatabase();
			return mDb.update(DATABASE_TABLE, args, null, null) > 0;
		}
	}

	public boolean cancleALLExpend() {
		ContentValues args = new ContentValues();
		args.put(KEY_ISEXPEND, 0);
		if (mDb.isOpen())
		{
			return mDb.update(DATABASE_TABLE, args, null, null) > 0;
		} else
		{
			mDb = mDbHelper.getWritableDatabase();
			return mDb.update(DATABASE_TABLE, args, null, null) > 0;
		}
	}

	public boolean cancleIsChecked(long rowId) {
		ContentValues args = new ContentValues();
		args.put(KEY_ISCHECKED, 0);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean cancleIsExpend(long rowId) {
		ContentValues args = new ContentValues();
		args.put(KEY_ISEXPEND, 0);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean cancleAlarmALLChecked() {
		ContentValues args = new ContentValues();
		args.put(KEY_ISCHECKED, 0);
		if (mDb.isOpen())
		{
			return mDb.update(DATABASE_ALARM, args, null, null) > 0;
		} else
		{
			mDb = mDbHelper.getWritableDatabase();
			return mDb.update(DATABASE_ALARM, args, null, null) > 0;
		}
	}

	public boolean updateCatagory(long rowId, String name) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		return mDb.update(DATABASE_CATAGORY, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public boolean lockNote(long rowId) {
		ContentValues args = new ContentValues();
		args.put(KEY_LOCK, 1);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public boolean unLockNote(long rowId) {
		ContentValues args = new ContentValues();
		args.put(KEY_LOCK, 0);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId,
				null) > 0;
	}

	public boolean isAllUnChacked() {
		Cursor cursor = mDb.rawQuery("select * from note where ischecked = 1 ",
				null);
		if (cursor.getCount() > 0)
		{
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

	public boolean isAlarmAllUnChacked() {
		Cursor cursor = mDb.rawQuery(
				"select * from alarm where ischecked = 1 ", null);
		if (cursor.getCount() > 0)
		{
			cursor.close();
			return false;
		}
		cursor.close();
		return true;
	}

	public void deleteAllNotesAndCat() {
		mDb.execSQL("truncate table note");
		mDb.execSQL("truncate table catagory");
		mDb.execSQL("truncate table alarm");
	}
}
