package com.donote.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.donote.activity.CommonEdit;
import com.wxl.donote.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
public class GesView extends View {

	private Paint mPaint;

	private static final float MINP = 0.25f;
	private static final float MAXP = 0.75f;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;

	private long mStartTime;
	private long mEndTime;

	private String path = Environment.getExternalStorageDirectory().getPath() + "/"
			+ "DoNote" + "/" + "gesture" + "/";
	private String name = null;
	private String gesfile = null;

	private File mydirFile;
	private File file;
	private Bitmap newbmp;


	private boolean isStart = false;

	private Handler mHandler;
	private Context mContext;

	public GesView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public GesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFF000000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(25);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				name = DateFormat.format("yyyyMMdd_hhmmss",
						Calendar.getInstance(Locale.CHINA))
						+ ".png";

				gesfile = path + name;
				mydirFile = new File(path);
				mydirFile.mkdirs();
				file = new File(gesfile);
				try {

					file.createNewFile();
					FileOutputStream out = new FileOutputStream(file);
					newbmp.compress(CompressFormat.PNG, 100, out);
					out.flush();
					out.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

				//在CommonEdit中添加上去图片
				Drawable drawable = new BitmapDrawable(getResources(),newbmp);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight());
				ImageSpan picSpan = new ImageSpan(drawable,"Gesture^_^[" + gesfile + "]^_^",
						ImageSpan.ALIGN_BASELINE);
				SpannableString spanPicture = new SpannableString( "Gesture^_^[" + gesfile + "]^_^");
				spanPicture.setSpan(picSpan, 0, spanPicture.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				int start = CommonEdit.mBodyText.getSelectionStart();
				Editable mbody = CommonEdit.mBodyText.getText();
				mbody.insert(start, spanPicture);
				mBitmap = Bitmap.createBitmap(mBitmap.getWidth(),
						mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
				mCanvas.setBitmap(mBitmap);
				invalidate();

			}
		};
	}

	public GesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(0x00000000);

		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

		canvas.drawPath(mPath, mPaint);
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mEndTime = System.currentTimeMillis();
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			mEndTime = System.currentTimeMillis();
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			mEndTime = System.currentTimeMillis();
			touch_up();
			invalidate();
			if (!isStart) {
				isStart = true;
				new Thread() {
					public void run() {
						while (isStart) {
							long time = System.currentTimeMillis();
							if (time >= mEndTime + 1000) {

								int width = mBitmap.getWidth();
								int height = mBitmap.getHeight();
								Matrix matrix = new Matrix();
								float scaleWidht = ((float) 60 / width);
								float scaleHeight = ((float) 80 / height);
								matrix.postScale(scaleWidht, scaleHeight);
								newbmp = Bitmap.createBitmap(mBitmap, 0, 0, width, height,
										matrix, true);

								mHandler.sendEmptyMessage(0);
								isStart = false;
							}
						}
					};
				}.start();
			}
			break;
		}
		return true;
	}

}
