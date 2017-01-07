package de.blinkt.openvpn.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.CountryDetailPackageAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CountryPacketHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.model.CountryPacketEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.contact.DividerDecoration;

import static android.view.View.GONE;

public class CountryPackageActivity extends BaseActivity implements InterfaceCallback {

	@BindView(R.id.packageImageView)
	ImageView packageImageView;
	@BindView(R.id.packageDetailRecyclerView)
	RecyclerView packageDetailRecyclerView;
	@BindView(R.id.retryTextView)
	TextView retryTextView;
	@BindView(R.id.NoNetRelativeLayout)
	RelativeLayout NoNetRelativeLayout;
	@BindView(R.id.nodataTextView)
	TextView nodataTextView;

	public static void launch(Context context, String countryPic, String countryStr, String id) {
		Intent intent = new Intent(context, CountryPackageActivity.class);
		intent.putExtra("country", countryStr);
		intent.putExtra("id", id);
		intent.putExtra("countryPic", countryPic);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_country_detail);
		ButterKnife.bind(this);
		initSet();
	}
	 String countryPic;
	private void initSet() {
		Intent intent = getIntent();
		String countryStr = intent.getStringExtra("country");
		titleBar.setLeftBtnIcon(R.drawable.btn_top_back);
		if (TextUtils.isEmpty(countryStr)) {
			titleBar.getTitle().setText("");
		} else {
			titleBar.getTitle().setText(countryStr);
		}

		titleBar.getLeftText().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		addData();
		countryPic= getIntent().getStringExtra("countryPic");
		if (countryPic != null)
			Glide.with(ICSOpenVPNApplication.getContext()).load(countryPic).asBitmap().into(new SimpleTarget<Bitmap>() {
			@Override
			public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
				int imageWidth = resource.getWidth();
				int imageHeight = resource.getHeight();
				int height = CommonTools.getScreenWidth(CountryPackageActivity.this) * imageHeight / imageWidth;
				ViewGroup.LayoutParams para = packageImageView.getLayoutParams();
				para.height = height;
				packageImageView.setLayoutParams(para);
				packageImageView.setImageBitmap(resource);
			}
		});
		packageDetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		packageDetailRecyclerView.addItemDecoration(new DividerDecoration(this));
	}


	private void addData() {
		String countryId = getIntent().getStringExtra("id");
		if (countryId != null) {
			CountryPacketHttp http = new CountryPacketHttp(this, HttpConfigUrl.COMTYPE_COUNTRY_PACKET, countryId);
			new Thread(http).start();
		}
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		CountryPacketHttp http = (CountryPacketHttp) object;
		List<CountryPacketEntity> bean = http.getCountryPacketList();
		if (bean != null) {
			if (bean.size() != 0) {
				NoNetRelativeLayout.setVisibility(GONE);
				packageDetailRecyclerView.setVisibility(View.VISIBLE);
				packageImageView.setVisibility(View.VISIBLE);
				packageDetailRecyclerView.setAdapter(new CountryDetailPackageAdapter(this, http.getCountryPacketList(),countryPic));
			} else {
				packageDetailRecyclerView.setVisibility(View.GONE);
				nodataTextView.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(this, errorMessage);
	}

	@Override
	public void noNet() {
		NoNetRelativeLayout.setVisibility(View.VISIBLE);
		packageDetailRecyclerView.setVisibility(GONE);
		packageImageView.setVisibility(GONE);
	}

	@OnClick(R.id.retryTextView)
	public void onClick() {
		addData();
	}
}
