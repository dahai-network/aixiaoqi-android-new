package de.blinkt.openvpn.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/14.
 */
public class CountryPacketEntity implements Serializable {


	private String PackageId;
	private String PackageName;
	private String PackageNum;
	private String Operators;
	private float Price;
	private  String OriginalPrice;
	private String Flow;
	private String Pic;
	private int ExpireDays;

	public String getOriginalPrice() {
		return OriginalPrice;
	}

	public void setOriginalPrice(String originalPrice) {
		OriginalPrice = originalPrice;
	}

	public String getPackageId() {
		return PackageId;
	}

	public void setPackageId(String packageId) {
		PackageId = packageId;
	}
//	private Object Desction;
//	private Object Features;
//	private Object Details;
//	private String LogoPic;
//	private String CountryId;
//	private int Lock4;
//	private boolean IsDeleted;
//	private int CallMinutes;
//	private Object UT_Country;
//	private List<?> UT_Order;


	public String getPic() {
		return Pic;
	}

	public void setPic(String pic) {
		Pic = pic;
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

	public float getPrice() {
		return Price;
	}

	public void setPrice(float Price) {
		this.Price = Price;
	}

	public String getFlow() {
		return Flow;
	}

	public void setFlow(String Flow) {
		this.Flow = Flow;
	}

//	public Object getDesction() {
//		return Desction;
//	}
//
//	public void setDesction(Object Desction) {
//		this.Desction = Desction;
//	}
//
//	public Object getFeatures() {
//		return Features;
//	}
//
//	public void setFeatures(Object Features) {
//		this.Features = Features;
//	}
//
//	public Object getDetails() {
//		return Details;
//	}
//
//	public void setDetails(Object Details) {
//		this.Details = Details;
//	}
//
//	public String getLogoPic() {
//		return LogoPic;
//	}
//
//	public void setLogoPic(String LogoPic) {
//		this.LogoPic = LogoPic;
//	}

	public int getExpireDays() {
		return ExpireDays;
	}

	public void setExpireDays(int ExpireDays) {
		this.ExpireDays = ExpireDays;
	}

//	public String getCountryId() {
//		return CountryId;
//	}
//
//	public void setCountryId(String CountryId) {
//		this.CountryId = CountryId;
//	}

	public String getOperators() {
		return Operators;
	}

	public void setOperators(String Operators) {
		this.Operators = Operators;
	}

//	public int getLock4() {
//		return Lock4;
//	}
//
//	public void setLock4(int Lock4) {
//		this.Lock4 = Lock4;
//	}
//
//	public boolean isIsDeleted() {
//		return IsDeleted;
//	}
//
//	public void setIsDeleted(boolean IsDeleted) {
//		this.IsDeleted = IsDeleted;
//	}
//
//	public int getCallMinutes() {
//		return CallMinutes;
//	}
//
//	public void setCallMinutes(int CallMinutes) {
//		this.CallMinutes = CallMinutes;
//	}
//
//	public Object getUT_Country() {
//		return UT_Country;
//	}
//
//	public void setUT_Country(Object UT_Country) {
//		this.UT_Country = UT_Country;
//	}

	public String getID() {
		return PackageId;
	}

	public void setID(String PackageId) {
		this.PackageId = PackageId;
	}

//	public List<?> getUT_Order() {
//		return UT_Order;
//	}
//
//	public void setUT_Order(List<?> UT_Order) {
//		this.UT_Order = UT_Order;
//	}

	@Override
	public String toString() {
		return "DataBean{" +
				"PackageName='" + PackageName + '\'' +
				", PackageNum='" + PackageNum + '\'' +
				", Price=" + Price +
				", Flow=" + Flow +
				", ExpireDays=" + ExpireDays +
				", Operators='" + Operators + '\'' +
				", PackageId='" + PackageId + '\'' +
				'}';
	}
}
