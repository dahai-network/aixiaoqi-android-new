package de.blinkt.openvpn.core;

import android.content.Context;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by Administrator on 2016/6/9.
 */
public class CheckUtil {
	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobiles, Context context) {
	/*
	移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
		String telRegex = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if ("".equals(mobiles)) {
			CommonTools.showShortToast(context, context.getResources().getString(R.string.phone_not_null));
			return false;
		} else if (!mobiles.matches(telRegex)) {
			CommonTools.showShortToast(context, context.getResources().getString(R.string.phone_input_correct));
			return false;
		} else return true;
	}

	/**
	 * 验证密码长度
	 */
	public static boolean isPassWordNo(String password, Context context) {
		if ("".equals(password)) {
			CommonTools.showShortToast(context, context.getResources().getString(R.string.password_not_null));
			return false;
		}

		if (password.length() < 6) {
			CommonTools.showShortToast(context, context.getResources().getString(R.string.password_to_short));
			return false;
		}
		return true;
	}


}
