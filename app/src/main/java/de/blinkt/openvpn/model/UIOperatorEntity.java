package de.blinkt.openvpn.model;

public class UIOperatorEntity {
	public static int onError = 0;
	public static int onCompelete = 1;
	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}