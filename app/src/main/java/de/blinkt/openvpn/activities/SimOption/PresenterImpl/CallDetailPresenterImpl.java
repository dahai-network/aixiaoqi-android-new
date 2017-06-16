package de.blinkt.openvpn.activities.SimOption.PresenterImpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.CallRecordAdapter;
import cn.com.johnson.model.OnlyCallModel;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.SimOption.ModelImpl.AddBlackListModelImpl;
import de.blinkt.openvpn.activities.SimOption.ModelImpl.DeleteBlackListModelImpl;
import de.blinkt.openvpn.activities.SimOption.Presenter.CallDetailPresenter;
import de.blinkt.openvpn.activities.SimOption.View.CallDetailView;
import de.blinkt.openvpn.activities.SimOption.ui.CallPhoneNewActivity;
import de.blinkt.openvpn.activities.SimOption.ui.SMSAcivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.database.BlackListDBHelp;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl.MaxPhoneCallTimeModelImpl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.OnlyCallHttp;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.model.ContactRecodeEntity;
import de.blinkt.openvpn.model.SmsEntity;
import de.blinkt.openvpn.util.PhoneFormatUtil;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.querylocaldatebase.AsyncQueryContactRecodeHandler;
import de.blinkt.openvpn.util.querylocaldatebase.FindContactUtil;
import de.blinkt.openvpn.util.querylocaldatebase.QueryCompleteListener;
import static de.blinkt.openvpn.constant.Constant.NETWORK_CELL_PHONE;
import static de.blinkt.openvpn.constant.Constant.SIM_CELL_PHONE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTDETAILCALL;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class CallDetailPresenterImpl extends NetPresenterBaseImpl implements CallDetailPresenter,QueryCompleteListener<ContactRecodeEntity> {
    CallDetailView callDetailView;
    AddBlackListModelImpl addBlackListModel;
    DeleteBlackListModelImpl deleteBlackListModel;
    MaxPhoneCallTimeModelImpl maxPhoneCallTimeModel;
    public static String PHONE_INFO = "phone_info";
    private  ContactBean contactBean;
    private   Context context;
    private   BlackListDBHelp blackListDBHelp;
    private boolean isBlackList;
    AsyncQueryContactRecodeHandler asyncQueryContactRecodeHandler;
    private int page = 0;

    public CallRecordAdapter getCallRecordAdapter() {
        return callRecordAdapter;
    }

    CallRecordAdapter callRecordAdapter;
    List<ContactRecodeEntity> list = new ArrayList<>();
    public boolean isBlackList() {
        return isBlackList;
    }

    public boolean isExist() {
        return isExist;
    }

    private boolean isExist;
    public CallDetailPresenterImpl(CallDetailView callDetailView,Context context){
        this.context=context;
        this.callDetailView=callDetailView;
        addBlackListModel=new AddBlackListModelImpl(this);
        deleteBlackListModel=new DeleteBlackListModelImpl(this);
        maxPhoneCallTimeModel=new MaxPhoneCallTimeModelImpl(this);
        initAdapter();
    }

    public void sendSms(){
        SmsEntity smsEntity = new SmsEntity();
        smsEntity.setFm(SharedUtils.getInstance().readString(Constant.USER_NAME));
        smsEntity.setTo(contactBean.getPhoneNum());
        smsEntity.setRealName(contactBean.getDesplayName());
        Intent intent= new Intent(context, SMSAcivity.class);
        intent.putExtra(IntentPutKeyConstant.SMS_LIST_KEY, smsEntity);
        context.startActivity(intent);
    }

    public void callPhone(){
        ContactRecodeEntity contactRecodeEntity = new ContactRecodeEntity();
        contactRecodeEntity.setPhoneNumber(PhoneFormatUtil.deleteprefix("-", contactBean.getPhoneNum()));
        contactRecodeEntity.setName(contactBean.getDesplayName());
        Intent intent = new Intent(context, CallPhoneNewActivity.class);
        intent.putExtra(IntentPutKeyConstant.DATA_CALLINFO, contactRecodeEntity);
        intent.putExtra(IntentPutKeyConstant.CELL_PHONE_TYPE, SIM_CELL_PHONE);
        context.startActivity(intent);
    }

    private void  initAdapter(){
        callRecordAdapter = new CallRecordAdapter(context, list);
    }

    private void getCallRecordData() {
        asyncQueryContactRecodeHandler = new AsyncQueryContactRecodeHandler(this, context.getContentResolver(), true);
        queryContactRecoder();
    }
    public void queryContactRecoder() {
        FindContactUtil.queryContactRecoderData(asyncQueryContactRecodeHandler, contactBean.getPhoneNum(), page*10+ "");
        page++;
    }
    List<ContactRecodeEntity> mAllList = new ArrayList<>();

    @Override
    public void queryComplete(List<ContactRecodeEntity> mAllLists) {
        callDetailView.loadMoreComplete();
        if (mAllLists.size() < 10) {
            callDetailView.noMoreLoading();
        }
        mAllList.addAll(mAllLists);
        if (mAllList.size() == 0) {
            callDetailView.callRecordRvIsVisible(View.GONE);
        } else {
            callDetailView.callRecordRvIsVisible(View.VISIBLE);
        }
        if (mAllLists.size() >= 1)
            callDetailView.lastCallTimeText(mAllList.get(0).getData());
        callRecordAdapter.addAll(mAllList);
        mAllLists.clear();

    }


    private void isContactExist(String phoneNumber) {

        for (ContactBean contactBean : ICSOpenVPNApplication.getInstance().getContactList()) {
            if (contactBean.getPhoneNum().equals(phoneNumber)) {
                this.contactBean = contactBean;
                isExist = true;
                break;
            }
        }

    }
    /**
     * 从不同界面过来，出不同的类，转化为同一个类处理
     */
    public void initData(){
        ContactRecodeEntity phoneInfo = (ContactRecodeEntity) ((Activity)context).getIntent().getSerializableExtra(PHONE_INFO);
        contactBean = (ContactBean) ((Activity)context).getIntent().getSerializableExtra("contactBean");
        if (phoneInfo != null) {
            isContactExist(phoneInfo.getPhoneNumber());
        } else if (contactBean != null) {
            isContactExist(contactBean.getPhoneNum());
        }
        if (contactBean == null && phoneInfo != null) {
            contactBean = new ContactBean();
            contactBean.setPhoneNum(phoneInfo.getPhoneNumber());
            contactBean.setDesplayName(phoneInfo.getName());
        }
        if (!TextUtils.isEmpty(contactBean.getDesplayName()))
            callDetailView.setUserNameText(contactBean.getDesplayName());
        else {
            callDetailView.setUserNameVisible(View.GONE);
        }
        callDetailView.setPhoneNameText(contactBean.getPhoneNum());
        if (blackListDBHelp == null)
            blackListDBHelp = new BlackListDBHelp(context);
        isBlackList = blackListDBHelp.isBlackList(contactBean.getPhoneNum());
        callDetailView.setBlackList();
        getCallRecordData();
    }

public void clickRight(){
    if (isExist) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        //需要获取到数据库contacts表中lookup列中的key值，在上面遍历contacts集合时获取到
        Uri data = ContactsContract.Contacts.getLookupUri(contactBean.getContactId(), contactBean.getLookUpKey());
        intent.setDataAndType(data, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        intent.putExtra("finishActivityOnSaveCompleted", true);
        context.startActivity(intent);
    } else {
        Intent addIntent = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
        addIntent.setType("vnd.android.cursor.dir/person");
        addIntent.setType("vnd.android.cursor.dir/contact");
        addIntent.setType("vnd.android.cursor.dir/raw_contact");
        addIntent.putExtra(ContactsContract.Intents.Insert.PHONE, contactBean.getPhoneNum());
        context.startActivity(addIntent);
    }
}


    @Override
    public void requestAddBlackList() {
        addBlackListModel.requestAddBlackList(contactBean.getPhoneNum());

    }

    @Override
    public void requestDeleteBlackList() {
        deleteBlackListModel.requestDeleteBlackList(contactBean.getPhoneNum());
    }

    @Override
    public void requestMaxPhoneCallTime() {
        maxPhoneCallTimeModel.requestMaxPhoneCallTime();
    }


    @Override
    public void rightLoad(int cmdType, CommonHttp object) {

        if (cmdType == HttpConfigUrl.COMTYPE_GET_MAX_PHONE_CALL_TIME) {
            //友盟方法统计
            MobclickAgent.onEvent(context, CLICKCONTACTDETAILCALL);
            OnlyCallHttp onlyCallHttp = (OnlyCallHttp) object;
            if (1 == onlyCallHttp.getStatus()) {
                OnlyCallModel onlyCallModel = onlyCallHttp.getOnlyCallModel();
                if (!onlyCallModel.getMaximumPhoneCallTime().equals("0")) {
                    ContactRecodeEntity contactRecodeEntity = new ContactRecodeEntity();
                    contactRecodeEntity.setPhoneNumber(PhoneFormatUtil.deleteprefix("-", contactBean.getPhoneNum()));
                    contactRecodeEntity.setName(contactBean.getDesplayName());
                    Intent intent = new Intent(context, CallPhoneNewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(IntentPutKeyConstant.DATA_CALLINFO, contactRecodeEntity);
                    intent.putExtra(IntentPutKeyConstant.CELL_PHONE_TYPE, NETWORK_CELL_PHONE);
                    intent.putExtra(IntentPutKeyConstant.MAXINUM_PHONE_CALL_TIME, onlyCallModel.getMaximumPhoneCallTime());
                    context.startActivity(intent);
                } else {
                    callDetailView.noCallTime();
                }
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_BLACK_LIST_ADD) {
            if (object.getStatus() == 1) {
                callDetailView.showToast(R.string.black_add_success);
                isBlackList = !isBlackList;
                blackListDBHelp.insertOneDefriend(contactBean.getPhoneNum());
                callDetailView.setBlackList();
            } else {
                callDetailView.showToast(object.getMsg());
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_BLACK_LIST_DELETE) {
            if (object.getStatus() == 1) {
                callDetailView.showToast(R.string.black_remove_success);
                isBlackList = !isBlackList;
                callDetailView.setBlackList();
                blackListDBHelp.deleteDefriend(contactBean.getPhoneNum());
            } else {
                Log.d(TAG, "rightComplete: " + object.getMsg());
                callDetailView.showToast(object.getMsg());
            }
        }
    }

    @Override
    public void onDestroy() {
        if (blackListDBHelp != null)
            blackListDBHelp.close();
    }
}
