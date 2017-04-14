package de.blinkt.openvpn.fragments;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;
import com.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.widget.GlideCircleTransform;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.BalanceParticularsActivity;
import de.blinkt.openvpn.activities.ChoiceDeviceTypeActivity;
import de.blinkt.openvpn.activities.ImportantAuthorityActivity;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.activities.PackageCategoryActivity;
import de.blinkt.openvpn.activities.PackageMarketActivity;
import de.blinkt.openvpn.activities.PersonalCenterActivity;
import de.blinkt.openvpn.activities.RechargeActivity;
import de.blinkt.openvpn.activities.SettingActivity;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.BalanceHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.OrderUsageRemainHttp;
import de.blinkt.openvpn.model.ChangeConnectStatusEntity;
import de.blinkt.openvpn.model.UsageRemainEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.TitleBar;

import static android.view.View.GONE;
import static de.blinkt.openvpn.activities.MyDeviceActivity.BRACELETTYPE;
import static de.blinkt.openvpn.constant.Constant.BRACELETNAME;
import static de.blinkt.openvpn.constant.Constant.BRACELETPOWER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBALANCE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKENTERPERSONCENTER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKMYDEVICE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKMYPACKAGE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKRECHARGE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSET;


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
	@BindView(R.id.activateRelativeLayout)
	RelativeLayout activateRelativeLayout;
	@BindView(R.id.addDeviceRelativeLayout)
	RelativeLayout addDeviceRelativeLayout;
	@BindView(R.id.deviceSummarizedRelativeLayout)
	RelativeLayout deviceSummarizedRelativeLayout;
	@BindView(R.id.permission_set)
	TextView tvPermissionSet;
	@BindView(R.id.billtv)
	TextView billTv;
	@BindView(R.id.tv_setting)
	TextView tvSetting;
	@BindView(R.id.deviceNameTextView)
	TextView deviceNameTextView;
	@BindView(R.id.powerTextView)
	TextView powerTextView;
	@BindView(R.id.signalIconImageView)
	ImageView signalIconImageView;
	@BindView(R.id.operatorTextView)
	TextView operatorTextView;
	@BindView(R.id.rechargeTextView)
	TextView rechargeTextView;
	@BindView(R.id.unBindTextView)
	TextView unBindTextView;
	SharedUtils utils = SharedUtils.getInstance();
	@BindView(R.id.noPacketRelativeLayout)
	RelativeLayout noPacketRelativeLayout;
	@BindView(R.id.PacketRelativeLayout)
	RelativeLayout PacketRelativeLayout;
	@BindView(R.id.add_or_activate_package)
	TextView addOrActivatePackage;
	@BindView(R.id.call_time)
	TextView callTime;
	@BindView(R.id.flow)
	TextView flow;
	@BindView(R.id.flow_count)
	TextView flowCount;
	@BindView(R.id.package_all_count)
	TextView packageAllCount;
	//bluetooth status蓝牙状态
	private String bleStatus;
	private String TAG = "AccountFragment";
	boolean hasPackage = false;

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
		Glide.with(ICSOpenVPNApplication.getContext()).load(SharedUtils.getInstance().readString(Constant.USER_HEAD)).placeholder(R.drawable.default_head).error(R.drawable.default_head).
				transform(new GlideCircleTransform(getActivity())).into(headImageView);

	}

	@Override
	public void onResume() {
		super.onResume();
		//获取数据，每次都重新获取一次以保持正确性。
		getData();
		getPackage();
	}


	private void getPackage() {
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_USER_ORDER_USAGE_REMAINING);
	}

	public void showDeviceSummarized(boolean isShow) {
		if (isShow) {
			deviceSummarizedRelativeLayout.setVisibility(View.VISIBLE);
		} else {
			deviceSummarizedRelativeLayout.setVisibility(GONE);
		}
	}

	public void setSummarized(String deviceType, String powerPercent, boolean isRegisted) {
		deviceNameTextView.setText(deviceType);
		powerTextView.setText(powerPercent + "%");
		setRegisted(isRegisted);
	}

	public void setRegisted(boolean isRegisted) {
		if (isRegisted) {
			signalIconImageView.setBackgroundResource(R.drawable.registed);
			String operater = SharedUtils.getInstance().readString(Constant.OPERATER);
			if (operater != null) {
				switch (operater) {
					case Constant.CHINA_TELECOM:
						operatorTextView.setText(getString(R.string.china_telecom));
						break;
					case Constant.CHINA_MOBILE:
						operatorTextView.setText(getString(R.string.china_mobile));
						break;
					case Constant.CHINA_UNICOM:
						operatorTextView.setText(getString(R.string.china_unicom));
						break;
				}
			}
		} else {
			signalIconImageView.setBackgroundResource(R.drawable.unregist);
			operatorTextView.setText("----");
		}
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

	}


	@OnClick({R.id.rechargeTextView,
			R.id.activateRelativeLayout,
			R.id.billtv,
			R.id.addDeviceRelativeLayout,
			R.id.tv_setting,
			R.id.rl_people_center,
			R.id.permission_set,
			R.id.unBindTextView,
			R.id.going_buy
	})
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.activateRelativeLayout:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKMYPACKAGE);
				if (!hasPackage) {
					intent = new Intent(getActivity(), PackageMarketActivity.class);
					intent.putExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE, Constant.SHOW);
				} else {
					intent = new Intent(getActivity(), PackageCategoryActivity.class);
				}
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
				intent.putExtra(BRACELETTYPE, SharedUtils.getInstance().readString(Constant.BRACELETNAME));
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
			case R.id.billtv:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKBALANCE);
				intent = new Intent(getActivity(), BalanceParticularsActivity.class);
				break;
			case R.id.rechargeTextView:
				//友盟方法统计
				MobclickAgent.onEvent(getActivity(), CLICKRECHARGE);
				intent = new Intent(getActivity(), RechargeActivity.class);
				break;
			case R.id.unBindTextView:
				//断开连接
				CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_UN_BIND_DEVICE);

				return;
			case R.id.going_buy:
				intent = new Intent(getActivity(), PackageMarketActivity.class);
				intent.putExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE, Constant.SHOW);
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
		} else  if (cmdType == HttpConfigUrl.COMTYPE_UN_BIND_DEVICE) {
			if (object.getStatus() == 1) {
				SharedUtils.getInstance().delete(BRACELETPOWER);
				SharedUtils.getInstance().delete(Constant.IMEI);
				SharedUtils.getInstance().delete(BRACELETNAME);
				SharedUtils.getInstance().delete(Constant.BRACELETVERSION);
				BluetoothConstant.IS_BIND = false;
				//判断是否再次重连的标记
				ICSOpenVPNApplication.isConnect = false;
				ReceiveBLEMoveReceiver.isConnect = false;
				EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL_INITIATIVE);
				sendEventBusChangeBluetoothStatus(getString(R.string.index_unbind));
				CommonTools.showShortToast(getActivity(), "已解绑设备");
				ICSOpenVPNApplication.uartService.disconnect();
				showDeviceSummarized(false);
			} else {
				CommonTools.showShortToast(getActivity(), object.getMsg());
				Log.i(TAG, object.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_GET_USER_ORDER_USAGE_REMAINING) {
			if (object.getStatus() == 1) {
				OrderUsageRemainHttp orderUsageRemainHttp = (OrderUsageRemainHttp) object;
				UsageRemainEntity.Unactivated unactivated = orderUsageRemainHttp.getUsageRemainEntity().getUnactivated();
				UsageRemainEntity.Used used = orderUsageRemainHttp.getUsageRemainEntity().getUsed();
				if ("0".equals(used.getTotalNum()) && !"0".equals(unactivated.getTotalNumFlow()) && "0".equals(used.getTotalNumFlow())) {//有套餐，未激活
					hasPackage = true;
					PacketRelativeLayout.setVisibility(View.GONE);
					noPacketRelativeLayout.setVisibility(View.VISIBLE);
					Drawable drawable = getResources().getDrawable(R.drawable.activate_device_account);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					addOrActivatePackage.setCompoundDrawables(drawable, null, null, null);
					addOrActivatePackage.setText(getString(R.string.activate_packet));
				}
				else	if ("0".equals(used.getTotalNum()) && "0".equals(unactivated.getTotalNumFlow())) {//无套餐显示
					hasPackage = false;
					PacketRelativeLayout.setVisibility(View.GONE);
					noPacketRelativeLayout.setVisibility(View.VISIBLE);
					Drawable drawable = getResources().getDrawable(R.drawable.add_device);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					addOrActivatePackage.setCompoundDrawables(drawable, null, null, null);
					addOrActivatePackage.setText(getString(R.string.add_package));
				}   else {//有套餐且激活了。
					hasPackage = true;
					PacketRelativeLayout.setVisibility(View.VISIBLE);
					noPacketRelativeLayout.setVisibility(View.GONE);
					callTime.setText(used.getTotalRemainingCallMinutes() + "分");
					if ("0".equals(used.getTotalNumFlow())) {
						flow.setText(getString(R.string.no_flow_count));
						flowCount.setText(unactivated.getTotalNumFlow());
					} else {
						flow.setText(getString(R.string.flow_count));
						flowCount.setText(used.getTotalNumFlow());
					}
					packageAllCount.setText(used.getTotalNum());
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



	public String getBleStatus() {
		return bleStatus;
	}

	public void setBleStatus(String bleStatus) {
		this.bleStatus = bleStatus;
	}

	/**
	 * 修改蓝牙连接状态，通过EVENTBUS发送到各个页面。
	 */
	private void sendEventBusChangeBluetoothStatus(String status) {
		int statusDrawable = R.drawable.index_connecting;
		if (status.equals(getString(R.string.index_connecting))) {
		} else if (status.equals(getString(R.string.index_aixiaoqicard))) {
			statusDrawable = R.drawable.index_no_signal;
		} else if (status.equals(getString(R.string.index_no_signal))) {
			statusDrawable = R.drawable.index_no_signal;
		} else if (status.equals(getString(R.string.index_regist_fail))) {
			statusDrawable = R.drawable.index_no_signal;
		} else if (status.equals(getString(R.string.index_registing))) {
			statusDrawable = R.drawable.index_no_signal;
		} else if (status.equals(getString(R.string.index_unbind))) {
			statusDrawable = R.drawable.index_unbind;
		} else if (status.equals(getString(R.string.index_no_packet))) {
			statusDrawable = R.drawable.index_no_packet;
		} else if (status.equals(getString(R.string.index_un_insert_card))) {
			statusDrawable = R.drawable.index_no_signal;
		} else if (status.equals(getString(R.string.index_high_signal))) {
			statusDrawable = R.drawable.index_high_signal;
		} else if (status.equals(getString(R.string.index_blue_un_opne))) {
			statusDrawable = R.drawable.index_blue_unpen;
		}
		ChangeConnectStatusEntity entity = new ChangeConnectStatusEntity();
		entity.setStatus(status);
		entity.setStatusDrawableInt(statusDrawable);
		EventBus.getDefault().post(entity);
	}
}
