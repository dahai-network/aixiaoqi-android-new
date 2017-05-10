package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;

public class FreeWorryIntroActivity extends BaseNetActivity {

	@BindView(R.id.titleImageView)
	ImageView titleImageView;
	@BindView(R.id.introTextView)
	TextView introTextView;
	@BindView(R.id.dredgeBtn)
	Button dredgeBtn;
	@BindView(R.id.introContentImageView)
	ImageView introContentImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_free_worry_intro);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.free_for_worry, 0);
	}

	@OnClick(R.id.dredgeBtn)
	public void onViewClicked() {
		Intent intent = new Intent(this, CommitFreeWorryActivity.class);
		startActivity(intent);
	}
}
