package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
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
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.ParticularHttp;
import de.blinkt.openvpn.model.ParticularEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

public class BalanceParticularsActivity extends BaseNetActivity implements InterfaceCallback, XRecyclerView.LoadingListener {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_balance_particulars);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.bill,0);
		particularsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		particularsRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
		particularsRecyclerView.setLoadingListener(this);
		particularAdapter = new ParticularAdapter(this, data);
		particularsRecyclerView.setAdapter(particularAdapter);
		addData();
	}

	private void addData() {
		createHttpRequest( HttpConfigUrl.COMTYPE_PARTICULAR, pageNumber+"", Constant.PAGESIZE+"");
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		particularsRecyclerView.loadMoreComplete();
		particularsRecyclerView.refreshComplete();
		ParticularHttp http = (ParticularHttp) object;
		if (http.getParticularEntity().getList().size() != 0) {
			particularsRecyclerView.setVisibility(View.VISIBLE);
			NoNetRelativeLayout.setVisibility(View.GONE);
			if (pageNumber == 1) {
//				if (particularAdapter.getItemCount() == 0) {
				if (http.getParticularEntity().getList().size() < Constant.PAGESIZE) {
					particularAdapter.add(http.getParticularEntity().getList());
					particularsRecyclerView.noMoreLoading();
				} else {
					particularAdapter.add(http.getParticularEntity().getList());
					particularsRecyclerView.canMoreLoading();
				}
			} else {
				particularAdapter.addAll(http.getParticularEntity().getList());
			}
		} else {
			if (pageNumber == 1) {
				particularsRecyclerView.setVisibility(View.GONE);
				NodataRelativeLayout.setVisibility(View.VISIBLE);
				noDataTextView.setText(getResources().getString(R.string.no_balance_detail));
			}
			particularsRecyclerView.noMoreLoading();
		}
		particularAdapter.notifyDataSetChanged();
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

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(this, errorMessage);
	}

	@Override
	public void noNet() {
		NoNetRelativeLayout.setVisibility(View.VISIBLE);
		particularsRecyclerView.setVisibility(View.GONE);
		particularsRecyclerView.noMoreLoading();
	}

	@OnClick(R.id.retryTextView)
	public void onClick() {
		addData();
	}
}
