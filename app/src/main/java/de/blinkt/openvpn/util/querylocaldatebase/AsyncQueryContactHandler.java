package de.blinkt.openvpn.util.querylocaldatebase;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.text.TextUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.util.PinYinConverNumber;
import de.blinkt.openvpn.util.pinyin.CharacterParser;
import de.blinkt.openvpn.util.pinyin.PinyinComparator;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class AsyncQueryContactHandler extends AsyncQueryHandler{
    QueryCompleteListener queryCompleteListener;
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    /**
     * @author Administrator
     */


    private Map<String, ContactBean> contactIdMap = null;
    public List<ContactBean> mAllLists;
    private List<ContactBean> mMembers = new ArrayList<>();
    public AsyncQueryContactHandler(QueryCompleteListener queryCompleteListener ,ContentResolver cr) {
        super(cr);
        pinyinComparator = new PinyinComparator();
        characterParser = CharacterParser.getInstance();
        this.queryCompleteListener=queryCompleteListener;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            if(contactIdMap==null)
            contactIdMap = new HashMap<>();
            if(mAllLists==null)
            mAllLists = new ArrayList<>();
            cursor.moveToFirst(); // 游标移动到第一项
            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                String name = cursor.getString(1);
                String number = cursor.getString(2);
                String sortKey = cursor.getString(3);
                int contactId = cursor.getInt(4);
                Long photoId = cursor.getLong(5);
                String lookUpKey = cursor.getString(6);
                number=number.replace(" ","");
                if (contactIdMap.containsKey(name)) {
                    ContactBean contac=	contactIdMap.get(name);
                    if(contac!=null){
                        mMembers.remove(contac);
                        contac.setPhoneNum(contac.getPhoneNum()+","+number);
                        mMembers.add(contac);
                    }
                } else {
                    // 创建联系人对象
                    ContactBean contact = new ContactBean();
                    contact.setContactId(contactId);
                    contact.setDesplayName(name);
                    contact.setPhoneNum(number);
                    contact.setSortKey(sortKey);
                    if(!TextUtils.isEmpty(name)){
                        contact.setFormattedNumber(PinYinConverNumber.getInstance().getNameNum(name));
                    }

//                    else{
//                        contact.setFormattedNumber(PinYinConverNumber.getInstance().getNameNum(number));
//                    }

                    String pinyin = characterParser.getSelling(name);
                    String sortString = pinyin.substring(0, 1).toUpperCase();
                    if (sortString.matches("[A-Z]")) {
                        contact.setSortLetters(sortString.toUpperCase());
                    } else {
                        contact.setSortLetters("#");
                    }

                    contact.setPhotoId(photoId);
                    contact.setHeader(headIcon[(int) (Math.random() * 6)]);
                    contact.setLookUpKey(lookUpKey);
                    mMembers.add(contact);
                    contactIdMap.put(name, contact);
                }
            }

            Collections.sort(mMembers, pinyinComparator);
            mAllLists.addAll(mMembers);

        }
        queryCompleteListener.queryComplete(mAllLists);
        super.onQueryComplete(token, cookie, cursor);
        if(cursor!=null){
            cursor.close();
        }
    }

    private int[] headIcon = {
            R.drawable.head_icon_1,
            R.drawable.head_icon_2,
            R.drawable.head_icon_3,
            R.drawable.head_icon_4,
            R.drawable.head_icon_5,
            R.drawable.head_icon_1
    };

    public static Bitmap getContactPhoto(Context context, long photoId, BitmapFactory.Options options) {
        if (photoId < 0) {
            return null;
        }

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId),
                    new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO }, null, null, null);

            if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) {
                byte[] photoData = cursor.getBlob(0);
                // Workaround for Android Issue 8488 http://code.google.com/p/android/issues/detail?id=8488
                if (options == null) {
                    options = new BitmapFactory.Options();
                }
                options.inTempStorage = new byte[16 * 1024];
                options.inSampleSize = 2;
                return BitmapFactory.decodeByteArray(photoData, 0, photoData.length, options);
            }
        } catch (java.lang.Throwable error) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
