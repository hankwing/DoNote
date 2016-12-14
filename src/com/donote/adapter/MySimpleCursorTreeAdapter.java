package com.donote.adapter;

/**
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2012 Mitchell Pellegrino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.donote.activity.MainActivity;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;

/**
 * An easier adapter of groups and their children. The childData must be
 * synchronized with the order of the groups in groupData. All you have to do to
 * use this class is implement the newGroupView, newChildView, bindGroupView,
 * and bindChildView methods, like you would a list view adapter
 */
public abstract class MySimpleCursorTreeAdapter extends
		BaseExpandableListAdapter {
	private Cursor groupData;
	private Context mContext;
	private LayoutInflater inflater;
	
	public MySimpleCursorTreeAdapter(Context context, Cursor groupData) {
		// TODO Auto-generated constructor stub
		this.groupData = groupData;
		mContext = context;
		this.inflater = LayoutInflater.from(context);
	}

	public Object getChild(int groupPosition, int childPosition) {
		groupData.moveToPosition(groupPosition);
		Cursor note = MainActivity.mDbHelper.findChilds(groupData.getString(groupData
				.getColumnIndexOrThrow("name")));
		note.moveToPosition(childPosition);
		return note;
	}

	public long getChildId(int groupPosition, int childPosition) {
		groupData.moveToPosition(groupPosition);
		Cursor note = MainActivity.mDbHelper.findChilds(groupData.getString(groupData
				.getColumnIndexOrThrow("name")));
		note.moveToPosition(childPosition);
		Long childId = note.getLong(note.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
		note.close();
		return childId;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		groupData.moveToPosition(groupPosition);
		Cursor note = MainActivity.mDbHelper.findChilds(groupData.getString(groupData
				.getColumnIndexOrThrow("name")));
		note.moveToPosition(childPosition);
		//myAnimation_Alpha = AnimationUtils.loadAnimation(mContext,R.anim.touming);
		//myAnimation_Alpha.setDuration(1000);
		View v;
		if (convertView == null)
		{
			v = newChildView(mContext, note, isLastChild, parent);
			
		} else
		{
			v = convertView;
		}
		bindChildView(groupPosition, childPosition, v, mContext, note,
				isLastChild);
		//note.close();
		return v;
	}

	/**
	 * Instantiates a new View for a child.
	 * 
	 * @param parent
	 *            The eventual parent of this new View.
	 * @return A new child View
	 */
	protected abstract View newChildView(Context context, Cursor cursor,
			boolean isLastChild, ViewGroup parent);

	/**
	 * @param childPosition
	 *            Position of the child in the childData list
	 * @param groupPosition
	 *            Position of the child's group in the groupData list
	 * @param v
	 *            The view to bind data to
	 * @param parent
	 *            The eventual parent of v.
	 */
	protected abstract void bindChildView(int groupPosition, int childPosition,
			View view, Context context, Cursor cursor, boolean isLastChild);

	public int getChildrenCount(int groupPosition) {
		groupData.moveToPosition(groupPosition);
		Cursor note = MainActivity.mDbHelper.findChilds( groupData.getString(groupData
				.getColumnIndexOrThrow("name")) );
		int count = note.getCount();
		note.close();
		return count;
	}
	
	public Object getGroup(int groupPosition) {
		groupData.moveToPosition(groupPosition);
		Cursor groupCursor = groupData;
		return groupCursor;
	}

	public int getGroupCount() {
		return groupData.getCount();
	}
	
	public int getGroupAllCount() {
		int n = getGroupCount() -1;
		int childCount = 0;
		while( n> -1) {
			childCount += getChildrenCount(n);
			n --;
		}
		return childCount + 10;
	}

	public long getGroupId(int groupPosition) {
		groupData.moveToPosition(groupPosition);
		return groupData.getLong(groupData
				.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		groupData.moveToPosition(groupPosition);
		View v;
		if (convertView == null)
		{
			v = newGroupView(mContext, groupData, isExpanded, parent);
		} else
		{
			v = convertView;
		}
		bindGroupView(groupPosition, v, mContext, groupData, isExpanded);
		return v;
	}

	/**
	 * Instantiates a new View for a group.
	 * 
	 * @param isExpanded
	 *            Whether the group is currently expanded.
	 * @param parent
	 *            The eventual parent of this new View.
	 * @return A new group View
	 */
	protected abstract View newGroupView(Context context, Cursor cursor,
			boolean isExpanded, ViewGroup parent);

	/**
	 * @param groupPosition
	 *            Position of the group in the groupData list
	 * @param isExpanded
	 *            Whether the group is currently expanded.
	 * @param v
	 *            The view to bind data to
	 * @param parent
	 *            The eventual parent of v.
	 */
	protected abstract void bindGroupView(int groupPosition, View view, Context context,
			Cursor cursor, boolean isExpanded);

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

	/* (non-Javadoc)
	 * @see android.widget.BaseExpandableListAdapter#notifyDataSetChanged()
	 */
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		groupData = MainActivity.mDbHelper.getAllCatagory();
		super.notifyDataSetChanged();
	}

	public LayoutInflater getInflater() {
		return inflater;
	}

}