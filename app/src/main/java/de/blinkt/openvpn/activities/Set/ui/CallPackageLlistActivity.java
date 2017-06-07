package de.blinkt.openvpn.activities.Set.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.CallPacketAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Set.Presenter.CallPackageListPresenter;
import de.blinkt.openvpn.activities.Set.View.CallPackageListView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * 通话套餐列表
 */
public class CallPackageLlistActivity extends BaseActivity implements XRecyclerView.LoadingListener,CallPackageListView{

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
    private CallPackageListPresenter callPackageListPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_package_llist);
		ButterKnife.bind(this);
		callPackageListPresenter=new CallPackageListPresenter(this);
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
		//获取通话套餐列表数据
		callPackageListPresenter.getCallPackageListData();
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
	public int getPageNumber() {
		return pageNumber;
	}

	@Override
	public String getPageSize() {
		return Constant.PAGESIZE+"";
	}

	@Override
	public String getCategory() {
		return CATOGORY+"";
	}

	@Override
	public XRecyclerView getCallListRecylerView() {
		return callListRecylerView;
	}

	@Override
	public RelativeLayout getNoNetRelativeLayout() {
		return NoNetRelativeLayout;
	}

	@Override
	public CallPacketAdapter getCallPacketAdapter() {
		return callPacketAdapter;
	}

	@Override
	public RelativeLayout getNodataRelativeLayout() {
		return NodataRelativeLayout;
	}

	@Override
	public TextView getNoDataTextView() {
		return noDataTextView;
	}
}
