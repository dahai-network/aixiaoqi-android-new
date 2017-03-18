package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.PackageMarketAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.PacketMarketHttp;
import de.blinkt.openvpn.model.PacketMarketEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.pinyin.CharacterParser;

public class PackageMarketActivity extends BaseActivity implements InterfaceCallback {

	public static PackageMarketActivity activity ;
	@BindView(R.id.marketRecyclerView)
	RecyclerView marketRecyclerView;
	@BindView(R.id.retryTextView)
	TextView retryTextView;
	@BindView(R.id.NoNetRelativeLayout)
	RelativeLayout NoNetRelativeLayout;
	@BindView(R.id.noDataTextView)
	TextView noDataTextView;
	@BindView(R.id.NodataRelativeLayout)
	RelativeLayout NodataRelativeLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_package_market);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		activity = this;
		hasLeftViewTitle(R.string.package_market,0);
		marketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		initData();

	}

	//模拟数据
	private void initData() {
		//全部展示国家套餐，200个
		PacketMarketHttp http = new PacketMarketHttp(this, HttpConfigUrl.COMTYPE_PACKET_MARKET, 200);
		new Thread(http).start();
	}
	CharacterParser characterParser;
	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		PacketMarketHttp http = (PacketMarketHttp) object;
		characterParser = CharacterParser.getInstance();
		List<List<PacketMarketEntity>> data = http.getPacketMarketEntityList();

		if(data!=null)
		{
			if(data.size()!=0)
			{
				marketRecyclerView.setVisibility(View.VISIBLE);
				NoNetRelativeLayout.setVisibility(View.GONE);
				marketRecyclerView.setAdapter(new PackageMarketAdapter(data, this));
			}
		}

	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(this, errorMessage);
	}

	@Override
	public void noNet() {
		marketRecyclerView.setVisibility(View.GONE);
		NoNetRelativeLayout.setVisibility(View.VISIBLE);
	}

	@OnClick(R.id.retryTextView)
	public void onClick() {
		initData();
	}
}
