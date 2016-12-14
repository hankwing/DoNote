package com.donote.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.wxl.donote.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

@SuppressLint("DrawAllocation")
public class Scrawl extends View {
	
	private Context mContext;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;// �����Ļ���
	private Paint mPaint;// ��ʵ�Ļ���
	private float mX, mY;// ��ʱ������
	private static final float TOUCH_TOLERANCE = 4;
	private String path = Environment.getExternalStorageDirectory().getPath() + "/"
			+ "DoNote" + "/" + "draw" + "/";
	private String name = null;
	private String drawfile = null;
	private File mydirFile;
	private File file;
	// ����Path·���ļ���,��List������ģ��ջ

	@SuppressWarnings("rawtypes")
	public static List savePath;
	// ��¼Path·���Ķ���
	private DrawPath dp;

	private class DrawPath {
		public Path path;// ·��
		public int paint;// ����
	}

	public Scrawl(Context context) {
		super(context);
		mContext = context;
	}

	public Scrawl(Context context, AttributeSet a) {
		super(context, a);
		mContext = context;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		InitScrawl(mContext);
		super.onLayout(changed, left, top, right, bottom);
	}

	public void InitScrawl(Context context) {

		mBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
				Bitmap.Config.ARGB_8888);
		// ����һ��һ�λ��Ƴ�����ͼ��
		mCanvas = new Canvas(mBitmap);

		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mBitmapPaint.setColor(Color.WHITE);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);// �������Ե
		mPaint.setStrokeCap(Paint.Cap.ROUND);// ��״
		mPaint.setStrokeWidth(12);// ���ʿ��

		Canvas canvas = new Canvas(mBitmap);
		Paint backPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		backPaint.setColor(Color.WHITE);
		Rect backRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
		canvas.drawRect(backRect, backPaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();

		savePath = new ArrayList<DrawPath>();
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		// ��ǰ���Ѿ���������ʾ����
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		if (mPath != null) {
			// ʵʱ����ʾ
			canvas.drawPath(mPath, mPaint);
		}
	}

	private void touch_start(float x, float y) {
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(mY - y);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			// ��x1,y1��x2,y2��һ�����������ߣ���ƽ��(ֱ����mPath.lineToҲ�ǿ��Ե�)
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	@SuppressWarnings("unchecked")
	private void touch_up() {
		mPath.lineTo(mX, mY);
		mCanvas.drawPath(mPath, mPaint);
		savePath.add(dp);
		mPath = null;
	}

	/**
	 * �����ĺ���˼����ǽ�������գ� ������������Path·�����һ���Ƴ����� ���½�·�����ڻ������档
	 */
	public void backScrawl() {
		if (savePath != null && savePath.size() > 0) {
			savePath.remove(savePath.size() - 1);
			redrawOnBitmap();
		}
	}

	/**
	 * ����
	 */
	public void clearScrawl() {
		if (savePath != null && savePath.size() > 0) {
			savePath.clear();
			redrawOnBitmap();
		}
	}

	private void redrawOnBitmap() {
		mBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
				Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);// �������û������൱����ջ���
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mBitmapPaint.setColor(Color.WHITE);
		
		Canvas canvas = new Canvas(mBitmap);
		Paint backPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		backPaint.setColor(Color.WHITE);
		Rect backRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
		canvas.drawRect(backRect, backPaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		
		@SuppressWarnings("rawtypes")
		Iterator iter = savePath.iterator();
		while (iter.hasNext()) {
			DrawPath drawPath = (DrawPath) iter.next();
			mPaint.setColor(drawPath.paint);
			mCanvas.drawPath(drawPath.path, mPaint);
		}
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// ÿ��down��ȥ����newһ��Path
			mPath = new Path();
			// ÿһ�μ�¼��·�������ǲ�һ����
			dp = new DrawPath();
			dp.path = mPath;
			dp.paint = mPaint.getColor();
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}

	public File saveScrawl() {
		if (savePath.size() != 0) {
			new DateFormat();
			name = DateFormat.format("yyyyMMdd_hhmmss",
					Calendar.getInstance(Locale.CHINA))
					+ ".png";

			drawfile = path + name;
			mydirFile = new File(path);
			mydirFile.mkdirs();
			file = new File(drawfile);
			try {
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(file);
				mBitmap.compress(CompressFormat.PNG, 100, out);
				out.flush();
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return file;
		} else {
			Toast.makeText(getContext(), getResources().getString(R.string.empty_draw), Toast.LENGTH_SHORT).show();
			return null;
		}
	}

	public void colorScrawl(int color) {
		mPaint.setColor(color);
		invalidate();
	}
	
	public void sizeScrawl(int size) {
		mPaint.setStrokeWidth(size);
		invalidate();
	} 
	
	public int getColor() {
		return mPaint.getColor();
	}
	
	public int getSize() {
		return (int) mPaint.getStrokeWidth();
	}

}