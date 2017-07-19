package de.blinkt.openvpn.bluetooth.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * 用于分包组包（包信息去除不要，并把有效数据放在一个大包里面。）
 * Created by Administrator on 2016/9/7.
 */
public class PacketeUtil {


	private static final int startLength = 15 * 2;
	private static final int onrPackageLength = 17 * 2;
	//分包
	public static String[] Separate(String message, String type) {
		String[] packets;
		if (message.length() / 2 <= 15) {
			packets = new String[1];
			int length = message.length() / 2 + 2;
			Log.e("length", "length=" + Integer.toHexString(message.length() / 2 + 2));
			if (length < 16) {
				packets[0] = "88800" + Integer.toHexString(message.length() / 2 + 2) + type + message;
			} else {
				packets[0] = "8880" + Integer.toHexString(message.length() / 2 + 2) + type + message;
			}
			Log.e("PacketeUtil","packets"+packets[0]);
		} else {
			int totalNum = ((message.length() - startLength) % (onrPackageLength) != 0 ? ((message.length() - startLength) / (onrPackageLength) + 1) : (message.length() - startLength) / (onrPackageLength)) + 1;
			Log.e("PacketeUtil", "totalNum=" + totalNum);
			packets = new String[totalNum];
			for (int i = 0; i < totalNum; i++) {
				if (i == 0) {
					packets[i] = "880011" + type + message.substring(0, startLength);
				} else if (i == totalNum - 1) {
					packets[i] = String.format("88%02X%02X", 0x80 + i, (message.length() - (startLength + onrPackageLength * (i - 1))) / 2) + message.substring(startLength + onrPackageLength * (i - 1), message.length());
				} else {
					packets[i] = String.format("88%02X%02X", i, 17) + message.substring(startLength + onrPackageLength * (i - 1), startLength + onrPackageLength * i);
				}
				Log.e("PacketeUtil", "packets[" + i + "]=" + packets[i]);
			}

		}

		return packets;
	}

	//组包

	@NonNull
	public static String Combination(ArrayList<String> message) {
		//存储加入的byte
		StringBuilder builder = new StringBuilder();
		int size = message.size();
		Log.d("PacketeUtil", "Combination: "+size);
		for (int i = 0; i < size; i++) {
			String eachCombindMessage;
			if ((Integer.parseInt(message.get(i).substring(2, 4), 16) & 127) == 0) {

				eachCombindMessage = message.get(i).substring(10, message.get(i).length());
			} else {

				eachCombindMessage = message.get(i).substring(6, message.get(i).length());
			}
			builder.append(eachCombindMessage);
		}
        Log.d("PacketeUtil", "Combination: "+builder.toString());
        return builder.toString();
	}



}
