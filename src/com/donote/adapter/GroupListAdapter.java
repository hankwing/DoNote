package com.donote.adapter;

import java.util.HashMap;

import com.donote.activity.MainActivity;
import com.donote.util.GroupListView;
import com.donote.util.GroupListView.QQHeaderAdapter;
import com.wxl.donote.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupListAdapter extends MyCursorTreeAdapter implements
		QQHeaderAdapter {
	private GroupListView listView;
	private Context context;

	public GroupListAdapter(Context context, GroupListView listView,
			Cursor groupcursor) {
		super(context, groupcursor);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.listView = listView;
	}

	@Override
	public int getQQHeaderState(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1)
		{
			return PINNED_HEADER_PUSHED_UP;
		} else if (childPosition == -1
				&& !listView.isGroupExpanded(groupPosition))
		{
			return PINNED_HEADER_GONE;
		} else
		{
			return PINNED_HEADER_VISIBLE;
		}
	}

	@Override
	public void configureQQHeader(View header, int groupPosition,
			int childPosition, int alpha) {
		// TODO Auto-generated method stub
		Cursor groupData = (Cursor) this.getGroup(groupPosition);
		((TextView) header.findViewById(R.id.groupto)).setText(groupData
				.getString(groupData
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME)));
		Cursor group = (Cursor) super.getGroup(groupPosition);
		if (!group.isClosed())
		{
			TextView noteCount = (TextView)header.findViewById(R.id.notecount);
			String nameString = group.getString(group
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME));
			noteCount.setText(String.valueOf(MainActivity.mDbHelper.findChilds(nameString).getCount()));
		}
	}

	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, Integer> groupStatusMap = new HashMap<Integer, Integer>();

	@Override
	public void setGroupClickStatus(int groupPosition, int status) {
		// TODO Auto-generated method stub
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getGroupClickStatus(int groupPosition) {
		// TODO Auto-generated method stub
		if (groupStatusMap.containsKey(groupPosition))
		{
			return groupStatusMap.get(groupPosition);
		} else
		{
			return 0;
		}
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.group,
					null);
		}
		
		ImageView iv = (ImageView) convertView.findViewById(R.id.groupIcon);
		Cursor group = (Cursor) super.getGroup(groupPosition);
		if (!group.isClosed())
		{
			TextView noteCount = (TextView)convertView.findViewById(R.id.notecount);
			String nameString = group.getString(group
					.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME));
			noteCount.setText(String.valueOf(MainActivity.mDbHelper.findChilds(nameString).getCount()));
		}

		if (isExpanded)
		{
			iv.setImageResource(R.drawable.btn_browser2);
		} else
		{
			iv.setImageResource(R.drawable.btn_browser);
		}

		return super.getGroupView(groupPosition, isExpanded, convertView,
				parent);
	}

	public Cursor getChildrenCursor(Cursor groupCursor) {
		// TODO Auto-generated method stub
		return MainActivity.mDbHelper.findChilds(groupCursor.getString(groupCursor
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_NAME)));
	}
}
