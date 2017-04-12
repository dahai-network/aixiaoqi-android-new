package de.blinkt.openvpn.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.widget.GlideCircleTransform;
import de.blinkt.openvpn.activities.AlarmClockActivity;
import de.blinkt.openvpn.activities.BalanceParticularsActivity;
import de.blinkt.openvpn.activities.ChoiceDeviceTypeActivity;
import de.blinkt.openvpn.activities.ImportantAuthorityActivity;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.activities.MyPackageActivity;
import de.blinkt.openvpn.activities.PackageCategoryActivity;
import de.blinkt.openvpn.activities.PackageMarketActivity;
import de.blinkt.openvpn.activities.PersonalCenterActivity;
import de.blinkt.openvpn.activities.RechargeActivity;
import de.blinkt.openvpn.activities.SettingActivity;
import de.blinkt.openvpn.activities.TipUserOptionActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.AlarmClockCountHttp;
import de.blinkt.openvpn.http.BalanceHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.TitleBar;

import static de.blinkt.openvpn.constant.UmengContant.CLICKALARMTIP;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBALANCE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCOMINGTELTIP;
import static de.blinkt.openvpn.constant.UmengContant.CLICKENTERPERSONCENTER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKLIFTWRIST;
import static de.blinkt.openvpn.constant.UmengContant.CLICKMYDEVICE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKMYPACKAGE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKQQTIP;
import static de.blinkt.openvpn.constant.UmengContant.CLICKRECHARGE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSET;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSMSTIP;
import static de.blinkt.openvpn.constant.UmengContant.CLICKWEIXINTIP;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener, InterfaceCallback {


	@BindView(R.id.title)
	TitleBar title;
	@BindView(R.id.headImageView)
	ImageView headImageView;
	@BindView(R.id.accountNameTextView)
	TextView accountNameTextView;
	@BindView(R.id.accountPhoneTextView)
	TextView accountPhoneTextView;
	@BindView(R.id.rl_people_center)
	RelativeLayout rlPeopleCenter;
	@BindView(R.id.balanceTextView)
	TextView balanceTextView;
	@BindView(R.id.billRelativeLayout)
	RelativeLayout billRelativeLayout;
	@BindView(R.id.activateRelativeLayout)
	RelativeLayout activateRelativeLayout;
	@BindView(R.id.addDeviceRelativeLayout)
	RelativeLayout addDeviceRelativeLayout;
	@BindView(R.id.permission_set)
	TextView tvPermissionSet;
	@BindView(R.id.tv_setting)
	TextView tvSetting;
	@BindView(R.id.rechargeTextView)
	TextView rechargeTextView;
	@BindView(R.id.ll_coming_tel_tip)
	LinearLayout llComingTelTip;
	@BindView(R.id.tv_alarm_clock_tip)
	TextView tvAlarmClockTip;
	@BindView(R.id.ll_alarm_clock_tip)
	LinearLayout llAlarmClockTip;
	@BindView(R.id.tv_sms_tip)
	TextView tvSmsTip;
	@BindView(R.id.ll_sms_tip)
	LinearLayout llSmsTip;
	@BindView(R.id.tv_weixin_tip)
	TextView tvWeixinTip;
	@BindView(R.id.ll_weixin_tip)
	LinearLayout llWeixinTip;
	@BindView(R.id.tv_qq_tip)
	TextView tvQqTip;
	@BindView(R.id.ll_qq_tip)
	LinearLayout llQqTip;
	@BindView(R.id.liftWristTextView)
	TextView liftWristTextView;
	@BindView(R.id.liftWristLinearLayout)
	LinearLayout liftWristLinearLayout;
	@BindView(R.id.tv_coming_tel_tip)
	TextView tvComingTelTip;
	@BindView(R.id.unBindTextView)
	TextView unBindTextView;
	SharedUtils utils = SharedUtils.getInstance();
	//bluetooth status蓝牙状态
	private String bleStatus;
	private String TAG = "AccountFragment";

	public AccountFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Glide.get(getActivity()).clearMemory();
		View rootView = inflater.inflate(R.layout.fragment_account,
				container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initSet();
	}

	private void initSet() {
		tvAlarmClockTip.setText(String.format(getResources().getString(R.string.opened_num_alarm_clock), "0"));

		Glide.with(ICSOpenVPNApplication.getContext()).load(SharedUtils.getInstance().readString(Constant.USER_HEAD)).placeholder(R.drawable.default_head).error(R.drawable.default_head).
				transform(new GlideCircleTransform(getActivity())).into(headImageView);
	}

	@Override
	public void onResume() {
		super.onResume();
		//获取数据，每次都重新获取一次以保持正确性。
		getData();
//		getAlarmClock();
	}

	private void getAlarmClock() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_ALARM_CLOCK_COUNT);
	}

	private void getData() {
		title.setTextTitle(getString(R.string.personal_center));
		if (!TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.NICK_NAME)))
			accountNameTextView.setText(SharedUtils.getInstance().readString(Constant.NICK_NAME));
		Glide.with(ICSOpenVPNApplication.getContext()).load(SharedUtils.getInstance().readString(Constant.USER_HEAD)).
				transform(new GlideCircleTransform(getActivity())).into(headImageView);
		accountPhoneTextView.setText(SharedUtils.getInstance().readString(Constant.USER_NAME));
		BalanceHttp http = new BalanceHttp(this, HttpConfigUrl.COMTYPE_GET_BALANCE);
		new Thread(http).start();
		if (utils.readInt(Constant.LIFT_WRIST) == 1) {
			liftWristTextView.setText(getResources().getString(R.string.opened));
//			SendCommandToBluetooth.sendMessageToBlueTooth("AA0C0401A3");
		} else {
			liftWristTextView.setText(getResources().getString(R.string.unopened));
//			SendCommandToBluetooth.sendMessageToBlueTooth("AA0C0400A2");
		}
		if (utils.readInt(Constant.COMING_TEL_REMIND) == 1) {
			tvComingTelTip.setText(getResources().getString(R.string.opened));
		} else {
			tvComingTelTip.setText(getResources().getString(R.string.unopened));
		}
		if (utils.readInt(Constant.MESSAGE_REMIND) == 1) {
			tvSmsTip.setText(getResources().getString(R.string.opened));
		} else {
			tvSmsTip.setText(getResources().getString(R.string.unopened));
		}
		if (utils.readInt(Constant.WEIXIN_REMIND) == 1) {
			tvWeixinTip.setText(getResources().getString(R.string.opened));
		} else {
			tvWeixinTip.setText(getResources().getString(R.string.unopened));
		}
		if (utils.readInt(Constant.QQ_REMIND) == 1) {
			tvQqTip.setText(getResources().getString(R.string.opened));
		} else {
			tvQqTip.setText(getResources().getString(R.string.unopened));
		}
	}


	@OnClick({R.id.rechargeTextView,
			R.id.activateRelativeLayout,
			R.id.billRelativeLayout,
			R.id.addDeviceRelativeLayout,
			R.id.tv_setting,
			R.id.rl_people_center,
			R.id.ll_qq_tip,
			R.id.ll_weixin_tip,
			R.id.ll_sms_tip,
			R.id.permission_set,
			R.id.ll_alarm_clock_tip,
			R.id.ll_coming_tel_tip,
			R.id.liftWristLinearLayout,
			R.id.unBindTextView
	})
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.activateRelativeLayout:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKMYPACKAGE);
				intent = new Intent(getActivity(), PackageCategoryActivity.class);
				intent.putExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE,Constant.SHOW);
				break;
			case R.id.addDeviceRelativeLayout:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKMYDEVICE);
				if (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))) {
					intent = new Intent(getActivity(), ChoiceDeviceTypeActivity.class);
				} else {
					intent = new Intent(getActivity(), MyDeviceActivity.class);
				}
				int status = R.string.index_connecting;
				if (getActivity().getResources().getString(R.string.index_no_signal).equals(getBleStatus())) {
					status = R.string.index_no_signal;
				} else if (getActivity().getResources().getString(R.string.index_connecting).equals(getBleStatus())) {
					status = R.string.index_connecting;
				} else if (getActivity().getResources().getString(R.string.index_high_signal).equals(getBleStatus())) {
					status = R.string.index_high_signal;
				} else if (getActivity().getResources().getString(R.string.index_no_packet).equals(getBleStatus())) {
					status = R.string.index_no_packet;
				} else if (getString(R.string.index_un_insert_card).equals(getBleStatus())) {
					status = R.string.index_un_insert_card;
				} else if (getString(R.string.index_high_signal).equals(getBleStatus())) {
					status = R.string.index_high_signal;
				} else if (getString(R.string.index_registing).equals(getBleStatus())) {
					status = R.string.index_registing;
				} else if (getString(R.string.index_aixiaoqicard).equals(getBleStatus())) {
					status = R.string.index_aixiaoqicard;
				}
				intent.putExtra(MyDeviceActivity.BLUESTATUSFROMPROMAIN, getString(status));
				break;
			case R.id.permission_set:
