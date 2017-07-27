package de.blinkt.openvpn.activities.SimOption.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CallLog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import cn.qfishphone.sipengine.SipEngineCore;
import de.blinkt.openvpn.activities.Base.BaseSensorActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.ContactRecodeEntity;
import de.blinkt.openvpn.service.CallPhoneService;
import de.blinkt.openvpn.util.AssetsDatabaseManager;
import de.blinkt.openvpn.util.DatabaseDAO;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.IsHasrecodePermission;
import de.blinkt.openvpn.util.PhoneNumberZone;
import de.blinkt.openvpn.util.PinYinConverNumber;
import de.blinkt.openvpn.util.querylocaldatebase.FindContactUtil;
import de.blinkt.openvpn.util.querylocaldatebase.SearchConnectterHelper;
import de.blinkt.openvpn.util.querylocaldatebase.TipHelper;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKRECIVECANCEL;
import static de.blinkt.openvpn.constant.UmengContant.CLICKRECIVECONTROLVOIDE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKRECIVEHANGUP;
import static de.blinkt.openvpn.constant.UmengContant.CLICKRECIVEPHONEQUIET;
import static de.blinkt.openvpn.constant.UmengContant.CLICKRECIVESURE;

public class ReceiveCallActivity extends BaseSensorActivity {

    @BindView(R.id.nametxt)
    TextView nametxt;
    @BindView(R.id.address_text)
    TextView addressText;
    @BindView(R.id.chronometer)
    Chronometer timer;
    @BindView(R.id.calmTextView)
    TextView calmTextView;
    @BindView(R.id.mtview)
    TextView mtview;
    @BindView(R.id.ll_control_voide)
    LinearLayout llControlVoide;
    @BindView(R.id.receiveBtn)
    TextView receiveBtn;
    @BindView(R.id.cancelcallbtn)
    TextView cancelcallbtn;
    @BindView(R.id.hangupbtn)
    TextView hangUpBtn;

