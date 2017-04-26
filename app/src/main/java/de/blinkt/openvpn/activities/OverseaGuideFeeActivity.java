package de.blinkt.openvpn.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.OutsideAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;



public class OverseaGuideFeeActivity extends BaseActivity  {
	private ViewPager viewPager;
	private ArrayList<View> list;

	LinearLayout group;
	View view1;
	View view2;
	View view3;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outside);
		initView();
		initData();
		initTitle();


	}

	private void initView() {
		group = (LinearLayout) findViewById(R.id.viewGroup);
		group.setVisibility(View.GONE);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
	}



	private void initData() {
		LayoutInflater inflater = getLayoutInflater();
		list = new ArrayList<>();
		view1 = inflater.inflate(R.layout.oversea_guide_fee_item01, null);
		view2 = inflater.inflate(R.layout.oversea_guide_fee_item02, null);
		view3 = inflater.inflate(R.layout.oversea_guide_fee_item03, null);
		list.add(view1);
		list.add(view2);
		list.add(view3);
		viewPager.setAdapter(new OutsideAdapter(list));

		viewPager.setCurrentItem(0);
	}

	private void initTitle() {
			hasLeftViewTitle(R.string.overseas_guide_fee, 0);
	}







}
