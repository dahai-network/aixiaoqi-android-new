package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetSelectPhoneNumberUrl;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVENUMBER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSELECTNUMBER;

public class InlandSaveActivity extends BaseNetActivity {

	@BindView(R.id.activateTextView)
	TextView activateTextView;
	@BindView(R.id.getRedBagTextView)
	TextView getRedBagTextView;
	@BindView(R.id.communicationTextView)
	TextView communicationTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inland_save);
		ButterKnife.bind(this);
		hasAllViewTitle(R.string.inland_set, R.string.crowd_funding, -1, false);
//		titleBar.setTextTitle(R.string.inland_set);
//		hasLeftViewTitle(R.string.inland_set,0);
	}

	@OnClick({R.id.activateTextView, R.id.getRedBagTextView, R.id.communicationTextView})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.activateTextView:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKACTIVENUMBER);
				Intent activateIntent = new Intent(this,ActivateLinkCardActivity.class);
				startActivity(activateIntent);
				break;
			case R.id.getRedBagTextView:
				break;
			case R.id.communicationTextView:
				break;
		}
	}

	@Override
	protected void onClickRightView() {
//		toActivity(EBuzOrderListActivity.class);
		if(NetworkUtils.isNetworkAvailable(this)){
			GetSelectPhoneNumberUrl getSelectPhoneNumberUrl=new GetSelectPhoneNumberUrl(this, HttpConfigUrl.COMTYPE_GET_SELECT_NUMBER_URL);
			new Thread(getSelectPhoneNumberUrl).start();
			//友盟方法统计
			MobclickAgent.onEvent(context, CLICKSELECTNUMBER);
		}else {
			CommonTools.showShortToast(this,getString(R.string.no_wifi));
		}
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if(cmdType==HttpConfigUrl.COMTYPE_GET_SELECT_NUMBER_URL){
			GetSelectPhoneNumberUrl getSelectPhoneNumberUrl=(GetSelectPhoneNumberUrl)object;
			if(getSelectPhoneNumberUrl.getStatus()==1){
				String selectPhoneUrl=(String) getSelectPhoneNumberUrl.getData();
				WebViewActivity.launch(this,selectPhoneUrl,getString(R.string.crowd_funding));
			}
		}

	}
}
