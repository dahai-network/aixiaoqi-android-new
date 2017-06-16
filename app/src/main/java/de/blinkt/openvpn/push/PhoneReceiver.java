package de.blinkt.openvpn.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import de.blinkt.openvpn.activities.SimOption.ui.CallPhoneNewActivity;
import de.blinkt.openvpn.activities.SimOption.ui.ReceiveCallActivity;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;

/**
 * Created by Administrator on 2017/2/23 0023.
 */


public class PhoneReceiver extends BroadcastReceiver {
    public static final String CALL_PHONE=  "cn.phone.android.intent.CALL_PHONE";
    public static final String RECIVE_PHONE=  "cn.phone.android.intent.RECIVE_PHONE";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        Log.e("PhoneReceiver","action="+action);
        if(CALL_PHONE.equals(action)){
            if(!CallPhoneNewActivity.isForeground){
                Intent  intent1=new Intent(context, CallPhoneNewActivity.class);
                intent1.putExtra(IntentPutKeyConstant.DATA_CALLINFO, intent.getSerializableExtra(IntentPutKeyConstant.DATA_CALLINFO));
                intent1.putExtra( IntentPutKeyConstant.CELL_PHONE_TYPE,intent.getIntExtra(IntentPutKeyConstant.CELL_PHONE_TYPE,-1));
                intent1.putExtra(IntentPutKeyConstant.MAXINUM_PHONE_CALL_TIME,intent.getStringExtra(IntentPutKeyConstant.MAXINUM_PHONE_CALL_TIME));
                startActivity(context, intent1);
            }
        }else if(RECIVE_PHONE.equals(action)){
            if(!ReceiveCallActivity.isForeground){
                Intent  intent1=new Intent(context, ReceiveCallActivity.class);
                intent1.putExtra("phoneNum",intent.getStringExtra("phoneNum"));
                startActivity(context, intent1);
            }
        }
    }

    private void startActivity(Context context, Intent intent1) {
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
