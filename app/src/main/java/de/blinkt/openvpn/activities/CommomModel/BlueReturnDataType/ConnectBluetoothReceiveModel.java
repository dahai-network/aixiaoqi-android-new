package de.blinkt.openvpn.activities.CommomModel.BlueReturnDataType;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.aixiaoqi.socket.EventBusUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.constant.BluetoothConstant;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.EncryptionUtil;
import de.blinkt.openvpn.util.SharedUtils;

import static de.blinkt.openvpn.bluetooth.util.SendCommandToBluetooth.sendMessageToBlueTooth;
import static de.blinkt.openvpn.constant.Constant.BIND_DEVICE;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class ConnectBluetoothReceiveModel {
    private int IS_NOT_UNI = 3;
    Context context;
    public ConnectBluetoothReceiveModel(Context context){
        this.context=context;
    }

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            if (msg.what == IS_NOT_UNI) {
                CommonTools.showShortToast(context, context.getString(R.string.bind_error));
            }

        }
    };
    public  void appConnectReceive(ArrayList<String> messages) {

        Log.i("Encryption", "返回加密数据----：" + messages.get(0).toString());
        String random8NumberString = ICSOpenVPNApplication.random8NumberString;
        Log.i("Encryption", "判断是否加密一致：" + EncryptionUtil.isPassEncrypt(messages.get(0).toString().substring(10), random8NumberString));
        if (!EncryptionUtil.isPassEncrypt(messages.get(0).toString().substring(10), random8NumberString)) {
            if(ICSOpenVPNApplication.uartService!=null)
            ICSOpenVPNApplication.uartService.disconnect();
            handler.sendEmptyMessage(IS_NOT_UNI);
        } else {
            Log.i("Encryption", "IMEI是否为空: " + (TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))));
            if (!CommonTools.isFastDoubleClick(20) && TextUtils.isEmpty(SharedUtils.getInstance().readString(Constant.IMEI))) {
                    EventBusUtil.bingDeviceStep(BluetoothConstant.BLUE_BIND);
                //Log.d("Encryption", "run: 发送绑定命令");-
                Logger.d("发送绑定命令");
                sendMessageToBlueTooth(BIND_DEVICE);//绑定命令
            }
        }
    }
}
