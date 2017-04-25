package cn.com.aixiaoqi.wxapi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import de.blinkt.openvpn.activities.Base.CommenActivity;
import de.blinkt.openvpn.activities.CommitOrderActivity;
import de.blinkt.openvpn.activities.MyOrderDetailActivity;
import de.blinkt.openvpn.activities.PaySuccessActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;

public class WXPayEntryActivity extends CommenActivity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	public static int PAY_PURPOSE=0;
	public static int PAY_RECHARGE=1;
	public static int PAY_ORDER=2;
	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		api = WXAPIFactory.createWXAPI(this, Constant.WEIXIN_APPID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0) {
				SharedPreferences preferences = getSharedPreferences("order", MODE_PRIVATE);
				String orderIdStr = preferences.getString("orderId", null);
				String orderAmount = preferences.getString("orderAmount", null);
				if (orderIdStr != null) {
					ICSOpenVPNApplication.getInstance().finishActivity(CommitOrderActivity.class);
					if(PAY_PURPOSE==PAY_RECHARGE){
					PaySuccessActivity.launch(WXPayEntryActivity.this, PaySuccessActivity.BUY, PaySuccessActivity.WEIXIN, orderAmount, orderIdStr);
					}else if(PAY_PURPOSE==PAY_ORDER){
						MyOrderDetailActivity.launch(this, orderIdStr);
					}
					preferences.edit().clear().commit();
					finish();
				} else {
					onBackPressed();
					Intent intent = new Intent("android.intent.action.recharge");
					sendBroadcast(intent);
					finish();
				}
			} else {
				onBackPressed();
				finish();
			}
		}
	}
}