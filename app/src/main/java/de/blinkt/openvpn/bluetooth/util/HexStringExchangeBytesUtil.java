package de.blinkt.openvpn.bluetooth.util;

/**
 * Created by Administrator on 2016/9/7.
 */
public class HexStringExchangeBytesUtil {
	/**
	 * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 *
	 * @param src byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src, int length) {
		if (src == null) {
			return "";
		}
		return bytesToHexString(src, 0, length);
	}

	public static String bytesToHexString(byte[] src) {
		if (src == null) {
			return "";
		}
		return bytesToHexString(src, 0, src.length);
	}

	public static String bytesToHexString(byte[] src, int startPos, int endpos) {
		if (src == null) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder("");

		for (int i = startPos; i < endpos; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}

			stringBuilder.append(hv);
		}

		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 *
	 * @param hexString the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 *
	 * @param c char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	// 十六进制转二进制
	public static String HToB(String a) {
		String b = Integer.toBinaryString(Integer.valueOf(toD(a, 16)));
		return b;

	}

	// 二进制转十六进制
	public static String BToH(String a) {
		// 将二进制转为十进制再从十进制转为十六进制
		String b = Integer.toHexString(Integer.valueOf(toD(a, 2)));
		return b;
	}

	// 任意进制数转为十进制数
	public static String toD(String a, int b) {
		int r = 0;
		for (int i = 0; i < a.length(); i++) {
			r = (int) (r + formatting(a.substring(i, i + 1))
					* Math.pow(b, a.length() - i - 1));
		}
		return String.valueOf(r);
	}

	// 将十六进制中的字母转为对应的数字
	public static int formatting(String a) {
		int i = 0;
		for (int u = 0; u < 10; u++) {
			if (a.equals(String.valueOf(u))) {
				i = u;
			}
		}
		if (a.equals("a")) {
			i = 10;
		}
		if (a.equals("b")) {
			i = 11;
		}
		if (a.equals("c")) {
			i = 12;
		}
		if (a.equals("d")) {
			i = 13;
		}
		if (a.equals("e")) {
			i = 14;
		}
		if (a.equals("f")) {
			i = 15;
		}
		return i;
	}
}
