package de.blinkt.openvpn.http;



/**
 * Created by Administrator on 2016/9/6 0006.
 */
public interface InterfaceCallback {
	void rightComplete(int cmdType, CommonHttp object);

	void errorComplete(int cmdType, String errorMessage);

	void noNet();
}