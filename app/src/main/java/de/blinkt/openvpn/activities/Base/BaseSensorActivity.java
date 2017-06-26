package de.blinkt.openvpn.activities.Base;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.push.PhoneReceiver;

/**
 * Created by Administrator on 2016/11/26.
 */

public class BaseSensorActivity extends CommenActivity implements SensorEventListener {
	//调用距离传感器，控制屏幕
	private SensorManager mManager;//传感器管理对象
	//屏幕开关
	private PowerManager localPowerManager = null;//电源管理对象
	protected PowerManager.WakeLock localWakeLock = null;//电源锁
	protected NotificationManager mNotificationManager;
	NotificationCompat.Builder mBuilder;
	protected  int notifyId = 100;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		addSensorSet();
	}

	//设置距离传感器，贴脸的时候息屏，离开时候亮屏
	private void addSensorSet() {
		mManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		//获取系统服务POWER_SERVICE，返回一个PowerManager对象
		localPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
		localWakeLock = this.localPowerManager.newWakeLock(32, "unitoy power");//第一个参数为电源锁级别，第二个是日志tag
	}

	@Override
	protected void onResume() {
		super.onResume();
		mManager.registerListener(this, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
				, SensorManager.SENSOR_DELAY_NORMAL);//注册传感器，第一个参数为距离监听器，第二个是传感器类型，第三个是延迟类型
	}
	private int distance;
	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] distances = event.values;
		if (distances != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			if (distances[0] == 0.0f) {
				distance=1;
				Log.d("unitoy", "hands up in calling activity");
				if (localWakeLock.isHeld()) {
					return;
				} else {
					localWakeLock.acquire();// 申请设备电源锁
				}
			} else {
				distance=2;
				Log.d("unitoy", "hands moved in calling activity");
				if (localWakeLock.isHeld()) {
					return;
				} else {
					localWakeLock.setReferenceCounted(false);
					if (localWakeLock.isHeld()) {
						localWakeLock.release(); // 释放设备电源锁
					}
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//自动生成的方法存根
	}

	public void onDestroy() {
		super.onDestroy();
		if (mManager != null) {
			if (localWakeLock.isHeld()) {
				localWakeLock.release();//释放电源锁，如果不释放finish这个acitivity后仍然会有自动锁屏的效果，不信可以试一试
			}
			mManager.unregisterListener(this);//注销传感器监听
		}
	}

	protected void initNotify(String name,String phone ) {
		if(distance==1){
			return;
		}
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}
		if (mBuilder == null) {
			mBuilder = new NotificationCompat.Builder(this);
		}
		mBuilder.setContentTitle(getString(R.string.unitoys_phone))
				.setContentText(getString(R.string.call_phoning, name, phone))
				.setNumber(3)//显示数量
//				.setTicker("有新短信来啦")//通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
				.setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//				.setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
				//Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
				.setSmallIcon(R.drawable.login_icon);
		Intent intent = new Intent(this, PhoneReceiver.class);
		intent.setAction(PhoneReceiver.CALL_PHONE);
		PendingIntent contextIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		mBuilder.setContentIntent(contextIntent);
		mBuilder.build().vibrate = null;
		mBuilder.build().sound=null;
		mNotificationManager.notify(notifyId, mBuilder.build());
	}

	protected void setDataParam(Intent intent){

	}



	AudioManager audio = (AudioManager) getSystemService(Service.AUDIO_SERVICE);

	//重写 Activity 的 onKeyDown 方法

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_VOLUME_UP:
				audio.adjustStreamVolume(
						AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_RAISE,
						AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				audio.adjustStreamVolume(
						AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_LOWER,
						AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
				return true;
			default:
				break;
		}
		return super.onKeyDown(keyCode, event);
	}


}
