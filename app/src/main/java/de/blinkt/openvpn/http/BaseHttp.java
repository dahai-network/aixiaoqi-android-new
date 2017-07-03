package de.blinkt.openvpn.http;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/26 0026.
 */
public class BaseHttp extends CommonHttp {
	protected InterfaceCallback interfaceCallback;
	protected int cmdType_;
	protected boolean isCreateHashMap = true;
	protected String[] valueParams;
	private String TAG = "JSON";

	public BaseHttp(InterfaceCallback interfaceCallback, int cmdType_) {
		this.interfaceCallback = interfaceCallback;
		this.cmdType_ = cmdType_;
	}

	public BaseHttp(InterfaceCallback interfaceCallback, int cmdType_, String url, String... params) {
		initParams(interfaceCallback, cmdType_, url);
		valueParams = params;
	}

	public BaseHttp(InterfaceCallback interfaceCallback, int cmdType_, int requestType, String url, String... params) {

		initParams(interfaceCallback, cmdType_, url);
		valueParams = params;
		sendMethod_ = requestType;
	}

	private void initParams(InterfaceCallback interfaceCallback, int cmdType_, String url) {
		this.interfaceCallback = interfaceCallback;
		Log.d(TAG, "initParams: cmdType_="+cmdType_+"--url="+url);
		this.cmdType_ = cmdType_;
		slaverDomain_ = url;
	}

	public BaseHttp(InterfaceCallback interfaceCallback, int cmdType_, boolean isCreateHashMap, int requestType, String url) {
		initParams(interfaceCallback, cmdType_, url);
		this.isCreateHashMap = isCreateHashMap;
		sendMethod_ = requestType;
	}

	public BaseHttp() {

	}

	@Override
	protected void BuildParams() throws Exception {
		Log.d(TAG, "BuildParams: params="+params+"--isCreateHashMap="+isCreateHashMap);
		if (params == null && isCreateHashMap) {
			Log.d(TAG, "BuildParams: params"+params);
			params = new HashMap<>();
		}
	}


	protected void parseObject(String response) {

	}

	@Override
	protected void parseResult(String response) {
		Log.e(TAG, "JSON--- 日志：" + response);
		if (!TextUtils.isEmpty(response)) {

			try{
				parseObject(response);
			}catch (JsonSyntaxException e){
				e.printStackTrace();
			}
		}

		interfaceCallback.rightComplete(cmdType_, this);

	}

	@Override
	protected void errorResult(String s) {
		interfaceCallback.errorComplete(cmdType_, s);
	}

	@Override
	protected void noNet() {
		interfaceCallback.noNet();
	}
}
