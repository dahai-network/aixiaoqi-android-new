package de.blinkt.openvpn.fragments.ProMainTabFragment.ui;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import cn.com.johnson.model.AppMode;
import cn.com.johnson.model.ChangeViewStateEvent;
import cn.com.johnson.widget.GlideCircleTransform;
import de.blinkt.openvpn.activities.Device.ui.ChoiceDeviceTypeActivity;
import de.blinkt.openvpn.activities.Device.ui.MyDeviceActivity;
import de.blinkt.openvpn.activities.FreeWorryPacketChoiceActivity;
import de.blinkt.openvpn.activities.MyModules.ui.BalanceParticularsActivity;
import de.blinkt.openvpn.activities.MyModules.ui.ImportantAuthorityActivity;
import de.blinkt.openvpn.activities.MyModules.ui.PackageCategoryActivity;
import de.blinkt.openvpn.activities.MyModules.ui.PackageMarketActivity;
import de.blinkt.openvpn.activities.MyModules.ui.RechargeActivity;
import de.blinkt.openvpn.activities.PersonalCenterActivity;
import de.blinkt.openvpn.activities.Set.ui.SettingActivity;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.activities.StartUpHomePageActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.ProMainTabFragment.PresenterImpl.AccountPresenterImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.View.AccountView;
import de.blinkt.openvpn.fragments.base.BaseStatusFragment;
import de.blinkt.openvpn.model.enentbus.BindStatue;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.views.TitleBar;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import static android.view.View.GONE;
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
public class AccountFragment extends BaseStatusFragment implements AccountView, DialogInterfaceTypeBase {
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
    @BindView(R.id.serviceTextView)
    TextView serviceTextView;
    @BindView(R.id.add_or_activate_package_iv)
    ImageView addOrActivatePackageIv;
    @BindView(R.id.tv_new_packaget_action)
    ImageView tvNewPackagetAction;
    @BindView(R.id.tv_new_version)
    ImageView tvNewVersion;
    //bluetooth status蓝牙状态
    private String TAG = "AccountFragment";
    private final static int SIGN_MSG_ONE = 1;
    private final static int SIGN_MSG_TWO = 2;
    private final static int SIGN_MSG_THREE = 3;
    private final static int SIGN_MSG_FOUR = 4;
    private boolean isNewVersion;
    private boolean isNewPackage;
    AccountPresenterImpl accountPresenterImpl;
    Unbinder unbinder;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SIGN_MSG_ONE:
                    if (tvNewPackagetAction != null) {
                        setNewPackage(true);
                    }
                    break;
                case SIGN_MSG_TWO:
                    if (tvNewPackagetAction != null) {
                        setNewPackage(false);
                    }
                    break;
                case SIGN_MSG_THREE:
                    if (tvNewVersion != null && !AppMode.getInstance().isClickAddDevice) {
                        setNewVersion(true);
                    }
                    break;
                case SIGN_MSG_FOUR:
                    if (tvNewVersion != null) {
                        setNewVersion(false);
                    }
                    break;
            }
            EventBus.getDefault().post(new ChangeViewStateEvent(isNewVersion, isNewPackage));
        }
    };

    @Override
    public void setServiceText(String textContent) {
        serviceTextView.setText(textContent);
    }

    @Override
    public void showPackage(int hasPackageVisible, int noPackageVisible) {
        PacketRelativeLayout.setVisibility(hasPackageVisible);
        noPacketRelativeLayout.setVisibility(noPackageVisible);
    }

    @Override
    public void addOrActivatePackageIvAndText(int drawableId, int textId) {
        addOrActivatePackageIv.setImageResource(drawableId);
        addOrActivatePackage.setText(getString(textId));
    }

    @Override
    public void updateRedDot(int isVisiable) {
        mHandler.sendEmptyMessage(isVisiable);
    }

    @Override
    public void activatePackage(int activateStatue, String activateCount) {
        flow.setText(getString(activateStatue));
        flowCount.setText(activateCount);
    }

    @Override
    public void packageAllCount(String allCount) {
        packageAllCount.setText(allCount);
    }

    @Override
    public void callTime(String time) {
        callTime.setText(time);
    }

    @Override
    public void toMyDeviceActivity() {
        Intent intent = getIntent();
        startActivity(intent);
    }

    @Override
    public void toActivity( ) {
        Intent intent=new Intent(getActivity(),ChoiceDeviceTypeActivity.class);
        startActivity(intent);
    }

    @Override
    public void setBalanceText(float balanceCount) {
        balanceTextView.setText(ICSOpenVPNApplication.getInstance().getString(R.string.balance) + ": " + balanceCount
                + ICSOpenVPNApplication.getInstance().getString(R.string.yuan));
    }

    @Override
    public void showToast(String toastContent) {
        CommonTools.showShortToast(getActivity(),toastContent);
    }

    @Override
    public void showToast(int toastId) {
        CommonTools.showShortToast(getActivity(),getString(toastId));
    }
    /**
     * 设备布局
     *
     * @param isShow
     */
    @Override
    public void showDeviceSummarized(boolean isShow) {
                if (deviceSummarizedRelativeLayout != null) {
            deviceSummarizedRelativeLayout.setVisibility(isShow?View.VISIBLE:GONE);
        }
    }

    private void setNewPackage(boolean isNewPackage){
        this.isNewPackage=isNewPackage;
        tvNewPackagetAction.setVisibility(isNewPackage?View.VISIBLE:GONE);
    }

    private void setNewVersion(boolean isNewVersion){
        this.isNewVersion=isNewVersion;
        tvNewVersion.setVisibility(isNewVersion?View.VISIBLE:GONE);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Glide.get(getActivity()).clearMemory();
        setLayoutId(R.layout.fragment_account);
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        topProgressView.setWhiteBack(true);
        accountPresenterImpl=new AccountPresenterImpl(this,getActivity());
        title.setTextTitle(getString(R.string.personal_center));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取数据，每次都重新获取一次以保持正确性。
        getData();
        accountPresenterImpl.requestUserPackage();
//        if(!getString(R.string.remove_bind).equals(unBind))
//        getDeviceType();
//        else{
//            showDeviceSummarized(false);
//        }
        if(getString(R.string.index_high_signal).equals(bleStatus)){
            setRegisted(true);
        }else{
            setRegisted(false);
        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bindStatue(BindStatue bindStatue) {
        Log.e(TAG,"bindStatue="+bindStatue.getBindStatues());
        if(bindStatue.getBindStatues()==0){
            showDeviceSummarized(false);
        }else if(bindStatue.getBindStatues()==1){
            getDeviceType();
        }
    }

    private void getDeviceType() {
        if (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI)) || TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.BRACELETNAME))) {
            accountPresenterImpl.requestGetBindInfo();
        } else {
            showDeviceSummarized(true);
            setDeviceType();
            setPowerPercent();
            showRedDot();
        }
    }

    private void showRedDot(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showAddDeviceRed();
            }
        },200);

    }

    private void showAddDeviceRed() {
        Log.e(TAG,"isClickAddDevice="+AppMode.getInstance().isClickAddDevice);
        if (!AppMode.getInstance().isClickAddDevice && SharedUtils.getInstance().readBoolean(Constant.HAS_DEVICE_NEED_UPGRADE)) {
            updateVersionRedDot(SIGN_MSG_THREE);
        } else {
            updateVersionRedDot(SIGN_MSG_FOUR);
        }
    }

    @Override
    public void setDeviceType() {
        String deviceType = SharedUtils.getInstance().readString(Constant.BRACELETNAME);
        String typeText = "";
        if (!TextUtils.isEmpty(deviceType)) {
            //0是手环，1是钥匙扣
            if (deviceType.contains(Constant.UNITOYS)) {
                typeText = getString(R.string.device) + ": " + getString(R.string.unitoy);
            } else if (deviceType.contains(Constant.UNIBOX)) {
                typeText = getString(R.string.device) + ": " + getString(R.string.unibox_key);
            }
            showDeviceType(typeText);
        }
    }
    private void showDeviceType(String deviceType) {
        if (!TextUtils.isEmpty(deviceType))
            deviceNameTextView.setText(deviceType);
    }

    //显示电量
    public void setPowerPercent() {
        if (SharedUtils.getInstance().readInt(Constant.BRACELETPOWER) != 0)
            powerTextView.setText(SharedUtils.getInstance().readInt(Constant.BRACELETPOWER) + "%");
    }

    //控制注册信息
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
            //判断是否是爱小器卡
            if (getString(R.string.index_aixiaoqicard).equals(bleStatus)) {
                operatorTextView.setText(getString(R.string.unitoy_card));
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
        accountPresenterImpl.requestBalance(); //余额

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
                accountPresenterImpl.requestUnbindDevice();//解除绑定
            }
        }
    }

    @OnClick({R.id.rechargeTextView,
            R.id.activateRelativeLayout,
            R.id.billtv,
            R.id.addDeviceRelativeLayout,
            R.id.tv_setting,
            R.id.rl_people_center,
            R.id.permission_set,
            R.id.unBindTextView,
            R.id.going_buy,
            R.id.serviceTextView
    })

    public void onClick(View v) {
        Intent intent = null;
        boolean flg=false;
        switch (v.getId()) {
            case R.id.activateRelativeLayout:
                intent = activateClick();
                break;
            case R.id.addDeviceRelativeLayout:
                if (CommonTools.isFastDoubleClick(1000)) {
                    return;
                }
                //友盟方法统计
                MobclickAgent.onEvent(getActivity(), CLICKMYDEVICE);
                    if(!accountPresenterImpl.canClick()){
                        return;
                    }
                    intent = getIntent();
                break;
            case R.id.permission_set:
                requestSomePermission();
                flg=true;
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
                intent.putExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE, Constant.HIDDEN);
                break;
            case R.id.serviceTextView:
                if ("---".equals(serviceTextView.getText().toString())) {
                    intent = new Intent(getActivity(), FreeWorryPacketChoiceActivity.class);
                } else {
                    MyOrderDetailActivity.launch(getActivity(), accountPresenterImpl.getServiceOrderId());
                    return;
                }
                break;
        }
