package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/23.
 */

public class IsBindEntity implements Serializable{

	/**
	 * BindStatus : 0
	 */
	private int BindStatus;

	public int getBindStatus() {
		return BindStatus;
	}

	public void setBindStatus(int BindStatus) {
		this.BindStatus = BindStatus;
	}
}
