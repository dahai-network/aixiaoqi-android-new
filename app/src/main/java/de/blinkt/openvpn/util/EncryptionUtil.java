package de.blinkt.openvpn.util;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import de.blinkt.openvpn.bluetooth.util.HexStringExchangeBytesUtil;

/**
 * Created by Administrator on 2017/4/26.
 */

public class EncryptionUtil {

	//随机数字符串用于加密算法
	public static String random8NumberString;
	//共享密钥
	private static byte[] tribleKey = {(byte) 122, (byte) 59, (byte) 89, (byte) 100, (byte) 202, (byte) 142, (byte) 157, (byte) 242,
			(byte) 122, (byte) 59, (byte) 89, (byte) 100, (byte) 202, (byte) 142, (byte) 157, (byte) 242};

	public static boolean isPassEncrypt(String receiveBlueEncrypt) {
		String result = random8NumberString;
		try {
			result = TribleDESencrypt(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (result != null) {
			result = result.substring(0, 16);
			Log.i("Encryption", "最终自身处理加密出来的byte数组："
					+ result + "\n接收到的byte数组" + receiveBlueEncrypt + "，是否匹配：" + result.equals(receiveBlueEncrypt));
			return result.equals(receiveBlueEncrypt);
		} else {
			Log.i("Encryption", "result为空！");
		}
		return false;
	}

	public static byte[] getMacBytes(String mac) {
		byte[] macBytes = new byte[6];
		String[] strArr = mac.split(":");

		for (int i = 0; i < strArr.length; i++) {
			int value = Integer.parseInt(strArr[i], 16);
			macBytes[i] = (byte) value;
		}
		return macBytes;
	}

	//随机15位数字输出
	public static String random8Number() {
		StringBuilder builder = new StringBuilder();
		String randomNum;
		for (int i = 0; i < 8; i++) {
			randomNum = Integer.toHexString((int) Math.rint(0xff * Math.random()));
			if (randomNum.length() == 1) {
				randomNum = "0" + randomNum;
			}
			Log.i("Encryption", "随机数：" + randomNum);
			builder.append(randomNum);
		}
		random8NumberString = builder.toString();
		Log.i("Encryption", "随机数总：" + random8NumberString);
		return builder.toString();
	}


	// AES加密方法,以后使用
	public static byte[] encrypt(byte[] clear, byte[] key) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	// AES解密方法，以后使用
	public static byte[] decrypt(byte[] encrypted, byte[] key)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	// 3DES加密方法
	public static String TribleDESencrypt(String encryptString) throws Exception {
		SecretKeySpec key = new SecretKeySpec(tribleKey, "DESede");
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedData = cipher.doFinal(HexStringExchangeBytesUtil.hexStringToBytes(encryptString), 0, 8);

		return HexStringExchangeBytesUtil.bytesToHexString(encryptedData);
	}

	// 3DES解密方法
	public static String TribleDESdecrypt(String decryptString) throws Exception {
		byte[] byteMi = Base64.decode(decryptString);
		SecretKeySpec key = new SecretKeySpec(tribleKey, "DESede");
		Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte decryptedData[] = cipher.doFinal(byteMi);

		return HexStringExchangeBytesUtil.bytesToHexString(decryptedData);
	}

}
