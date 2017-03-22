package de.blinkt.openvpn.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/11.
 */

public class SimRegisterStatue implements Serializable {

	//Goip注册失败类型
	private int rigsterSimStatue;
	private int progressCount;

	public int getRigsterSimStatue() {
		return rigsterSimStatue;
	}

	public void setRigsterSimStatue(int rigsterSimStatue) {
		this.rigsterSimStatue = rigsterSimStatue;
	}

	public int getProgressCount() {
		return progressCount;
	}

	public void setProgressCount(int progressCount) {
		this.progressCount = progressCount;
	}
}
