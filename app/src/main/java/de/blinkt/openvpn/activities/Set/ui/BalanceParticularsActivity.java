package de.blinkt.openvpn.activities.Set.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.ParticularAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Set.Presenter.BalanceParticularsPresenter;
import de.blinkt.openvpn.activities.Set.View.BalanceParticularsView;
import de.blinkt.openvpn.model.ParticularEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

public class BalanceParticularsActivity extends BaseActivity implements  XRecyclerView.LoadingListener,BalanceParticularsView {

	@BindView(R.id.particularsRecyclerView)
	XRecyclerView particularsRecyclerView;
	@BindView(R.id.retryTextView)
	TextView retryTextView;
	@BindView(R.id.NoNetRelativeLayout)
	RelativeLayout NoNetRelativeLayout;
	@BindView(R.id.NodataRelativeLayout)
	RelativeLayout NodataRelativeLayout;
	@BindView(R.id.activity_balance_particulars)
	RelativeLayout activityBalanceParticulars;
	@BindView(R.id.noDataTextView)
	TextView noDataTextView;
	private int pageNumber = 1;
	private List<ParticularEntity.ListBean> data = new ArrayList<>();
	private ParticularAdapter particularAdapter;
	BalanceParticularsPresenter balanceParticularsPresenter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_balance_particulars);
		ButterKnife.bind(this);
		initSet();
		balanceParticularsPresenter = new BalanceParticularsPresenter(BalanceParticularsActivity.this,this);
		addData();

	}

	private void initSet() {
		hasLeftViewTitle(R.string.bill, 0);
		particularsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		particularsRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
		particularsRecyclerView.setLoadingListener(this);
		particularAdapter = new ParticularAdapter(this, data, false);
		particularsRecyclerView.setAdapter(particularAdapter);
	}

	/**
	 * 加载账单信息
	 */
	private void addData() {
		balanceParticularsPresenter.addData();
	}

	@Override
	public void onRefresh() {
		pageNumber = 1;
		addData();
	}
	@Override
	public void onLoadMore() {
		pageNumber++;
		addData();
	}
	@OnClick(R.id.retryTextView)
	public void onClick() {
		addData();
	}

	@Override
	public XRecyclerView getParticularsRecyclerView() {
		return particularsRecyclerView;
	}
	@Override
	public RelativeLayout getNoNetRelativeLayout() {
		return NoNetRelativeLayout;
	}
	@Override
	public int getPageNumber() {
		return pageNumber;
	}
	@Override
	public ParticularAdapter getParticularAdapter() {
		return particularAdapter;
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
		CommonTools.showShortToast(BalanceParticularsActivity.this, msg);
	}


}
