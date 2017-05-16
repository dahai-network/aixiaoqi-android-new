package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/18.
 */
public class PacketDtailEntity implements Serializable {

	/**
	 * PackageName : 1分钱测试套餐
	 * PackageNum : p002
	 * Operators : 中国电信
	 * Price : 0.01
	 * Flow : 102400
	 * Desction : null
	 * Pic : http://image4.ali168.com/Unitoys/2016/01/1601191537071787109.jpg
	 * ExpireDays : 30
	 * Features : 产品特色
	 * 便宜：数据流量由境外运营商提供，本地化资费，无需开通本地漫游
	 * 方便：一个手环游遍全球100多个国家，无需换卡
	 * 安全：先付后用，费用一手掌握
	 * 双卡：在国外免费用国内手机拨打接听电话，收发短信，无漫游
	 * Details : 套餐详情
	 * 激活时间：最晚激活为2016年11月31日，否则自动失效
	 * 生效时间：套餐从激活日（当地时间）开始计算，24小时有效
	 * 适用客户：中国移动，联通，电信客户（无需开通漫游）
	 * 支持网络：当地3G，4G
	 * 修改激活时间：如果已经设定好了激活时间，却出现行程有变的情况，在之前设定的激活日之前可以修改激活时间
	 */

	private ListBean list;

	public ListBean getList() {
		return list;
	}

	public void setList(ListBean list) {
		this.list = list;
	}

	public static class ListBean implements Serializable {
		private String PackageId;
		private String PackageName;
		private String PackageNum;
		private String Operators;
		private double Price;
		private String Flow;
		private Object Desction;
		private String Pic;
		private String UseDescr;
		private String LogoPic;
		private int ExpireDays;  //有效期
		private String Features;
		private String Details;
		private String ApnName;
		private String DescTitlePic;
		private String DescPic;
		private String OriginalPrice;

		public String getDescTitlePic() {
			return DescTitlePic;
		}

		public void setDescTitlePic(String descTitlePic) {
			DescTitlePic = descTitlePic;
		}

		public String getDescPic() {
			return DescPic;
		}

		public void setDescPic(String descPic) {
			DescPic = descPic;
		}

		public String getOriginalPrice() {
			return OriginalPrice;
		}

		public void setOriginalPrice(String originalPrice) {
			OriginalPrice = originalPrice;
		}

		public String getApnName() {
			return ApnName;
		}

		public void setApnName(String apnName) {
			ApnName = apnName;
		}

		public String getUseDescr() {
			return UseDescr;
		}

		public void setUseDescr(String useDescr) {
			UseDescr = useDescr;
		}

		public String getLogoPic() {
			return LogoPic;
		}

		public void setLogoPic(String logoPic) {
			LogoPic = logoPic;
		}

		public String getPackageId() {
			return PackageId;
		}

		public void setPackageId(String packageId) {
			PackageId = packageId;
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

		public double getPrice() {
			return Price;
		}

		public void setPrice(double Price) {
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

		public String getPic() {
			return Pic;
		}

		public void setPic(String Pic) {
			this.Pic = Pic;
		}

		public int getExpireDays() {
			return ExpireDays;
		}

		public void setExpireDays(int ExpireDays) {
			this.ExpireDays = ExpireDays;
		}

		public String getFeatures() {
			return Features;
		}

		public void setFeatures(String Features) {
			this.Features = Features;
		}

		public String getDetails() {
			return Details;
		}

		public void setDetails(String Details) {
			this.Details = Details;
		}

		@Override
		public String toString() {
			return "ListBean{" +
					"PackageId='" + PackageId + '\'' +
					", PackageName='" + PackageName + '\'' +
					", PackageNum='" + PackageNum + '\'' +
					", Operators='" + Operators + '\'' +
					", Price=" + Price +
					", Flow='" + Flow + '\'' +
					", Desction=" + Desction +
					", Pic='" + Pic + '\'' +
					", LogoPic='" + LogoPic + '\'' +
					", ExpireDays=" + ExpireDays +
					", Features='" + Features + '\'' +
					", Details='" + Details + '\'' +
					'}';
		}
	}

	@Override
	public String toString() {
		return "PacketDtailEntity{" +
				"list=" + list +
				'}';
	}
}
