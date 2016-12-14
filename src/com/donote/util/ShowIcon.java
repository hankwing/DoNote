package com.donote.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import com.donote.activity.CommonEdit;
import com.donote.activity.MainActivity;
import com.wxl.donote.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

public class ShowIcon {

	private static Context context;
	static float Width;
	static float Height;

	public ShowIcon(Context context, float Width, float Height) {
		ShowIcon.context = context;
		ShowIcon.Width = Width;
		ShowIcon.Height = Height;
	}

	public SpannableString showImage(String photofile) {

		Bitmap photo = readBitmapAutoSize(photofile, Width, Height,
				readPictureDegree(photofile));
		ImageSpan phoSpan = null;
		String information = "Photo^_^[" + photofile + "]^_^ ";
		Drawable drawable = null;
		if (photo == null) {
			drawable = context.getResources().getDrawable(
					R.drawable.ic_default_image);
		}
		else {
			photo = zoomBitmap(photo);
			drawable = new BitmapDrawable(context.getResources(), photo);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
		}
		phoSpan = new ImageSpan(drawable, information, ImageSpan.ALIGN_BASELINE);
		SpannableString spanPhoto = new SpannableString(information);
		spanPhoto.setSpan(phoSpan, 0, spanPhoto.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanPhoto;

	}

	public ImageSpan getImage(Matcher photoMatcher, String photofile) {
		// Bitmap photo = BitmapFactory.decodeFile(photofile);
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photofile, opt);
		Bitmap photo = readBitmapAutoSize(photofile, Width, (float) opt.outHeight
				* ((float) MainActivity.Width / opt.outWidth),
				readPictureDegree(photofile));
		ImageSpan phoSpan = null;
		if (photo == null) {
			phoSpan = getDefaultImage(photoMatcher);
			return phoSpan;
		} else {
			photo = ShowIcon.zoomBitmap(photo);
			Drawable drawable = new BitmapDrawable(context.getResources(),photo);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			phoSpan = new ImageSpan(drawable, photoMatcher.group());
			return phoSpan;
		}
	}
	
