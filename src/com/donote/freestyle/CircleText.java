package com.donote.freestyle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.TextView;

@SuppressLint("DrawAllocation")
public class CircleText extends TextView {

	boolean isCircle = false;

	public CircleText(Context context, boolean isCircle) {
		super(context);

		this.isCircle = isCircle;

		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (this.isCircle == false) {
			 
		                
			final Paint paint = new Paint();
		        // 下面的四个参数分别为：left,top,right,bottom
		    final Rect rect = new Rect(1, 0, this.getWidth()-1, this.getHeight()-1);          
		    final RectF rectF = new RectF(rect);          
		    final float roundPx = 21;            
		    paint.setAntiAlias(true);          
		    canvas.drawARGB(0, 0, 0, 0);          
		    paint.setColor(android.graphics.Color.GRAY);
		    paint.setStrokeWidth(7);
		    paint.setStyle(Style.STROKE);
		    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);            
		}
		else if(this.isCircle == true){
		      
			final Paint paint = new Paint();
			
			paint.setColor(android.graphics.Color.GRAY);
			paint.setStrokeWidth(7);
			paint.setStyle(Style.STROKE);
			canvas.drawCircle(this.getWidth() /2, this.getHeight() /2, this.getWidth()/2 -4, paint);
		}
	}
	

}
