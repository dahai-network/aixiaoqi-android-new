package de.blinkt.openvpn.activities;

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
public class MyPackageActivity extends BaseActivity implements XRecyclerView.LoadingListener, InterfaceCallback, DialogInterfaceTypeBase {
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
        //hasLeftViewTitle(R.string.order_list, 0);
		manager = new LinearLayoutManager(this);
		orderListRecylerView.setLayoutManager(manager);
		orderListRecylerView.setArrowImageView(R.drawable.iconfont_downgrey);
		orderListRecylerView.setLoadingListener(this);
		orderAdapter = new OrderAdapter(MyPackageActivity.this, R.layout.item_order);
		orderListRecylerView.setAdapter(orderAdapter);
		onRefresh();
	}

	//加入数据
	private void addData() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_ORDER, pageNumber+"", Constant.PAGESIZE+"","-1");
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
						//页码为1且没有数据，则显示无数据页面
						if (bean.getList().size() < Constant.PAGESIZE) {
							orderAdapter.addAll(bean.getList());
							orderListRecylerView.noMoreLoading();
						} else {
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
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(this, errorMessage);
	}

	@Override
	public void noNet() {
		orderListRecylerView.setVisibility(View.GONE);
		NoNetRelativeLayout.setVisibility(View.VISIBLE);
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
}
