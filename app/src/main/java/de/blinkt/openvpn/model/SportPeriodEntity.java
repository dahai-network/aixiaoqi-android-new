package de.blinkt.openvpn.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/3 0003.
 */

public class SportPeriodEntity implements Serializable {
	private String TotalStepNum;

	public List<TimePeriodsEntity> getTimePeriods() {
		return TimePeriods;
	}

	public void setTimePeriods(List<TimePeriodsEntity> timePeriods) {
		TimePeriods = timePeriods;
	}

	private List<TimePeriodsEntity> TimePeriods;


	public String getTotalStepNum() {
		return TotalStepNum;
	}

	public void setTotalStepNum(String totalStepNum) {
		TotalStepNum = totalStepNum;
	}
}
