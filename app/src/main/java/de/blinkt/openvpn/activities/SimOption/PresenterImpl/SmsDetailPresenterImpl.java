package de.blinkt.openvpn.activities.SimOption.PresenterImpl;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.RecyclerBaseAdapter;
import cn.com.johnson.adapter.SmsDetailAdapter;
import de.blinkt.openvpn.activities.ContactDetailActivity;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.SimOption.ModelImpl.DeleteSmsModelImpl;
import de.blinkt.openvpn.activities.SimOption.ModelImpl.GetSmsDetailModelImpl;
import de.blinkt.openvpn.activities.SimOption.ModelImpl.OnceSendSmsModelImpl;
import de.blinkt.openvpn.activities.SimOption.ModelImpl.SendSmsMessageModelImpl;
import de.blinkt.openvpn.activities.SimOption.Presenter.SmsDetailPresenter;
import de.blinkt.openvpn.activities.SimOption.View.SmsDetailView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.ProMainTabFragment.PresenterImpl.SmsPresenterImpl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.SendRetryForErrorHttp;
import de.blinkt.openvpn.http.SendSmsHttp;
import de.blinkt.openvpn.http.SmsDetailHttp;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.model.SmsDetailEntity;
import de.blinkt.openvpn.model.SmsEntity;
import de.blinkt.openvpn.util.SharedUtils;
import de.blinkt.openvpn.util.User;

/**
 * Created by Administrator on 2017/6/15 0015.
 */

public class SmsDetailPresenterImpl extends NetPresenterBaseImpl implements SmsDetailPresenter,SmsDetailAdapter.OnItemLongAndResendClickListener,RecyclerBaseAdapter.OnItemClickListener {
    SmsDetailView smsDetailView;
    DeleteSmsModelImpl deleteSmsModel;
    GetSmsDetailModelImpl getSmsDetailModel;
    OnceSendSmsModelImpl onceSendSmsModel;
    SendSmsMessageModelImpl sendSmsMessageModel;
    public static final String SEND_PROGRESSING = "0";
    public SmsDetailAdapter getSmsDetailAdapter() {
        return smsDetailAdapter;
    }
    SmsDetailAdapter smsDetailAdapter;
    Context context;
    List<SmsDetailEntity> list = new ArrayList<>();
    private HashSet<SmsDetailEntity> ids = new HashSet<>();
    List<ContactBean> mAllLists = new ArrayList<>();
    int position;
    int pageNumber = 1;
    SmsEntity smsEntity;
    private boolean isClick;
    public static final String MESSAGE_RECEIVED_ACTION = "com.aixiaoqi.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String SEND_SUCCEED = "1";
    public static final String SEND_FAIL = "2";
    public static final String KEY_EXTRAS = "extras";
    public  SmsDetailPresenterImpl(SmsDetailView smsDetailView, Context context){
        this.context=context;
        this.smsDetailView=smsDetailView;
        mAllLists = ICSOpenVPNApplication.getInstance().getContactList();
        deleteSmsModel=new DeleteSmsModelImpl(this);
        getSmsDetailModel=new GetSmsDetailModelImpl(this);
        onceSendSmsModel=new OnceSendSmsModelImpl(this);
        sendSmsMessageModel=new SendSmsMessageModelImpl(this);
        initSmsEntity();
        initAdapter();
        registerMessageReceiver();
    }

    private void initSmsEntity(){
        Object object = ((Activity)context).getIntent().getSerializableExtra(IntentPutKeyConstant.SMS_LIST_KEY);//从有号码的地方进入发短信界面
        String receivePhoneNumer = ((Activity)context).getIntent().getStringExtra(IntentPutKeyConstant.RECEIVE_SMS);//从通知进来
        if (object != null) {
            smsEntity = (SmsEntity) object;
        } else if (!TextUtils.isEmpty(receivePhoneNumer)) {
            smsEntity = new SmsEntity();
            smsEntity.setFm(receivePhoneNumer);
            smsEntity.setRealName(setRealName(receivePhoneNumer));
        }
    }

    public boolean getDeleteStatue(){
        if(smsDetailAdapter.isDeleteState()){
            clearDeleteOption();
        }
        return smsDetailAdapter.isDeleteState();
    }

   public void  clearDeleteOption(){
       smsDetailAdapter.setDeleteState(false);
       smsDetailView.rlSmsImageViewVisible(View.GONE);
       smsDetailView.llSendSmsVisible(View.VISIBLE);
       smsDetailAdapter.notifyDataSetChanged();
       ids.clear();
    }

    public SmsEntity getSmsEntity() {
        return smsEntity;
    }

