package de.blinkt.openvpn.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.blinkt.openvpn.model.TimeInfoEntity;

/*
　　* @author Msquirrel
　　*/
public class DateUtils {

	private static SimpleDateFormat sf;

	/*获取系统时间 格式为："yyyy/MM/dd "*/
	public static String getCurrentDate() {
		Date d = new Date();
		sf = new SimpleDateFormat("yyyy年MM月dd日",
				Locale.getDefault());
		return sf.format(d);
	}

	/*获取系统时间 格式为："yyyyMMdd "*/
	public static String getCurrentDateForFile() {
		Date d = new Date();
		sf = new SimpleDateFormat("yyyyMMdd",
				Locale.getDefault());
		return sf.format(d);
	}

	/*获取系统时间 格式为："yyyyMMdd "*/
	public static String getCurrentDateForFileDetail() {
		Date d = new Date();
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		return sf.format(d);
	}



	/*
	  提交订单处添加半年后（180天）的时间
	 */
	public static String getAdd180DayDate() {
		Date currentTime = new Date();
		long currentTimeLong = currentTime.getTime();
		long add180DayTimeLong = 1000L * 60 * 60 * 24 * 180;
		long afterTime = currentTimeLong + add180DayTimeLong;
		sf = new SimpleDateFormat("yyyy年MM月dd日",
				Locale.getDefault());
		return sf.format(afterTime);
	}

	/*时间戳转换成字符窜*/
	public static String getDateToString(long time) {
		String timeTemp = time + "";
		if (timeTemp.length() == 10) {
			time = time * 1000;
		}
		Date d = new Date(time);
		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		return sf.format(d);
	}

