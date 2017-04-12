package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import cn.com.johnson.model.SecurityConfig;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.UsageRemainEntity;

/**
 * Created by Administrator on 2017/4/12 0012.
 */

public class OrderUsageRemainHttp extends BaseHttp {
    public UsageRemainEntity getUsageRemainEntity() {
        if(usageRemainEntity==null){
            usageRemainEntity=new UsageRemainEntity();
        }
        return usageRemainEntity;
    }

    UsageRemainEntity usageRemainEntity;
    public OrderUsageRemainHttp(InterfaceCallback call, int cmdType_) {
        super(call,cmdType_,false,GET_MODE,HttpConfigUrl.GET_USER_ORDER_USAGE_REMAINING);
    }

    @Override
    protected void parseObject(String response) {
        usageRemainEntity  = new Gson().fromJson(response, UsageRemainEntity.class);
    }
}
