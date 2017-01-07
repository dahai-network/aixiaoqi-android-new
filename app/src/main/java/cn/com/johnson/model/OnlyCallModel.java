package cn.com.johnson.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/1.
 */

public class OnlyCallModel  implements  Serializable{
	private String maximumPhoneCallTime;
	public String getMaximumPhoneCallTime() {
		return maximumPhoneCallTime;
	}

	public void setMaximumPhoneCallTime(String maximumPhoneCallTime) {
		this.maximumPhoneCallTime = maximumPhoneCallTime;
	}
}
