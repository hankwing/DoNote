package com.donote.adapter;

import com.donote.activity.MainActivity;
import com.wxl.donote.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

//÷ÿ–¥simplecursoradapter
public class MyAlarmSimpleCursorAdapter extends BaseAdapter {

	protected static final int ACTIVITY_TIMEPICKER = 1;
	private LayoutInflater mInflater;
	private Cursor cursor;
	public static View itemGroup[];
	public static boolean visflag = false;

	// public ArrayList<Long> idList = new ArrayList<Long>();

	public final class ViewHolder {
		public TextView timeAndCata;
		private CheckBox cb;
		private TextView content;
	}

	public MyAlarmSimpleCursorAdapter(Context context, Cursor c) {
		this.mInflater = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
		this.cursor = c;
		itemGroup = new View[getCount()];
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
			holder.content = (TextView) convertView.findViewById(R.id.alarm_content);
			convertView.setTag(holder);
		} else
		{
			holder = (ViewHolder) convertView.getTag();
		}
		String timeAndCataString = cursor.getString(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMTIME));
		/*timeAndCataString += ("("
				+ cursor.getString(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMCATA)) + ")");*/
		String alarmContentString = cursor.getString(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALAMCATA));
		
		holder.timeAndCata.setText(timeAndCataString);
		holder.content.setText(alarmContentString);
		
		if (cursor.getInt(cursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ISCHECKED)) == 0)
		{
			holder.cb.setChecked(false);
		} else
		{
			holder.cb.setChecked(true);
		}
		holder.cb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(Pos);
				Long id = cursor.getLong(cursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
				Cursor c = MainActivity.mDbHelper.getalarm(id);
				if(c!= null) {
					c.moveToFirst();
					if (c.getInt(c
							.getColumnIndexOrThrow(NoteDbAdapter.KEY_ISCHECKED)) == 0)
					{
						MainActivity.mDbHelper.updateAlarmIsChecked(id);
						cursor = MainActivity.mDbHelper.findAlarmByID(c.getLong(c
								.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALARMID)));
	
					} else
					{
						MainActivity.mDbHelper.cancleAlarmIsChecked(id);
						cursor = MainActivity.mDbHelper.findAlarmByID(c.getLong(c
								.getColumnIndexOrThrow(NoteDbAdapter.KEY_ALARMID)));
	
					}
					c.close();
				}
			}
		});

		//itemGroup[position] = convertView;
		return convertView;
	}

	public void closeCursor() {
		// TODO Auto-generated method stub
		cursor.close();
	}

}
