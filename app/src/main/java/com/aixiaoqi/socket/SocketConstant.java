package com.aixiaoqi.socket;

/**
 * Created by Administrator on 2016/12/17 0017.
 */
public class SocketConstant {

	//创建连接的标签TAG
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
			, Integer.toHexString(199)
	};//预读数据内容
	//创建连接的标签内容会变动的下标
	public static int [] CONNECT_VARIABLE_POSITION={11,12,15,16};
	//创建连接的value
	public static String[] CONNENCT_VALUE = {"01",//01，请求GoIp模块的值
			"b4"//101,链接存活时间
			, "01"//107,是否请求预读数据
			, ""//120,服务寻址sessionID
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

	};




	public static String SESSION_ID = "00000000";//会话ID
	public static String SDK_TAG = "c7";
	public static String SDK_VALUE = "";

	public static String UPDATE_CONNECTION = "108a0500";
	public static String CONNECTION = "108a0400";//连接
	public static String PRE_DATA = "108a9000";//读取数据
	public static String RECEIVE_CONNECTION = "108a8400";//连接回来
	public static String RECEIVE_PRE_DATA = "108a1000";//
	public static final String SESSION_ID_TEMP = "00000000";//
	public static String hostIP = "120.25.161.113";
	public static int port = 20016;
	//SDK读取状态
	public static final String EN_APPEVT_NONE = "00";
	public static final String EN_APPEVT_SETSIMTYPE = "01";
	public static final String EN_APPEVT_CMD_SETRST = "02";
	public static final String EN_APPEVT_CMD_SIMCLR = "03";
	public static final String EN_APPEVT_RSTRSP = "04";
	public static final String EN_APPEVT_PRDATA = "05";
	public static final String EN_APPEVT_SIMDATA = "06";
	public static final String EN_APPEVT_SIMINFO = "07";





	public static int REGISTER_STATUE_CODE = 0;//0，表示还没有开始注册。1，表示获取预读取数据没有完成（已经写卡但未完成）。2，表示注册中，3表示注册成功
	public static final String TRAN_DATA_TO_SDK = "88888888";
	public static int SIM_TYPE=0;
	public static String REGISTER_REMOTE_ADDRESS;
	public static String REGISTER_ROMOTE_PORT;
	public static final String HEARTBEAT_PACKET_TIMER = "heartbeat_packet_timer";

	public static final int REGISTER_SUCCESS = 0;//注册成功

	public static final int REGISTER_FAIL = 1;//注册失败
	public static final int REGISTER_FAIL_IMSI_IS_NULL = 101;//imsi是空的，注册失败
	public static final int REGISTER_FAIL_IMSI_IS_ERROR = 102;//imsi是错误的，注册失败
	public static final int REGISTER_FAIL_INITIATIVE = 103;//如果主动解除绑定那么不显示注册失败
	public static final int TOKEN_IS_NULL = 104;//没有token，注册失败
	public static final int SERVER_IS_ERROR = 105;//服务端出错，注册失败
	public static final int NOT_CAN_RECEVIE_BLUETOOTH_DATA = 106;//收不到蓝牙发过来的数据，注册失败
	public static final int SDK_SEND_IS_NULL = 107;//sdk发过来的数据是空的
	public static final int NO_NET=108;//没有网络
	public static final int BIND_TCP_SERIVCE_FAIL=109;//绑定失败
	public static final int NO_NET_ERROR=110;//网络异常

	public static final int REGISTERING= 2;//注册中
	public static final int START_TCP_FAIL = 201;//开启TCP失败，显示注册中
	public static final int TCP_DISCONNECT = 202;//TCP主动断开，不显示。否则注册中
	public static final int REG_STATUE_CHANGE = 203;//注册状态改变，注册中
	public static final int RESTART_TCP = 204;//重连TCP，注册中
	public static final int REGISTER_CHANGING=205;//重新注册，注册中
	public static final int UPDATE_PERCENT=206;//更改进度条
	public static final int VAILD_CARD=207;//有效卡，注册中
	/**
	 * 未注册
	 */
	public static final int UNREGISTER=3;//未注册
	public static final int UN_INSERT_CARD=301;//未插卡，未注册
	public static final int AIXIAOQI_CARD=302;//爱小器卡，未注册
	public static final int BLUETOOTH_CLOSE=303;//蓝牙未打开，未注册
	/**
	 * 设备连接中
	 */
	public static final int CONNECTING_DEVICE=304;//设备连接中
	public static final int UNBIND_DEVICE=305;//未绑定设备
	public static final int DISCOONECT_DEVICE=306;//断开连接
	public static final int CONNECTING_SUCCESS=307;//连接成功
}
