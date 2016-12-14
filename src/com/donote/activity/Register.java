package com.donote.activity;

import java.io.IOException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.baidu.mobstat.StatService;
import com.donote.util.HttpUtil;
import com.donote.util.ResizeLayout;
import com.wxl.donote.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Register extends Activity {

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

	private static final int MSG_RESIZE = 3;
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private Button registerButton;
	private ResizeLayout mainLayout;
	private RelativeLayout logoLayout;
	private Button returnButton;
	private EditText account_name;
	private EditText password;
	private SharedPreferences account;
	private EditText retypePassword;
	private ProgressDialog myDialog = null;
	private HttpResponse httpResponse;
	private String register_url = "http://1.hankwing.duapp.com/Register";
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what)
			{
			case 0:// 注册成功后登录
				Intent intent = new Intent(Register.this, MainActivity.class);
				intent.putExtra("sync", true);
				setResult(RESULT_OK);
				startActivity(intent);
				finish();
				break;
			case MSG_RESIZE:
			{
				if (msg.arg1 == BIGGER)
				{
					logoLayout.setVisibility(View.VISIBLE);
				} else
				{
					logoLayout.setVisibility(View.GONE);
				}
			}
			}
		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		registerButton = (Button) findViewById(R.id.register);
		logoLayout = (RelativeLayout) findViewById(R.id.register_logo);
		mainLayout = (ResizeLayout) findViewById(R.id.common_register_fields);
		returnButton = (Button) findViewById(R.id.return_to_mainview);
		account_name = (EditText) findViewById(R.id.register_account);
		password = (EditText) findViewById(R.id.register_password);
		retypePassword = (EditText) findViewById(R.id.register_retype_password);
		returnButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
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

		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!password.getText().toString()
						.equals(retypePassword.getText().toString()))
				{
					Toast.makeText(Register.this,
							getResources().getString(R.string.inconsistent),
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (account_name.getText().toString().length() > 16
						|| password.getText().toString().length() > 16
						|| account_name.getText().toString().length() < 6
						|| password.getText().toString().length() < 6)
				{
					Toast.makeText(Register.this,
							getResources().getString(R.string.password_tip),
							Toast.LENGTH_SHORT).show();
					return;
				}
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(Register.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				register(account_name.getText().toString(), password.getText()
						.toString());
			}
		});

	}

	private void register(final String account_name,
			final String account_password) {

		myDialog = ProgressDialog.show(Register.this,
				getResources().getString(R.string.please_wait), getResources()
						.getString(R.string.register));

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
					myDialog.dismiss();
					if (httpResponse == null)
					{
						Looper.prepare();
						Toast.makeText(
								Register.this,
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
							Log.i("bae", resultString);
							if (resultString.equals("OK"))
							{
								Looper.prepare();
								Toast.makeText(
										Register.this,
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
							} else
							{
								Looper.prepare();
								Toast.makeText(
										Register.this,
										getResources().getString(
												R.string.account_error2),
										Toast.LENGTH_SHORT).show();
								Looper.loop();
							}

						} else
						{
							Looper.prepare();
							Toast.makeText(
									Register.this,
									getResources().getString(
											R.string.login_failed),
									Toast.LENGTH_SHORT).show();
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

}
