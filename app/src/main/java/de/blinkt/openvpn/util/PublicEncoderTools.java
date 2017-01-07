package de.blinkt.openvpn.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class PublicEncoderTools {

	public static String UnicodeTenToDefaultCode(String unicodetenstr) {
		String regExp = "&#\\d*;";
		Matcher ms = Pattern.compile(regExp).matcher(unicodetenstr);
		StringBuffer sb = new StringBuffer();
		while (ms.find()) {
			String s = ms.group(0);
			s = s.replaceAll("(&#)|;", "");
			char c = (char) Integer.parseInt(s);
			ms.appendReplacement(sb, Character.toString(c));
		}
		ms.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 16进制的字符串表示转成字节数组
	 *
	 * @param hexString 16进制格式的字符串
	 * @return 转换后的字节数组
	 **/
	public static byte[] hexStr2ByteArray(String hexString) {
		// if (StringUtils.isEmpty(hexString))
		// throw new
		// IllegalArgumentException("this hexString must not be empty");

		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		int length = byteArray.length;
		for (int i = 0; i < length ; i++) {
			// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
			// 将hex 转换成byte "&" 操作为了防止负数的自动扩展
			// hex转换成byte 其实只占用了4位，然后把高位进行右移四位
			// 然后“|”操作 低四位 就能得到 两个 16进制数转换成一个byte.
			//
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		System.out.println("come byteArray!!!");
		return byteArray;

	}

	/**
	 * 转换字节数组为16进制字串
	 *
	 * @param b 字节数组
	 * @return 16进制字串
	 */
	/*
	public static String byteArrayToHexString(byte[] b) { 
	        StringBuffer resultSb = new StringBuffer(); 
	        for (int i = 0; i < b.length; i++) { 
	         resultSb.append(byteToHexString(b[i])); 
        } 
	        return resultSb.toString(); 
      } 
	
	private final static String[] hexDigits = { 
		           "0", "1", "2", "3", "4", "5", "6", "7", 
		          "8", "9", "a", "b", "c", "d", "e", "f"}; 
	
	private static String byteToHexString(byte b) { 
		        int n = b; 
		         if (n < 0) 
		            n = 256 + n; 
		           int d1 = n / 16; 
		           int d2 = n % 16; 
		     return hexDigits[d1] + hexDigits[d2]; 
	   } 
	
	*/
	public static String signature(String key, String param) {
		try {
			System.out.println("key:" + key + "|param:" + param);
			byte[] keys = key.getBytes("GBK");
			byte[] params = param.getBytes("GBK");
			System.out.println("signature:" + Base64.encodeToString(PublicEncoderTools.encryptHMAC(params, keys), Base64.DEFAULT).trim());
			return Base64.encodeToString(PublicEncoderTools.encryptHMAC(params, keys), Base64.DEFAULT).trim();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 二行制转字符串
	 *
	 * @param b
	 * @return
	 */
	public static String byte2String(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		int length = b.length;
		for (int n = 0; b != null && n < length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toUpperCase(Locale.CHINA);
	}

	private static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
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
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			//md.update(resultString.getBytes());
			// resultString=bytesToHexString(md.digest());

			// resultString=byteArrayToHexString(md.digest(resultString.getBytes()));
			resultString = bytesToHexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {

		}
		return resultString;
	}

	/**
	 * sha-1加密方式
	 *
	 * @param origin
	 * @return
	 */
	public static String SHAEncode(String origin) {
		String resultString = null;

		try {
			resultString = new String(origin);

			MessageDigest md = MessageDigest.getInstance("SHA-1");
			//md.update(resultString.getBytes());
			// resultString=bytesToHexString(md.digest());

			// resultString=byteArrayToHexString(md.digest(resultString.getBytes()));
			resultString = bytesToHexString(md.digest(resultString.getBytes()));
		} catch (Exception ex) {

		}
		return resultString;
	}

	/**
	 * HMAC 加密
	 *
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception128
	 */
	public static byte[] encryptHMAC(byte[] data, byte[] key) throws Exception {
		String KEY_MAC = "HmacSHA256";
		SecretKey secretKey = new SecretKeySpec(key, KEY_MAC);
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return mac.doFinal(data);
	}

}
