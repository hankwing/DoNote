package com.donote.adapter;

//this should be whatever your namespace is, mine was test.test
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.donote.activity.CommonEdit;
import com.donote.activity.FreeEdit;
import com.donote.activity.MainActivity;
import com.donote.alarm.AlarmSet;
import com.donote.alarm.ListTimePicker;
import com.donote.animation.ExpandAnimation;
import com.donote.imagehandler.ImageMemoryCache;
import com.donote.util.ShareUtil;
import com.wxl.donote.R;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class MyCursorTreeAdapter extends MySimpleCursorTreeAdapter {
	private static final String addition_detect = "\\[(ÎÄ¼þ|Â¼Òô)\\]";
	private static String patternString = "(Photo|Gesture|Video|Record|Draw|File|Picture){1}\\^_\\^\\[(.*?)\\]\\^_\\^";
	private static final String image_detect = "(Photo|Video|Picture|Draw){1}\\^_\\^\\[(.*?)\\]{1,2}\\^_\\^";
	private static MainActivity context2;
	private AlertDialog.Builder builder;
	public static boolean visflag = false;
	private Matcher photoMatcher;
	static String sortWay;
	private LayoutParams mViewLayoutParams;
	private Matcher imageMatcher;

	// public ArrayList<Long> idList = new ArrayList<Long>();

	public MyCursorTreeAdapter(Context co, Cursor groupData) {
		super(co, groupData);
		// TODO Auto-generated constructor stub
		MyCursorTreeAdapter.context2 = (MainActivity) co;
		SharedPreferences settings = context2
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
	protected View newChildView(Context context, Cursor cursor,
			boolean isLastChild, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getInflater().inflate(R.layout.note_row, parent, false);
	}

	@Override
	protected View newGroupView(Context context, Cursor cursor,
			boolean isExpanded, ViewGroup parent) {
		// TODO Auto-generated method stub
		return getInflater().inflate(R.layout.group, parent, false);
	}

	@Override
	protected void bindChildView(final int groupPosition,
			final int childPosition, View view, final Context context,
			final Cursor cursor, boolean isLastChild) {
		// TODO Auto-generated method stub
		final Cursor child = (Cursor) super.getChild(groupPosition,
				childPosition);
		TextView textView = (TextView) view.findViewById(R.id.text1);
		ImageButton fastViewAlarm;
		ImageButton fastViewDelete;
		ImageButton fastViewShare;
		ImageButton fastViewLockOrUnlock;
		ImageButton fastViewMoveTo;
		ImageButton fastViewModify;
		ImageButton fastViewAlarmManager;
		switch (MainActivity.textsize)
		{
		case 0:
			textView.setTextSize(16);
			break;
		case 1:
			textView.setTextSize(19);
			break;
		case 2:
			textView.setTextSize(21);
			break;
		case 3:
			textView.setTextSize(24);
			break;
		default:
			break;
		}
		ImageView icon = (ImageView) view.findViewById(R.id.noteicon);
		fastViewAlarm = (ImageButton) view
				.findViewById(R.id.fast_view_alarm_set);
		fastViewAlarmManager = (ImageButton) view
				.findViewById(R.id.fast_view_alarm_manager);
		fastViewShare = (ImageButton) view.findViewById(R.id.fast_view_share);
		fastViewMoveTo = (ImageButton) view.findViewById(R.id.fast_view_moveto);
		fastViewModify = (ImageButton) view.findViewById(R.id.fast_view_modify);
		fastViewLockOrUnlock = (ImageButton) view  
				.findViewById(R.id.fast_view_lock_or_unlock);
		fastViewDelete = (ImageButton) view.findViewById(R.id.fast_view_delete);
		textView.setText(child.getString(child.getColumnIndexOrThrow("title")));
		TextView created = (TextView) view.findViewById(R.id.created);
		String createdString = child.getString(child
				.getColumnIndexOrThrow(sortWay));
		int end = createdString.indexOf("Ê±");
		createdString = createdString.substring(0, end);
		created.setText(createdString);
		ImageButton button1 = (ImageButton) view
				.findViewById(R.id.fast_view_button);
		TextView bodyTextView = (TextView) view
				.findViewById(R.id.note_item_body);
		final String bodyString = child.getString(child
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_CONTENT));
		bodyTextView.setText(bodyString);
		final String bodyString2 = child.getString(child
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY));
		final Long id = child.getLong(child
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
		Pattern additionPattern = Pattern.compile(addition_detect);
		Pattern imagePattern = Pattern.compile(image_detect);
		photoMatcher = additionPattern.matcher(bodyString);
		imageMatcher = imagePattern.matcher(bodyString2);
		if (cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK)) == 1)
		{
			icon.setPadding(2, 2, 0, 0);
			icon.setImageResource(R.drawable.ic_note_locked);
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

			if (!MySimpleCursorAdapter.imageCache.isCached(id))
			{
				ImageMemoryCache.addBitmapToCache(id,
						tempBitmap);
			}
			if (tempBitmap != null)
			{
				icon.setImageBitmap(tempBitmap);
				icon.setPadding(2, 2, 0, 0);
			} else
			{
				icon.setPadding(2, 2, 0, 0);
				icon.setImageResource(R.drawable.ic_note);
			}
		} else if (photoMatcher.find())
		{
			icon.setPadding(2, 2, 0, 0);
			icon.setImageResource(R.drawable.ic_add_note);
		} else
		{
			icon.setPadding(2, 2, 0, 0);
			icon.setImageResource(R.drawable.ic_note);
		}
		final CheckBox cb = (CheckBox) view.findViewById(R.id.checkBox);
		if (child.getInt(child
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ISCHECKED)) == 0)
		{
			cb.setChecked(false);
		} else
		{
			cb.setChecked(true);
		}

		cursor.moveToPosition(childPosition);
		if (child.getInt(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALARMFLAG)) == 1)
		{
			button1.setImageResource(R.drawable.ic_expend_with_alarm);
		} else
		{
			button1.setImageResource(R.drawable.ic_expend);
		}
		
		final View toolbar = view.findViewById(R.id.toolbar);
		mViewLayoutParams = (LayoutParams) toolbar.getLayoutParams();
		if (child.getInt(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ISEXPEND)) == 0)
		{
			toolbar.setVisibility(View.GONE);
			mViewLayoutParams.bottomMargin = -90;
		} else
		{
			toolbar.setVisibility(View.VISIBLE);
			mViewLayoutParams.bottomMargin = 0;
		}	
		
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(childPosition);
				if (cursor.getInt(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_ISEXPEND)) == 0)
				{
					MainActivity.mDbHelper.updateIsExpend(id);
					ExpandAnimation expandAni = new ExpandAnimation(toolbar, 300);
					toolbar.startAnimation(expandAni);
				} else
				{
					MainActivity.mDbHelper.cancleIsExpend(id);
					ExpandAnimation expandAni = new ExpandAnimation(toolbar, 300);
					toolbar.startAnimation(expandAni);
				}
			}
		});

		cb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Cursor cursor = MainActivity.mDbHelper.getnote(id);
				if (cursor.getInt(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_ISCHECKED)) == 0)
				{
					MainActivity.mDbHelper.updateIsChecked(id);
				} else
				{
					MainActivity.mDbHelper.cancleIsChecked(id);
				}
				cursor.close();
			}
		});

		if (visflag)
		{
			cb.setVisibility(View.VISIBLE);
			button1.setVisibility(View.INVISIBLE);
		} else
		{
			cb.setVisibility(View.INVISIBLE);
			button1.setVisibility(View.VISIBLE);
		}

		fastViewAlarm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(childPosition);
				Long id = cursor.getLong(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
				Intent time_picker;
				time_picker = new Intent(context2, ListTimePicker.class);
				time_picker.putExtra("GroupPosition", groupPosition);
				time_picker.putExtra("ChildPosition", childPosition);
				time_picker.putExtra("ID", id);
				context2.startActivityForResult(time_picker, 2);
			}
		});

		fastViewDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(childPosition);
				if (cursor.getInt(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK)) == 0)
				{
					builder = new Builder(context2);
					builder.setMessage(context2.getResources().getString(R.string.confirm_delete));
					builder.setTitle(context2.getResources().getString(R.string.tip));
					builder.setPositiveButton(context2.getResources().getString(R.string.confirm),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MainActivity.mDbHelper.deleteNote(cursor.getLong(cursor
											.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID)));
									Toast.makeText(context2, context2.getResources().getString(R.string.delete_succeed),
											Toast.LENGTH_SHORT).show();
									notifyDataSetChanged();
									MainActivity.notes.notifyDataSetChanged();
								}
							});
					builder.setNegativeButton(context2.getResources().getString(R.string.cancel),
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
					Toast.makeText(context2, context2.getResources().getString(R.string.note_locked), Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		fastViewShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(childPosition);
				String body = bodyString;
				body = bodyString.replaceAll(patternString, "");
				body = bodyString.replaceAll("Face:f" + "\\w{3}", "");
				body = bodyString.replace(" ", "");
				Pattern imagePattern = Pattern.compile(image_detect);
				imageMatcher = imagePattern.matcher(bodyString2);

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
					ShareUtil.shareMsg(
							context2,
							context2.getTitle().toString(),
							context2.getResources().getString(
									R.string.note_share), body, idString);
					return;
				}
				ShareUtil.shareMsg(context2, context2.getTitle().toString(),
						context2.getResources().getString(R.string.note_share),
						body, null);
			}
		});

		fastViewLockOrUnlock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(childPosition);
				if (cursor.getInt(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK)) == 0)
				{
					MainActivity.mDbHelper.lockNote(cursor.getLong(cursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID)));
					Toast.makeText(context2, context2.getResources().getString(R.string.lock_succeed), Toast.LENGTH_SHORT).show();
					notifyDataSetChanged();
					MainActivity.notes.notifyDataSetChanged();
				} else
				{
					MainActivity.mDbHelper.unLockNote(cursor.getLong(cursor
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID)));
					Toast.makeText(context2, context2.getResources().getString(R.string.unlock_succeed), Toast.LENGTH_SHORT).show();
					notifyDataSetChanged();
					MainActivity.notes.notifyDataSetChanged();
				}
			}
		});
		
		fastViewModify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(childPosition);
				edit_Enter_modify(cursor,cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID)));
				
			}
		});
		
		fastViewAlarmManager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(childPosition);
				if (cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALARMFLAG)) == 1)
				{
					Intent i = new Intent(context2,
							AlarmSet.class);
					i.putExtra("NoteID", cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID)));
					context2.startActivity(i);
				} else
				{
					Toast.makeText(context2, context2.getResources().getString(R.string.the_note_hasnot_alarm),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		fastViewMoveTo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(childPosition);
				moveTo_showDialog(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID)));
			}
		});

	}

	@Override
	protected void bindGroupView(int groupPosition, View view, Context context,
			Cursor cursor, boolean isExpanded) {
		// TODO Auto-generated method stub
		Cursor group = (Cursor) super.getGroup(groupPosition);
		if (!group.isClosed())
		{
			TextView textView = (TextView) view.findViewById(R.id.groupto);
			TextView noteCount = (TextView) view.findViewById(R.id.notecount);
			String nameString = group.getString(group
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME));
			textView.setText(nameString);
			noteCount.setText(String.valueOf(MainActivity.mDbHelper.findChilds(
					nameString).getCount()));
		}
	}

	/*
	 * public void changeAlarmStatusToTrue(int groupPosition, int childPosition)
	 * {
	 * 
	 * int childCount = 0; int group_e = groupPosition; while (group_e != 0) {
	 * childCount += getChildrenCount(group_e - 1); group_e--;
	 * 
	 * }
	 * 
	 * // View childView = getChildView(groupPosition, childPosition, false, //
	 * null, null); View childView =
	 * mySimpleCursorTreeAdapter.childViewGroup[groupPosition][childPosition];
	 * 
	 * ImageButton imageButton = (ImageButton) childView
	 * .findViewById(R.id.note_set_alarm);
	 * 
	 * imageButton.setImageResource(R.drawable.ic_alarm);
	 * 
	 * MainActivity.mDbHelper.updateAlarmflag(getChildId(groupPosition,
	 * childPosition)); }
	 * 
	 * public void changeAlarmStatusToFalse(int groupPosition, int
	 * childPosition) {
	 * 
	 * View childView =
	 * mySimpleCursorTreeAdapter.childViewGroup[groupPosition][childPosition];
	 * ImageButton imageButton = (ImageButton) childView
	 * .findViewById(R.id.note_set_alarm);
	 * imageButton.setImageResource(R.drawable.ic_alarm_unset);
	 * MainActivity.mDbHelper.cancleAlarmflag(getChildId(groupPosition,
	 * childPosition));
	 * 
	 * }
	 */

	public static void changeSortWay() {
		SharedPreferences settings = context2
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
	
	public void edit_Enter_modify(Cursor c, Long id) {
		if(c.getInt(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_LOCK)) == 0) {
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
		}
		else {
			Toast.makeText(context2, context2.getResources().getString(R.string.note_locked), Toast.LENGTH_SHORT).show();
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

		new AlertDialog.Builder(context2).setTitle(context2.getResources().getString(R.string.choose)).setView(view)
				.setPositiveButton(context2.getResources().getString(R.string.confirm), new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						final String catagoryNameString = spinner_c
								.getSelectedItem().toString();
						MainActivity.mDbHelper.updata_catagory_single_notes(id,
								catagoryNameString);
						notifyDataSetChanged();
						Toast.makeText(context2, context2.getResources().getString(R.string.move_succeed),
								Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton(context2.getResources().getString(R.string.cancel), null).create().show();
	}

}