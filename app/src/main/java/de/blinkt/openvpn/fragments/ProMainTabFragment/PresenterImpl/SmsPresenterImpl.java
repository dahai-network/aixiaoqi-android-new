package de.blinkt.openvpn.fragments.ProMainTabFragment.PresenterImpl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.RecyclerBaseAdapter;
import cn.com.johnson.adapter.SmsListAdapter;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.activities.SMSAcivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl.SmsDeleteByTelsModelImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl.SmsListModelImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Presenter.SmsPresenter;
import de.blinkt.openvpn.fragments.ProMainTabFragment.View.SmsView;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ui.SmsFragment;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.CreateHttpFactory;
import de.blinkt.openvpn.http.SMSListHttp;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.model.SmsEntity;
import de.blinkt.openvpn.model.SmsIdsEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.User;

import static de.blinkt.openvpn.constant.UmengContant.CLICKSMSITEM;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class SmsPresenterImpl extends NetPresenterBaseImpl implements SmsPresenter, RecyclerBaseAdapter.OnItemClickListener, SmsListAdapter.OnItemLongClickListener {
    SmsView smsView;
    SmsListModelImpl smsListModel;
    SmsDeleteByTelsModelImpl smsDeleteByTelsModel;
    List<SmsEntity> list = new ArrayList<>();
    SmsListAdapter smsListAdapter;
    Context context;
    List<ContactBean> mAllLists = new ArrayList<>();
    List<SmsEntity> mAllTempLists = new ArrayList<>();
    //储存需要删除的item
    private HashSet<SmsEntity> ids = new HashSet<>();
    public static final String NOTIFY_RECEIVED_ACTION = "NOTIFY_RECEIVED_ACTION";
    public static final String DELTE_MESSAGE = "DELTE_MESSAGE";
    public static final String UPDATE_MESSAGE = "REFRESH_DATA";
    public  SmsPresenterImpl(SmsView smsView,Context context){
        this.smsView=smsView;
        this.context=context;
        smsListModel=new SmsListModelImpl(this);
        smsDeleteByTelsModel=new SmsDeleteByTelsModelImpl(this);
        mAllLists = ICSOpenVPNApplication.getInstance().getContactList();
        initSmsAdapter();
    }
    int pageNumber;
    int requestNetCount;
    @Override
    public void requestSmsList(int pageNumber,int requestNetCount) {
        this.pageNumber=pageNumber;
        this.requestNetCount=requestNetCount;
        smsListModel.requestSmsList(pageNumber+"");
    }

    @Override
    public void requestSmsDeleteByTels(ArrayList<String> tels) {
        smsDeleteByTelsModel.requestSmsDeleteByTels(tels);
    }

    private void setSmsListRealName(List<SmsEntity> smsEntityList) {

        int size = smsEntityList.size();
        for (int i = 0; i < size; i++) {
            String comparePhoneNumber;
            if (User.isCurrentUser(smsEntityList.get(i).getFm())) {
                comparePhoneNumber = smsEntityList.get(i).getTo();
            } else {
                comparePhoneNumber = smsEntityList.get(i).getFm();
            }
            int insideSize = mAllLists.size();
            for (int j = 0; j < insideSize; j++) {
                if (comparePhoneNumber.equals(mAllLists.get(j).getPhoneNum())) {
                    smsEntityList.get(i).setRealName(mAllLists.get(j).getDesplayName());
                    break;
                }
            }
        }
    }

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_GET_SMS_LIST) {
            smsView.loadMoreComplete();
            smsView.refreshComplete();
            smsView. noNetRelativeLayout(View.GONE);
            SMSListHttp smsListHttp = (SMSListHttp) object;
            if (smsListHttp.getStatus() == 1) {
                List<SmsEntity> smsEntityList = smsListHttp.getSmsEntityList();

                if (smsEntityList.size() != 0) {
                    if (smsEntityList.size() < Constant.PAGESIZE)
                        smsView.noMoreLoad();
                    smsView.recyclerView(View.VISIBLE);
                    smsView.nodataRelativeLayout(View.GONE);
                    setSmsListRealName(smsEntityList);
                    if (pageNumber == 1) {
                        mAllTempLists.clear();
                        mAllTempLists.addAll(smsEntityList);
                    } else {
                        mAllTempLists.addAll(smsEntityList);
                    }
                    smsListAdapter.addAll(mAllTempLists);

                } else {
                    if (pageNumber == 1) {
                        smsView. recyclerView(View.GONE);

                        smsView.nodataRelativeLayout(View.VISIBLE);
                    } else {
                        smsView.showToast(R.string.no_more_content);
                    }
                    smsView.noMoreLoad();
                }
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_SMS_DELETE_BY_TELS) {
            if (object.getStatus() == 1) {
                smsView.onDataRefresh();
            } else {
                smsView.showToast(object.getMsg());
            }
        }
    }

    @Override
    public void noNet() {

            if (requestNetCount == 1) {
                smsView.noNetRelativeLayout(View.VISIBLE);
            } else {
                smsView.showToast(R.string.no_wifi);
            }
    }
    private void  initSmsAdapter(){
        smsListAdapter = new SmsListAdapter(context, list);
        smsListAdapter.setOnItemClickListener(this);
        smsListAdapter.setOnItemLongClickListener(this);
    }

    public SmsListAdapter getSmsListAdapter() {
        return smsListAdapter;
    }

    @Override
    public void onItemLongClick(View view, Object data) {

        smsListAdapter.setDeleteImage(true);
        smsView.editSmsBackground(R.drawable.delete_sms_selector);
        smsListAdapter.notifyDataSetChanged();
        smsListAdapter.clearCheckState();
        ids.clear();
    }
    private  int clickPosition;
    public void onItemClick(View view, Object object, boolean isCheck) {
        if (!smsListAdapter.isDeleteState()) {
            //友盟方法统计
            MobclickAgent.onEvent(context, CLICKSMSITEM);
            Intent intent = new Intent(context, SMSAcivity.class);
            SmsEntity smsEntity = (SmsEntity) object;
            intent.putExtra(IntentPutKeyConstant.SMS_LIST_KEY, smsEntity);
            clickPosition = mAllTempLists.indexOf(object);
            if ("0".equals(smsEntity.getIsRead())) {
                smsEntity.setIsRead("1");
                int size = mAllTempLists.size();
                for (int i = 0; i < size; i++) {
                    if (smsEntity.getFm().equals(mAllTempLists.get(i).getFm()) || smsEntity.getTo().equals(mAllTempLists.get(i).getTo())) {
                        mAllTempLists.remove(i);
                        mAllTempLists.add(i, smsEntity);
                        smsListAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
            context.startActivity(intent);
        } else {
            SmsEntity smsEntity = (SmsEntity) object;
            if (isCheck) {
                ids.add(smsEntity);
            } else {
                ids.remove(smsEntity);
            }
        }
    }

    public void selectDeleteSms(){
        Iterator<SmsEntity> iter = ids.iterator();
        ArrayList<String> fms = new ArrayList<>();
        while (iter.hasNext()) {
            SmsEntity entity = iter.next();
            if (entity.isCheck()) {
                if (!User.isCurrentUser(entity.getFm())) {
                    fms.add(entity.getFm());
                } else {
                    fms.add(entity.getTo());
                }
            }
        }
        if (fms.size() > 0)
            requestSmsDeleteByTels(fms);
    }


    NotifyReceiver mNotifyReceiver;

    public void registerMessageReceiver() {
        mNotifyReceiver = new NotifyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(NOTIFY_RECEIVED_ACTION);
        filter.addAction(DELTE_MESSAGE);
        filter.addAction(UPDATE_MESSAGE);
        context.registerReceiver(mNotifyReceiver, filter);
    }

    public class NotifyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NOTIFY_RECEIVED_ACTION.equals(intent.getAction())) {
                smsView.onDataRefresh();
            } else if (DELTE_MESSAGE.equals(intent.getAction())) {
                if (mAllTempLists != null && clickPosition != -1 && smsListAdapter != null) {
                    mAllTempLists.remove(clickPosition);
                    smsListAdapter.addAll(mAllTempLists);
                }
            } else if (UPDATE_MESSAGE.equals(intent.getAction())) {
                smsView.onDataRefresh();
            }
        }
    }

    public boolean backButton(){
        if (smsListAdapter.isDeleteState()) {
            smsListAdapter.setDeleteImage(false);
            smsView.editSmsBackground(R.drawable.edit_sms_selector);
            smsListAdapter.notifyDataSetChanged();
            ids.clear();
            return true;
        }
        return false;
    }

    @Override
    public void onDestory() {
        if(smsView!=null)
            smsView=null;
        if(smsDeleteByTelsModel!=null){
            smsDeleteByTelsModel=null;
        }
        if(smsListModel!=null){
            smsListModel=null;
        }
        context.unregisterReceiver(mNotifyReceiver);
    }
}
