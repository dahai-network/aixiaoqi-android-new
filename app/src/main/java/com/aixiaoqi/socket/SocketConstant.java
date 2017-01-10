package com.aixiaoqi.socket;

import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2016/12/17 0017.
 */
public class SocketConstant {

	public static String[] CONNENCT_TAG = {"01",//请求GoIp模块
			Integer.toHexString(101),//链接存活时间
			Integer.toHexString(107),//是否请求预读数据
			Integer.toHexString(120)//服务寻址sessionID
			, Integer.toHexString(121)//数据链接协议
			, Integer.toHexString(150)//uuwifi设备编号vid
			, Integer.toHexString(160)//模块位置描述goip
			, Integer.toHexString(170)//模块IMEI
			, Integer.toHexString(171)//模块类型mod_type
			, Integer.toHexString(172)//模块版本 mod_ver
			, Integer.toHexString(180)//sim卡位置描述
			, Integer.toHexString(190)//SIM卡ICCID
			, Integer.toHexString(191)//SIM卡IMSI
			, Integer.toHexString(192)//SIM卡卡号，number
			, Integer.toHexString(193)//SIM卡余额
			, Integer.toHexString(198)//预读数据压缩前的长度
			, Integer.toHexString(199)};//预读数据内容
	public static String[] CONNENCT_VALUE = {"01",//01，请求GoIp模块的值
			"b4"//101,链接存活时间
			, "01"//107,是否请求预读数据
			,RadixAsciiChange.convertStringToHex(SharedUtils.getInstance().readString(Constant.TOKEN))//120,服务寻址sessionID
//			, RadixAsciiChange.convertStringToHex("43fa381a01a3cd934c71fdc54dcdc2f")//120,服务寻址sessionID
			, "01"//121,数据链接协议
			, "757573696d00"//150,uuwifi设备编号vid
			, "757573696d2e303100"//160,模块位置描述goip
			, "00"//170,模块IMEI
			, "00"//171,模块类型mod_type
			, "00"//172,模块版本
			, "00"//180,sim卡位置描述
			, "",//190,SIM卡ICCID
			"",//191,SIM卡IMSI
			"00"//192,SIM卡卡号，number
			, "00"//193,SIM卡余额
			, "15f1"//198,预读数据压缩前的长度
			, ""
			//199,预读数据内容
	};
	public static String SESSION_ID = "00000000";
	public static String SDK_TAG = "c7";
	public static String SDK_VALUE = "";

	public static String UPDATE_CONNECTION = "108a0500";
	public static String CONNECTION = "108a0400";
	public static String PRE_DATA = "108a9000";
	public static final String SESSION_ID_TEMP = "00000000";
	public static final String hostIP = "120.25.91.50";
	public static final int port = 20016;

	public static final String EN_APPEVT_NONE = "00";
	public static final String EN_APPEVT_SETSIMTYPE = "01";
	public static final String EN_APPEVT_CMD_SETRST = "02";
	public static final String EN_APPEVT_CMD_SIMCLR = "03";
	public static final String EN_APPEVT_RSTRSP = "04";
	public static final String EN_APPEVT_PRDATA = "05";
	public static final String EN_APPEVT_SIMDATA = "06";
	public static final String EN_APPEVT_SIMINFO = "07";

	public static final int REGISTER_FAIL = 1;
	public static final int REGISTER_FAIL_IMSI_IS_NULL = 2;
	public static final int REGISTER_FAIL_IMSI_IS_ERROR = 3;
	public static  int REGISTER_STATUE_CODE= 0;//0，表示还没有开始注册。1，表示获取预读取数据没有完成。2，表示注册中，3表示注册成功
}
