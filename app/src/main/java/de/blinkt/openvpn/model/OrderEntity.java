package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/19.
 */
public class OrderEntity implements Serializable {

	private String totalRows;
	/**
	 * OrderID : string,订单编码
	 * OrderNum : string,编号
	 * UserId : string,用户编码
	 * PackageName : string,套餐名称
	 * Flow : int,套餐包含的总流量
	 * Quantity : int,数量
	 * UnitPrice : Decimal,订单项单价
	 * TotalPrice : Decimal,订单项总价
	 * ExpireDays : int,有效天数
	 * OrderDate : int,订单日期
	 * PayDate : int,订单付款时间
	 * PayStatus : int,付款状态，0：未付款 1：已付款
	 * OrderStatus : int,订单状态,0 : 未激活，1：正在使用，2：已用完，3：已取消
	 * RemainingCallMinutes : int,剩余通话分钟数
	 * EffectiveDate : int,生效日期
	 * ActivationDate : int,激活时间
	 * PayUserAmount : Decimal,支付的用户金额
	 * IsPayUserAmount : bool,是否用户金额支付
	 * LogoPic : string,国家logo图(小图)
	 */

	private ListBean list;

	public String getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(String totalRows) {
		this.totalRows = totalRows;
	}

	public ListBean getList() {
		return list;
	}

	public void setList(ListBean list) {
		this.list = list;
	}

	public static class ListBean {
		private String OrderID;
		private String OrderNum;
		private String UserId;
		private String PackageName;
		private String Flow;
		private String Quantity;
		private float UnitPrice;
		private float TotalPrice;
		private String ExpireDays;
		private long OrderDate;
		private String PayDate;
		private String PayStatus;
		private int OrderStatus;
		private int RemainingCallMinutes;
		private String EffectiveDate;
		private String ActivationDate;
		private String PayUserAmount;
		private String IsPayUserAmount;
		private String LogoPic;
		private String PaymentMethod;
		private int ExpireDaysInt;
		private long LastCanActivationDate;
		private String PackageId;
		private String PackageFeatures;
		private String PackageDetails;
		private String Pic;
		private String PackageCategory;
		private boolean PackageIsSupport4G;
		private boolean PackageIsApn;
		private String CountryName;
		private String PackageApnName;

		public String getPackageApnName() {
			return PackageApnName;
		}

		public void setPackageApnName(String packageApnName) {
			PackageApnName = packageApnName;
		}

		public String getCountryName() {
			return CountryName;
		}

		public void setCountryName(String countryName) {
			CountryName = countryName;
		}

		public String getPackageCategory() {
			return PackageCategory;
		}

		public void setPackageCategory(String packageCategory) {
			PackageCategory = packageCategory;
		}

		public boolean isPackageIsSupport4G() {
			return PackageIsSupport4G;
		}

		public void setPackageIsSupport4G(boolean packageIsSupport4G) {
			PackageIsSupport4G = packageIsSupport4G;
		}

		public boolean isPackageIsApn() {
			return PackageIsApn;
		}

		public void setPackageIsApn(boolean packageIsApn) {
			PackageIsApn = packageIsApn;
		}

		public int getExpireDaysInt() {
			return ExpireDaysInt;
		}

		public void setExpireDaysInt(int expireDaysInt) {
			ExpireDaysInt = expireDaysInt;
		}

		public String getPaymentMethod() {
			return PaymentMethod;
		}

		public void setPaymentMethod(String paymentMethod) {
			PaymentMethod = paymentMethod;
		}

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

		public String getUserId() {
			return UserId;
		}

		public void setUserId(String UserId) {
			this.UserId = UserId;
		}

		public String getPackageName() {
			return PackageName;
		}

		public void setPackageName(String PackageName) {
			this.PackageName = PackageName;
		}

		public String getFlow() {
			return Flow;
		}

		public void setFlow(String Flow) {
			this.Flow = Flow;
		}

		public String getQuantity() {
			return Quantity;
		}

		public void setQuantity(String Quantity) {
			this.Quantity = Quantity;
		}

		public float getUnitPrice() {
			return UnitPrice;
		}

		public void setUnitPrice(float UnitPrice) {
			this.UnitPrice = UnitPrice;
		}

		public float getTotalPrice() {
			return TotalPrice;
		}

