package de.blinkt.openvpn.util;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.util.List;


public class NetworkUtils {

	/**
	 * 网络是否可用
	 *
	 * @param
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		if(context==null){
			return false;
		}
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				int length = info.length;
				for (int i = 0; i < length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}

		return false;
	}




	/**
	 * Gps是否打开
	 *
	 * @param context
	 * @return
	 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = ((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

	/**
	 *判断位置信息是否开启
	 * @param context
	 * @return
	 */
	public static boolean isLocationOpen(final Context context){
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//gps定位
		boolean isGpsProvider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		//网络定位
		boolean isNetWorkProvider = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		return isGpsProvider|| isNetWorkProvider;
	}

	/**
	 * wifi是否打开
	 */
	public static boolean isWifiEnabled(Context context) {
		ConnectivityManager mgrConn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mgrTel = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return ((mgrConn.getActiveNetworkInfo() != null && mgrConn
				.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel
				.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	}

	/**
	 * 判断当前网络是否是wifi网络
	 * if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) { //判断3G�?
	 *
	 * @param context
	 * @return boolean
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 判断当前网络是否�?3G网络
	 *
	 * @param context
	 * @return boolean
	 */
	public static boolean is3G(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE;
	}
}
