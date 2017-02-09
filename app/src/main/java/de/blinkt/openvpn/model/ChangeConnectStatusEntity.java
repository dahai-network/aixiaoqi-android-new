package de.blinkt.openvpn.model;

/**
 * Created by Administrator on 2017/2/9.
 * 修改连接状态
 */
public class ChangeConnectStatusEntity {
	private int statusInt;
	private int statusDrawableInt;

	public int getStatusInt() {
		return statusInt;
	}

	public void setStatusInt(int statusInt) {
		this.statusInt = statusInt;
	}

	public int getStatusDrawableInt() {
		return statusDrawableInt;
	}

	public void setStatusDrawableInt(int statusDrawableInt) {
		this.statusDrawableInt = statusDrawableInt;
	}
}