	public SpannableString showTable(String name, String idString) {
		
		Drawable drawable = context.getResources().getDrawable(
				R.drawable.ic_table2);
		drawable = tableIcon(name , Width/2);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		ImageSpan recSpan = new ImageSpan(drawable, idString,
				ImageSpan.ALIGN_BASELINE);
		SpannableString spanRecord = new SpannableString(idString);
		spanRecord.setSpan(recSpan, 0, spanRecord.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanRecord;

	}

	public ImageSpan getDefaultImage(Matcher photoMatcher) {
		Drawable photo = context.getResources().getDrawable(
				R.drawable.ic_default_image);
		photo.setBounds(0, 0, photo.getIntrinsicWidth(),
				photo.getIntrinsicHeight());
		ImageSpan phoSpan = new ImageSpan(photo, photoMatcher.group());
		return phoSpan;
	}
	
	public static ImageSpan getTableImage(Matcher tableMatcher, String idString) {
		String title = idString.substring(
				idString.indexOf("<--title:") + 9,
				idString.indexOf("-->"));
		if (title.equals("")) {
			title = context.getResources().getString(R.string.without_title);
		}
		Drawable drawable = tableIcon(title , Width/2);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		ImageSpan recSpan = new ImageSpan(drawable, tableMatcher.group());
		return recSpan;
	}

	public static ImageSpan getDefaultVideoImage(Matcher photoMatcher) {
		Drawable photo = context.getResources().getDrawable(
				R.drawable.ic_default_video_iamge);
		photo.setBounds(0, 0, photo.getIntrinsicWidth(),
				photo.getIntrinsicHeight());
		ImageSpan phoSpan = new ImageSpan(photo, photoMatcher.group());
		return phoSpan;
	}

	public ImageSpan getVideoImage(Matcher videoMatcher, String videofile) {
		Bitmap video = ThumbnailUtils.createVideoThumbnail(videofile,
				MediaStore.Images.Thumbnails.MICRO_KIND);

		ImageSpan videoSpan = null;
		if (video == null) {
			videoSpan = getDefaultImage(videoMatcher);
			return videoSpan;
		} else {
			video = ShowIcon.zoomBitmap2(video);
			video = ShowIcon.VideoIcon(video);
			Drawable drawable = new BitmapDrawable(context.getResources(),video);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			videoSpan = new ImageSpan(drawable, videoMatcher.group());
			return videoSpan;
		}
	}

	public ImageSpan getRecordImage(Matcher recordMatcher, String photofile) {
		Drawable drawable = ShowIcon.RecordIcon(photofile , Width/2);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		ImageSpan recSpan = new ImageSpan(drawable, recordMatcher.group());
		return recSpan;
	}
	
	//��ô���Ӱ��ͼƬ
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {  
	    final int reflectionGap = 4;  
	    int w = bitmap.getWidth();  
	    int h = bitmap.getHeight();  
	  
	    Matrix matrix = new Matrix();  
	    matrix.preScale(1, -1);  
	  
	    Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,  
	            h / 2, matrix, false);  
	  
	    Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),  
	            Config.ARGB_8888);  
	  
	    Canvas canvas = new Canvas(bitmapWithReflection);  
	    canvas.drawBitmap(bitmap, 0, 0, null);  
	    Paint deafalutPaint = new Paint();  
	    canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);  
	  
	    canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);  
	  
	    Paint paint = new Paint();  
	    LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,  
	            bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,  
	            0x00ffffff, TileMode.CLAMP);  
	    paint.setShader(shader);  
	    // Set the Transfer mode to be porter duff and destination in  
	    paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));  
	    // Draw a rectangle using the paint with our linear gradient  
	    canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()  
	            + reflectionGap, paint);  
	    return bitmapWithReflection;  
	}  
	
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {  
	    int width = drawable.getIntrinsicWidth();  
	    int height = drawable.getIntrinsicHeight();  
	    // drawableת����bitmap  
	    Bitmap oldbmp = drawableToBitmap(drawable);  
	    // ��������ͼƬ�õ�Matrix����  
	    Matrix matrix = new Matrix();  
	    // �������ű���  
	    float sx = ((float) w / width);  
	    float sy = ((float) h / height);  
	    // �������ű���  
	    matrix.postScale(sx, sy);  
	    // �����µ�bitmap���������Ƕ�ԭbitmap�����ź��ͼ  
	    Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,  
	            matrix, true);  
	    return new BitmapDrawable(context.getResources(),newbmp);  
	}  
	
	public static Bitmap drawableToBitmap(Drawable drawable) {  
        // ȡ drawable �ĳ���  
        int w = drawable.getIntrinsicWidth();  
        int h = drawable.getIntrinsicHeight();  
  
        // ȡ drawable ����ɫ��ʽ  
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
                : Bitmap.Config.RGB_565;  
        // ������Ӧ bitmap  
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);  
        // ������Ӧ bitmap �Ļ���  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0, 0, w, h);  
        // �� drawable ���ݻ���������  
        drawable.draw(canvas);  
        return bitmap;  
    }  

	public ImageSpan getFileImage(Matcher fileMatcher, String photofile) {
		Drawable drawable = ShowIcon.FileIcon(photofile,Width/2);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		ImageSpan recSpan = new ImageSpan(drawable, fileMatcher.group());
		return recSpan;

	}

	public SpannableString showVoice(String name, String filePath) {
		Drawable drawable = RecordIcon(name ,Width/2);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		ImageSpan recSpan = new ImageSpan(drawable, filePath,
				ImageSpan.ALIGN_BASELINE);
		SpannableString spanRecord = new SpannableString(filePath);
		spanRecord.setSpan(recSpan, 0, filePath.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanRecord;
	}

	public SpannableString showFiles(String fileName, String filePath) {
		String information = "File^_^[" + filePath + "]^_^ ";
		Drawable drawable = context.getResources().getDrawable(
				R.drawable.ic_file);
		drawable = FileIcon(filePath,Width/2);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		ImageSpan recSpan = new ImageSpan(drawable, information,
				ImageSpan.ALIGN_BASELINE);
		SpannableString spanRecord = new SpannableString(information);
		spanRecord.setSpan(recSpan, 0, spanRecord.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanRecord;
	}

	// hankwing add******************************
	// ��ʾ��Ƶͼ��
	public SpannableString showVideo(String videofile) {
		Bitmap video = ThumbnailUtils.createVideoThumbnail(videofile,
				MediaStore.Images.Thumbnails.MICRO_KIND);

		String information = "Video^_^[" + videofile + "]^_^ ";
		Drawable drawable = null;
		if (video == null) {
			Resources res = context.getResources();
			drawable = res.getDrawable(R.drawable.ic_default_video_iamge);
		} else {
			video = zoomBitmap2(video);
			video = VideoIcon(video);
			drawable = new BitmapDrawable(context.getResources(),video);
		}

		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		ImageSpan vidSpan = new ImageSpan(drawable, information,
				ImageSpan.ALIGN_BASELINE);
		SpannableString spanVideo = new SpannableString(information);
		spanVideo.setSpan(vidSpan, 0, spanVideo.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spanVideo;

	}

	public static Bitmap zoomBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float nwidth = (float) (Width / 2.5);
		float nheight = (nwidth/Width)* height;
		Matrix matrix = new Matrix();
		float scaleWidht = nwidth / width;
		float scaleHeight = nheight / height;
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true).copy(Bitmap.Config.RGB_565, true);
		Canvas canvas = new Canvas(newbmp);
		Rect frame = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
		Paint framePaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		framePaint.setStyle(Style.STROKE);
		framePaint.setColor(Color.BLACK);
		canvas.drawRect(frame, framePaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return newbmp;

	}

	public static Bitmap zoomBitmap2(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float nwidth = (float) (Width / 3);
		float nheight = (float) (Height / 4);
		Matrix matrix = new Matrix();
		float scaleWidht = nwidth / width;
		float scaleHeight = nheight / height;
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true).copy(Bitmap.Config.RGB_565, true);
		Canvas canvas = new Canvas(newbmp);
		Rect frame = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
		Paint framePaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		framePaint.setStyle(Style.STROKE);
		framePaint.setColor(Color.BLACK);
		canvas.drawRect(frame, framePaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return newbmp;

	}
	public static Bitmap zoomBitmapTospe(Bitmap bitmap, float nwidth,
			float nheight) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = nwidth / width;
		float scaleHeight = nheight / height;
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true).copy(Bitmap.Config.RGB_565, true);
		return newbmp;
	}

	static public Bitmap VideoIcon(Bitmap video) {
		Canvas canvas = new Canvas(video);
		// ��־
		float mWidth = video.getWidth();
	    float mHeight = video.getHeight();
		Resources res = context.getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res,
				R.drawable.ic_recordmark);
		float cx = (mWidth - bitmap.getWidth()) / 2;
		float cy = (mHeight - bitmap.getHeight()) / 2;
		canvas.drawBitmap(bitmap, cx, cy, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return video;
	}

	static public Drawable FileIcon(String filePath, float Width) {
		File file = new File(filePath);
		float iconWidth = Width -20;
		float iconHeight;
		if(Width < MainActivity.Width) {
			iconHeight = (float) ((Width-20)/3.5);
		}
		else {
			iconHeight = (float) ((Width-20)/5);
		}
		Bitmap icon = Bitmap.createBitmap((int)iconWidth, (int)iconHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(icon);// ��ʼ���������Ƶ�ͼ��icon��
		Paint photoPaint = new Paint(); // ��������
		photoPaint.setDither(true);
		photoPaint.setFilterBitmap(true);
		// ����
		final Rect back = new Rect(2,1, icon.getWidth() - 2,
				icon.getHeight() - 2);
		final RectF backF = new RectF(back);
		final float backPx = (float) (iconWidth * 0.03);
		Paint backPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		backPaint.setStrokeWidth(5);
		backPaint.setARGB(120, 204, 204, 204); // ��ɫ
		canvas.drawRoundRect(backF, backPx, backPx, backPaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// ���
		final Rect frame = new Rect(1,0, icon.getWidth() - 1, icon.getHeight()-1);
		final RectF frameF = new RectF(frame);
		final float framePx = (float) (iconWidth * 0.03);
		Paint framePaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		framePaint.setStrokeWidth(2);
		framePaint.setStyle(Style.STROKE);
		framePaint.setARGB(120, 0, 0, 0); // ��ɫ
		canvas.drawRoundRect(frameF, framePx, framePx, framePaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// ��־
		Rect mark = new Rect((int) (iconWidth / 45), (int) (iconHeight / 6),
				(int) (iconWidth / 6), (int) (iconHeight/1.2));
		Resources res = context.getResources();
		Bitmap bitmap = null;
		if(CommonEdit.checkEndsWithInStringArray(file.getName(), context.getResources().getStringArray(
				R.array.fileEndingWebText)))
		{
			bitmap = BitmapFactory.decodeResource(res, R.drawable.file_unknow);
		} else if(CommonEdit.checkEndsWithInStringArray(file.getName(), context.getResources().getStringArray(
				R.array.fileEndingExcel))) {
			bitmap = BitmapFactory.decodeResource(res, R.drawable.file_excel);
		} else if(CommonEdit.checkEndsWithInStringArray(file.getName(), context.getResources().getStringArray(
				R.array.fileEndingPdf))) {
			bitmap = BitmapFactory.decodeResource(res, R.drawable.file_pdf);
		} else if(CommonEdit.checkEndsWithInStringArray(file.getName(), context.getResources().getStringArray(
				R.array.fileEndingPPT))) {
			bitmap = BitmapFactory.decodeResource(res, R.drawable.file_ppt);
		} else if(CommonEdit.checkEndsWithInStringArray(file.getName(), context.getResources().getStringArray(
				R.array.fileEndingText))) {
			bitmap = BitmapFactory.decodeResource(res, R.drawable.file_unknow);
		} else if(CommonEdit.checkEndsWithInStringArray(file.getName(), context.getResources().getStringArray(
				R.array.fileEndingWord))) {
			bitmap = BitmapFactory.decodeResource(res, R.drawable.file_word);
		}
		else {
			bitmap = BitmapFactory.decodeResource(res, R.drawable.file_unknow);
		}
		canvas.drawBitmap(bitmap, null, mark, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// ����
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);
		textPaint.setTextSize(iconHeight/4);
		textPaint.setARGB(200, 0, 0, 0); // ��ɫ
		StringBuffer buffer = new StringBuffer(file.getName());
		float maxWidth = (float)(iconWidth*0.75);
		//Log.i("display", "iconwIdth maxWidth " + iconWidth + " " + maxWidth);
		if(textPaint.measureText(buffer.toString())>= maxWidth) {
			while(textPaint.measureText(buffer.toString()) >= maxWidth) {
				buffer = buffer.deleteCharAt(buffer.length()-1);
			}
			buffer.append("...");
		}
		canvas.drawText(buffer.toString(), (int) Width / 6, (int) (icon.getHeight() * 0.4),
				textPaint);// ������ȥ�֣���ʼδ֪x,y������ֻ�ʻ���
		textPaint.setARGB(220, 102, 102, 102);
		canvas.drawText(FormetFileSize(file.length()), (int) Width / 6, (int) (icon.getHeight() * 0.8),
				textPaint);// ������ȥ�֣���ʼδ֪x,y������ֻ�ʻ���
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		Drawable recIcon = new BitmapDrawable(context.getResources(), icon);
		return recIcon;

	}
	
	public static String FormetFileSize(long fileS) { 
        DecimalFormat df = new DecimalFormat("#"); 
        String fileSizeString = ""; 
        if (fileS < 1024) { 
            fileSizeString = df.format((double) fileS) + "B"; 
        } else if (fileS < 1048576) { 
            fileSizeString = df.format((double) fileS / 1024) + "K"; 
        } else if (fileS < 1073741824) { 
            fileSizeString = df.format((double) fileS / 1048576) + "M"; 
        } else { 
            fileSizeString = df.format((double) fileS / 1073741824) + "G"; 
        } 
        return fileSizeString; 
    } 

	public static Drawable RecordIcon(String filePath , float Width) {
		File file = new File(filePath);
		float iconWidth = Width -20;
		float iconHeight;
		if(Width < MainActivity.Width) {
			iconHeight = (float) ((Width-20)/3.5);
		}
		else {
			iconHeight = (float) ((Width-20)/5);
		}
		Bitmap icon = Bitmap.createBitmap((int)iconWidth, (int)iconHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(icon);
		Paint recordPaint = new Paint(); // ��������
		recordPaint.setDither(true); // ��ȡ��������ͼ�����
		recordPaint.setFilterBitmap(true);// ����һЩ
		final Rect frame = new Rect(1,0, icon.getWidth() - 1, icon.getHeight()-1);
		final RectF frameF = new RectF(frame);
		final float framePx = (float) (iconWidth * 0.03);
		Paint framePaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		framePaint.setStrokeWidth(2);
		framePaint.setStyle(Style.STROKE);
		framePaint.setARGB(120, 0, 0, 0); 
		canvas.drawRoundRect(frameF, framePx, framePx, framePaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// ����
		final Rect back = new Rect(2,1, icon.getWidth() - 2,
				icon.getHeight() - 2);
		final RectF backF = new RectF(back);
		final float backPx = (float) (iconWidth * 0.03);
		Paint backPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		backPaint.setStrokeWidth(5);
		backPaint.setARGB(120, 204, 204, 204); 
		canvas.drawRoundRect(backF, backPx, 7, backPaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// ��־
		Rect mark = new Rect((int) (iconWidth / 45), (int) (iconHeight / 6),
				(int) (iconWidth / 6), (int) (iconHeight/1.2));
		Resources res = context.getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_music2);
		canvas.drawBitmap(bitmap, null, mark, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// ����
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(iconHeight/4);
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);
		textPaint.setARGB(200, 0, 0, 0);
		StringBuffer buffer = new StringBuffer(file.getName());
		float maxWidth = (float) (iconWidth*0.75);
		if(textPaint.measureText(buffer.toString())>= maxWidth) {
			while(textPaint.measureText(buffer.toString()) >= maxWidth) {
				buffer = buffer.deleteCharAt(buffer.length()-1);
			}
			buffer.append("...");
		}
		canvas.drawText(buffer.toString(), (int) Width / 6, (int) (icon.getHeight() * 0.4),
				textPaint);// ������ȥ�֣���ʼδ֪x,y������ֻ�ʻ���
		textPaint.setARGB(220, 102, 102, 102);
		canvas.drawText(FormetFileSize(file.length()), (int) Width / 6, (int) (icon.getHeight() * 0.8),
				textPaint);// ������ȥ�֣���ʼδ֪x,y������ֻ�ʻ���
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		Drawable recIcon = new BitmapDrawable(context.getResources(),icon);
		return recIcon;
	}
	
	public static Drawable tableIcon(String filePath , float Width) {
		File file = new File(filePath);
		float iconWidth = Width -20;
		float iconHeight;
		if(Width < MainActivity.Width) {
			iconHeight = (float) ((Width-20)/3.5);
		}
		else {
			iconHeight = (float) ((Width-20)/5);
		}
		Bitmap icon = Bitmap.createBitmap((int)iconWidth, (int)iconHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(icon);
		Paint recordPaint = new Paint(); // ��������
		recordPaint.setDither(true); // ��ȡ��������ͼ�����
		recordPaint.setFilterBitmap(true);// ����һЩ
		final Rect frame = new Rect(1,0, icon.getWidth() - 1, icon.getHeight()-1);
		final RectF frameF = new RectF(frame);
		final float framePx = (float) (iconWidth * 0.03);
		Paint framePaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		framePaint.setStrokeWidth(2);
		framePaint.setStyle(Style.STROKE);
		framePaint.setARGB(120, 0, 0, 0); 
		canvas.drawRoundRect(frameF, framePx, framePx, framePaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// ����
		final Rect back = new Rect(2,1, icon.getWidth() - 2,
				icon.getHeight() - 2);
		final RectF backF = new RectF(back);
		final float backPx = (float) (iconWidth * 0.03);
		Paint backPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		backPaint.setStrokeWidth(5);
		backPaint.setARGB(120, 204, 204, 204); 
		canvas.drawRoundRect(backF, backPx, backPx, backPaint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		// ��־
		Rect mark = new Rect((int) (iconWidth / 45), (int) (iconHeight / 6),
				(int) (iconWidth / 6), (int) (iconHeight/1.2));
		Resources res = context.getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_table2);
		canvas.drawBitmap(bitmap, null, mark, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);  
		canvas.restore();
		// ����
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize(iconHeight/2);
		textPaint.setTypeface(Typeface.DEFAULT_BOLD);
		textPaint.setARGB(150, 0, 0, 0);
		StringBuffer buffer = new StringBuffer(file.getName());
		float maxWidth = (float) (iconWidth*0.75);
		if(textPaint.measureText(buffer.toString())>= maxWidth) {
			while(textPaint.measureText(buffer.toString()) >= maxWidth) {
				buffer = buffer.deleteCharAt(buffer.length()-1);
			}
			buffer.append("...");
		}
		canvas.drawText(buffer.toString(), (int) Width / 6, (int) (icon.getHeight() * 0.63),
				textPaint);// ������ȥ�֣���ʼδ֪x,y������ֻ�ʻ���
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		Drawable recIcon = new BitmapDrawable(context.getResources(),icon);
		return recIcon;
	}

	public static Bitmap readBitmapAutoSize(String filePath, float outWidth,
			float outHeight, int angle) {
		// outWidth��outHeight��Ŀ��ͼƬ������Ⱥ͸߶ȣ���������
		FileInputStream fs = null;
		BufferedInputStream bs = null;
		if(new File(filePath).exists()) {
			try {
				fs = new FileInputStream(filePath);
				bs = new BufferedInputStream(fs);
				BitmapFactory.Options options = setBitmapOption(filePath,
						(int) outWidth, (int) outHeight);
				Bitmap temp = BitmapFactory.decodeStream(bs, null, options);
				
				
				temp = rotaingImageView(angle, temp);
				temp = zoomBitmapTospe(temp, outWidth, outHeight);
				return temp;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					bs.close();
					fs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else {
			return null;
		}
		return null;
	}

	private static BitmapFactory.Options setBitmapOption(String file,
			int width, int height) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		opt.inTargetDensity = context.getResources().getDisplayMetrics().densityDpi;
		opt.inScaled = true;
		// ����ֻ�ǽ���ͼƬ�ı߾࣬�˲���Ŀ���Ƕ���ͼƬ��ʵ�ʿ�Ⱥ͸߶�
		BitmapFactory.decodeFile(file, opt);
		int outWidth = opt.outWidth; // ���ͼƬ��ʵ�ʸߺͿ�
		int outHeight = opt.outHeight;
		/*
		 * Log.i("display", "height " + outHeight + " width " + outWidth);
		 * Log.i("display", "wheight " + height + " wwidth " + width);
		 */
		opt.inDither = false;
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		// ���ü���ͼƬ����ɫ��Ϊ16bit��Ĭ����RGB_8888����ʾ24bit��ɫ��͸��ͨ������һ���ò���
		opt.inSampleSize = 1;
		// �������ű�,1��ʾԭ������2��ʾԭ�����ķ�֮һ....
		// �������ű�
		if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
			int sampleSize = (outWidth / width + outHeight / height) / 2;
			opt.inSampleSize = sampleSize;
		}
		opt.inJustDecodeBounds = false;// ���ѱ�־��ԭ
		return opt;
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	// ����ĳЩ����ѡ��ͼƬ����ת����
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// �����µ�ͼƬ
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
		/**
	 * Բ�Ǵ���
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {  
        
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
  
        final int color = 0xff424242;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
        final float roundPx = pixels;  
   
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;  
    }
	
	public static int dip2px(Context context, float dipValue){ 
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int)(dipValue * scale + 0.5f); 
		} 

}
