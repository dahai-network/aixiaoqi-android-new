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
	public static String bytesToHexString(byte[] src,int length) {
		return bytesToHexString( src,0,length);
	}
	public static String bytesToHexString(byte[] src ) {

		return bytesToHexString( src,0,src.length);
	}
	public static String bytesToHexString(byte[] src, int startPos, int endpos) {
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
	 * @param c char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}


}
