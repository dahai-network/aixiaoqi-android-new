package de.blinkt.openvpn.constant;

import android.os.Environment;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
public class Constant {

    public static final boolean IS_DEBUG = false;
    public static final boolean PRINT_LOGS = false;
    public static final String UNITOYS = "unitoys";
    public static final String UNIBOX = "unibox";

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
	public static final String BRACELETNAME = "braceletname";
	public static final String BRACELETTYPEINT = "bracelettypeint";
	public static final String BRACELETPOWER = "braceletpower";
	public static final String NULLCARD_SERIALNUMBER = "NULLCARD_SERIALNUMBER";
	public static final String VERSION_HEADER = "Version";
	public static final String TERMINAL_HEADER = "Terminal";
	public static final String PHONE_NUMBER_LIST = "phone_number_list";
	public static final String IS_NEED_UPGRADE_IN_HARDWARE = "is_need_upgrade_in_hardware";
	//运营商
	public static final String OPERATER = "operater";
	public static final String CHINA_TELECOM = "China_Telecom";
	public static final String CHINA_MOBILE = "China_Mobile";
	public static final String CHINA_UNICOM = "China_Unicom";
	//更新请求的时间间隔（一小时提示一次升级）
	public static final String UPGRADE_INTERVAL = "upgrade_interval";
	//极光推送标签
	public static final String JPUSH_ALIAS = "jpush_alias";
	public static final String JPUSH_ALIAS_SUCCESS = "success";
	//页码
	public static final int PAGESIZE = 20;
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
    public static final String SKY_UPGRADE_ORDER = "8880030A00B1";//空中升级指令
    public static final String RESTORATION = "8880020100";//复位命令

    public static final String HANG_UP_PUSH = "888003040000";//挂电推送到设备
    public static final String COMING_TEL_PUSH = "888003040001";//来电推送到设备
    public static final String MESSAGE_PUSH = "888003040002";//消息推送到设备
    public static final String WEIXIN_PUSH = "888003040003";//微信推送到设备
    public static final String QQ_PUSH = "888003040004";//QQ推送到设备

	public static final String APP_CONNECT = "8880021400";//APP专属命令
	public static final String BIND_DEVICE = "8880020700";//绑定命令
	public static final String BIND_SUCCESS = "888003080001";//绑定成功命令
	public static final String BIND_FAIL = "888003080000";//绑定失败命令
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

    //	public static final String DOWNLOAD_PATH = "/sdcard/aixiaoqi";
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath() + "/aixiaoqi/";
    public static String UPLOAD_PATH = "/aixiaoqi/upload.zip";
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

    public static final String SYSTEM_BASICE_INFO = "0100";//系统基本信息

    public static final String RECEIVE_ELECTRICITY = "0200";//接收电量
    public static final String RECHARGE_STATE = "0300";//充电状态
    public static final String AGREE_BIND = "0400";//同意绑定
    public static final String UPDATE_TIME = "0500";//更新时间
    public static final String REQUEST_SYSTEM_BASICE_INFO = "0600";//系统基本信息请求
    public static final String RETURN_POWER = "0700";//回应上电
    public static final String BIND_STATUE = "0800";//是否绑定成功
    public static final String READ_SIM_DATA = "0900";//读卡返回数据
    public static final String RECEIVE_CARD_MSG = "0a00";//爱小器卡
    public static final String REQUEST_POWER = "0b00";//请求电量
    public static final String IS_INSERT_CARD = "0c00";//检测是否插卡
    public static final String THE_HAND_FUNCTION = "0d00";//是否使用抬手功能
    public static final String HISTORY_STEP = "0e00";//历史步数
    public static final String SETTING_ALARM = "0f00";//设置闹钟
    public static final String SIM_UP_POWER = "1000";//对卡上电
    public static final String SIM_DOWN_POWER = "1100";//对卡断电
    public static final String READED_SIM_DATA = "1200";//读卡数据
    public static final String WRITE_SIM_DATA = "1300";//写卡数据
    public static final String WRITE_SIM_STEP_ONE = "A0A40000023F00";//写卡第一步
    public static final String WRITE_SIM_STEP_TWO = "A0A40000022F02";//写卡第一步
    public static final String WRITE_SIM_STEP_THREE = "A0B000000A";//写卡第一步

