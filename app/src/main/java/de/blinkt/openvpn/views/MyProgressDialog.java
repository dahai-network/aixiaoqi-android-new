package de.blinkt.openvpn.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;


/**
 * 自定义进度框
 *
 * @author wangxiaojie
 */
public class MyProgressDialog extends AlertDialog {

	private boolean state = false;

	private TextView mMessageView;

	private CharSequence mMessage;

	private Context mContext;

	private Handler handler;

	private int time;

	private String toastMsg;

	private ICSOpenVPNApplication app;

	public MyProgressDialog(Context context) {
		super(context);
		this.mContext = context;
	}

	public void setMyTouchOutside(boolean touchOutside) {
		this.setCanceledOnTouchOutside(touchOutside);
	}

	public void setMyCancelable(boolean cancelable) {
		this.setCancelable(cancelable);
	}

	public void setMyMessage(String message) {
		this.setMessage(message);
	}

	public MyProgressDialog(Context context, boolean cancelable,
							OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		this.mContext = context;
	}

	public MyProgressDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	public static MyProgressDialog show(Context context, CharSequence title,
										CharSequence message) {
		return show(context, title, message, null);
	}

	public static MyProgressDialog show(Context context, CharSequence title,
										CharSequence message, OnCancelListener cancleListener) {
		MyProgressDialog dialog = new MyProgressDialog(context);
		dialog.setMessage(message);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(cancleListener);
		dialog.show();
		return dialog;
	}

	public void setMyTiming(int time, String msg, ICSOpenVPNApplication app) {
		state = true;
		this.time = time;
		this.toastMsg = msg;
		this.app = app;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		View view = mInflater.inflate(R.layout.progress_dialog, null);
		ProgressBar mProgress = (ProgressBar) view.findViewById(android.R.id.progress);
		mMessageView = (TextView) view.findViewById(android.R.id.message);
		setContentView(view);
		if (mMessage != null) {
			setMessage(mMessage);
		}
		handler = new Handler() {

			public void handleMessage(Message msg) {
				if (state) {
					app.showMsg(toastMsg);
					mydismiss();
				}
			}
		};
	}

	public void mydismiss() {
		state = false;
		dismiss();
	}

	public void myShow() {
		this.show();
		if (state) {
			new Thread(new TimeThread(time)).start();
		}
	}

	public class TimeThread implements Runnable {

		int time;

		public TimeThread(int time) {
			this.time = time;
		}

		public void run() {
			try {
				for (int i = time; i > 0; i--) {
					Thread.sleep(1000);
				}
				Message msg = new Message();
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setMessage(CharSequence message) {
		if (mMessageView != null) {
			mMessageView.setText(message);
		} else {
			mMessage = message;
		}
	}
}
