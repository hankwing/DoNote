package com.donote.imagehandler;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.donote.activity.MainActivity;
import com.donote.util.ShowIcon;
import com.wxl.donote.R;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ImageMemoryCache {
	/**
	 * 从内存读取数据速度是最快的，为了更大限度使用内存，这里使用了两层缓存。 硬引用缓存不会轻易被回收，用来保存常用数据，不常用的转入软引用缓存。
	 */
	private static String patternString = "(Photo|Video|Picture|Draw){1}\\^_\\^\\[(.*?)\\]{1,2}\\^_\\^";
	private static final int SOFT_CACHE_SIZE = 15; // 软引用缓存容量
	private Resources res;
	private static LruCache<Long, Bitmap> mLruCache; // 硬引用缓存
	private static LinkedHashMap<Long, SoftReference<Bitmap>> mSoftCache; // 软引用缓存
	public static ImageFileCache fileCache;
	private static Bitmap tempDrawable;
	private static float Width;
	private static float Height;

	public ImageMemoryCache(Context context) {

		res = context.getResources();
		tempDrawable = BitmapFactory.decodeResource(res, R.drawable.ic_note);
		Width = (float) (tempDrawable.getWidth()*0.83);
		Height = (float) (tempDrawable.getHeight()*0.83);
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		fileCache = new ImageFileCache();
		int memClass = activityManager.getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 4; // 硬引用缓存容量，为系统可用内存的1/4
		mLruCache = new LruCache<Long, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(Long key, Bitmap value) {
				if (value != null)
					return value.getRowBytes() * value.getHeight();
				else
					return 0;
			}

			@Override
			protected void entryRemoved(boolean evicted, Long key,
					Bitmap oldValue, Bitmap newValue) {
				if (oldValue != null)
					// 硬引用缓存容量满的时候，会根据LRU算法把最近没有被使用的图片转入此软引用缓存
					mSoftCache.put(key, new SoftReference<Bitmap>(oldValue));
			}
		};
		mSoftCache = new LinkedHashMap<Long, SoftReference<Bitmap>>(
				SOFT_CACHE_SIZE, 0.75f, true) {
			private static final long serialVersionUID = 6040103833179403725L;

			@Override
			protected boolean removeEldestEntry(
					Entry<Long, SoftReference<Bitmap>> eldest) {
				if (size() > SOFT_CACHE_SIZE)
				{
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * 从缓存中获取图片
	 */
	public static Bitmap getBitmapFromCache(Long url) {
		Bitmap bitmap;
		// 先从硬引用缓存中获取
		if (mLruCache == null || mSoftCache == null)
		{
			return null;
		}

		synchronized (mLruCache)
		{
			bitmap = mLruCache.get(url);
			if (bitmap != null)
			{
				// 如果找到的话，把元素移到LinkedHashMap的最前面，从而保证在LRU算法中是最后被删除
				mLruCache.remove(url);
				mLruCache.put(url, bitmap);
				return bitmap;
			}
		}
		// 如果硬引用缓存中找不到，到软引用缓存中找
		synchronized (mSoftCache)
		{
			SoftReference<Bitmap> bitmapReference = mSoftCache.get(url);
			if (bitmapReference != null)
			{
				bitmap = bitmapReference.get();
				if (bitmap != null)
				{
					// 将图片移回硬缓存
					mLruCache.put(url, bitmap);
					mSoftCache.remove(url);
					return bitmap;
				} else
				{
					mSoftCache.remove(url);
				}
			}
		}
		return null;
	}

	/**
	 * 添加图片到缓存
	 */
	public static void addBitmapToCache(Long url, Bitmap bitmap) {
		if (bitmap != null)
		{
			synchronized (mLruCache)
			{
				mLruCache.put(url, bitmap);
			}
		}
	}

	public void clearCache() {
		mSoftCache.clear();
	}

	public static void deleteCache(Long url) {
		mSoftCache.remove(url);
	}

	public boolean isCached(Long url) {
		if (mSoftCache.containsKey(url))
		{
			return true;
		} else
		{
			return false;
		}
	}

	public void detectImage(String body, Long id) {
		Pattern photoPattern = Pattern.compile(patternString);
		Matcher photoMatcher = photoPattern.matcher(body);
		if (photoMatcher.find())
		{
			if (photoMatcher.group(1).equals("Photo"))
			{
				String idString = photoMatcher.group();
				if(photoMatcher.group(2).contains("][")) {
					idString = idString.substring(
							idString.indexOf("Photo^_^[") + 9,
							idString.indexOf("]["));
				}
				else {
					idString = photoMatcher.group(2);
				}
				Bitmap temp = ShowIcon.readBitmapAutoSize(idString, MainActivity.Width,
						MainActivity.Height, ShowIcon.readPictureDegree(idString));
				if(temp != null) {
					temp = ShowIcon.zoomBitmapTospe(temp, Width, Height);
					temp = ShowIcon.toRoundCorner(temp, (int) (Width*0.15));
					temp.equals(temp);
					addBitmapToCache(id, temp);
					if (!fileCache.isExitUri(idString))
					{
						fileCache.saveBitmap(temp, new File(idString).getName());
					}
				}
			}

			else if (photoMatcher.group(1).equals("Draw"))
			{
				String idString = photoMatcher.group();
				if(photoMatcher.group(2).contains("][")) {
					idString = idString.substring(
							idString.indexOf("Draw^_^[") + 8,
							idString.indexOf("]["));
				}
				else {
					idString = photoMatcher.group(2);
				}
				Bitmap temp = ShowIcon.readBitmapAutoSize(idString, MainActivity.Width,
						MainActivity.Height, ShowIcon.readPictureDegree(idString));
				if(temp != null) {
					temp = ShowIcon.zoomBitmapTospe(temp, Width, Height);
					temp = ShowIcon.toRoundCorner(temp, (int) (Width*0.15));
					temp.equals(temp);
					addBitmapToCache(id, temp);
					if (!fileCache.isExitUri(idString))
					{
						fileCache.saveBitmap(temp, new File(idString).getName());
					}
				}
			}

			else if (photoMatcher.group(1).equals("Picture"))
			{
				String idString = photoMatcher.group();
				if(photoMatcher.group(2).contains("][")) {
					idString = idString.substring(
							idString.indexOf("Picture^_^[") + 11,
							idString.indexOf("]["));
				}
				else {
					idString = photoMatcher.group(2);
				}
				Bitmap temp = ShowIcon.readBitmapAutoSize(idString,MainActivity.Width,
						MainActivity.Height, ShowIcon.readPictureDegree(idString));
				if(temp != null) {
					temp = ShowIcon.zoomBitmapTospe(temp, Width, Height);
					temp = ShowIcon.toRoundCorner(temp, (int) (Width*0.15));
					addBitmapToCache(id, temp);
					if (!fileCache.isExitUri(idString))
					{
						fileCache.saveBitmap(temp, new File(idString).getName());
					}
				}

			} else if (photoMatcher.group(1).equals("Video"))
			{
				String idString = photoMatcher.group();
				if(photoMatcher.group(2).contains("][")) {
					idString = idString.substring(
							idString.indexOf("Video^_^[") + 9,
							idString.indexOf("]["));
				}
				else {
					idString = photoMatcher.group(2);
				}
				Bitmap video = ThumbnailUtils.createVideoThumbnail(idString,
						MediaStore.Images.Thumbnails.MINI_KIND);
				if(video != null) {
					video = ShowIcon.zoomBitmapTospe(video, Width, Height);
					video = ShowIcon.toRoundCorner(video, (int) (Width*0.15));
					addBitmapToCache(id, video);
					if (!fileCache.isExitUri(idString))
					{
						fileCache.saveBitmap(video, new File(idString).getName());
					}
				}
			}
		}
	}

	public static Bitmap getBitmap(Long url, String idString) {
		// 从内存缓存中获取图片
		if (fileCache == null)
		{
			fileCache = new ImageFileCache();
		}
		Bitmap result = getBitmapFromCache(url);
		if (result == null && idString != null)
		{
			// 文件缓存中获取
			result = fileCache.getImage(idString);
		}
		return result;
	}

}