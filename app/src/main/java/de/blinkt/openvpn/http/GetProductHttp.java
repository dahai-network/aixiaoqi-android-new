package de.blinkt.openvpn.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.model.ProductEntity;

/**
 * Created by Administrator on 2017/4/8.
 */

public class GetProductHttp extends BaseHttp {
	private List<ProductEntity> productList;

	public List<ProductEntity> getProductEntity() {
		if (productList == null) {
			productList = new ArrayList<>();
		}
		return productList;
	}

	public GetProductHttp(InterfaceCallback call, int cmdType_, String... params) {
		super(call, cmdType_, false, GET_MODE, HttpConfigUrl.GET_PRODUCTS);

	}


	@Override
	protected void parseObject(String response) {
		productList = new Gson().fromJson(response, new TypeToken<List<ProductEntity>>() {
		}.getType());
	}
}
