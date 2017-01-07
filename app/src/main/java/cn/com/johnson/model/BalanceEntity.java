package cn.com.johnson.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/6.
 */
public class BalanceEntity implements Serializable {

	private float amount;

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "BalanceData{" +
				"amount=" + amount +
				'}';
	}
}

