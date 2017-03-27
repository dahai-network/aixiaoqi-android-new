package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;



/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class CreateHttpFactory {
    public static void instanceHttp(InterfaceCallback interfaceCallback,int cmdType,String ...params){
        switch (cmdType){
            case HttpConfigUrl.COMTYPE_GET_HOT://获取热门套餐
                startHttp(new GetHotHttp(interfaceCallback, cmdType, params));
                break ;
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
                startHttp(new IsHavePacketHttp(interfaceCallback, cmdType,params));
                break;
            case HttpConfigUrl.COMTYPE_SMS_DELETE_BY_TEL://删除某个人的短信
                startHttp(new SmsDeleteByTelHttp(interfaceCallback, cmdType,params));
                break;
            case HttpConfigUrl.COMTYPE_SMS_DELETE://删除短信
                startHttp(new SmsDeleteHttp(interfaceCallback, cmdType,params));
                break;
            case HttpConfigUrl.COMTYPE_ACTIVATE_KINGCARD://激活大王卡
                startHttp(new ActivateKingCardHttp(interfaceCallback, cmdType,params));
                break;
            case HttpConfigUrl.COMTYPE_ORDER_ACTIVATION_LOCAL_COMPLETED://激活完成
                startHttp(new ActivationLocalCompletedHttp(interfaceCallback, cmdType,params));
                break;
            case HttpConfigUrl.COMTYPE_ADD_ALARM://添加闹钟
                startHttp(new AddAlarmHttp(interfaceCallback, cmdType,params));
                break;
            case HttpConfigUrl.COMTYPE_UPDATE_ALARM://修改
                startHttp(new UpdateAlarmHttp(interfaceCallback, cmdType,params));
                break;
            case HttpConfigUrl.COMTYPE_ADD_NUMBER://添加数
                startHttp(new AddNumberHttp(interfaceCallback, cmdType,params));
                break;
            case HttpConfigUrl.COMTYPE_ALARM_CLOCK_COUNT://添加的闹钟总个数
                startHttp(new AlarmClockCountHttp(interfaceCallback, cmdType));
                break;
            case HttpConfigUrl.COMTYPE_BALANCE_GETPAYID://余额支付
                startHttp(new BalanceGetPayIdHttp(interfaceCallback, cmdType,params));
                break;
            case HttpConfigUrl.COMTYPE_GET_BALANCE://余额
                startHttp(new BalanceHttp(interfaceCallback, cmdType));
                break;
            case HttpConfigUrl.COMTYPE_BIND_DEVICE://绑定设备
                startHttp(new BindDeviceHttp(interfaceCallback, cmdType,params));
                break;
            case  HttpConfigUrl.COMTYPE_BIND_GIFT://绑定礼包
                startHttp(new BindGiftHttp(interfaceCallback, cmdType,params));
                break;
            case  HttpConfigUrl.COMTYPE_BIND_RECHARGE_CARD://绑定充值
                startHttp(new BindRechargeHttp(interfaceCallback, cmdType,params));
                break;
            case  HttpConfigUrl.COMTYPE_CANCEL_ORDER://取消订单
                startHttp(new CancelOrderHttp(interfaceCallback, cmdType,params));
                break;
            case  HttpConfigUrl.COMTYPE_CHECKTOKEN://检查token
                startHttp(new CheckTokenHttp(interfaceCallback, cmdType));
                break;
        }
    }

    private  static  void startHttp(BaseHttp baseHttp){
        new Thread(baseHttp).start();
    }

}
