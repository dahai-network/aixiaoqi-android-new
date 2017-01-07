package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/13.
 */
public class PacketMarketEntity implements Serializable {


	/**
	 * CountryID : c0737684-8d46-40b5-9ddd-8678f25a7521
	 * CountryName : 香港
	 * CountryCode : 0000
	 * Pic : http://image4.ali168.com/Unitoys/2016/09/1609021159400268558.png
	 * LogoPic : http://image4.ali168.com/Unitoys/2016/09/1609021159424585898.png
	 * Rate : 1
	 * IsHot : true
	 * Continents : 0
	 * ContinentsDescr : Asia
	 */

	private String CountryID;
	private String CountryName;
	private String CountryCode;
	private String Pic;
	private String LogoPic;
	private int Rate;
	private boolean IsHot;
	private int Continents;
	private String ContinentsDescr;


	public String getCountryID() {
		return CountryID;
	}

	public void setCountryID(String CountryID) {
		this.CountryID = CountryID;
	}

	public String getCountryName() {
		return CountryName;
	}

	public void setCountryName(String CountryName) {
		this.CountryName = CountryName;
	}

	public String getCountryCode() {
		return CountryCode;
	}

	public void setCountryCode(String CountryCode) {
		this.CountryCode = CountryCode;
	}

	public String getPic() {
		return Pic;
	}

	public void setPic(String Pic) {
		this.Pic = Pic;
	}

	public String getLogoPic() {
		return LogoPic;
	}

	public void setLogoPic(String LogoPic) {
		this.LogoPic = LogoPic;
	}

	public int getRate() {
		return Rate;
	}

	public void setRate(int Rate) {
		this.Rate = Rate;
	}

	public boolean isIsHot() {
		return IsHot;
	}

	public void setIsHot(boolean IsHot) {
		this.IsHot = IsHot;
	}

	public int getContinents() {
		return Continents;
	}

	public void setContinents(int Continents) {
		this.Continents = Continents;
	}

	public String getContinentsDescr() {
		return ContinentsDescr;
	}

	public void setContinentsDescr(String ContinentsDescr) {
		this.ContinentsDescr = ContinentsDescr;
	}

}
