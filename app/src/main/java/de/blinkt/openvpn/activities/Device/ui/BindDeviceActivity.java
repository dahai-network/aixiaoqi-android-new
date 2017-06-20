package de.blinkt.openvpn.activities.Device.ui;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Device.PresenterImpl.BindDevicePresenterImpl;
import de.blinkt.openvpn.activities.Device.View.BindDeviceView;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

import static de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth.sendMessageToBlueTooth;
import static de.blinkt.openvpn.constant.Constant.BIND_SUCCESS;


public class BindDeviceActivity extends BluetoothBaseActivity implements BindDeviceView, DialogInterfaceTypeBase {

	@BindView(R.id.stopTextView)
	TextView stopTextView;
	@BindView(R.id.tip_search)
	TextView tip_search;
	@BindView(R.id.search_bluetooth)
	TextView search_bluetooth;
	@BindView(R.id.findedImageView)
	ImageView findedImageView;
	@BindView(R.id.seekImageView)
	ImageView seekImageView;
	@BindView(R.id.uniImageView)
	ImageView uniImageView;
	SharedUtils utils = SharedUtils.getInstance();
	private DialogBalance noDevicedialog;

	private UartService mService = ICSOpenVPNApplication.uartService;//
	//设备名称：类型不同名称不同，分别有【unitoys、unibox】
	private String bluetoothName = Constant.UNITOYS;
	private Handler mHandler=new Handler(){};

	BindDevicePresenterImpl bindDevicePresenter;

	@Override
	public void showToast(String showContent) {
		super.showToast(showContent);
	}

	@Override
	public void showToast(int showContentId) {
		super.showToast(showContentId);
	}

	@Override
	public void finishView() {
		finish();
	}

	@Override
	public String getDeviceName() {
		return bluetoothName;
	}

	@Override
	public void tipSearchText(int tipText) {
		tip_search.setText(getString(tipText));
	}

	@Override
	public void SetUniImageViewBackground(int sourceId) {
		uniImageView.setBackgroundResource(sourceId);
	}

	@Override
	protected void findDevices(BluetoothDevice device, int rssi, byte[] scanRecord) {
		bindDevicePresenter.findDevices(device,rssi,scanRecord);
	}

	@Override
	public void setFindedImageView(int isVisible) {
		findedImageView.setVisibility(isVisible);
		if(isVisible==View.VISIBLE){
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_trans_seek_over);
			findedImageView.startAnimation(anim);
		}else{
			findedImageView.clearAnimation();
		}
	}

	@Override
	public void searchBluetoothText(int searchId) {
		search_bluetooth.setText(getString(searchId));
	}

	@Override
	public void toActivity() {
		toActivity(MyDeviceActivity.class);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_bind_device);
		bluetoothName = getIntent().getStringExtra(Constant.BRACELETNAME);
		ButterKnife.bind(this);
		bindDevicePresenter=new BindDevicePresenterImpl(this);
		if(bluetoothIsOpen()){
			setAnimation();
			scanLeDevice(true);
		}
		if (bluetoothName != null && bluetoothName.contains(Constant.UNIBOX)) {
			initUnibox();
		}
	}

	@Override
	public void connect(String macAddress) {
		super.connect(macAddress);
	}

	@Override
	public void scanLeDevice(boolean enable) {
		super.scanLeDevice(enable);
	}

	@Override
	public void scanNotFindDevice() {
		bindDevicePresenter.scanNotFindDevice();
	}

	private void initUnibox() {
		search_bluetooth.setText(getString(R.string.searching_unibox_strap));
		tip_search.setText(getString(R.string.please_makesure_bind));
		uniImageView.setBackgroundResource(R.drawable.pic_sdw);
	}

	//蓝牙服务是否已经打开
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_ENABLE_BT:
				if (resultCode == Activity.RESULT_OK) {
					setAnimation();
					scanLeDevice(true);
				} else {
					Log.d(TAG, "蓝牙未打开");
					finish();
				}
				break;
		}
	}

	//设置动画
	private void setAnimation() {
		if (seekImageView.getAnimation() != null)
			seekImageView.clearAnimation();
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate_seek);
		anim.setInterpolator(new LinearInterpolator());//代码设置插补器
		seekImageView.startAnimation(anim);
	}

	@Override
	public void onBackPressed() {
		stopTextView.performClick();
	}

	//连接成功设备以后
	@Override
	public void afterConnDevice() {
		if (bluetoothName != null) {
			if (bluetoothName.contains(Constant.UNIBOX)) {
				showIsBindLayout();
			} else {
				finish();
			}
		}
	}

	@Override
	public void showNotSearchDeviceDialog() {
		showDialog();
	}
	//创建提示对话框
	private void showDialog() {
		//不能按返回键，只能二选其一
		if(noDevicedialog!=null)
			noDevicedialog.show();
		if(noDevicedialog==null) {
			noDevicedialog = new DialogBalance(this, this, R.layout.dialog_balance, 2);
			if (bluetoothName != null && bluetoothName.contains(Constant.UNIBOX)) {
				noDevicedialog.changeText(getString(R.string.no_find_unibox), getResources().getString(R.string.retry));
			} else {
				noDevicedialog.changeText(getResources().getString(R.string.no_find_unitoys), getResources().getString(R.string.retry));
			}
		}
	}

	//停止搜索和隐藏对话框
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		if (noDevicedialog != null  && noDevicedialog.isShowing()) {
			noDevicedialog.dismiss();
		}
	}

	//回收数据，取消订阅
	@Override
	protected void onDestroy() {
		super.onDestroy();
		bindDevicePresenter.onDestory();
		seekImageView.clearAnimation();
		if (noDevicedialog != null) {
			noDevicedialog.dismiss();
			noDevicedialog = null;
		}

	}


	//停止绑定，清除设备信息
	@OnClick(R.id.stopTextView)
	public void onClick() {
        Log.d(TAG, "onClick: 停止绑定");
        //发送绑定失败
       // SendCommandToBluetooth.sendMessageToBlueTooth(Constant.BIND_FAIL);
		scanLeDevice(false);
        mService.disconnect();

		ICSOpenVPNApplication.isConnect = false;
		utils.delete(Constant.IMEI);
		utils.delete(Constant.BRACELETNAME);
		finish();
	}
	@Override
	public void dialogText(int type, String text) {
		if (type == 2) {
			scanLeDevice(true);
		} else {
			stopTextView.performClick();
		}
	}

	//连接设备成功，提示用户绑定
	private void showIsBindLayout() {
		tip_search.setText(getString(R.string.finded_bracelet));
		search_bluetooth.setText(getString(R.string.click_bracelet_sure_bind));
		seekImageView.clearAnimation();
		if (bluetoothName != null) {
			if (bluetoothName.equals(Constant.UNIBOX)) {
				seekImageView.setBackgroundResource(R.drawable.seek_finish_pic);
				setFindedImageView(View.VISIBLE);
			}
		}
	}

}
