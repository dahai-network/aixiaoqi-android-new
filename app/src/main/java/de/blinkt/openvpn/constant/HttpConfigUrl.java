package de.blinkt.openvpn.constant;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class HttpConfigUrl {

	public static final String NEWBASICAPITEST = "http://apitest.unitoys.com/api/";
	public static final String NEWBASICAPI = "https://api.unitoys.com/api/";
	public static final String PRIVATE_KEY = "BAS123!@#FD1A56K";
	public static final String LOGIN = "Login/CheckLogin";
	public static final String SEND_SMS = "Confirmation/SendSMS";

	public static final String REGIST = "Register/Post";
	public static final String FORGET_PSW = "User/ForgotPassword";
	public static final String INDEX_BANNER = "config/getbannerlist";
	public static final String GET_HOT = "Country/GetHot";
	public static final String GET_ORDER = "Order/GetUserOrderList";//获取用户订单
	public static final String GET_BALANCE = "User/GetUserAmount";
	public static final String POST_UPLOAD_HEADER = "User/ModifyUserHead";
	public static final String POST_MODIFY_INFO = "User/UpdateUserInfoAndUserShape";
	//用户反馈
	public static final String USER_FEED_BACK = "Feedback/AddFeedback";
	//国家套餐
	public static final String COUNTRY_PACKET = "Package/GetByCountry";
	//余额明细
	public static final String PARTICULAR = "User/GetUserBill";
	//套餐超市
	public static final String PACKET_MARKET = "Country/Get";
	public static final String PACKET_DETAIL = "Package/GetByID";
	public static final String GET_SMS_LIST = "SMS/GetUserContactTelLastSMS";
	public static final String GET_SMS_DETAIL = "SMS/GetByTel";
	//根据ID查询用户订单
	public static final String GET_USER_PACKET_BY_ID = "Order/GetByID";
	public static final String SEND_SMS_MESSAGE = "SMS/Send";
	public static final String GET_MAX_PHONE_CALL_TIME = "User/GetMaximumPhoneCallTime";
	public static final String SECURITY_CONFIG = "Config/GetSecurityConfig";
	//创建订单
	public static final String CREATE_ORDER = "Order/Add";
	//取消订单
	public static final String CANCEL_ORDER = "Order/Cancel";
	//微信支付生成预支付ID
	public static final String WEIXIN_GETPAYID = "WxPay/GetPayId";
	//余额支付
	public static final String BALANCE_GETPAYID = "Order/PayOrderByUserAmount";
	//充值生成订单
	public static final String RECHARGE_ORDER = "Payment/Add";
	//判断token是否过期
	public static final String CHECKTOKEN = "Login/Get";
	//退出登录
	public static final String EXIT = "Login/Get";
	public static final String SPORT_GET_TIME_PERIOD_DATE = "Sport/GetTimePeriodByDate";
	//记录历史步数
	public static final String SPORT_REPORT_HISTORY_STEP = "Sport/AddHistorys";
	public static final String SPORT_GET_RECORD_DATE = "Sport/GetRecordDate";
	//绑定设备
	public static final String BIND_DEVICE = "DeviceBracelet/Bind";	public static final String GET_BIND_DEVICE = "DeviceBracelet/Get";
	//获取当前账号绑定的设备

	public static final String UN_BIND_DEVICE = "DeviceBracelet/UnBind";
	//运动总量

	public static final String ALARM_CLOCK_GET = "AlarmClock/Get";
	public static final String ALARM_CLOCK_DELETE = "AlarmClock/Delete";
	public static final String GET_SPORT_TOTAL = "Sport/GetSportTotal";
	public static final String ORDER_ACTIVATION = "Order/Activation";
	public static final String ORDER_DATA = "Order/QueryOrderData";
	public static final String ORDER_ACTIVATION_LOCAL_COMPLETED = "Order/ActivationLocalCompleted";
	public static final String SEND_RETRY_FOR_ERROR = "SMS/SendRetryForError";
	public static final String DEVICE_BRACELET_OTA = "DeviceBracelet/OTA";
	public static final String ISBIND_DEVICE = "DeviceBracelet/IsBind";
	public static final String ADD_ALARM = "AlarmClock/Add";
	public static final String UPDATE_ALARM = "AlarmClock/Update";
	public static final String UPLOAD_REMIND_CONFIG = "UsersConfig/UploadConfig";
	public static final String ALARM_CLOCK_COUNT="AlarmClock/GetByDisabledNum";
	public static final String UPDATE_ALARM_CLOCK_STATUE="AlarmClock/UpdateStatus";
	public static final String BIND_RECHARGE_CARD="PaymentCard/Recharge";
	//支付宝回调
	public static final String NOTIFYASYNC="AliPay/NotifyAsync";

	public static final String GET_BASIC_CONFIG="config/GetBasicConfig";
	public static final String ADD_NUMBER ="OrderByZC/Bind";
	public static final String ORDER_LIST ="OrderByZC/GetUserOrderByZCList";
	public static final String ORDER_DETAIL ="OrderByZC/GetByID";
	public static final String GET_LOCATION_LIST ="ZCSelectionNumber/GetLocationList";
	public static final String GET_SELECT_NUMBER ="ZCSelectionNumber/Get";
	public static final String ADD_SELECT_NUMBER_INFO ="OrderByZCSelectionNumber/Add";
	public static final String BIND_GIFT ="GiftCard/Bind";
	public static final String ACTIVATE_KINGCARD ="Order/ActivationKindCard";
	public static final String GET_SELECT_NUMBER_URL ="Config/getDWKUrl";
	public static final String CHECK_IS_HAVE_PACKET ="Order/CheckUsedExistByPageCategory";
	public static final String GET_SECURITY_CONFIG ="Config/GetSecurityConfig";
	public static final String GET_DEVICE_SIM_REG_STATUES ="DeviceBracelet/GetRegStatus";
	public static final String PACKET_GET ="Package/Get";
	public static final String BLACK_LIST_ADD ="BlackList/Add";
	public static final String BLACK_LIST_DELETE ="BlackList/Delete";
	public static final String BLACK_LIST_GET ="BlackList/Get";
	public static final String UPDATE_CONN_INFO ="DeviceBracelet/UpdateConnectInfo";
	public static final String SMS_DELETE_BY_TEL ="SMS/DeletesByTel";
	public static final String SMS_DELETE_BY_TELS ="SMS/DeletesByTels";
	public static final String SMS_DELETE_SMSs ="SMS/Deletes";
	public static final String SMS_DELETE ="SMS/Delete";
	public static final String GET_PRODUCTS ="config/GetProductList";
	public static final int COMTYPE_LOGIN = 0x0001;
	public static final int COMTYPE_SECURITY_CONFIG = 0x0002;
	public static final int COMTYPE_SEND_SMS = 0x0003;
	public static final int COMTYPE_REGIST = 0x0004;
	public static final int COMTYPE_FORGET_PSW = 0x0005;
	public static final int COMTYPE_INDEX_BANNER = 0x0006;
	public static final int COMTYPE_GET_HOT = 0x0007;
	public static final int COMTYPE_GET_ORDER = 0x0008;
	public static final int COMTYPE_GET_BALANCE = 0x0009;
	public static final int COMTYPE_GET_SMS_LIST = 0x0010;
	public static final int COMTYPE_USER_FEED_BACK = 0x0011;
	public static final int COMTYPE_COUNTRY_PACKET = 0x0012;
	public static final int COMTYPE_PACKET_DETAIL = 0x0013;
	public static final int COMTYPE_GET_USER_PACKET_BY_ID = 0x0014;
	public static final int COMTYPE_PACKET_MARKET = 0x0015;
	public static final int COMTYPE_CREATE_ORDER = 0x0016;
	public static final int COMTYPE_WEIXIN_GETPAYID = 0x0017;
	public static final int COMTYPE_RECHARGE_ORDER = 0x0018;
	public static final int COMTYPE_CANCEL_ORDER = 0x0019;
	public static final int COMTYPE_UPLOAD_HEADER = 0x0020;
	public static final int COMTYPE_GET_MAX_PHONE_CALL_TIME = 0x0021;
	public static final int COMTYPE_GET_SMS_DETAIL = 0x0022;
	public static final int COMTYPE_SEND_SMS_MESSAGE = 0x00123;

	public static final int COMTYPE_DOWNLOAD_SKY_UPDATE_PACKAGE = 0x0037;
	public static final int COMTYPE_DEVICE_BRACELET_OTA = 0x0036;
	public static final int COMTYPE_SEND_RETRY_FOR_ERROR = 0x0039;
	public static final int COMTYPE_GET_BASIC_CONFIG = 0x0040;
	public static final int COMTYPE_ADD_NUMBER = 0x0041;
	public static final int COMTYPE_ORDER_LIST = 0x0042;
	public static final int COMTYPE_ALARM_CLOCK_GET = 0x0043;
	public static final int COMTYPE_ALARM_CLOCK_DELETE = 0x0044;
	public static final int COMTYPE_ALARM_CLOCK_COUNT=0x0045;
	public static final int COMTYPE_UPDATE_ALARM_CLOCK_STATUE=0x0046;
	public static final int COMTYPE_ORDER_DETAIL=0x0047;
	public static final int COMTYPE_GET_LOCATION_LIST=0x0048;
	public static final int COMTYPE_GET_SELECT_NUMBER=0x0049;
	public static final int COMTYPE_ADD_SELECT_NUMBER_INFO=0x0050;
	public static final int  COMTYPE_GET_SELECT_NUMBER_URL =0x0051;
	public static final int  COMTYPE_BLACK_LIST_ADD =0x0055;
	public static final int  COMTYPE_BLACK_LIST_DELETE =0x0056;
	public static final int  COMTYPE_BLACK_LIST_GET =0x0057;
	public static final int  COMTYPE_GET_DEVICE_SIM_REG_STATUES =0x0052;
	public static final int  COMTYPE_SMS_DELETE_BY_TEL =0x0053;
	public static final int  COMTYPE_SMS_DELETE =0x0054;
	public static final int COMTYPE_POST_MODIFY_NICK = 0x1001;
	public static final int COMTYPE_POST_MODIFY_GENDER = 0x1002;
	public static final int COMTYPE_POST_MODIFY_AGE = 0x1003;
	public static final int COMTYPE_POST_MODIFY_HEIGHT = 0x1004;
	public static final int COMTYPE_POST_MODIFY_WEIGHT = 0x10015;
	public static final int COMTYPE_POST_MODIFY_SPORT_TARGET = 0x1016;
	public static final int COMTYPE_PARTICULAR = 0x1017;
	public static final int COMTYPE_CHECKTOKEN = 0x1018;
	public static final int COMTYPE_EXIT = 0x1019;
	public static final int COMTYPE_SPORT_GET_TIME_PERIOD_DATE = 0x1020;
	public static final int COMTYPE_SPORT_REPORT_REALTIME_STEP = 0x1021;
	public static final int COMTYPE_SPORT_REPORT_HISTORY_STEP = 0x1022;
	public static final int COMTYPE_SPORT_GET_RECORD_DATE = 0x1030;
	public static final int COMTYPE_BIND_DEVICE = 0x1031;
	public static final int COMTYPE_GET_BIND_DEVICE = 0x1032;
	public static final int COMTYPE_UN_BIND_DEVICE = 0x1033;
	public static final int COMTYPE_GET_SPORT_TOTAL = 0x1034;
	public static final int COMTYPE_ORDER_ACTIVATION = 0x1035;
	public static final int COMTYPE_ORDER_DATA = 0x1036;
	public static final int COMTYPE_ORDER_ACTIVATION_LOCAL_COMPLETED = 0x1037;
	public static final int COMTYPE_ISBIND_DEVICE = 0x1038;
	public static final int COMTYPE_ADD_ALARM = 0x1039;
	public static final int COMTYPE_UPDATE_ALARM = 0x1040;
	public static final int COMTYPE_UPLOAD_REMIND_CONFIG = 0x1041;
	public static final int COMTYPE_BALANCE_GETPAYID = 0x1042;
	public static final int COMTYPE_BIND_RECHARGE_CARD = 0x1043;
	public static final int COMTYPE_BIND_GIFT = 0x1044;
	public static final int COMTYPE_ACTIVATE_KINGCARD = 0x1045;
	public static final int COMTYPE_PACKET_GET = 0x1046;
	public static final int COMTYPE_CHECK_IS_HAVE_PACKET = 0x1047;
	public static final int COMTYPE_GET_SECURITY_CONFIG = 0x1048;
	public static final int COMTYPE_UPDATE_CONN_INFO = 0x1049;
	public static final int COMTYPE_SMS_DELETE_SMSS = 0x1050;
	public static final int COMTYPE_SMS_DELETE_BY_TELS =0x1051;
	public static final int COMTYPE_GET_PRODUCTS =0x1052;

}
