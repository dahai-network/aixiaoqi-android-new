package de.blinkt.openvpn.activities.MyModules.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.OrderAdapter;
import cn.com.johnson.model.BoughtPackageEntity;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.MyModules.presenter.MyPackagePresenter;
import de.blinkt.openvpn.activities.MyModules.view.MyPackageView;
import de.blinkt.openvpn.activities.Set.ui.CallPackageLlistActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.BoughtPacketHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.dialog.DialogSexAndHeaderAndMyPacket;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;


/**
 * 订单列表，用于显示购买过的套餐
 */
public class MyPackageActivity extends BaseActivity implements XRecyclerView.LoadingListener, DialogInterfaceTypeBase,MyPackageView {
	public static final int BOTTOM_LIST = 1;
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
	MyPackagePresenter myPackagePresenter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_package);
		ButterKnife.bind(this);
		init();


	}

	//初始化
	private void init() {
		hasAllViewTitle(R.string.order_list, R.drawable.order_list_add, 0, true);
		manager = new LinearLayoutManager(this);
		orderListRecylerView.setLayoutManager(manager);
		orderListRecylerView.setArrowImageView(R.drawable.iconfont_downgrey);
		orderListRecylerView.setLoadingListener(this);
		orderAdapter = new OrderAdapter(MyPackageActivity.this, R.layout.item_order);
		orderListRecylerView.setAdapter(orderAdapter);
		myPackagePresenter = new MyPackagePresenter(this);
		onRefresh();
	}

	//加入数据
	private void addData() {
		myPackagePresenter.getOrderData(pageNumber, Constant.PAGESIZE,-1);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (ReceiveBLEMoveReceiver.orderStatus == 1) {
			orderAdapter.changeStatus(1);
			ReceiveBLEMoveReceiver.orderStatus = -1;
		} else if (ReceiveBLEMoveReceiver.orderStatus == 4) {
			orderAdapter.changeStatus(4);
			ReceiveBLEMoveReceiver.orderStatus = -1;
		}
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
	protected void onClickRightView() {
		DialogSexAndHeaderAndMyPacket(BOTTOM_LIST);
	}

	private void DialogSexAndHeaderAndMyPacket(int type) {
		new DialogSexAndHeaderAndMyPacket(this, this, R.layout.choice_pic_way_popupwindow, type);
	}

	@OnClick(R.id.retryTextView)
	public void onClick() {
		addData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void dialogText(int type, String text) {
		Intent intent;
		switch (text) {
			case "0":
				//如果是0则跳转通话套餐
				intent = new Intent(MyPackageActivity.this, CallPackageLlistActivity.class);
				startActivity(intent);
				break;
			case "1":
				Intent marketIntent = new Intent(this,PackageMarketActivity.class);
				startActivity(marketIntent);
				break;
			case "2":
				//如果是0则跳转绑定套餐礼包卡
				BindRechargeCardActivity.launch(MyPackageActivity.this, BindRechargeCardActivity.GIFT);
				break;
		}
	}

	@Override
	public XRecyclerView getOrderListRecylerView() {
		return orderListRecylerView;
	}

	@Override
	public RelativeLayout getNoNetRelativeLayout() {
		return NoNetRelativeLayout;
	}

	@Override
	public OrderAdapter getOrderAdapter() {
		return orderAdapter;
	}

	@Override
	public RelativeLayout getNodataRelativeLayout() {
		return NodataRelativeLayout;
	}

	@Override
	public TextView getNoDataTextView() {
		return noDataTextView;
	}

	@Override
	public void showToast(String msg) {

		CommonTools.showShortToast(MyPackageActivity.this,msg);

	}
}
