package cn.com.johnson.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/6.
 */
public class HotPackageEntity implements Serializable {
	private String CountryID;
	private String CountryName;
	private String CountryCode;
	private String Pic;
	private String LogoPic;
	private int Rate;

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

	@Override
	public String toString() {
		return "HotPackageData{" +
				"CountryID='" + CountryID + '\'' +
				", CountryName='" + CountryName + '\'' +
				", CountryCode='" + CountryCode + '\'' +
				", Pic='" + Pic + '\'' +
				", LogoPic='" + LogoPic + '\'' +
				", Rate=" + Rate +
				'}';
	}
}
