package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;
import de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.http.AddAlarmHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.UpdateAlarmHttp;
import de.blinkt.openvpn.model.AlarmClockEntity;
import de.blinkt.openvpn.util.BLECheckBitUtil;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.PickerScrollView;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.IntentPutKeyConstant.ALARM_CLOCK_ENTITY;
import static de.blinkt.openvpn.constant.IntentPutKeyConstant.ALARM_CLOCK_POSITION;
import static de.blinkt.openvpn.constant.UmengContant.CLICKALARMTAG;
import static de.blinkt.openvpn.constant.UmengContant.CLICKREPEATDAY;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSAVEADDALARM;

public class SetAlarmActivity extends BaseNetActivity  {

	@BindView(R.id.pickerscrlllviewHour)
	PickerScrollView pickerscrlllviewHour;
	@BindView(R.id.pickerscrlllviewMiunue)
	PickerScrollView pickerscrlllviewMiunue;
	@BindView(R.id.repeatTextView)
	TextView repeatTextView;
	@BindView(R.id.lableTextView)
	TextView lableTextView;
	@BindView(R.id.repeatLinearLayout)
	LinearLayout repeatLinearLayout;
	@BindView(R.id.lableLinearLayout)
	LinearLayout lableLinearLayout;
	public static int ALARM_REPEAT = 1;
	public static int ALARM_LABLE = 2;
	private byte[] alarmBytes = new byte[8];
	private ArrayList<String> repeatDayList = new ArrayList<>();

