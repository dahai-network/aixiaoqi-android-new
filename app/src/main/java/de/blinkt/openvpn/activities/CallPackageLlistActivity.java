package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.support.annotation.Size;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.CallPacketAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetPakcetHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.model.PacketEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

public class CallPackageLlistActivity extends BaseNetActivity implements XRecyclerView.LoadingListener, InterfaceCallback {

	public static CallPackageLlistActivity activity;
	@BindView(R.id.retryTextView)
	TextView retryTextView;
	@BindView(R.id.callListRecylerView)
	XRecyclerView callListRecylerView;
	CallPacketAdapter callPacketAdapter;
	@BindView(R.id.NoNetRelativeLayout)
	RelativeLayout NoNetRelativeLayout;
	@BindView(R.id.noDataTextView)
	TextView noDataTextView;
	@BindView(R.id.NodataRelativeLayout)
	RelativeLayout NodataRelativeLayout;
	private int CATOGORY = 1;
	private int pageNumber = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_package_llist);
		ButterKnife.bind(this);
		init();
	}

	//初始化
	private void init() {
		activity = this;
		hasLeftViewTitle(R.string.communication_packet, 0);
		LinearLayoutManager manager = new LinearLayoutManager(this);
		callListRecylerView.setLayoutManager(manager);
		callListRecylerView.setArrowImageView(R.drawable.iconfont_downgrey);
		callListRecylerView.setLoadingListener(this);
		callPacketAdapter = new CallPacketAdapter(this);
		callListRecylerView.setAdapter(callPacketAdapter);
		onRefresh();
	}

	//加入数据
	private void addData() {
		createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_GET, pageNumber+"", Constant.PAGESIZE+"", CATOGORY+"");
	}

	@Override
	public void onRefresh() {
		callListRecylerView.canMoreLoading();
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
		if (cmdType == HttpConfigUrl.COMTYPE_PACKET_GET) {
			callListRecylerView.loadMoreComplete();
			callListRecylerView.refreshComplete();
			GetPakcetHttp http = (GetPakcetHttp) object;
			PacketEntity bean = http.getPacketEntity();
			if (bean != null) {
				if (bean.getList().size() != 0) {
					//有数据则显示
					NoNetRelativeLayout.setVisibility(View.GONE);
					callListRecylerView.setVisibility(View.VISIBLE);
					if (pageNumber == 1) {
						//页码为1且没有数据，则显示无数据页面
						if (bean.getList().size() < Constant.PAGESIZE) {
							callPacketAdapter.add(bean.getList());
							callListRecylerView.noMoreLoading();
						} else {
							callPacketAdapter.add(bean.getList());
						}

					} else {
						callPacketAdapter.addAll(bean.getList());
					}
				} else {
					if (pageNumber == 1) {
						callListRecylerView.setVisibility(View.GONE);
						NodataRelativeLayout.setVisibility(View.VISIBLE);
						noDataTextView.setText(getResources().getString(R.string.no_packet));
					}
					callListRecylerView.noMoreLoading();
				}
			}
		}
		callPacketAdapter.notifyDataSetChanged();
	}


	@Override
	public void noNet() {
		callListRecylerView.setVisibility(View.GONE);
		NoNetRelativeLayout.setVisibility(View.VISIBLE);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	@OnClick(R.id.retryTextView)
	public void onClick() {

	}
}
