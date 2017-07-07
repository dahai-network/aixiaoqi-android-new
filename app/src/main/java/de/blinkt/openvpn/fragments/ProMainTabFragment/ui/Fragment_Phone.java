package de.blinkt.openvpn.fragments.ProMainTabFragment.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.SocketConstant;
import com.yanzhenjie.permission.AndPermission;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import cn.com.johnson.model.OnlyCallModel;
import de.blinkt.openvpn.activities.FreeWorryPacketChoiceActivity;
import de.blinkt.openvpn.activities.MyModules.ui.RechargeActivity;
import de.blinkt.openvpn.activities.SimOption.PresenterImpl.CallDetailPresenterImpl;
import de.blinkt.openvpn.activities.SimOption.ui.CallDetailActivity;
import de.blinkt.openvpn.activities.SimOption.ui.CallPhoneNewActivity;
import de.blinkt.openvpn.activities.StartUpHomePageActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.fragments.ProMainTabFragment.PresenterImpl.PhoneRedocerPresenterImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.View.PhoneView;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.InterfaceCallback;
import de.blinkt.openvpn.http.OnlyCallHttp;
import de.blinkt.openvpn.model.ContactRecodeEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SetPermission;
import de.blinkt.openvpn.util.querylocaldatebase.SearchConnectterHelper;
import de.blinkt.openvpn.views.T9TelephoneDialpadView;
import de.blinkt.openvpn.views.dialog.DialogBalance;
import de.blinkt.openvpn.views.dialog.DialogInterfaceTypeBase;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static de.blinkt.openvpn.constant.Constant.NETWORK_CELL_PHONE;
import static de.blinkt.openvpn.constant.Constant.SIM_CELL_PHONE;


public class Fragment_Phone extends Fragment implements InterfaceCallback, T9TelephoneDialpadView.OnT9TelephoneDialpadView, DialogInterfaceTypeBase, T9TelephoneDialpadView.OnControlCallOptionListener, View.OnKeyListener,PhoneView{

    public TextView dial_delete_btn;
    public static int PERMISSION_SET = 1;
    @BindView(R.id.rv_contact_recode)
    RecyclerView rvContactRecode;
    @BindView(R.id.tv_no_permission)
    TextView tvNoPermission;
    @BindView(R.id.jump_permission)
    Button jumpPermission;
    @BindView(R.id.rl_no_permission)
    RelativeLayout rlNoPermission;
    @BindView(R.id.floatingActionButton)
    ImageView floatingActionButton;
    @BindView(R.id.t9dialpadview)
    T9TelephoneDialpadView t9dialpadview;
    Unbinder unbinder;
    PhoneRedocerPresenterImpl phoneRedocerPresenter;

    @Override
    public void showToast(String toastContent) {
        CommonTools.showShortToast(getActivity(),toastContent);
    }

    @Override
    public void showToast(int toastId) {
        CommonTools.showShortToast(getActivity(),getString(toastId));
    }

    @Override
    public void toCallDetailActivity(ContactRecodeEntity contactRecodeEntity) {
        this.contactRecodeEntity=contactRecodeEntity;
        Intent intent = new Intent(getActivity(), CallDetailActivity.class);
        intent.putExtra(CallDetailPresenterImpl.PHONE_INFO, contactRecodeEntity);
        startActivity(intent);
    }

