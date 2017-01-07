package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/21.
 */

public class OrderAddEntity implements Serializable{

	/**
	 * OrderID : b2a2397e-51d2-4dc6-a753-7c967220a379
	 * OrderNum : 8022201609211438480778499
	 * OrderDate : 1474439928
	 * PackageId : 9001de0d-b1d7-40db-b82a-f22905d31ef4
	 * PackageName : 1分钱测试套餐
	 * Quantity : 1
	 * UnitPrice : 0.01
	 * TotalPrice : 0.01
	 * ExpireDays : 30.0
	 * Flow : 102400
	 * RemainingCallMinutes : 30
	 * PayUserAmount : 1.0
	 * IsPayUserAmount : 0
	 * PaymentMethod : 0
	 */

	private OrderBean order;

	public OrderBean getOrder() {
		return order;
	}

	public void setOrder(OrderBean order) {
		this.order = order;
	}

	public static class OrderBean implements Serializable {
		private String OrderID;
		private String OrderNum;
		private String OrderDate;
		private String PackageId;
		private String PackageName;
		private String Quantity;
		private double UnitPrice;
		private double TotalPrice;
		private String ExpireDays;
		private String Flow;
		private String RemainingCallMinutes;
		private double PayUserAmount;
		private String IsPayUserAmount;
		private String PaymentMethod;

		public String getOrderID() {
			return OrderID;
		}

		public void setOrderID(String OrderID) {
			this.OrderID = OrderID;
		}

		public String getOrderNum() {
			return OrderNum;
		}

		public void setOrderNum(String OrderNum) {
			this.OrderNum = OrderNum;
		}

		public String getOrderDate() {
			return OrderDate;
		}

		public void setOrderDate(String OrderDate) {
			this.OrderDate = OrderDate;
		}

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

		public String getQuantity() {
			return Quantity;
		}

		public void setQuantity(String Quantity) {
			this.Quantity = Quantity;
		}

		public double getUnitPrice() {
			return UnitPrice;
		}

		public void setUnitPrice(double UnitPrice) {
			this.UnitPrice = UnitPrice;
		}

		public double getTotalPrice() {
			return TotalPrice;
		}

		public void setTotalPrice(double TotalPrice) {
			this.TotalPrice = TotalPrice;
		}

		public String getExpireDays() {
			return ExpireDays;
		}

		public void setExpireDays(String ExpireDays) {
			this.ExpireDays = ExpireDays;
		}

		public String getFlow() {
			return Flow;
		}

		public void setFlow(String Flow) {
			this.Flow = Flow;
		}

		public String getRemainingCallMinutes() {
			return RemainingCallMinutes;
		}

		public void setRemainingCallMinutes(String RemainingCallMinutes) {
			this.RemainingCallMinutes = RemainingCallMinutes;
		}

		public double getPayUserAmount() {
			return PayUserAmount;
		}

		public void setPayUserAmount(double PayUserAmount) {
			this.PayUserAmount = PayUserAmount;
		}

		public String getIsPayUserAmount() {
			return IsPayUserAmount;
		}

		public void setIsPayUserAmount(String IsPayUserAmount) {
			this.IsPayUserAmount = IsPayUserAmount;
		}

		public String getPaymentMethod() {
			return PaymentMethod;
		}

		public void setPaymentMethod(String PaymentMethod) {
			this.PaymentMethod = PaymentMethod;
		}
	}
}
