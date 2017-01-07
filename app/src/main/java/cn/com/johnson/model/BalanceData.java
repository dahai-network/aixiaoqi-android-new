package cn.com.johnson.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/6.
 */
public class BalanceData implements Serializable {
	/**
	 * status : 1
	 * data : {"amount":0}
	 */

	private int status;
	/**
	 * amount : 0
	 */

	private DataBean data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public static class DataBean {
		private float amount;

		public float getAmount() {
			return amount;
		}

		public void setAmount(float amount) {
			this.amount = amount;
		}
	}
}
