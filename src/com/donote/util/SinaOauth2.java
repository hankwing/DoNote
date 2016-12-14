package com.donote.util;

import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.net.AsyncWeiboRunner;
import com.weibo.sdk.android.net.RequestListener;
/**
 * 
 * @author doMen
 */
public class SinaOauth2 {
	
	public static final String OAUTH2_SERVER = "https://api.weibo.com/oauth2";
	private String code;
	private String client_id = "1072407155";
	private String client_secret = "c996d4a90e218d828ae7e345b4c29aa7";
	private String grant_type = "authorization_code";
	private String redirect_uri = "https://api.weibo.com/oauth2/default.html";
	
	public SinaOauth2(String code) {
		this.code = code;
    }

    private static final String SERVER_URL_PRIX = OAUTH2_SERVER + "/access_token";

    public void getAccessToken( RequestListener listener) {
		WeiboParameters params = new WeiboParameters();
		params.add("client_id", client_id);
		params.add("client_secret", client_secret);
		params.add("grant_type", grant_type);
		params.add("code", code);
		params.add("redirect_uri", redirect_uri);
		request(SERVER_URL_PRIX, params, "POST", listener);
	}
	
	protected void request( final String url, final WeiboParameters params,
			final String httpMethod,RequestListener listener) {
		AsyncWeiboRunner.request(url, params, httpMethod, listener);
	}

}
