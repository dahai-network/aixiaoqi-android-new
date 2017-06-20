package com.aixiaoqi.socket;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class TlvEntity implements Serializable{

    private String hexStringTag;//参数类型   Tag
    private String hexStringLength;//内容字节数 Length
    private String hexStringValue;//内容 Value

    public TlvEntity(String hexStringTag, String hexStringValue) {
        this.hexStringTag = hexStringTag;
        this.hexStringValue = hexStringValue;
        this.hexStringLength= getValueLength(hexStringValue);
    }

    public TlvEntity(String hexStringTag, String hexStringLength, String hexStringValue) {
        this.hexStringTag = hexStringTag;
        this.hexStringLength = hexStringLength;
        this.hexStringValue = hexStringValue;
    }

    //获取value的长度
    protected String getValueLength(String hexStringValue) {
        return getValueLength(hexStringValue.length()/2);
    }

    protected String getValueLength(int length) {
        String hexStringLength;
        if(length<127){
            hexStringLength=AddZero(length);
        }else{
            hexStringLength=AddZero(length);
            hexStringLength=Integer.toHexString((Integer.parseInt(hexStringLength,16)|0x8000));
        }
        return hexStringLength;
    }
    //补零
    private String AddZero(int  length){
        String    hexStringLength=Integer.toHexString(length);
        if(hexStringLength.length()%2!=0){
            hexStringLength="0"+hexStringLength;
        }
        return  hexStringLength;
    }

    public TlvEntity() {
    }

    public String getHexStringValue() {
        return hexStringValue;
    }

    public void setHexStringValue(String hexStringValue) {
        this.hexStringValue = hexStringValue;
    }

    public String getHexStringTag() {
        return hexStringTag;
    }

    public void setHexStringTag(String hexStringTag) {
        this.hexStringTag = hexStringTag;
    }

    public String getHexStringLength() {
        return hexStringLength;
    }

    public void setHexStringLength(String hexStringLength) {
        this.hexStringLength = hexStringLength;
    }

    @Override
    public String toString() {
        return "YiZhengTlv{" +
                "hexStringTag='" + hexStringTag + '\'' +
                ", hexStringLength='" + hexStringLength + '\'' +
                ", hexStringValue='" + hexStringValue + '\'' +
                '}';
    }
}
