package de.blinkt.openvpn.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

import static de.blinkt.openvpn.fragments.ProMainTabFragment.ui.Fragment_Phone.PERMISSION_SET;

/**
 * Created by Administrator on 2017/4/28 0028.
 */

public class SetPermission {
	Context context;

	public SetPermission(Context context) {
		if (context == null) {
			return;
		}
		this.context = context;

		String name = Build.MANUFACTURER;

		if ("HUAWEI".equals(name)) {
			goHuaWeiMainager();
		} else if ("vivo".equals(name)) {
			goVivoMainager();
		} else if ("OPPO".equals(name)) {
			goOppoMainager();
		} else if ("Coolpad".equals(name)) {
			goCoolpadMainager();
		} else if ("Meizu".equals(name)) {
			goMeizuMainager();
			// getAppDetailSettingIntent();
		} else if ("Xiaomi".equals(name)) {
			goXiaoMiMainager();
		} else if ("samsung".equals(name)) {
			goSangXinMainager();
		} else {
			goIntentSetting();
		}
	}


	private void goHuaWeiMainager() {
		try {
			Intent intent = new Intent("demo.vincent.com.tiaozhuan", android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
			intent.setComponent(comp);
			jumpActivity(intent);
		} catch (Exception e) {
			CommonTools.showShortToast(context, "跳转失败");
			e.printStackTrace();
			goIntentSetting();
		}
	}

	private void jumpActivity(Intent intent) {
		((Activity) context).startActivityForResult(intent, PERMISSION_SET);
	}

	private void goXiaoMiMainager() {
		try {
			Intent localIntent = new Intent(
					"miui.intent.action.APP_PERM_EDITOR", android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			localIntent
					.setClassName("com.miui.securitycenter",
							"com.miui.permcenter.permissions.AppPermissionsEditorActivity");
			localIntent.putExtra("extra_pkgname", context.getPackageName());
			jumpActivity(localIntent);
		} catch (ActivityNotFoundException localActivityNotFoundException) {
			goIntentSetting();
		}
	}

	private void goMeizuMainager() {
		try {
			Intent intent = new Intent("com.meizu.safe.security", android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.putExtra("packageName", "xiang.settingpression");
			jumpActivity(intent);
		} catch (ActivityNotFoundException localActivityNotFoundException) {
			localActivityNotFoundException.printStackTrace();
			Log.e("goMeizuMainager", "localActivityNotFoundException.printStackTrace()");
			goIntentSetting();
		}
	}

	private void goSangXinMainager() {
		//三星4.3可以直接跳转
		goIntentSetting();
	}

	private void goIntentSetting() {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		Uri uri = Uri.fromParts("package", context.getPackageName(), null);
		intent.setData(uri);
		try {
			jumpActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void goOppoMainager() {
		doStartApplicationWithPackageName("com.coloros.safecenter");
	}

	/**
	 * doStartApplicationWithPackageName("com.yulong.android.security:remote")
	 * 和Intent open = getPackageManager().getLaunchIntentForPackage("com.yulong.android.security:remote");
	 * startActivity(open);
	 * 本质上没有什么区别，通过Intent open...打开比调用doStartApplicationWithPackageName方法更快，也是android本身提供的方法
	 */
	private void goCoolpadMainager() {
		doStartApplicationWithPackageName("com.yulong.android.security:remote");
	}

	//vivo
	private void goVivoMainager() {
		doStartApplicationWithPackageName("com.bairenkeji.icaller");
	}

	private void doStartApplicationWithPackageName(String packagename) {

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = context.getPackageManager().getPackageInfo(packagename, 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return;
		}
		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);
		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = context.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);
		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);
			intent.setComponent(cn);
			try {
				jumpActivity(intent);
			} catch (Exception e) {
				goIntentSetting();
				e.printStackTrace();
			}
		}
	}

}
