package de.blinkt.openvpn.util;

import de.blinkt.openvpn.constant.Constant;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class User {
	public static boolean isCurrentUser(String phoneNumber) {
		SharedUtils sharedUtils = SharedUtils.getInstance();
		return phoneNumber.equals(sharedUtils.readString(Constant.USER_NAME));
	}

	public static boolean isLogin(String phoneNumber) {
		SharedUtils sharedUtils = SharedUtils.getInstance();
		return phoneNumber.equals(sharedUtils.readString(Constant.USER_NAME));
	}
}
