package de.blinkt.openvpn.model;

import java.util.List;

/**
 * Created by Administrator on 2017/5/12.
 */

public class FreeWorryEntity {

	/**
	 * totalRows : 2
	 * list : [{"PackageId":"d1cc4026-af80-44bb-bc48-b9e9eca25226","PackageName":"通话时长免费领","PicHaveed":"http://img.unitoys.com/Unitoys/2017/05/1705121057076986544.png","Pic":"http://img.unitoys.com/Unitoys/2017/05/1705121100229635865.png","Haveed":false},{"PackageId":"576986b1-a06a-445f-b768-bab90831743e","PackageName":"省心服务","PicHaveed":"http://img.unitoys.com","Pic":"http://img.unitoys.com/Unitoys/2017/05/1705121100515415286.png","Haveed":false}]
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

	public static class ListBean {
		/**
		 * PackageId : d1cc4026-af80-44bb-bc48-b9e9eca25226
		 * PackageName : 通话时长免费领
		 * PicHaveed : http://img.unitoys.com/Unitoys/2017/05/1705121057076986544.png
		 * Pic : http://img.unitoys.com/Unitoys/2017/05/1705121100229635865.png
		 * Haveed : false
		 */

		private String PackageId;
		private String PackageName;
		private String PicHaveed;
		private String Pic;
		private String Category;
		private boolean Haveed;

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

		public String getPicHaveed() {
			return PicHaveed;
		}

		public void setPicHaveed(String PicHaveed) {
			this.PicHaveed = PicHaveed;
		}

		public String getPic() {
			return Pic;
		}

		public void setPic(String Pic) {
			this.Pic = Pic;
		}

		public String getCategory() {
			return Category;
		}

		public void setCategory(String category) {
			Category = category;
		}

		public boolean isHaveed() {
			return Haveed;
		}

		public void setHaveed(boolean Haveed) {
			this.Haveed = Haveed;
		}

	}
}