    private SipEngineCore sipEngineCore;
    private ReceiveCallReceiver receiver;
    public SQLiteDatabase sqliteDB;
    public DatabaseDAO dao;
    public static boolean isForeground = false;
    private ScreenStatusReceiver mScreenStatusReceiver;
    Unbinder unbinder;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_receive_call);
        unbinder= ButterKnife.bind(this);
        IsHasrecodePermission isHasrecodePermission=new IsHasrecodePermission(this);
        isHasrecodePermission.isHasPermission();
        registerReceiver();
        setData();
        initDB();
        searchArea();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
    }

    private void initDB() {
        AssetsDatabaseManager.initManager(getApplicationContext());
        AssetsDatabaseManager mg = AssetsDatabaseManager.getAssetsDatabaseManager();
        sqliteDB = mg.getDatabase("number_location.zip");
        dao = new DatabaseDAO(sqliteDB);
    }

    private void searchArea() {
        String address;
        String phoneNumStr = getPhoneNumber();
        address = PhoneNumberZone.getAddress(dao, phoneNumStr);
        if (!TextUtils.isEmpty(address))
            addressText.setText(address);
    }

    private void registerReceiver() {
        receiver = new ReceiveCallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CallPhoneService.endFlag);
        filter.addAction(CallPhoneService.reportFlag);
        registerReceiver(receiver, filter);
        TipHelper.PlaySound(this);
        TipHelper.PlayShock(this);
        sipEngineCore = ICSOpenVPNApplication.the_sipengineReceive;
        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStatusReceiver, screenStatusIF);
    }

    private void setData() {
        String phoneNumStr = getPhoneNumber();
        String realName = getRealName();
        if (!TextUtils.isEmpty(realName)) {
            nametxt.setText(realName);
        } else {
            nametxt.setText(phoneNumStr);
        }
    }

    private String getRealName() {
        String str = getPhoneNumber();
        String realName = SearchConnectterHelper.getContactNameByPhoneNumber(this, str);
        return realName;
    }

    private String getPhoneNumber() {
        String phoneNumStr = getIntent().getStringExtra("phoneNum");
        if (phoneNumStr.startsWith("86")) {
            phoneNumStr = phoneNumStr.substring(2, phoneNumStr.length());
        }
        return phoneNumStr;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    private void insertCallRecode(int type) {
        ContactRecodeEntity contactRecodeEntity = new ContactRecodeEntity();
        String phoneNumStr = getPhoneNumber();
        String realName = getRealName();
        if (!TextUtils.isEmpty(realName)) {
            contactRecodeEntity.setName(realName);
        } else {
            contactRecodeEntity.setName("");
        }
        long time = System.currentTimeMillis();
        contactRecodeEntity.setPhoneNumber(phoneNumStr);
        contactRecodeEntity.setCallTime(time);
        contactRecodeEntity.setData(DateUtils.getTimeStampString(time + ""));
        if (!TextUtils.isEmpty(addressText.getText()))
            contactRecodeEntity.setAddress(addressText.getText().toString());
        contactRecodeEntity.setType(type);
        if (type == CallLog.Calls.INCOMING_TYPE)
            contactRecodeEntity.setTypeString(Constant.CALL_INCOMING);
        else {
            contactRecodeEntity.setTypeString(Constant.CALL_MISSED);
        }
        if (!TextUtils.isEmpty(realName)) {
            contactRecodeEntity.setFormattedNumber(PinYinConverNumber.getInstance().getNameNum(realName));
        } else {
            contactRecodeEntity.setFormattedNumber(PinYinConverNumber.getInstance().getNameNum(phoneNumStr));
        }
        FindContactUtil.addCallRecode(this, contactRecodeEntity);

    }

    @OnClick({R.id.receiveBtn,R.id.calmTextView,R.id.mtview, R.id.cancelcallbtn,R.id.hangupbtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.receiveBtn:
                //友盟方法统计
                MobclickAgent.onEvent(context, CLICKRECIVESURE);
                sipEngineCore.AnswerCall();
                TipHelper.stopShock();
                TipHelper.stopSound();
                controlWidget();
                startTimer();
                break;
            case R.id.calmTextView:
                //友盟方法统计
                MobclickAgent.onEvent(context, CLICKRECIVEPHONEQUIET);
                Boolean iscalmSelected = calmTextView.isSelected();
                sipEngineCore.MuteMic(!iscalmSelected);
                calmTextView.setSelected(!iscalmSelected);
                break;
            case R.id.mtview:
                //友盟方法统计
                MobclickAgent.onEvent(context, CLICKRECIVECONTROLVOIDE);
                Boolean isselected = mtview.isSelected();
                sipEngineCore.SetLoudspeakerStatus(!isselected);
                mtview.setSelected(!isselected);
                break;
            case R.id.cancelcallbtn:
                //友盟方法统计
                MobclickAgent.onEvent(context, CLICKRECIVECANCEL);
                sipEngineCore.Hangup();
                finish();
                TipHelper.stopShock();
                TipHelper.stopSound();
                break;
            case R.id.hangupbtn:
                //友盟方法统计
                MobclickAgent.onEvent(context, CLICKRECIVEHANGUP);
                sipEngineCore.Hangup();

                TipHelper.stopShock();
                TipHelper.stopSound();
                break;
        }

    }

    private void startTimer() {
        timer.setBase(SystemClock.elapsedRealtime());
        //开始计时
        timer.start();
    }

    private void stopTimer() {
        if (timer.isActivated()) {
            timer.stop();
            timer.setBase(SystemClock.elapsedRealtime());
            timer = null;
        }
    }

    private void controlWidget() {
        llControlVoide.setVisibility(View.VISIBLE);
        timer.setVisibility(View.VISIBLE);
        hangUpBtn.setVisibility(View.VISIBLE);
        cancelcallbtn.setVisibility(View.GONE);
        receiveBtn.setVisibility(View.GONE);
    }

    @Override
    protected void setDataParam(Intent intent) {
        super.setDataParam(intent);
        intent.putExtra("phoneNum", getIntent().getStringExtra("phoneNum"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        isForeground = false;
        initNotify(nametxt.getText().toString(), getIntent().getStringExtra("phoneNum"));
    }

    public class ReceiveCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (CallPhoneService.endFlag.equals(action)) {

                if (CallPhoneService.CALL_DIR == 0) {
                    cancelNotify();
                    stopTimer();
                    if (!isFinishing())
                        try {
                            onBackPressed();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            } else if (CallPhoneService.reportFlag.equals(action)) {
                if (CallPhoneService.CALL_DIR == 0) {
                    long nativePtr = intent.getLongExtra("nativePtr", -1);
                    if (nativePtr > 0) {
                        insertCallRecode(CallLog.Calls.MISSED_TYPE);
                    } else {
                        insertCallRecode(CallLog.Calls.INCOMING_TYPE);
                    }
                }
            }
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (receiver != null)
            unregisterReceiver(receiver);
        receiver = null;
        if (sipEngineCore != null) {
            sipEngineCore.MuteMic(true);
            sipEngineCore.SetLoudspeakerStatus(true);
            sipEngineCore = null;
        }
        if (mScreenStatusReceiver != null)
            unregisterReceiver(mScreenStatusReceiver);
        cancelNotify();
    }

    private void cancelNotify() {
        if (mNotificationManager != null)
            mNotificationManager.cancel(notifyId);
    }

    class ScreenStatusReceiver extends BroadcastReceiver {
        String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SCREEN_OFF.equals(intent.getAction())) {
                TipHelper.stopSound();
            }
        }
    }

}
