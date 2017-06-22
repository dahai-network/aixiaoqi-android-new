package de.blinkt.openvpn.activities.CommomModel.JPush.ModelImpl;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.blinkt.openvpn.activities.CommomModel.JPush.Model.JPushSetAliaModel;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by Administrator on 2017/5/26 0026.
 */

public class JPushSetAliaModelImpl implements JPushSetAliaModel {
    private static final int MSG_SET_ALIAS = 1001;
    private JpushHandler handler ;
    @Override
    public void setJPushAlia(String jPushAlia) {
        if(handler==null){
            handler= new JpushHandler(mAliasCallback);
        }
        handler.sendMessage(handler.obtainMessage(MSG_SET_ALIAS, jPushAlia));

    }

    private  class JpushHandler extends Handler {

        private final WeakReference<TagAliasCallback> callback;

        public JpushHandler(TagAliasCallback mAliasCallback) {
            this.callback = new WeakReference<>(mAliasCallback);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    JPushInterface.setAliasAndTags(ICSOpenVPNApplication.getContext(), (String) msg.obj, null, callback.get());
                    break;
                default:
            }

        }

    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {

            SharedUtils sharedUtils = SharedUtils.getInstance();

            switch (code) {
                case 0:
                    if(TextUtils.isEmpty(alias)){
                        sharedUtils.delete(Constant.JPUSH_ALIAS);
                    }else{
                        sharedUtils.writeString(Constant.JPUSH_ALIAS,
                                Constant.JPUSH_ALIAS_SUCCESS);
                    }
                    break;

                case 6002:
                    if (NetworkUtils.isNetworkAvailable(ICSOpenVPNApplication.getContext()) && !TextUtils.isEmpty(alias)) {
                        handler.sendMessageDelayed(handler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    }
                    break;
                default:
            }
        }
    };

    @Override
    public void destoryHandler() {
        if (handler != null && handler.getLooper() == Looper.getMainLooper()) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
