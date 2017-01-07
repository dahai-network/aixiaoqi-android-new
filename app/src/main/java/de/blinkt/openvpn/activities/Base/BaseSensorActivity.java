package de.blinkt.openvpn.activities.Base;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by Administrator on 2016/11/26.
 */

public class BaseSensorActivity extends CommenActivity implements SensorEventListener {
	//调用距离传感器，控制屏幕
	private SensorManager mManager;//传感器管理对象
	//屏幕开关
	private PowerManager localPowerManager = null;//电源管理对象
	private PowerManager.WakeLock localWakeLock = null;//电源锁

	@Override
	public void onCreate(Bundle savedInstanceState) {
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

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] distances = event.values;
		if (distances != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
			if (distances[0] == 0.0f) {
				Log.d("unitoy", "hands up in calling activity");
				if (localWakeLock.isHeld()) {
					return;
				} else {
					localWakeLock.acquire();// 申请设备电源锁
				}
			} else {
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
}
