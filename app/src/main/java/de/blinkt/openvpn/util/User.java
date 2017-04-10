package de.blinkt.openvpn.util;

import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.database.BlackListDBHelp;

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
	public static boolean isBlackList(String phoneNumber) {
	BlackListDBHelp blackListDBHelp=new BlackListDBHelp(ProMainActivity.instance);
		return blackListDBHelp.isBlackList(phoneNumber);
	}

}
