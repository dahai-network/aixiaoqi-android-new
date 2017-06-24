package de.blinkt.openvpn.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class CommonTools {

    /**
     * 长显示Toast消息
     *
     * @param context
     * @param message
     */
    public static void showShortToast(Context context, String message) {
        Log.d("CommonTools", "showShortToast: " + message + "\ncontext=" + context);
        if (!TextUtils.isEmpty(message) && context != null) {
            ToastCompat.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
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


    public static long lastClickTime;

    public static boolean isFastDoubleClick(int maxTime) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        Log.i("timetest", "time:" + time + ",timeD:" + timeD);
        lastClickTime = time;
        if (0 < timeD && timeD < maxTime) {
            return true;
        }
        return false;
    }

    public static void clearLastClickTime() {
        if (lastClickTime != 0)
            lastClickTime = 0;
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

    public static void delayTime(int time) {
        try {

            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    public static String getBLETime() {
        String bleTime;
        Calendar calendar = Calendar.getInstance();
        //年
        int year = calendar.get(Calendar.YEAR);
        year = year - 2000;
        //月
        int mouth = calendar.get(Calendar.MONTH);
        mouth++;
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //周
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1) {
            week = 7;
        } else {
            week--;
        }
        //时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //分
        int minute = calendar.get(Calendar.MINUTE);
        //秒
        int second = calendar.get(Calendar.SECOND);


        bleTime = "8880090500" + addZero(toHex(year)) + addZero(toHex(mouth)) + addZero(toHex(day))
                + addZero(toHex(hour)) + addZero(toHex(minute)) + addZero(toHex(second)) + addZero("" + week);
        return bleTime;
    }

    private static String toHex(int num) {
        return Integer.toHexString(num);
    }

    //为发送的数据添加0，如果小于15
    private static String addZero(String date) {
        date = date.toUpperCase();
        if (date.length() <= 1) {
            return "0" + date;
        } else {
            return "" + date;
        }
    }
}
