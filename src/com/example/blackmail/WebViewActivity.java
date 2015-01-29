package com.example.blackmail;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
	
	private WebView webView;
	
	public static String requestURL = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_webview);
		
		setTitle("Login to Twitter");

		String url = this.getIntent().getStringExtra(requestURL);
		if (null == url) {
			Log.e("Twitter", "URL cannot be null");
			finish();
		}

		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new webClient());
		webView.loadUrl(url);
	}


	class webClient extends WebViewClient {
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (url.contains("http://blackmail.android.app")) {
				Uri uri = Uri.parse(url);
				
				/* Sending results back */
				String verifier = uri.getQueryParameter("oauth_verifier");
				Intent resultIntent = new Intent();
				resultIntent.putExtra("oauth_verifier", verifier);
				setResult(RESULT_OK, resultIntent);
				
				/* closing webview */
				finish();
				return true;
			}
			return false;
		}
	}

}
