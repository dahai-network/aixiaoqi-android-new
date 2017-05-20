package de.blinkt.openvpn.util;

import java.security.MessageDigest;


public class PublicEncoderTools {


	private static String bytesToHexString(byte[] bytes) {

		StringBuilder sb = new StringBuilder();
		int length = bytes.length;
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public static String MD5Encode(String origin) {
		String resultString = null;

		try {
			resultString = origin;
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = bytesToHexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

}
