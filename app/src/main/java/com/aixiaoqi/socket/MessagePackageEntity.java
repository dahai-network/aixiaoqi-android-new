package com.aixiaoqi.socket;

import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2016/12/10 0010.
 */
public class MessagePackageEntity {
    protected String hexStringSessionId;//会话ID
    protected String hexStringMessageNumber;//消息序号
    protected String hexStringDatalength;//数据长度
    protected String hexStringMessageHeader;//协议版本标志位消息类型，响应码组合
    protected List<TlvEntity> yiZhengTlvList;

    public String getHexStringSessionId() {
        return hexStringSessionId;
    }

    public void setHexStringSessionId(String hexStringSessionId) {
        this.hexStringSessionId = hexStringSessionId;
    }

    public String getHexStringMessageNumber() {
        return hexStringMessageNumber;
    }

    public void setHexStringMessageNumber(String hexStringMessageNumber) {
        this.hexStringMessageNumber = hexStringMessageNumber;
    }

    public String getHexStringDatalength() {
        return hexStringDatalength;
    }

    public void setHexStringDatalength(String hexStringDatalength) {
        this.hexStringDatalength = hexStringDatalength;
    }

    public String getHexStringMessageHeader() {
        return hexStringMessageHeader;
    }

    public void setHexStringMessageHeader(String hexStringMessageHeader) {
        this.hexStringMessageHeader = hexStringMessageHeader;
    }

    public List<TlvEntity> getYiZhengTlvList() {
        return yiZhengTlvList;
    }

    public void setYiZhengTlvList(List<TlvEntity> yiZhengTlvList) {
        this.yiZhengTlvList = yiZhengTlvList;
    }

    /**
     *
     * @param hexStringSessionId 会话ID
     * @param hexStringMessageNumber  消息序号
     * @param hexStringMessageHeader  协议版本标志位消息类型，响应码组合
     */


    public MessagePackageEntity(String hexStringSessionId, String hexStringMessageNumber, String hexStringMessageHeader, List<TlvEntity> yiZhengTlvList) {
        this.hexStringSessionId = hexStringSessionId;
        this.hexStringMessageNumber = hexStringMessageNumber;
        this.hexStringMessageHeader = hexStringMessageHeader;
        this.yiZhengTlvList=yiZhengTlvList;
    }

    public MessagePackageEntity(List<TlvEntity> yiZhengTlvList, String hexStringSessionId, String hexStringMessageNumber, String hexStringDatalength, String hexStringMessageHeader) {
        this.yiZhengTlvList = yiZhengTlvList;
        this.hexStringSessionId = hexStringSessionId;
        this.hexStringMessageNumber = hexStringMessageNumber;
        this.hexStringDatalength = hexStringDatalength;
        this.hexStringMessageHeader = hexStringMessageHeader;
    }

    public MessagePackageEntity() {
    }

    private String AddZero(int  length){
        String    hexStringLength=Integer.toHexString(length/2);
        int byteCount=hexStringLength.length();
        if(byteCount%4==1){
            hexStringLength="000"+hexStringLength;
        }else if(byteCount%4==2){
            hexStringLength="00"+hexStringLength;
        }else if(byteCount%4==3){
            hexStringLength="0"+hexStringLength;
        }
        Log.e("YiZhengTlvhexString",hexStringLength);

        return  hexStringLength;
    }




    public String combinationPackage() {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(hexStringMessageHeader);
        stringBuilder.append(hexStringSessionId);
        stringBuilder.append(hexStringMessageNumber+AddZero(listToString(yiZhengTlvList).length()));
//        stringBuilder.append(hexStringMessageNumber+"0487");
       stringBuilder.append( listToString(yiZhengTlvList));
        Log.e("combinationPackage",stringBuilder.toString());
        return stringBuilder.toString();
    }

    private String listToString(List<TlvEntity> list) {
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<list.size();i++){
            stringBuilder.append(list.get(i).getHexStringTag());
            stringBuilder.append(list.get(i).getHexStringLength());
            stringBuilder.append(list.get(i).getHexStringValue());
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "MessagePackageEntity{" +
                "hexStringSessionId='" + hexStringSessionId + '\'' +
                ", hexStringMessageNumber='" + hexStringMessageNumber + '\'' +
                ", hexStringDatalength='" + hexStringDatalength + '\'' +
                ", hexStringMessageHeader='" + hexStringMessageHeader + '\'' +
                ", yiZhengTlvList="  +getListString(yiZhengTlvList)+
                '}';
    }

    public String  getListString(List<TlvEntity> yiZhengTlvList){
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<yiZhengTlvList.size();i++){
            stringBuilder.append(yiZhengTlvList.get(i).toString());
        }
        return  stringBuilder.toString();
    }

}
