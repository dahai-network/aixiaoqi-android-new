package de.blinkt.openvpn.model;

/**
 * Created by Administrator on 2017/3/14.
 * 保存蓝牙地址，信号
 */

public class BluetoothEntity {
	private String address;
	private int rssi;
	private String diviceName;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public String getDiviceName() {
		return diviceName;
	}

	public void setDiviceName(String diviceName) {
		this.diviceName = diviceName;
	}

	@Override
	public String toString() {
		return "BlueToothModel{" +
				"address='" + address + '\'' +
				", rssi=" + rssi +
				", diviceName='" + diviceName + '\'' +
				'}';
	}
}
