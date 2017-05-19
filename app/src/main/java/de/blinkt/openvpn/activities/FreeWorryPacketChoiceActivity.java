package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.FreeWorryPacketChoiceAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetRelaxedHttp;
import de.blinkt.openvpn.model.FreeWorryEntity;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

public class FreeWorryPacketChoiceActivity extends BaseNetActivity implements XRecyclerView.LoadingListener {

	@BindView(R.id.packetChoiceRecyclerView)
	XRecyclerView packetChoiceRecyclerView;
	@BindView(R.id.retryTextView)
	TextView retryTextView;
	@BindView(R.id.NoNetRelativeLayout)
	RelativeLayout NoNetRelativeLayout;
	@BindView(R.id.noDataTextView)
	TextView noDataTextView;
	@BindView(R.id.NodataRelativeLayout)
	RelativeLayout NodataRelativeLayout;
	private FreeWorryPacketChoiceAdapter adapter;
	private int pageNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_free_worry_packet_choice);
		ButterKnife.bind(this);
		initSet();
	}

	//初始化
	private void initSet() {
		hasLeftViewTitle(R.string.free_for_worry, 0);
		addData();
		packetChoiceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		packetChoiceRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
		packetChoiceRecyclerView.setLoadingListener(this);
		adapter = new FreeWorryPacketChoiceAdapter(this);
		packetChoiceRecyclerView.setAdapter(adapter);
	}

	//获取信息
	private void addData() {
		createHttpRequest(HttpConfigUrl.COMTYPE_GET_RELAXED);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		onRefresh();
	}

	@Override
	public void onRefresh() {
		packetChoiceRecyclerView.canMoreLoading();
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
		if (cmdType == HttpConfigUrl.COMTYPE_GET_RELAXED) {
			packetChoiceRecyclerView.loadMoreComplete();
			packetChoiceRecyclerView.refreshComplete();
			GetRelaxedHttp http = (GetRelaxedHttp) object;
			FreeWorryEntity bean = http.getFreeWorryEntity();
			if (bean != null) {
				if (bean.getList() != null) {
					if (bean.getList().size() != 0) {
						//有数据则显示
						NoNetRelativeLayout.setVisibility(View.GONE);
						packetChoiceRecyclerView.setVisibility(View.VISIBLE);
						if (pageNumber == 1) {
							//页码为1且没有数据，则显示无数据页面
							if (bean.getList().size() < Constant.PAGESIZE) {
								adapter.setData(bean.getList());
								packetChoiceRecyclerView.noMoreLoading();
							} else {
								adapter.setData(bean.getList());
							}

						} else {
							adapter.addData(bean.getList());
						}
					} else {
						if (pageNumber == 1) {
							packetChoiceRecyclerView.setVisibility(View.GONE);
							NodataRelativeLayout.setVisibility(View.VISIBLE);
							noDataTextView.setText(getResources().getString(R.string.no_order));
						}
						packetChoiceRecyclerView.noMoreLoading();
					}
				}
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void noNet() {
		super.noNet();
		NoNetRelativeLayout.setVisibility(View.VISIBLE);
	}
}
