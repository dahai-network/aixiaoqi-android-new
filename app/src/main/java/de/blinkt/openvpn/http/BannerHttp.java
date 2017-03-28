package de.blinkt.openvpn.http;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cn.com.johnson.model.IndexBannerEntity;
import de.blinkt.openvpn.constant.HttpConfigUrl;

/**
 * Created by Administrator on 2016/9/28.
 */

public class BannerHttp extends BaseHttp {
	private List<IndexBannerEntity> bannerList;

	public List<IndexBannerEntity> getBannerList() {
		if(bannerList==null){
			bannerList=new ArrayList<>();
		}
		return bannerList;
	}

	public BannerHttp(InterfaceCallback call, int cmdType_) {
		super(call,cmdType_,false,GET_MODE,HttpConfigUrl.INDEX_BANNER);

	}



	@Override
	protected void parseObject(String response) {
		bannerList = new Gson().fromJson(response, new TypeToken<List<IndexBannerEntity>>() {
		}.getType());
	}


}
