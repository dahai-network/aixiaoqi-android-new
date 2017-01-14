package de.blinkt.openvpn.model;

/**
 * Created by Administrator on 2017/1/14.
 */

public class GetHostAndPortEntity {

	/**
	 * Out : {"AsteriskIp":"120.25.161.113","AsteriskPort":"65060","PublicPassword":"e40c3d9e04df371e329d8cda24ecc367"}
	 * In : {"AsteriskIp":"115.29.41.39","AsteriskPort":"5060"}
	 * VswServer : {"Ip":"120.76.240.82","Port":"5089"}
	 */

	private OutBean Out;
	private InBean In;
	private VswServerBean VswServer;

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

	public VswServerBean getVswServer() {
		return VswServer;
	}

	public void setVswServer(VswServerBean VswServer) {
		this.VswServer = VswServer;
	}

	public static class OutBean {
		/**
		 * AsteriskIp : 120.25.161.113
		 * AsteriskPort : 65060
		 * PublicPassword : e40c3d9e04df371e329d8cda24ecc367
		 */

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

	public static class InBean {
		/**
		 * AsteriskIp : 115.29.41.39
		 * AsteriskPort : 5060
		 */

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

	public static class VswServerBean {
		/**
		 * Ip : 120.76.240.82
		 * Port : 5089
		 */

		private String Ip;
		private int Port;

		public String getIp() {
			return Ip;
		}

		public void setIp(String Ip) {
			this.Ip = Ip;
		}

		public int getPort() {
			return Port;
		}

		public void setPort(int Port) {
			this.Port = Port;
		}
	}

}
