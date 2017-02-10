package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.AlarmClockAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.DeleteAlarmClockHttp;
import de.blinkt.openvpn.http.FindAlarmClockHttp;
import de.blinkt.openvpn.http.UpdateAlarmClockStatueHttp;
import de.blinkt.openvpn.model.AlarmClockEntity;
import de.blinkt.openvpn.util.BLECheckBitUtil;
import de.blinkt.openvpn.util.CommonTools;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.IntentPutKeyConstant.ALARM_CLOCK_POSITION;
import static de.blinkt.openvpn.constant.UmengContant.CLICKADDALARM;
import static de.blinkt.openvpn.constant.UmengContant.CLICKALARMITEMEDIT;
import static de.blinkt.openvpn.constant.UmengContant.CLICKDELETEALARM;
import static de.blinkt.openvpn.constant.UmengContant.CLICKEDITALARM;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class AlarmClockActivity extends BaseNetActivity implements AlarmClockAdapter.onSwipeListener {
	@BindView(R.id.bluetooth_tv)
	TextView bluetoothTv;
	@BindView(R.id.open_bluetooth_tv)
	TextView openBluetoothTv;
	@BindView(R.id.open_bluetooth_ll)
	RelativeLayout openBluetoothLl;
	@BindView(R.id.alarm_clock_rv)
	RecyclerView alarmClockRv;
	AlarmClockAdapter alarmClockAdapter;
	List<AlarmClockEntity> alarmClockEntityList = new ArrayList<>();
	@BindView(R.id.alarm_clock_tip_tv)
	TextView alarmClockTipTv;
	@BindView(R.id.edit_alarm_tv)
	TextView editAlarmTv;
	@BindView(R.id.add_alarm_tv)
	TextView addAlarmTv;
	public boolean isLinkedDevice;
	@BindView(R.id.iv_load_push)
	ImageView ivLoadPush;
	@BindView(R.id.tv_load_push)
	TextView tvLoadPush;
	@BindView(R.id.ll_load_push)
	LinearLayout llLoadPush;
	@BindView(R.id.edit_ll)
	LinearLayout llEdit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_clock);
		ButterKnife.bind(this);
		initTitle();
		initUI();
		initData();
		findAlarmClockHttp();
	}

	private void findAlarmClockHttp() {
		FindAlarmClockHttp findAlarmClockHttp = new FindAlarmClockHttp(this, HttpConfigUrl.COMTYPE_ALARM_CLOCK_GET);
		new Thread(findAlarmClockHttp).start();
	}

	private void deleteAlarmClock(String id) {
		DeleteAlarmClockHttp deleteAlarmClock = new DeleteAlarmClockHttp(this, HttpConfigUrl.COMTYPE_ALARM_CLOCK_DELETE, id);
		new Thread(deleteAlarmClock).start();
	}

	private void updateAlarmClockStatue(String statue, String id) {
		UpdateAlarmClockStatueHttp updateAlarmClockStatueHttp = new UpdateAlarmClockStatueHttp(this, HttpConfigUrl.COMTYPE_UPDATE_ALARM_CLOCK_STATUE, id, statue);
		new Thread(updateAlarmClockStatueHttp).start();
	}

	UartService uartservice;

	private void initTitle() {
		uartservice = ICSOpenVPNApplication.uartService;
		titleBar.setTextTitle(R.string.alarm_clock_tip);
		titleBar.setLeftBtnIcon(R.drawable.btn_top_back);
		titleBar.getLeftText().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getResources().getString(R.string.complete).equals(titleBar.getLeftText().getText().toString())) {
					noCanEdit();
					alarmClockAdapter.notifyDataSetChanged();
				} else {
					onBackPressed();
				}
			}
		});
		titleBar.setRightBtnIcon(R.drawable.edit_add);
		titleBar.getRightText().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isDoubleClick){
					llEdit.setVisibility(View.GONE);
				}else{
				llEdit.setVisibility(View.VISIBLE);

				}
				isDoubleClick=!isDoubleClick;
			}
		});
	}

