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
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.http.BoughtPacketHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import de.blinkt.openvpn.views.dialog.DialogOrderedOutside;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

import static de.blinkt.openvpn.constant.UmengContant.CLICKBEFOREABROADPURCHASEPACKAGE;

/**
 * 订单列表，用于显示购买过的套餐
 */
public class OrderedOutsidePurchaseActivity extends BaseNetActivity implements XRecyclerView.LoadingListener,DialogInterfaceTypeBase {
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
	private int pageNumber = 1;
	private LinearLayoutManager manager;
private static final int DIALOG_ORDERED_OUTSIDE_TYPE=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ordered_outside_purchase);
		showDefaultProgress();
		ButterKnife.bind(this);
		if(SharedUtils.getInstance().readInt(IntentPutKeyConstant.ORDERED_OUTSIDE)!=1){
		setDialog();
		}
		init();
	}
	private  void setDialog(){
	new DialogOrderedOutside(this,this,R.layout.dialog_ordered_outside,DIALOG_ORDERED_OUTSIDE_TYPE);
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
		hasAllViewTitle(R.string.ordered_outside_purchase,R.string.purchase_packet,-1,false);
		manager = new LinearLayoutManager(this);
		orderListRecylerView.setLayoutManager(manager);
		orderListRecylerView.setArrowImageView(R.drawable.iconfont_downgrey);
		orderListRecylerView.setLoadingListener(this);
		orderAdapter = new OrderAdapter(this, R.layout.item_before_going_aborad);
		orderListRecylerView.setAdapter(orderAdapter);
	}

	@Override
	protected void onClickRightView() {
		//友盟方法统计
		MobclickAgent.onEvent(this, CLICKBEFOREABROADPURCHASEPACKAGE);
		toActivity(PackageMarketActivity.class);
	}

	//加入数据
	private void addData() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_ORDER, pageNumber+"", 20+"","0");
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
		dismissProgress();
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

	@Override
	public void dialogText(int type, String text) {
		if(type==DIALOG_ORDERED_OUTSIDE_TYPE){
			SharedUtils.getInstance().writeInt(IntentPutKeyConstant.ORDERED_OUTSIDE,1);
		}
	}
}
