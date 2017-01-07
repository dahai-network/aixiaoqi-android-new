package de.blinkt.openvpn.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.DataEntitiy;
;

/**
 * Created by Administrator on 2016/10/7 0007.
 */
public class SportRecordDateHttp extends BaseHttp {

	String dateTime;

	public List<DataEntitiy> getDataEntitiyList() {
		return dataEntitiyList;
	}


    private List<DataEntitiy> dataEntitiyList=new ArrayList<>();
    public SportRecordDateHttp(InterfaceCallback interfaceCallback, int cmdType_,String dateTime){
       super(interfaceCallback,cmdType_);
        this.dateTime=dateTime;


    }
    @Override
    protected void BuildParams() throws Exception {
		super.BuildParams();
        sendMethod_=GET_MODE;
        slaverDomain_= HttpConfigUrl.SPORT_GET_RECORD_DATE;
        params.put("startDate",dateTime);
        params.put("days","31");
    }

	@Override
	protected void parseObject(String response) {
		dataEntitiyList = new Gson().fromJson(response, new TypeToken<List<DataEntitiy>>() {
		}.getType());
	}


}
