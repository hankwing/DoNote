package com.donote.implement;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

public class ImageButtonOnTouch implements OnTouchListener{
	private ImageButton bt;
	private String down_string;
	private String up_string;
	public ImageButtonOnTouch(ImageButton ibt,String down_color,String up_color ) {
		bt = ibt;
		down_string = down_color;
		up_string = up_color;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
			bt.setBackgroundColor(Color.parseColor(down_string));
		}
		if (arg1.getAction() == MotionEvent.ACTION_UP) {
			bt.setBackgroundColor(Color.parseColor(up_string));
		}
		return false;
	}
}
