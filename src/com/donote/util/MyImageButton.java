package com.donote.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class MyImageButton extends ImageButton {

	public MyImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setPressed(boolean pressed) {
		// TODO Auto-generated method stub
		if (pressed && getParent() instanceof View
				&& ((View) getParent()).isPressed())
		{
			return;
		}
		super.setPressed(pressed);
	}
}
