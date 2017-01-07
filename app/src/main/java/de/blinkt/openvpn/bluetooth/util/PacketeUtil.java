package de.blinkt.openvpn.bluetooth.util;

import java.util.ArrayList;

/**
 * 用于分包组包（包信息去除不要，并把有效数据放在一个大包里面。）
 * Created by Administrator on 2016/9/7.
 */
public class PacketeUtil {
	//每个数据包最大长度
	private static int PACKET_EACH_LENGTH = 14 * 2;

	//分包
	public static String[] Separate(String message) {

		String subPacket;
		String[] packets;

		int subPacketLength = message.length();
		int totalNum = 0;

		int startPos = 0;
		int endPos = 0;

		totalNum = (message.length() + PACKET_EACH_LENGTH - 1) / PACKET_EACH_LENGTH;
		packets = new String[totalNum];
		for (int i = 0; i < totalNum; i++) {
			startPos = i * PACKET_EACH_LENGTH;
			endPos = i == totalNum - 1 ? message.length() : startPos + PACKET_EACH_LENGTH;
//			subPacketLength = endPos - startPos;

			subPacket = String.format("AADA%02X%02X%02X", subPacketLength / 2, totalNum, i + 1);
			//添加0
			String eachSeparateMessage = message.substring(startPos, endPos);
			if (eachSeparateMessage.length() == (14 * 2)) {
				subPacket += eachSeparateMessage;
			} else {
				//补上0
				int needToAdd0Count = 28 - eachSeparateMessage.length();
				String addZeroStr = "";
				for (int j = 0; j < needToAdd0Count; j++) {
					addZeroStr += "0";
				}
				subPacket += (eachSeparateMessage + addZeroStr);
			}

			packets[i] = subPacket;
		}

		return packets;

	}

	//组包
	public static String Combination(ArrayList<String> message) {
		//存储加入的byte
		StringBuilder builder = new StringBuilder();
		int size = message.size();
		for (int i = 0; i < size; i++) {
			String eachCombindMessage = message.get(i).substring(10, message.get(i).length());
			builder.append(eachCombindMessage);
		}
		return builder.toString();
	}

	//组包(历史步数)
	public static String CombinationForHistory(ArrayList<String> message) {
		//存储加入的byte
		StringBuilder builder = new StringBuilder();
		int size = message.size();
		for (int i = 0; i < size; i++) {
			String eachCombindMessage = message.get(i).substring(10, message.get(i).length() - 2);
			if (eachCombindMessage.length() == (12 * 2)) {
				builder.append(eachCombindMessage);
			}
		}
		return builder.toString();
	}


}
