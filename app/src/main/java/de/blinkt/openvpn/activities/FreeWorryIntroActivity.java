package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.PacketDtailHttp;
import de.blinkt.openvpn.util.CommonTools;

import static de.blinkt.openvpn.activities.ProMainActivity.confirmedPhoneNum;

public class FreeWorryIntroActivity extends BaseNetActivity {

	@BindView(R.id.titleImageView)
	ImageView titleImageView;
	@BindView(R.id.introTextView)
	TextView introTextView;
	@BindView(R.id.dredgeBtn)
	Button dredgeBtn;
	@BindView(R.id.introContentImageView)
	ImageView introContentImageView;
	private PacketDtailHttp http;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_free_worry_intro);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		hasLeftViewTitle(R.string.free_for_worry, 0);
		if (confirmedPhoneNum == null) {
			CommonTools.showShortToast(this, getString(R.string.please_regist_confirmed));
			finish();
		} else {
			introTextView.setText(getString(R.string.phone_in_device) + confirmedPhoneNum);
		}
		addData();
	}

	//获取两张图片，一张标题图片，一张介绍图片，以后可以替换
	private void addData() {
		createHttpRequest(HttpConfigUrl.COMTYPE_PACKET_DETAIL, getIntent().getStringExtra("id"));
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_PACKET_DETAIL) {
			if (object.getStatus() == 1) {
				http = (PacketDtailHttp) object;
				String titlePic = http.getPacketDtailEntity().getList().getDescTitlePic();
				String descPic = http.getPacketDtailEntity().getList().getDescPic();
				Glide.with(FreeWorryIntroActivity.this).load(titlePic).into(titleImageView);
				Glide.with(FreeWorryIntroActivity.this).load(descPic).into(introContentImageView);
			} else {
				CommonTools.showShortToast(this, object.getMsg());
			}
		}
	}

	@OnClick(R.id.dredgeBtn)
	public void onViewClicked() {
		if (http != null) {
			Intent intent = new Intent(this, CommitFreeWorryActivity.class);
			intent.putExtra("price", http.getPacketDtailEntity().getList().getPrice());
			intent.putExtra("originalPrice", http.getPacketDtailEntity().getList().getOriginalPrice());
			intent.putExtra("id", http.getPacketDtailEntity().getList().getPackageId());
			startActivity(intent);
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(this, errorMessage);
	}
}
