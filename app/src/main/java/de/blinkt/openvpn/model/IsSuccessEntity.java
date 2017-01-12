package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/11.
 */

public class IsSuccessEntity  implements Serializable{
	private boolean isSuccess;
	//Goip注册失败类型
	private int failType;

	public int getFailType() {
		return failType;
	}

	public void setFailType(int failType) {
		this.failType = failType;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean success) {
		isSuccess = success;
	}
}
