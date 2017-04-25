package de.blinkt.openvpn.util;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class PhoneFormatUtil {

  public static   String deleteprefix(String type, String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        String phoneNumber;
        if (s.replace(type, "").startsWith("+86")) {
            phoneNumber = s.substring(3, s.length());
        } else if (s.replace(type, "").startsWith("86")) {
            phoneNumber = s.substring(2, s.length());
        } else {
            phoneNumber = s;
        }
        return phoneNumber;
    }
}
