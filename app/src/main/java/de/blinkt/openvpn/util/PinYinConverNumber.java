package de.blinkt.openvpn.util;

import de.blinkt.openvpn.util.pinyin.CharacterParser;

/**
 * Created by Administrator on 2016/11/21 0021.
 */
public class PinYinConverNumber {
    private static PinYinConverNumber instance;
    private static  CharacterParser characterParser;
    private PinYinConverNumber(){

    }
    public static PinYinConverNumber getInstance(){
        if(instance==null){
            synchronized (PinYinConverNumber.class){
                characterParser=CharacterParser.getInstance();
                instance=new PinYinConverNumber();
            }
        }
        return instance;
    }
    public  String getNameNum(String name) {

        try {
            if (name != null && name.length() != 0) {
                int len = name.length();
                char[] nums = new char[len];
                for (int i = 0; i < len; i++) {
                    String tmp = name.substring(i);
                    nums[i] = getOneNumFromAlpha(characterParser.getSelling(tmp).toLowerCase().charAt(0));
                }
                return new String(nums);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public  char getOneNumFromAlpha(char firstAlpha) {
        switch (firstAlpha) {
            case 'a':
            case 'b':
            case 'c':
                return '2';
            case 'd':
            case 'e':
            case 'f':
                return '3';
            case 'g':
            case 'h':
            case 'i':
                return '4';
            case 'j':
            case 'k':
            case 'l':
                return '5';
            case 'm':
            case 'n':
            case 'o':
                return '6';
            case 'p':
            case 'q':
            case 'r':
            case 's':
                return '7';
            case 't':
            case 'u':
            case 'v':
                return '8';
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                return '9';
            default:
                return '0';
        }
    }
}
