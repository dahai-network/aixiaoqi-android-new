package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.DataEntitiy;

/**
 * Created by Administrator on 2016/10/7 0007.
 */
public class SportRecordDateHttp extends BaseHttp {



	public List<DataEntitiy> getDataEntitiyList() {
		return dataEntitiyList;
	}


    private List<DataEntitiy> dataEntitiyList=new ArrayList<>();
    public SportRecordDateHttp(InterfaceCallback interfaceCallback, int cmdType_,String...params ){
       super(interfaceCallback,cmdType_,GET_MODE,HttpConfigUrl.SPORT_GET_RECORD_DATE,params);
    }
    @Override
    protected void BuildParams() throws Exception {
		super.BuildParams();
        params.put("startDate",valueParams[0]);
        params.put("days","31");
    }

	@Override
	protected void parseObject(String response) {
		dataEntitiyList = new Gson().fromJson(response, new TypeToken<List<DataEntitiy>>() {
		}.getType());
	}


}
