package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/10 0010.
 */

public class OrderDataEntity implements Serializable {
    private String OrderID;
    private String Data;


	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		OrderID = orderID;
	}

	public String getData() {
		return Data;
	}

	public void setData(String data) {
		Data = data;
	}
}
