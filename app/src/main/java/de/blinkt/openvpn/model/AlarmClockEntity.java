package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class AlarmClockEntity implements Serializable {
	private String AlarmClockId;
	private String Time;
	private String Repeat;
	private String Tag;
	private String Status;
	private int position;
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}




	public String getAlarmClockId() {
		return AlarmClockId;
	}

	public void setAlarmClockId(String alarmClockId) {
		AlarmClockId = alarmClockId;
	}


	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getRepeat() {
		return Repeat;
	}

	public void setRepeat(String repeat) {
		Repeat = repeat;
	}

	public String getTag() {
		return Tag;
	}

	public void setTag(String tag) {
		Tag = tag;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	@Override
	public String toString() {
		return "AlarmClockEntity{" +
				"AlarmClockId='" + AlarmClockId + '\'' +
				", Time='" + Time + '\'' +
				", Repeat='" + Repeat + '\'' +
				", Tag='" + Tag + '\'' +
				", Status='" + Status + '\'' +
				'}';
	}
}
