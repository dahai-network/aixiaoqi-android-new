package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/8.
 */

public class BlueToothDeviceEntity implements Serializable{

	/**
	 * IMEI : 123456
	 * Version : 1.0.0
	 * CreateDate : 1475895458
	 */
	private String IMEI;
	private String Version;
	private String DeviceType;

	private String CreateDate;


	public String getDeviceType() {
		return DeviceType;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String IMEI) {
		this.IMEI = IMEI;
	}

	public String getVersion() {
		return Version;
	}

	public void setVersion(String Version) {
		this.Version = Version;
	}

	public String getCreateDate() {
		return CreateDate;
	}

	public void setCreateDate(String CreateDate) {
		this.CreateDate = CreateDate;
	}

}
