package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKAFTERINLAND;
import static de.blinkt.openvpn.constant.UmengContant.CLICKFASTSETBEFOREABROAD;
import static de.blinkt.openvpn.constant.UmengContant.CLICKFASTSETDEVICE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKFASTSETOUTSIDE;


public class FastSetActivity extends BaseActivity implements View.OnClickListener{
	TextView afterGoingAbroadTv;
	TextView linkBraceletTv;
	TextView outsideTv;
	TextView beforeGoingAbroadTv;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fast_set);
		hasLeftViewTitle(R.string.quick_setting,0);
		initView();
		addListener();
	}

	private  void initView(){
		beforeGoingAbroadTv=(TextView)findViewById(R.id.before_going_abroad_tv);
		linkBraceletTv=(TextView)findViewById(R.id.link_bracelet_tv);
		outsideTv=(TextView)findViewById(R.id.outside_tv);
		afterGoingAbroadTv=(TextView)findViewById(R.id.after_going_abroad_tv);
	}

	private void addListener(){
		beforeGoingAbroadTv.setOnClickListener(this);
		linkBraceletTv.setOnClickListener(this);
		outsideTv.setOnClickListener(this);
		afterGoingAbroadTv.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()){

//			case R.id.before_going_abroad_tv:
//				//友盟方法统计
//				MobclickAgent.onEvent(context, CLICKFASTSETBEFOREABROAD);
//			toActivity(new Intent(this, OrderedOutsidePurchaseActivity.class));
//				break;
			case R.id.link_bracelet_tv:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKFASTSETDEVICE);
				toActivity(MyDeviceActivity.class);
				break;
			case R.id.outside_tv:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKFASTSETOUTSIDE);
				toActivity(new Intent(this,OutsideActivity.class).putExtra(IntentPutKeyConstant.OUTSIDE,IntentPutKeyConstant.OUTSIDE));
				break;
			case R.id.after_going_abroad_tv:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKAFTERINLAND);
				toActivity(new Intent(this,OutsideActivity.class).putExtra(IntentPutKeyConstant.OUTSIDE,IntentPutKeyConstant.AFTER_GOING_ABROAD));
				break;
		}
	}
}
