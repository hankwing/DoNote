package com.donote.activity;

import java.io.File;

import com.baidu.mobstat.StatService;
import com.donote.util.Scrawl;
import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.ColorPicker.OnColorChangedListener;
import com.larswerkman.colorpicker.OpacityBar;
import com.larswerkman.colorpicker.PaintSizeBar;
import com.larswerkman.colorpicker.SVBar;
import com.wxl.donote.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public class Draw extends Activity implements OnColorChangedListener{
	private Scrawl mView = null;
	private PopupWindow colorMenu;
	private Button draw_clear;
	private Button draw_save;
	private Button draw_back;
	private Button draw_color;
	private File file = null;
	private OpacityBar opacityBar;
	private Button button;
	private TextView text;
	private ColorPicker picker;
	private PaintSizeBar mPaintSizeBar;

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
		StatService.onResume(this);
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_draw);	
		mView = (Scrawl) this.findViewById(R.id.scrawl);
		mView.setFocusable(true);
		draw_back = (Button) this.findViewById(R.id.draw_back);
		draw_clear = (Button) this.findViewById(R.id.draw_clear);
		draw_save = (Button) this.findViewById(R.id.draw_save);
		draw_color = (Button) this.findViewById(R.id.draw_color);
		draw_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mView.backScrawl();
			}
		});

		draw_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mView.clearScrawl();
			}
		});

		draw_color.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showColorMenu();
			}
		}); 

		draw_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				file = mView.saveScrawl();
				if (file != null) {
					finishActivity();
				}
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (Scrawl.savePath.size() == 0) {
				finish();
			}
			else {
				drawDialog();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void drawDialog() {
		AlertDialog.Builder builder = new Builder(Draw.this);
		builder.setMessage(getResources().getString(R.string.weather_to_save));
		builder.setTitle(getResources().getString(R.string.tip));
		builder.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				file = mView.saveScrawl();
				finishActivity();
			}
		});

		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finishActivity();
			}
		});
		builder.create().show();
	}

	public void showColorMenu() {
		
		if(colorMenu == null) {
			Context mContext = Draw.this;
			LayoutInflater mLayoutInflater = (LayoutInflater) mContext
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View Color_popunwindwow = mLayoutInflater.inflate(R.layout.draw_color,
					null);
			colorMenu = new PopupWindow(Color_popunwindwow,
					(int) (MainActivity.Width * 0.8),LayoutParams.WRAP_CONTENT);
			colorMenu.setBackgroundDrawable(new BitmapDrawable(getResources()));
			colorMenu.setOutsideTouchable(false);
			colorMenu.setFocusable(true);
			colorMenu.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					picker.setOldCenterColor(picker.getColor());
				}
			});
			
			picker = (ColorPicker) Color_popunwindwow.findViewById(R.id.picker);
			picker.setColor(mView.getColor());
			//svBar = (SVBar) Color_popunwindwow.findViewById(R.id.svbar);
			mPaintSizeBar = (PaintSizeBar) Color_popunwindwow.findViewById(R.id.sizebar);
			
			opacityBar = (OpacityBar) Color_popunwindwow.findViewById(R.id.opacitybar);
			//picker.addSVBar(svBar);
			picker.addOpacityBar(opacityBar);
			picker.addSizeBar(mPaintSizeBar);
			mPaintSizeBar.setColorPicker(picker);
			mPaintSizeBar.setSize(mView.getSize());
			picker.setOnColorChangedListener(this);
			colorMenu.showAtLocation(findViewById(R.id.draw_layout),
					Gravity.CENTER, 0, 0);  
		}
		else {
			picker.setColor(mView.getColor());
			mPaintSizeBar.setSize(mView.getSize());
			colorMenu.showAtLocation(findViewById(R.id.draw_layout),
					Gravity.CENTER, 0, 0);
		}
			
	}

	private void finishActivity() {
		if (file != null) { 
			Bundle bundle = new Bundle();
			bundle.putString("draw", file.getName());
			Draw.this.setResult(22, Draw.this.getIntent().putExtras(bundle));
		}
		Draw.this.finish();
	} 

	@Override
	public void onColorChanged(int color) {
		// TODO Auto-generated method stub
		mView.colorScrawl(color);
	}

	@Override
	public void onSizeChanged(int size) {
		// TODO Auto-generated method stub
		mView.sizeScrawl(size);
	}
}
