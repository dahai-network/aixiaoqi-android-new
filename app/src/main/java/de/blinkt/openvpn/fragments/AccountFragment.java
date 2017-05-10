package de.blinkt.openvpn.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import cn.com.johnson.model.AppMode;
import cn.com.johnson.model.ChangeViewStateEvent;
import cn.com.johnson.widget.GlideCircleTransform;
import de.blinkt.openvpn.ReceiveBLEMoveReceiver;
import de.blinkt.openvpn.activities.BalanceParticularsActivity;
import de.blinkt.openvpn.activities.ChoiceDeviceTypeActivity;
import de.blinkt.openvpn.activities.ImportantAuthorityActivity;
import de.blinkt.openvpn.activities.MyDeviceActivity;
import de.blinkt.openvpn.activities.PackageCategoryActivity;
import de.blinkt.openvpn.activities.PackageMarketActivity;
import de.blinkt.openvpn.activities.PersonalCenterActivity;
import de.blinkt.openvpn.activities.RechargeActivity;
import de.blinkt.openvpn.activities.SettingActivity;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.base.BaseStatusFragment;
import de.blinkt.openvpn.http.BalanceHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.GetBindDeviceHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.OrderUsageRemainHttp;
import de.blinkt.openvpn.model.UsageRemainEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.TitleBar;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;

import static android.view.View.GONE;
import static de.blinkt.openvpn.activities.MyDeviceActivity.BRACELETTYPE;
import static de.blinkt.openvpn.constant.Constant.BRACELETNAME;
import static de.blinkt.openvpn.constant.Constant.BRACELETPOWER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKBALANCE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKENTERPERSONCENTER;
import static de.blinkt.openvpn.constant.UmengContant.CLICKMYDEVICE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKMYPACKAGE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKRECHARGE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKSET;

