package com.donote.activity;


import com.baidu.mobstat.StatService;
import com.donote.adapter.MySimpleCursorAdapter;
import com.donote.adapter.MyWidgetCursorAdapter;
import com.donote.adapter.NoteDbAdapter;
import com.donote.widget.DoNoteWidgetProvider;
import com.wxl.donote.R;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetActivity extends Activity {

	ImageView back_red;
	ImageView back_green;
	ImageView back_blue;
	ImageView back_gray;

	private ListView listView;
	public static NoteDbAdapter mDbHelper;
	private Cursor mNoteCursor;
	public static MyWidgetCursorAdapter notes;
	RemoteViews views; 

	SharedPreferences sp;



	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		StatService.onPause(this);
		super.onPause();
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		StatService.onResume(this);
		super.onResume();
	}



	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget);
		listView = (ListView) this.findViewById(R.id.widget_list);
		back_blue = (ImageView) this.findViewById(R.id.Wid_back_blue);
		back_gray = (ImageView) this.findViewById(R.id.Wid_back_gray);
		back_green = (ImageView) this.findViewById(R.id.Wid_back_green);
		back_red = (ImageView) this.findViewById(R.id.Wid_back_red);

		//获取颜色的值
		sp = getSharedPreferences("widget_color", MODE_PRIVATE);
		if (sp!=null) {
			if(sp.getString("widget_back_color", "gray").equals("blue")){
				back_blue.setImageDrawable(getResources().getDrawable(R.drawable.widget_back_confirm));
			}
			if(sp.getString("widget_back_color", "gray").equals("red")){
				back_red.setImageDrawable(getResources().getDrawable(R.drawable.widget_back_confirm));
			}
			if(sp.getString("widget_back_color", "gray").equals("green")){
				back_green.setImageDrawable(getResources().getDrawable(R.drawable.widget_back_confirm));
			}
			if(sp.getString("widget_back_color", "gray").equals("gray")){
				back_gray.setImageDrawable(getResources().getDrawable(R.drawable.widget_back_confirm));
			}
		}else {
			back_gray.setImageDrawable(getResources().getDrawable(R.drawable.widget_back_confirm));
		}



		views = new RemoteViews(WidgetActivity.this.getPackageName(), R.layout.widget_layout);

		back_blue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editor editor = sp.edit();
				editor.putString("widget_back_color", "blue");
				editor.commit();
				back_blue.setImageDrawable(getResources().getDrawable(R.drawable.widget_back_confirm));
				back_green.setImageDrawable(null);
				back_red.setImageDrawable(null);
				back_gray.setImageDrawable(null);
				views.setInt(R.id.widget_back_title, "setBackgroundColor", Color.argb(255, 237, 240, 255));
				views.setInt(R.id.widget_back_content, "setBackgroundColor", Color.argb(255, 237, 240, 255));
				ComponentName widget  = new ComponentName(WidgetActivity.this,DoNoteWidgetProvider.class);
				AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
				manager.updateAppWidget(widget, views);
			}
		});
		back_red.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editor editor = sp.edit();
				editor.putString("widget_back_color", "red");
				editor.commit();
				back_red.setImageDrawable(getResources().getDrawable(R.drawable.widget_back_confirm));
				back_green.setImageDrawable(null);
				back_blue.setImageDrawable(null);
				back_gray.setImageDrawable(null);
				views.setInt(R.id.widget_back_title, "setBackgroundColor", Color.argb(255, 255, 237, 250));
				views.setInt(R.id.widget_back_content, "setBackgroundColor", Color.argb(255, 255, 237, 250));
				ComponentName widget  = new ComponentName(WidgetActivity.this,DoNoteWidgetProvider.class);
				AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
				manager.updateAppWidget(widget, views);
			}
		});
		back_green.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editor editor = sp.edit();
				editor.putString("widget_back_color", "green");
				editor.commit();
				back_green.setImageDrawable(getResources().getDrawable(R.drawable.widget_back_confirm));
				back_red.setImageDrawable(null);
				back_blue.setImageDrawable(null);
				back_gray.setImageDrawable(null);
				views.setInt(R.id.widget_back_title, "setBackgroundColor", Color.argb(255, 234, 255, 203));
				views.setInt(R.id.widget_back_content, "setBackgroundColor", Color.argb(255, 234, 255, 203));
				ComponentName widget  = new ComponentName(WidgetActivity.this,DoNoteWidgetProvider.class);
				AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
				manager.updateAppWidget(widget, views);
			}
		});
		back_gray.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editor editor = sp.edit();
				editor.putString("widget_back_color", "gray");
				editor.commit();
				back_gray.setImageDrawable(getResources().getDrawable(R.drawable.widget_back_confirm));
				back_green.setImageDrawable(null);
				back_blue.setImageDrawable(null);
				back_red.setImageDrawable(null);
				views.setInt(R.id.widget_back_title, "setBackgroundColor", Color.argb(255, 227, 225, 227));
				views.setInt(R.id.widget_back_content, "setBackgroundColor", Color.argb(255, 227, 225, 227));
				ComponentName widget  = new ComponentName(WidgetActivity.this,DoNoteWidgetProvider.class);
				AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
				manager.updateAppWidget(widget, views);
			}
		});
		
		if(MainActivity.mDbHelper == null){
			mDbHelper = new NoteDbAdapter(this);
			mDbHelper.open();
			mNoteCursor = mDbHelper.getAllNotes();
		}else {
			mNoteCursor = MainActivity.mDbHelper.getAllNotes();
		}
		
		
		notes = new MyWidgetCursorAdapter(WidgetActivity.this, mNoteCursor, "");
		listView.setAdapter(notes);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				Cursor c = null;
				if(mDbHelper!=null){
					c = mDbHelper.getAllNotes();
				}
				else{ 
					c = MainActivity.mDbHelper.getAllNotes();
				}

				c.moveToPosition(position);

				//改变
				views.setTextViewText(R.id.text_title,c.getString(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_TITLE)) );
				views.setTextViewText(R.id.text_body, c.getString(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_CONTENT)));
				DoNoteWidgetProvider.note_id = id;
				DoNoteWidgetProvider.note_style = c.getInt(c.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE));
				ComponentName widget  = new ComponentName(WidgetActivity.this,DoNoteWidgetProvider.class);
				AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
				manager.updateAppWidget(widget, views); 

				mNoteCursor.close();
				if(mDbHelper!=null){
					mDbHelper.close();
				}
				WidgetActivity.this.finish();
			}
		});
	}
}
