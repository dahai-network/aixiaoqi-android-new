package cn.com.johnson.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 */
public class HotPackageData implements Serializable {

	/**
	 * status : 1
	 * data : [{"CountryID":"396d4776-b165-48d9-b308-711a5f0e96c3","CountryName":"英国","CountryCode":"0000","Pic":"http://image4.ali168.com/Unitoys/2016/09/1609021158033297834.png","LogoPic":"http://image4.ali168.com/Unitoys/2016/09/1609021158009400432.png","Rate":1},{"CountryID":"037cab71-1e65-4dd1-8add-0b931bcb7606","CountryName":"印尼","CountryCode":"0000","Pic":"http://image4.ali168.com/Unitoys/2016/09/1609021157364459386.png","LogoPic":"http://image4.ali168.com/Unitoys/2016/09/1609021157340688942.png","Rate":1},{"CountryID":"31b77c55-91de-4d14-9285-63d79c842956","CountryName":"意大利","CountryCode":"0000","Pic":"http://image4.ali168.com/Unitoys/2016/09/1609021158549880170.png","LogoPic":"http://image4.ali168.com/Unitoys/2016/09/1609021158512388496.png","Rate":1},{"CountryID":"9c541cdc-0735-4dbd-9871-29b567bd1308","CountryName":"新加坡","CountryCode":"0000","Pic":"http://image4.ali168.com/Unitoys/2016/09/1609021159211677332.png","LogoPic":"http://image4.ali168.com/Unitoys/2016/09/1609021159180504260.png","Rate":1},{"CountryID":"c0737684-8d46-40b5-9ddd-8678f25a7521","CountryName":"香港","CountryCode":"0000","Pic":"http://image4.ali168.com/Unitoys/2016/09/1609021159400268558.png","LogoPic":"http://image4.ali168.com/Unitoys/2016/09/1609021159424585898.png","Rate":1},{"CountryID":"616bc539-94b0-47d8-ab16-63153296aca6","CountryName":"泰国","CountryCode":"0000","Pic":"http://image4.ali168.com/Unitoys/2016/09/1609021200100241014.png","LogoPic":"http://image4.ali168.com/Unitoys/2016/09/1609021200060932864.png","Rate":1},{"CountryID":"61a876a5-a82d-463d-b606-b5e9f81512b2","CountryName":"台湾","CountryCode":"0000","Pic":"http://image4.ali168.com/Unitoys/2016/09/1609021200376384430.png","LogoPic":"http://image4.ali168.com/Unitoys/2016/09/1609021200344430078.png","Rate":1},{"CountryID":"f7fcaebe-14b1-4ef7-b4be-a829a15f1201","CountryName":"圣马力诺","CountryCode":"0000","Pic":"http://image4.ali168.com/Unitoys/2016/09/1609021153226149028.png","LogoPic":"http://image4.ali168.com/Unitoys/2016/09/1609021153193511056.png","Rate":1},{"CountryID":"a6e1a66e-62e5-4bfd-bd82-6bd901c15c20","CountryName":"瑞典","CountryCode":"0000","Pic":"http://image4.ali168.com/Unitoys/2016/09/1609021153013972912.png","LogoPic":"http://image4.ali168.com/Unitoys/2016/09/1609021152580456000.png","Rate":1},{"CountryID":"0a7590cd-61ae-4f50-b17a-e233055469cb","CountryName":"日本","CountryCode":"0000","Pic":"http://image4.ali168.com/Unitoys/2016/09/1609021200531859150.png","LogoPic":"http://image4.ali168.com/Unitoys/2016/09/1609021200500461460.png","Rate":1}]
	 */

	private int status;
	/**
	 * CountryID : 396d4776-b165-48d9-b308-711a5f0e96c3
	 * CountryName : 英国
	 * CountryCode : 0000
	 * Pic : http://image4.ali168.com/Unitoys/2016/09/1609021158033297834.png
	 * LogoPic : http://image4.ali168.com/Unitoys/2016/09/1609021158009400432.png
	 * Rate : 1
	 */

	private List<DataBean> data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public static class DataBean {
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
	}
}
