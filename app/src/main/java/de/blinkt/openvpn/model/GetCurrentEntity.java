package de.blinkt.openvpn.model;

/**
 * Created by Administrator on 2017/5/15.
 */

public class GetCurrentEntity {

	/**
	 * Tel : string,绑定手机号
	 * ICCID : string,ICCID
	 */

	private String Tel;
	private String ICCID;

	public String getTel() {
		return Tel;
	}

	public void setTel(String Tel) {
		this.Tel = Tel;
	}

	public String getICCID() {
		return ICCID;
	}

	public void setICCID(String ICCID) {
		this.ICCID = ICCID;
	}
}