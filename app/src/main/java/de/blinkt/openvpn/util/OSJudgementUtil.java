package de.blinkt.openvpn.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public final class OSJudgementUtil {


	public static boolean mIsJudgementBefore = false;

	public static boolean mIsMIUIOS = false;

	/**
	 * 判断是否是MIUI系统
	 *
	 * @return true 是小米平台
	 */
	public static boolean isMIUI() {
		try {
			if (!mIsJudgementBefore) {
				mIsJudgementBefore = true;
				Properties properties = new Properties();
				properties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));
				mIsMIUIOS = properties.getProperty("ro.miui.ui.version.code", null) != null ||
				            properties.getProperty("ro.miui.ui.version.name", null) != null ||
				            properties.getProperty("ro.miui.internal.storage", null) != null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mIsMIUIOS;
	}

}