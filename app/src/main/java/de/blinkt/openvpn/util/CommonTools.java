package de.blinkt.openvpn.util;

import java.util.ArrayList;


import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.widget.Toast;

public class CommonTools {

	/**
	 * 长显示Toast消息
	 *
	 * @param context
	 * @param message
	 */
	public static void showShortToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 根据手机分辨率从dp转成px
	 *
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dpValue * scale + 0.5f);
	}


	public static float sp2px(Resources resources, float sp) {
		final float scale = resources.getDisplayMetrics().scaledDensity;
		return sp * scale;
	}

	public static float dp2px(Resources resources, float dp) {
		float v = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
		return (int) (v + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f) - 15;
	}

	/**
	 * 获取手机状态栏高度
	 *
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c ;
		Object obj;
		java.lang.reflect.Field field ;
		int x ;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	private static long lastClickTime;

	public static boolean isFastDoubleClick(int maxTime) {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < maxTime) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 获取屏幕宽度
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		float scale = context.getResources().getDisplayMetrics().widthPixels;
		return (int) scale;
	}

	/**
	 * 获取屏幕高度
	 *
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		float scale = context.getResources().getDisplayMetrics().heightPixels;
		return (int) scale;
	}

	public static boolean isNotBlank(ArrayList<Object> list) {
		return null != list && list.size() > 0;
	}

	public static  void delayTime(int time) {
		try {

			Thread.sleep(time);
		}catch (InterruptedException e){

		}
	}
}
