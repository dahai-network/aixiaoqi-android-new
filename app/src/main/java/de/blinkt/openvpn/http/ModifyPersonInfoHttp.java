package de.blinkt.openvpn.http;

import android.text.TextUtils;

import java.util.HashMap;

import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/21 0021.
 */
public class ModifyPersonInfoHttp extends CommonHttp {


	private InterfaceCallback interfaceCallback;
	private String nickName;
	private String sex;
	private String birthday;
	private String height;
	private String weight;
	private String movingTarget;

	public void setSex(String sex, int cmdType_) {
		this.sex = sex;
		this.cmdType_ = cmdType_;
	}

	public void setNickName(String nickName, int cmdType_) {
		this.nickName = nickName;
		this.cmdType_ = cmdType_;
	}

	public void setBirthday(String birthday, int cmdType_) {
		this.birthday = birthday;
		this.cmdType_ = cmdType_;
	}

	public void setHeight(String height, int cmdType_) {
		this.height = height;
		this.cmdType_ = cmdType_;
	}

	public void setWeight(String weight, int cmdType_) {
		this.weight = weight;
		this.cmdType_ = cmdType_;
	}

	public void setMovingTarget(String movingTarget, int cmdType_) {
		this.movingTarget = movingTarget;
		this.cmdType_ = cmdType_;
	}


	public ModifyPersonInfoHttp(InterfaceCallback interfaceCallback) {
		this.interfaceCallback = interfaceCallback;
	}


	@Override
	protected void BuildParams() throws Exception {
		slaverDomain_ = HttpConfigUrl.POST_MODIFY_INFO;
		if (params == null) {
			params = new HashMap<>();
		}
		if (!TextUtils.isEmpty(nickName)) {
			params.put("nickName", nickName);
		}
		if (!TextUtils.isEmpty(sex)) {
			params.put("sex", sex);
		}
		if (!TextUtils.isEmpty(birthday)) {
			params.put("Birthday", birthday);
		}
		if (!TextUtils.isEmpty(height)) {
			params.put("height", height);
		}
		if (!TextUtils.isEmpty(weight)) {
			params.put("weight", weight);
		}
		if (!TextUtils.isEmpty(movingTarget)) {
			params.put("movingTarget", movingTarget);
		}

	}

	@Override
	protected void parseResult(String response) {
		interfaceCallback.rightComplete(cmdType_, this);
	}

	@Override
	protected void errorResult(String s) {

	}

	@Override
	protected void noNet() {
		interfaceCallback.noNet();
	}

}