/**
 * 我的界面
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends BaseStatusFragment implements View.OnClickListener, InterfaceCallback, DialogInterfaceTypeBase {
    @BindView(R.id.title)
    TitleBar title;
    @BindView(R.id.headImageView)
    ImageView headImageView;
    @BindView(R.id.accountNameTextView)
    TextView accountNameTextView;
    @BindView(R.id.accountPhoneTextView)
    TextView accountPhoneTextView;
    @BindView(R.id.rl_people_center)
    RelativeLayout rlPeopleCenter;
    @BindView(R.id.balanceTextView)
    TextView balanceTextView;
    @BindView(R.id.activateRelativeLayout)
    RelativeLayout activateRelativeLayout;
    @BindView(R.id.addDeviceRelativeLayout)
    RelativeLayout addDeviceRelativeLayout;
    @BindView(R.id.deviceSummarizedRelativeLayout)
    RelativeLayout deviceSummarizedRelativeLayout;
    @BindView(R.id.permission_set)
    TextView tvPermissionSet;
    @BindView(R.id.billtv)
    TextView billTv;
    @BindView(R.id.tv_setting)
    TextView tvSetting;
    @BindView(R.id.deviceNameTextView)
    TextView deviceNameTextView;
    @BindView(R.id.powerTextView)
    TextView powerTextView;
    @BindView(R.id.signalIconImageView)
    ImageView signalIconImageView;
    @BindView(R.id.operatorTextView)
    TextView operatorTextView;
    @BindView(R.id.rechargeTextView)
    TextView rechargeTextView;
    @BindView(R.id.unBindTextView)
    TextView unBindTextView;
    @BindView(R.id.noPacketRelativeLayout)
    RelativeLayout noPacketRelativeLayout;
    @BindView(R.id.PacketRelativeLayout)
    RelativeLayout PacketRelativeLayout;
    @BindView(R.id.add_or_activate_package)
    TextView addOrActivatePackage;
    @BindView(R.id.call_time)
    TextView callTime;
    @BindView(R.id.flow)
    TextView flow;
    @BindView(R.id.flow_count)
    TextView flowCount;
    @BindView(R.id.package_all_count)
    TextView packageAllCount;
    @BindView(R.id.accountScrollView)
    ScrollView accountScrollView;
    //bluetooth status蓝牙状态
    private String bleStatus;
    private String TAG = "AccountFragment";
    boolean hasPackage = false;
    public static ImageView tvNewPackagetAction;
    public static ImageView tvNewVersion;


    public AccountFragment() {
        // Required empty public constructor
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (tvNewPackagetAction != null)
                        tvNewPackagetAction.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    if (tvNewPackagetAction != null) {
                        tvNewPackagetAction.setVisibility(View.GONE);
                    }
                    break;
                case 3:
                    if (tvNewVersion != null && !AppMode.getInstance().isClickAddDevice)
                        tvNewVersion.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    if (tvNewVersion != null)
                        tvNewVersion.setVisibility(View.GONE);
                    break;
                case 5:
                    showDeviceSummarized(true);
                    break;
                case 6:
                    showDeviceSummarized(false);
                    break;


            }
            EventBus.getDefault().post(new ChangeViewStateEvent(msg.what));
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Glide.get(getActivity()).clearMemory();
        setLayoutId(R.layout.fragment_account);
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        topProgressView.setWhiteBack(true);
        ButterKnife.bind(this, rootView);
        title.setTextTitle(getString(R.string.personal_center));
        tvNewPackagetAction = (ImageView) rootView.findViewById(R.id.tv_new_packaget_action);
        tvNewVersion = (ImageView) rootView.findViewById(R.id.tv_new_version);
        //初始化状态
        tvNewPackagetAction.setVisibility(View.GONE);
        tvNewVersion.setVisibility(View.GONE);
        //注册广播
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNoticBroadCastReciver, new IntentFilter("Notic"));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取数据，每次都重新获取一次以保持正确性。
        getData();
        getDeviceType();
        getPackage();
    }


    private void getPackage() {
        CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_GET_USER_ORDER_USAGE_REMAINING);
    }

    private void getDeviceType(){
        if(TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))||TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETNAME))){
        CreateHttpFactory.instanceHttp(AccountFragment.this, HttpConfigUrl.COMTYPE_GET_BIND_DEVICE);
        }else{
            mHandler.sendEmptyMessage(5);

        }
    }
    /**
     * 设备布局
     *
     * @param isShow
     */
    //  @Override
    public void showDeviceSummarized(boolean isShow) {
        if (deviceSummarizedRelativeLayout != null) {
            if (isShow) {
                deviceSummarizedRelativeLayout.setVisibility(View.VISIBLE);
            } else {
                deviceSummarizedRelativeLayout.setVisibility(GONE);
            }
        }
    }

    public void setSummarized(String deviceType, String powerPercent, boolean isRegisted) {
        try {
            if (deviceType != null)
                deviceNameTextView.setText(deviceType);
            if (powerPercent != null)
                setPowerPercent(powerPercent);
            setRegisted(isRegisted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPowerPercent(String powerPercent) {
        powerTextView.setText(powerPercent + "%");
    }


    @Override
    public void setRegisted(boolean isRegisted) {
        if (isRegisted) {
            signalIconImageView.setBackgroundResource(R.drawable.registed);
            String operater = SharedUtils.getInstance().readString(Constant.OPERATER);
            if (operater != null) {
                switch (operater) {
                    case Constant.CHINA_TELECOM:
                        operatorTextView.setText(getString(R.string.china_telecom));
                        break;
                    //中国移动
                    case Constant.CHINA_MOBILE:
                        operatorTextView.setText(getString(R.string.china_mobile));
                        break;
                    case Constant.CHINA_UNICOM:
                        operatorTextView.setText(getString(R.string.china_unicom));
                        break;

                }
            }
        } else {
            if (signalIconImageView != null)
                signalIconImageView.setBackgroundResource(R.drawable.unregist);

            if (operatorTextView != null) {
                operatorTextView.setText("----");
            }
        }
    }

    private void getData() {

        if (!TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.NICK_NAME)))
            accountNameTextView.setText(SharedUtils.getInstance().readString(Constant.NICK_NAME));
        Glide.with(ICSOpenVPNApplication.getContext()).load(SharedUtils.getInstance().readString(Constant.USER_HEAD)).centerCrop().placeholder(R.drawable.default_head)
                .transform(new GlideCircleTransform(ICSOpenVPNApplication.getContext(), 2, ICSOpenVPNApplication.getContext().getResources().getColor(R.color.white)))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(headImageView);
        accountPhoneTextView.setText(SharedUtils.getInstance().readString(Constant.USER_NAME));
        BalanceHttp http = new BalanceHttp(this, HttpConfigUrl.COMTYPE_GET_BALANCE);
        new Thread(http).start();

    }

    private void showDialog() {
        //不能按返回键，只能二选其一
        DialogBalance cardRuleBreakDialog = new DialogBalance(this, getActivity(), R.layout.dialog_balance, 2);
        cardRuleBreakDialog.setCanClickBack(false);
        cardRuleBreakDialog.changeText(getResources().getString(R.string.are_you_sure_unbind), getResources().getString(R.string.sure));
    }


    @Override
    public void dialogText(int type, String text) {
        if (type == 2) {
            if (!CommonTools.isFastDoubleClick(2000)) {
                //断开连接
                CreateHttpFactory.instanceHttp(this, HttpConfigUrl.COMTYPE_UN_BIND_DEVICE);
            }
        }
    }
    private boolean isClickAddDevice=false;
    @OnClick({R.id.rechargeTextView,
            R.id.activateRelativeLayout,
            R.id.billtv,
            R.id.addDeviceRelativeLayout,
            R.id.tv_setting,
            R.id.rl_people_center,
            R.id.permission_set,
            R.id.unBindTextView,
            R.id.going_buy
    })

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.activateRelativeLayout:
                //友盟方法统计
                MobclickAgent.onEvent(getActivity(), CLICKMYPACKAGE);
                if (!hasPackage) {

                    intent = new Intent(getActivity(), PackageMarketActivity.class);
                    intent.putExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE, Constant.SHOW);
                } else {
                    intent = new Intent(getActivity(), PackageCategoryActivity.class);

                    if (tvNewPackagetAction.getVisibility() == View.VISIBLE) {
                        mHandler.sendEmptyMessage(2);
                    }
                }

                //记录点击状态
                AppMode.getInstance().isClickPackage = true;
                break;
            case R.id.addDeviceRelativeLayout:
                if(CommonTools.isFastDoubleClick(1000)){
                    return ;
                }
                //友盟方法统计
                MobclickAgent.onEvent(getActivity(), CLICKMYDEVICE);
                if (deviceSummarizedRelativeLayout.getVisibility() == View.GONE) {
                    BluetoothConstant.IS_BIND = false;
                }
                if (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))) {
                    intent = new Intent(getActivity(), ChoiceDeviceTypeActivity.class);
                } else {
                    String braceletName = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
                    //如果设备名没有就设置成爱小器钥匙扣
                    if (TextUtils.isEmpty(braceletName)) {
                        if(NetworkUtils.isNetworkAvailable(getActivity())){
                            isClickAddDevice=true;
                            getDeviceType();
                        }else{
                            CommonTools.showShortToast(getActivity(),getActivity().getString(R.string.no_wifi));
                        }
                        return;
                    }
                    intent= toActivity(intent,braceletName);
                }
                break;
            case R.id.permission_set:
                intent = new Intent(getActivity(), ImportantAuthorityActivity.class);
                break;
            case R.id.tv_setting:
                //友盟方法统计
                MobclickAgent.onEvent(getActivity(), CLICKSET);
                intent = new Intent(getActivity(), SettingActivity.class);
                break;
            case R.id.rl_people_center:
                //友盟方法统计
                MobclickAgent.onEvent(getActivity(), CLICKENTERPERSONCENTER);
                intent = new Intent(getActivity(), PersonalCenterActivity.class);
                break;
            case R.id.billtv:
                //友盟方法统计
                MobclickAgent.onEvent(getActivity(), CLICKBALANCE);
                intent = new Intent(getActivity(), BalanceParticularsActivity.class);
                break;
            case R.id.rechargeTextView:
                //友盟方法统计
                MobclickAgent.onEvent(getActivity(), CLICKRECHARGE);
                intent = new Intent(getActivity(), RechargeActivity.class);
                break;
            case R.id.unBindTextView:
                showDialog();
                return;
            case R.id.going_buy:
                intent = new Intent(getActivity(), PackageMarketActivity.class);
                intent.putExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE, Constant.SHOW);
                break;
        }
        getActivity().startActivity(intent);

    }
    private Intent toActivity(Intent intent,String braceletName){
        intent = new Intent(getActivity(), MyDeviceActivity.class);
        AppMode.getInstance().isClickAddDevice = true;
        mHandler.sendEmptyMessage(4);

        int status = R.string.index_connecting;
        if (getActivity().getResources().getString(R.string.index_no_signal).equals(getBleStatus())) {
            status = R.string.index_no_signal;
        } else if (getActivity().getResources().getString(R.string.index_connecting).equals(getBleStatus())) {
            status = R.string.index_connecting;
        } else if (getActivity().getResources().getString(R.string.index_high_signal).equals(getBleStatus())) {
            status = R.string.index_high_signal;
        } else if (getActivity().getResources().getString(R.string.index_no_packet).equals(getBleStatus())) {
            status = R.string.index_no_packet;
        } else if (getString(R.string.index_un_insert_card).equals(getBleStatus())) {
            status = R.string.index_un_insert_card;
        } else if (getString(R.string.index_high_signal).equals(getBleStatus())) {
            status = R.string.index_high_signal;
        } else if (getString(R.string.index_registing).equals(getBleStatus())) {
            status = R.string.index_registing;
        } else if (getString(R.string.index_aixiaoqicard).equals(getBleStatus())) {
            status = R.string.index_aixiaoqicard;
        } else if (getString(R.string.index_regist_fail).equals(getBleStatus())) {
            status = R.string.index_regist_fail;
        }

        intent.putExtra(BRACELETTYPE, braceletName);
        intent.putExtra(MyDeviceActivity.BLUESTATUSFROMPROMAIN, getString(status));
        return intent;
    }


    @Override
    public void rightComplete(int cmdType, CommonHttp object) {

        if (cmdType == HttpConfigUrl.COMTYPE_GET_BALANCE) {
            BalanceHttp http = (BalanceHttp) object;
            if (http.getBalanceEntity() != null)
                balanceTextView.setText(ICSOpenVPNApplication.getInstance().getString(R.string.balance) + ": " + http.getBalanceEntity().getAmount()
                        + ICSOpenVPNApplication.getInstance().getString(R.string.yuan));
        } else if (cmdType == HttpConfigUrl.COMTYPE_UN_BIND_DEVICE) {
            if (object.getStatus() == 1) {
                SharedUtils.getInstance().delete(BRACELETPOWER);
                SharedUtils.getInstance().delete(Constant.IMEI);
                SharedUtils.getInstance().delete(BRACELETNAME);
                SharedUtils.getInstance().delete(Constant.BRACELETVERSION);
                BluetoothConstant.IS_BIND = false;
                //判断是否再次重连的标记
                ICSOpenVPNApplication.isConnect = false;
                ReceiveBLEMoveReceiver.isConnect = false;
                // 注册失败不显示
                EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL_INITIATIVE);

                sendEventBusChangeBluetoothStatus(getString(R.string.index_unbind));
                CommonTools.showShortToast(getActivity(), "已解绑设备");
                ICSOpenVPNApplication.uartService.disconnect();
                showDeviceSummarized(false);
            } else {
                CommonTools.showShortToast(getActivity(), object.getMsg());
                Log.i(TAG, object.getMsg());
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_GET_USER_ORDER_USAGE_REMAINING) {

            if (object.getStatus() == 1) {
                OrderUsageRemainHttp orderUsageRemainHttp = (OrderUsageRemainHttp) object;
                UsageRemainEntity.Unactivated unactivated = orderUsageRemainHttp.getUsageRemainEntity().getUnactivated();
                UsageRemainEntity.Used used = orderUsageRemainHttp.getUsageRemainEntity().getUsed();
                if (used == null) {
                    return;
                }
                if ("0".equals(used.getTotalNum()) && !"0".equals(unactivated.getTotalNumFlow()) && "0".equals(used.getTotalNumFlow())) {//有套餐，未激活
                    if (!AppMode.getInstance().isClickPackage)
                        mHandler.sendEmptyMessage(1);
                    hasPackage = true;
                    PacketRelativeLayout.setVisibility(View.GONE);
                    noPacketRelativeLayout.setVisibility(View.VISIBLE);
                    Drawable drawable = getResources().getDrawable(R.drawable.activate_device_account);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    addOrActivatePackage.setCompoundDrawables(drawable, null, null, null);
                    addOrActivatePackage.setText(getString(R.string.activate_packet));
                } else if ("0".equals(used.getTotalNum()) && "0".equals(unactivated.getTotalNumFlow())) {//无套餐显示
                    mHandler.sendEmptyMessage(2);
                    hasPackage = false;
                    PacketRelativeLayout.setVisibility(View.GONE);
                    noPacketRelativeLayout.setVisibility(View.VISIBLE);
                    Drawable drawable = getResources().getDrawable(R.drawable.add_device);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    addOrActivatePackage.setCompoundDrawables(drawable, null, null, null);
                    addOrActivatePackage.setText(getString(R.string.add_package));
                } else {//有套餐且激活了。
                    hasPackage = true;
                    PacketRelativeLayout.setVisibility(View.VISIBLE);
                    noPacketRelativeLayout.setVisibility(View.GONE);
                    callTime.setText(used.getTotalRemainingCallMinutes() + "分");

                    //显示出有未激活套餐的提示
                    if (!"0".equals(unactivated.getTotalNumFlow()) && !AppMode.getInstance().isClickPackage) {
                        mHandler.sendEmptyMessage(1);
                    } else {
                        mHandler.sendEmptyMessage(2);
                    }
                    if ("0".equals(used.getTotalNumFlow())) {
                        flow.setText(getString(R.string.no_flow_count));
                        flowCount.setText(unactivated.getTotalNumFlow());
                    } else {
                        flow.setText(getString(R.string.flow_count));
                        flowCount.setText(used.getTotalNumFlow());
                    }
                    packageAllCount.setText(used.getTotalNum());
                }
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_GET_BIND_DEVICE) {
            GetBindDeviceHttp getBindDeviceHttp = (GetBindDeviceHttp) object;
            if (object.getStatus() == 1) {
                if (getBindDeviceHttp.getBlueToothDeviceEntityity() != null) {
                    if (!TextUtils.isEmpty(getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI())) {
                        mHandler.sendEmptyMessage(5);
                        SharedUtils.getInstance().writeString(Constant.IMEI, getBindDeviceHttp.getBlueToothDeviceEntityity().getIMEI());
                        String        deviceTypeStr = getBindDeviceHttp.getBlueToothDeviceEntityity().getDeviceType();
                        if("0".equals(deviceTypeStr)){
                            SharedUtils.getInstance().writeString(Constant.BRACELETNAME,MyDeviceActivity.UNITOYS);
                        }else if("1".equals(deviceTypeStr)){
                            SharedUtils.getInstance().writeString(Constant.BRACELETNAME,MyDeviceActivity.UNIBOX);
                        }
                        if(isClickAddDevice){
                            Intent intent=null;
                            intent=   toActivity(intent, SharedUtils.getInstance().readString(Constant.BRACELETNAME) );
                            startActivity(intent);
                        }

                    } else {
                        mHandler.sendEmptyMessage(6);
                    }
                }
            }
            isClickAddDevice=false;
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        CommonTools.showShortToast(getActivity(), errorMessage);
        isClickAddDevice=false;
    }

    @Override
    public void noNet() {
        CommonTools.showShortToast(getActivity(), getResources().getString(R.string.no_wifi));
    }


    public String getBleStatus() {
        return bleStatus;
    }

    public void setBleStatus(String bleStatus) {
        this.bleStatus = bleStatus;
        if (isAdded()) {
            if (getString(R.string.index_un_insert_card).equals(bleStatus)
                    || getString(R.string.index_unconnect).equals(bleStatus)
                    || getString(R.string.index_registing).equals(bleStatus)) {
                setRegisted(false);
            } else if (getString(R.string.index_aixiaoqicard).equals(bleStatus)) {
                operatorTextView.setText(getString(R.string.unitoy_card));
            }
        }
    }

    /**
     * 修改蓝牙连接状态，通过EVENTBUS发送到各个页面。
     */
    private void sendEventBusChangeBluetoothStatus(String status) {
        int statusDrawable = R.drawable.index_connecting;
        if (status.equals(getString(R.string.index_connecting))) {
        } else if (status.equals(getString(R.string.index_aixiaoqicard))) {
            statusDrawable = R.drawable.index_no_signal;
        } else if (status.equals(getString(R.string.index_no_signal))) {
            statusDrawable = R.drawable.index_no_signal;
        } else if (status.equals(getString(R.string.index_regist_fail))) {
            statusDrawable = R.drawable.index_no_signal;
        } else if (status.equals(getString(R.string.index_registing))) {
            statusDrawable = R.drawable.index_no_signal;
        } else if (status.equals(getString(R.string.index_unbind))) {
            statusDrawable = R.drawable.index_unbind;
        } else if (status.equals(getString(R.string.index_no_packet))) {
            statusDrawable = R.drawable.index_no_packet;
        } else if (status.equals(getString(R.string.index_un_insert_card))) {
            statusDrawable = R.drawable.index_no_signal;
        } else if (status.equals(getString(R.string.index_high_signal))) {
            statusDrawable = R.drawable.index_high_signal;
        } else if (status.equals(getString(R.string.index_blue_un_opne))) {
            statusDrawable = R.drawable.index_blue_unpen;
        }
        EventBusUtil.changeConnectStatus(status, statusDrawable);
    }

    public final String NoticSign = "flg";

    public BroadcastReceiver mNoticBroadCastReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean flg = intent.getBooleanExtra(NoticSign, false);
            if (flg)
                mHandler.sendEmptyMessage(3);
            else
                mHandler.sendEmptyMessage(4);

            Log.d(TAG, "onReceive: " + flg);
        }

    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppMode.getInstance().isClickPackage = false;
        AppMode.getInstance().isClickAddDevice = false;
        tvNewVersion = null;
        tvNewPackagetAction = null;
        //null.unbind();
    }
}
