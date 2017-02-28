package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.com.johnson.model.HotPackageEntity;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/28.
 */

public class GetHotHttp extends BaseHttp {
	private int pageSize;
	private List<HotPackageEntity> hotPackageEntityList;

	public List<HotPackageEntity> getHotPackageEntityList() {
		if(hotPackageEntityList==null){
			hotPackageEntityList=new ArrayList<>();
		}
		return hotPackageEntityList;
	}

	public GetHotHttp(InterfaceCallback call, int cmdType_, int pageSize) {
	super(call,cmdType_);
		this.pageSize = pageSize;

	}


	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		slaverDomain_ = HttpConfigUrl.GET_HOT;
		sendMethod_ = GET_MODE;
		params.put("pageSize", URLEncoder.encode(pageSize + "", "utf-8"));
	}

	@Override
	protected void parseObject(String response) {
		hotPackageEntityList = new Gson().fromJson(response, new TypeToken<List<HotPackageEntity>>() {
		}.getType());
	}




}
