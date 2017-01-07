package de.blinkt.openvpn.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/6.
 */

public class SportStepEntity implements Serializable {
	private ArrayList<Integer> ToDays;
	private ArrayList<Integer> YesterDays;
	private ArrayList<Integer> BeforeYesterDays;
	private ArrayList<Integer> HistoryDays;

	public SportStepEntity(ArrayList<Integer> toDays, ArrayList<Integer> yesterDays, ArrayList<Integer> beforeYesterDays, ArrayList<Integer> historyDays) {
		ToDays = toDays;
		YesterDays = yesterDays;
		BeforeYesterDays = beforeYesterDays;
		HistoryDays = historyDays;
	}

	public SportStepEntity() {
	}

	public ArrayList<Integer> getTodayList() {
		return ToDays;
	}

	public void setTodayList(ArrayList<Integer> ToDays) {
		this.ToDays = ToDays;
	}

	public ArrayList<Integer> getYesterdayList() {
		return YesterDays;
	}

	public void setYesterdayList(ArrayList<Integer> YesterDays) {
		this.YesterDays = YesterDays;
	}

	public ArrayList<Integer> getBeforeyesterdayList() {
		return BeforeYesterDays;
	}

	public void setBeforeyesterdayList(ArrayList<Integer> BeforeYesterDays) {
		this.BeforeYesterDays = BeforeYesterDays;
	}

	public ArrayList<Integer> getSixDayList() {
		return HistoryDays;
	}

	public void setSixDayList(ArrayList<Integer> HistoryDays) {
		this.HistoryDays = HistoryDays;
	}

	@Override
	public String toString() {
		return "SportStepEntity{" +
				"ToDays=" + ToDays +
				", YesterDays=" + YesterDays +
				", BeforeYesterDays=" + BeforeYesterDays +
				", HistoryDays=" + HistoryDays +
				'}';
	}
}
