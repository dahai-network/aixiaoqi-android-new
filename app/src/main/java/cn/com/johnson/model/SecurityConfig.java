package cn.com.johnson.model;

import java.io.Serializable;

/**
 * Created by wangqiangxin on 16/6/21.
 */
public class SecurityConfig implements Serializable {

	/**
	 * AsteriskIp : 115.29.41.39
	 * AsteriskPort : 5060
	 * PublicPassword : 1ea3f5980e15f3f82747a6eaae69c868
	 */

	private OutBean Out;
	/**
	 * AsteriskIp : 115.29.41.39
	 * AsteriskPort : 5060
	 */

	private InBean In;

	public OutBean getOut() {
		return Out;
	}

	public void setOut(OutBean Out) {
		this.Out = Out;
	}

	public InBean getIn() {
		return In;
	}

	public void setIn(InBean In) {
		this.In = In;
	}

	public  class OutBean implements Serializable{
		private String AsteriskIp;
		private String AsteriskPort;
		private String PublicPassword;

		public String getAsteriskIp() {
			return AsteriskIp;
		}

		public void setAsteriskIp(String AsteriskIp) {
			this.AsteriskIp = AsteriskIp;
		}

		public String getAsteriskPort() {
			return AsteriskPort;
		}

		public void setAsteriskPort(String AsteriskPort) {
			this.AsteriskPort = AsteriskPort;
		}

		public String getPublicPassword() {
			return PublicPassword;
		}

		public void setPublicPassword(String PublicPassword) {
			this.PublicPassword = PublicPassword;
		}
	}

	public  class InBean implements Serializable{
		private String AsteriskIp;
		private String AsteriskPort;

		public String getAsteriskIp() {
			return AsteriskIp;
		}

		public void setAsteriskIp(String AsteriskIp) {
			this.AsteriskIp = AsteriskIp;
		}

		public String getAsteriskPort() {
			return AsteriskPort;
		}

		public void setAsteriskPort(String AsteriskPort) {
			this.AsteriskPort = AsteriskPort;
		}
	}
}
