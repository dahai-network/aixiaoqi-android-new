package com.aixiaoqi.socket;

/**
 * Created by Administrator on 2016/12/27 0027.
 */
public class JNIUtil {
    static  JNIUtil jniUtil;
    private static final String libSoName = "aixiaoqi";
    public native void getCardInfo() ;

    public native void main() ;
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
    public static void  startSDK(){
        if(jniUtil!=null)
            jniUtil.main();
    }
    public static void  reStartSDK(){
        if(jniUtil!=null)
            jniUtil.simComEvtApp2Drv((byte)0,(byte)1,0,HexStringExchangeBytesUtil.hexStringToBytes(""));
            jniUtil.main();
    }

}
