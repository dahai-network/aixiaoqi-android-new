package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/22.
 */

public class RechargeEntity implements Serializable {
	/**
	 * PaymentNum : 9022201609221611301956642
	 * Amount : 1
	 * OrderDate : 1474531890
	 */

	private PaymentBean payment;

	public PaymentBean getPayment() {
		return payment;
	}

	public void setPayment(PaymentBean payment) {
		this.payment = payment;
	}

	public static class PaymentBean implements Serializable{
		private String PaymentNum;
		private float Amount;
		private String OrderDate;

		public String getPaymentNum() {
			return PaymentNum;
		}

		public void setPaymentNum(String PaymentNum) {
			this.PaymentNum = PaymentNum;
		}

		public float getAmount() {
			return Amount;
		}

		public void setAmount(float Amount) {
			this.Amount = Amount;
		}

		public String getOrderDate() {
			return OrderDate;
		}

		public void setOrderDate(String OrderDate) {
			this.OrderDate = OrderDate;
		}

		@Override
		public String toString() {
			return "PaymentBean{" +
					"PaymentNum='" + PaymentNum + '\'' +
					", Amount=" + Amount +
					", OrderDate='" + OrderDate + '\'' +
					'}';
		}
	}

	@Override
	public String toString() {
		return "RechargeEntity{" +
				"payment=" + payment +
				'}';
	}
}
