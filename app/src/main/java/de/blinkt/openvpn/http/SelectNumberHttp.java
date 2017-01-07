package de.blinkt.openvpn.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.SelectNumberEntity;
import de.blinkt.openvpn.model.SmsEntity;

/**
 * Created by Administrator on 2016/11/30 0030.
 */
public class SelectNumberHttp extends BaseHttp{
    private String province;
    private String city;
    private String mobileNumber;
    private  int pageNumber;
    private  int pageSize;
   SelectNumberEntity selectNumberEntity;

    public  SelectNumberEntity getSelectNumberEntity(){
        if(selectNumberEntity==null){
            selectNumberEntity=new SelectNumberEntity();
        }

        return selectNumberEntity;
    }
    public SelectNumberHttp(InterfaceCallback call, int cmdType_, int pageNumber,int pageSize, String province, String city,String MobileNumber) {
        super(call,cmdType_);
        this.province = province;
        this.city = city;
        this.mobileNumber=MobileNumber;
        this.pageNumber=pageNumber;
        this.pageSize=pageSize;
    }

    @Override
    protected void BuildParams() throws Exception {
        super.BuildParams();
        sendMethod_=GET_MODE;
        slaverDomain_= HttpConfigUrl.GET_SELECT_NUMBER;
        params.put("PageNumber",pageNumber+"");
        params.put("PageSize",pageSize+"");
        params.put("Province",province);
        params.put("City",city);
        if(!TextUtils.isEmpty(mobileNumber))
            params.put("MobileNumber",mobileNumber);
    }

    @Override
    protected void parseObject(String response) {
        selectNumberEntity = new Gson().fromJson(response,SelectNumberEntity.class);
    }
}
