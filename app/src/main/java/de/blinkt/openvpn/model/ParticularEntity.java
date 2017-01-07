package de.blinkt.openvpn.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/9/23.
 */

public class ParticularEntity implements Serializable {

	private int totalRows;
	/**
	 * ID : ce744540-c8fe-4a79-be44-219f98c748d6
	 * Amount : 0.01
	 * UserAmount : 0.07
	 * BillType : 1
	 * Descr : null
	 * PayType : 0
	 * PayTips : 在线充值
	 * CreateDate : 1474542216
	 */

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
		private String ID;
		private String Amount;
		private String UserAmount;
		private int BillType;
		private String Descr;
		private int PayType;
		private String PayTips;
		private long CreateDate;

		public String getID() {
			return ID;
		}

		public void setID(String ID) {
			this.ID = ID;
		}

		public String getAmount() {
			return Amount;
		}

		public void setAmount(String Amount) {
			this.Amount = Amount;
		}

		public String getUserAmount() {
			return UserAmount;
		}

		public void setUserAmount(String UserAmount) {
			this.UserAmount = UserAmount;
		}

		public int getBillType() {
			return BillType;
		}

		public void setBillType(int BillType) {
			this.BillType = BillType;
		}

		public String getDescr() {
			return Descr;
		}

		public void setDescr(String Descr) {
			this.Descr = Descr;
		}

		public int getPayType() {
			return PayType;
		}

		public void setPayType(int PayType) {
			this.PayType = PayType;
		}

		public String getPayTips() {
			return PayTips;
		}

		public void setPayTips(String PayTips) {
			this.PayTips = PayTips;
		}

		public long getCreateDate() {
			return CreateDate;
		}

		public void setCreateDate(long CreateDate) {
			this.CreateDate = CreateDate;
		}
	}
}
