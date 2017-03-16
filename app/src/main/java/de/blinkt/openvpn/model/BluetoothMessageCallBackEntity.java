package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/13.
 */

public class BluetoothMessageCallBackEntity implements Serializable{
	public static String BIND_TYPE = "bind_type";
	public static String REGIST_TYPE = "regist_type";
	private String blueType;
	private String braceletversion;
	private boolean isSuccess;

	public String getBlueType() {
		return blueType;
	}

	public void setBlueType(String blueType) {
		this.blueType = blueType;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean success) {
		isSuccess = success;
	}

	public String getBraceletversion() {
		return braceletversion;
	}

	public void setBraceletversion(String braceletversion) {
		this.braceletversion = braceletversion;
	}
}
