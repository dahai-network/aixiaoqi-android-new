package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/13.
 */

public class BluetoothMessageCallBackEntity implements Serializable{
	private String blueType;
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


}
