package com.donote.util;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ShareUtil {
	
	public static void shareMsg(Context context, String activityTitle,
			String msgTitle, String msgText, String imgPath) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (imgPath == null || imgPath.equals(""))
		{
			intent.setType("text/plain"); // ´¿ÎÄ±¾
		} else
		{
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile())
			{
				intent.setType("image/png");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, msgText);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, activityTitle));
	}
}
