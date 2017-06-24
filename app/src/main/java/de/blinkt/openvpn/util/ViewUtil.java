package de.blinkt.openvpn.util;

import android.app.Activity;
import android.text.Html;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class ViewUtil {

	public static void showView(View view) {
		if (null == view) {
			return;
		}

		if (View.VISIBLE != view.getVisibility()) {
			view.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * hide soft keyboard
	 *
	 * @param activity
	 */
	public static void hideSoftKeyboard(Activity activity) {
		if (null == activity) {
			return;
		}
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (null != inputMethodManager) {
			View view = activity.getCurrentFocus();
			if (null != view) {
				inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}

		}
	}

}
