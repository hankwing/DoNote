package com.donote.util;

import java.util.Calendar;

import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
/**
 * 该类用于保存Oauth2AccessToken到sharepreference，并提供读取功能
 * @author xiaowei6@staff.sina.com.cn
 *
 */
public class BaiduAccessTokenKeeper {
	private static final String PREFERENCES_NAME = "baidu_netdisk";
	/**
	 * 保存accesstoken到SharedPreferences
	 * @param context Activity 上下文环�?	 * @param token Oauth2AccessToken
	 */
	public static void keepAccessToken(Context context, BaiduOAuthResponse token) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString("token", token.getAccessToken());
		editor.putString("refresh_token", token.getRefreshToken());
		editor.putString("user_name", token.getUserName());
		editor.putLong("expiresTime", Long.valueOf(token.getExpiresIn()));
		editor.putLong("date", Calendar.getInstance().getTimeInMillis()/1000);
		editor.commit();
	}
	
	public static void saveUsedSpace(Context context,String status) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString("space", status);
	}
	
	public static boolean isOverTime(Context context) {
		
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		if(Calendar.getInstance().getTimeInMillis()/1000 - 
				pref.getLong("date", 0) > pref.getLong("expiresTime", 0)) {
			return true;
		}
		return false;
		
	}
	/**
	 * 清空sharepreference
	 * @param context
	 */
	public static void clear(Context context){
	    SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
	    Editor editor = pref.edit();
	    editor.clear();
	    editor.commit();
	}

	/**
	 * 从SharedPreferences读取accessstoken
	 * @param context
	 * @return Oauth2AccessToken
	 */
	public static BaiduOAuthResponse readAccessToken(Context context){
		BaiduOAuthResponse token = new BaiduOAuthResponse();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		token.setAccessToken(pref.getString("token", ""));
		token.setExpiresIn(String.valueOf(pref.getLong("expiresTime", 0)));
		token.setRefreshToken(pref.getString("refresh_token", ""));
		token.setUserName(pref.getString("user_name", ""));
		return token;
	}
}
