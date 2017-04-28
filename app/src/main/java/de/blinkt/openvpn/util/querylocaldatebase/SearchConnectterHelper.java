package de.blinkt.openvpn.util.querylocaldatebase;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by Administrator on 2016/8/4.
 */
public class SearchConnectterHelper {

    /*
         * 根据电话号码取得联系人姓名
         */
    public static String getContactNameByPhoneNumber(Context context, String address) {
        if (context == null || TextUtils.isEmpty(address)) {
            return "";
        }

        Cursor c=null;
        try {
            c = context.getContentResolver().query(Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI, address), new String[]{
                    ContactsContract.PhoneLookup._ID,
                    ContactsContract.PhoneLookup.NUMBER,
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.TYPE, ContactsContract.PhoneLookup.LABEL}, null, null, null);
        } catch (Exception e) {
           // CommonTools.showShortToast(context, "请打开手机联系人权限");

        }

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            return c.getString(2); //获取姓名
        }
        if (c != null)
            c.close();
        return "";
    }

    /**
     * 获取所有联系人内容
     *
     * @param context
     * @return
     */
    public static String getContacts(Context context) {
        StringBuilder sb = new StringBuilder();

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String contactId = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor
                        .getString(cursor
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //第一条不用换行
                if (sb.length() == 0) {
                    sb.append(name);
                } else {
                    sb.append("\n" + name);
                }

                Cursor phones = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + " = " + contactId, null, null);
                while (phones != null && phones.moveToNext()) {
                    String phoneNumber = phones
                            .getString(phones
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    // 添加Phone的信息
                    sb.append("\t").append(phoneNumber);

                }
                if (phones != null)
                    phones.close();

            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return sb.toString();
    }
}
