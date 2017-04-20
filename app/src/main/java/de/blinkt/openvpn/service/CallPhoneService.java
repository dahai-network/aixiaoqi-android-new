package de.blinkt.openvpn.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import cn.com.aixiaoqi.R;
import cn.com.johnson.model.SecurityConfig;
import cn.qfishphone.sipengine.SipEngineCore;
import cn.qfishphone.sipengine.SipEngineEventListener;
import cn.qfishphone.sipengine.SipEngineFactory;
import de.blinkt.openvpn.activities.ReceiveCallActivity;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.database.BlackListDBHelp;
import de.blinkt.openvpn.http.CheckTokenHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.SecurityConfigHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.PublicEncoderTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.User;
import de.blinkt.openvpn.util.querylocaldatebase.TipHelper;

/**
 * Created by Administrator on 2016/11/21 0021.
 */
public class CallPhoneService extends Service implements SipEngineEventListener, InterfaceCallback {
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static String endFlag = "endCall";
	public static String connectedFlag = "startCall";
	public static String callProcessing = "callProcessing";
	public static String waitConnected = "waitConnected";
	public static String reportFlag = "reportFlag";
	public static String CALL_FAIL = "callfail";
	private String TAG = "CallPhoneService";
	public SipEngineCore the_sipengineReceive;
	private Timer mTimerReceive = new Timer("51DTY scheduler");
	private SharedUtils sharedUtils;

	@Override
	public void onCreate() {
		super.onCreate();
		httpToken();
	}

