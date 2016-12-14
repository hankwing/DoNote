package com.donote.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;

import android.util.Log;

public class HttpUtil {

	public static HttpResponse makeRequest(String path,
			Map<String, Object> params) {
		// instantiates httpclient to make request
		HttpParams parms = new BasicHttpParams();
		parms.setParameter("charset", HTTP.UTF_8);
		parms.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		parms.setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		DefaultHttpClient httpclient = new DefaultHttpClient(parms);

		// url with the post data
		HttpPost httpost = new HttpPost(path);
		// passes the results to a string builder/entity
		StringEntity se = null;
		try
		{
			se = new StringEntity(toJson(params), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// sets the post request as the resulting string
		httpost.setEntity(se);
		// sets a request header so the page receving the request
		// will know what to do with it
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json");
		httpost.addHeader("charset", HTTP.UTF_8);
		// Handles what is returned from the page
		try
		{
			return httpclient.execute(httpost);
		} catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String postData(String posturl,
			HttpParams parms) {
		String strResult = null;
		try
		{		
			parms.setParameter("charset", HTTP.UTF_8);
			DefaultHttpClient httpclient = new DefaultHttpClient(parms);
			
			// 你的URL
			HttpPost httppost = new HttpPost(posturl);
			httppost.addHeader("charset", HTTP.UTF_8);
/*			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));*/
			HttpResponse response;
			response = httpclient.execute(httppost);
			Log.i("bae", "code:" + response.getStatusLine().getStatusCode());
			strResult = EntityUtils.toString(response.getEntity());
			if (response.getStatusLine().getStatusCode() == 200)
			{
				/* 读返回数据 */
				Log.i("bae", "success");
				strResult = EntityUtils.toString(response.getEntity());
			}
		} catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			// return e.toString();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			// return e.toString();
		}
		// Log.d("postData",strResult);
		return strResult;
	}

	public static HttpResponse makeRequest(String path,
			ArrayList<Map<String, Object>> params, String account_name) {
		// instantiates httpclient to make request
		HttpParams parms = new BasicHttpParams();
		parms.setParameter("charset", HTTP.UTF_8);
		parms.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		parms.setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		DefaultHttpClient httpclient = new DefaultHttpClient(parms);
		// url with the post data
		HttpPost httpost = new HttpPost(path);
		// passes the results to a string builder/entity
		StringEntity se = null;
		try
		{
			se = new StringEntity(toJson(params), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// sets the post request as the resulting string
		httpost.setEntity(se);
		// sets a request header so the page receving the request
		// will know what to do with it
		httpost.addHeader("account_name", account_name);
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json");
		httpost.addHeader("charset", HTTP.UTF_8);
		// Handles what is returned from the page
		try
		{
			return httpclient.execute(httpost);
		} catch (ClientProtocolException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String toJson(Map<String, Object> map) {
		Set<String> keys = map.keySet();
		String key = "";
		String value = "";
		StringBuffer jsonBuffer = new StringBuffer();
		jsonBuffer.append("{");
		for (Iterator<String> it = keys.iterator(); it.hasNext();)
		{
			key = (String) it.next();
			value = (String) map.get(key);
			jsonBuffer.append(key + ":" + value);
			if (it.hasNext())
			{
				jsonBuffer.append(",");
			}
		}
		jsonBuffer.append("}");
		return jsonBuffer.toString();
	}

	public static String toJson(ArrayList<Map<String, Object>> arrayMap) {

		StringBuffer allJsonBuffer = new StringBuffer();
		for (int i = 0; i < arrayMap.size(); i++)
		{
			Map<String, Object> map = arrayMap.get(i);
			Set<String> keys = map.keySet();
			String key = "";
			Object value;
			StringBuffer jsonBuffer = new StringBuffer();
			jsonBuffer.append("{");
			for (Iterator<String> it = keys.iterator(); it.hasNext();)
			{
				key = (String) it.next();
				value = map.get(key);
				if (value instanceof Integer)
				{
					jsonBuffer.append("'" + key + "'" + ":" + value);
				} else
				{
					jsonBuffer
							.append("'" + key + "'" + ":" + "'" + value + "'");
				}
				if (it.hasNext())
				{
					jsonBuffer.append(",");
				}
			}
			jsonBuffer.append("}");
			allJsonBuffer.append(jsonBuffer.toString()).append(",");
		}
		allJsonBuffer.deleteCharAt(allJsonBuffer.length() - 1);
		return allJsonBuffer.toString();
	}

	public static String toJson2(Map<String, String> map) {
		Set<Map.Entry<String, String>> entrys = map.entrySet();
		Map.Entry<String, String> entry = null;
		String key = "";
		String value = "";
		StringBuffer jsonBuffer = new StringBuffer();
		jsonBuffer.append("{");
		for (Iterator<Map.Entry<String, String>> it = entrys.iterator(); it
				.hasNext();)
		{
			entry = (Map.Entry<String, String>) it.next();
			key = entry.getKey();
			value = entry.getValue();
			jsonBuffer.append(key + ":" + value);
			if (it.hasNext())
			{
				jsonBuffer.append(",");
			}
		}
		jsonBuffer.append("}");
		return jsonBuffer.toString();
	}

	public static ArrayList<HashMap<String, Object>> AnalysisNotes(
			String jsonStr) throws JarException, JSONException {

		// 初始化list数组对象
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		if (!jsonStr.startsWith("["))
		{
			jsonStr = "[".concat(jsonStr).concat("]");
		}
		JSONArray array = new JSONArray(jsonStr);
		for (int i = 0; i < array.length(); i++)
		{
			JSONObject jsonObject = array.getJSONObject(i);
			// 初始化map数组对象
			HashMap<String, Object> tempMap = new HashMap<String, Object>();
			if (jsonObject.has("title"))
			{
				tempMap.put("_id", "" + jsonObject.getInt("_id"));
				tempMap.put("title", jsonObject.getString("title"));
				tempMap.put("body", jsonObject.getString("body"));
				tempMap.put("catagory", jsonObject.getString("catagory"));
				tempMap.put("created", jsonObject.getString("created"));
				tempMap.put("content", jsonObject.getString("content"));
				tempMap.put("modify", jsonObject.getString("modify"));
				tempMap.put("alarmflag", "" + jsonObject.getInt("alarmflag"));
				tempMap.put("ischecked", "" + jsonObject.getInt("ischecked"));
				tempMap.put("isclocked", "" + jsonObject.getInt("isclocked"));
				tempMap.put("isexpend", "" + jsonObject.getInt("isexpend"));
				tempMap.put("style", "" + jsonObject.getInt("style"));
			} else
			{
				tempMap.put("_id", "" + jsonObject.getInt("_id"));
				tempMap.put("name", jsonObject.getString("name"));
			}
			list.add(tempMap);
		}
		return list;
	}
}