//				CommonTools.showShortToast(getActivity(), "产品信息: " + Build.MANUFACTURER + ","
//						+ android.os.Build.VERSION.SDK + ","
//						+ android.os.Build.VERSION.RELEASE);
				intent = new Intent(getActivity(), ImportantAuthorityActivity.class);
				break;
			case R.id.tv_setting:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKSET);
				intent = new Intent(getActivity(), SettingActivity.class);
				break;
			case R.id.rl_people_center:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKENTERPERSONCENTER);
				intent = new Intent(getActivity(), PersonalCenterActivity.class);
				break;
			case R.id.billRelativeLayout:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKBALANCE);
				intent = new Intent(getActivity(), BalanceParticularsActivity.class);
				break;
			case R.id.rechargeTextView:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKRECHARGE);
				intent = new Intent(getActivity(), RechargeActivity.class);
				break;
			case R.id.ll_coming_tel_tip:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKCOMINGTELTIP);
				intent = new Intent(getActivity(), TipUserOptionActivity.class);
				intent.putExtra(IntentPutKeyConstant.TIP_TYPE, Constant.COMING_TEL_REMIND);
				break;
			case R.id.ll_qq_tip:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKQQTIP);
				intent = new Intent(getActivity(), TipUserOptionActivity.class);
				intent.putExtra(IntentPutKeyConstant.TIP_TYPE, Constant.QQ_REMIND);
				break;
			case R.id.ll_weixin_tip:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKWEIXINTIP);
				intent = new Intent(getActivity(), TipUserOptionActivity.class);
				intent.putExtra(IntentPutKeyConstant.TIP_TYPE, Constant.WEIXIN_REMIND);
				break;
			case R.id.ll_sms_tip:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKSMSTIP);
				intent = new Intent(getActivity(), TipUserOptionActivity.class);
				intent.putExtra(IntentPutKeyConstant.TIP_TYPE, Constant.MESSAGE_REMIND);
				break;
			case R.id.liftWristLinearLayout:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKLIFTWRIST);
				intent = new Intent(getActivity(), TipUserOptionActivity.class);
				intent.putExtra(IntentPutKeyConstant.TIP_TYPE, Constant.LIFT_WRIST);
				break;
			case R.id.ll_alarm_clock_tip:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKALARMTIP);
				intent = new Intent(getActivity(), AlarmClockActivity.class);
