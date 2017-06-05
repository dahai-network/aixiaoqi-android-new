package de.blinkt.openvpn.activities;

import android.os.Bundle;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.http.CommonHttp;

public class FreeWorryOrderDetailActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monthly);
		initSet();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.order_detail, 0);

	}

}
