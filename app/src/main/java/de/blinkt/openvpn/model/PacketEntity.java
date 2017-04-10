package de.blinkt.openvpn.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/12/24.
 */

public class PacketEntity implements Serializable {

	/**
	 * totalRows : 1
	 * list : [{"PackageId":"6e9d162f-ea8d-4625-86f1-3aba1860891b","PackageName":"200分钟通话时长","PackageNum":"TH001","Operators":"爱小器","Price":"5.00","Flow":"不限制流量","Desction":null,"CallMinutes":200,"Pic":"http://img.unitoys.com/Unitoys/2016/12/1612231202009786345.png","ExpireDays":"30"}]
	 */

	private int totalRows;
	private List<ListBean> list;

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public List<ListBean> getList() {
		return list;
	}

	public void setList(List<ListBean> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "PacketEntity{" +
				"totalRows=" + totalRows +
				", list=" + list +
				'}';
	}

	public static class ListBean implements Serializable {
		/**
		 * PackageId : 6e9d162f-ea8d-4625-86f1-3aba1860891b
		 * PackageName : 200分钟通话时长
		 * PackageNum : TH001
		 * Operators : 爱小器
		 * Price : 5.00
		 * Flow : 不限制流量
		 * Desction : null
		 * CallMinutes : 200
		 * Pic : http://img.unitoys.com/Unitoys/2016/12/1612231202009786345.png
		 * ExpireDays : 30
		 */

		private String PackageId;
		private String PackageName;
		private String PackageNum;
		private String Operators;
		private String Price;
		private String Flow;
		private Object Desction;
		private int CallMinutes;
		private String Pic;
		private String ExpireDays;

		public String getPackageId() {
			return PackageId;
		}

		public void setPackageId(String PackageId) {
			this.PackageId = PackageId;
		}

		public String getPackageName() {
			return PackageName;
		}

		public void setPackageName(String PackageName) {
			this.PackageName = PackageName;
		}

		public String getPackageNum() {
			return PackageNum;
		}

		public void setPackageNum(String PackageNum) {
			this.PackageNum = PackageNum;
		}

		public String getOperators() {
			return Operators;
		}

		public void setOperators(String Operators) {
			this.Operators = Operators;
		}

		public String getPrice() {
			return Price;
		}

		public void setPrice(String Price) {
			this.Price = Price;
		}

		public String getFlow() {
			return Flow;
		}

		public void setFlow(String Flow) {
			this.Flow = Flow;
		}

		public Object getDesction() {
			return Desction;
		}

		public void setDesction(Object Desction) {
			this.Desction = Desction;
		}

		public int getCallMinutes() {
			return CallMinutes;
		}

		public void setCallMinutes(int CallMinutes) {
			this.CallMinutes = CallMinutes;
		}

		public String getPic() {
			return Pic;
		}

		public void setPic(String Pic) {
			this.Pic = Pic;
		}

		public String getExpireDays() {
			return ExpireDays;
		}

		public void setExpireDays(String ExpireDays) {
			this.ExpireDays = ExpireDays;
		}

		@Override
		public String toString() {
			return "ListBean{" +
					"PackageId='" + PackageId + '\'' +
					", PackageName='" + PackageName + '\'' +
					", PackageNum='" + PackageNum + '\'' +
					", Operators='" + Operators + '\'' +
					", Price='" + Price + '\'' +
					", Flow='" + Flow + '\'' +
					", Desction=" + Desction +
					", CallMinutes=" + CallMinutes +
					", Pic='" + Pic + '\'' +
					", ExpireDays='" + ExpireDays + '\'' +
					'}';
		}
	}
}
