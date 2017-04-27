package de.blinkt.openvpn.model;

/**
 * Created by wzj on 2017/4/19.
 * 搜集顶部状态栏的状态改变通知
 */

public class StateChangeEntity {
	public static final String NET_STATE = "net_state";//网络状态
	public static final String BLUETOOTH_STATE = "bluetooth_state";//蓝牙状态
	public static final String JUMP_ACTIVITY="jump_activity";//正在注册sim卡
	private String stateType;
	private boolean isopen;

	public String getStateType() {
		return stateType;
	}

	public void setStateType(String stateType) {
		this.stateType = stateType;
	}

	public boolean isopen() {
		return isopen;
	}

	public void setIsopen(boolean isopen) {
		this.isopen = isopen;
	}
}
