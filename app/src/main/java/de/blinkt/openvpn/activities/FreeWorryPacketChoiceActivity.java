package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.FreeWorryPacketChoiceAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

public class FreeWorryPacketChoiceActivity extends BaseNetActivity implements XRecyclerView.LoadingListener {

	@BindView(R.id.packetChoiceRecyclerView)
	XRecyclerView packetChoiceRecyclerView;

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
		packetChoiceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		packetChoiceRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
		packetChoiceRecyclerView.setLoadingListener(this);
		FreeWorryPacketChoiceAdapter adapter = new FreeWorryPacketChoiceAdapter(this);
		packetChoiceRecyclerView.setAdapter(adapter);
	}


	@Override
	public void onRefresh() {

	}

	@Override
	public void onLoadMore() {

	}
}
