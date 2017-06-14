package de.blinkt.openvpn.activities.UserInfo.Presenter;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.UserInfo.Model.VertifyPhoneNumModel;
import de.blinkt.openvpn.activities.UserInfo.ModelImpl.VertifyPhoneNumImpl;
import de.blinkt.openvpn.activities.UserInfo.View.VertifyPhoneNumView;
import de.blinkt.openvpn.activities.UserInfo.ui.VertifyPhoneNumActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.http.CheckConfirmedHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.ConfirmedHttp;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.SharedUtils;

/**
 * Created by kim
 * on 2017/6/8.
 */
public class VertifyPhoneNumPresenter extends BaseNetActivity {

    private VertifyPhoneNumModel vertifyPhoneNumModel;
    private VertifyPhoneNumView vertifyPhoneNumView;
    private VertifyPhoneNumActivity instance;
    private boolean isVertifying = false;

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            checkIsConFirmed(SharedUtils.getInstance().readString(Constant.ICCID));
        }
    };

    public VertifyPhoneNumPresenter(VertifyPhoneNumView vertifyPhoneNumView) {
        this.vertifyPhoneNumView = vertifyPhoneNumView;
        vertifyPhoneNumModel = new VertifyPhoneNumImpl();
        instance = ICSOpenVPNApplication.vertifyPhoneNumActivity;

    }
    Thread overtimeThread;
    //进行验证手机号码
    public void confirmedPNumber(String phoneNumber) {

        vertifyPhoneNumModel.confirmedICCID(phoneNumber, SharedUtils.getInstance().readString(Constant.ICCID), this);
        //服务器验证操作

        instance.showProgress(R.string.vertifying);
        isVertifying = true;
        overtimeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                CommonTools.delayTime(60000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isVertifying) {
                            vertifyPhoneNumView.showToast(instance.getString(R.string.vertify_overtime));
                            isVertifying = false;
                            instance.dismissProgress();
                        }
                    }
                });
            }
        });
        overtimeThread.start();
        checkIsConFirmed(SharedUtils.getInstance().readString(Constant.ICCID));

    }

    private void checkIsConFirmed(String ICCID) {
        createHttpRequest(HttpConfigUrl.COMTYPE_CHECK_CONFIRMED, ICCID);
    }
    String confirmedPhoneNum;
    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_CHECK_CONFIRMED) {
            CheckConfirmedHttp http = (CheckConfirmedHttp) object;
            if (http.getStatus() == 1) {
                if (http.getEntity().isIsConfirmed()) {
                    isVertifying = false;
                    instance.dismissProgress();
                    confirmedPhoneNum = http.getEntity().getTel();
                    vertifyPhoneNumView.showToast(instance.getString(R.string.vertify_success));
                    instance.finish();
                } else {
                    if (!isVertifying) return;
                    new Thread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CommonTools.delayTime(3000);
                            handler.sendEmptyMessage(0);
                        }
                    })).start();
                }
            } else {
                vertifyPhoneNumView.showToast(http.getMsg());
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_CONFIRMED) {
            ConfirmedHttp http = (ConfirmedHttp) object;
            if (http.getStatus() == 1) {
                Log.i(TAG, "验证成功！！！！");
            }
        }
    }

    public void releaseResource() {
        if (overtimeThread != null) {
            overtimeThread.interrupt();
        }

    }
}
