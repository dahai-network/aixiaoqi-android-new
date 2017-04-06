package de.blinkt.openvpn.util.querylocaldatebase;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.model.ContactRecodeEntity;
import de.blinkt.openvpn.util.DateUtils;
import de.blinkt.openvpn.util.PinYinConverNumber;

/**
 * Created by Administrator on 2016/9/24 0024.
 */
public class AsyncQueryContactRecodeHandler extends AsyncQueryHandler {
    private QueryCompleteListener queryCompleteListener;

    private Map<String, ContactRecodeEntity> contactRecodeMap = null;

    /**
     * @author Administrator
     */

    private  List<ContactRecodeEntity> mAllLists;
    public AsyncQueryContactRecodeHandler(  QueryCompleteListener queryCompleteListener , ContentResolver cr  ) {
        super(cr);
        this.queryCompleteListener=queryCompleteListener;
        if(mAllLists==null)
            mAllLists=new ArrayList<>();
    }

    @Override
    protected void onQueryComplete(int token, Object cookie,final  Cursor cursor) {
        if (cursor != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loopCursor(cursor);
                }
            }).start();
        }else{
            queryCompleteListener.queryComplete(mAllLists);
        }

        super.onQueryComplete(token, cookie, cursor);
    }

    private void loopCursor(Cursor cursor) {

        while (cursor.moveToNext()) {
            if (addData(cursor)) continue;
        }
        mHandler.sendEmptyMessage(0);
        if(cursor!=null)
            cursor.close();
    }

    private boolean addData(Cursor cursor) {
        String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
        if(TextUtils.isEmpty(number)){
            return true;
        }
        if(contactRecodeMap==null){
            contactRecodeMap=new HashMap<>();
        }
        if(!contactRecodeMap.containsKey(number)){

            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));

            String typeString;
            if (type==CallLog.Calls.INCOMING_TYPE) {

                typeString = Constant.CALL_INCOMING;
            }else if(type==CallLog.Calls.OUTGOING_TYPE)
            {
                typeString = Constant.CALL_OUTGOING;
            }
            else if(type==CallLog.Calls.MISSED_TYPE) {
                typeString = Constant.CALL_MISSED;
            }else{
                return true;
            }
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String date = DateUtils.getTimeStampString(dateLong+"");
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));

            ContactRecodeEntity contactRecodeEntity=new ContactRecodeEntity();
            contactRecodeEntity.setData(date);
            if(!TextUtils.isEmpty(name))
                contactRecodeEntity.setFormattedNumber(PinYinConverNumber.getInstance().getNameNum(name));

            contactRecodeEntity.setDuration(duration);
            contactRecodeEntity.setName(name);
            contactRecodeEntity.setPhoneNumber(number);
            contactRecodeEntity.setTypeString(typeString);
            contactRecodeMap.put(number,contactRecodeEntity);
            mAllLists.add(contactRecodeEntity);
        }
        return false;
    }


    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            queryCompleteListener.queryComplete(mAllLists);
        }
    };

}
