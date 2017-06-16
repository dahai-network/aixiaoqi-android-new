package de.blinkt.openvpn.activities.SimOption.PresenterImpl;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.com.johnson.adapter.RecyclerBaseAdapter;
import cn.com.johnson.adapter.SelectContactAdapter;
import de.blinkt.openvpn.activities.SimOption.View.ContactView;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl.AddressCommonModelImpl;
import de.blinkt.openvpn.model.ContactBean;

/**
 * Created by Administrator on 2017/6/16 0016.
 */

public class ContactPresenterImpl implements RecyclerBaseAdapter.OnItemClickListener{


    SelectContactAdapter selectContactAdapter;
    Context context;
    ContactView contactView;
    AddressCommonModelImpl addressCommonModel;
    List<ContactBean> mAllLists = new ArrayList<>();
    public  ContactPresenterImpl(ContactView contactView, Context context){
        this.context=context;
        this.contactView=contactView;
        addressCommonModel=new AddressCommonModelImpl();
        initAdapter();
    }
    public SelectContactAdapter getSelectContactAdapter() {
        return selectContactAdapter;
    }
    private void initAdapter(){
        selectContactAdapter = new SelectContactAdapter(context, mAllLists);
        mAllLists = ICSOpenVPNApplication.getInstance().getContactList();
        contactView.rlNoPermission(mAllLists.size() != 0?View.GONE:View.VISIBLE);
        selectContactAdapter.addAll(mAllLists);
        selectContactAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, Object data, boolean b) {
        ContactBean contactBean = (ContactBean) data;
        addressCommonModel.toActivity(contactBean,-1,context);
    }
    public void searchContact(String s){
        if (!TextUtils.isEmpty(s)) {
            selectContactAdapter.addAll(addressCommonModel.search(s,mAllLists));
        } else {
            selectContactAdapter.addAll(mAllLists);
        }
    }
}
