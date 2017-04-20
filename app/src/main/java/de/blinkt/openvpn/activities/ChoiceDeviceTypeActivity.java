package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.activities.MyDeviceActivity.BLUESTATUSFROMPROMAIN;

public class ChoiceDeviceTypeActivity extends BaseActivity {

	@BindView(R.id.aixiaoqi1RelativeLayout)
	RelativeLayout aixiaoqi1RelativeLayout;
	@BindView(R.id.uniboxRelativeLayout)
	RelativeLayout uniboxRelativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice_device_type);
		ButterKnife.bind(this);
		init();
	}

	private void init() {
		hasLeftViewTitle(R.string.choice_bind_device, 0);
	}

	@OnClick({R.id.aixiaoqi1RelativeLayout, R.id.uniboxRelativeLayout})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.aixiaoqi1RelativeLayout:
				toMyDevice(MyDeviceActivity.UNITOYS);
				break;
			case R.id.uniboxRelativeLayout:
				toMyDevice(MyDeviceActivity.UNIBOX);
				break;

		}
	}

	private void toMyDevice(String type) {
		String blueStatus = getIntent().getStringExtra(BLUESTATUSFROMPROMAIN);
		Intent intent = new Intent(this, BindDeviceActivity.class);
		intent.putExtra(MyDeviceActivity.BRACELETTYPE, type);
		intent.putExtra(MyDeviceActivity.BLUESTATUSFROMPROMAIN, blueStatus);
		SharedUtils.getInstance().writeString(MyDeviceActivity.BRACELETTYPE,type);
		startActivity(intent);
		finish();
	}
}