if(!flg){
    getActivity().startActivity(intent);
}


    }

    private void requestSomePermission() {
        // 先判断是否有权限。
        if (!AndPermission.hasPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ) {
            // 申请权限。
            AndPermission.with(AccountFragment.this)
                    .requestCode(100)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .send();
        }else{
            Intent  intent = new Intent(getActivity(), ImportantAuthorityActivity.class);
            getActivity().startActivity(intent);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 只需要调用这一句，其它的交给AndPermission吧，最后一个参数是PermissionListener。
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, listener);
    }
    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantPermissions) {
            Intent intent = new Intent(getActivity(), ImportantAuthorityActivity.class);
            getActivity().startActivity(intent);
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            CommonTools.showShortToast(getActivity(),"权限申请失败,请打开此权限，否则app不能正常运行");
        }
    };

    @NonNull
    private Intent activateClick() {
        Intent intent;//友盟方法统计
        MobclickAgent.onEvent(getActivity(), CLICKMYPACKAGE);
        if (!accountPresenterImpl.isHasPackage()) {
            intent = new Intent(getActivity(), PackageMarketActivity.class);
            intent.putExtra(IntentPutKeyConstant.CONTROL_CALL_PACKAGE, Constant.HIDDEN);
        } else {
            intent = new Intent(getActivity(), PackageCategoryActivity.class);
            if (tvNewPackagetAction.getVisibility() == View.VISIBLE) {
                mHandler.sendEmptyMessage(SIGN_MSG_TWO);
            }
        }
        //记录点击状态
        AppMode.getInstance().isClickPackage = true;
        return intent;
    }

    private Intent getIntent( ) {
     Intent  intent = new Intent(getActivity(), MyDeviceActivity.class);
        AppMode.getInstance().isClickAddDevice = true;
        mHandler.sendEmptyMessage(SIGN_MSG_FOUR);
        Log.e(TAG, "bleStatus" + bleStatus);
        return intent;
    }

    @Override
    protected void setBleStatus(String bleStatus) {
        Log.i(TAG, "bleStatus=" + bleStatus);
        if (isAdded()) {
            if (getString(R.string.index_aixiaoqicard).equals(bleStatus)) {
                operatorTextView.setText(getString(R.string.unitoy_card));
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.e("isClickAddDevice","isClickAddDevice="+AppMode.getInstance().isClickAddDevice);
            showAddDeviceRed();
        }
    }

    private void updateVersionRedDot(int signMsgThree) {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(signMsgThree);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AppMode.getInstance().isClickPackage = false;
        AppMode.getInstance().isClickAddDevice = false;
        unbinder.unbind();
    }
}
