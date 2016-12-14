package com.donote.activity;

import java.io.IOException;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.Authorize;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.component.sso.WeiboToken;
import java.util.HashMap;
import java.util.Properties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.donote.util.ConstantS;
import com.donote.util.HttpUtil;
import com.donote.util.ResizeLayout;
import com.donote.util.UsersAPI;
import com.imax.vmall.sdk.android.common.adapter.ServiceCallback;
import com.imax.vmall.sdk.android.entry.CommonService;
import com.imax.vmall.sdk.android.oauthv2.UserInfo;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.sso.SsoHandler;
import com.wxl.donote.R;

@SuppressLint({ "HandlerLeak" })
public class Login extends Activity {
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private static final int MSG_RESIZE = 3;
	private static final int REQUEST_CODE_LOGIN = 4;
	public CommonService mCommon;
	private ResizeLayout mainLayout;
	private RelativeLayout logoLayout;
	private TextView theThirdTextView;
	private LinearLayout theThirdLinearLayout;
	private ImageView sina;
	//private ImageView qq;
	private ImageView qqWeibo;
	private String DefaultCode = "1072407155";// 新浪等第三方默认密码，选用appkey
	private String sinaCode = null;// 新浪认证授权CODE
	private Button logIn;
	private Button register;
	private EditText account_name;
	private EditText account_password;
	private String register_url = "http://1.hankwing.duapp.com/Register";
	private String login_url = "http://1.hankwing.duapp.com/login";
	private ProgressDialog myDialog = null;
	private HttpResponse httpResponse;
	private SharedPreferences account;
	private Weibo mWeibo;
	private SsoHandler mSsoHandler;
	private UsersAPI sinaUserAPI;
	public static Oauth2AccessToken accessToken;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what)
			{
			case 0:// 注册成功后登录
				Intent intent = new Intent(Login.this, MainActivity.class);
				intent.putExtra("sync", true);
				startActivity(intent);
				finish();
				break;
			case 1:
			{
				Bundle bundle = (Bundle) msg.obj;
				Register(bundle.getString("account_name"), DefaultCode, true);
			}
				break;
			case MSG_RESIZE:
			{
				if (msg.arg1 == BIGGER)
				{
					theThirdLinearLayout.setVisibility(View.VISIBLE);
					theThirdTextView.setVisibility(View.VISIBLE);
					logoLayout.setVisibility(View.VISIBLE);
				} else
				{
					theThirdLinearLayout.setVisibility(View.GONE);
					theThirdTextView.setVisibility(View.GONE);
					logoLayout.setVisibility(View.GONE);
				}
			}
			}
		}
	};

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
		StatService.onResume(this);
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_fields);
		theThirdTextView = (TextView) findViewById(R.id.other_account_login);
		theThirdLinearLayout = (LinearLayout) findViewById(R.id.other_image);
		logoLayout = (RelativeLayout) findViewById(R.id.login_logo);
		/*mWeibo = Weibo.getInstance(ConstantS.APP_KEY, ConstantS.REDIRECT_URL,
				ConstantS.SCOPE);*/
		mainLayout = (ResizeLayout) findViewById(R.id.common_login_fields);
		logIn = (Button) findViewById(R.id.login);
		register = (Button) findViewById(R.id.register);
		account_name = (EditText) findViewById(R.id.account);
		account_password = (EditText) findViewById(R.id.password);
		sina = (ImageView) findViewById(R.id.sina_login);
		//qq = (ImageView) findViewById(R.id.qq_cqq_login);
		qqWeibo = (ImageView) findViewById(R.id.qq_weibo_login);
		sdkInit();
		initButton();
	}

	private void initButton() {
		// TODO Auto-generated method stub
		
		/*qq.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				login("qzone");
			}
		});*/

		qqWeibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Properties util = Util.getConfig();
				if (util != null)
				{
					//auth(801388202, "abe93aa886636b9350b6cf0faaad66a4");
				}
			}
		});

		sina.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//mWeibo.anthorize(Login.this, new AuthDialogListener());
				login("sina");
			}
		});

		mainLayout.setOnResizeListener(new ResizeLayout.OnResizeListener() {

			@Override
			public void OnResize(int w, int h, int oldw, int oldh) {
				// TODO Auto-generated method stub
				int change = BIGGER;
				if (h < oldh)
				{
					change = SMALLER;
				}
				Message msg = new Message();
				msg.what = 3;
				msg.arg1 = change;
				handler.sendMessage(msg);
			}
		});

		logIn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (account_name.getText().toString().length() > 16
						|| account_password.getText().toString().length() > 16
						|| account_name.getText().toString().length() < 6
						|| account_password.getText().toString().length() < 6)
				{
					Toast.makeText(Login.this,
							getResources().getString(R.string.password_tip),
							Toast.LENGTH_SHORT).show();
					return;
				}
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(Login.this.getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				myDialog = ProgressDialog.show(Login.this, getResources()
						.getString(R.string.please_wait), getResources()
						.getString(R.string.logining));
				Login2(account_name.getText().toString(), account_password
						.getText().toString());
			}
		});

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(Login.this, Register.class);
				startActivityForResult(intent, 2);
				/*if (account_name.getText().toString().length() > 16
						|| account_password.getText().toString().length() > 16
						|| account_name.getText().toString().length() < 6
						|| account_password.getText().toString().length() < 6)
				{
					Toast.makeText(Login.this,
							getResources().getString(R.string.password_tip),
							Toast.LENGTH_SHORT).show();
					return;
				}
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(Login.this.getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				myDialog = ProgressDialog.show(Login.this, getResources()
						.getString(R.string.please_wait), getResources()
						.getString(R.string.logining));
				Register(account_name.getText().toString(), account_password
						.getText().toString(), false);
*/
			}
		});
	}

	/*class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {

			sinaCode = values.getString("code");
			if (sinaCode != null)
			{
				//Log.i("bae", "getSinaCode");
				myDialog = ProgressDialog.show(Login.this, getResources()
						.getString(R.string.please_wait), getResources()
						.getString(R.string.logining));
				SinaOauth2 getAccessToken = new SinaOauth2(sinaCode);
				getAccessToken.getAccessToken(new getAccessTokenListener());
			} else
			{
				//Log.i("bae", "getAccess_token");
				myDialog = ProgressDialog.show(Login.this, getResources()
						.getString(R.string.please_wait), getResources()
						.getString(R.string.logining));
				String token = values.getString("access_token");
				String expires_in = values.getString("expires_in");
				Log.i("bae", values.getString("uid"));
				//Log.i("bae", expires_in);
				Oauth2AccessToken oauth2AccessToken = new Oauth2AccessToken(
						token, expires_in);
				sinaUserAPI = new UsersAPI(oauth2AccessToken);
				sinaUserAPI.show(Long.valueOf(values.getString("uid")),
						new RequestListener() {

							@Override
							public void onIOException(IOException arg0) {
								// TODO Auto-generated method stub
								Log.i("bae", "1");
							}

							@Override
							public void onError(WeiboException arg0) {
								// TODO Auto-generated method stub
								Log.i("bae", "2");
							}

							@Override
							public void onComplete4binary(
									ByteArrayOutputStream arg0) {
								// TODO Auto-generated method stub
								Log.i("bae", "3");
							}

							@Override
							public void onComplete(String arg0) {
								// TODO Auto-generated method stub
								JSONObject access = null;
								try
								{
									access = new JSONObject(arg0);
								} catch (JSONException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Message msg = new Message();
								Bundle bundle = new Bundle();
								try
								{
									bundle.putString("account_name", "sina_"
											+ access.getString("screen_name"));
								} catch (JSONException e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								msg.what = 1;
								msg.obj = bundle;
								handler.sendMessage(msg);
							}
						});
			}

		}

		@Override
		public void onError(WeiboDialogError e) {
			Log.i("bae", "1");
		}

		@Override
		public void onCancel() {
			Log.i("bae", "2 :");
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Log.i("bae", "weiboexcet :");
		}

	}*/

	/*class getAccessTokenListener implements RequestListener {

		@Override
		public void onComplete(String arg0) {
			// TODO Auto-generated method stub
			try
			{
				JSONObject access = new JSONObject(arg0);
				Long uid = Long.valueOf(access.getString("uid"));
				accessToken = new Oauth2AccessToken(
						access.getString("access_token"),
						access.getString("expires_in"));
				sinaUserAPI = new UsersAPI(accessToken);
				sinaUserAPI.show(uid, new RequestListener() {

					@Override
					public void onIOException(IOException arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onError(WeiboException arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onComplete4binary(ByteArrayOutputStream arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onComplete(String arg0) {
						// TODO Auto-generated method stub
						JSONObject access = null;
						try
						{
							access = new JSONObject(arg0);
						} catch (JSONException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Message msg = new Message();
						Bundle bundle = new Bundle();
						try
						{
							bundle.putString("account_name",
									"sina_" + access.getString("screen_name"));
						} catch (JSONException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						msg.what = 1;
						msg.obj = bundle;
						handler.sendMessage(msg);
					}
				});

			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onComplete4binary(ByteArrayOutputStream arg0) {
			// TODO Auto-generated method stub
			Log.i("bae", "Ioerror");
		}

		@Override
		public void onError(WeiboException arg0) {
			// TODO Auto-generated method stub
			Log.i("bae", "error");
		}

		@Override
		public void onIOException(IOException arg0) {
			// TODO Auto-generated method stub
			Log.i("bae", "Ioerror");
		}

	}*/

	private void Login2(final String account_name, final String account_password) {
		new Thread(new Runnable() {
			public void run() {
				try
				{
					// TODO Auto-generated method stub
					HashMap<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("account_name", account_name);
					tempMap.put("account_password", account_password);
					httpResponse = HttpUtil.makeRequest(login_url, tempMap);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally
				{ // 卸除所建立的myDialog对象。
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(Login.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					if (httpResponse == null)
					{
						myDialog.dismiss();
						Looper.prepare();
						Toast.makeText(
								Login.this,
								getResources().getString(
										R.string.interner_error),
								Toast.LENGTH_SHORT).show();
						Looper.loop();
						return;
					}
					HttpEntity entity = httpResponse.getEntity();
					try
					{
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK
								&& entity != null)
						{
							String resultString = EntityUtils.toString(entity);
							if (resultString.equals("OK"))
							{
								Message msg = null;
								msg = new Message();
								msg.what = 0;
								handler.sendMessage(msg);
								account = getSharedPreferences("account", 0);
								account.edit()
										.putString("account_name", account_name)
										.putString("account_password",
												account_password).commit();

							} else
							{
								Looper.prepare();
								Toast.makeText(
										Login.this,
										getResources().getString(
												R.string.account_error1),
										Toast.LENGTH_SHORT).show();
								myDialog.dismiss();
								Looper.loop();
							}
						} else
						{
							Looper.prepare();
							Toast.makeText(
									Login.this,
									getResources().getString(
											R.string.login_failed),
									Toast.LENGTH_SHORT).show();
							myDialog.dismiss();
							Looper.loop();
						}
						
					} catch (ParseException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Looper.prepare();
				}
			}
		}).start();
	}

	private void Register(final String account_name,
			final String account_password, final boolean isTheThird) {

		new Thread(new Runnable() {
			public void run() {
				try
				{
					// TODO Auto-generated method stub
					HashMap<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("account_name", account_name);
					tempMap.put("account_password", account_password);
					httpResponse = HttpUtil.makeRequest(register_url, tempMap);
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally
				{ // 卸除所建立的myDialog对象。
					if (httpResponse == null)
					{
						myDialog.dismiss();
						Looper.prepare();
						Toast.makeText(
								Login.this,
								getResources().getString(
										R.string.interner_error),
								Toast.LENGTH_SHORT).show();
						Looper.loop();
						return;
					}
					HttpEntity entity = httpResponse.getEntity();
					try
					{
						if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK
								&& entity != null)
						{
							String resultString = EntityUtils.toString(entity);
							if (resultString.equals("OK"))
							{
								Looper.prepare();
								Toast.makeText(
										Login.this,
										getResources().getString(
												R.string.register_succeed),
										Toast.LENGTH_SHORT).show();
								account = getSharedPreferences("account", 0);
								account.edit()
										.putString("account_name", account_name)
										.putString("account_password",
												account_password).commit();
								Message msg = null;
								msg = new Message();
								msg.what = 0;
								handler.sendMessage(msg);
								Looper.loop();
							} else if (isTheThird)
							{
								Looper.prepare();
								myDialog = ProgressDialog.show(
										Login.this,
										getResources().getString(
												R.string.please_wait),
										getResources().getString(
												R.string.logining));
								Login2(account_name, account_password);
								Looper.loop();
							} else
							{
								Looper.prepare();
								Toast.makeText(
										Login.this,
										getResources().getString(
												R.string.account_error2),
										Toast.LENGTH_SHORT).show();
								myDialog.dismiss();
								Looper.loop();
							}

						} else
						{
							Looper.prepare();
							Toast.makeText(
									Login.this,
									getResources().getString(
											R.string.login_failed),
									Toast.LENGTH_SHORT).show();
							myDialog.dismiss();
							Looper.loop();
						}
						
					} catch (ParseException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}).start();
	}

	private void auth(long appid, String app_secket) {
		final Context context = this.getApplicationContext();
		Util.clearSharePersistent(context, "OPEN_ID");
		AuthHelper.register(this, appid, app_secket, new OnAuthListener() {

			@Override
			public void onWeiBoNotInstalled() {
				Intent i = new Intent(Login.this, Authorize.class);
				startActivityForResult(i, 1);
			}

			@Override
			public void onWeiboVersionMisMatch() {
				Intent i = new Intent(Login.this, Authorize.class);
				startActivityForResult(i, 1);
			}

			@Override
			public void onAuthFail(int result, String err) {
				Toast.makeText(
						Login.this,
						getResources().getString(R.string.authorization_falied),
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onAuthPassed(String name, WeiboToken token) {
				Util.saveSharePersistent(context, "ACCESS_TOKEN",
						token.accessToken);
				Util.saveSharePersistent(context, "EXPIRES_IN",
						String.valueOf(token.expiresIn));
				Util.saveSharePersistent(context, "OPEN_ID", token.openID);// 用户唯一凭证
				// Util.saveSharePersistent(context, "OPEN_KEY", token.omasKey);
				Util.saveSharePersistent(context, "REFRESH_TOKEN", "");
				Util.saveSharePersistent(context, "NAME", name);
				Util.saveSharePersistent(context, "NICK", name);
				Util.saveSharePersistent(context, "CLIENT_ID", Util.getConfig()
						.getProperty("APP_KEY"));
				Util.saveSharePersistent(context, "AUTHORIZETIME",
						String.valueOf(System.currentTimeMillis() / 1000l));
				String openID = Util.getSharePersistent(Login.this, "OPEN_ID");
				myDialog = ProgressDialog.show(Login.this, getResources()
						.getString(R.string.please_wait), getResources()
						.getString(R.string.logining));
				Register(openID, DefaultCode, true);
			}
		});

		AuthHelper.auth(this, "");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == REQUEST_CODE_LOGIN && data != null){
    		String ep_id = data.getStringExtra("ep_id");
    		String state = data.getStringExtra("auth_state");
    		if(state != null){
    			if(state.equals("ok")){//用户授权成功
    				myDialog = ProgressDialog.show(Login.this, getResources()
    						.getString(R.string.please_wait), getResources()
    						.getString(R.string.logining));
    				String uid = data.getStringExtra("uid");
    				Message msg = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("account_name", ep_id+"_" + uid);
					msg.what = 1;
					msg.obj = bundle;
					handler.sendMessage(msg);
    				//获取用户信息
    				//getUserInfo(ep_id , uid);
    			}else{//用户授权失败
    				Toast.makeText(this, "用户授权出错", Toast.LENGTH_SHORT).show();
    			}	
    		}
    	}
		// 腾讯微博回调
		else if (requestCode == 1)
		{
			// String name = Util.getSharePersistent(Login.this, "NAME");
			String ID = Util.getSharePersistent(Login.this, "OPEN_ID");
			if (Util.getSharePersistent(Login.this, "OPEN_ID").length() > 10) {
				myDialog = ProgressDialog.show(Login.this, getResources()
						.getString(R.string.please_wait),
						getResources().getString(R.string.logining));
				Register(ID, DefaultCode, true);
			}
		} else if(requestCode == 2 && resultCode == RESULT_OK){
			finish();
		}
	}
	
	 //华为SDK初始化
    public void sdkInit(){
        //获取SDK公共服务类实例
        mCommon = CommonService.getInstance();
        
        //初始化SDK,初始化成功后再调用其他接口
        //appID:APP在IMAX注册应用时分配的APP ID
        //appKey:APP在IMAX注册应用时分配的APP KEY
        mCommon.init(this, "8418448804", "be3e9d3661048e69a51419caa65cee20", new ServiceCallback(){
        	@Override
        	public void onComplete(String result) {
        		// TODO Auto-generated method stub
        		//初始化SDK成功，result是IMAX返回的token
        		
        		String token = result; //或者使用//String token = CommonService.getImaxToken();
        		//通知主线程初始化成功，不要在回调方法中执行会阻塞线程的操作
        	}
        	@Override
        	public void onError(String message) {
        		// TODO Auto-generated method stub
        	}
        });	
    }
    
  //第二步：打开第三方账号登录页面
    //以下参数需要APP自己到第三方平台注册自己的应用时获取
    public void login(String ep_id){
    	//用WebView控件打开登录页面(本质是OAuth授权)
    	Intent it = new Intent(Login.this, LoginActivity.class);
    	it.putExtra("ep_id", ep_id);
    	//APP在新浪微博开放平台注册应用时分配的参数
    	if(ep_id.equals("sina")){
    		it.putExtra("app_key", ConstantS.APP_KEY);
    		it.putExtra("app_secret", ConstantS.APP_SECRET);
    		it.putExtra("redirect_url", ConstantS.REDIRECT_URL);
    	}
    	//APP在人人网开放平台注册应用时分配的参数
    	else if(ep_id.equals("renren")){
    		it.putExtra("app_key", "0102efbe3f124f0082d4f8902544363c");
    		it.putExtra("app_secret", "3bbc88d4f66147acbe2c3b3b017e4ddd");
    		it.putExtra("redirect_url", "http://graph.renren.com/oauth/login_success.html");
    	}
    	//APP在QQ开放平台注册应用时分配的参数
    	else if(ep_id.equals("qzone")){
    		it.putExtra("app_key", "801388202");
    		it.putExtra("app_secret", "abe93aa886636b9350b6cf0faaad66a4");
    		it.putExtra("redirect_url", "http://open.z.qq.com/moc2/success.jsp");
    	}
    	
    	startActivityForResult(it, REQUEST_CODE_LOGIN);
    }
    
  //第三步：获取用户在第三方的用户信息
    /**
     * 
     * @param ep_id 第三方平台标识，例如新浪微博(sina),人人网(renren),QQ空间(qzone)
     * @param uid   用户在第三方平台的标识，在用户授权成功后返回，APP可以保存此id作为用户标识
     */
    public void getUserInfo(String ep_id, String uid){
    	
    	myDialog = ProgressDialog.show(Login.this, getResources()
				.getString(R.string.please_wait), getResources()
				.getString(R.string.logining));
    	
    	UserInfo.getInstance().getUserInfo(ep_id, uid, new ServiceCallback(){
    		@Override
    		public void onComplete(String result) {
    			// TODO Auto-generated method stub
    			try {
					JSONObject obj = new JSONObject(result);
					String ret = obj.getString("ret");
					if(ret.equals("0")){//获取用户信息成功
						//String uid = obj.getString("userid"); //用户在第三方平台的标识
						String epname = obj.getString("epname"); //第三方平台的名称
						String username = obj.getString("username"); //用户名
						String nickname = obj.getString("headurl"); //用户昵称
						//String headurl = obj.getString("headurl"); //用户头像url
						//String largeurl = obj.getString("largeurl"); //用户大头像url
						//String description = obj.getString("description"); //用户描述
		        		//通知主线程显示用户信息，不要在回调方法中执行会阻塞线程的操作
						Message msg = new Message();
						Bundle bundle = new Bundle();
						bundle.putString("account_name", epname+":" + username);
						msg.what = 1;
						msg.obj = bundle;
						handler.sendMessage(msg);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		@Override
    		public void onError(String message) {
    			// TODO Auto-generated method stub
    			/**
    			 * 网络请求发生错误 HTTP Status != 200
    			 */
    			myDialog.dismiss();
    			//服务异常
    		}
    	});	
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		CommonService.getInstance().destory();
	}

}
