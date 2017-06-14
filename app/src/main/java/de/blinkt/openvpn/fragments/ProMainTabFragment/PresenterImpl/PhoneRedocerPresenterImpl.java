package de.blinkt.openvpn.fragments.ProMainTabFragment.PresenterImpl;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Filter;

import com.aixiaoqi.socket.SocketConstant;

import java.util.ArrayList;
import java.util.List;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.ContactRecodeAdapter;
import cn.com.johnson.adapter.RecyclerBaseAdapter;
import de.blinkt.openvpn.activities.CallDetailActivity;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl.NumberDbModelImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.View.PhoneView;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.model.ContactRecodeEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.querylocaldatebase.AsyncQueryContactRecodeHandler;
import de.blinkt.openvpn.util.querylocaldatebase.FindContactUtil;
import de.blinkt.openvpn.util.querylocaldatebase.QueryCompleteListener;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class PhoneRedocerPresenterImpl  implements RecyclerBaseAdapter.OnItemClickListener,QueryCompleteListener<ContactRecodeEntity> {
    PhoneView phoneView;



    ContactRecodeAdapter contactRecodeAdapter;
    NumberDbModelImpl numberDbModel;
    Context context;
    List<ContactRecodeEntity> mAllList = new ArrayList<>();
    public  PhoneRedocerPresenterImpl(PhoneView phoneView, Context context){
        this.phoneView=phoneView;
        this.context=context;
        numberDbModel=new NumberDbModelImpl();
        initContactRecodeAdapter();
        searchContactRedocer(context);
    }

    public ContactRecodeAdapter getContactRecodeAdapter() {
        return contactRecodeAdapter;
    }
    private  void initContactRecodeAdapter(){
        contactRecodeAdapter = new ContactRecodeAdapter(numberDbModel.initDB(context), context, mAllList);
        contactRecodeAdapter.setOnItemClickListener(this);
    }

    public void addDataContactRecodeAdapter(){
        contactRecodeAdapter.addAll(mAllList);
    }
    @Override
    public void onItemClick(View view, Object data, boolean b) {
        ContactRecodeEntity    contactRecodeEntity = (ContactRecodeEntity) data;
        switch (view.getId()) {
            case R.id.iv_arrow:
                Intent intent = new Intent(context, CallDetailActivity.class);
                intent.putExtra(CallDetailActivity.PHONE_INFO, contactRecodeEntity);
                phoneView.toCallDetailActivity(contactRecodeEntity );
                break;
            default:
                if (SocketConstant.REGISTER_STATUE_CODE == 3) {
                    //拨打电话
                    if (!CommonTools.isFastDoubleClick(1000))
                        phoneView.simCallPhone(contactRecodeEntity);
                } else {
                    phoneView.showToast(R.string.sim_register_phone_tip);
                }
                break;
        }
    }


    public Filter getFilter() {
        Filter filter = new Filter() {
            String str;

            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null)
                    contactRecodeAdapter.setSearchChar(str);
                contactRecodeAdapter.addAll((ArrayList<ContactRecodeEntity>) results.values);
            }

            protected FilterResults performFiltering(CharSequence s) {
                str = s.toString();
                FilterResults results = new FilterResults();
                ArrayList<ContactRecodeEntity> contactList = new ArrayList<>();
                searchContactRecoder(str, contactList);
                searchContect(str, contactList, false);
                removeDuplicate(contactList);
                results.values = contactList;
                return results;
            }
        };
        return filter;
    }


    //查找联系人
    private void searchContect(String str, List<ContactRecodeEntity> searchResultList, boolean isExist) {
        for (ContactBean contactBean : ICSOpenVPNApplication.getInstance().getContactList()) {
            for (int i = 0; i < searchResultList.size(); i++) {
                if (contactBean.getPhoneNum().equals(searchResultList.get(i).getPhoneNumber()) || contactBean.getDesplayName().equals(searchResultList.get(i).getName())) {
                    isExist = true;
                    break;
                }

            }
            if (!isExist && contactBean.getFormattedNumber()[0].indexOf(str) > -1 || contactBean.getFormattedNumber()[1].indexOf(str) > -1 || contactBean.getPhoneNum().indexOf(str) > -1) {
                ContactRecodeEntity contactRecodeEntity = new ContactRecodeEntity();
                String phoneNumber = contactBean.getPhoneNum().split(",")[0];
                contactRecodeEntity.setPhoneNumber(phoneNumber);
                if (!TextUtils.isEmpty(contactBean.getDesplayName()))
                    contactRecodeEntity.setName(contactBean.getDesplayName());
                else {
                    contactRecodeEntity.setName(contactBean.getPhoneNum());
                }
                contactRecodeEntity.setFormattedNumber(contactBean.getFormattedNumber());
                searchResultList.add(contactRecodeEntity);
            } else {
                isExist = false;
            }
        }
    }

    public void searchContactRedocer(Context context) {
        AsyncQueryContactRecodeHandler asyncQueryContactRecodeHandler = new AsyncQueryContactRecodeHandler(this, context.getContentResolver(), false);
        FindContactUtil.queryContactRecoderData(asyncQueryContactRecodeHandler);
    }

    @Override
    public void queryComplete(List<ContactRecodeEntity> mAllLists) {
        if (mAllLists == null || mAllLists.size() == 0) {
            phoneView. rlNoPermission(View.VISIBLE);
        } else {
            phoneView.rlNoPermission(View.GONE);
            mAllList = mAllLists;
            addDataContactRecodeAdapter();
        }
    }
    //搜索通话记录
    private void searchContactRecoder(String str, List<ContactRecodeEntity> searchResultList) {
        try {
            for (ContactRecodeEntity contactRecodeEntityntact : mAllList) {
                if (contactRecodeEntityntact.getFormattedNumber()[0].indexOf(str) > -1 || contactRecodeEntityntact.getFormattedNumber()[1].indexOf(str) > -1 || contactRecodeEntityntact.getPhoneNumber().indexOf(str) > -1) {
                    if (!searchResultList.contains(contactRecodeEntityntact)) {
                        searchResultList.add(contactRecodeEntityntact);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //去重
    public List<ContactRecodeEntity> removeDuplicate(List<ContactRecodeEntity> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getPhoneNumber().equals(list.get(i).getPhoneNumber())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }


    public void onDestory(){
        numberDbModel.close();
    }


}
