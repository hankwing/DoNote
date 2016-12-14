package com.donote.alarm;

import com.baidu.mobstat.StatService;
import com.donote.activity.MainActivity;
import com.wxl.donote.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AlarmSound extends Activity {

	private String music_uri= "";
	private ListView listView = null;
	private Cursor cursor;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_file);
		ContentResolver mResolver = AlarmSound.this.getContentResolver();
		cursor = mResolver
				.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						new String[] { MediaStore.Audio.Media.TITLE,
								MediaStore.Audio.Media._ID,
								MediaStore.Audio.Media.DATA }, null, null, null);
		listView = (ListView) findViewById(R.id.file_list);

		@SuppressWarnings("deprecation")
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.music_row, cursor,
				new String[] { MediaStore.Audio.Media.TITLE, },
				new int[] { R.id.file_item });

		listView.setAdapter(notes);

		listView.setOnItemClickListener(new ItemClickListener());
	}

	private class ItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			cursor.moveToPosition(position);
			music_uri = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
			SharedPreferences settings = getSharedPreferences(
					"alarm_sound_info", 0);
			settings.edit()
			.putString("alarm_info",music_uri)
			.commit();
			Toast.makeText(AlarmSound.this, getResources().getString(R.string.ring_succeed), Toast.LENGTH_SHORT).show();
			cursor.close();
			finish();

		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
}
