package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.OrderAdapter;
import cn.com.johnson.model.BoughtPackageEntity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.BoughtPacketHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

import static de.blinkt.openvpn.constant.UmengContant.CLICKBEFOREABROADPURCHASEPACKAGE;

/**
 * 订单列表，用于显示购买过的套餐
 */
public class BeforeGoingAboradActivity extends BaseNetActivity implements XRecyclerView.LoadingListener {
	@BindView(R.id.orderListRecylerView)
	XRecyclerView orderListRecylerView;
	@BindView(R.id.rootLinearLayout)
	LinearLayout rootLinearLayout;
	@BindView(R.id.retryTextView)
	TextView retryTextView;
	@BindView(R.id.NoNetRelativeLayout)
	RelativeLayout NoNetRelativeLayout;
	@BindView(R.id.noDataTextView)
	TextView noDataTextView;

	@BindView(R.id.NodataRelativeLayout)
	RelativeLayout NodataRelativeLayout;

	private OrderAdapter orderAdapter;

	private String TAG = "MyPackageActivity";
	private int pageNumber = 1;
	private LinearLayoutManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_before_going_aborad);
		ButterKnife.bind(this);
		init();
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
		super.onSaveInstanceState(outState, outPersistentState);
		int position = manager.findFirstVisibleItemPosition();
		outState.putInt("position", position);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		int position = savedInstanceState.getInt("position");
		orderListRecylerView.scrollToPosition(position);
	}


	//初始化
	private void init() {
		hasAllViewTitle(R.string.before_going_abroad,R.string.purchase_packet,-1,false);
		manager = new LinearLayoutManager(this);
		orderListRecylerView.setLayoutManager(manager);
		orderListRecylerView.setArrowImageView(R.drawable.iconfont_downgrey);
		orderListRecylerView.setLoadingListener(this);
		orderAdapter = new OrderAdapter(BeforeGoingAboradActivity.this, R.layout.item_before_going_aborad);
		orderListRecylerView.setAdapter(orderAdapter);
	}

	@Override
	protected void onClickRightView() {
		//友盟方法统计
		MobclickAgent.onEvent(BeforeGoingAboradActivity.this, CLICKBEFOREABROADPURCHASEPACKAGE);
		toActivity(PackageMarketActivity.class);
	}

	//加入数据
	private void addData() {
		BoughtPacketHttp http = new BoughtPacketHttp(this, HttpConfigUrl.COMTYPE_GET_ORDER, pageNumber, 20);
		new Thread(http).start();
	}


	@Override
	public void onRefresh() {
		orderListRecylerView.canMoreLoading();
		pageNumber = 1;
		addData();
	}

	@Override
	public void onLoadMore() {
		pageNumber = pageNumber + 1;
		addData();
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GET_ORDER) {
			orderListRecylerView.loadMoreComplete();
			orderListRecylerView.refreshComplete();
			BoughtPacketHttp http = (BoughtPacketHttp) object;
			BoughtPackageEntity bean = http.getBoughtPackageEntity();
			if (bean != null) {
				if (bean.getList().size() != 0) {
					//有数据则显示
					NoNetRelativeLayout.setVisibility(View.GONE);
					orderListRecylerView.setVisibility(View.VISIBLE);
					if (pageNumber == 1) {
						if (bean.getList().size() < 20) {
							orderAdapter.addAll(bean.getList());
							orderListRecylerView.noMoreLoading();
						}else {
							orderAdapter.addAll(bean.getList());
						}

					} else {
						orderAdapter.add(bean.getList());
					}
				} else {
					if (pageNumber == 1) {
						orderListRecylerView.setVisibility(View.GONE);
						NodataRelativeLayout.setVisibility(View.VISIBLE);
						noDataTextView.setText(getResources().getString(R.string.no_order));
					}
					orderListRecylerView.noMoreLoading();
				}
			}
		}
		orderAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();
		onRefresh();
	}



	@Override
	public void noNet() {
		orderListRecylerView.setVisibility(View.GONE);
		NoNetRelativeLayout.setVisibility(View.VISIBLE);
	}

	@OnClick(R.id.retryTextView)
	public void onClick() {
		addData();
	}
}
