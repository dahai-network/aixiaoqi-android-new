package de.blinkt.openvpn.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;

/**
 * Created by Administrator on 2016/10/13 0013.
 */
public class PhoneNumberZero {

	public static String getMobilePrefix(String number) {
		return number.substring(0, 3);
	}

	public static String getCenterNumber(String number) {
		return number.substring(3, 7);
	}


	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}


    public static boolean isZeroStarted(String number){
        return !(number == null || number.isEmpty())&&number.charAt(0) == '0';
    }


	/**
	 * 得到输入区号中的前三位数字或前四位数字去掉首位为零后的数字。
	 */
	public static String getAreaCodePrefix(String number) {
		if (number.charAt(1) == '1' || number.charAt(1) == '2')
			return number.substring(1, 3);
		if (number.length() <= 3) {
			return "";
		}
		return number.substring(1, 4);
	}


	/**
	 * 得到号码的长度
	 */
	public static int getNumLength(String number) {
		if (number == null || number.isEmpty())
			return 0;
		return number.length();
	}


	public static Map<String, String> getPhoneZero(DatabaseDAO dao, String phoneNumber) {
		String prefix, center;
		Map<String, String> map;
		if (!isNumeric(phoneNumber)) {
			map = new HashMap<>();
		} else {
			if (isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 2) {
				prefix = getAreaCodePrefix(phoneNumber);
				map = dao.queryAeraCode(prefix);
			} else if (!isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 6) {
				prefix = getMobilePrefix(phoneNumber);
				center = getCenterNumber(phoneNumber);
				map = dao.queryNumber(prefix, center);
			} else {
				map = new HashMap<>();
			}
		}
		return map;
	}

	public static String getAddress(DatabaseDAO dao, String phoneNumber) {
		String address;
		Map<String, String> map = getPhoneZero(dao, phoneNumber);
		String province = map.get("provinceName");
		String city = map.get("cityName");
		if (province == null || province.isEmpty())
			address = ICSOpenVPNApplication.getContext().getString(R.string.title_search_result_not_found);
		else if (province.equals(city))
			address = province;
		else
			address = province + "  " + city;
		return address;
	}

	public static String getPhoneNumberFormat(String phoneNumber) {
		String phonenum;
		if (isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 2) {
			phonenum = phoneNumber.substring(0, 4) + "-" + phoneNumber.substring(4, phoneNumber.length());
		} else if (!isZeroStarted(phoneNumber) && getNumLength(phoneNumber) > 6) {
			phonenum = phoneNumber.substring(0, 3) + "-" + phoneNumber.substring(3, 7) + "-" + phoneNumber.substring(7, phoneNumber.length());
		} else {
			phonenum = phoneNumber;
		}
		return phonenum;
	}

}
