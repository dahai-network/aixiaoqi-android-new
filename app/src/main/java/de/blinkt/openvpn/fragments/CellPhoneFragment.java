package de.blinkt.openvpn.fragments;

/**
 * Created by Administrator on 2016/9/8 0008.
 */

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aixiaoqi.socket.EventBusUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.CellPhoneFragmentPagerAdapter;
import de.blinkt.openvpn.fragments.base.BaseStatusFragment;
import de.blinkt.openvpn.model.enentbus.OptionCellPhoneFragmentView;
import de.blinkt.openvpn.util.PageChangeListener;
import de.blinkt.openvpn.views.MyViewPager;

import static de.blinkt.openvpn.constant.UmengContant.CLICKTITLEPHONE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKTITLESMS;

/**
 * 拨打电话界面
 */
public class CellPhoneFragment extends BaseStatusFragment {
	public RadioGroup operation_rg;
	RadioButton cell_phone_rb;
	RadioButton message_rb;
	/**
	 * 拨打电话标题
	 */
	public TextView dial_tittle_fl;
	private ArrayList<Fragment> fragments = new ArrayList<>();
	MyViewPager mViewPager;
	public static boolean isForeground = false;
	Drawable drawable;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		setLayoutId(R.layout.cell_phone_fragment);
		View view = super.onCreateView(inflater, container,
				savedInstanceState);
		initView(view);
		ClickPhone();
		return view;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		Log.e("CellPhoneFragment", "isVisibleToUser=" + isVisibleToUser);
		super.setUserVisibleHint(isVisibleToUser);
		isForeground = isVisibleToUser;
		if (isVisibleToUser) {
			if (operation_rg != null) {
				operation_rg.check(cell_phone_rb.getId());
				ClickPhone();
			}

		}
		EventBusUtil.optionView(true);
		if (fragmentPhone != null && fragmentPhone.t9dialpadview != null)
			fragmentPhone.t9dialpadview.setVisibility(View.GONE);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void optionView(OptionCellPhoneFragmentView entity) {

		if (!TextUtils.isEmpty(entity.getTextChange())) {
			dial_tittle_fl.setVisibility(View.VISIBLE);
			mViewPager.setScrollble(false);
			operation_rg.setVisibility(View.GONE);
		} else {
			dial_tittle_fl.setVisibility(View.GONE);
			mViewPager.setScrollble(true);
			operation_rg.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onDestroy() {
		operation_rg = null;
		dial_tittle_fl = null;
		super.onDestroy();
	}

	private void initView(View view) {
		operation_rg = ((RadioGroup) view.findViewById(R.id.operation_rg));
		cell_phone_rb = ((RadioButton) view.findViewById(R.id.cell_phone_rb));
		//拨打电话标题
		dial_tittle_fl = (TextView) view.findViewById(R.id.dial_tittle_tv);
		message_rb = ((RadioButton) view.findViewById(R.id.message_rb));
		mViewPager = (MyViewPager) view.findViewById(R.id.mViewPager);
		initFragment();
		new PageChangeListener(mViewPager) {
			@Override
			public void pageSelected(int position) {
				if (position == 0) {
					ClickPhone();
				} else {
					ClickMessage();
				}
			}
		};
		//初始化标题下标的小三角
		drawable = getActivity().getResources().getDrawable(R.drawable.image_slidethetriangle);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		mViewPager.setCurrentItem(0);
		setBottomImage(null, drawable);

	}

	private void setBottomImage(Drawable messageDrawable, Drawable cellPhoneDrawable) {
		message_rb.setCompoundDrawables(null, null, null, messageDrawable);
		cell_phone_rb.setCompoundDrawables(null, null, null, cellPhoneDrawable);
	}

	Fragment_Phone fragmentPhone;

	private void initFragment() {
		fragments.clear();//清空
		fragments.add(fragmentPhone = new Fragment_Phone());
		fragments.add(new SmsFragment());
		CellPhoneFragmentPagerAdapter mAdapter = new CellPhoneFragmentPagerAdapter(getChildFragmentManager(), fragments);
		mAdapter.setFragments(fragments);
		mViewPager.setAdapter(mAdapter);
		operation_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.cell_phone_rb:
						//友盟方法统计
						MobclickAgent.onEvent(getActivity(), CLICKTITLEPHONE);
						ClickPhone();
						break;
					case R.id.message_rb:
						//友盟方法统计
						MobclickAgent.onEvent(getActivity(), CLICKTITLESMS);
						ClickMessage();
						break;
				}
			}
		});
	}

	private void ClickMessage() {
		setBottomImage(drawable, null);

		mViewPager.setCurrentItem(1);
	}

	private void ClickPhone() {
/**
 * 监听ViewPage的状态变化，控制是否滑动
 */
		mViewPager.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						mViewPager.requestDisallowInterceptTouchEvent(false);
						break;
					case MotionEvent.ACTION_CANCEL:
						mViewPager.requestDisallowInterceptTouchEvent(false);
					default:
						break;
				}
				return dial_tittle_fl.getVisibility() == View.VISIBLE;

			}
		});

		/**
		 * \根据键盘的显示来实现控件的显示或则隐藏
		 */
		setBottomImage(null, drawable);
		mViewPager.setCurrentItem(0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (fragmentPhone != null) {
			fragmentPhone.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}


}
