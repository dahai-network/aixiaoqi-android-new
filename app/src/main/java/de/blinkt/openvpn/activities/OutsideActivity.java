package de.blinkt.openvpn.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
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
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CancelCallTransferHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.OpenCallTransferHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCLOSE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKOPENAPNSET;
import static de.blinkt.openvpn.constant.UmengContant.CLICKOPENSYSTEMSET;


public class OutsideActivity extends BaseNetActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
	private ViewPager viewPager;
	private ArrayList<View> list;
	private ImageView imageView;
	private ImageView[] imageViews;
	Button outsideStepFiveBt;
	Button outsideStepThirdBt;
	Button outsideStepFourBt;
	Button outsideStepSecondBt;
	TextView outside_step_five_content_tv;
	TextView outsideStepSecondContentTv;
	TextView outside_step_third_tv;
	TextView outside_step_third_content_tv;
	TextView outside_step_four_tv;
	TextView outside_step_four_content_tv;
	TextView outside_step_five_tv;
	TextView outsideStepSecondTv;
	LinearLayout group;
	String statuString;
	View view1;
	View view2;
	View view3;
	View view4;
	View view5;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outside);
		initView();
		initData();
		initSubView();
		setData();
		addListener();
		initTitle();


	}

	private void initView() {
		group = (LinearLayout) findViewById(R.id.viewGroup);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
	}



	private void initData() {
		LayoutInflater inflater = getLayoutInflater();
		list = new ArrayList<>();
		statuString = getIntent().getStringExtra(IntentPutKeyConstant.OUTSIDE);
		boolean isSupport4G=getIntent().getBooleanExtra(IntentPutKeyConstant.IS_SUPPORT_4G,false);
		view1 = inflater.inflate(R.layout.activity_outside_item01, null);
		view2 = inflater.inflate(R.layout.activity_outside_item02, null);
		view3 = inflater.inflate(R.layout.activity_outside_item03, null);
		view4 = inflater.inflate(R.layout.activity_outside_item04, null);
		view5 = inflater.inflate(R.layout.activity_outside_item05, null);
		TextView step=(TextView)view4.findViewById(R.id.step);
		TextView step5=(TextView)view5.findViewById(R.id.step5);

		list.add(view1);
		list.add(view2);
		if(!isSupport4G){
		list.add(view3);
		}else{
			step.setText("3");
			step5.setText("4");
		}
		list.add(view4);
		if (IntentPutKeyConstant.OUTSIDE.equals(statuString)) {
		list.add(view5);
		}
		int length = list.size();
		imageViews = new ImageView[length];
		for (int i = 0; i < length; i++) {
			imageView = new ImageView(this);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.setMargins(0, 0, 15, 0);
			imageView.setLayoutParams(lp);
			imageViews[i] = imageView;
			if (i == 0) {
				imageView.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				imageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
			group.addView(imageView);
		}


		viewPager.setAdapter(new OutsideAdapter(list));
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(0);
	}



	private void initSubView() {
		outsideStepSecondBt = (Button) view2.findViewById(R.id.outside_step_second_bt);
		outsideStepThirdBt = (Button) view3.findViewById(R.id.outside_step_third_bt);
		outsideStepFourBt = (Button) view4.findViewById(R.id.outside_step_four_bt);
		outsideStepFiveBt = (Button) view5.findViewById(R.id.outside_step_five_bt);
		outsideStepSecondTv = (TextView) view2.findViewById(R.id.outside_step_second_tv);
		outsideStepSecondContentTv = (TextView) view2.findViewById(R.id.outside_step_second_content_tv);
		outside_step_third_tv = (TextView) view3.findViewById(R.id.outside_step_third_tv);
		outside_step_third_content_tv = (TextView) view3.findViewById(R.id.outside_step_third_content_tv);
		outside_step_four_tv = (TextView) view4.findViewById(R.id.outside_step_four_tv);
		outside_step_four_content_tv = (TextView) view4.findViewById(R.id.outside_step_four_content_tv);
		outside_step_five_tv = (TextView) view5.findViewById(R.id.outside_step_five_tv);
		outside_step_five_content_tv = (TextView) view5.findViewById(R.id.outside_step_five_content_tv);
	}

	private void setData() {
		if (!IntentPutKeyConstant.OUTSIDE.equals(statuString)) {
			outsideStepSecondTv.setText(getString(R.string.outside_step_second_1));
			outsideStepSecondContentTv.setText(getString(R.string.outside_step_second_content_1));
			outside_step_third_tv.setText(getString(R.string.outside_step_third_1));
			outside_step_third_content_tv.setText(getString(R.string.outside_step_third_content_1));
			outside_step_four_tv.setText(getString(R.string.outside_step_four_1));
			outside_step_four_content_tv.setText(getString(R.string.outside_step_four_content_1));
			outside_step_five_tv.setText(getString(R.string.outside_step_five_1));
			outside_step_five_content_tv.setText(getString(R.string.outside_step_five_content_1));
			outsideStepFiveBt.setText(getString(R.string.outside_step_five_click_1));
		}
	}

	private void addListener() {
		outsideStepSecondBt.setOnClickListener(this);
		outsideStepThirdBt.setOnClickListener(this);
		outsideStepFourBt.setOnClickListener(this);
		outsideStepFiveBt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.outside_step_second_bt:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKOPENAPNSET);
						try{
				startActivity(new Intent(Settings.ACTION_APN_SETTINGS));
						}catch (ActivityNotFoundException e){
							CommonTools.showShortToast(this,getString(R.string.not_suppert_open_way));
						}
				break;
			case R.id.outside_step_third_bt:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKOPENSYSTEMSET);
				try{
				startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
				}catch (ActivityNotFoundException e){
					CommonTools.showShortToast(this,getString(R.string.not_suppert_open_way));
				}
				break;
			case R.id.outside_step_four_bt:
				try{
				startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
		}catch (ActivityNotFoundException e){
			CommonTools.showShortToast(this,getString(R.string.not_suppert_open_way));
		}
				break;


		}
	}


	private void initTitle() {
		if (IntentPutKeyConstant.OUTSIDE.equals(statuString))
			hasLeftViewTitle(R.string.outside, 0);

		else
			hasLeftViewTitle(R.string.after_going_abroad, 0);

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		setImageBackground(arg0 % list.size());
	}

	private void setImageBackground(int selectItems) {
		int length = imageViews.length;
		for (int i = 0; i < length; i++) {
			if (i == selectItems) {
				imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
		}
	}


	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		finish();
		if (!TextUtils.isEmpty(object.getMsg()))
			CommonTools.showShortToast(this, object.getMsg());
	}


}