		public void setTotalPrice(float TotalPrice) {
			this.TotalPrice = TotalPrice;
		}

		public String getExpireDays() {
			return ExpireDays;
		}

		public void setExpireDays(String ExpireDays) {
			this.ExpireDays = ExpireDays;
		}

		public long getOrderDate() {
			return OrderDate;
		}

		public void setOrderDate(long OrderDate) {
			this.OrderDate = OrderDate;
		}

		public String getPayDate() {
			return PayDate;
		}

		public void setPayDate(String PayDate) {
			this.PayDate = PayDate;
		}

		public String getPayStatus() {
			return PayStatus;
		}

		public void setPayStatus(String PayStatus) {
			this.PayStatus = PayStatus;
		}

		public int getOrderStatus() {
			return OrderStatus;
		}

		public void setOrderStatus(int OrderStatus) {
			this.OrderStatus = OrderStatus;
		}

		public int getRemainingCallMinutes() {
			return RemainingCallMinutes;
		}

		public void setRemainingCallMinutes(int RemainingCallMinutes) {
			this.RemainingCallMinutes = RemainingCallMinutes;
		}

		public String getEffectiveDate() {
			return EffectiveDate;
		}

		public void setEffectiveDate(String EffectiveDate) {
			this.EffectiveDate = EffectiveDate;
		}

		public String getActivationDate() {
			return ActivationDate;
		}

		public void setActivationDate(String ActivationDate) {
			this.ActivationDate = ActivationDate;
		}

		public String getPayUserAmount() {
			return PayUserAmount;
		}

		public void setPayUserAmount(String PayUserAmount) {
			this.PayUserAmount = PayUserAmount;
		}

		public String getIsPayUserAmount() {
			return IsPayUserAmount;
		}

		public void setIsPayUserAmount(String IsPayUserAmount) {
			this.IsPayUserAmount = IsPayUserAmount;
		}

		public String getLogoPic() {
			return LogoPic;
		}

		public void setLogoPic(String LogoPic) {
			this.LogoPic = LogoPic;
		}

		public long getLastCanActivationDate() {
			return LastCanActivationDate;
		}

		public void setLastCanActivationDate(long lastCanActivationDate) {
			LastCanActivationDate = lastCanActivationDate;
		}

		public String getPackageId() {
			return PackageId;
		}

		public void setPackageId(String packageId) {
			PackageId = packageId;
		}

		public String getPackageFeatures() {
			return PackageFeatures;
		}

		public void setPackageFeatures(String packageFeatures) {
			PackageFeatures = packageFeatures;
		}

		public String getPackageDetails() {
			return PackageDetails;
		}

		public void setPackageDetails(String packageDetails) {
			PackageDetails = packageDetails;
		}

		public String getPic() {
			return Pic;
		}

		public void setPic(String pic) {
			Pic = pic;
		}

		@Override
		public String toString() {
			return "ListBean{" +
					"OrderID='" + OrderID + '\'' +
					", OrderNum='" + OrderNum + '\'' +
					", UserId='" + UserId + '\'' +
					", PackageName='" + PackageName + '\'' +
					", Flow='" + Flow + '\'' +
					", Quantity='" + Quantity + '\'' +
					", UnitPrice=" + UnitPrice +
					", TotalPrice=" + TotalPrice +
					", ExpireDays='" + ExpireDays + '\'' +
					", OrderDate=" + OrderDate +
					", PayDate='" + PayDate + '\'' +
					", PayStatus='" + PayStatus + '\'' +
					", OrderStatus=" + OrderStatus +
					", RemainingCallMinutes='" + RemainingCallMinutes + '\'' +
					", EffectiveDate='" + EffectiveDate + '\'' +
					", ActivationDate='" + ActivationDate + '\'' +
					", PayUserAmount='" + PayUserAmount + '\'' +
					", IsPayUserAmount='" + IsPayUserAmount + '\'' +
					", LogoPic='" + LogoPic + '\'' +
					", PaymentMethod='" + PaymentMethod + '\'' +
					", ExpireDaysInt=" + ExpireDaysInt +
					'}';
		}
	}

	@Override
	public String toString() {
		return "OrderEntity{" +
				"totalRows='" + totalRows + '\'' +
				", list=" + list +
				'}';
	}


}
