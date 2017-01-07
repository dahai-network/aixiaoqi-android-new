package de.blinkt.openvpn.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/21.
 */

public class WeiXinResultEntity implements Serializable {

	/**
	 * appid : wxff7e7ee82cd9afc4
	 * noncestr : 2f9d647ac208497fa576425da7072cd0
	 * package : Sign=WXPay
	 * partnerid : 1388414002
	 * timestamp : 1474444307
	 * prepayid : wx2016092115514729e51af4240956178389
	 * sign : B21D014219C72950DDEBE59E00677237
	 */


	private String appid;
	private String noncestr;
	@SerializedName("package")
	private String packageX;
	private String partnerid;
	private String timestamp;
	private String prepayid;
	private String sign;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getNoncestr() {
		return noncestr;
	}

	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}

	public String getPackageX() {
		return packageX;
	}

	public void setPackageX(String packageX) {
		this.packageX = packageX;
	}

	public String getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getPrepayid() {
		return prepayid;
	}

	public void setPrepayid(String prepayid) {
		this.prepayid = prepayid;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "WeiXinResultEntity{" +
				"appid='" + appid + '\'' +
				", noncestr='" + noncestr + '\'' +
				", packageX='" + packageX + '\'' +
				", partnerid='" + partnerid + '\'' +
				", timestamp='" + timestamp + '\'' +
				", prepayid='" + prepayid + '\'' +
				", sign='" + sign + '\'' +
				'}';
	}
}