//                intent = new Intent(getActivity(), SetAlarmActivity.class);
				break;
			case R.id.unBindTextView:
				//断开连接
				ICSOpenVPNApplication.getInstance().uartService.disconnect();
				break;

		}

		getActivity().startActivity(intent);

	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_GET_BALANCE) {
			BalanceHttp http = (BalanceHttp) object;
			if (http.getBalanceEntity() != null)
				balanceTextView.setText(getString(R.string.balance) + ": " + http.getBalanceEntity().getAmount() + getString(R.string.yuan));
		} else if (cmdType == HttpConfigUrl.COMTYPE_ALARM_CLOCK_COUNT) {
			AlarmClockCountHttp alarmClockCountHttp = (AlarmClockCountHttp) object;
			if (alarmClockCountHttp.getStatus() == 1) {
				if (alarmClockCountHttp.getAlarmClockCount() != null)
					tvAlarmClockTip.setText(String.format(getResources().getString(R.string.opened_num_alarm_clock), alarmClockCountHttp.getAlarmClockCount().getTotalRows()));
				else {
					tvAlarmClockTip.setText(String.format(getResources().getString(R.string.opened_num_alarm_clock), "0"));
				}
			}
		}
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(getActivity(), errorMessage);
	}

	@Override
	public void noNet() {
		CommonTools.showShortToast(getActivity(), getResources().getString(R.string.no_wifi));
	}

	@OnClick(R.id.liftWristLinearLayout)
	public void onClick() {
	}


	public String getBleStatus() {
		return bleStatus;
	}

	public void setBleStatus(String bleStatus) {
		this.bleStatus = bleStatus;
	}
}
