package com.aixiaoqi.socket;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/1/4 0004.
 */
public class RadixAsciiChange {

    public static String convertHexToString(String hex) {
if(TextUtils.isEmpty(hex)){
	return "";
}
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }


    public static String convertStringToHex(String str){
		if(TextUtils.isEmpty(str)){
			return "";
		}
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }
        return hex.toString();
    }
}
