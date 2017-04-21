package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.BlackListEntity;


/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class BlackListGetHttp extends BaseHttp {
    public List<BlackListEntity> getBlackListEntities() {
        if(blackListEntities==null){
            blackListEntities=new ArrayList<>();
        }
        return blackListEntities;
    }

    private  List<BlackListEntity> blackListEntities;


    public  BlackListGetHttp(InterfaceCallback call,int cmdType_){
        super(call,cmdType_,false,GET_MODE,HttpConfigUrl.BLACK_LIST_GET);
    }

    @Override
    protected void parseObject(String response) {

        blackListEntities = new Gson().fromJson(response, new TypeToken<List<BlackListEntity>>() {
        }.getType());
    }
}
