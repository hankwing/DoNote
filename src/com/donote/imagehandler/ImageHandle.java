package com.donote.imagehandler;

import java.io.File;
import java.io.IOException;

import com.baidu.mobstat.StatService;
import com.donote.activity.CommonEdit;
import com.donote.activity.MainActivity;
import com.donote.util.ShowIcon;
import com.wxl.donote.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class ImageHandle extends Activity {

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

	private static final int CROP_BIG_PICTURE = 0x12;
	private File mfile, RetFile;
	private WebView WV_View;
	private ProgressDialog myDialog = null;
	private int time = 0;
	private Bitmap bm, bmp;
	private File f;
	private LinearLayout LL_ToolBar;
	private String strFormat_ImageZoom;
	private float scale = 1;
	private float degree = 0;
	private int reverseflag = 0;
	private ImageButton handleMore;
	private LinearLayout mainLayout;
	private Button oldImageButton; // 怀旧效果
	private Button blurImageButton; // 模糊效果
	private Button sharpenImageButton; // 锐化效果
	private Button embossImageButton; // 浮雕效果
	private Button filmImageButton; // 底片效果
	private Button sunshineImageButton; // 光照效果
	private PopupWindow window;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_imagezoom);
		LoadWebView();
		LL_ToolBar = (LinearLayout) findViewById(R.id.LL_ToolBar);
		mainLayout = (LinearLayout) findViewById(R.id.imagehandle_main);
		ImageButton IB_RotateLeft = (ImageButton) findViewById(R.id.IB_RotateLeft);
		IB_RotateLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				degree -= 90;
				degree = degree % 360;
				ImageMatrix();
			}
		});
		ImageButton IB_RotateRight = (ImageButton) findViewById(R.id.IB_RotateRight);
		IB_RotateRight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				degree += 90;
				degree = degree % 360;
				ImageMatrix();
			}
		});

		handleMore = (ImageButton) findViewById(R.id.handle_more);
		handleMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopupWindow popupWindow = menuPopupwindow(ImageHandle.this);
				// int yoff = popupWindow.getHeight() - moreButton.getHeight();
				// popupWindow.showAsDropDown(moreButton, 0, -yoff/8);
				popupWindow.showAtLocation(mainLayout, Gravity.RIGHT
						| Gravity.BOTTOM, 0, handleMore.getHeight());
			}
		});

		ImageButton IB_Crop = (ImageButton) findViewById(R.id.IB_Crop);
		IB_Crop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// RetFile.renameTo(mfile);
				mfile = RetFile;
				scale = 1;
				degree = 0;
				reverseflag = 0;
				f = new File(CommonEdit.pathPicture, "z" + mfile.getName());
				CropImageUri(Uri.fromFile(mfile), Uri.fromFile(f),
						CROP_BIG_PICTURE);
			}
		});

		ImageButton IB_LogoImg = (ImageButton) findViewById(R.id.IB_LogoImg);
		IB_LogoImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		ImageButton IB_Setup = (ImageButton) findViewById(R.id.IB_Function);
		IB_Setup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 保存结果
				// Global.SaveFile(getCacheDir(), mfile.getName(),
				// ImageDispose.Bitmap2Bytes(RetBmp));
				// RetFile.renameTo(mfile);
				scale = 1;
				degree = 0;
				reverseflag = 0;
				// Intent i = new Intent();
				// Bundle b = new Bundle();
				// b.putString("CALCULATION", value);
				// i.putExtras(b);
				Intent aintent = new Intent(ImageHandle.this, CommonEdit.class);
				/* 将数据打包到aintent Bundle 的过程略 */
				Bundle b = new Bundle();
				b.putString("filepath", RetFile.getPath());
				aintent.putExtras(b);
				setResult(RESULT_OK, aintent);
				finish();
			}
		});

		// 载入显示模板
		try
		{
			strFormat_ImageZoom = ImageHandleGlobal.GetString(ImageHandle.this
					.getAssets().open("imagezoom.html"));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent callerIntent = getIntent();
		mfile = new File(callerIntent.getStringExtra("filepath"));
		UpdateUI(mfile);
	}

	/**
	 * 图像处理
	 */
	private void ImageMatrix() {
		if (degree == 0 && reverseflag == 0 && scale == 1)
		{
			UpdateUI(mfile);
			return;
		}
		myDialog = ProgressDialog.show(ImageHandle.this, getResources()
				.getString(R.string.please_wait),
				getResources().getString(R.string.picture_processing));
		new Thread() {
			public void run() {
				try
				{
					bm = ImageDispose.getBitmapFromFile(mfile, 1800, 1800);
					bmp = ImageDispose.toMatrix(bm, degree, scale, reverseflag);
					bm.recycle();
					bm = null;
					System.gc();
					f = new File(CommonEdit.pathPicture, "z" + mfile.getName());
				} catch (Exception e)
				{
					e.printStackTrace();
				} finally
				{ // 卸除所建立的myDialog对象。
					myDialog.dismiss();
					if (ImageHandleGlobal.SaveFile(f,
							ImageDispose.Bitmap2Bytes(bmp)))
					{
						UpdateUI(f);
					}
					bmp.recycle();
					bmp = null;
					System.gc();
				}
			}
		}.start();

	}

	/**
	 * 更新UI
	 * 
	 * @param f
	 */
	private void UpdateUI(File f) {
		if (f != null)
		{
			if (ImageHandleGlobal.isImageFile(f.getName()))
			{
				RetFile = f;
				BitmapFactory.Options opts = null;
				opts = new BitmapFactory.Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(f.getPath(), opts);
				WV_View.clearCache(true);
				float height = opts.outHeight
						* (MainActivity.Width / opts.outWidth);
				String strHtml = String.format(strFormat_ImageZoom,
						f.getPath(), MainActivity.Height / 2 - (height / 2)
								- 80);
				String baseUrl = "file:///assets";
				WV_View.loadDataWithBaseURL(baseUrl, strHtml, "text/html",
						"utf-8", null);
				/*
				 * WV_View.loadDataWithBaseURL(baseUrl,"<html><center><img src="+
				 * f.getPath()+" vspace="
				 * +(height/2-(opts.outHeight/2))+"></html>"
				 * ,"text/html","utf-8","");
				 */

				if (time == 0)
				{
					time++;
					degree += ShowIcon.readPictureDegree(f.getAbsolutePath());
					degree = degree % 360;
					ImageMatrix();
				}

			} else
			{
				String strHtml = String.format(strFormat_ImageZoom,
						"file:///assets/attachment.png");
				String baseUrl = "file:///assets";
				WV_View.loadDataWithBaseURL(baseUrl, strHtml, "text/html",
						"utf-8", null);
				LL_ToolBar.setVisibility(LinearLayout.INVISIBLE);
			}
		}
	}

	/**
	 * 图像剪裁
	 * 
	 * @param uri
	 *            原始图片uri
	 * @param outuri
	 *            输出的图片uri
	 * @param requestCode
	 */
	private void CropImageUri(Uri uri, Uri outuri, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// intent.putExtra("aspectX", 2);
		// intent.putExtra("aspectY", 1);
		// intent.putExtra("outputX", outputX);
		// intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	// 处理返回的结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode)
		{
		case CROP_BIG_PICTURE:// from crop_big_picture
			// Log.d(TAG, "CROP_BIG_PICTURE: data = " + data);//it seems to be
			// null
			// imageUri = data.getData();
			f = new File(CommonEdit.pathPicture, "z" + mfile.getName());
			if (f.length() > 0)
			{
				// f.renameTo(mfile);
				UpdateUI(f);
			}
			// if(imageUri != null){
			// Bitmap bitmap = decodeUriAsBitmap(imageUri);
			// img_shower.setImageBitmap(bitmap);
			// }
			break;
		}

	}

	public void LoadWebView() {

		WV_View = (WebView) findViewById(R.id.WV_View);
		// WV_View.getSettings().setJavaScriptEnabled(true);
		WV_View.getSettings().setBuiltInZoomControls(true);
		WV_View.getSettings().setSupportZoom(true); // 启用页面的缩放
		WV_View.getSettings().setDefaultZoom(ZoomDensity.FAR);
		WV_View.getSettings().setUseWideViewPort(true);
		WV_View.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		WV_View.setClickable(true);
		// WV_View.setOnTouchListener(OnTouch_WebView);// 监听触摸事件
		WV_View.setInitialScale(100);
	}

	// 怀旧效果
	private Bitmap oldRemeber(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		int pixColor = 0;
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < height; i++)
		{
			for (int k = 0; k < width; k++)
			{
				pixColor = pixels[width * i + k];
				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);
				newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
				newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
				newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
				int newColor = Color.argb(255, newR > 255 ? 255 : newR,
						newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
				pixels[width * i + k] = newColor;
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

		return bitmap;
	}

	// 模糊效果

	private Bitmap blurImageAmeliorate(Bitmap bmp) {
		// 高斯矩阵
		int[] gauss = new int[] { 1, 2, 1, 2, 4, 2, 1, 2, 1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int pixColor = 0;
		int newR = 0;
		int newG = 0;
		int newB = 0;
		int delta = 16; // 值越小图片会越亮，越大则越暗
		int idx = 0;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++)
		{
			for (int k = 1, len = width - 1; k < len; k++)
			{
				idx = 0;
				for (int m = -1; m <= 1; m++)
				{
					for (int n = -1; n <= 1; n++)
					{
						pixColor = pixels[(i + m) * width + k + n];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * gauss[idx]);
						newG = newG + (int) (pixG * gauss[idx]);
						newB = newB + (int) (pixB * gauss[idx]);
						idx++;
					}
				}

				newR /= delta;
				newG /= delta;
				newB /= delta;

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);

				newR = 0;
				newG = 0;
				newB = 0;
			}
		}
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

		return bitmap;
	}

	// 锐化效果
	private Bitmap sharpenImageAmeliorate(Bitmap bmp) {
		// 拉普拉斯矩阵
		int[] laplacian = new int[] { -1, -1, -1, -1, 9, -1, -1, -1, -1 };

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int idx = 0;
		float alpha = 0.3F;
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 1, length = height - 1; i < length; i++)
		{
			for (int k = 1, len = width - 1; k < len; k++)
			{
				idx = 0;
				for (int m = -1; m <= 1; m++)
				{
					for (int n = -1; n <= 1; n++)
					{
						pixColor = pixels[(i + n) * width + k + m];
						pixR = Color.red(pixColor);
						pixG = Color.green(pixColor);
						pixB = Color.blue(pixColor);

						newR = newR + (int) (pixR * laplacian[idx] * alpha);
						newG = newG + (int) (pixG * laplacian[idx] * alpha);
						newB = newB + (int) (pixB * laplacian[idx] * alpha);
						idx++;
					}
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[i * width + k] = Color.argb(255, newR, newG, newB);
				newR = 0;
				newG = 0;
				newB = 0;
			}
		}
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	// 浮雕效果

	private Bitmap embossImageAmeliorate(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++)
		{
			for (int k = 1, len = width - 1; k < len; k++)
			{
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				pixColor = pixels[pos + 1];
				newR = Color.red(pixColor) - pixR + 127;
				newG = Color.green(pixColor) - pixG + 127;
				newB = Color.blue(pixColor) - pixB + 127;

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[pos] = Color.argb(255, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	// 底片效果
	private Bitmap filmImageAmeliorate(Bitmap bmp) {
		// RGBA 的最大值
		final int MAX_VALUE = 255;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++)
		{
			for (int k = 1, len = width - 1; k < len; k++)
			{
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = MAX_VALUE - pixR;
				newG = MAX_VALUE - pixG;
				newB = MAX_VALUE - pixB;

				newR = Math.min(MAX_VALUE, Math.max(0, newR));
				newG = Math.min(MAX_VALUE, Math.max(0, newG));
				newB = Math.min(MAX_VALUE, Math.max(0, newB));

				pixels[pos] = Color.argb(MAX_VALUE, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	// 光照效果
	public Bitmap sunshineAmeliorate(Bitmap bmp) {

		final int width = bmp.getWidth();
		final int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);

		int pixR = 0;
		int pixG = 0;
		int pixB = 0;

		int pixColor = 0;

		int newR = 0;
		int newG = 0;
		int newB = 0;

		int centerX = width / 2;
		int centerY = height / 2;
		int radius = Math.min(centerX, centerY);

		final float strength = 150F; // 光照强度 100~150
		int[] pixels = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		for (int i = 1, length = height - 1; i < length; i++)
		{
			for (int k = 1, len = width - 1; k < len; k++)
			{
				pos = i * width + k;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = pixR;
				newG = pixG;
				newB = pixB;

				// 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
				int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(
						centerX - k, 2));
				if (distance < radius * radius)
				{
					// 按照距离大小计算增加的光照值
					int result = (int) (strength * (1.0 - Math.sqrt(distance)
							/ radius));
					newR = pixR + result;
					newG = pixG + result;
					newB = pixB + result;
				}

				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));

				pixels[pos] = Color.argb(255, newR, newG, newB);
			}
		}

		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private PopupWindow menuPopupwindow(Context cx) {
		if (window == null)
		{
			window = new PopupWindow(cx);
			View contentView = LayoutInflater.from(this).inflate(
					R.layout.imagehandle_menu, null);
			window.setContentView(contentView);
			window.setWidth(LayoutParams.WRAP_CONTENT);
			window.setHeight(LayoutParams.WRAP_CONTENT);
			oldImageButton = (Button) contentView.findViewById(R.id.old_image);
			oldImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					myDialog = ProgressDialog.show(ImageHandle.this, getResources()
							.getString(R.string.please_wait),
							getResources().getString(R.string.picture_processing));
					new Thread() {
						public void run() {
							try
							{
								// TODO Auto-generated method stub
								bm = ImageDispose.getBitmapFromFile(mfile, 1800, 1800);
								bmp = oldRemeber(bm);
								bm.recycle();
								bm = null;
								System.gc();
								f = new File(CommonEdit.pathPicture, "z"
										+ mfile.getName());
							} catch (Exception e)
							{
								e.printStackTrace();
							} finally
							{ // 卸除所建立的myDialog对象。
								myDialog.dismiss();
								if (ImageHandleGlobal.SaveFile(f,
										ImageDispose.Bitmap2Bytes(bmp)))
								{
									UpdateUI(f);
								}
								bmp.recycle();
								bmp = null;
								System.gc();
								
							}
						}
					}.start();
					window.dismiss();
				}
				
			});
			blurImageButton = (Button) contentView.findViewById(R.id.blur_image);
			blurImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDialog = ProgressDialog.show(ImageHandle.this, getResources()
							.getString(R.string.please_wait),
							getResources().getString(R.string.picture_processing));
					new Thread() {
						public void run() {
							try
							{
								// TODO Auto-generated method stub
								bm = ImageDispose.getBitmapFromFile(mfile, 1800, 1800);
								bmp = blurImageAmeliorate(bm);
								;
								bm.recycle();
								bm = null;
								System.gc();
								f = new File(CommonEdit.pathPicture, "z"
										+ mfile.getName());
							} catch (Exception e)
							{
								e.printStackTrace();
							} finally
							{ // 卸除所建立的myDialog对象。
								myDialog.dismiss();
								if (ImageHandleGlobal.SaveFile(f,
										ImageDispose.Bitmap2Bytes(bmp)))
								{
									UpdateUI(f);
								}
								bmp.recycle();
								bmp = null;
								System.gc();
								
							}
						}
						
					}.start();
					window.dismiss();
				}

			});
			sharpenImageButton = (Button) contentView.findViewById(R.id.sharpen_image);
			sharpenImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDialog = ProgressDialog.show(ImageHandle.this, getResources()
							.getString(R.string.please_wait),
							getResources().getString(R.string.picture_processing));
					new Thread() {
						public void run() {
							try
							{
								// TODO Auto-generated method stub
								bm = ImageDispose.getBitmapFromFile(mfile, 1800, 1800);
								bmp = sharpenImageAmeliorate(bm);
								bm.recycle();
								bm = null;
								System.gc();
								f = new File(CommonEdit.pathPicture, "z"
										+ mfile.getName());
							} catch (Exception e)
							{
								e.printStackTrace();
							} finally
							{ // 卸除所建立的myDialog对象。
								myDialog.dismiss();
								if (ImageHandleGlobal.SaveFile(f,
										ImageDispose.Bitmap2Bytes(bmp)))
								{
									UpdateUI(f);
								}
								bmp.recycle();
								bmp = null;
								System.gc();
						
							}
						
						}
					}.start();
					window.dismiss();
				}
			});

			embossImageButton = (Button)contentView.findViewById(R.id.emboss_image);
			embossImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDialog = ProgressDialog.show(ImageHandle.this, getResources()
							.getString(R.string.please_wait),
							getResources().getString(R.string.picture_processing));
					new Thread() {
						public void run() {
							try
							{
								// TODO Auto-generated method stub
								bm = ImageDispose.getBitmapFromFile(mfile, 1800, 1800);
								bmp = embossImageAmeliorate(bm);
								bm.recycle();
								bm = null;
								System.gc();
								f = new File(CommonEdit.pathPicture, "z"
										+ mfile.getName());
							} catch (Exception e)
							{
								e.printStackTrace();
							} finally
							{ // 卸除所建立的myDialog对象。
								myDialog.dismiss();
								if (ImageHandleGlobal.SaveFile(f,
										ImageDispose.Bitmap2Bytes(bmp)))
								{
									UpdateUI(f);
								}
								bmp.recycle();
								bmp = null;
								System.gc();
							
							}
						
						}
					}.start();
					window.dismiss();
				}
			});

			filmImageButton = (Button)contentView.findViewById(R.id.film_image);
			filmImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDialog = ProgressDialog.show(ImageHandle.this, getResources()
							.getString(R.string.please_wait),
							getResources().getString(R.string.picture_processing));
					new Thread() {
						public void run() {
							try
							{
								// TODO Auto-generated method stub
								bm = ImageDispose.getBitmapFromFile(mfile, 1800, 1800);
								bmp = filmImageAmeliorate(bm);
								bm.recycle();
								bm = null;
								System.gc();
								f = new File(CommonEdit.pathPicture, "z"
										+ mfile.getName());
							} catch (Exception e)
							{
								e.printStackTrace();
							} finally
							{ // 卸除所建立的myDialog对象。
								myDialog.dismiss();
								if (ImageHandleGlobal.SaveFile(f,
										ImageDispose.Bitmap2Bytes(bmp)))
								{
									UpdateUI(f);
								}
								bmp.recycle();
								bmp = null;
								System.gc();
								
							}
						
						}
					}.start();
					window.dismiss();
				}
			});

			sunshineImageButton = (Button) contentView.findViewById(R.id.sunshine_image);
			sunshineImageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myDialog = ProgressDialog.show(ImageHandle.this, getResources()
							.getString(R.string.please_wait),
							getResources().getString(R.string.picture_processing));
					new Thread() {
						public void run() {
							try
							{
								// TODO Auto-generated method stub
								bm = ImageDispose.getBitmapFromFile(mfile, 1800, 1800);
								bmp = sunshineAmeliorate(bm);
								bm.recycle();
								bm = null;
								System.gc();
								f = new File(CommonEdit.pathPicture, "z"
										+ mfile.getName());
							} catch (Exception e)
							{
								e.printStackTrace();
							} finally
							{ // 卸除所建立的myDialog对象。
								myDialog.dismiss();
								if (ImageHandleGlobal.SaveFile(f,
										ImageDispose.Bitmap2Bytes(bmp)))
								{
									UpdateUI(f);
								}
								bmp.recycle();
								bmp = null;
								System.gc();
							
							}
						
						}
						
					}.start();
					window.dismiss();
				}
			});

			// 设置PopupWindow外部区域是否可触摸
			window.setFocusable(true); // 设置PopupWindow可获得焦点
			window.setTouchable(true); // 设置PopupWindow可触摸
			window.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
			// window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			// window.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
		}
		return window;
	}

}
