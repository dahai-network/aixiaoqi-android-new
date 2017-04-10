package de.blinkt.openvpn.util.querylocaldatebase;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;

import de.blinkt.openvpn.model.ContactRecodeEntity;

/**
 * Created by Administrator on 2016/9/12 0012.
 */
public class FindContactUtil {



    public static void queryContactData(AsyncQueryHandler asyncQueryHandler) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人Uri；
        // 查询的字段
        String[] projection = {ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY};
        // 按照sort_key升序查詢
        asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");

    }
    public static void queryContactRecoderData(AsyncQueryHandler asyncQueryHandler) {
        Uri uri = CallLog.Calls.CONTENT_URI; // 联系人Uri；
        // 查询的字段
        String[] projection = { CallLog.Calls.CACHED_NAME// 通话记录的联系人
                , CallLog.Calls.NUMBER// 通话记录的电话号码
                , CallLog.Calls.DATE// 通话记录的日期
                , CallLog.Calls.DURATION// 通话时长
                , CallLog.Calls.TYPE };
        // 按照sort_key升序查詢
        asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                CallLog.Calls.DEFAULT_SORT_ORDER);
    }
    public static void queryContactRecoderData(AsyncQueryHandler asyncQueryHandler,String phoneNumber,String page) {
        Uri uri = CallLog.Calls.CONTENT_URI; // 联系人Uri；
        // 查询的字段
        String[] projection = { CallLog.Calls.CACHED_NAME// 通话记录的联系人
                , CallLog.Calls.NUMBER// 通话记录的电话号码
                , CallLog.Calls.DATE// 通话记录的日期
                , CallLog.Calls.DURATION// 通话时长
                , CallLog.Calls.TYPE };
        // 按照sort_key升序查詢
        asyncQueryHandler.startQuery(0, null, uri, projection, CallLog.Calls.NUMBER+ "= ?" , new String[]{phoneNumber},
                CallLog.Calls.DEFAULT_SORT_ORDER+ " LIMIT  10 "+" OFFSET "+ page);
    }
    public static  void  addCallRecode(Context context, ContactRecodeEntity contactRecodeEntity){
        ContentValues contentValues=new ContentValues();
        contentValues.put(CallLog.Calls.TYPE,contactRecodeEntity.getType());
        contentValues.put(CallLog.Calls.NUMBER,contactRecodeEntity.getPhoneNumber());
        contentValues.put(CallLog.Calls.DATE,contactRecodeEntity.getCallTime());
        contentValues.put(CallLog.Calls.CACHED_NAME,contactRecodeEntity.getName());
        contentValues.put(CallLog.Calls.DURATION,contactRecodeEntity.getDuration());
        try {
            context.getContentResolver().insert(CallLog.Calls.CONTENT_URI,contentValues);
        }catch (SecurityException e){

        }
    }

}
