package de.blinkt.openvpn.http;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.blinkt.openvpn.constant.HttpConfigUrl;


/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class CreateHttpFactory {
	public static void instanceHttp(InterfaceCallback interfaceCallback, int cmdType, String... params) {
		switch (cmdType) {
			case HttpConfigUrl.COMTYPE_GET_HOT://获取热门套餐
				startHttp(new GetHotHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_GET_ORDER://获取订单
				startHttp(new BoughtPacketHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_GET_SPORT_TOTAL://获取运动总数据
				startHttp(new GetSportTotalHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_INDEX_BANNER://获取banner图
				startHttp(new BannerHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_GET_SECURITY_CONFIG://获取端口号和IP地址
				startHttp(new GetHostAndPortHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_CHECK_IS_HAVE_PACKET://是否有套餐
				startHttp(new IsHavePacketHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_SMS_DELETE_BY_TEL://删除某个人的短信
				startHttp(new SmsDeleteByTelHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_SMS_DELETE_BY_TELS://删除多个人的短信
				startHttp(new SmsDeleteByTelsHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_SMS_DELETE_SMSS://删除多条短信
				startHttp(new SmsDeleteByManyHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_SMS_DELETE://删除短信
				startHttp(new SmsDeleteHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_ACTIVATE_KINGCARD://激活大王卡
				startHttp(new ActivateKingCardHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_ORDER_ACTIVATION_LOCAL_COMPLETED://激活完成
				startHttp(new ActivationLocalCompletedHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_ADD_ALARM://添加闹钟
				startHttp(new AddAlarmHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_UPDATE_ALARM://修改
				startHttp(new UpdateAlarmHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_ALARM_CLOCK_COUNT://添加的闹钟总个数
				startHttp(new AlarmClockCountHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_BALANCE_GETPAYID://余额支付
				startHttp(new BalanceGetPayIdHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_GET_BALANCE://余额
				startHttp(new BalanceHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_BIND_DEVICE://绑定设备
				startHttp(new BindDeviceHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_BIND_GIFT://绑定礼包
				startHttp(new BindGiftHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_BIND_RECHARGE_CARD://绑定充值
				startHttp(new BindRechargeHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_CANCEL_ORDER://取消订单
				startHttp(new CancelOrderHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_CHECKTOKEN://检查token
				startHttp(new CheckTokenHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_COUNTRY_PACKET://国家套餐
				startHttp(new CountryPacketHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_EXIT://退出登录
				startHttp(new ExitHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_ALARM_CLOCK_GET://获取闹钟
				startHttp(new FindAlarmClockHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_FORGET_PSW://忘记密码
				startHttp(new ForgetPswHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_GET_BASIC_CONFIG://获取基本配置
				startHttp(new GetBasicConfigHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_GET_BIND_DEVICE://绑定设备
				startHttp(new GetBindDeviceHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_GET_DEVICE_SIM_REG_STATUES://获取sim卡状态
				startHttp(new GetDeviceSimRegStatuesHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_GET_USER_PACKET_BY_ID://通过id查询订单
				Log.d("CreateHttpFactory", "instanceHttp: ");
				startHttp(new GetOrderByIdHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_PACKET_GET://获取包
				startHttp(new GetPakcetHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_GET_PRODUCTS://获取产品信息
				startHttp(new GetProductHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_ISBIND_DEVICE://获取选择号码
				startHttp(new IsBindHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_LOGIN://获取选择号码
				startHttp(new LoginHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_GET_MAX_PHONE_CALL_TIME://获取选择号码
				startHttp(new OnlyCallHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_ORDER_ACTIVATION://获取选择号码
				startHttp(new OrderActivationHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_ORDER_DATA://获取选择号码
				startHttp(new OrderDataHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_PACKET_DETAIL://获取选择号码
				startHttp(new PacketDtailHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_PACKET_MARKET://获取选择号码
				startHttp(new PacketMarketHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_PARTICULAR://获取选择号码
				startHttp(new ParticularHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_RECHARGE_ORDER://获取选择号码
				startHttp(new RechargeHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_REGIST://获取选择号码
				startHttp(new RegistHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_SECURITY_CONFIG://获取选择号码
				startHttp(new SecurityConfigHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_SEND_SMS://获取选择号码
				startHttp(new SendMsgHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_SEND_RETRY_FOR_ERROR://获取选择号码
				startHttp(new SendRetryForErrorHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_SEND_SMS_MESSAGE://获取选择号码
				startHttp(new SendSmsHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_DEVICE_BRACELET_OTA://获取选择号码
				startHttp(new SkyUpgradeHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_GET_SMS_DETAIL://获取选择号码
				startHttp(new SmsDetailHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_GET_SMS_LIST://获取选择号码
				startHttp(new SMSListHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_SPORT_GET_TIME_PERIOD_DATE://获取选择号码
				startHttp(new SportPeriodHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_SPORT_GET_RECORD_DATE://获取选择号码
				startHttp(new SportRecordDateHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_UN_BIND_DEVICE://获取选择号码
				startHttp(new UnBindDeviceHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_UPDATE_ALARM_CLOCK_STATUE://获取选择号码
				startHttp(new UpdateAlarmClockStatueHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_UPDATE_CONN_INFO://获取选择号码
				startHttp(new UpdateConnectInfoHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_UPLOAD_HEADER://获取选择号码
				startHttp(new UploadHeaderHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_UPLOAD_REMIND_CONFIG://获取选择号码
				startHttp(new UploadRemindConfigHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_USER_FEED_BACK://获取选择号码
				startHttp(new UserFeedBackHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_WEIXIN_GETPAYID://获取选择号码
				startHttp(new WeixinGetPayIdHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_BLACK_LIST_ADD://获取选择号码
				startHttp(new BlackListAddHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_BLACK_LIST_DELETE://获取选择号码
				startHttp(new BlackListDeleteHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_BLACK_LIST_GET://获取黑名单
				startHttp(new BlackListGetHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_GET_USER_ORDER_USAGE_REMAINING://获取选择号码
				startHttp(new OrderUsageRemainHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_GET_RELAXED://获取省心服务列表
				startHttp(new GetRelaxedHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_ADD_RECEIVE://领取免费通话时长
				startHttp(new AddReceiveHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_CHECK_CONFIRMED://检查是否已验证
				startHttp(new CheckConfirmedHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_CONFIRMED://检查是否已验证
				startHttp(new ConfirmedHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_GETCURRENT://确认是否绑定了ICCID
				startHttp(new GetCurrentHttp(interfaceCallback, cmdType));
				break;
			case HttpConfigUrl.COMTYPE_GET_ATTRS_BY_ID://获取
				startHttp(new GetAttrsByIdHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_CREATE_ORDER://创建订单
				startHttp(new OrderAddHttp(interfaceCallback, cmdType, params));
				break;
			case HttpConfigUrl.COMTYPE_DOWNLOAD_SKY_UPDATE_PACKAGE://创建订单
				startHttp(new DownloadSkyUpgradePackageHttp(interfaceCallback, cmdType,true,params));
				break;
		}
	}

	static  ExecutorService cachedThreadPool;
	private static void startHttp(BaseHttp baseHttp) {
		if(cachedThreadPool==null){
            cachedThreadPool = Executors.newCachedThreadPool();
		}
        cachedThreadPool.execute(baseHttp);
	}

}
