package de.blinkt.openvpn.activities.Base;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.http.CommonHttp;
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
		CommonTools.showShortToast(mContext, errorMessage);
	}

	@Override
	public void noNet() {
		CommonTools.showShortToast(mContext, getString(R.string.no_wifi));
	}
}