    @Override
    public void rlNoPermission(int isVisiable) {
        rlNoPermission.setVisibility(isVisiable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_phone, container, false);
        if (savedInstanceState != null) {
            String FRAGMENTS_TAG = "Android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        unbinder = ButterKnife.bind(this, rootView);
        phoneRedocerPresenter =new PhoneRedocerPresenterImpl(this,getActivity());
        initView();
        return rootView;
    }

    private void initView() {
        tvNoPermission.setText(String.format(getString(R.string.no_permission), getString(R.string.call_recoder)));
        inited();
    }

    /***
     *手环拨打电话
     */
    private void braceletDial() {
        if (CommonTools.isFastDoubleClick(500)) {
            return;
        }
        if (SocketConstant.REGISTER_STATUE_CODE == 3) {
            //如果没有套餐那么就需要弹出提示框
            //拨打电话
            simCallPhone(contactRecodeEntity);
        } else {
            CommonTools.showShortToast(getActivity(), getString(R.string.sim_register_phone_tip));
        }
    }

    public void closedialClicked() {
        t9dialpadview.clearT9Input();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(mCallLogObserver);
        phoneRedocerPresenter.onDestory();
        t9dialpadview = null;
    }

    //未连上设备
    @OnClick({R.id.jump_permission, R.id.floatingActionButton})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jump_permission:
                new SetPermission(getActivity());
                // 先判断是否有权限。
                break;
            case R.id.floatingActionButton:
                EventBusUtil.optionView(false);
                t9dialpadview.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void inited() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvContactRecode.setLayoutManager(layoutManager);
        rvContactRecode.setAdapter(phoneRedocerPresenter.getContactRecodeAdapter());
        t9dialpadview.setOnT9TelephoneDialpadView(this);
        t9dialpadview.setOnControlCallOptionListener(this);
        rvContactRecode.setOnKeyListener(this);
        dial_delete_btn = t9dialpadview.getDeteleBtn();
        if (dial_delete_btn != null) {
            dial_delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(curInputStr) && curInputStr.length() > 0) {
                        String newCurInputStr = curInputStr.substring(0, curInputStr.length() - 1);
                        t9dialpadview.mT9InputEt.setText(newCurInputStr);
                        onDialInputTextChanged(newCurInputStr);
                    }
                }
            });
        }
        getActivity().getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, mCallLogObserver);
    }
    @Override
    public void onAddDialCharacter(String addCharacter) {
        // TODO Auto-generated method stub
    }


    @Override
    public void onDeleteDialCharacter(String deleteCharacter) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDialInputTextChanging(String curCharacter) {
        if (!TextUtils.isEmpty(curCharacter)) {
            phoneRedocerPresenter.getFilter().filter(curCharacter);
        } else {
            phoneRedocerPresenter.getContactRecodeAdapter().setSearchChar("");
            phoneRedocerPresenter.addDataContactRecodeAdapter();
        }
    }

    /**
     * 监听拨打电话输入文本的变化
     *
     * @param curCharacter
     */
    @Override
    public void onDialInputTextChanged(String curCharacter) {
        // TODO Auto-generated method stub
        //进行逻辑判断
        this.curInputStr = curCharacter;
        EventBusUtil.optionView(curCharacter);
    }

    private String TAG = "Fragment_Phone";

    //对话框
    @Override
    public void dialogText(int type, String text) {
        if (type == 0) {
            Intent intent = new Intent(getActivity(), RechargeActivity.class);
            getActivity().startActivity(intent);
        } else if (type == 1) {
            simCallPhone(contactRecodeEntity);
        } else if (type == 2) {
            Intent intent = new Intent(getActivity(), FreeWorryPacketChoiceActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void simCallPhone(ContactRecodeEntity contactRecodeEntity) {
        this.contactRecodeEntity=contactRecodeEntity;
        CommonTools.delayTime(500);
        Intent intent = new Intent(getActivity(), CallPhoneNewActivity.class);
        intent.putExtra(IntentPutKeyConstant.DATA_CALLINFO, contactRecodeEntity);
        intent.putExtra(IntentPutKeyConstant.CELL_PHONE_TYPE, SIM_CELL_PHONE);
        getActivity().startActivity(intent);
    }
    public String curInputStr;

    //网络请求结果
    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_GET_MAX_PHONE_CALL_TIME) {
            OnlyCallHttp onlyCallHttp = (OnlyCallHttp) object;
            if (1 == onlyCallHttp.getStatus()) {
                OnlyCallModel onlyCallModel = onlyCallHttp.getOnlyCallModel();
                if (!onlyCallModel.getMaximumPhoneCallTime().equals("0")) {
                    Intent intent = new Intent(getActivity(), CallPhoneNewActivity.class);
                    intent.putExtra(IntentPutKeyConstant.DATA_CALLINFO, contactRecodeEntity);
                    intent.putExtra(IntentPutKeyConstant.CELL_PHONE_TYPE, NETWORK_CELL_PHONE);
                    intent.putExtra(IntentPutKeyConstant.MAXINUM_PHONE_CALL_TIME, onlyCallModel.getMaximumPhoneCallTime());
                    getActivity().startActivity(intent);
                } else {
                    new DialogBalance(this, getActivity(), R.layout.dialog_balance, 0);
                }
            } else {
                CommonTools.showShortToast(getActivity(), onlyCallHttp.getMsg());
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {

    }

    @Override
    public void noNet() {
        CommonTools.showShortToast(getActivity(), getResources().getString(R.string.no_wifi));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    int REQUEST_CODE_ASK_PERMISSIONS = 123;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.e(TAG, "isVisibleToUser=" + isVisibleToUser);
        if (!isVisibleToUser) {
            hindT9DiaView();
        }
    }

    private void hindT9DiaView() {
        if (t9dialpadview != null)
            t9dialpadview.setVisibility(View.GONE);
        EventBusUtil.optionView(true);
    }

    //隐藏自定义键盘
    @Override
    public void hideT9() {
        hindT9DiaView();
    }
    ContactRecodeEntity contactRecodeEntity;

    //点击键盘的拨打电话
    @Override
    public void callPhone() {
        int hasWriteContactsPermission = 0;
        int version = Build.VERSION.SDK_INT;
        if (t9dialpadview.getT9Input() != null && t9dialpadview.getT9Input().length() > 0) {
            //检测是否开启读取联系人电话
            if (getActivity() != null) {
                hasWriteContactsPermission = checkSelfPermission(getActivity(), Manifest.permission.WRITE_CONTACTS);
            }
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED && version > 22) {
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
            contactRecodeEntity = new ContactRecodeEntity();
            contactRecodeEntity.setPhoneNumber(t9dialpadview.getT9Input());
            contactRecodeEntity.setName(SearchConnectterHelper.getContactNameByPhoneNumber(getActivity(), contactRecodeEntity.getPhoneNumber()));

            if (!CommonTools.isFastDoubleClick(1000)) {
                braceletDial();
                closedialClicked();
            }
        } else {
            if (!CommonTools.isFastDoubleClick(1000)) {
                CommonTools.showShortToast(getActivity(), "请输入要拨打的电话号码");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_SET) {
            Log.e(TAG,"onActivityResult");
            phoneRedocerPresenter.searchContactRedocer(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        phoneRedocerPresenter.searchContactRedocer(getActivity());
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
                if (t9dialpadview != null && t9dialpadview.getVisibility() == View.VISIBLE) {
                    hindT9DiaView();
                } else {
                    return false;
                }
                return true;    //已处理
            }
        }
        return false;
    }

    //通话记录改变刷新
    // 当通话记录数据库发生更改时触发此操作
    private ContentObserver mCallLogObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // 当通话记录数据库发生更改时触发此操作
            phoneRedocerPresenter.searchContactRedocer(getActivity());
        }

    };
}
