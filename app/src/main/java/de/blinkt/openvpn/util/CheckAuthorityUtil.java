package de.blinkt.openvpn.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * Created by Administrator on 2017/5/19.
 */

public class CheckAuthorityUtil {
	private static final int PERMISSION_REQUEST_CODE = 1;
	private static Activity activity = null;

	/**
	 * 检查权限是否已请求到 (6.0)
	 */
	public static void checkPermissions(Activity checkActivity, String... permissions) {
		activity = checkActivity;
		// 版本兼容 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
				// 判断缺失哪些必要权限 
				&& lacksPermissions(permissions)) {
			// 如果缺失,则申请 
			requestPermissions(permissions);
		}
	}

	/**
	 * 判断是否缺失权限集合中的权限
	 */
	private static boolean lacksPermissions(String... permissions) {
		for (String permission : permissions) {
			if (lacksPermission(permission)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否缺少某个权限
	 */
	private static boolean lacksPermission(String permission) {
		return ContextCompat.checkSelfPermission(activity, permission) ==
				PackageManager.PERMISSION_DENIED;
	}

	/**
	 * 请求权限
	 */
	private static void requestPermissions(String... permissions) {
		ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE);
	}

	/**
	 * 启动应用的设置,进入手动配置权限页面
	 */
	private void startAppSettings() {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
		intent.setData(uri);
		activity.startActivity(intent);
	}
}
