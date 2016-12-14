package com.donote.activity;

import java.util.ArrayList;
import java.util.HashMap;
import com.baidu.mobstat.StatService;
import com.donote.adapter.MyCursorTreeAdapter;
import com.donote.adapter.MySimpleCursorAdapter;
import com.donote.adapter.NoteDbAdapter;
import com.donote.alarm.AlarmSound;
import com.donote.util.MyShakeBootService;
import com.wxl.donote.R;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NoteSet extends Activity {

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

	private ArrayList<HashMap<String, Object>> items;
	private SeekBar shake_seekBar;
	private RadioButton mRadio1;
	private RadioButton mRadio2;
	private RadioButton smallTextSize;
	private RadioButton normalTextSize;
	private RadioButton largeTextSize;
	private RadioButton hugeTextSize;
	private String oldWayString;
	private int oldTextSize;
	boolean flag = false;
	private CheckBox sheckBox;
	private CheckBox sheckbootBox;
	private int shakeChecked;
	private int shakeBootChecked;
	public static String pathPicture = Environment
			.getExternalStorageDirectory().getPath()
			+ "/"
			+ "DoNote"
			+ "/"
			+ "picture" + "/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		sheckBox = (CheckBox) findViewById(R.id.set_shake);
		sheckbootBox = (CheckBox) findViewById(R.id.set_shake_boot);
		SharedPreferences shakeInfo = getSharedPreferences("shake_info", 0);
		shakeChecked = shakeInfo.getInt("isshake", 1);
		shakeBootChecked = shakeInfo.getInt("isshakeboot", 1);
		shake_seekBar = (SeekBar) findViewById(R.id.shake_seekbar);
		if (shakeChecked == 1) {
			sheckBox.setChecked(true);
		} else {
			sheckBox.setChecked(false);
		}
		if (shakeBootChecked == 1) {
			sheckbootBox.setChecked(true);
		} else {
			sheckbootBox.setChecked(false);
		}
		
		sheckBox.setOnCheckedChangeListener(new ShakeCheckedChangeListener());
		sheckbootBox
				.setOnCheckedChangeListener(new ShakeCheckedBootChangeListener());

		String[] itemStrings = new String[11];
		itemStrings[0] = getResources().getString(R.string.alarm_ring);
		String[] itemStrings2 = new String[11];
		itemStrings2[0] = getResources().getString(R.string.sort_way);
		itemStrings2[1] = getResources().getString(R.string.font_size);
		itemStrings2[2] = getResources().getString(R.string.background);
		// itemStrings2[3] = "意见反馈";
		// itemStrings2[4] = "帮助";

		items = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < 1; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("item", itemStrings[i]);
			items.add(item);
		}

		SimpleAdapter saImageItems = new SimpleAdapter(this, items,// 数据来源
				R.layout.setting_lists, new String[] { "item" },
				// 分别对应view 的id
				new int[] { R.id.item });

		ArrayList<HashMap<String, Object>> items2 = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < 3; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("item", itemStrings2[i]);
			items2.add(item);
		}
		SimpleAdapter saImageItems2 = new SimpleAdapter(this, items2,// 数据来源
				R.layout.setting_lists, new String[] { "item" },
				// 分别对应view 的id
				new int[] { R.id.item });
		ListView alarmListView = (ListView) findViewById(R.id.list);
		alarmListView.setAdapter(saImageItems);
		alarmListView.setOnItemClickListener(new alarmItemClickListener());
		ListView otherListView = (ListView) findViewById(R.id.list2);
		otherListView.setAdapter(saImageItems2);
		otherListView.setOnItemClickListener(new ItemClickListener());
		final SharedPreferences settings = getSharedPreferences("shake_info", 0);
		if( settings.getInt("sensity", 0) == 0) {
			shake_seekBar.setProgress(80);
			settings.edit().putInt("sensity", (int) (4500*((100-(float)80)/100) + 500)).commit();
		}
		else {
			shake_seekBar.setProgress((int) (100-((float)(settings.getInt("sensity", 0) -500)/4500*100)));
		}
		
		shake_seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				settings.edit().putInt("sensity", (int) (4500*((100-(float)progress)/100) + 500)).commit();
			}
		});

	}

	private class ShakeCheckedChangeListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			SharedPreferences shakeInfo = getSharedPreferences("shake_info", 0);
			if (isChecked == true) {
				shakeInfo.edit().putInt("isshake", 1).commit();
			} else {
				shakeInfo.edit().putInt("isshake", 0).commit();
			}
		}
	}

	private class ShakeCheckedBootChangeListener implements
			OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			SharedPreferences shakeInfo = getSharedPreferences("shake_info", 0);
			if (isChecked == true) {
				shakeInfo.edit().putInt("isshakeboot", 1).commit();
				startService(new Intent(NoteSet.this, MyShakeBootService.class));
			} else {
				shakeInfo.edit().putInt("isshakeboot", 0).commit();
				stopService(new Intent(NoteSet.this, MyShakeBootService.class));
			}
		}
	}

	private class alarmItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(NoteSet.this, AlarmSound.class);
			startActivity(intent);
		}
	}

	public final class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position == 0) {

				SortshowDialog();// 选择排序方式
			} else if (position == 1) {

				chooseTextSizeDialog();// 字体大小
			} else if (position == 2) {
				Intent intent = new Intent(NoteSet.this, MainBackground.class);
				startActivity(intent);

			}
		}
	}

	protected void SortshowDialog() {
		// TODO Auto-generated method stub

		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.sort,
				(ViewGroup) findViewById(R.id.sortDialog));
		RadioGroup c = (RadioGroup) view.findViewById(R.id.sort_group);
		SharedPreferences settings = NoteSet.this.getSharedPreferences(
				"sortWay", 0);
		mRadio1 = (RadioButton) view.findViewById(R.id.sort_create_time);// 创建时间
		mRadio2 = (RadioButton) view.findViewById(R.id.sort_modify_time);// 修改时间

		int ID;
		if (settings.getString("sort", "").equals("")) {
			oldWayString = "c";
			ID = mRadio1.getId();
		} else if (settings.getString("sort", "").equals("c")) {
			oldWayString = "c";
			ID = mRadio1.getId();
		} else {
			oldWayString = "m";
			ID = mRadio2.getId();
		}
		c.check(ID);

		c.setOnCheckedChangeListener(mChangeRadio);
		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.choose_sort_way)).setView(view)
				.setPositiveButton(getResources().getString(R.string.confirm), new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						SharedPreferences settings = getSharedPreferences(
								"sortWay", 0);
						settings.edit().putString("sort", oldWayString)
								.commit();
						Toast.makeText(NoteSet.this, getResources().getString(R.string.modify_success), Toast.LENGTH_SHORT)
								.show();
						NoteDbAdapter.changeSortWay();
						MySimpleCursorAdapter.changeSortWay();
						MyCursorTreeAdapter.changeSortWay();

					}
				}).setNegativeButton(getResources().getString(R.string.cancel), new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				}).create().show();
	}

	protected void chooseTextSizeDialog() {
		// TODO Auto-generated method stub

		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.choosetextsize,
				(ViewGroup) findViewById(R.id.choosetextsize));
		RadioGroup textsize = (RadioGroup) view
				.findViewById(R.id.choosetextsize_group);
		SharedPreferences settings = NoteSet.this.getSharedPreferences(
				"textsize", 0);
		smallTextSize = (RadioButton) view.findViewById(R.id.small_textsize);// 小
		normalTextSize = (RadioButton) view.findViewById(R.id.normal_textsize);// 正常
		largeTextSize = (RadioButton) view.findViewById(R.id.large_textsize);
		hugeTextSize = (RadioButton) view.findViewById(R.id.huge_textsize);

		int ID;
		if (settings.getInt("size", 1) == 0) {
			oldTextSize = 0;
			ID = smallTextSize.getId();
		} else if (settings.getInt("size", 1) == 1) {
			oldTextSize = 1;
			ID = normalTextSize.getId();
		} else if (settings.getInt("size", 1) == 2) {
			oldTextSize = 2;
			ID = largeTextSize.getId();
		} else {
			oldTextSize = 3;
			ID = hugeTextSize.getId();
		}
		textsize.check(ID);

		textsize.setOnCheckedChangeListener(chooseTextSize);
		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.choose_font_size)).setView(view)
				.setPositiveButton(getResources().getString(R.string.confirm), new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						SharedPreferences settings = getSharedPreferences(
								"textsize", 0);
						settings.edit().putInt("size", oldTextSize).commit();
						Toast.makeText(NoteSet.this,getResources().getString(R.string.modify_success), Toast.LENGTH_SHORT)
								.show();

					}
				}).setNegativeButton(getResources().getString(R.string.cancel), new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				}).create().show();
	}

	private RadioGroup.OnCheckedChangeListener mChangeRadio = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == mRadio1.getId()) {
				oldWayString = "c";
			} else if (checkedId == mRadio2.getId()) {
				oldWayString = "m";
			}
		}
	};

	private RadioGroup.OnCheckedChangeListener chooseTextSize = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (checkedId == smallTextSize.getId()) {
				MainActivity.textsize = 0;
				oldTextSize = 0;
			} else if (checkedId == normalTextSize.getId()) {
				MainActivity.textsize = 1;
				oldTextSize = 1;
			} else if (checkedId == largeTextSize.getId()) {
				MainActivity.textsize = 2;
				oldTextSize = 2;
			} else { 
				MainActivity.textsize = 3;
				oldTextSize = 3;
			}
		}
	};

	/* (non-Javadoc)
	 * @see android.app.Activity#finish()
	 */
	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}
}
