package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.CommenActivity;

public class EveryDayRecomActivity extends CommenActivity {

	@BindView(R.id.recomImageView)
	ImageView recomImageView;
	@BindView(R.id.dismissImageView)
	ImageView dismissImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_every_day_recom);
		ButterKnife.bind(this);
	}


	@OnClick(R.id.dismissImageView)
	public void onViewClicked() {
		onBackPressed();
	}
}
