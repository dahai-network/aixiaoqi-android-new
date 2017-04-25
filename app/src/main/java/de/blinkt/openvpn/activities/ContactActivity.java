package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;




import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.RecyclerBaseAdapter;
import cn.com.johnson.adapter.SelectContactAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;

import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.util.pinyin.CharacterParser;
import de.blinkt.openvpn.views.contact.SideBar;
import de.blinkt.openvpn.views.contact.TouchableRecyclerView;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class ContactActivity  extends BaseActivity implements RecyclerBaseAdapter.OnItemClickListener{
    SideBar mSideBar;
    TouchableRecyclerView mRecyclerView;
    TextView  mUserDialog;
    List<ContactBean> mAllLists=new ArrayList<>();
    EditText searchEditText;
    SelectContactAdapter selectContactAdapter ;
    private TextView tvNoPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initView();
        hasLeftViewTitle(R.string.address_list,0);
        addListener();

        setSearchLinstener();
    }
    private void addListener(){
        selectContactAdapter.setOnItemClickListener(this);

    }
    private void initView(){
        mSideBar = (SideBar) findViewById(R.id.contact_sidebar);
        mUserDialog = (TextView) findViewById(R.id.contact_dialog);
        searchEditText = (EditText)findViewById(R.id.searchEditText);
        mRecyclerView = (TouchableRecyclerView) findViewById(R.id.contact_member);
        tvNoPermission = (TextView) findViewById(R.id.tv_no_permission);
        int orientation = LinearLayoutManager.VERTICAL;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, false);
        mRecyclerView.setLayoutManager(layoutManager);

        selectContactAdapter=new SelectContactAdapter(this,mAllLists);
        mAllLists=ICSOpenVPNApplication.getInstance().getContactList();
        if(mAllLists!=null&&mAllLists.size()!=0){
            tvNoPermission.setVisibility(View.GONE);
        }else{
            tvNoPermission.setVisibility(View.VISIBLE);

        }
        selectContactAdapter.addAll(mAllLists);
        mRecyclerView.setAdapter(selectContactAdapter);
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {

                int position = selectContactAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRecyclerView.scrollToPosition(position);
                }

            }
        });
        mSideBar.setTextView(mUserDialog);
    }

    private void setSearchLinstener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {

                    selectContactAdapter.addAll(search(s.toString()));
                } else {
                    selectContactAdapter.addAll(mAllLists);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        List<ContactBean> localAllLists = mAllLists;
        for (ContactBean contact : localAllLists) {
            if ((contact.getPhoneNum() != null && contact.getDesplayName() != null)||(contact.getPhoneNum() != null && contact.getDesplayName() != null)){
                if ((contact.getPhoneNum().contains(str) || contact.getDesplayName().contains(str))||(contact.getDesplayName().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE)) ||
                        characterParser.getSelling(contact.getDesplayName().toLowerCase(Locale.CHINESE)).contains(characterParser.getSelling(str.toLowerCase(Locale.CHINESE)))
                )){
                    if (!searchResultList.contains(contact)) {
                        searchResultList.add(contact);
                    }
                }
            }

        }

        return searchResultList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                setResult(RESULT_OK,data);
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(View view, Object data, boolean b) {
        ContactBean contactBean=(ContactBean)data;
        String[] arrayPhoneNum= contactBean.getPhoneNum().split(",");
        if(arrayPhoneNum.length>1){
            Intent intent=new Intent(this,ContactDetailActivity.class);
            if(contactBean.getBitmapHeader()!=null){
            intent.putExtra("contactBean",tempObject(contactBean));
                Bundle bundle = new Bundle();
                bundle.putParcelable("bitmap", contactBean.getBitmapHeader());
                intent.putExtras(bundle);
            }else{
                intent.putExtra("contactBean",contactBean);
            }
            intent.putExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE,IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE);
            startActivityForResult(intent,0);
        }else{
            Intent intent =new Intent();
            if(contactBean.getBitmapHeader()!=null){
                intent.putExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE,tempObject(contactBean));
            }else{
                intent.putExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE,contactBean);
            }
            setResult(IntentPutKeyConstant.ADD_CONTACT,intent);
            finish();
        }
    }

    private ContactBean tempObject(ContactBean contactBean) {
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
        return  contactBean1;
    }


}
