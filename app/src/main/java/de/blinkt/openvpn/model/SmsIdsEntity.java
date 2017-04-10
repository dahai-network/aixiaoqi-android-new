package de.blinkt.openvpn.model;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/7.
 */

public class SmsIdsEntity {
	private ArrayList<String> ids = new ArrayList<>();
	private ArrayList<String> tels = new ArrayList<>();

	public SmsIdsEntity(ArrayList<String> tels, ArrayList<String> ids) {
		this.tels = tels;
		this.ids = ids;
	}

	public ArrayList<String> getTels() {
		return tels;
	}

	public void setTels(ArrayList<String> tels) {
		this.tels = tels;
	}

	public ArrayList<String> getIds() {
		return ids;
	}

	public void setIds(ArrayList<String> ids) {
		this.ids = ids;
	}
}
