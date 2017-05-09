package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.FreeWorryPacketChoiceAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;

public class FreeWorryPacketChoiceActivity extends BaseNetActivity {

	@BindView(R.id.packetChoiceRecyclerView)
	RecyclerView packetChoiceRecyclerView;

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
		FreeWorryPacketChoiceAdapter adapter = new FreeWorryPacketChoiceAdapter();
		packetChoiceRecyclerView.setAdapter(adapter);
	}


}
