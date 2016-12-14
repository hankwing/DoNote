package com.donote.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.mobstat.StatService;
import com.wxl.donote.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TableEdit extends Activity {

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

	class Unit {
		public int id;
		public String text;

		Unit(int id, String text) {
			this.id = id;
			this.text = text;
		}

		/**
		 * @return the id
		 */
		public int getId() {
			return id;
		}

		/**
		 * @param id
		 *            the id to set
		 */
		public void setId(int id) {
			this.id = id;
		}

		/**
		 * @return the text
		 */
		public String getText() {
			return text;
		}

		/**
		 * @param text
		 *            the text to set
		 */
		public void setText(String text) {
			this.text = text;
		}

	}

	private LinearLayout canEditLayout;
	private String content = "";
	private String title = "";
	private int id;
	private boolean background = true;
	private static List<Unit> tableList;
	private static List<ImageButton> rowlist;
	private static List<ImageButton> columnlist;
	private Bundle extrasBundle = null;
	private boolean isdelete = false;
	private Time time = new Time();
	private int k = 1;
	private int columns;
	private LinearLayout table;
	private int rows;
	private String tString = null;
	private boolean isadd = true;
	private Button table_add;
	private Button table_delete;
	private Button table_save;
	// TextView TableWidth;
	private EditText titleText;
	private boolean iswrite = false;
	private TableLayout tableNote = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		time.setToNow();
		tString = String.valueOf(time.year) + String.valueOf(time.month)
				+ String.valueOf(time.monthDay);
		setContentView(R.layout.activity_table);
		tableList = new ArrayList<Unit>();
		rowlist = new ArrayList<ImageButton>();
		columnlist = new ArrayList<ImageButton>();
		canEditLayout = (LinearLayout) findViewById(R.id.edit_table);
		table_add = (Button) this.findViewById(R.id.table_add);
		table_delete = (Button) this.findViewById(R.id.table_delete);
		table_save = (Button) this.findViewById(R.id.table_save);
		titleText = (EditText) this.findViewById(R.id.table_title);
		tableNote = (TableLayout) this.findViewById(R.id.tableNote);
		table = (LinearLayout) findViewById(R.id.tableView);
		extrasBundle = getIntent().getExtras();
		iswrite = extrasBundle.getBoolean("iswrite");
		content = extrasBundle.getString("content");
		id = extrasBundle.getInt("id");
		if (extrasBundle.getInt("can_write") == 2)
		{
			canEditLayout.setVisibility(View.GONE);
		}
		if (iswrite == true)
		{
			if (content != null)
			{

				Pattern titlePattern = Pattern
						.compile("(<\\-\\-title\\:(.*?)\\-\\-\\>)");
				Matcher titleMatcher = titlePattern.matcher(content);
				while (titleMatcher.find())
				{
					String idString = titleMatcher.group();
					title = idString.substring(
							idString.indexOf("<--title:") + 9,
							idString.indexOf("-->"));
				}

				titleText.setText(title);

				Pattern rowPattern = Pattern
						.compile("(<\\-\\-row\\:(.*?)\\-\\-\\>)");
				Matcher rowMatcher = rowPattern.matcher(content);
				while (rowMatcher.find())
				{
					String idString = rowMatcher.group();
					rows = Integer.valueOf(idString.substring(
							idString.indexOf("<--row:") + 7,
							idString.indexOf("-->")));
				}

				Pattern columnPattern = Pattern
						.compile("(<\\-\\-column\\:(.*?)\\-\\-\\>)");
				Matcher columnMatcher = columnPattern.matcher(content);
				while (columnMatcher.find())
				{
					String idString = columnMatcher.group();
					columns = Integer.valueOf(idString.substring(
							idString.indexOf("<--column:") + 10,
							idString.indexOf("-->")));
				}

				Pattern unitPattern = Pattern
						.compile("(<\\-\\-unit\\:(.*?)\\-\\-\\>)");
				Matcher unitMatcher = unitPattern.matcher(content);
				while (unitMatcher.find())
				{
					String idString = unitMatcher.group();
					String temp = idString.substring(
							idString.indexOf("<--unit:") + 8,
							idString.indexOf("-->"));
					Unit unit = new Unit(Integer.valueOf(tString) + k, temp);
					tableList.add(unit);
					k++;
				}
			}
		} else if (iswrite == false)
		{
			columns = extrasBundle.getInt("column");
			rows = extrasBundle.getInt("row");
		}
		if (columns == 0 || rows == 0)
		{
			iswrite = false;
			initTable(3, 4);
		} else
		{
			initTable(columns, rows);
		}
		table_add.setOnClickListener(new TableListener());  
		table_delete.setOnClickListener(new TableListener());
		table_save.setOnClickListener(new TableListener());
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		// 此处相当于布局文件中的Android:layout_gravity属性
		lp.gravity = Gravity.CENTER_HORIZONTAL;
		table.setLayoutParams(lp);
		super.onCreate(savedInstanceState);
	}

	public void initTable(int column, int row) {
		tableNote.removeAllViews();
		int count = 0;
		for (int i = 0; i < row; i++)
		{
			TableRow rownote = new TableRow(this);
			List<Unit> rtemplist = new ArrayList<Unit>();

			for (int j = 0; j < column; j++, count++)
			{

				TextView textView = new TextView(this);
				textView.setTextSize(21);
				textView.setPadding(3, 3, 3, 3);
				textView.setGravity(Gravity.CENTER);
				if (iswrite == true)
				{
					Unit unit = tableList.get(count);
					rtemplist.add(unit);
					textView.setId(unit.getId());
					textView.setText(unit.getText());
				} else if (iswrite == false)
				{
					textView.setId(Integer.valueOf(tString) + k);
					k++;
					textView.setText("");
					Unit unit = new Unit(textView.getId(), textView.getText()
							.toString());
					tableList.add(unit);
					rtemplist.add(unit);
				}
				if (background == true)
				{
					textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left1);
				}
				if (background == false)
				{
					textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left2);
				}
				if (extrasBundle.getInt("can_write") != 2)
				{
					textView.setOnClickListener(new TableTextListener(textView
							.getId(), String.valueOf(textView.getText())));
				}
				rownote.addView(textView);
			}
			if (background == true)
			{
				background = false;
			} else
			{
				background = true;
			}

			ImageButton cancel = new ImageButton(getApplicationContext());
			cancel.setScaleType(ScaleType.CENTER);
			Drawable drawable = this.getResources().getDrawable(
					R.drawable.ic_table_delete4);
			cancel.setImageDrawable(drawable);
			cancel.setBackgroundColor(Color.WHITE);

			cancel.setVisibility(View.GONE);

			rowlist.add(cancel);
			rownote.addView(cancel);
			cancel.setOnClickListener(new RowCancelListener(rownote, rtemplist));
			tableNote.addView(rownote);

		}

		TableRow rownote = new TableRow(this);
		for (int i = 0; i < column; i++)
		{

			ImageButton cancel = new ImageButton(getApplicationContext());

			cancel.setScaleType(ScaleType.CENTER);

			Drawable drawable = this.getResources().getDrawable(
					R.drawable.ic_table_delete4);

			cancel.setImageDrawable(drawable);

			cancel.setBackgroundColor(Color.WHITE);

			cancel.setVisibility(View.GONE);

			columnlist.add(cancel);
			cancel.setOnClickListener(new ColumnCancelListner(i));

			rownote.addView(cancel);

		}

		tableNote.addView(rownote);

		columns = column;
		rows = row;

	}

	// 删除一列
	class ColumnCancelListner implements OnClickListener {

		int column;

		public ColumnCancelListner(int column) {
			this.column = column;
		}

		@Override
		public void onClick(View v) {
			// 删除列以后重新建立表格

			tableNote.removeAllViews();
			int count = 0;

			List<Unit> tempTable = new ArrayList<Unit>();
			List<ImageButton> rowButton = new ArrayList<ImageButton>();
			List<ImageButton> columnButton = new ArrayList<ImageButton>();

			for (int i = 0; i < rows; i++)
			{
				TableRow rownote = new TableRow(TableEdit.this);
				List<Unit> templist = new ArrayList<Unit>();

				for (int j = 0; j < columns; j++, count++)
				{
					if (j != this.column)
					{
						TextView textView = new TextView(TableEdit.this);
						textView.setTextSize(21);
						textView.setPadding(3, 3, 3, 3);
						textView.setGravity(Gravity.CENTER);

						Unit unit = tableList.get(count);
						tempTable.add(unit);
						templist.add(unit);
						textView.setId(unit.getId());
						textView.setText(unit.getText());

						if (background == true)
						{
							textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left1);
						}
						if (background == false)
						{
							textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left2);
						}

						textView.setOnClickListener(new TableTextListener(
								textView.getId(), String.valueOf(textView
										.getText())));
						rownote.addView(textView);
					}
				}
				
				if (background == true)
				{
					background = false;
				} else
				{
					background = true;
				}

				ImageButton cancel = new ImageButton(getApplicationContext());
				cancel.setPadding(3, 3, 3, 3);
				cancel.setScaleType(ScaleType.CENTER);
				Drawable drawable = TableEdit.this.getResources().getDrawable(
						R.drawable.ic_table_delete4);

				cancel.setImageDrawable(drawable);

				cancel.setBackgroundColor(Color.WHITE);
				rowButton.add(cancel);
				rownote.addView(cancel);
				cancel.setOnClickListener(new RowCancelListener(rownote,
						templist));
				tableNote.addView(rownote);
			}

			columns--;

			TableRow rownote = new TableRow(TableEdit.this);

			for (int i = 0; i < columns; i++)
			{

				ImageButton cancel = new ImageButton(getApplicationContext());
				cancel.setPadding(3, 3, 3, 3);
				cancel.setScaleType(ScaleType.CENTER);
				Drawable drawable = TableEdit.this.getResources().getDrawable(
						R.drawable.ic_table_delete4);

				cancel.setImageDrawable(drawable);

				cancel.setBackgroundColor(Color.WHITE);
				columnButton.add(cancel);
				cancel.setOnClickListener(new ColumnCancelListner(i));
				rownote.addView(cancel);

			}

			tableNote.addView(rownote);
			if ((MainActivity.Width - tableNote.getWidth()) > 0)
			{
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				// 此处相当于布局文件中的Android:layout_gravity属性
				lp.gravity = Gravity.CENTER_HORIZONTAL;
				table.setLayoutParams(lp);
			}
			tableList = tempTable;
			rowlist = rowButton;
			columnlist = columnButton;

			table_add.setTextColor(Color.GRAY);
			table_add.setClickable(false);

			table_save.setTextColor(Color.GRAY);
			table_save.setClickable(false);

			isdelete = true;

			if (columns == 0)
			{
				tableNote.removeAllViews();
			}

		}
	}

	// 删除一行
	class RowCancelListener implements OnClickListener {

		List<Unit> unitlist;
		TableRow table_row;

		public RowCancelListener(TableRow table_row, List<Unit> unitlist) {
			this.unitlist = unitlist;
			this.table_row = table_row;
		}

		@Override
		public void onClick(View v) {
			// 同时要删除相应的Unit
			for (int i = 0; i < unitlist.size(); i++)
			{
				tableList.remove(unitlist.get(i));
			}
			tableNote.removeView(this.table_row);
			if (rows > 0)
				rows--;
			if (rows == 0)
			{
				columns = 0;
				tableNote.removeAllViews();

			}

			if (tableNote.getWidth() < MainActivity.Width)
			{
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				// 此处相当于布局文件中的Android:layout_gravity属性
				lp.gravity = Gravity.CENTER_HORIZONTAL;
				table.setLayoutParams(lp);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 7 && resultCode != RESULT_CANCELED)
		{

			Bundle bundle = data.getExtras();

			if (bundle != null)
			{
				TextView textView = new TextView(getApplicationContext());

				textView = (TextView) this.findViewById(bundle.getInt("id"));

				textView.setText(bundle.getString("text"));

				for (int i = 0; i < tableList.size(); i++)
				{
					Unit unit = tableList.get(i);
					if (unit.getId() == bundle.getInt("id"))
					{
						unit.setText(bundle.getString("text"));
						tableList.set(i, unit);
					}
				}
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	class TableTextListener implements OnClickListener {
		int id;
		String text = null;

		TableTextListener(int id, String text) {
			this.id = id;
			this.text = text;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			TextView textView = (TextView) TableEdit.this.findViewById(this.id);

			if (textView != null)
			{
				this.text = textView.getText().toString();
			}

			Intent intent = new Intent(TableEdit.this, TableText.class);
			Bundle bundle = new Bundle();
			bundle.putString("text", this.text);
			bundle.putInt("id", id);
			intent.putExtras(bundle);
			startActivityForResult(intent, 7);

		}

	}

	class TableListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
			case R.id.table_add:
				// 添加一行
				if (isadd == true)
				{
					isadd = false;
					table_add.setText(getResources().getString(
							R.string.return_to_mainview));
					table_delete.setText(getResources().getString(
							R.string.add_row));
					table_save.setText(getResources().getString(
							R.string.add_column));

				} else if (isadd == false)
				{
					isadd = true;
					table_add.setText(getResources().getString(R.string.add));
					table_delete.setText(getResources().getString(
							R.string.delete));
					table_save.setText(getResources().getString(R.string.save));
				}

				break;
			case R.id.table_delete:

				if (isadd == true)
				{

					if (isdelete == true)
					{
						// 删除模式
						table_delete.setText(getResources().getString(
								R.string.delete));

						for (int i = 0; i < rowlist.size(); i++)
						{

							ImageButton rowbutton = rowlist.get(i);
							rowbutton.setVisibility(View.GONE);

						}

						for (int j = 0; j < columnlist.size(); j++)
						{
							ImageButton colButton = columnlist.get(j);
							colButton.setVisibility(View.GONE);
						}

						table_add.setTextColor(Color.WHITE);
						table_add.setClickable(true);

						table_save.setTextColor(Color.WHITE);
						table_save.setClickable(true);

						isdelete = false;

					} else
					{

						table_delete.setText(getResources().getText(
								R.string.return_to_mainview));

						for (int i = 0; i < rowlist.size(); i++)
						{
							ImageButton button = rowlist.get(i);
							button.setVisibility(View.VISIBLE);

						}

						for (int j = 0; j < columnlist.size(); j++)
						{
							ImageButton button = columnlist.get(j);
							button.setVisibility(View.VISIBLE);
						}

						table_add.setTextColor(Color.GRAY);
						table_add.setClickable(false);

						table_save.setTextColor(Color.GRAY);
						table_save.setClickable(false);

						isdelete = true;
					}
				} else
				{

					tableNote.removeAllViews();
					int count = 0;

					List<Unit> tempTable = new ArrayList<Unit>();
					List<ImageButton> rowButton = new ArrayList<ImageButton>();
					List<ImageButton> columnButton = new ArrayList<ImageButton>();

					for (int i = 0; i < rows; i++)
					{
						TableRow rownote = new TableRow(TableEdit.this);
						List<Unit> templist = new ArrayList<Unit>();

						for (int j = 0; j < columns; j++, count++)
						{

							TextView textView = new TextView(TableEdit.this);
							textView.setTextSize(21);
							textView.setPadding(3, 3, 3, 3);
							textView.setGravity(Gravity.CENTER);

							Unit unit = tableList.get(count);
							tempTable.add(unit);
							templist.add(unit);
							textView.setId(unit.getId());
							textView.setText(unit.getText());

							if (background == true)
							{
								textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left1);
							}
							if (background == false)
							{
								textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left2);
							}
							textView.setOnClickListener(new TableTextListener(
									textView.getId(), String.valueOf(textView
											.getText())));
							rownote.addView(textView);
						}

						if (background == true)
						{
							background = false;
						} else
						{
							background = true;
						}

						ImageButton cancel = new ImageButton(
								getApplicationContext());
						cancel.setPadding(3, 3, 3, 3);
						cancel.setScaleType(ScaleType.CENTER);
						Drawable drawable = TableEdit.this.getResources()
								.getDrawable(R.drawable.ic_table_delete4);

						cancel.setImageDrawable(drawable);

						cancel.setBackgroundColor(Color.WHITE);
						cancel.setVisibility(View.GONE);
						rowButton.add(cancel);
						rownote.addView(cancel);
						cancel.setOnClickListener(new RowCancelListener(
								rownote, templist));
						tableNote.addView(rownote);

					}
					tableList = tempTable;
					rowlist = rowButton;

					// new row
					TableRow rownote = new TableRow(TableEdit.this);
					List<Unit> templist = new ArrayList<Unit>();
					for (int i = 0; i < columns; i++)
					{

						TextView textView = new TextView(TableEdit.this);
						textView.setId(Integer.valueOf(tString) + k);
						textView.setTextSize(21);
						textView.setPadding(3, 3, 3, 3);
						textView.setGravity(Gravity.CENTER);
						textView.setText("");
						k++;
						Unit unit = new Unit(textView.getId(), textView
								.getText().toString());
						tableList.add(unit);
						templist.add(unit);
						textView.setOnClickListener(new TableTextListener(
								textView.getId(), String.valueOf(textView
										.getText())));
						if (background == true)
						{
							textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left1);
						}
						if (background == false)
						{
							textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left2);
						}
						rownote.addView(textView);
					}

					ImageButton cancel = new ImageButton(
							getApplicationContext());
					cancel.setPadding(3, 3, 3, 3);
					cancel.setScaleType(ScaleType.CENTER);
					Drawable drawable = TableEdit.this.getResources()
							.getDrawable(R.drawable.ic_table_delete4);

					cancel.setImageDrawable(drawable);
					cancel.setBackgroundColor(Color.WHITE);
					cancel.setVisibility(View.GONE);
					rowlist.add(cancel);
					rownote.addView(cancel);
					cancel.setOnClickListener(new RowCancelListener(rownote,
							templist));

					tableNote.addView(rownote);

					Drawable drawable2 = TableEdit.this.getResources()
							.getDrawable(R.drawable.ic_table_delete4);

					TableRow cancelRow = new TableRow(TableEdit.this);
					for (int i = 0; i < columns; i++)
					{

						ImageButton cancelButton = new ImageButton(
								getApplicationContext());

						cancelButton.setScaleType(ScaleType.CENTER);

						cancelButton.setImageDrawable(drawable2);

						cancelButton.setBackgroundColor(Color.WHITE);

						cancelButton.setVisibility(View.GONE);

						cancelButton
								.setOnClickListener(new ColumnCancelListner(i));

						columnButton.add(cancelButton);

						cancelRow.addView(cancelButton);

					}
					columnlist = columnButton;

					tableNote.addView(cancelRow);
					if ((MainActivity.Width - tableNote.getWidth()) < 50)
					{
						FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.MATCH_PARENT);
						// 此处相当于布局文件中的Android:layout_gravity属性
						table.setLayoutParams(lp);
						table.setPadding(20, 0, 20, 0);
					}
					rows++;
				}
				break;

			case R.id.table_save:
				if (isadd == true)
				{
					content = "";

					content = content + "<--title:"
							+ titleText.getText().toString() + "-->";

					content = content + "<--row:" + String.valueOf(rows)
							+ "-->";

					content = content + "<--column:" + String.valueOf(columns)
							+ "-->";

					for (int i = 0; i < tableList.size(); i++)
					{
						Unit unit = tableList.get(i);
						content = content + "<--unit:" + unit.getText() + "-->";
					}

					Bundle bundle = new Bundle();
					bundle.putString("content", content);
					bundle.putInt("id", id);

					TableEdit.this.setResult(14, TableEdit.this.getIntent()
							.putExtras(bundle));
					TableEdit.this.finish();
				} else
				{
					// 添加列
					// 由于添加列比较麻烦所以采用从新构造表格实现添加列的效果

					tableNote.removeAllViews();
					int count = 0;

					List<Unit> tempTable = new ArrayList<Unit>();
					List<ImageButton> rowButton = new ArrayList<ImageButton>();
					List<ImageButton> columnButton = new ArrayList<ImageButton>();

					for (int i = 0; i < rows; i++)
					{
						TableRow rownote = new TableRow(TableEdit.this);
						List<Unit> templist = new ArrayList<Unit>();

						for (int j = 0; j < columns; j++, count++)
						{

							TextView textView = new TextView(TableEdit.this);
							textView.setTextSize(21);
							textView.setPadding(3, 3, 3, 3);
							textView.setGravity(Gravity.CENTER);

							Unit unit = tableList.get(count);
							tempTable.add(unit);
							templist.add(unit);
							textView.setId(unit.getId());
							textView.setText(unit.getText());

							if (background == true)
							{
								textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left1);
							}
							if (background == false)
							{
								textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left2);
							}
							textView.setOnClickListener(new TableTextListener(
									textView.getId(), String.valueOf(textView
											.getText())));
							rownote.addView(textView);
						}

						// new
						TextView textView = new TextView(TableEdit.this);
						textView.setId(Integer.valueOf(tString) + k);
						textView.setTextSize(21);
						textView.setPadding(3, 3, 3, 3);
						textView.setGravity(Gravity.CENTER);
						textView.setText("");
						k++;
						Unit unit = new Unit(textView.getId(), textView
								.getText().toString());
						tempTable.add(unit);
						templist.add(unit);

						if (background == true)
						{
							textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left1);
						}
						if (background == false)
						{
							textView.setBackgroundResource(R.drawable.bg_tbl_row_down_left2);
						}

						if (background == true)
						{
							background = false;
						} else
						{
							background = true;
						}

						textView.setOnClickListener(new TableTextListener(
								textView.getId(), String.valueOf(textView
										.getText())));

						rownote.addView(textView);

						ImageButton cancel = new ImageButton(
								getApplicationContext());
						cancel.setPadding(3, 3, 3, 3);
						cancel.setScaleType(ScaleType.CENTER);
						Drawable drawable = TableEdit.this.getResources()
								.getDrawable(R.drawable.ic_table_delete4);

						cancel.setImageDrawable(drawable);

						cancel.setBackgroundColor(Color.WHITE);
						cancel.setVisibility(View.GONE);
						rowButton.add(cancel);
						rownote.addView(cancel);
						cancel.setOnClickListener(new RowCancelListener(
								rownote, templist));
						tableNote.addView(rownote);
					}

					columns++;

					Drawable drawable2 = TableEdit.this.getResources()
							.getDrawable(R.drawable.ic_table_delete4);

					TableRow addRow = new TableRow(TableEdit.this);
					for (int i = 0; i < columns; i++)
					{

						ImageButton addButton = new ImageButton(
								getApplicationContext());

						addButton.setScaleType(ScaleType.CENTER);

						addButton.setImageDrawable(drawable2);

						addButton.setBackgroundColor(Color.WHITE);

						addButton.setVisibility(View.GONE);

						columnButton.add(addButton);
						addButton
								.setOnClickListener(new ColumnCancelListner(i));

						addRow.addView(addButton);

					}

					tableNote.addView(addRow);
					if ((MainActivity.Width - tableNote.getWidth()) < 50)
					{
						FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.MATCH_PARENT);
						// 此处相当于布局文件中的Android:layout_gravity属性
						table.setLayoutParams(lp);
						table.setPadding(20, 0, 20, 0);
					}
					tableList = tempTable;
					rowlist = rowButton;
					columnlist = columnButton;

				}
				break;

			default:
				break;
			}

		}
	}

}
