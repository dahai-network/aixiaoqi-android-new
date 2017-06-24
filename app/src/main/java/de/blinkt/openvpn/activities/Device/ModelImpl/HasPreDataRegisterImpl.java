package de.blinkt.openvpn.activities.Device.ModelImpl;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aixiaoqi.socket.EventBusUtil;
import com.aixiaoqi.socket.RadixAsciiChange;
import com.aixiaoqi.socket.ReceiveSocketService;
import com.aixiaoqi.socket.SdkAndBluetoothDataInchange;
import com.aixiaoqi.socket.SendYiZhengService;
import com.aixiaoqi.socket.SocketConnection;
import com.aixiaoqi.socket.SocketConstant;
import com.aixiaoqi.socket.TestProvider;

import de.blinkt.openvpn.activities.Device.Model.HasPreDataRegisterModel;
import de.blinkt.openvpn.activities.Device.ui.ProMainActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.PreReadEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

public class HasPreDataRegisterImpl  implements HasPreDataRegisterModel{
    private int bindtime = 0;
    private Context context;
    SocketConnection socketTcpConnection;
    public  static SendYiZhengService  sendYiZhengService;
    public HasPreDataRegisterImpl(Context context){
        this.context=context;
        if (!ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
            socketTcpConnection = new SocketConnection();
        }
        initSendYiZhengService();
    }
    @Override
    public void initPreData(PreReadEntity preReadEntity) {
        SdkAndBluetoothDataInchange.isHasPreData = true;
        SdkAndBluetoothDataInchange.PERCENT = 0;
        SocketConstant.REGISTER_STATUE_CODE = 2;
        SocketConstant.CONNENCT_VALUE[3] = RadixAsciiChange.convertStringToHex(SharedUtils.getInstance().readString(Constant.TOKEN));
        SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[3]] = preReadEntity.getPreReadData();
        SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[2]] = preReadEntity.getDataLength();
        SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[1]] = preReadEntity.getImsi();
        SocketConstant.CONNENCT_VALUE[SocketConstant.CONNECT_VARIABLE_POSITION[0]] = preReadEntity.getIccid();
    }

    @Override
    public void registerSimPreData() {
        if (SocketConnection.mReceiveSocketService != null && SocketConnection.mReceiveSocketService.CONNECT_STATUE == SocketConnection.mReceiveSocketService.CONNECT_SUCCEED) {//TCP已经创建成功了
            sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
        } else if (SocketConnection.mReceiveSocketService != null && SocketConnection.mReceiveSocketService.CONNECT_STATUE == SocketConnection.mReceiveSocketService.CONNECT_FAIL) {//如果是连接失败且没有销毁，则断开在重新创建连接
            SocketConnection.mReceiveSocketService.disconnect();
            startTcp();
        } else {//创建连接
            startTcp();
        }
    }

    private void startTcp() {
        startSocketService();//开始绑定
        startTcpSocket();
        SocketConnection.mReceiveSocketService.setListener(new ReceiveSocketService.CreateSocketLisener() {
            @Override
            public void create() {
                TestProvider.isCreate = true;
                CommonTools.delayTime(500);
                sendYiZhengService.sendGoip(SocketConstant.CONNECTION);
            }

        });
    }

    public void startSocketService() {
        if (!ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
            Intent receiveSdkIntent = new Intent(context, ReceiveSocketService.class);
            context.bindService(receiveSdkIntent, socketTcpConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void startTcpSocket() {
        initSendYiZhengService();//在解除绑定的时候把这些数据清理了，因此如果要重新简历连接就需要重新初始化
        if (sendYiZhengService != null && SocketConnection.mReceiveSocketService != null) {
            sendYiZhengService.initSocket(SocketConnection.mReceiveSocketService);
            return;
        }
        bindTcpSucceed();
    }

    private void initSendYiZhengService() {
        if(sendYiZhengService==null){
            sendYiZhengService=new SendYiZhengService();
        }
    }

    private void bindTcpSucceed() {
        if (SocketConnection.mReceiveSocketService == null) {
            CommonTools.delayTime(1000);
            if (bindtime > 15) {
                EventBusUtil.simRegisterStatue(SocketConstant.REGISTER_FAIL,SocketConstant.BIND_TCP_SERIVCE_FAIL);
                return;
            }
            bindtime++;
            startTcpSocket();
        }
        bindtime = 0;
    }

    public void unbindTcpService() {
        if (ICSOpenVPNApplication.getInstance().isServiceRunning(ReceiveSocketService.class.getName())) {
            if(context!=null&&socketTcpConnection!=null)
                context.unbindService(socketTcpConnection);
            if (SocketConnection.mReceiveSocketService != null) {
                SocketConnection.mReceiveSocketService.stopSelf();
                SocketConnection.mReceiveSocketService = null;
            }
        }
    }
}
