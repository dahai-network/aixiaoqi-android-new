package de.blinkt.openvpn.fragments.ProMainTabFragment.PresenterImpl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.com.johnson.adapter.ContactAdapter;

import de.blinkt.openvpn.activities.ContactDetailActivity;
import de.blinkt.openvpn.activities.SimOption.ui.CallDetailActivity;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl.AddressCommonModelImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.View.AddressListView;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.util.pinyin.CharacterParser;
import de.blinkt.openvpn.util.querylocaldatebase.AsyncQueryContactHandler;
import de.blinkt.openvpn.util.querylocaldatebase.FindContactUtil;
import de.blinkt.openvpn.util.querylocaldatebase.QueryCompleteListener;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class AddressPresenterImpl implements QueryCompleteListener<ContactBean>,ContactAdapter.CallLisener, ContactDetailActivity.ContactChangeDataListener  {
    List<ContactBean> mAllLists = new ArrayList<>();
    Context context;
    AddressListView addressListView;
    AddressCommonModelImpl addressCommonModel;
    public ContactAdapter getmAdapter() {
        return mAdapter;
    }

    private ContactAdapter mAdapter;
    public AddressPresenterImpl(AddressListView addressListView, Context context){
        this.addressListView=addressListView;
        this.context=context;
        addressCommonModel=new AddressCommonModelImpl();
        initContactAdapter();
    }

    private void initContactAdapter(){
        mAdapter = new ContactAdapter(context, mAllLists, this);
    }

    public  void  visibleFragment() {
        mAllLists = ICSOpenVPNApplication.getInstance().getContactList();
        if (mAllLists != null && mAllLists.size() != 0) {
            addressListView.rlNoPermission(View.GONE);
            mAdapter.addAll(mAllLists);
        } else {
            searchContact();
        }
    }

    @Override
    public void gotoActivity(ContactBean contactBean, int position) {
        String[] arrayNum = contactBean.getPhoneNum().split(",");
        if(arrayNum.length>1){
            ContactDetailActivity.setNotifyFragmentDataListener(this);
        }
        addressCommonModel.toActivity(contactBean,position,context);
    }


    private void searchContact() {
        AsyncQueryContactHandler asyncQueryHandler = new AsyncQueryContactHandler(this, context.getContentResolver());
        FindContactUtil.queryContactData(asyncQueryHandler);
    }

    @Override
    public void queryComplete(List<ContactBean> mAllLists) {
        ICSOpenVPNApplication.getInstance().setmAllList(mAllLists);
        if (mAllLists == null || mAllLists.size() == 0) {
            addressListView.rlNoPermission(View.VISIBLE);
        } else {
            addressListView.rlNoPermission(View.GONE);
            mAdapter.addAll(mAllLists);
        }
    }

    public void searchContact(String s){
        if (!TextUtils.isEmpty(s)) {
            mAdapter.addAll(addressCommonModel.search(s,mAllLists));
        } else {
            mAdapter.addAll(mAllLists);
        }
    }


    @Override
    public void contactChangeData(int position) {
        mAdapter.notifyItemRemoved(position);
    }

}
