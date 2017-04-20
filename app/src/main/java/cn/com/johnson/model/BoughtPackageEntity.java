package cn.com.johnson.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/6.
 */
public class BoughtPackageEntity implements Serializable {


	private int totalRows;
	/**
	 * OrderID : a78a258c-bb68-4bd5-b636-43900a58a457
	 * OrderNum : 8022201609061136027418259
	 * UserId : 52dc2044-2ec6-4fd8-ae8e-66c18f75316d
	 * PackageName : 1分钱测试套餐
	 * Flow : 102400
	 * Quantity : 1
	 * UnitPrice : 0.01
	 * TotalPrice : 0.01
	 * ExpireDays : 30
	 * OrderDate : 2016-09-06T11:36:02.743
	 * PayDate : null
	 * PayStatus : 0
	 * OrderStatus : 0
	 * RemainingCallMinutes : 30
	 * EffectiveDate : null
	 * ActivationDate : null
	 * PayUserAmount : 0
	 * IsPayUserAmount : true
	 */

	private List<ListBean> list;

	public int getTotalRows() {
		return totalRows;
	}

	@Override
	public String toString() {
		return "DataBean{" +
				"totalRows=" + totalRows +
				", list=" + list +
				'}';
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public List<ListBean> getList() {
		if (list == null) {
			list = new ArrayList<>();
		}
		return list;
	}

	public void setList(List<ListBean> list) {
		this.list = list;
	}

	public static class ListBean implements Serializable {
		private String OrderID;
		private String OrderNum;
		private String UserId;
		private String PackageName;
		private String Flow;
		private int Quantity;
		private double UnitPrice;
		private double TotalPrice;
		private String ExpireDays;
		private String OrderDate;
		private Object PayDate;
		private int PayStatus;
		private int OrderStatus;
		private int RemainingCallMinutes;
		private String EffectiveDate;
		private Object ActivationDate;
		private int PayUserAmount;
		private String LogoPic;
		private int PackageCategory;
		private int IsPayUserAmount;
		private String PackageId;
		private boolean PackageIsCategoryFlow;
		private boolean PackageIsCategoryCall;
		private boolean PackageIsCategoryDualSimStandby;
		private int ExpireDaysInt;
		private boolean PackageIsSupport4G;
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

		public boolean isPackageIsSupport4G() {
			return PackageIsSupport4G;
		}

		public void setPackageIsSupport4G(boolean packageIsSupport4G) {
			PackageIsSupport4G = packageIsSupport4G;
		}

		public int getExpireDaysInt() {
			return ExpireDaysInt;
		}

		public void setExpireDaysInt(int expireDaysInt) {
			ExpireDaysInt = expireDaysInt;
		}

		public boolean isPackageIsCategoryKingCard() {
			return PackageIsCategoryKingCard;
		}

		public void setPackageIsCategoryKingCard(boolean packageIsCategoryKingCard) {
			PackageIsCategoryKingCard = packageIsCategoryKingCard;
		}

		public boolean isPackageIsCategoryDualSimStandby() {
			return PackageIsCategoryDualSimStandby;
		}

		public void setPackageIsCategoryDualSimStandby(boolean packageIsCategoryDualSimStandby) {
			PackageIsCategoryDualSimStandby = packageIsCategoryDualSimStandby;
		}

		public boolean isPackageIsCategoryCall() {
			return PackageIsCategoryCall;
		}

		public void setPackageIsCategoryCall(boolean packageIsCategoryCall) {
			PackageIsCategoryCall = packageIsCategoryCall;
		}

		public boolean isPackageIsCategoryFlow() {
			return PackageIsCategoryFlow;
		}

		public void setPackageIsCategoryFlow(boolean packageIsCategoryFlow) {
			PackageIsCategoryFlow = packageIsCategoryFlow;
		}

		private boolean PackageIsCategoryKingCard;


		public String getLogoPic() {
			return LogoPic;
		}

		public void setLogoPic(String LogoPic) {
			this.LogoPic = LogoPic;
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

		public int getPackageCategory() {
			return PackageCategory;
		}

		public void setPackageCategory(int packageCategory) {
			PackageCategory = packageCategory;
		}

		@Override
		public String toString() {
			return "ListBean{" +
					"OrderID='" + OrderID + '\'' +
					", OrderNum='" + OrderNum + '\'' +
					", UserId='" + UserId + '\'' +
					", PackageName='" + PackageName + '\'' +
					", Flow=" + Flow +
					", Quantity=" + Quantity +
					", UnitPrice=" + UnitPrice +
					", TotalPrice=" + TotalPrice +
					", ExpireDays=" + ExpireDays +
					", OrderDate='" + OrderDate + '\'' +
					", PayDate=" + PayDate +
					", PayStatus=" + PayStatus +
					", OrderStatus=" + OrderStatus +
					", RemainingCallMinutes=" + RemainingCallMinutes +
					", EffectiveDate='" + EffectiveDate + '\'' +
					", ActivationDate=" + ActivationDate +
					", PayUserAmount=" + PayUserAmount +
					", LogoPic='" + LogoPic + '\'' +
					", IsPayUserAmount=" + IsPayUserAmount +
					'}';
		}

		public String getFlow() {
			return Flow;
		}

		public void setFlow(String Flow) {
			this.Flow = Flow;
		}

		public int getQuantity() {
			return Quantity;
		}

		public void setQuantity(int Quantity) {
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

		public String getOrderDate() {
			return OrderDate;
		}

		public void setOrderDate(String OrderDate) {
			this.OrderDate = OrderDate;
		}

		public Object getPayDate() {
			return PayDate;
		}

		public void setPayDate(Object PayDate) {
			this.PayDate = PayDate;
		}

		public int getPayStatus() {
			return PayStatus;
		}

		public void setPayStatus(int PayStatus) {
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

		public Object getActivationDate() {
			return ActivationDate;
		}

		public void setActivationDate(Object ActivationDate) {
			this.ActivationDate = ActivationDate;
		}

		public int getPayUserAmount() {
			return PayUserAmount;
		}

		public void setPayUserAmount(int PayUserAmount) {
			this.PayUserAmount = PayUserAmount;
		}

		public int getIsPayUserAmount() {
			return IsPayUserAmount;
		}

		public void setIsPayUserAmount(int IsPayUserAmount) {
			this.IsPayUserAmount = IsPayUserAmount;
		}

		public String getPackageId() {
			return PackageId;
		}

		public void setPackageId(String packageId) {
			PackageId = packageId;
		}
	}

}
