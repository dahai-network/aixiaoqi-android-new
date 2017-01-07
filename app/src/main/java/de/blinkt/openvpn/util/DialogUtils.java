package de.blinkt.openvpn.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import cn.com.aixiaoqi.R;


public class DialogUtils {
	/**
	 * @param dialog           要显示的对话框
	 * @param mContext
	 * @param dialogPosition   设置dialog显示的位置
	 * @param widthMultiple    设置为屏幕宽度的倍数
	 * @param heightMultiple   设置为屏幕高度的倍数
	 * @param setWidthflag     如果为true，设置宽度,false,自适应宽度
	 * @param setHeightFlag    如果为true，设置高度,false,自适应高度
	 * @param addAnimationFlag 如果为true，添加动画,false,为不添加动画
	 */
	public static void dialogSet(Dialog dialog, Context mContext, int dialogPosition, double widthMultiple, double heightMultiple, boolean setWidthflag, boolean setHeightFlag, boolean addAnimationFlag) {
		WindowManager m = ((Activity) mContext).getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		if (setWidthflag) {
			params.width = (int) (d.getWidth() * widthMultiple);
		}// 宽度设置为屏幕的0.8
		if (setHeightFlag) {
			params.height = (int) (d.getHeight() * heightMultiple);
		}// 宽度设置为屏幕的0.8
		Window window = dialog.getWindow();
		window.setGravity(dialogPosition);
		if (addAnimationFlag) {// 此处可以设置dialog显示的位置
			window.setWindowAnimations(R.style.dialogstyle); // 添加动画
		}

	}

}
