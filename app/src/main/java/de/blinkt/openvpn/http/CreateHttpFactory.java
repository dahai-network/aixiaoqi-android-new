package de.blinkt.openvpn.http;

import de.blinkt.openvpn.constant.HttpConfigUrl;



/**
 * Created by Administrator on 2017/3/10 0010.
 */

public class CreateHttpFactory {
    public static void instanceHttp(InterfaceCallback interfaceCallback,int cmdType){
        switch (cmdType){
            case HttpConfigUrl.COMTYPE_GET_HOT://获取热门套餐
                startHttp(new GetHotHttp(interfaceCallback, cmdType, 12));
                break ;
            case HttpConfigUrl.COMTYPE_GET_ORDER://获取订单
                startHttp(new BoughtPacketHttp(interfaceCallback, cmdType, 1, 3));
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
            case HttpConfigUrl.COMTYPE_CHECK_IS_HAVE_PACKET://获取端口号和IP地址
                startHttp(new IsHavePacketHttp(interfaceCallback, cmdType,"3"));
                break;
        }
    }

    public  static  void startHttp(BaseHttp baseHttp){
        new Thread(baseHttp).start();
    }

}
