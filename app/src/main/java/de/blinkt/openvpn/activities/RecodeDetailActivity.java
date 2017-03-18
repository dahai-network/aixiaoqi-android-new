package de.blinkt.openvpn.activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.CallLog;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.model.ContactRecodeEntity;
import de.blinkt.openvpn.util.PinYinConverNumber;

/**
 * Created by Administrator on 2017/3/14 0014.
 */

public class RecodeDetailActivity extends BaseActivity {
    private List<ContactRecodeEntity> mAllLists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private void   searchRecodeDetail(String[] phone){
        ContentResolver resolver = getContentResolver();
        String projection[] = new String[]{CallLog.Calls.DATE, CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE, CallLog.Calls.DURATION};
        Cursor recordCursor = resolver.query(CallLog.Calls.CONTENT_URI, projection,"number=? or number=?"
                , phone, CallLog.Calls.DEFAULT_SORT_ORDER);
        SimpleDateFormat sfd = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        if(mAllLists==null){
            mAllLists=new ArrayList<>();
        }
        if(recordCursor != null){
            while(recordCursor.moveToNext()){
                String date = sfd.format(new Date(recordCursor.getLong(
                        recordCursor.getColumnIndex(CallLog.Calls.DATE))));
                String phoneNum = recordCursor.getString(recordCursor.getColumnIndex(CallLog.Calls.NUMBER));
                int type = recordCursor.getInt(recordCursor.getColumnIndex(CallLog.Calls.TYPE));
                int duration = recordCursor.getInt(recordCursor.getColumnIndex(CallLog.Calls.DURATION));
            String    typeString;
                if (type==CallLog.Calls.INCOMING_TYPE) {

                    typeString = Constant.CALL_INCOMING;
                }else if(type==CallLog.Calls.OUTGOING_TYPE)
                {
                    typeString = Constant.CALL_OUTGOING;
                }
                else if(type==CallLog.Calls.MISSED_TYPE) {
                    typeString = Constant.CALL_MISSED;
                }else{
                    return ;
                }

                ContactRecodeEntity contactRecodeEntity=new ContactRecodeEntity();
                contactRecodeEntity.setData(date);
                contactRecodeEntity.setDuration(duration);
                contactRecodeEntity.setPhoneNumber(phoneNum);
                contactRecodeEntity.setTypeString(typeString);
                mAllLists.add(contactRecodeEntity);

            }
            recordCursor.close();
        }
    }
}
