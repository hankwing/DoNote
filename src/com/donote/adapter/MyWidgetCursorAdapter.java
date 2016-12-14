package com.donote.adapter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.donote.activity.CommonEdit;
import com.donote.activity.DisplayContent;
import com.donote.activity.FreeEdit;
import com.donote.activity.MainActivity;
import com.donote.alarm.AlarmSet;
import com.donote.alarm.ListTimePicker;
import com.donote.animation.ExpandAnimation;
import com.donote.imagehandler.ImageMemoryCache;
import com.donote.implement.ImageButtonOnTouch;
import com.wxl.donote.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

//重写simplecursoradapter
public class MyWidgetCursorAdapter extends BaseAdapter {
	private static String patternString = "(Photo|Video|Record|Draw|File|Picture|Text){1}\\^_\\^\\[(.*?)\\]\\^_\\^";
	protected static final int ACTIVITY_TIMEPICKER = 1;
	private static final String addition_detect = "\\[(文件|录音)\\]";
	private static final String image_detect = "(Photo|Video|Picture|Draw){1}\\^_\\^\\[(.*?)\\]{1,2}\\^_\\^";
	private static Activity context;
	private AlertDialog.Builder builder;
	private Activity context2;
	private LayoutParams mViewLayoutParams;
	public static ImageMemoryCache imageCache;
	private static String sortWay;
	private LayoutInflater mInflater;
	private Cursor cursor;
	private Matcher photoMatcher;
	private Matcher imageMatcher;
	private String conditionString;
	// public static View itemGroup[];
	public static boolean visflag = false;

	// public ArrayList<Long> idList = new ArrayList<Long>();

	public static final class ViewHolder {
		public ImageView icon;
		public TextView title;
		public TextView created;
		public TextView bodyTextView;

	}

	public MyWidgetCursorAdapter(Context context, Cursor c, String condition) {
		this.mInflater = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
		MyWidgetCursorAdapter.context = (Activity) context;
		context2 = (Activity) context;
		this.cursor = c;
		this.conditionString = condition;
		imageCache = new ImageMemoryCache(MyWidgetCursorAdapter.context);
		// itemGroup = new View[getCount()];
		SharedPreferences settings = MyWidgetCursorAdapter.context
				.getSharedPreferences("sortWay", 0);
		if (settings.getString("sort", "").equals(""))
		{
			sortWay = "created";
		} else if (settings.getString("sort", "").equals("c"))
		{
			sortWay = "created";
		} else
		{
			sortWay = "modify";
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		cursor.moveToPosition(position);
		return cursor.getLong(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		// TODO Auto-generated method stub
		ViewHolder holder = null;
		final int Pos = position;
		cursor.moveToPosition(position);
		final Long id = cursor.getLong(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));

		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.widget_row, null);
			holder.title = (TextView) convertView.findViewById(R.id.text1);
			switch (MainActivity.textsize)
			{
			case 0:
				holder.title.setTextSize(16);
				break;
			case 1:
				holder.title.setTextSize(18);
				break;
			case 2:
				holder.title.setTextSize(21);
				break;
			case 3:
				holder.title.setTextSize(24);
				break;
			default:
				break;
			}

			holder.icon = (ImageView) convertView.findViewById(R.id.noteicon);
			holder.created = (TextView) convertView.findViewById(R.id.created);
			holder.bodyTextView = (TextView) convertView
					.findViewById(R.id.note_item_body);
			// holder.fastViewText = (TextView)
			// convertView.findViewById(R.id.fast_view_content);
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
			switch (MainActivity.textsize)
			{
			case 0:
				holder.title.setTextSize(16);
				break;
			case 1:
				holder.title.setTextSize(18);
				break;
			case 2:
				holder.title.setTextSize(21);
				break;
			case 3:
				holder.title.setTextSize(24);
				break;
			default:
				break;
			}
		}

		holder.title.setText(cursor.getString(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));

