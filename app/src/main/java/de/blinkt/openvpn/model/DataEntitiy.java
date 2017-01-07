package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/7 0007.
 */

public class DataEntitiy implements Serializable {
    public String getDate() {
        return Date;
    }


	public void setDate(String date) {
		Date = date;
	}

	private String Date;


}
