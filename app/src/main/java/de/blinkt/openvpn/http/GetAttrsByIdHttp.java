package de.blinkt.openvpn.http;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.GetAttrsByIdEntity;

/**
 * Created by Administrator on 2017/5/16.
 */

public class GetAttrsByIdHttp extends BaseHttp {

	private List<GetAttrsByIdEntity.ListBean> attrsList;

	public List<GetAttrsByIdEntity.ListBean> getAttrsList() {
		if (attrsList == null) {
			attrsList = new ArrayList<>();
		}
		return attrsList;
	}

	public GetAttrsByIdHttp(InterfaceCallback interfaceCallback, int cmdType_, String... params) {
		super(interfaceCallback, cmdType_, GET_MODE, HttpConfigUrl.GET_ATTRS_BY_ID, params);
	}

	@Override
	protected void BuildParams() throws Exception {
		super.BuildParams();
		params.put("id", valueParams[0]);
	}

	@Override
	protected void parseObject(String response) {
		attrsList = new Gson().fromJson(response, GetAttrsByIdEntity.class).getList();
	}
}