    public String setRealName(String phoneNumber) {
      String  realName = phoneNumber;
        int size = mAllLists.size();
        if (mAllLists == null) {
            return realName;
        }
        for (int j = 0; j < size; j++) {
            if (phoneNumber.equals(mAllLists.get(j).getPhoneNum())) {
                realName = mAllLists.get(j).getDesplayName();
                break;
            }
        }
        return realName;
    }

    MessageReceiver mMessageReceiver;

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        context.registerReceiver(mMessageReceiver, filter);
    }

    public void clickRight(){
        Intent intent = new Intent(context, ContactDetailActivity.class);
        ContactBean contactBean = new ContactBean();
        if (User.isCurrentUser(smsEntity.getFm())) {
            contactBean.setPhoneNum(smsEntity.getTo());
        } else {
            contactBean.setPhoneNum(smsEntity.getFm());
        }
        contactBean.setDesplayName(smsEntity.getRealName());
        intent.putExtra("contactBean", contactBean);
        intent.putExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE_DETAIL, IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE_DETAIL);
        context.startActivity(intent);
    }
    public void sendSms(){
        position = -1;
        isClick = true;
        String content =smsDetailView.getSendSmsContent();
        if (TextUtils.isEmpty(content)) {
            smsDetailView.showToast(R.string.send_content_is_null);
            return;
        }
        long sendTime = System.currentTimeMillis();
        SharedUtils sharedUtils = SharedUtils.getInstance();
        SmsDetailEntity smsDetailEntity = new SmsDetailEntity();
        smsDetailEntity.setFm(sharedUtils.readString(Constant.USER_NAME));
        smsDetailEntity.setSend(true);
        smsDetailEntity.setSMSTime(sendTime + "");
        smsDetailEntity.setSMSContent(content);
        smsDetailEntity.setStatus(SEND_PROGRESSING);
        String phoneNumbertemp;
        if (smsEntity != null) {
            if (User.isCurrentUser(smsEntity.getFm())) {
                phoneNumbertemp = smsEntity.getTo();
            } else {
                phoneNumbertemp = smsEntity.getFm();
            }
            smsDetailEntity.setTo(phoneNumbertemp);
            smsDetailAdapter.add(smsDetailEntity);

        } else {
            smsDetailView.combinePhoneNumber();
            if (!TextUtils.isEmpty(smsDetailView.getPhoneNumbers()) && !TextUtils.isEmpty(smsDetailView.getSendSmsPhone())){
                phoneNumbertemp = smsDetailView.getPhoneNumbers() + "," + smsDetailView.getSendSmsPhone();
            smsDetailView.addMap(smsDetailView.getSendSmsPhone(),setRealName(smsDetailView.getSendSmsPhone()));}
            else if (TextUtils.isEmpty(smsDetailView.getPhoneNumbers()) && !TextUtils.isEmpty(smsDetailView.getSendSmsPhone())) {//要往map里边加入
                phoneNumbertemp = smsDetailView.getSendSmsPhone();
                smsDetailView.addMap(phoneNumbertemp,setRealName(phoneNumbertemp));
            }else if(!TextUtils.isEmpty(smsDetailView.getPhoneNumbers())){
                phoneNumbertemp=smsDetailView.getPhoneNumbers();
            }else{
                phoneNumbertemp="";
            }
            if (!TextUtils.isEmpty(phoneNumbertemp)) {
                smsDetailEntity.setTo(phoneNumbertemp);
                smsDetailAdapter.add(smsDetailEntity);
            } else {
                smsDetailView.showToast(R.string.has_no_contact);
                return;
            }
            if (smsEntity == null) {
                smsEntity = new SmsEntity();
            }
            smsEntity.setFm(SharedUtils.getInstance().readString(Constant.USER_NAME));
            smsEntity.setTo(phoneNumbertemp);
            smsDetailView.setTitleBar();
            smsDetailView.setSwipeRefresh();
           smsDetailView.consigneeLl(View.GONE);
        }
        smsDetailView.recyclerViewBottom();
        requestSendSmsMessage(phoneNumbertemp, smsDetailEntity.getSMSContent());
        smsDetailView.setSmsContent("");
    }
    //接收通知改变界面
    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String extras = intent.getStringExtra(KEY_EXTRAS);
                if (!TextUtils.isEmpty(extras)) {
                    JsonObject jsonObject = new JsonParser().parse(extras).getAsJsonObject();
                    String status = jsonObject.get("Status").getAsString();
                    String tel = jsonObject.get("Tel").getAsString();
                    String smsID = jsonObject.get("SMSID").getAsString();
                    if (smsEntity!=null&&smsEntity.getTo().equals(tel)) {
                        List<SmsDetailEntity> list = smsDetailAdapter.getList();
                        int length = list.size();
                        for (int i = 0; i < length; i++) {
                            if (smsID.equals(list.get(i).getSMSID())) {
                                if ("1".equals(status)) {
                                    list.get(i).setStatus(SEND_SUCCEED);
                                } else {
                                    list.get(i).setStatus(SEND_FAIL);
                                }
                                smsDetailAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }

        }
    }
    @Override
    public void onItemClick(View view, Object data, boolean isCheck) {
        SmsDetailEntity smsEntity = (SmsDetailEntity) data;

        if (isCheck) {
            ids.add(smsEntity);
        } else {
            ids.remove(smsEntity);
        }
    }

    @Override
    public void onResendClick(View view, Object data) {
        position = (Integer) data;
        smsDetailView.showResendDialog();
    }

    @Override
    public void onItemLongClick(View view, final Object data) {
        smsDetailAdapter.setDeleteState(true);
        smsDetailView.rlSmsImageViewVisible(View.VISIBLE);
        smsDetailView.llSendSmsVisible(View.GONE);
        smsDetailAdapter.notifyDataSetChanged();
        smsDetailAdapter.clearCheckState();
        ids.clear();
    }

    private void initAdapter(){
        smsDetailAdapter = new SmsDetailAdapter(context, list);
        smsDetailAdapter.setOnItemClickListener(this);
        smsDetailAdapter.setOnItemLongAndResendClickListener(this);
    }

    @Override
    public void requestSendSmsMessage(String phoneNumber, String content) {
        sendSmsMessageModel.requestSendSmsMessage(phoneNumber,content);
    }

    @Override
    public void requestOnceSendSms(String smsID) {
        onceSendSmsModel.requestOnceSendSms(smsID);
    }


    public void resendSendMsg(){
        SmsDetailEntity smsDetailEntity = list.get(position);
        smsDetailAdapter.remove(position);
        smsDetailEntity.setStatus(SEND_PROGRESSING);
        smsDetailAdapter.add(position, smsDetailEntity);
        if (!TextUtils.isEmpty(smsDetailEntity.getSMSID())) {
            requestOnceSendSms(smsDetailEntity.getSMSID());
        } else {
            String phoneNumber;
            if (User.isCurrentUser(smsDetailEntity.getFm())) {
                phoneNumber = smsDetailEntity.getTo();
            } else {
                phoneNumber = smsDetailEntity.getFm();
            }
            requestSendSmsMessage(phoneNumber, smsDetailEntity.getSMSContent());
        }
    }

    public void deleteSmsIds(){
        Iterator<SmsDetailEntity> iter = ids.iterator();
        ArrayList<String> fms = new ArrayList<>();
        while (iter.hasNext()) {
            SmsDetailEntity entity = iter.next();
            if (entity.isCheck()) {
                fms.add(entity.getSMSID());
            }
        }
        requestDeleteSms(fms);
    }

    @Override
    public void requestGetSmsDetail(String phoneNumber) {
        getSmsDetailModel.requestGetSmsDetail(phoneNumber,pageNumber+"");
        pageNumber = pageNumber + 1;
    }

    @Override
    public void requestDeleteSms(ArrayList<String> ids) {
        deleteSmsModel.requestDeleteSms(ids);
    }

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        smsDetailView.stopRefresh();
        if (cmdType == HttpConfigUrl.COMTYPE_GET_SMS_DETAIL) {
            SmsDetailHttp smsDetailHttp = (SmsDetailHttp) object;
            if (1 == smsDetailHttp.getStatus()) {
                List<SmsDetailEntity> smsDetailEntityList = smsDetailHttp.getSmsDetailEntityList();
                Collections.reverse(smsDetailEntityList);
                if (pageNumber == 1) {
                    smsDetailAdapter.addAll(smsDetailEntityList);
                    smsDetailView.recyclerViewBottom();
                } else if (smsDetailEntityList.size() == 0 && pageNumber != 1) {
                    smsDetailView.showToast(R.string.no_more_content);
                } else {
                    smsDetailAdapter.addTopAll(smsDetailEntityList);
                }
//更新数据
                updateView();
            } else {
                smsDetailView.showToast(smsDetailHttp.getMsg());
            }
        }else if (cmdType == HttpConfigUrl.COMTYPE_SEND_SMS_MESSAGE) {
            SendSmsHttp sendSmsHttp = (SendSmsHttp) object;
            SmsDetailEntity smsDetailEntity = smsDetailAdapter.getItem(smsDetailAdapter.getItemCount() - 1);
            if (1 == sendSmsHttp.getStatus()) {
                updateView();
                smsDetailEntity.setStatus(SEND_SUCCEED);
                smsDetailEntity.setSMSID(sendSmsHttp.getSmsId());
            } else {
                smsDetailEntity.setStatus(SEND_FAIL);
                smsDetailView.showToast(sendSmsHttp.getMsg());
            }
            smsDetailAdapter.remove(smsDetailAdapter.getItemCount() - 1);
            smsDetailAdapter.add(smsDetailEntity);


        } else if (cmdType == HttpConfigUrl.COMTYPE_SEND_RETRY_FOR_ERROR) {
            SendRetryForErrorHttp sendRetryForErrorHttp = (SendRetryForErrorHttp) object;
            SmsDetailEntity smsDetailEntity = smsDetailAdapter.getItem(position);
            if (1 == sendRetryForErrorHttp.getStatus()) {
                smsDetailEntity.setStatus(SEND_SUCCEED);
                updateView();
            } else {
                smsDetailEntity.setStatus(SEND_FAIL);
                smsDetailView.showToast(sendRetryForErrorHttp.getMsg());
            }
            smsDetailAdapter.remove(position);
            smsDetailAdapter.add(position, smsDetailEntity);
        } else if (cmdType == HttpConfigUrl.COMTYPE_SMS_DELETE) {
            if (object.getStatus() == 1) {
                smsDetailAdapter.remove(position);
                smsDetailAdapter.notifyDataSetChanged();

                if (smsDetailAdapter.getItemCount() == 0) {
                    Intent msgIntent = new Intent(SmsPresenterImpl.DELTE_MESSAGE);
                    context.sendBroadcast(msgIntent);
                    smsDetailView.finishView();
                }
                updateView();
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_SMS_DELETE_SMSS) {
            if (object.getStatus() == 1) {
                updateView();
                Iterator<SmsDetailEntity> iter
                        = ids.iterator();
                List<Integer> positions = new ArrayList<>();
                while (iter.hasNext()) {
                    int pisition = iter.next().getPosition();
                    positions.add(pisition);
                }
                ids.clear();
                Collections.sort(positions);
                for (int i = positions.size() - 1; i >= 0; i--)
                    smsDetailAdapter.remove(positions.get(i));
            }
            smsDetailAdapter.notifyDataSetChanged();
            if (smsDetailAdapter.getItemCount() == 0) {
                Intent msgIntent = new Intent(SmsPresenterImpl.DELTE_MESSAGE);
                context.sendBroadcast(msgIntent);
                smsDetailView.finishView();
            }
        }
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        sendFail(errorMessage);
    }
    private void sendFail(String errorMessage) {
        smsDetailView.stopRefresh();
        if (pageNumber == 1 && !isClick) {
            smsDetailView.noNetRelativeLayoutVisible(View.VISIBLE);
        } else if (isClick) {
            sendFail();
        } else {
            smsDetailView.showToast(errorMessage);
        }
    }
    @Override
    public void noNet() {
        sendFail(context.getString(R.string.no_wifi));
    }
    private void sendFail() {
        if (position == -1) {
            position = smsDetailAdapter.getItemCount() - 1;
            SmsDetailEntity smsDetailEntity = smsDetailAdapter.getItem(position);
            if (smsDetailEntity != null) {
                smsDetailEntity.setStatus(SEND_FAIL);
                smsDetailAdapter.remove(position);
                smsDetailAdapter.add(smsDetailEntity);
            }
        } else {
            SmsDetailEntity smsDetailEntity = smsDetailAdapter.getItem(position);
            if (smsDetailEntity != null) {
                smsDetailEntity.setStatus(SEND_FAIL);
                smsDetailAdapter.remove(position);
                smsDetailAdapter.add(position, smsDetailEntity);
            }
        }
    }
    public void updateView() {
        Intent intent = new Intent(SmsPresenterImpl.UPDATE_MESSAGE);
        context.sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        context.unregisterReceiver(mMessageReceiver);
        if(deleteSmsModel!=null){
            deleteSmsModel=null;
        }
        if(getSmsDetailModel!=null){
            getSmsDetailModel=null;
        }
        if(onceSendSmsModel!=null){
            onceSendSmsModel=null;
        }
        if(sendSmsMessageModel!=null){
            sendSmsMessageModel=null;
        }
        if(smsDetailView!=null){
            smsDetailView=null;
        }
    }
}
