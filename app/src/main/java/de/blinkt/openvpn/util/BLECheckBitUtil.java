package de.blinkt.openvpn.util;

/**
 * Created by Administrator on 2016/10/21.
 */

public class BLECheckBitUtil {
	//异或和
	public static byte getXor(byte[] datas) {

		byte temp = datas[0];
		int length = datas.length;
		for (byte i = 1; i < length; i++) {
			temp ^= datas[i];
		}

		return temp;
	}
}
