package com.donote.adapter;

import com.donote.activity.MainActivity;
import com.wxl.donote.R;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MyDetectAlarmAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private MergeCursor cursor;
	private Cursor[] childCursor;
	private long[] idList;
	private int number;

	// public static CursorWindow cursorWindow = new
	// CursorWindow("cursorWindow");

	// public ArrayList<Long> idList = new ArrayList<Long>();

	public final class ViewHolder {
		public TextView timeAndCata;
		private TextView content;
		private CheckBox cb;
	}

	public MyDetectAlarmAdapter(Context context, Cursor c, long[] idList,
			int number) {
		this.mInflater = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
		this.cursor = (MergeCursor) c;
		this.idList = idList;
		childCursor = new Cursor[number];
		this.number = number;
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
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.alarm_row, null);
			holder.timeAndCata = (TextView) convertView
					.findViewById(R.id.alarm_timeandcata);
			holder.cb = (CheckBox) convertView
					.findViewById(R.id.alarm_checkBox);
			holder.content = (TextView) convertView
					.findViewById(R.id.alarm_content);
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		String timeAndCataString = cursor.getString(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMTIME));
		/*
		 * timeAndCataString += ("(" + cursor.getString(cursor
		 * .getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMCATA)) + ")");
		 */
		String alarmContentString = cursor.getString(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMCATA));

		holder.timeAndCata.setText(timeAndCataString);
		holder.content.setText(alarmContentString);

		if (cursor.getInt(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ISCHECKED)) == 0)
		{
			holder.cb.setChecked(true);
		} else
		{
			holder.cb.setChecked(false);
		}

		holder.cb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(Pos);
				Long id = cursor.getLong(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
				Cursor c = MainActivity.mDbHelper.getalarm(id);
				if (c != null)
				{
					c.moveToFirst();
					if (c.getInt(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_ISCHECKED)) == 0)
					{
						MainActivity.mDbHelper.updateAlarmIsChecked(id);
						for (int i = 1; i <= number; i++)
						{
							childCursor[i - 1] = MainActivity.mDbHelper
									.findAlarmByAlarmID(idList[i - 1]);
						}
						cursor = new MergeCursor(childCursor);

					} else
					{
						MainActivity.mDbHelper.cancleAlarmIsChecked(id);
						for (int i = 1; i <= number; i++)
						{
							childCursor[i - 1] = MainActivity.mDbHelper
									.findAlarmByAlarmID(idList[i - 1]);
						}
						cursor = new MergeCursor(childCursor);
						// cursor.getWindow().close();
					}
					c.close();
				}
			}
		});

		// itemGroup[position] = convertView;
		return convertView;
	}

	public void closeCursor() {
		// TODO Auto-generated method stub
		cursor.close();
	}

}
