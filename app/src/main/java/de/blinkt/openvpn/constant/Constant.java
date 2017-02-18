package de.blinkt.openvpn.constant;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class Constant {

	public static final boolean IS_DEBUG = true;
	public static final String BLUETOOTH_NAME = "unitoys";
//	public static final String BLUETOOTH_NAME = "ZiJian";

	//购买套餐数量限制
	public static final int LIMIT_COUNT = 30;
	//保存登录信息
	public static final String ISFIRSTIN = "isfirstin";
	public static final String USER_NAME = "userName";
	public static final String PASSWORD = "password";
	public static final String TOKEN = "token";
	public static final String TEL = "tel";
	public static final String LOGIN_DATA = "loginDate";
	public static final String CONFIG_TIME = "config_time";
	public static final String USER_HEAD = "UserHead";
	public static final String GENDER = "gender";
	public static final String HEIGHT = "height";
	public static final String WEIGHT = "weight";
	public static final String SOPRT_TARGET = "sport_target";
	public static final String NICK_NAME = "nickname";
	public static final String BRITHDAY = "brithday";
	public static final String ASTERISK_IP_IN = "AsteriskIp";
	public static final String ASTERISK_PORT_IN = "AsteriskPort";
	public static final String ASTERISK_IP_OUT = "AsteriskIpOut";
	public static final String ASTERISK_PORT_OUT = "AsteriskPortOut";
	public static final String PUBLIC_PASSWORD = "publicpassword";
	public static final String IMEI = "BraceletIMEI";
	public static final String BRACELETVERSION = "BraceletVersion";
	public static final String NULLCARD_SERIALNUMBER = "NULLCARD_SERIALNUMBER";
	//旧版本蓝牙设备
	public static final String ELECTRICITY = "electricity";
	public static final String PHONE_NUMBER_LIST = "phone_number_list";
	//更新请求的时间间隔（一小时提示一次升级）
	public static final String UPGRADE_INTERVAL = "upgrade_interval";
	//极光推送标签
	public static final String JPUSH_ALIAS = "jpush_alias";
	public static final String JPUSH_ALIAS_SUCCESS = "success";
	//页码
	public static final int PAGESIZE = 10;
	//统计错误信息
	public static final String APPID = "0cd55873f3";

	//增加http头部信息

	public static final String PARTNER = "partner";
	public static final String EXPIRES = "expires";
	public static final String SIGN = "sign";
	public static final int INPUT_TEXT = 1;
	public static final int DELETE_TEXT = 2;
	public static final String CALL_INCOMING = "来电";
	public static final String CALL_OUTGOING = "拨出";
	public static final String CALL_MISSED = "未接";
	//蓝牙指令
	public static String UP_TO_POWER = "888003100001";//上电指令，没有详细卡信息以及BB332211BB
	public static String UP_TO_POWER_DETAIL = "888003100002";//上电指令,只有详细卡信息以及BB665544BB
	public static String UP_TO_POWER_USED_TO_SDK = "888003100003";//上电指令,返回BB998877BB，用于SDK使用的上下电命令
	public static final String OFF_TO_POWER = "8880021100";//断电指令
	public static final String REAL_TIME_STEPS = "AA010401AE ";//实时步数
	public static final String HISTORICAL_STEPS = "8880020E00";//历史步数
	public static final String FIND_DEVICE = "8880020C00";//查找手环
	public static final String BASIC_MESSAGE = "8880020600";//基本信息获取
	public static final String SKY_UPGRADE_ORDER = "8880030a00b1";//空中升级指令
	//		public static final String FIND_VERSION = "AA0A0401A5";//版本号（以后的版本）
	public static final String RESTORATION = "8880020100";//复位命令

	public static final String BIND_SUCCESS = "AADD01DDAA";//绑定成功命令
	//		public static final String ANDROID_TARGET = "AA010400AF";//绑定成功命令
	//ICCID标记
	public static final String ICCID = "ICCID";
	//来电，短信，微信，QQ通知开关标记
	public static final String LIFT_WRIST = "LiftWristLight";
	public static final String COMING_TEL_REMIND = "NotificaCall";
	public static final String MESSAGE_REMIND = "NotificaSMS";
	public static final String WEIXIN_REMIND = "NotificaWeChat";
	public static final String QQ_REMIND = "NotificaQQ";

	//是否打开写卡流程，如果没有打开则是测试SIM卡是否连接上，如果否则为写卡流程，可以连接到GOIP
	public static boolean IS_TEXT_SIM = false;
	//注册卡回调类型
	public static final int REGIST_CALLBACK_TYPE = 0;
	//蓝牙连接状态int
	public static final int BLUE_CONNECTED_INT = 2;

	/**
	 * 支付宝支付业务：入参app_id
	 */
	public static final String ALI_APPID = "2016081201740861";
	/**
	 * 微信支付业务APPid
	 */
	public static final String WEIXIN_APPID = "wxff7e7ee82cd9afc4";

//	/**
//	 * 支付宝账户登录授权业务：入参pid值
//	 */
//	public static final String PID = "2088421645383390";
//	/**
//	 * 支付宝账户登录授权业务：入参target_id值
//	 */
//	public static final String TARGET_ID = "";

	/**
	 * 商户私钥，pkcs8格式
	 */
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJU3u9F8d4jxw1jk" +
			"RwGx2g9kOzP3Exv9U/lfxkaSuBuEWASLXI2cFdFkGOyD3mKMM5XTs9HYD4qLFXta" +
			"ldyC2lY0fxb80F/vRU+3Oe8D9TH/CJ7p6SeTG8MfeIeWEK8VIQr6NnM7ywwECvpG" +
			"8uElzfnGlUSB28cBMCqARYGlyWhfAgMBAAECgYBB+VJhXNa9BaeJNeTvKuNuyrIi" +
			"V6trRKZMK7xOl7Au+mSwHa3eLpS277rVV7iLedGU/PUUYqL8bmIhF/wKcxB1QAaK" +
			"pDpPv9SIAzfHLw+KuYv0JN3Ypvet+EtLKTO2k74oQGN/GTFp2mOtYKwfkU/lyO73" +
			"HcgTUbVBcRL5iLIHAQJBAMadZPoQ5CF2A2OBp7cfCEeHmhtxk6QQBQ3cTRLC2ZZ9" +
			"R8zgl3Hyqvx6/BT1muuu5DOmzUHmfSZR/BV9pVduQNcCQQDAVKq00NXmpqi0+esS" +
			"9iozsvBNY6sS8q2r5EpyWdnzyLE8x/B0vjNoai6AW/t4m0aMGrXmfEaonCOeMjWu" +
			"zTu5AkEAsfJAjx9lFWmjfZqjhhjClTuz4dSvf7Vuoc14LE/xHLigBLpQVaIiedVC" +
			"VxD5vSFTicdvbRSxmgyoOyT4Z037vwJAIjErI/gYfufUCFCB5R4URJqkM+3rJPQ1" +
			"weBVB91HbRqZv8d/zRFfTEnMOI+htkBMm23INtCTMziG8IHWn1vnKQJAbXasp5Ga" +
			"rlCFiEYDaQVmR+JQAwFC6Xd5V1xwFcEpdkcIyvw8wkWObbKz0oWrMkKgHqpj8kQ2" +
			"i+5eD/ECJXgy9w==";

	//写卡流程
	public static final String RECEIVE_NULL_CARD_CHAR = "0344";
	public static final String WRITE_CARD_STEP1 = "9f1";
	public static final String WRITE_CARD_91 = "91";
	public static final String WRITE_CARD_STEP4 = "d03281030125";
	public static final String WRITE_CARD_STEP5 = "9000";
	public static final String WRITE_CARD_STEP7 = "d04381030113";
	public static final String WRITE_CARD_STEP9 = "d02081030123";
	public static final String WRITE_CARD_STEP11 = "d03c8103012";

	//获取空卡流程
	public static final String GET_NULLCARDID = "9f0f";

	public static final String DOWNLOAD_PATH = "/sdcard/aixiaoqi";
	public static final String UPLOAD_PATH = "/aixiaoqi/upload.zip";
	public static final String DOWNLOAD_SUCCEED = "succeed";
	public static final String DOWNLOAD_FAIL = "fail";


	public static final int SAVE_REALNAME = 104;
	public static final int SAVE_ID_CARD_NUMBER = 101;
	public static final int SAVE_ADDRESS = 102;
	public static final int SAVE_SELECT_NUMBER = 103;
	public static final int ADD_PHONE_NUMBER = 105;
	//是否有订单参数
	public static final String ISHAVEORDER = "ishaveorder";
	public static final int NETWORK_CELL_PHONE = 1;
	public static final int SIM_CELL_PHONE = 2;

	//蓝牙操作时间限制（5秒内不可重复操作）
	public static final int REPEAT_OPERATE = 2500;


	//蓝牙发送类型

	public static final String SYSTEM_RESET = "0100";//系统复位

	public static final String SEND_PHONE_SYSTEM_TO_BLUETOOTH = "0200";//发送手机类型
	public static final String USER_NOTIFY = "0300";//是否使用通知
	public static final String MESSAGE_PUSH = "0400";//检测到有消息，消息推送
	public static final String UPDATE_TIME = "0500";//更新时间
	public static final String SYSTEM_BASICE_INFO = "0600";//系统基本信息请求
	public static final String BIND_DEVICE = "0700";//请求绑定
	public static final String BIND_STATUE = "0800";//是否绑定成功
	public static final String UNBIND_DEVICE = "0900";//解除绑定
	public static final String SKY_UPGRADE = "0a0001";//空中升级
	public static final String REQUEST_POWER = "0b00";//请求电量
	public static final String SEARCH_DEVICE = "0c00";//查找手环
	public static final String THE_HAND_FUNCTION = "0d00";//是否使用抬手功能
	public static final String HISTORY_STEP = "0e00";//历史步数
	public static final String SETTING_ALARM = "0f00";//设置闹钟
	public static final String SIM_UP_POWER = "1000";//对卡上电
	public static final String SIM_DOWN_POWER = "1100";//对卡断电
	public static final String READED_SIM_DATA = "1200";//读卡数据
	public static final String WRITE_SIM_DATA = "1300";//写卡数据
	public static final String WRITE_SIM_STEP_ONE ="A0A40000023F00";//写卡第一步
	public static final String WRITE_SIM_STEP_TWO ="A0A40000022F02";//写卡第一步
	public static final String WRITE_SIM_STEP_THREE ="A0B000000A";//写卡第一步
}
