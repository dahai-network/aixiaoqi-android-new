package de.blinkt.openvpn.model;

/**
 * Created by wzj on 2017/2/9.
 * 修改连接状态
 */
public class ChangeConnectStatusEntity {
	private String status;
	private int statusDrawableInt;

	public String getStatus() {
		return status;
	}

	public void setStatus(String statusInt) {
		this.status = statusInt;
	}

	public int getStatusDrawableInt() {
		return statusDrawableInt;
	}

	public void setStatusDrawableInt(int statusDrawableInt) {
		this.statusDrawableInt = statusDrawableInt;
	}
}