	private void httpToken() {
		sharedUtils = SharedUtils.getInstance();
		CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_CHECKTOKEN);
	}

	private void registSipForReceive() {
		SharedUtils sharedUtils = SharedUtils.getInstance();
		String username = sharedUtils.readString(Constant.USER_NAME);
		String password = PublicEncoderTools.MD5Encode(PublicEncoderTools.MD5Encode(sharedUtils.readString(Constant.PUBLIC_PASSWORD) + "voipcc2015"));

		String server = sharedUtils.readString(Constant.ASTERISK_IP_OUT);
		Log.e(TAG, "username=" + username + ",password=" + password);
		System.out.println("pwd:" + sharedUtils.readString(Constant.PUBLIC_PASSWORD) + "|username:" + username + "|server:" + server + "|port:" + sharedUtils.readString(Constant.ASTERISK_IP_OUT));
		/*
		 * 设置rc4加密 并使用 65061 登陆
		 * 取消加密  并使用65060 登陆
		 * */
		Log.d(TAG, "registSipForReceive: "+sharedUtils.readString(Constant.ASTERISK_PORT_OUT));
		int port = Integer.parseInt(sharedUtils.readString(Constant.ASTERISK_PORT_OUT));
		int expire = 60;
		the_sipengineReceive = SipEngineFactory.instance().createPhoneCore(this, this);
		the_sipengineReceive.CoreInit();
		the_sipengineReceive.SetRC4Crypt(false);
		the_sipengineReceive.DeRegisterSipAccount();
		the_sipengineReceive.EnableDebug(true);
		the_sipengineReceive.RegisterSipAccount(username, password, server, port, expire);

		TimerTask lTask = new TimerTask() {
			@Override
			public void run() {
				the_sipengineReceive.CoreEventProgress();
			}
		};
		mTimerReceive.scheduleAtFixedRate(lTask, 0, 100);
		ICSOpenVPNApplication.the_sipengineReceive = the_sipengineReceive;
	}

	@Override
	public void OnSipEngineState(int code) {
		Log.e(TAG, "OnSipEngineState" + code);
	}

	@Override
	public void OnRegistrationState(int code, int error_code) {
		Log.e(TAG, "code=" + code + "  , errorcode=" + error_code);
	}

	public static int CALL_DIR = 0;

	@Override
	public void OnNewCall(int CallDir, final String peer_caller, boolean is_video_call) {
		Log.e(TAG, "新来电 CAllDir=" + CallDir+",peer_caller="+peer_caller);
		muteAudioFocus(this, true);
		if (CallDir != 0) {
			if(!User.isBlackList(peer_caller.substring(2,peer_caller.length()))) {
				CALL_DIR = 0;
				ReceiveCallActivity.launch(CallPhoneService.this, peer_caller);
				SendCommandToBluetooth.sendMessageToBlueTooth(Constant.COMING_TEL_PUSH);//发送给手环电话设备通知
			}else{
				ICSOpenVPNApplication.the_sipengineReceive.Hangup();
			}
		} else {
			CALL_DIR = 1;
		}


	}

	@Override
	public void OnCallProcessing() {
		Intent intent = new Intent();
		intent.setAction(callProcessing);
		sendBroadcast(intent);
		Log.e(TAG, "正在呼叫");
	}

	@Override
	public void OnCallRinging() {
		Log.e(TAG, "正在响铃");
	}

	@Override
	public void OnCallConnected() {
		Log.e(TAG, "呼叫接通");
		TipHelper.stopSound();
		TipHelper.stopShock();
		Intent intent = new Intent();
		intent.setAction(connectedFlag);
		sendBroadcast(intent);
	}

	@Override
	public void OnCallStreamsRunning(boolean is_video_call) {
		Log.e(TAG, "媒体接通");
	}

	@Override
	public void OnCallMediaStreamConnected(int mode) {
		if (mode == 0)
			Log.e(TAG, "媒体连接成功 模式 RTP");
		else
			Log.e(TAG, "媒体连接成功 模式 P2P");

		CommonTools.delayTime(1000);
		Intent intent = new Intent();
		intent.setAction(waitConnected);
		sendBroadcast(intent);
	}

	@Override
	public void OnCallPaused() {
		Log.e(TAG, "呼叫保持");
	}

	@Override
	public void OnCallResuming() {
		Log.e(TAG, "通话恢复");
	}

	@Override
	public void OnCallPausedByRemote() {
		Log.e(TAG, "远端呼叫保持");
	}

	@Override
	public void OnCallResumingByRemote() {
		Log.e(TAG, "远端通话恢复");
	}

	@Override
	public void OnCallEnded() {
		Log.e(TAG, "呼叫结束");
		muteAudioFocus(this, false);
		TipHelper.stopSound();
		TipHelper.stopShock();
		Intent intent = new Intent();
		intent.setAction(endFlag);
		sendBroadcast(intent);
	}

	@Override
	public void OnCallFailed(int status) {
		Log.e(TAG, "呼叫失败,错误代码 " + status);
		Intent intent = new Intent();
		intent.setAction(CALL_FAIL);
		sendBroadcast(intent);
	}

	@Override
	public void OnNetworkQuality(int ms, String vos_balance) {
		Log.e(TAG, "网络延迟 " + ms + ", 余额 " + vos_balance);
	}

	@Override
	public void OnRemoteDtmfClicked(int dtmf) {
		Log.e(TAG, "远程点击" + dtmf);
	}

	@Override
	public void OnCallReport(long nativePtr) {
		Log.e(TAG, "通话记录" + nativePtr);
		TipHelper.stopSound();
		TipHelper.stopShock();
		Intent intent = new Intent();
		intent.putExtra("nativePtr", nativePtr);
		intent.setAction(reportFlag);
		sendBroadcast(intent);
	}

	@Override
	public void onDestroy() {
		if (ICSOpenVPNApplication.the_sipengineReceive != null)
			ICSOpenVPNApplication.the_sipengineReceive.DeRegisterSipAccount();
		ICSOpenVPNApplication.the_sipengineReceive = null;
		Log.d(TAG, "onDestroy: ");
		super.onDestroy();
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_CHECKTOKEN) {
			CheckTokenHttp http = (CheckTokenHttp) object;
			if (http.getStatus() == 1) {
				//写入登陆天数，如果十五天没有登陆过则重新登录
				long time = Long.parseLong(http.getUpdateConfigTime()) * 1000;
				if (time != sharedUtils.readLong(Constant.CONFIG_TIME)) {
					if (time > sharedUtils.readLong(Constant.CONFIG_TIME)) {
						SecurityConfigHttp securityConfigHttp = new SecurityConfigHttp(this, HttpConfigUrl.COMTYPE_SECURITY_CONFIG);
						new Thread(securityConfigHttp).start();
					} else {
						registSipForReceive();
					}
					sharedUtils.writeLong(Constant.CONFIG_TIME, time);
				} else {
					registSipForReceive();
				}
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_SECURITY_CONFIG) {
			SecurityConfigHttp securityConfigHttp = (SecurityConfigHttp) object;
			if (securityConfigHttp.getStatus() == 1) {
				saveSecurityConfig(securityConfigHttp);
			}
		}
	}

	private void saveSecurityConfig(SecurityConfigHttp securityConfigHttp) {
		SecurityConfig.InBean in = securityConfigHttp.getSecurityConfig().getIn();
		SecurityConfig.OutBean out = securityConfigHttp.getSecurityConfig().getOut();
		sharedUtils.writeString(Constant.ASTERISK_IP_IN, in.getAsteriskIp());
		sharedUtils.writeString(Constant.ASTERISK_PORT_IN, in.getAsteriskPort());
		sharedUtils.writeString(Constant.ASTERISK_IP_OUT, out.getAsteriskIp());
		sharedUtils.writeString(Constant.ASTERISK_PORT_OUT, out.getAsteriskPort());
		sharedUtils.writeString(Constant.PUBLIC_PASSWORD, out.getPublicPassword());
		registSipForReceive();
	}

	@Override
	public void errorComplete(int cmdType, String errorMessage) {

	}

	private int count = 0;

	@Override
	public void noNet() {
		if (count == 0)
			CommonTools.showShortToast(getApplicationContext(), getString(R.string.no_wifi));
		count++;
		httpToken();
	}

	/**
	 * @param bMute 值为true时为关闭背景音乐。
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
	private boolean muteAudioFocus(Context context, boolean bMute) {
		if (context == null) {
			Log.d("ANDROID_LAB", "context is null.");
			return false;
		}
		boolean bool = false;
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (bMute) {
			int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		} else {
			int result = am.abandonAudioFocus(null);
			bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
		}
		Log.d("ANDROID_LAB", "pauseMusic bMute=" + bMute + " result=" + bool);
		return bool;
	}
	
	
}