	/*将字符串转为时间戳*/
	public static long getStringToDate(String time) {

		sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		Date date = new Date();
		try {
			date = sf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static String DateToString(Date date, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat,
				Locale.getDefault());
		String result = null;
		try {
			result = sdf.format(date);
		} catch (android.net.ParseException e) {
			// e.printStackTrace();
		}

		return result;
	}

	public static Date stringToDate(String dateString) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (android.net.ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;

	}

	/*将天数转换成时间戳*/
	public static long getDateToDays(long days) {
		return days * 86400000;
	}

	public static String friendlyTime(Date time) {
		//获取time距离当前的秒数
		int ct = (int) ((System.currentTimeMillis() - time.getTime()) / 1000);

		if (ct == 0) {
			return "刚刚";
		}

		if (ct > 0 && ct < 60) {
			return ct + "秒前";
		}

		if (ct >= 60 && ct < 3600) {
			return Math.max(ct / 60, 1) + "分钟前";
		}
		if (ct >= 3600 && ct < 86400)
			return ct / 3600 + "小时前";
		if (ct >= 86400 && ct < 2592000) { //86400 * 30
			int day = ct / 86400;
			return day + "天前";
		}
		if (ct >= 2592000 && ct < 31104000) { //86400 * 30
			return ct / 2592000 + "月前";
		}
		return ct / 31104000 + "年前";
	}

	public static String friendlyTime(String time) {
		//获取time距离当前的秒数
		long smsTime = Long.parseLong(time);
		if (time.length() < 10) {
			return "";
		}
		int ct = 0;
		if (time.length() == 10) {
			ct = (int) ((System.currentTimeMillis() - smsTime * 1000) / 1000);
			smsTime = smsTime * 1000;
		} else if (time.length() == 13) {
			ct = (int) ((System.currentTimeMillis() - smsTime) / 1000);
		}

		if (ct < 0) {
			return getDateToString(smsTime);
		}
		if (ct < 86400)
			return getDateToString(smsTime).substring(11, 16);
		else if (ct < 2 * 86400) { //86400 * 30

			return "昨天";
		} else if (ct < 3 * 86400) {

			return "前天";
		} else { //86400 * 30
			return getDateToString(smsTime).substring(6, 11).replace("-", "月");
		}

	}

//getTimeStampString
	public static String getTimeStampString(String time) {
		String str ;
		long timeStamp = Long.parseLong(time);
		if (time.length() < 10) {
			return "";
		} else if (time.length() == 10) {
			timeStamp = timeStamp * 1000;
		}
		Calendar localCalendar = GregorianCalendar.getInstance();
		localCalendar.setTimeInMillis(timeStamp);
		int year = localCalendar.get(Calendar.YEAR);
		if (!isSameYear(year)) {
			//String paramDate2Str = new SimpleDateFormat("yyyy年MM月dd", Locale.getDefault()).format(new Date(timeStamp));
			String paramDate2Str = new SimpleDateFormat("yyyy年MM月dd", Locale.CHINA).format(new Date(timeStamp));
			return paramDate2Str;
		}

		if (isSameDay(timeStamp)) {
			str = "HH:mm";
		} else if (isYesterday(timeStamp)) {
			//return "昨天";
			str = "昨天 HH:mm";
		} else if (isBeforeYesterday(timeStamp)) {
			//return "前天";
			str = "前天 HH:mm";
		} else {
			//str = "M月d日";
			str = "M月d日HH:mm";
		}
		String paramDate2Str = new SimpleDateFormat(str, Locale.CHINA).format(new Date(timeStamp));
		return paramDate2Str;
	}
//getTimeStampDetailString
	public static String getTimeStampDetailString(String time) {
		String str ;
		long timeStamp = Long.parseLong(time);
		if (time.length() < 10) {
			return "";
		} else if (time.length() == 10) {
			timeStamp = timeStamp * 1000;
		}
		Calendar localCalendar = GregorianCalendar.getInstance();
		localCalendar.setTimeInMillis(timeStamp);
		int year = localCalendar.get(Calendar.YEAR);
		if (!isSameYear(year)) {
			String paramDate2Str = new SimpleDateFormat("yyyy年MM月dd", Locale.CHINA).format(new Date(timeStamp));
			return paramDate2Str;
		}

		if (isSameDay(timeStamp)) {
			str = "HH:mm";
		} else if (isYesterday(timeStamp)) {
			str = "昨天 HH:mm";
		} else if (isBeforeYesterday(timeStamp)) {
			str = "前天 HH:mm";
		} else {
			str = "M月d日HH:mm";
		}
		String paramDate2Str = new SimpleDateFormat(str, Locale.CHINA).format(new Date(timeStamp));
		return paramDate2Str;
	}

	public static TimeInfoEntity getTodayStartAndEndTime() {
		TimeInfoEntity timeInfo = getTimeInfoEntity(0);
		return timeInfo;
	}


	public static TimeInfoEntity getYesterdayStartAndEndTime() {
		TimeInfoEntity timeInfo = getTimeInfoEntity(-1);
		return timeInfo;
	}

	public static TimeInfoEntity getBeforeYesterdayStartAndEndTime() {
		TimeInfoEntity timeInfo = getTimeInfoEntity(-2);
		return timeInfo;
	}

	@NonNull
	private static TimeInfoEntity getTimeInfoEntity(int i) {
		Calendar localCalendar1 = Calendar.getInstance();
		localCalendar1.add(Calendar.DAY_OF_MONTH, i);
		localCalendar1.set(Calendar.HOUR_OF_DAY, 0);
		localCalendar1.set(Calendar.MINUTE, 0);
		localCalendar1.set(Calendar.SECOND, 0);
		localCalendar1.set(Calendar.MILLISECOND, 0);
		Date localDate1 = localCalendar1.getTime();
		long l1 = localDate1.getTime();

		Calendar localCalendar2 = Calendar.getInstance();
		localCalendar2.add(Calendar.DAY_OF_MONTH, i);
		localCalendar2.set(Calendar.HOUR_OF_DAY, 23);
		localCalendar2.set(Calendar.MINUTE, 59);
		localCalendar2.set(Calendar.SECOND, 59);
		localCalendar2.set(Calendar.MILLISECOND, 999);
		Date localDate2 = localCalendar2.getTime();
		long l2 = localDate2.getTime();
		TimeInfoEntity timeInfo = new TimeInfoEntity();
		timeInfo.setEndTime(l2);
		timeInfo.setStartTime(l1);
		return timeInfo;
	}

	private static boolean isSameYear(int year) {
		Calendar calendar = Calendar.getInstance();
		int CurYear = calendar.get(Calendar.YEAR);
		return CurYear == year;
	}

	private static boolean isSameDay(long paramLong) {
		TimeInfoEntity timeInfo = getTodayStartAndEndTime();
		return (paramLong > timeInfo.getStartTime()) && (paramLong < timeInfo.getEndTime());
	}

	private static boolean isYesterday(long paramLong) {
		TimeInfoEntity timeInfo = getYesterdayStartAndEndTime();
		if ((paramLong > timeInfo.getStartTime()) && (paramLong < timeInfo.getEndTime())) {
			Log.e("time", "paramLong==========" + paramLong + "\ngetStartTime==========" + timeInfo.getStartTime() + "\ngetEndTime===========" + timeInfo.getEndTime());
		}
		return (paramLong > timeInfo.getStartTime()) && (paramLong < timeInfo.getEndTime());
	}

	private static boolean isBeforeYesterday(long paramLong) {
		TimeInfoEntity timeInfo = getBeforeYesterdayStartAndEndTime();
		return (paramLong > timeInfo.getStartTime()) && (paramLong < timeInfo.getEndTime());
	}

}

