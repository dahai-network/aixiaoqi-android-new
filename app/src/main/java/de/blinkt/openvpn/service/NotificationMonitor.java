package de.blinkt.openvpn.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by wzj on 2016/10/23.
 */

public class NotificationMonitor extends NotificationListenerService {
	private static String TAG = "NotificationMonitor";
	private  static boolean isQQCalling = true;
	private static boolean isWeixinCalling = true;
	private static boolean isIncome = true;
	private static boolean isHangup = true;
	private static boolean isComeMessage = true;
	private static boolean isBeginTimer = false;
	private static boolean isRepeat = true;
	private  static SharedUtils utils = SharedUtils.getInstance();
	private  Timer refreshTimer = new Timer(true);
	private  TimerTask task = new TimerTask() {
		@Override
		public void run() {
			isQQCalling = true;
			isWeixinCalling = true;
			isComeMessage = true;
			isIncome = true;
			isHangup = true;
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		if (!isBeginTimer) {
			refreshTimer.schedule(task, 100, 5000);
			isBeginTimer = true;
		}
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		Log.i(TAG, "获取到通知：" + sbn.getPackageName());
		Uri uri = Uri.parse("smsto:");
		Intent infoIntent = new Intent(Intent.ACTION_SENDTO, uri);
		ResolveInfo res = getPackageManager().resolveActivity(infoIntent, PackageManager.MATCH_DEFAULT_ONLY);
		if (res != null) {
			Log.i(TAG, "获取到通知 包名：" + res.activityInfo.packageName);
			if (sbn.getPackageName().equals(res.activityInfo.packageName) && utils.readInt(Constant.MESSAGE_REMIND) == 1) {
				if (isComeMessage) {
					SendCommandToBluetooth.sendMessageToBlueTooth(Constant.MESSAGE_PUSH);//发送给手环短信通知
					isComeMessage = false;
				}
			} else if (sbn.getPackageName().equals("com.tencent.mm") && utils.readInt(Constant.WEIXIN_REMIND) == 1)//如果是微信
			{
				if (isWeixinCalling) {
					SendCommandToBluetooth.sendMessageToBlueTooth(Constant.WEIXIN_PUSH);
					isWeixinCalling = false;
				}
			} else if (sbn.getPackageName().equals("com.tencent.mobileqq") && utils.readInt(Constant.QQ_REMIND) == 1) {
				if (isQQCalling) {
					SendCommandToBluetooth.sendMessageToBlueTooth(Constant.QQ_PUSH);
					isQQCalling = false;
				}
			}
		}
	}



	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.i(TAG, "通知删除：" + sbn.getPackageName());
		Uri uri = Uri.parse("smsto:");
		Intent infoIntent = new Intent(Intent.ACTION_SENDTO, uri);
		ResolveInfo res = getPackageManager().resolveActivity(infoIntent, PackageManager.MATCH_DEFAULT_ONLY);
		if (res != null) {
			Log.i(TAG, "通知删除 包名：" + res.activityInfo.packageName);
		}
	}

	public static class PhoneBroadcastReceiver extends BroadcastReceiver {
		public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

		@Override
		public void onReceive(Context context, Intent intent) {
			// 如果是拨打电话
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
				Log.i(TAG, "call OUT:" + phoneNumber);

			} else if (SMS_RECEIVED.equals(intent.getAction()) && utils.readInt(Constant.MESSAGE_REMIND) == 1) {
				if (isComeMessage) {
					Log.i(TAG, "收到短信");
					Toast.makeText(context, "收到短信", Toast.LENGTH_LONG).show();
					SendCommandToBluetooth.sendMessageToBlueTooth(Constant.MESSAGE_PUSH);//发送给手环短信通知
					isComeMessage = false;
				}
			} else {
				// 如果是来电
				TelephonyManager tManager = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);
				tManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
			}
		}

		PhoneStateListener listener = new PhoneStateListener() {

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				// TODO Auto-generated method stub
				//state 当前状态 incomingNumber,貌似没有去电的API
				super.onCallStateChanged(state, incomingNumber);
				switch (state) {
					case TelephonyManager.CALL_STATE_IDLE:
						if (isHangup && utils.readInt(Constant.COMING_TEL_REMIND) == 1) {
							System.out.println("挂断");
							SendCommandToBluetooth.sendMessageToBlueTooth(Constant.HANG_UP_PUSH);//发送给手环挂断通知
							isHangup = false;
							isRepeat = true;
						}
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:
						System.out.println("接听");
//						SendCommandToBluetooth.sendMessageToBlueTooth(Constant.COMING_TEL_PUSH);//发送给手环接听通知
						isRepeat = true;
						break;
					case TelephonyManager.CALL_STATE_RINGING:
						if (isIncome && utils.readInt(Constant.COMING_TEL_REMIND) == 1 && isRepeat) {
							System.out.println("响铃:来电号码" + incomingNumber);
							SendCommandToBluetooth.sendMessageToBlueTooth(Constant.COMING_TEL_PUSH);//发送给手环来电通知
							isIncome = false;
							isRepeat = false;
						}
						break;
				}
			}

		};
	}
}