		String createdString = cursor.getString(cursor
				.getColumnIndexOrThrow(sortWay));
		int end = createdString.indexOf("时");
		createdString = createdString.substring(0, end);
		holder.created.setText(createdString);
		final String bodyString = cursor.getString(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_CONTENT));
		String bodyString2 = cursor.getString(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY));
		holder.bodyTextView.setText(bodyString);
		// holder.fastViewText.setText(bodyString);
		Pattern additionPattern = Pattern.compile(addition_detect);
		Pattern imagePattern = Pattern.compile(image_detect);
		photoMatcher = additionPattern.matcher(bodyString);
		imageMatcher = imagePattern.matcher(bodyString2);
		if (cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK)) == 1)
		{
			holder.icon.setPadding(2, 2, 0, 0);
			holder.icon.setImageResource(R.drawable.ic_note_locked);
		} else if (imageMatcher.find())
		{
			Bitmap tempBitmap = null;
			if (imageMatcher.group(1).equals("Photo"))
			{
				String idString = imageMatcher.group();
				if(imageMatcher.group(2).contains("][")) {
					idString = idString.substring(
							idString.indexOf("Photo^_^[") + 9,
							idString.indexOf("]["));
				}
				else {
					idString = imageMatcher.group(2);
				}
				tempBitmap = ImageMemoryCache.getBitmap(id, idString);
			} else if (imageMatcher.group(1).equals("Draw"))
			{
				String idString = imageMatcher.group();
				if(imageMatcher.group(2).contains("][")) {
					idString = idString.substring(
							idString.indexOf("Draw^_^[") + 8,
							idString.indexOf("]["));
				}
				else {
					idString = imageMatcher.group(2);
				}
				tempBitmap = ImageMemoryCache.getBitmap(id, idString);
			}

			else if (imageMatcher.group(1).equals("Picture"))
			{
				String idString = imageMatcher.group();
				if(imageMatcher.group(2).contains("][")) {
					idString = idString.substring(
							idString.indexOf("Picture^_^[") + 11,
							idString.indexOf("]["));
				}
				else {
					idString = imageMatcher.group(2);
				}
				tempBitmap = ImageMemoryCache.getBitmap(id, idString);

			} else if (imageMatcher.group(1).equals("Video"))
			{
				String idString = imageMatcher.group();
				if(imageMatcher.group(2).contains("][")) {
					idString = idString.substring(
							idString.indexOf("Video^_^[") + 9,
							idString.indexOf("]["));
				}
				else {
					idString = imageMatcher.group(2);
				}
				tempBitmap = ImageMemoryCache.getBitmap(id, idString);
			}

			if (!imageCache.isCached(id))
			{
				ImageMemoryCache.addBitmapToCache(id, tempBitmap);
			}
			if (tempBitmap != null)
			{
				holder.icon.setPadding(2, 2, 0, 0);
				holder.icon.setImageBitmap(tempBitmap);
			} else
			{
				// imageCache.detectImage(bodyString2, id);
				holder.icon.setPadding(2, 2, 0, 0);
				holder.icon.setImageResource(R.drawable.ic_note);
			}
		} else if (photoMatcher.find())
		{
			holder.icon.setPadding(2, 2, 0, 0);
			holder.icon.setImageResource(R.drawable.ic_add_note);
		} else
		{
			holder.icon.setPadding(2, 2, 0, 0);
			holder.icon.setImageResource(R.drawable.ic_note);
		}
		
		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#notifyDataSetChanged()
	 */
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		if (getCount() >= MainActivity.mDbHelper.getCount())
		{
			cursor = MainActivity.mDbHelper.getAllNotes();
		} else
		{
			cursor = MainActivity.mDbHelper.find(conditionString);
		}
		super.notifyDataSetChanged();
	}

	public void closeCursor() {
		// TODO Auto-generated method stub
		cursor.close();
	}

	public static void changeSortWay() {
		SharedPreferences settings = context.getSharedPreferences("sortWay", 0);
		if (settings.getString("sort", "").equals(""))
		{
			sortWay = "created";
		} else if (settings.getString("sort", "").equals("c"))
		{
			sortWay = "created";
		} else
		{
			sortWay = "modify";
		}
	}

	public void edit_Enter(Cursor c, Long id) {
		int style = c.getInt(c
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE));
		if (style == 0) {

			Intent i = new Intent(context2, DisplayContent.class);
			i.putExtra(NoteDbAdapter.KEY_ROWID, id);
			i.putExtra(NoteDbAdapter.KEY_TITLE,
					c.getString(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
			i.putExtra(NoteDbAdapter.KEY_BODY,
					c.getString(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
			i.putExtra(NoteDbAdapter.KEY_CATAGORY, c.getString(c
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_CATAGORY)));
			context2.startActivity(i);

		} else if (style == 1) {
			Intent i = new Intent(context2, FreeEdit.class);
			i.putExtra(NoteDbAdapter.KEY_ROWID, id);
			i.putExtra(NoteDbAdapter.KEY_TITLE,
					c.getString(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
			i.putExtra(NoteDbAdapter.KEY_BODY,
					c.getString(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
			i.putExtra(NoteDbAdapter.KEY_CATAGORY, c.getString(c
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_CATAGORY)));
			context2.startActivity(i);
		}
	}

	public void edit_Enter_modify(Cursor c, Long id) {
		if (c.getInt(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK)) == 0)
		{
			Intent i = null;
			if(c.getInt(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE)) == 0) {
				i = new Intent(context2, CommonEdit.class);
			}
			else {
				i = new Intent(context2, FreeEdit.class);
			}
			i.putExtra(NoteDbAdapter.KEY_ROWID, id);
			i.putExtra(NoteDbAdapter.KEY_TITLE, c.getString(c
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)));
			i.putExtra(NoteDbAdapter.KEY_BODY, c.getString(c
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
			i.putExtra(NoteDbAdapter.KEY_CATAGORY, c.getString(c
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_CATAGORY)));
			context2.startActivity(i);
		} else
		{
			Toast.makeText(context2, "该笔记被锁定", Toast.LENGTH_SHORT).show();
		}

	}

	protected void moveTo_showDialog(final Long id) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(context2);
		View view = layoutInflater.inflate(R.layout.movetodialog,
				(ViewGroup) context2.findViewById(R.id.moveto_dialog));
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
				context2, android.R.layout.simple_spinner_item, allCatagory);
		allCatagory_adapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_c.setAdapter(allCatagory_adapter);

		new AlertDialog.Builder(context2).setTitle("请选择").setView(view)
		.setPositiveButton("确定", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				final String catagoryNameString = spinner_c
						.getSelectedItem().toString();
				MainActivity.mDbHelper.updata_catagory_single_notes(id,
						catagoryNameString);
				Toast.makeText(context2, "移动成功", Toast.LENGTH_SHORT)
				.show();
				MainActivity.adapter.notifyDataSetChanged();
			}
		}).setNegativeButton("取消", null).create().show();
	}

}
