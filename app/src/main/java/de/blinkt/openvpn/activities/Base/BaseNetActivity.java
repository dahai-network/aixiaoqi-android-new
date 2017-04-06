package de.blinkt.openvpn.activities.Base;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by Administrator on 2016/11/25 0025.
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

	public void createHttpRequest(int cmdType){
		CreateHttpFactory.instanceHttp(this,cmdType);
	}

	public void createHttpRequest(int cmdType,String...params){
		CreateHttpFactory.instanceHttp(this,cmdType,params);
	}
}
