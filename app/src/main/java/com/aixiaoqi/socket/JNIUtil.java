package com.aixiaoqi.socket;

import android.util.Log;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;

import static com.aixiaoqi.socket.SocketConstant.EN_APPEVT_CMD_SETRST;
import static com.aixiaoqi.socket.TestProvider.sendYiZhengService;
import static com.aixiaoqi.socket.TlvAnalyticalUtils.sendToSdkLisener;

/**
 * Created by Administrator on 2016/12/27 0027.
 */
public class JNIUtil {
    static  JNIUtil jniUtil;
    private static final String libSoName = "aixiaoqi";
    public native void getCardInfo() ;

    public native void main(byte simType) ;
    public native void simComEvtApp2Drv(byte chn,byte index,int length ,byte[] pData);
    static {
        System.loadLibrary(libSoName);
    }

    public static JNIUtil  getInstance(){
        synchronized (SendYiZhengService.class){
            if(jniUtil==null){
                synchronized (SendYiZhengService.class){
                    jniUtil=new JNIUtil();
                }
            }
        }
        return jniUtil;
    }
    public static void  startSDK(String phonenumber){
        Log.e("Blue_Chanl","启动startSDK - REGISTER_STATUE_CODE="+SocketConstant.REGISTER_STATUE_CODE);
        switch (SocketConstant.REGISTER_STATUE_CODE){
            case 0:
                if(jniUtil!=null)
                    phoneAddress(phonenumber);
                break;
            case 1:
                reStartSDK(phonenumber);
                break;
            case 2:
                if(sendToSdkLisener!=null)
                sendToSdkLisener.send(Byte.parseByte(SocketConstant.EN_APPEVT_CMD_SIMCLR), 0, HexStringExchangeBytesUtil.hexStringToBytes(""));
                if(sendYiZhengService!=null)
                sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
            break;
            default:
                Log.e("ReconnectBluebooth","RegisterSucceed");
                break;
        }

    }
    private static void phoneAddress(String phonenumber){
        Log.e("phoneAddress","phoneAddress");
        if(matchesPhoneNumber(phonenumber)==1||matchesPhoneNumber(phonenumber)==2)
            jniUtil.main((byte)1);
        else if(matchesPhoneNumber(phonenumber)==3){
            jniUtil.main((byte)2);
        }
    }

    public static void  reStartSDK(String phonenumber){
        if(jniUtil!=null)
            jniUtil.simComEvtApp2Drv((byte)0,Byte.parseByte(EN_APPEVT_CMD_SETRST),0, HexStringExchangeBytesUtil.hexStringToBytes(SocketConstant.TRAN_DATA_TO_SDK));
//        phoneAddress(phonenumber);
    }
    public static int matchesPhoneNumber(String phone_number) {

        String cm = "^((13[4-9])|(147)|(15[0-2,7-9])|(178)|(18[2-4,7-8]))\\d{8}$";//中国移动
        String cu = "^((13[0-2])|(145)|(15[5-6])|(176)|(18[5-6]))\\d{8}$";//中国联通
        String ct = "^((133)|(153)|(1700)|(18[0-1,9]))\\d{8}$";//中国电信

        int flag = 0;
        if (phone_number.matches(cm)) {
            flag = 1;
        } else if (phone_number.matches(cu)) {
            flag = 2;
        } else if (phone_number.matches(ct)) {
            flag = 3;
        } else {
            flag = 4;
        }
        return flag;

    }
}
