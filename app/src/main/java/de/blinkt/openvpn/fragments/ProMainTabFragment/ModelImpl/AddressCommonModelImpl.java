package de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.blinkt.openvpn.activities.ContactDetailActivity;
import de.blinkt.openvpn.activities.SimOption.ui.CallDetailActivity;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.util.pinyin.CharacterParser;

/**
 * Created by Administrator on 2017/6/16 0016.
 */

public class AddressCommonModelImpl {

    /**
     * 模糊查询
     *
     * @param str
     * @return
     */
    public List<ContactBean> search(String str, List<ContactBean> mAllLists) {
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


    public void toActivity(ContactBean contactBean, int position,Context context) {
        String[] arrayNum = contactBean.getPhoneNum().split(",");
        Intent intent;
        if (arrayNum.length > 1) {
            intent = new Intent(context, ContactDetailActivity.class);
            if (contactBean.getBitmapHeader() != null) {
                intent.putExtra("contactBean",  tempObject(contactBean));
                Bundle b = new Bundle();
                b.putParcelable("bitmap", contactBean.getBitmapHeader());
                intent.putExtras(b);
            } else {
                intent.putExtra("contactBean", contactBean);
            }
            if(position==-1){
                intent.putExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE,IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE);
                ((Activity)context).startActivityForResult(intent,0);
            }else{
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        }else if(position==-1){
            intent =new Intent();
            if(contactBean.getBitmapHeader()!=null){
                intent.putExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE,tempObject(contactBean));
            }else{
                intent.putExtra(IntentPutKeyConstant.SELECFT_CONTACT_PEOPLE,contactBean);
            }
            ((Activity)context).setResult(IntentPutKeyConstant.ADD_CONTACT,intent);
            ((Activity)context).finish();
        } else {
            intent = new Intent(context, CallDetailActivity.class);
            if (contactBean.getBitmapHeader() != null) {
                intent.putExtra("contactBean",  tempObject(contactBean));
            } else {
                intent.putExtra("contactBean", contactBean);
            }
            context.startActivity(intent);
        }

    }
    private ContactBean tempObject(ContactBean contactBean ) {
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
