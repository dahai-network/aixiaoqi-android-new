package de.blinkt.openvpn.model;

/**
 * Created by Administrator on 2017/5/15.
 */

public class CheckConfirmedEntity {
	/**
	 * IsConfirmed : false
	 * Tel :
	 */

	private boolean IsConfirmed;
	private String Tel;

	public boolean isIsConfirmed() {
		return IsConfirmed;
	}

	public void setIsConfirmed(boolean IsConfirmed) {
		this.IsConfirmed = IsConfirmed;
	}

	public String getTel() {
		return Tel;
	}

	public void setTel(String Tel) {
		this.Tel = Tel;
	}
}
