package de.blinkt.openvpn.activities;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

public class BluetoothNoConnectActivity extends AppCompatActivity {

	@BindView(R.id.noConnectImageView)
	ImageView noConnectImageView;
	@BindView(R.id.stopImageView)
	ImageView stopImageView;
	@BindView(R.id.all_device_rv)
	RecyclerView allDeviceRv;
	@BindView(R.id.noConnectTextView)
	TextView noConnectTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_no_connect);
		ButterKnife.bind(this);
		setSpan(noConnectTextView);
	}

	public void setSpan(TextView noConnectImageView) {
		Spannable WordtoSpan = new SpannableString(noConnectImageView.getText().toString());
		WordtoSpan.setSpan(new ForegroundColorSpan(
				ContextCompat.getColor(context, R.color.color_7d8698)), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		noConnectImageView.setText(WordtoSpan, TextView.BufferType.SPANNABLE);
	}

	@OnClick(R.id.stopImageView)
	public void onClick() {
	}


}
