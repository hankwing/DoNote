package com.donote.widget;

import com.baidu.location.i;
import com.donote.activity.Draw;
import com.donote.activity.MainActivity;
import com.donote.activity.SplashActivity;
import com.donote.activity.WidgetActivity;
import com.donote.adapter.NoteDbAdapter;
import com.wxl.donote.R;
import com.wxl.donote.R.color;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class DoNoteWidgetProvider extends AppWidgetProvider {

	private static final String LEFT_NAME_ACTION = "com.donote.action.widget.prior";
	private static final String RIGHT_NAME_ACTION = "com.donote.action.widget.next";
	private static RemoteViews rv;

	public static long note_id;
	public static int note_style;
	public static boolean isdelete = true;

	public static NoteDbAdapter mDbHelper;

	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,int appWidgetId){

		rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

		//打开WidgetActivity
		Intent intentClick = new Intent(context,WidgetActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, 
				intentClick, 0);
		rv.setOnClickPendingIntent(R.id.widget_act, pendingIntent);

		//打开笔记
		Intent intentClick2 = new Intent(context,MainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("wxl", "widget");
		intentClick2.putExtras(bundle);
		PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, 
				intentClick2, 0);
		rv.setOnClickPendingIntent(R.id.text_body, pendingIntent2);

		Intent intentClick3 = new Intent(LEFT_NAME_ACTION);
		PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 0,
				intentClick3, 0);
		rv.setOnClickPendingIntent(R.id.btn_prior,pendingIntent3);

		Intent intentClick4 = new Intent(RIGHT_NAME_ACTION);
		PendingIntent pendingIntent4 = PendingIntent.getBroadcast(context, 0, 
				intentClick4, 0);
		rv.setOnClickPendingIntent(R.id.btn_next, pendingIntent4);

		appWidgetManager.updateAppWidget(appWidgetId, rv);
	}



	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onAppWidgetOptionsChanged(android.content.Context, android.appwidget.AppWidgetManager, int, android.os.Bundle)
	 */
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// TODO Auto-generated method stub
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onDeleted(android.content.Context, int[])
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		if (mDbHelper != null) 
			mDbHelper.close(); 
		super.onDeleted(context, appWidgetIds);
		Log.i("wxl","onDeleted");
	}

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onDisabled(android.content.Context)
	 */
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		Log.i("wxl","onDisabled");
	}


	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onEnabled(android.content.Context)
	 */
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		Log.i("wxl","onEnabled");
		mDbHelper = new NoteDbAdapter(context);
		mDbHelper.open();
		Cursor cursor =mDbHelper.getAllNotes();
		if (cursor != null) {
			cursor.moveToFirst();
			rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			rv.setTextViewText(R.id.text_title,cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)) );
			rv.setTextViewText(R.id.text_body, cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_CONTENT)));
			note_id = cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
			note_style = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE));
			cursor.close();
			mDbHelper.close();
		}else{
			if (rv == null) {
				rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			}
			rv.setTextViewText(R.id.text_title,"");
			rv.setTextViewText(R.id.text_body, "╮(╯_╰)╭还没有内容");
			note_id = 0;
		}
	}

	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		super.onReceive(context, intent);

		mDbHelper = new NoteDbAdapter(context);
		mDbHelper.open();

		if (rv == null) {
			rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		}

		Cursor cursor =mDbHelper.getAllNotes();

		if(cursor.getCount() == 0){
			rv.setTextViewText(R.id.text_title,"" );
			rv.setTextViewText(R.id.text_body, "无笔记");
			note_id = 0;
		}

		Log.i("wxl", String.valueOf(cursor.getCount()));

		if(cursor != null && cursor.getCount() != 0){
			cursor.moveToFirst();
			if (intent.getAction().equals(LEFT_NAME_ACTION) && note_id != 0 ){

				if(isdelete == false){
					for( int i = 0; i < cursor.getCount(); i++){

						if(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID))==note_id){
							if(cursor.moveToPrevious()){
								rv.setTextViewText(R.id.text_title,cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)) );
								rv.setTextViewText(R.id.text_body, cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_CONTENT)));
								note_id = cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
								note_style = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE));
							}
							break;
						}
						cursor.moveToNext();
					}
				}else{
					rv.setTextViewText(R.id.text_title,cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)) );
					rv.setTextViewText(R.id.text_body, cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_CONTENT)));
					note_id = cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
					note_style = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE));
				}
				cursor.close();
				isdelete = false;

			}

			if (intent.getAction().equals(RIGHT_NAME_ACTION) && note_id !=0  ){
				Log.i("wxl","right");
				if(isdelete == false){
					for( int i = 0; i < cursor.getCount(); i++){

						if(cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID))==note_id){
							if(cursor.moveToNext()){
								rv.setTextViewText(R.id.text_title,cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)) );
								rv.setTextViewText(R.id.text_body, cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_CONTENT)));
								note_id = cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
								note_style = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE));
							}
							break;
						}
						cursor.moveToNext();
					}
				}
				else{
					rv.setTextViewText(R.id.text_title,cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)) );
					rv.setTextViewText(R.id.text_body, cursor.getString(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_CONTENT)));
					note_id = cursor.getLong(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_ROWID));
					note_style = cursor.getInt(cursor.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE));
				}
				cursor.close();
				isdelete = false;
			}
		}



		if (mDbHelper != null) 
			mDbHelper.close(); 

		AppWidgetManager appWidgetManger = AppWidgetManager
				.getInstance(context);
		int[] appIds = appWidgetManger.getAppWidgetIds(new ComponentName(
				context, com.donote.widget.DoNoteWidgetProvider.class));
		appWidgetManger.updateAppWidget(appIds, rv);
	}


	/* (non-Javadoc)
	 * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context, android.appwidget.AppWidgetManager, int[])
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.i("wxl","startWidget2");
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

}
