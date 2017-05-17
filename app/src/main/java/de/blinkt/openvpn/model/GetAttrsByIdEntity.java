package de.blinkt.openvpn.model;

import java.util.List;

/**
 * Created by Administrator on 2017/5/16.
 */

public class GetAttrsByIdEntity {


	private List<ListBean> list;


	public List<ListBean> getList() {
		return list;
	}

	public void setList(List<ListBean> list) {
		this.list = list;
	}

	public static class ListBean {
		/**
		 * ID : 2b5fed74-2854-41e6-bb5c-a5fe4277ce7a
		 * CallMinutes : 0
		 * ExpireDays : 1
		 * Flow :
		 * CallMinutesDescr : 无限通话
		 * ExpireDaysDescr : 1个月
		 * FlowDescr :
		 * Price : 1.00
		 * OriginalPrice : 10.00
		 */

		private String ID;
		private String CallMinutes;
		private String ExpireDays;
		private String Flow;
		private String CallMinutesDescr;
		private String ExpireDaysDescr;
		private String FlowDescr;
		private String Price;
		private String OriginalPrice;

		public String getID() {
			return ID;
		}

		public void setID(String ID) {
			this.ID = ID;
		}

		public String getCallMinutes() {
			return CallMinutes;
		}

		public void setCallMinutes(String CallMinutes) {
			this.CallMinutes = CallMinutes;
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

		public String getCallMinutesDescr() {
			return CallMinutesDescr;
		}

		public void setCallMinutesDescr(String CallMinutesDescr) {
			this.CallMinutesDescr = CallMinutesDescr;
		}

		public String getExpireDaysDescr() {
			return ExpireDaysDescr;
		}

		public void setExpireDaysDescr(String ExpireDaysDescr) {
			this.ExpireDaysDescr = ExpireDaysDescr;
		}

		public String getFlowDescr() {
			return FlowDescr;
		}

		public void setFlowDescr(String FlowDescr) {
			this.FlowDescr = FlowDescr;
		}

		public String getPrice() {
			return Price;
		}

		public void setPrice(String Price) {
			this.Price = Price;
		}

		public String getOriginalPrice() {
			return OriginalPrice;
		}

		public void setOriginalPrice(String OriginalPrice) {
			this.OriginalPrice = OriginalPrice;
		}

		@Override
		public String toString() {
			return "GetAttrsByIdEntity{" +
					"ID='" + ID + '\'' +
					", CallMinutes='" + CallMinutes + '\'' +
					", ExpireDays='" + ExpireDays + '\'' +
					", Flow='" + Flow + '\'' +
					", CallMinutesDescr='" + CallMinutesDescr + '\'' +
					", ExpireDaysDescr='" + ExpireDaysDescr + '\'' +
					", FlowDescr='" + FlowDescr + '\'' +
					", Price='" + Price + '\'' +
					", OriginalPrice='" + OriginalPrice + '\'' +
					'}';
		}
	}
}
