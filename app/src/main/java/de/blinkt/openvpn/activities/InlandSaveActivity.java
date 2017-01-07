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
import de.blinkt.openvpn.activities.Base.BaseActivity;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKACTIVENUMBER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSELECTNUMBER;

public class InlandSaveActivity extends BaseActivity {

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
//		hasAllViewTitle(R.string.inland_set, R.string.crowd_funding, -1, false);
//		titleBar.setTextTitle(R.string.inland_set);
		hasLeftViewTitle(R.string.inland_set,0);
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
		toActivity(EBuzOrderListActivity.class);
		//友盟方法统计
		MobclickAgent.onEvent(context, CLICKSELECTNUMBER);
	}
}
