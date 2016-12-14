package com.donote.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSClient;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.donote.activity.MainActivity;
import com.donote.adapter.NoteDbAdapter;
import com.wxl.donote.R;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BaiduApiUtil {
	private static String patternString = "(Photo|Video|Record|Draw|File|Picture){1}\\^_\\^\\[(.*?)\\]{1,2}\\^_\\^";
	private Context mContext;
	public static String mbOauth = "";
	private final static String mbApiKey = "vlkV1EUhdRcyUFqVvC0WGWFh";
	private final static String mbSK = "G8a2QCORZkN4vRLHxOTGDH4DU3NgtQhK";
	private final static String mbRootPath = "/apps/Donote";
	public static BaiduOAuthResponse access_token;
	private Matcher photoMatcher;
	private ArrayList<Thread> threads = new ArrayList<Thread>();
	public long totalSpace;
	public long usedSpace;
	private Handler mHandler;

	public BaiduApiUtil(Context context, Handler handler) {
		access_token = BaiduAccessTokenKeeper.readAccessToken(context);
		mContext = context;
		mbOauth = access_token.getAccessToken();
		mHandler = handler;
		if (BaiduAccessTokenKeeper.isOverTime(context))
		{
			BaiduAccessTokenKeeper.clear(context);
			// getRefreshAccess();
		}
	}

	public void getRefreshAccess() {

		new Thread(new Runnable() {
			public void run() {

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("grant_type", "refresh_token"));
				params.add(new BasicNameValuePair("refresh_token", access_token
						.getRefreshToken()));
				params.add(new BasicNameValuePair("client_id", mbApiKey));
				params.add(new BasicNameValuePair("client_secret", mbSK));
				params.add(new BasicNameValuePair("scope", "basic netdisk"));
				HttpParams parms = new BasicHttpParams();
				//parms.setParameter("grant_type", "refresh_token");
				//parms.setParameter("refresh_token",
						//access_token.getRefreshToken());
				//parms.setParameter("client_id", mbApiKey);
				//parms.setParameter("client_secret", mbSK);
				//parms.setParameter("scope", "basic netdisk");
				String reJsonString = HttpUtil.postData(
						"https://openapi.baidu.com/oauth/2.0/token", parms);
				// Log.i("bae", reJsonString);
			}
		}).start();
	}

	public void login() {

		if (Long.valueOf(access_token.getExpiresIn()) != 0)
		{
			// Log.i("bae", "expiretime:" + access_token.getExpiresIn());
			return;
		}

		BaiduOAuth oauthClient = new BaiduOAuth();
		oauthClient.startOAuth(mContext, mbApiKey, new String[] { "basic",
				"netdisk" }, new BaiduOAuth.OAuthListener() {
			@Override
			public void onException(String msg) {
				Toast.makeText(
						mContext,
						mContext.getResources()
								.getString(R.string.login_failed),
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(BaiduOAuthResponse response) {
				if (null != response)
				{
					mbOauth = response.getAccessToken();

					BaiduAccessTokenKeeper.keepAccessToken(mContext, response);
					access_token = BaiduAccessTokenKeeper
							.readAccessToken(mContext);
					Message msg = new Message();
					msg.what = 16;
					mHandler.sendMessage(msg);
				}
			}

			@Override
			public void onCancel() {
			}
		});
	}

	public void downLoad(Cursor mNoteCursor, Handler handler) {
		if (mbOauth == "")
		{
			login();
			return;
		} else
		{
			threads.clear();
			mNoteCursor.moveToFirst();
			int i = 0;
			while (!mNoteCursor.isAfterLast())
			{
				String houzhuiString = null;
				if (mNoteCursor.getInt(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE)) == 0)
				{
					houzhuiString = "]^_^";
				} else
				{
					houzhuiString = "][";
				}
				Pattern photoPattern = Pattern.compile(patternString);
				photoMatcher = photoPattern
						.matcher(mNoteCursor.getString(mNoteCursor
								.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
				while (photoMatcher.find())
				{
					if (photoMatcher.group(1).equals("Photo"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("Photo^_^[") + 9,
								idString.indexOf(houzhuiString));
						if (!new File(idString).exists())
						{
							test_clouddownload(idString, "Photo", i, handler);
							i++;
						}
					}
					// 判断录音
					else if (photoMatcher.group(1).equals("Record"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("Record^_^[") + 10,
								idString.indexOf(houzhuiString));
						if (!new File(idString).exists())
						{
							test_clouddownload(idString, "Record", i, handler);
							i++;
						}

					}

					// 判断画图
					else if (photoMatcher.group(1).equals("Draw"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("Draw^_^[") + 8,
								idString.indexOf(houzhuiString));
						if (!new File(idString).exists())
						{
							test_clouddownload(idString, "Draw", i, handler);
							i++;
						}
					}

					// 判断附件
					else if (photoMatcher.group(1).equals("File"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("File^_^[") + 8,
								idString.indexOf(houzhuiString));
						if (!new File(idString).exists())
						{
							test_clouddownload(idString, "File", i, handler);
							i++;
						}
					}

					// 判断图片
					else if (photoMatcher.group(1).equals("Picture"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("Picture^_^[") + 11,
								idString.indexOf(houzhuiString));
						if (!new File(idString).exists())
						{
							test_clouddownload(idString, "Picture", i, handler);
							i++;
						}
					}

					// 判断视频
					else if (photoMatcher.group(1).equals("Video"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("Video^_^[") + 9,
								idString.indexOf(houzhuiString));
						if (!new File(idString).exists())
						{
							test_clouddownload(idString, "Video", i, handler);
							i++;
						}
					}
				}
				mNoteCursor.moveToNext();
			}
			if (!threads.isEmpty())
			{
				threads.get(0).start();
			} else
			{
				Message msg2 = new Message();
				msg2.arg1 = -1;// 代表完成
				msg2.what = 18;
				handler.sendMessage(msg2);
			}
		}
	}

	private void test_clouddownload(final String tmpFile,
			final String FileBrow, final int i, final Handler handler) {
		if ("" != mbOauth)
		{
			final File tempFile = new File(tmpFile);
			Thread workThread = new Thread(new Runnable() {
				public void run() {
					File dir = new File(tempFile.getParent());
					if (!dir.exists())
						dir.mkdirs();
					Message msg = new Message();
					msg.arg1 = 0;
					msg.what = 18;
					msg.obj = tempFile.getName();
					handler.sendMessage(msg);
					BaiduPCSClient api = new BaiduPCSClient();
					api.setAccessToken(mbOauth);
					String source = mbRootPath + "/" + FileBrow + "/"
							+ tempFile.getName();
					final BaiduPCSActionInfo.PCSSimplefiedResponse ret = api
							.downloadFileFromStream(source,
									tempFile.getAbsolutePath(),
									new BaiduPCSStatusListener() {
										// yangyangdd
										@Override
										public void onProgress(long bytes,
												long total) {
											// TODO Auto-generated method stub
											final long bs = bytes;
											final long tl = total;
											Message msg = new Message();
											msg.arg1 = (int) (bs * 100 / tl);
											msg.what = 18;
											msg.obj = tempFile.getName();
											handler.sendMessage(msg);
										}

										@Override
										public long progressInterval() {
											return 500;
										}

									});
					if (i + 1 < threads.size())
					{
						threads.get(i + 1).start();
					} else
					{
						Message msg2 = new Message();
						msg2.arg1 = -1;
						msg2.what = 18;
						handler.sendMessage(msg2);
					}
				}
			});
			threads.add(workThread);
		}
	}

	public void sync(Cursor mNoteCursor, Handler handler) {
		if (mbOauth == "")
		{
			login();
			return;
		} else
		{
			threads.clear();
			mNoteCursor.moveToFirst();
			int i = 0;
			while (!mNoteCursor.isAfterLast())
			{
				String houzhuiString = null;
				if (mNoteCursor.getInt(mNoteCursor
						.getColumnIndexOrThrow(NoteDbAdapter.KEY_STYLE)) == 0)
				{
					houzhuiString = "]^_^";
				} else
				{
					houzhuiString = "][";
				}
				Pattern photoPattern = Pattern.compile(patternString);
				photoMatcher = photoPattern
						.matcher(mNoteCursor.getString(mNoteCursor
								.getColumnIndexOrThrow(NoteDbAdapter.KEY_BODY)));
				while (photoMatcher.find())
				{
					if (photoMatcher.group(1).equals("Photo"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("Photo^_^[") + 9,
								idString.indexOf(houzhuiString));
						upload(idString, "Photo", i, handler);
						i++;
					}
					// 判断录音
					else if (photoMatcher.group(1).equals("Record"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("Record^_^[") + 10,
								idString.indexOf(houzhuiString));
						upload(idString, "Record", i, handler);
						i++;

					}

					// 判断画图
					else if (photoMatcher.group(1).equals("Draw"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("Draw^_^[") + 8,
								idString.indexOf(houzhuiString));
						upload(idString, "Draw", i, handler);
						i++;

					}

					// 判断附件
					else if (photoMatcher.group(1).equals("File"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("File^_^[") + 8,
								idString.indexOf(houzhuiString));
						upload(idString, "File", i, handler);
						i++;

					}

					// 判断图片
					else if (photoMatcher.group(1).equals("Picture"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("Picture^_^[") + 11,
								idString.indexOf(houzhuiString));
						upload(idString, "Picture", i, handler);
						i++;
					}

					// 判断视频
					else if (photoMatcher.group(1).equals("Video"))
					{
						String idString = photoMatcher.group();
						idString = idString.substring(
								idString.indexOf("Video^_^[") + 9,
								idString.indexOf(houzhuiString));
						upload(idString, "Video", i, handler);
						i++;
					}
				}
				mNoteCursor.moveToNext();
			}
			if (!threads.isEmpty())
			{
				threads.get(0).start();
			} else
			{
				Message msg2 = new Message();
				msg2.arg1 = -1;// 代表完成
				msg2.what = 17;
				handler.sendMessage(msg2);
			}

		}
	}

	/**
	 * 上传文件
	 */
	private void upload(final String tmpFile, final String FileBrow,
			final int i, final Handler handler) {
		final File tempFile = new File(tmpFile);
		if ("" != mbOauth && tempFile.length() > 262144)
		{
			cloudmatchupload(tmpFile, FileBrow, i, handler);
		} else if ("" != mbOauth)
		{
			Thread workThread = new Thread(new Runnable() {
				public void run() {
					Message msg = new Message();
					msg.arg1 = 0;
					msg.what = 17;
					msg.obj = tempFile.getName();
					handler.sendMessage(msg);

					BaiduPCSClient api = new BaiduPCSClient();
					api.setAccessToken(mbOauth);
					final BaiduPCSActionInfo.PCSFileInfoResponse response = api
							.uploadFile(tmpFile, mbRootPath + "/" + FileBrow
									+ "/" + tempFile.getName(),
									new BaiduPCSStatusListener() {

										@Override
										public void onProgress(long bytes,
												long total) {
											// TODO Auto-generated method stub

											final long bs = bytes;
											final long tl = total;
											Message msg = new Message();
											msg.arg1 = (int) (bs * 100 / tl);
											msg.what = 17;
											msg.obj = tempFile.getName();
											handler.sendMessage(msg);
										}

										@Override
										public long progressInterval() {
											return 500;
										}
									});
					if (i + 1 < threads.size())
					{
						threads.get(i + 1).start();
					} else
					{
						Message msg2 = new Message();
						msg2.arg1 = -1;// 代表完成
						msg2.what = 17;
						handler.sendMessage(msg2);
					}

				}
			});
			threads.add(workThread);
		}
	}

	public void cloudmatchupload(final String tmpFile, final String FileBrow,
			final int i, final Handler handler) {
		final File tempFile = new File(tmpFile);
		if (null != mbOauth)
		{
			Thread workThread = new Thread(new Runnable() {
				public void run() {

					Message msg = new Message();
					msg.arg1 = 0;
					msg.what = 17;
					msg.obj = tempFile.getName();
					handler.sendMessage(msg);

					BaiduPCSClient api = new BaiduPCSClient();
					api.setAccessToken(mbOauth);

					final BaiduPCSActionInfo.PCSFileInfoResponse ret = api
							.cloudMatchAndUploadFile(tmpFile, mbRootPath + "/"
									+ FileBrow + "/" + tempFile.getName(),
									new BaiduPCSStatusListener() {

										@Override
										public void onProgress(long bytes,
												long total) {
											// TODO Auto-generated method stub
											final long bs = bytes;
											final long tl = total;
											Message msg = new Message();
											msg.arg1 = (int) (bs * 100 / tl);
											msg.what = 17;
											msg.obj = tempFile.getName();
											handler.sendMessage(msg);
										}

										@Override
										public long progressInterval() {
											return 500;
										}

									});
					if (i + 1 < threads.size())
					{
						threads.get(i + 1).start();
					} else
					{
						Message msg2 = new Message();
						msg2.arg1 = -1;// 代表完成
						msg2.what = 17;
						handler.sendMessage(msg2);
					}
				}
			});
			threads.add(workThread);
		}
	}

	public void getQuota() {
		if (null != mbOauth)
		{
			Thread workThread = new Thread(new Runnable() {

				public void run() {
					BaiduPCSClient api = new BaiduPCSClient();
					api.setAccessToken(mbOauth);
					final BaiduPCSActionInfo.PCSQuotaResponse info = api
							.quota();
					totalSpace = info.total;
					usedSpace = info.used;
					Log.i("bae", "Quota :" + info.total + "  used: "
							+ info.used);
					Log.i("bae", "Quota failed: " + info.status.errorCode
							+ "  " + info.status.message);
					String infoString = mContext.getResources().getString(
							R.string.cloud_space_usage)
							+ ":"
							+ ShowIcon.FormetFileSize(info.used)
							+ "/"
							+ ShowIcon.FormetFileSize(info.total);
					BaiduAccessTokenKeeper.saveUsedSpace(mContext, infoString);
					Message mes = new Message();
					mes.what = 15;
					mes.obj = infoString;
					mHandler.sendMessage(mes);
				}
			});

			workThread.start();
		}
	}
}
