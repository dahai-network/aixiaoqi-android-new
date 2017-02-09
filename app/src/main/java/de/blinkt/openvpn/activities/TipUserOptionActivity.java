package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.UploadRemindConfigHttp;
import de.blinkt.openvpn.util.BLECheckBitUtil;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.SwitchView;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSTARTNOTIFIACATION;
import static de.blinkt.openvpn.constant.UmengContant.CLICKTIPBINDDEVICE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKTIPSWITCH;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class TipUserOptionActivity extends BaseActivity implements InterfaceCallback {


	@BindView(R.id.open_bluetooth_tv)
	TextView openBluetoothTv;
	@BindView(R.id.opened_option_tip_tv)
	TextView openedOptionTipTv;
	@BindView(R.id.opened_option_reminder_tv)
	TextView openedOptionReminderTv;
	@BindView(R.id.switchView)
	SwitchView switchView;
	@BindView(R.id.option_type_iv)
	ImageView optionTypeIv;
	@BindView(R.id.phone_link_blurtooth_rl)
	RelativeLayout phoneLinkBlurtoothRl;
	@BindView(R.id.open_bluetooth_ll)
	RelativeLayout openBluetoothLl;
	@BindView(R.id.phoneImageView)
	ImageView phoneImageView;
	@BindView(R.id.deviceImageView)
	ImageView deviceImageView;
	@BindView(R.id.optionBackLinearLayout)
	LinearLayout optionBackLinearLayout;
	@BindView(R.id.bluetooth_tv)
	TextView bluetoothTv;
	@BindView(R.id.iv_load_push)
	ImageView ivLoadPush;
	@BindView(R.id.tv_load_push)
	TextView tvLoadPush;
	@BindView(R.id.ll_load_push)
	LinearLayout llLoadPush;
	@BindView(R.id.openNotificationTextView)
	TextView openNotificationTextView;
	@BindView(R.id.openNotificationImageView)
	ImageView openNotificationImageView;
	@BindView(R.id.openNotificationLoadTextView)
	TextView openNotificationLoadTextView;
	@BindView(R.id.openNotificationLinearLayout)
	LinearLayout openNotificationLinearLayout;
	@BindView(R.id.openNotiCLickTextView)
	TextView openNotiCLickTextView;
	@BindView(R.id.openNotificationRelativeLayout)
	RelativeLayout openNotificationRelativeLayout;
	private boolean isLinkBluetooth;
	private SharedUtils utils = SharedUtils.getInstance();
	private int REQUEST_ENABLE_BT = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip_user_option);
		ButterKnife.bind(this);
		initTitle();
	}

	String type;
	String openedTip;
	String openedRecevieTip;

	private void formatString(int opened) {
		if (type.equals(Constant.LIFT_WRIST)) {
			openedTip = String.format(getResources().getString(R.string.opened_lift_wrist_tip), getResources().getString(opened));
			openedOptionReminderTv.setVisibility(View.GONE);
		} else {
			openedTip = String.format(getResources().getString(R.string.opened_tip), getResources().getString(opened));
			openedRecevieTip = String.format(getResources().getString(R.string.opened_revecie_tip), getResources().getString(opened));
		}
	}

	private void backgroundType(int resId) {
		optionTypeIv.setImageResource(resId);
	}

	private void backgroundConnect(int resPhone, int resDevice, int backColor) {
		phoneImageView.setImageResource(resPhone);
		deviceImageView.setImageResource(resDevice);
		optionBackLinearLayout.setBackgroundColor(ContextCompat.getColor(this, backColor));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isEnabled()) {
			openNotificationRelativeLayout.setVisibility(View.VISIBLE);
		} else {
			openNotificationRelativeLayout.setVisibility(View.GONE);
		}
		if (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))) {
			bluetoothTv.setText(getString(R.string.no_bind_bluetooth_device));
			openBluetoothTv.setText(getString(R.string.go_bind));
		} else if (uartservice != null && uartservice.mConnectionState == UartService.STATE_CONNECTED) {
			isLinkBluetooth = true;
			//判断是否重连/连接蓝牙中
		} else if (uartservice.isConnecttingBlueTooth()) {
			bluetoothIsOpen();
		}
		//如果连接上，图片要相应不同
		if (isLinkBluetooth) {
			switchView.setNoClick(false);
			openBluetoothLl.setVisibility(View.GONE);
			phoneLinkBlurtoothRl.setBackgroundColor(getResources().getColor(R.color.color01d0c0));
			setBackgroundLink();
		} else {
			switchView.setNoClick(true);
			phoneLinkBlurtoothRl.setBackgroundColor(getResources().getColor(R.color.color333a48));
			setBackgroundNotLink();
		}
		openedOptionTipTv.setText(openedTip);
		openedOptionReminderTv.setText(openedRecevieTip);
	}

	UartService uartservice;

	private void initTitle() {
		uartservice = ICSOpenVPNApplication.uartService;
		type = getIntent().getStringExtra(IntentPutKeyConstant.TIP_TYPE);
		switchView.setOpened(utils.readInt(type) == 1 ? true : false);


	}

	private void setBackgroundNotLink() {
		backgroundConnect(R.drawable.tip_phone_fail, R.drawable.bluetooth_fail, R.color.color333a48);
		if (Constant.LIFT_WRIST.equals(type)) {
			formatString(R.string.lift_wrist);
			hasLeftViewTitle(R.string.lift_wrist, 0);
			backgroundType(R.drawable.sms_tip_fail);
		} else if (Constant.MESSAGE_REMIND.equals(type)) {
			formatString(R.string.sms);
			hasLeftViewTitle(R.string.sms_tip, 0);
			backgroundType(R.drawable.sms_tip_fail);
		} else if (Constant.QQ_REMIND.equals(type)) {
			formatString(R.string.qq);
			hasLeftViewTitle(R.string.qq_tip, 0);
			backgroundType(R.drawable.qq_tip_fail);
		} else if (Constant.WEIXIN_REMIND.equals(type)) {
			formatString(R.string.weixin_remider);
			hasLeftViewTitle(R.string.weixin_tip, 0);
			backgroundType(R.drawable.weixin_tip_fail);
		} else if (Constant.COMING_TEL_REMIND.equals(type)) {
			formatString(R.string.coming_tel);
			hasLeftViewTitle(R.string.coming_tel_tip, 0);
			backgroundType(R.drawable.coming_tip_fail);
		}
	}

	//已连接上
	private void setBackgroundLink() {
		backgroundConnect(R.drawable.tip_phone_succeed, R.drawable.bluetooth_succeed, R.color.color06c6b8);
		if (Constant.LIFT_WRIST.equals(type)) {
			formatString(R.string.lift_wrist);
			hasLeftViewTitle(R.string.lift_wrist, 0);
			backgroundType(R.drawable.sms_tip_succeed);
		} else if (Constant.MESSAGE_REMIND.equals(type)) {
			formatString(R.string.sms);
			hasLeftViewTitle(R.string.sms_tip, 0);
			backgroundType(R.drawable.sms_tip_succeed);
		} else if (Constant.QQ_REMIND.equals(type)) {
			formatString(R.string.qq);
			hasLeftViewTitle(R.string.qq_tip, 0);
			backgroundType(R.drawable.qq_tip_succeed);
		} else if (Constant.WEIXIN_REMIND.equals(type)) {
			formatString(R.string.weixin_remider);
			hasLeftViewTitle(R.string.weixin_tip, 0);
			backgroundType(R.drawable.weixin_tip_succeed);
		} else if (Constant.COMING_TEL_REMIND.equals(type)) {
			formatString(R.string.coming_tel);
			hasLeftViewTitle(R.string.coming_tel_tip, 0);
			backgroundType(R.drawable.coming_tip_succeed);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				Toast.makeText(this, "蓝牙已启动", Toast.LENGTH_SHORT).show();
				bluetoothIsOpen();
			}
		}
	}

	private void bluetoothIsOpen() {
		openBluetoothTv.setVisibility(View.GONE);
		bluetoothTv.setVisibility(View.GONE);
		llLoadPush.setVisibility(View.VISIBLE);
		((AnimationDrawable) ivLoadPush.getBackground()).start();
	}

	private void bluetoothIsClose() {
		openBluetoothTv.setVisibility(View.VISIBLE);
		bluetoothTv.setVisibility(View.VISIBLE);
		llLoadPush.setVisibility(View.GONE);
		((AnimationDrawable) ivLoadPush.getBackground()).stop();
	}

	@Override
	public void onBackPressed() {
		((AnimationDrawable) ivLoadPush.getBackground()).stop();
		UploadRemindConfigHttp http = null;
		if (switchView != null) {
			if (switchView.isOpened()) {
				http = new UploadRemindConfigHttp(this, HttpConfigUrl.COMTYPE_UPLOAD_REMIND_CONFIG, type, 1);
			} else {
				http = new UploadRemindConfigHttp(this, HttpConfigUrl.COMTYPE_UPLOAD_REMIND_CONFIG, type, 0);
			}
		} else {
			super.onBackPressed();
		}
		new Thread(http).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@OnClick({R.id.open_bluetooth_tv, R.id.openNotiCLickTextView})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.open_bluetooth_tv:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKTIPBINDDEVICE);
				if (getString(R.string.go_bind).equals(openBluetoothTv.getText().toString())) {
					toActivity(MyDeviceActivity.class);
				} else {
					Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				}
				break;
			case R.id.openNotiCLickTextView:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKSTARTNOTIFIACATION);
				startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
				break;
		}
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_UPLOAD_REMIND_CONFIG) {
			if (object.getStatus() == 1) {
				HashMap<String, String> map = new HashMap<String, String>();
				if (switchView.isOpened()) {
					//友盟方法统计
					map.put("type", type);
					MobclickAgent.onEvent(context, CLICKTIPSWITCH, map);
					utils.writeInt(type, 1);
				} else {
					//友盟方法统计
					map.put("type", type);
					MobclickAgent.onEvent(context, CLICKTIPSWITCH, map);
					utils.writeInt(type, 0);
				}
				//发送蓝牙消息打开/关闭消息通知
				sendBlueEnable();
				finish();
			} else {
				CommonTools.showShortToast(this, getResources().getString(R.string.change_remind_config_fail));
				finish();
			}

		}

	}

	private void sendBlueEnable() {
		if (!type.equals(Constant.LIFT_WRIST)) {
			String enableQQ = "0" + utils.readInt(Constant.QQ_REMIND);
			String enableWeixin = "0" + utils.readInt(Constant.WEIXIN_REMIND);
			String enableComingTel = "0" + utils.readInt(Constant.COMING_TEL_REMIND);
			String enableMessage = "0" + utils.readInt(Constant.MESSAGE_REMIND);
			String blueStr = "AA0D07" + enableComingTel + enableMessage + enableWeixin + enableQQ;
			SendCommandToBluetooth.sendMessageToBlueTooth(blueStr +
					HexStringExchangeBytesUtil.bytesToHexString(new byte[]{BLECheckBitUtil.getXor(HexStringExchangeBytesUtil.hexStringToBytes(blueStr))})
			);
		}
	}



	@Override
	public void errorComplete(int cmdType, String errorMessage) {
		CommonTools.showShortToast(this, errorMessage);
		finish();
	}

	@Override
	public void noNet() {
		CommonTools.showShortToast(this, getResources().getString(R.string.no_wifi));
		finish();
	}

	private boolean isEnabled() {
		String pkgName = getPackageName();
		final String flat = Settings.Secure.getString(getContentResolver(),
				"enabled_notification_listeners");
		if (!TextUtils.isEmpty(flat)) {
			final String[] names = flat.split(":");
			for (int i = 0; i < names.length; i++) {
				final ComponentName cn = ComponentName.unflattenFromString(names[i]);
				if (cn != null) {
					if (TextUtils.equals(pkgName, cn.getPackageName())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
