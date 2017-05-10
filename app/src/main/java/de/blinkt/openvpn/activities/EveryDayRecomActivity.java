package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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


	@OnClick({R.id.recomImageView, R.id.dismissImageView})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.recomImageView:
				Intent intent = new Intent(this, FreeWorryPacketChoiceActivity.class);
				startActivity(intent);
				finish();
				break;
			case R.id.dismissImageView:
				onBackPressed();
				break;
		}
	}
}
