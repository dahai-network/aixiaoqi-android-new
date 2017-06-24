package de.blinkt.openvpn.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.OutsideAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.MyModules.ui.ImportantAuthorityActivity;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.PageChangeListener;


/**
 * Created by Administrator on 2017/3/22 0022.
 */

public class StartUpHomePageActivity extends BaseActivity implements  View.OnClickListener {

	private ViewPager viewPager;
	private ArrayList<View> list;
	private ImageView imageView;
	private ImageView[] imageViews;
	LinearLayout group;
	String statuString;
	View view1;
	View view2;
	View view3;
	View view4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_outside);
		initView();
		initData();
		initSubView();
		addListener();
	}

	private void initView() {
		group = (LinearLayout) findViewById(R.id.viewGroup);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
	}


	private void initData() {
		LayoutInflater inflater = getLayoutInflater();
		list = new ArrayList<>();
		statuString = getIntent().getStringExtra(IntentPutKeyConstant.OUTSIDE);
		view1 = inflater.inflate(R.layout.activity_start_up_item01, null);
		view2 = inflater.inflate(R.layout.activity_start_up_item02, null);
		view3 = inflater.inflate(R.layout.activity_start_up_item03, null);
		view4 = inflater.inflate(R.layout.activity_start_up_item04, null);
		list.add(view1);
		list.add(view2);
		list.add(view3);
		list.add(view4);
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
				imageView.setBackgroundResource(R.drawable.start_up_ret_select);
			} else {
				imageView.setBackgroundResource(R.drawable.start_up_cir_default);
			}
			group.addView(imageView);
		}


		viewPager.setAdapter(new OutsideAdapter(list));
		new PageChangeListener(viewPager){
			@Override
			public void pageSelected(int position) {
				setImageBackground(position % list.size());
			}
		};
		viewPager.setCurrentItem(0);
	}

	ImageView experienceIv;

	private void initSubView() {
		experienceIv = (ImageView) view4.findViewById(R.id.experience_iv);
	}

	private void addListener() {
		experienceIv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.experience_iv:
				//申请权限
					requestSomePermission();

					break;
		}
	}

	private void requestSomePermission() {
		// 先判断是否有权限。
		if (!AndPermission.hasPermission(StartUpHomePageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				) {
			// 申请权限。
			AndPermission.with(StartUpHomePageActivity.this)
					.requestCode(100)
					.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
					.send();
		}else{
			toActivity(ImportantAuthorityActivity.class);
			finish();
		}


	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
		AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
	}
	private PermissionListener listener = new PermissionListener() {

		@Override
		public void onSucceed(int requestCode, List<String> grantPermissions) {
			toActivity(ImportantAuthorityActivity.class);
			finish();
		}

		@Override
		public void onFailed(int requestCode, List<String> deniedPermissions) {
			CommonTools.showShortToast(StartUpHomePageActivity.this,"权限申请失败,请打开此权限，否则app不能正常运行");
		}
	};


	private void setImageBackground(int selectItems) {
		int length = imageViews.length;
		for (int i = 0; i < length; i++) {
			if (i == selectItems) {
				imageViews[i].setBackgroundResource(R.drawable.start_up_ret_select);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.start_up_cir_default);
			}
		}
	}

}