private boolean	isDoubleClick=false;
	private void noCanEdit() {
		alarmClockAdapter.isEditAlarmClock(false);
		titleBar.setLeftBtnIcon(R.drawable.btn_top_back);
		addAlarmTv.setVisibility(View.VISIBLE);
		editAlarmTv.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
//        if(TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))){
//            bluetoothTv.setText(getString(R.string.no_bind_bluetooth_device));
//            openBluetoothTv.setText(getString(R.string.go_bind));
//        }  else
		if (uartservice != null && uartservice.mConnectionState == UartService.STATE_CONNECTED) {
			isLinkedDevice = true;
		} else if (uartservice.isConnecttingBlueTooth()) {
			bluetoothIsOpen();
		}
		//如果连接上，图片要相应不同
		if (isLinkedDevice) {
			isLinkedDevice(isLinkedDevice);
			alarmClockTipTv.setVisibility(View.VISIBLE);
		} else {
			alarmClockTipTv.setVisibility(View.GONE);
		}

	}

	private void bluetoothIsOpen() {
		openBluetoothTv.setVisibility(View.GONE);
		bluetoothTv.setVisibility(View.GONE);
		llLoadPush.setVisibility(View.VISIBLE);
		((AnimationDrawable) ivLoadPush.getBackground()).start();
		bluetoothTv.setText(getString(R.string.no_bind_bluetooth_device));
		openBluetoothTv.setText(getString(R.string.go_bind));
	}

	private static final int REQUEST_ENABLE_BT = 2;

	@OnClick(R.id.open_bluetooth_tv)
	public void onClick() {
		if (getString(R.string.go_bind).equals(openBluetoothTv.getText().toString())) {
			toActivity(MyDeviceActivity.class);
		} else {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		noCanEdit();
		if (data == null) {
			alarmClockAdapter.notifyDataSetChanged();
			return;
		}

		switch (resultCode) {
			case ADD_CLICK:
				AlarmClockEntity alarmClockEntityAdd = (AlarmClockEntity) data.getSerializableExtra(IntentPutKeyConstant.ALARM_CLOCK_ENTITY);
				alarmClockAdapter.add(alarmClockAdapter.getItemCount(), alarmClockEntityAdd);
				break;
			case UPDATE_CLICK:
				AlarmClockEntity alarmClockEntity = (AlarmClockEntity) data.getSerializableExtra(IntentPutKeyConstant.ALARM_CLOCK_ENTITY);
				alarmClockAdapter.remove(alarmClockEntity.getPosition());
				alarmClockAdapter.add(alarmClockEntity.getPosition(), alarmClockEntity);
				break;
			case REQUEST_ENABLE_BT:

				if (resultCode == Activity.RESULT_OK) {
					Toast.makeText(this, "蓝牙已启动", Toast.LENGTH_SHORT).show();
					bluetoothIsOpen();
				}
				break;
		}

	}

	private void initUI() {
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		alarmClockRv.setLayoutManager(layoutManager);
	}

	private void initData() {
		alarmClockAdapter = new AlarmClockAdapter(this, alarmClockEntityList);
		alarmClockAdapter.isVisibility(openBluetoothLl.getVisibility() == View.VISIBLE ? true : false);
		alarmClockAdapter.setOnDelListener(this);
		alarmClockRv.setAdapter(alarmClockAdapter);
	}

	private void isLinkedDevice(boolean isLinkedDevice) {
		if (isLinkedDevice) {
			openBluetoothLl.setVisibility(View.GONE);
			alarmClockTipTv.setVisibility(View.VISIBLE);
		} else {
			openBluetoothLl.setVisibility(View.VISIBLE);
			alarmClockTipTv.setVisibility(View.GONE);
		}
	}

	public static final int ADD_CLICK = 101;
	public static final int UPDATE_CLICK = 102;

	@OnClick({R.id.edit_alarm_tv, R.id.add_alarm_tv,R.id.edit_ll})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.edit_alarm_tv:
				//友盟方法统计
				llEdit.setVisibility(View.GONE);
				MobclickAgent.onEvent(context, CLICKEDITALARM);
				alarmClockAdapter.isEditAlarmClock(true);
				alarmClockAdapter.notifyDataSetChanged();
				titleBar.setLeftBtnText(getResources().getString(R.string.complete));
				break;
			case R.id.add_alarm_tv:
				llEdit.setVisibility(View.GONE);
				if (alarmClockAdapter.getItemCount() >= 3) {
					CommonTools.showShortToast(this, getResources().getString(R.string.more_three));
					return;
				}
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKADDALARM);
				Intent intent = new Intent(this, SetAlarmActivity.class);
				intent.putExtra(ALARM_CLOCK_POSITION, alarmClockAdapter.getItemCount());
				startActivityForResult(intent, ADD_CLICK);
				break;
			case R.id.edit_ll:
				llEdit.setVisibility(View.GONE);
				break;

		}
	}

	public int position;

	@Override
	public void onDel(int pos) {
		//友盟方法统计
		MobclickAgent.onEvent(context, CLICKDELETEALARM);
		String delStr = "AA07080" + pos + "00000000";
		position = pos;
		deleteAlarmClock(alarmClockAdapter.getItem(pos).getAlarmClockId());
		//发送到蓝牙，删除闹钟
		SendCommandToBluetooth.sendMessageToBlueTooth(delStr + HexStringExchangeBytesUtil.bytesToHexString(new byte[]{BLECheckBitUtil.getXor(HexStringExchangeBytesUtil.hexStringToBytes(delStr))}));
	}

	@Override
	public void onItemClick(int pos) {
		//友盟方法统计
		MobclickAgent.onEvent(context, CLICKALARMITEMEDIT);
		Intent intent = new Intent(this, SetAlarmActivity.class);
		alarmClockAdapter.getItem(pos).setPosition(pos);
		intent.putExtra(IntentPutKeyConstant.ALARM_CLOCK_ENTITY, alarmClockAdapter.getItem(pos));
		startActivityForResult(intent, UPDATE_CLICK);
	}

	@Override
	public void statueChange(int statue, int pos) {
		updateAlarmClockStatue(statue + "", alarmClockAdapter.getItem(pos).getAlarmClockId());
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_ALARM_CLOCK_GET) {
			FindAlarmClockHttp findAlarmClockHttp = (FindAlarmClockHttp) object;
			if (findAlarmClockHttp.getStatus() == 1) {
				if (findAlarmClockHttp.getAlarmClockEntityList() != null && findAlarmClockHttp.getAlarmClockEntityList().size() != 0) {
					alarmClockAdapter.addAll(findAlarmClockHttp.getAlarmClockEntityList());
				}
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_ALARM_CLOCK_DELETE) {
			DeleteAlarmClockHttp deleteAlarmClock = (DeleteAlarmClockHttp) object;
			if (deleteAlarmClock.getStatus() == 1) {
				alarmClockAdapter.remove(position);
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_UPDATE_ALARM_CLOCK_STATUE) {
			CommonTools.showShortToast(this, object.getMsg());
		}
	}
}
