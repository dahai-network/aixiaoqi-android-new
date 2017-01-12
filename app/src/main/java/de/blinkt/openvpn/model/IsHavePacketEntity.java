package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/12.
 */

public class IsHavePacketEntity implements Serializable{
	/**
	 * Used : 0不存在/1存在
	 */

	private int Used;

	public int getUsed() {
		return Used;
	}

	public void setUsed(int Used) {
		this.Used = Used;
	}
}
