package de.blinkt.openvpn.views;

/**
 * Created by zhy on 15/9/21.
 */
public class StatusBarCompat {
//	private static final int INVALID_VAL = -1;
//	private static final int COLOR_DEFAULT = Color.parseColor("#20000000");
//
//	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
//	public static void compat(Activity activity, int statusColor) {
//
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			if (statusColor != INVALID_VAL) {
//				Window window = activity.getWindow();
//				//设置透明状态栏,这样才能让 ContentView 向上
//				window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//				//需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
//				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//				//设置状态栏颜色
//				window.setStatusBarColor(statusColor);
//
//				ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
//				View mChildView = mContentView.getChildAt(0);
//				if (mChildView != null) {
//					//注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
//					ViewCompat.setFitsSystemWindows(mChildView, false);
//				}
//
//			}
//			return;
//		}
//
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//			int color = COLOR_DEFAULT;
//			ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
//			if (statusColor != INVALID_VAL) {
//				color = statusColor;
//			}
//			View statusBarView = new View(activity);
//			ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//					getStatusBarHeight(activity));
//			statusBarView.setBackgroundColor(color);
//			contentView.addView(statusBarView, lp);
//		}
//
//	}
//
//	public static void compat(Activity activity) {
//		compat(activity, INVALID_VAL);
//	}
//
//
//	public static int getStatusBarHeight(Context context) {
//		int result = 0;
//		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
//		if (resourceId > 0) {
//			result = context.getResources().getDimensionPixelSize(resourceId);
//		}
//		return result;
//	}
}
