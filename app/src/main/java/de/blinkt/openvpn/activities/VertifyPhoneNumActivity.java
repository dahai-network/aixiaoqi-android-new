package de.blinkt.openvpn.activities;

import android.os.Bundle;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;

public class VertifyPhoneNumActivity extends BaseNetActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vertify_phone_num);
		initSet();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.phone_vertification, 0);
	}
}
