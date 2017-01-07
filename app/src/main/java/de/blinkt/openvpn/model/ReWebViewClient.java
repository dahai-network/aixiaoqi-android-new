package de.blinkt.openvpn.model;

import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Administrator on 2016/12/1.
 */

public class ReWebViewClient extends WebViewClient {

	@Override
	public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
		return super.shouldOverrideKeyEvent(view, event);
	}
}
