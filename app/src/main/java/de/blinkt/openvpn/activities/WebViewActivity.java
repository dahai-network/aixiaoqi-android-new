package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.com.aixiaoqi.R;
import cn.com.johnson.widget.CustomWebViewLayout;
import de.blinkt.openvpn.activities.Base.BaseActivity;

public class WebViewActivity extends BaseActivity {

	private static final String TAG_URL = "url";
	private static final String TAG_TITLE = "title";
	private CustomWebViewLayout layout;

	public static void launch(Context c, String url, String title) {
		Intent intent = new Intent(c, WebViewActivity.class);
		intent.putExtra(TAG_URL, url);
		intent.putExtra(TAG_TITLE, title);
		c.startActivity(intent);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		layout = (CustomWebViewLayout) findViewById(R.id.webview);
		titleBar.setTextTitle(getIntent().getStringExtra(TAG_TITLE));
		titleBar.setLeftBtnIcon(R.drawable.btn_top_back);
		titleBar.getLeftText().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		String url = getIntent().getStringExtra(TAG_URL);
		if (url == null) return;
		layout.setUrl(this, url, url);


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		layout.setVisibility(View.GONE);
		layout.mWebView.clearCache(false);
		layout.removeAllViews();
		layout = null;
	}
}
