package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.ParticularAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.ParticularHttp;
import de.blinkt.openvpn.util.CommonTools;

public class MonthlyOrderActivity extends BaseNetActivity {

	@BindView(R.id.retryTextView)
	TextView retryTextView;
	@BindView(R.id.NoNetRelativeLayout)
	RelativeLayout NoNetRelativeLayout;
	@BindView(R.id.NodataRelativeLayout)
	RelativeLayout NodataRelativeLayout;
	@BindView(R.id.particularsDetailRecyclerView)
	RecyclerView particularsDetailRecyclerView;
	private String TAG = "MonthlyOrderActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_monthly);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.bill_detail, 0);
		particularsDetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		addData();
	}

	private void addData() {
		createHttpRequest(HttpConfigUrl.COMTYPE_PARTICULAR, 1 + "", Constant.PAGESIZE + "", getIntent().getStringExtra("ID"));
	}

	@OnClick(R.id.retryTextView)
	public void onViewClicked() {
		addData();
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		ParticularHttp http = (ParticularHttp) object;
		Log.i(TAG, http.getParticularEntity().toString());
		if (http.getStatus() == 1) {
			ParticularAdapter particularAdapter = new ParticularAdapter(this, http.getParticularEntity().getList(), true);
			particularsDetailRecyclerView.setAdapter(particularAdapter);
		} else {
			CommonTools.showShortToast(this, http.getMsg());
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(this, errorMessage);
	}

	@Override
	public void noNet() {
		NoNetRelativeLayout.setVisibility(View.VISIBLE);
	}
}
