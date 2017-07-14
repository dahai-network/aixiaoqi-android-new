package de.blinkt.openvpn.util;

/**
 * Created by Administrator on 2017/3/13 0013.
 */




import android.telephony.SmsManager;

import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SimActivateHelper
{
    private static final char[] bcdLookup = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };

    public static void setInstance(SimActivateHelper instance) {
        SimActivateHelper.instance = instance;
    }

    private static SimActivateHelper instance;
    private Method _updateMessageOnIcc = null;
    private Class<?> mClass = null;
    private SmsManager mSmsManager = null;
    private Method _getDefault = null;
    private  SimActivateHelper()
    {
        try
        {

            mClass = Class.forName("android.telephony.SmsManager");
            Class localClass = mClass;

            Constructor[] cts = mClass.getDeclaredConstructors();
            System.out.println(cts.length);
            cts[0].setAccessible(true);
             mSmsManager = (SmsManager) cts[0].newInstance();
            Method[] methods=  localClass.getMethods();

            for(Method method:methods){
                if("updateMessageOnIcc".equals(method.getName())){
                    _updateMessageOnIcc=method;
                }
            }
            return;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }

    public static String IntToByteOneHex(int paramInt)
    {
        byte[] arrayOfByte = new byte[1];
        arrayOfByte[0] = ((byte)paramInt);
        return bytesToHex(arrayOfByte);
    }

    private boolean _updateMessageOnIcc(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
        if (_updateMessageOnIcc == null)
            return false;
        try
        {
            Method localMethod = _updateMessageOnIcc;
            SmsManager localSmsManager = mSmsManager;
            Object[] arrayOfObject = new Object[3];
            arrayOfObject[0] = Integer.valueOf(paramInt1);
            arrayOfObject[1] = Integer.valueOf(paramInt2);
            arrayOfObject[2] = paramArrayOfByte;
            boolean bool = ((Boolean)localMethod.invoke(localSmsManager, arrayOfObject)).booleanValue();
            return bool;
        }
        catch (Exception localException)
        {
            System.err.println(localException.toString());
        }
        return false;
    }

    public static final String bytesToHex(byte[] paramArrayOfByte)
    {
        StringBuffer localStringBuffer = new StringBuffer(2 * paramArrayOfByte.length);
        for (int i = 0; i < paramArrayOfByte.length; i++)
        {
            localStringBuffer.append(bcdLookup[(0xF & paramArrayOfByte[i] >>> 4)]);
            localStringBuffer.append(bcdLookup[(0xF & paramArrayOfByte[i])]);
        }
        return localStringBuffer.toString();
    }

    public static SimActivateHelper getInstance()
    {
        try
        {
            if (instance == null)
                instance = new SimActivateHelper();
            SimActivateHelper localSmsHelper = instance;
            return localSmsHelper;
        }
        finally
        {

        }
    }



    public static final byte[] hexToBytes(String paramString)
    {
        byte[] arrayOfByte = new byte[paramString.length() / 2];
        for (int i = 0; i < arrayOfByte.length; i++)
            arrayOfByte[i] = ((byte)Integer.parseInt(paramString.substring(i * 2, 2 + i * 2), 16));
        return arrayOfByte;
    }



    public boolean writeCMDSmall(String paramString)
    {
        if (TextUtils.isEmpty(paramString))
            return false;
        do
        {
            if (paramString.length() % 2 == 1)
                paramString = paramString + "f";
        }
        while (paramString.length() / 2 > 200);
        int i = paramString.length() / 2;
        return writeCMDraw("FEFEF80101" + IntToByteOneHex(i) + paramString);
    }

    public boolean writeCMDraw(String paramString)
    {
        return _updateMessageOnIcc(1, 2, hexToBytes(paramString));
    }

    public boolean writeCMDraw(byte[] paramArrayOfByte)
    {
        return _updateMessageOnIcc(1, 2, paramArrayOfByte);
    }
}