    //网络访问返回状态码
    /**
     * 一般通用接口返回值：
     * <p>
     * 失败 = 0,
     * 成功 = 1,
     * 找不到该用户 = 9900,
     * 手机号码格式不正确 = 9901,
     * 密码长度必须在6到20位之间 = 9902,
     * 验证码无效 = 9903,
     * 验证码错误 = 9904,
     * 此验证码已经过期_请重新发送验证码 = 9905,
     * 系统繁忙_请重试 = 9906,
     * 参数错误 = 9940,
     * 必填参数为空 = 9944,
     * 用户不能为空 = 9945,
     * 内部错误 = 9950,
     */
    public static final int HTTP_SUCCESS = 1;
    public static final int HTTP_FAIL = 0;
    public static final int CANT_FIND_USER = 9900;
    public static final int PHONE_NUMBER_ERROR = 9901;
    public static final int PASSWORD_LENGTH_ERROR = 9902;
    public static final int VERTIFICATION_CODE_VALID = 9903;
    public static final int VERTIFICATION_CODE_ERROR = 9904;
    public static final int VERTIFICATION_CODE_OVERDUE = 9905;
    public static final int SYSTEM_BUSY = 9906;
    public static final int PARAMETER_ERROR = 9940;
    public static final int PARAMETER_NULL = 9944;
    public static final int USER_NULL = 9945;
    public static final int INSIDE_ERROR = 9945;

    /**
     * 注册
     * 您输入的手机号码已注册 = 1001,
     * 注册失败_请重试 = 1002,
     */
    public static final int ALLREADY_REGIST = 1001;
    public static final int REGIST_FAIL = 1002;

    /**
     * 登录
     * 帐号不存在_请先注册 = 1003,
     * 您的帐号已被锁定 = 1004,
     * 密码不正确 = 1005
     */
    public static final int ACCOUNT_UNEXIST = 1003;
    public static final int ACCOUNT_LOCK = 1004;
    public static final int PSW_ERROR = 1005;

    /**
     * 退出登录
     * 退出失败 = 1006
     */

    public static final int EXIT_ERROR = 1006;

    /**
     * 忘记密码
     * 手机号未注册 = 1008
     */

    public static final int PHONE_UNREGIST = 1008;

    /**
     * 发送验证短信
     * 验证类型错误 = 1009,
     * 您输入的手机号码已注册2 = 1010,
     * 您输入的手机号码未注册 = 1011,
     * 短信服务器异常_请联系客服人员 = 1012,
     * 您发送的太频繁了 = 1013,
     * 阿里云短信调用失败 = 1014,
     * 一分钟内不能再次发送_RemainingSeconds秒以后可以再次发送 = 1015,
     */

    public static final int VERTIFICATION_ERROR = 1009;


    /**
     * 手机厂商硬件名
     */
    public static final String LEMOBILE = "lemobile";//乐视
    public static final String LENOVO = "lenovo";//联想
    public static final String MEIZU = "meizu";//魅族
    public static final String SAMSUNG = "samsung";//三星
    public static final String ONEPLUS = "oneplus";//一加

    //------------------------------------------
    public static final String HUAWEI = "huawei";//华为
    public static final String GIONEE = "gionee";//金立
    public static final String VIVO = "vivo";//vivo
    public static final String OPPO = "oppo";//OPPO
    public static final String XIAOMI = "xiaomi";

    /**
     * 手机提示
     */
    public static final String APP_PERTECT = "开启应用保护";
    public static final String PHONE_NO_OMIT = "不漏接任何来电";
    public static final String AUTO_RUN = "开启 “自启动管理”";
    public static final String PERTECT_AIXIAOQI_RUN_NORMAL = "保障爱小器正常运行";
    public static final String KEEP_WLAN_CONNECT = "休眠状态下保持WLAN连接";
    public static final String KEEP_RUN_IN_STANDBY = "在待机时保持运行";

    public static final String LOCK_SCREEN_CLEAR_WHITE_LIST = "开启 ”锁屏清理白名单“";
    public static final String BACKGROUND_HIGH_POWER = "后台高耗电时允许运行";
    public static final String OPEN_SUSPEND_WINDOW = "开启“系统悬浮窗”";
    public static final String SPIRIT_AWAY_MODE = "关闭“神隐模式”";

    public static final String SHUT_DOWN_BACKGROUND = "关闭“后台冻结”";
    public static final String REGISTER_SIM_NOT_PRE_DATA = "not_pre_data";
    public static final String REGISTER_SIM_PRE_DATA = "pre_data";

    /**
     * 套餐详情界面的一些参数
     */
    public static final String LOCALBROADCAST_INTENT_DATA = "net_data";
    public static final String DETAIL_SIGN = "detail";
    public static final String FEATURES_SIGN = "features";
    public static final String PAYTERMS_SIGN = "payterms";
    public static final String SHAREDPREFERENCES_SIGN = "detail_data";

	public static final String HIDDEN="hidden";
	public static final String SHOW="show";
}
