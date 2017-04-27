package de.blinkt.openvpn.activities.Base;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;

/**
 * Created by kim
 * on 2016/11/25 0025.
 */
public class BaseNetActivity extends BaseActivity implements InterfaceCallback {

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {

	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		dismissProgress();
	}

	@Override
	public void noNet() {
		dismissProgress();
		CommonTools.showShortToast(mContext, getString(R.string.no_wifi));
	}

	protected void createHttpRequest(int cmdType) {

		CreateHttpFactory.instanceHttp(this, cmdType);
	}

	protected void createHttpRequestNoCache(int cmdType) {
		if (hasWiFi()) {
			CreateHttpFactory.instanceHttp(this, cmdType);
		}
	}

	private  boolean hasWiFi(){
		if(!NetworkUtils.isNetworkAvailable(this)){
			dismissProgress();
			CommonTools.showShortToast(mContext, getString(R.string.no_wifi));
			return false;
		}
		return  true;
	}

	protected void createHttpRequest(int cmdType, String... params) {
		CreateHttpFactory.instanceHttp(this, cmdType, params);
	}
	protected void createHttpRequestNoCache(int cmdType, String... params) {
		if(hasWiFi()){
			CreateHttpFactory.instanceHttp(this, cmdType, params);
		}
	}

}