	//传入值，用于更新闹钟
	private AlarmClockEntity alarmClockEntity;
	public static int ADD_ALARM = 0;
	public static int UPDATE_ALARM = 1;
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			String repeat=getRepeatString(repeatDayList);
			if (msg.what == ADD_ALARM) {
				if (!CommonTools.isFastDoubleClick(1000)) {
					CreateHttpFactory.instanceHttp(SetAlarmActivity.this, HttpConfigUrl.COMTYPE_ADD_ALARM,
							pickerscrlllviewHour.getCurrentString() + ":" + pickerscrlllviewMiunue.getCurrentString(),
							repeat, lableTextView.getText().toString(), 1+"");
				}
			} else if (msg.what == UPDATE_ALARM) {
				alarmClockEntity.setStatus(1 + "");
				alarmClockEntity.setTime(pickerscrlllviewHour.getCurrentString() + ":" + pickerscrlllviewMiunue.getCurrentString());
				alarmClockEntity.setRepeat(repeat);
				alarmClockEntity.setTag(lableTextView.getText().toString());
				createHttpRequest( HttpConfigUrl.COMTYPE_UPDATE_ALARM,
						pickerscrlllviewHour.getCurrentString() + ":" + pickerscrlllviewMiunue.getCurrentString(),
						repeat, lableTextView.getText().toString(), alarmClockEntity.getAlarmClockId(), 1 + "");
			}
		}
	};
	//新创建闹钟的位置
	private int alarmPosition = -1;
	private String getRepeatString(List<String> repeatList) {
		StringBuilder repeatStrB = new StringBuilder();
		for (String i : repeatList) {
			repeatStrB.append(i + ",");
		}
		String repeatStr = repeatStrB.toString();
		if (!TextUtils.isEmpty(repeatStr)) {
			repeatStr = repeatStr.substring(0, repeatStr.length() - 1);
		}
		return repeatStr;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_alarm);
		ButterKnife.bind(this);
		initSet();
	}

	private void initSet() {
		Intent intent = getIntent();
		if (intent != null) {
			alarmClockEntity = (AlarmClockEntity) intent.getSerializableExtra(ALARM_CLOCK_ENTITY);
			alarmPosition = intent.getIntExtra(ALARM_CLOCK_POSITION, -1);
		}

		hasAllViewTitle(R.string.alarm, R.string.save_alarm, R.string.cancel, false);


		//拼接蓝牙字符串
		initBlueTextInput();
		//设置滑动
		addSroll();
	}

	@Override
	protected void onClickRightView() {
		//存储接口
//				Log.i("test", "设置闹钟:" + pickerscrlllviewMorningOrAfterNoon.getCurrentString() + ","
//						+ pickerscrlllviewHour.getCurrentString() + "时,"
//						+ pickerscrlllviewMiunue.getCurrentString() + "分。重复："
//						+ repeatTextView.getText().toString() + ","
//						+ repeatDayList.toString() + "备注："
//						+ lableTextView.getText().toString());
		//友盟方法统计
		MobclickAgent.onEvent(context, CLICKSAVEADDALARM);
		alarmBytes[6] = Byte.valueOf(addZero(pickerscrlllviewHour.getCurrentString()));
		alarmBytes[7] = Byte.valueOf(addZero(pickerscrlllviewMiunue.getCurrentString()));
		String alarmCheckStr = HexStringExchangeBytesUtil.bytesToHexString(new byte[]{BLECheckBitUtil.getXor(alarmBytes)});
		Log.i("test", "闹钟：" + HexStringExchangeBytesUtil.bytesToHexString(alarmBytes) + alarmCheckStr);
		SendCommandToBluetooth.sendMessageToBlueTooth(HexStringExchangeBytesUtil.bytesToHexString(alarmBytes) + alarmCheckStr);
		if (alarmClockEntity != null) {
			handler.sendEmptyMessage(UPDATE_ALARM);
		} else {
			handler.sendEmptyMessage(ADD_ALARM);
		}
	}

	private void initBlueTextInput() {
//		bytesList.add("AA070F"+alarmClockEntity.getPosition()+"0101");
		alarmBytes[0] = (byte) 0xAA;
		alarmBytes[1] = (byte) 0x07;
		alarmBytes[2] = (byte) 0x08;
		if (alarmPosition == -1) {
			alarmBytes[3] = (byte) alarmClockEntity.getPosition();
		} else {
			alarmBytes[3] = (byte) alarmPosition;
		}
		//周几和是否打开闹钟
		alarmBytes[4] = (byte) 0x01;
		//是否重复
		alarmBytes[5] = (byte) 0x01;
		//时
		alarmBytes[6] = (byte) 0x00;
		//分
		alarmBytes[7] = (byte) 0x00;
	}

	private void addSroll() {

		ArrayList<String> listHour = new ArrayList<>();
		ArrayList<String> listMin = new ArrayList<>();
		for (int i = 1; i <= 24; i++) {
			if (i < 10) {
				listHour.add("0" + i);
			} else {
				listHour.add(i + "");
			}
		}
		for (int i = 0; i < 60; i++) {
			if (i < 10) {
				listMin.add("0" + i);
			} else {
				listMin.add(i + "");
			}
		}
		pickerscrlllviewHour.setData(listHour);
		pickerscrlllviewMiunue.setData(listMin);
		pickerscrlllviewHour.setColor(0x007aff);
		pickerscrlllviewMiunue.setColor(0x007aff);
		pickerscrlllviewHour.setUnit(" " + getString(R.string.hour_single));
		pickerscrlllviewMiunue.setUnit(" " + getString(R.string.minute));
		if (alarmClockEntity != null) {
			String timeStr = alarmClockEntity.getTime();
			String[] times = timeStr.split(":");
			pickerscrlllviewHour.setSelected(times[0]);
			pickerscrlllviewMiunue.setSelected(times[1]);
			if (!TextUtils.isEmpty(alarmClockEntity.getRepeat())) {
				String[] arrayString = alarmClockEntity.getRepeat().split(",");
				int length = arrayString.length;
				for (int i = 0; i < length; i++) {
					repeatDayList.add(arrayString[i] + "");
				}
				if (arrayString.length != 0) {
					repeatTextView.setText(getStringBuilder(repeatDayList));
					getStringBuilder(repeatDayList);
				}
			}
			lableTextView.setText(alarmClockEntity.getTag() + "");

		} else {
			Calendar calendar = Calendar.getInstance();
			pickerscrlllviewHour.setSelected(calendar.get(Calendar.HOUR_OF_DAY) - 1);
			pickerscrlllviewMiunue.setSelected(calendar.get(Calendar.MINUTE));
		}


	}

	@OnClick({R.id.repeatLinearLayout, R.id.lableLinearLayout})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.repeatLinearLayout:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKREPEATDAY);
				Intent repeatIntent = new Intent(SetAlarmActivity.this, RepeatActivity.class);
				repeatIntent.putExtra("repeatDay", repeatDayList);
				startActivityForResult(repeatIntent, ALARM_REPEAT);
				break;
			case R.id.lableLinearLayout:
				//友盟方法统计
				MobclickAgent.onEvent(context, CLICKALARMTAG);
				Intent lableIntent = new Intent(SetAlarmActivity.this, LableActivity.class);
				lableIntent.putExtra("lableText", lableTextView.getText().toString());
				startActivityForResult(lableIntent, ALARM_LABLE);
				break;
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		if (requestCode == ALARM_REPEAT) {
			repeatDayList = data.getStringArrayListExtra("repeatDay");
			String weekStr = getStringBuilder(repeatDayList);
			repeatTextView.setText(weekStr);

			if (TextUtils.isEmpty(weekStr)) {

				repeatTextView.setText(getResources().getString(R.string.norepeat));
			}
		} else if (requestCode == ALARM_LABLE) {
			String lableText = data.getStringExtra("lableText");
			lableTextView.setText(lableText);
		}
	}

	@NonNull
	private String getStringBuilder(List<String> repeatDayList) {
		clearAlarmWeek();
		if (repeatDayList == null) {
			return "";
		}
		StringBuilder weekStr = new StringBuilder();
		for (String iString : repeatDayList) {
			if (TextUtils.isEmpty(iString)) {
				continue;
			}
			int jday = Integer.parseInt(iString);
			switch (jday) {
				case 0:
					weekStr.append(" " + mContext.getResources().getString(R.string.Monday));
					alarmBytes[4] |= 0x01;
					break;
				case 1:
					weekStr.append(" " + mContext.getResources().getString(R.string.Tuesday));
					alarmBytes[4] |= 0x02;
					break;
				case 2:
					weekStr.append(" " + mContext.getResources().getString(R.string.Wednesday));
					alarmBytes[4] |= 0x04;
					break;
				case 3:
					weekStr.append(" " + mContext.getResources().getString(R.string.Thursday));
					alarmBytes[4] |= 0x08;
					break;
				case 4:
					weekStr.append(" " + mContext.getResources().getString(R.string.Friday));
					alarmBytes[4] |= 0x10;
					break;
				case 5:
					weekStr.append(" " + mContext.getResources().getString(R.string.Saturday));
					alarmBytes[4] |= 0x20;
					break;
				case 6:
					weekStr.append(" " + mContext.getResources().getString(R.string.Sunday));
					alarmBytes[4] |= 0x40;
					break;
			}

		}
		return weekStr.toString();
	}

	//清空星期时间
	private void clearAlarmWeek() {
		alarmBytes[4] = (byte) 0x80;
	}

	@Override
	public void rightComplete(int cmdType, CommonHttp object) {
		if (cmdType == HttpConfigUrl.COMTYPE_ADD_ALARM) {
			if (object.getStatus() == 1) {
				AddAlarmHttp http = (AddAlarmHttp) object;
				AlarmClockEntity alarmClockEntity = http.getAlarmClockEntity();
				Intent intent = new Intent();
				intent.putExtra(IntentPutKeyConstant.ALARM_CLOCK_ENTITY, alarmClockEntity);
				setResult(AlarmClockActivity.ADD_CLICK, intent);
				finish();
				Log.i("test", alarmClockEntity.toString());
			} else {
				CommonTools.showShortToast(this, object.getMsg());
			}
		} else if (cmdType == HttpConfigUrl.COMTYPE_UPDATE_ALARM) {
			if (object.getStatus() == 1) {
				Intent intent = new Intent();
				intent.putExtra(IntentPutKeyConstant.ALARM_CLOCK_ENTITY, alarmClockEntity);
				alarmClockEntity.setStatus("1");
				setResult(AlarmClockActivity.UPDATE_CLICK, intent);
				finish();
			}
		}
	}




	//为发送的数据添加0，如果小于15
	private String addZero(String date) {
		date = date.toUpperCase();
		if (!TextUtils.isEmpty(date)&&date.length()==1) {
			return "0" + date;
		} else {
			return "" + date;
		}
	}
}
