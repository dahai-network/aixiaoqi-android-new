package de.blinkt.openvpn.util;

import de.blinkt.openvpn.util.pinyin.CharacterParser;

/**
 * Created by Administrator on 2016/11/21 0021.
 */
public class PinYinConverNumber {
	private static PinYinConverNumber instance;
	private static CharacterParser characterParser;
	private String TAG = "PinYinConverNumber";

	private PinYinConverNumber() {

	}

	public static PinYinConverNumber getInstance() {
		if (instance == null) {
			synchronized (PinYinConverNumber.class) {
				characterParser = CharacterParser.getInstance();
				instance = new PinYinConverNumber();
			}
		}
		return instance;
	}

	public String[] getNameNum(String name) {

		try {
			StringBuilder stringBuilder = new StringBuilder();
			StringBuilder stringheaderBuilder = new StringBuilder();
			String[] str = new String[2];
			if (name != null && name.length() != 0) {
				String litterName = characterParser.getSelling(name).toLowerCase();
				for (int i = 0; i < litterName.length(); i++) {
					stringBuilder.append(getOneNumFromAlpha(litterName.charAt(i)));

				}
				str[0] = stringBuilder.toString();
				int len = name.length();
				for (int i = 0; i < len; i++) {
					String tmp = name.substring(i);
					stringheaderBuilder.append(getOneNumFromAlpha(characterParser.getSelling(tmp).toLowerCase().charAt(0)));
				}
				str[1] = stringheaderBuilder.toString();
				return str;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public char getOneNumFromAlpha(char firstAlpha) {
		switch (firstAlpha) {
			case 'a':
			case 'b':
			case 'c':
			case '2':
				return '2';
			case 'd':
			case 'e':
			case 'f':
			case '3':
				return '3';
			case 'g':
			case 'h':
			case 'i':
			case '4':
				return '4';
			case 'j':
			case 'k':
			case 'l':
			case '5':
				return '5';
			case 'm':
			case 'n':
			case 'o':
			case '6':
				return '6';
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case '7':
				return '7';
			case 't':
			case 'u':
			case 'v':
			case '8':
				return '8';
			case 'w':
			case 'x':
			case 'y':
			case 'z':
			case '9':
				return '9';
			default:
				return '0';
		}
	}
}
