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
import de.blinkt.openvpn.activities.CallDetailActivity;
import de.blinkt.openvpn.activities.ContactDetailActivity;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
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
    public ContactAdapter getmAdapter() {
        return mAdapter;
    }

    private ContactAdapter mAdapter;
    public AddressPresenterImpl(AddressListView addressListView, Context context){
        this.addressListView=addressListView;
        this.context=context;
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
        Intent intent;
        if (arrayNum.length > 1) {
            intent = new Intent(context, ContactDetailActivity.class);
            ContactDetailActivity.setNotifyFragmentDataListener(this);
            if (contactBean.getBitmapHeader() != null) {
                tempObject(contactBean, intent);
                Bundle b = new Bundle();
                b.putParcelable("bitmap", contactBean.getBitmapHeader());
                intent.putExtras(b);
            } else {
                intent.putExtra("contactBean", contactBean);
            }

            intent.putExtra("position", position);

        } else {
            intent = new Intent(context, CallDetailActivity.class);
            if (contactBean.getBitmapHeader() != null) {
                tempObject(contactBean, intent);
            } else {
                intent.putExtra("contactBean", contactBean);
            }

        }
        context.startActivity(intent);
    }

    private void tempObject(ContactBean contactBean, Intent intent) {
        ContactBean contactBean1;
        contactBean1 = new ContactBean();
        contactBean1.setBitmapHeader(null);
        contactBean1.setContactId(contactBean.getContactId());
        contactBean1.setDesplayName(contactBean.getDesplayName());
        contactBean1.setPhoneNum(contactBean.getPhoneNum());
        contactBean1.setSortKey(contactBean.getSortKey());
        contactBean1.setPhotoId(contactBean.getPhotoId());
        contactBean1.setLookUpKey(contactBean.getLookUpKey());
        contactBean1.setFormattedNumber(contactBean.getFormattedNumber());
        contactBean1.setSelected(contactBean.getSelected());
        contactBean1.setPinyin(contactBean.getPinyin());
        contactBean1.setSortLetters(contactBean.getSortLetters());
        intent.putExtra("contactBean", contactBean1);
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
            mAdapter.addAll(search(s));
        } else {
            mAdapter.addAll(mAllLists);
        }
    }



    /**
     * 模糊查询
     *
     * @param str
     * @return
     */
    private List<ContactBean> search(String str) {
        CharacterParser characterParser = CharacterParser.getInstance();
        List<ContactBean> searchResultList = new ArrayList<>();//过滤后的list
        for (ContactBean contact : mAllLists) {
            if ((contact.getPhoneNum() != null && contact.getDesplayName() != null) || (contact.getPhoneNum() != null && contact.getDesplayName() != null)) {
                if ((contact.getPhoneNum().contains(str) || contact.getDesplayName().contains(str)) || (contact.getDesplayName().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE)) ||
                        characterParser.getSelling(contact.getDesplayName().toLowerCase(Locale.CHINESE)).contains(characterParser.getSelling(str.toLowerCase(Locale.CHINESE)))
                )) {
                    if (!searchResultList.contains(contact)) {
                        searchResultList.add(contact);
                    }
                }
            }

        }


        return searchResultList;
    }

    @Override
    public void contactChangeData(int position) {
        mAdapter.notifyItemRemoved(position);
    }

}
