package de.blinkt.openvpn;

import android.util.Log;

import de.blinkt.openvpn.constant.Constant;

/**
 * Created by Administrator on 2017/4/5 0005.
 */

public class Logger {
    protected  String TAG=getClass().toString();
    protected  void w(String message){
        if(Constant.PRINT_LOGS)
            Log.w(TAG,message);
    }
    protected  void e(String message){
        if(Constant.PRINT_LOGS)
            Log.e(TAG,message);
    }
    protected  void d(String message){
        if(Constant.PRINT_LOGS)
            Log.d(TAG,message);
    }
    protected  void i(String message){
        if(Constant.PRINT_LOGS)
            Log.i(TAG,message);
    }
}
