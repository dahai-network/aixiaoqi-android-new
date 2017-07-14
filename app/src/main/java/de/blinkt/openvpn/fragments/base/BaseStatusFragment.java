package de.blinkt.openvpn.fragments.base;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Device.ui.MyDeviceActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.CanClickEntity;
import de.blinkt.openvpn.model.SimRegisterStatue;
import de.blinkt.openvpn.model.StateChangeEntity;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.TopProgressView;

/**
 * Created by Administrator on 2017/4/26 0026.
 */

public class BaseStatusFragment extends Fragment {
	private int id;
	protected TopProgressView topProgressView;
	private BluetoothManager mBluetoothManager;
	protected BluetoothAdapter mBluetoothAdapter;
	protected void setLayoutId(int id) {
		this.id = id;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(id,
				container, false);
		initView(rootView);
		EventBus.getDefault().register(this);
		return rootView;
	}


	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter through
		// BluetoothManager.

		if (! ICSOpenVPNApplication.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {//蓝牙不支持低功耗蓝牙
			return false;
		}
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) ICSOpenVPNApplication.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null) {
				return false;
			}
		}
		mBluetoothAdapter = mBluetoothManager.getAdapter();

		if (mBluetoothAdapter == null) {
			return false;
		}
		return true;
	}
	//接收到到卡注册状态作出相应的操作
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void receiveStateChangeEntity(StateChangeEntity entity) {
		switch (entity.getStateType()) {
			case StateChangeEntity.BLUETOOTH_STATE:
					if (checkBlueIsOpen()) {
						topProgressView.setVisibility(View.GONE);
						//有蓝牙了重新连接
						String macStr = SharedUtils.getInstance().readString(Constant.IMEI);
						if (!TextUtils.isEmpty(macStr) && ICSOpenVPNApplication.uartService != null&&ICSOpenVPNApplication.uartService.isDisconnectedBlueTooth()) {
							ICSOpenVPNApplication.uartService.connect(macStr);
						}
				} else {
					topProgressView.showTopProgressView(getString(R.string.bluetooth_unopen), -1, null);
				}
				break;
			case StateChangeEntity.NET_STATE:
					if (checkNetWork()) {
						topProgressView.setVisibility(View.GONE);
						if (ICSOpenVPNApplication.the_sipengineReceive == null) {
							EventBusUtil.getTokenRes();
						}
					}
				 else {
					topProgressView.showTopProgressView(getString(R.string.no_wifi), -1, null);
					setRegisted(false);
				}
				break;
		}

	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void canClickEntity(CanClickEntity entity) {
		switch (entity.getJumpTo()) {
			case CanClickEntity.JUMP_MYDEVICE:
				topProgressView.showTopProgressView(getString(R.string.un_connect_tip), -1, new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						toMyDeviceActivity();
					}
				});
				break;
		}

	}

	private void toMyDeviceActivity() {
		String braceletName = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
		if (braceletName != null) {
            Intent intent = new Intent(getActivity(), MyDeviceActivity.class);
            intent.putExtra(Constant.BRACELETNAME, braceletName);
            startActivity(intent);
        }
	}

	public void topProgressGone() {
		topProgressView.setVisibility(View.GONE);
		topProgressView.setProgress(0);
	}

	public void setRegisted(boolean isRegister) {

	}


	protected  String unBind;

	//接收到到卡注册状态作出相应的操作
	@Subscribe(threadMode = ThreadMode.MAIN)//ui线程
	public void onIsSuccessEntity(SimRegisterStatue entity) {

		switch (entity.getRigsterSimStatue()) {
			case SocketConstant.REGISTER_SUCCESS:
				bleStatus=getString(R.string.index_high_signal);
				topProgressGone();
				setRegisted(true);
				break;
			case SocketConstant.REGISTER_FAIL:
				bleStatus=getString(R.string.index_regist_fail);
				setRegisted(false);
				topProgressGone();
				if(entity.getRigsterStatueReason()==SocketConstant.REGISTER_FAIL_INITIATIVE){
					unBind=getString(R.string.remove_bind);
				}

				break;
			case SocketConstant.UNREGISTER://未注册
				showStatue(entity);
				setRegisted(false);
				break;
			case SocketConstant.REGISTERING:
				if(SocketConstant.REGISTER_STATUE_CODE!=3){
					bleStatus=getString(R.string.index_registing);
					registeringReason(entity);
					setRegisted(false);
				}
				break;
		}
		setBleStatus(bleStatus);
	}
	protected  void setBleStatus(String bleStatus){

	}
	public static String bleStatus;

	@Override
	public void onResume() {
		super.onResume();
		if(initialize())
		checkBlueIsOpen();
	}

	private void showStatue(SimRegisterStatue entity){
		switch (entity.getRigsterStatueReason()){
			case SocketConstant.UN_INSERT_CARD:
				bleStatus=getString(R.string.index_un_insert_card);
				break;
			case SocketConstant.AIXIAOQI_CARD:
				bleStatus=getString(R.string.index_aixiaoqicard);
				break;
			case SocketConstant.CONNECTING_DEVICE:
				bleStatus=getString(R.string.index_connecting);
				break;
			case SocketConstant.DISCOONECT_DEVICE:
				bleStatus=getString(R.string.index_unconnect);
				break;
		}

	}

	private void 	registeringReason(SimRegisterStatue entity){
		double percent = entity.getProgressCount();
		if (topProgressView.getVisibility() != View.VISIBLE && SocketConstant.REGISTER_STATUE_CODE != 3) {
			topProgressView.setVisibility(View.VISIBLE);
			topProgressView.setContent(getString(R.string.registing));
			topProgressView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					toMyDeviceActivity();
				}
			});
		}
		 MyDeviceActivity.percentInt = (int) (percent / 1.6);
		if ( MyDeviceActivity.percentInt >= 100) {
			MyDeviceActivity.percentInt = 98;
		}
		topProgressView.setProgress( MyDeviceActivity.percentInt);

	}

	private boolean checkNetWork() {
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			topProgressView.showTopProgressView(getString(R.string.no_wifi), -1, null);
			return false;
		}
		return true;
	}

	private boolean checkBlueIsOpen() {
		if(!checkNetWork()){
			return false;
		}
	if (!mBluetoothAdapter.isEnabled()) {
			topProgressView.showTopProgressView(getString(R.string.bluetooth_unopen), -1, null);
			return false;
		}
		return true;
	}

	private void initView(View view) {
		topProgressView = (TopProgressView) view.findViewById(R.id.top_view);
		if (!NetworkUtils.isNetworkAvailable(getActivity())) {
			topProgressView.showTopProgressView(getString(R.string.no_wifi), -1, null);
		}
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
}
