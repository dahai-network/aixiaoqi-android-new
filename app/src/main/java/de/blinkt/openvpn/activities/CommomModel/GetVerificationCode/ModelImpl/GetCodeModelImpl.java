package de.blinkt.openvpn.activities.CommomModel.GetVerificationCode.ModelImpl;

import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.CommomModel.GetVerificationCode.Model.GetCodeModel;
import de.blinkt.openvpn.activities.NetModelBaseImpl;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;

/**
 * Created by Administrator on 2017/5/27 0027.
 */

public class GetCodeModelImpl extends NetModelBaseImpl implements GetCodeModel {

    public GetCodeModelImpl(View view, OnLoadFinishListener onLoadFinishListener){
        super(onLoadFinishListener);
        this.view=(TextView) view;
    }
    TextView view;
    @Override
    public void getCode(String phone,String paramsType) {
        createHttpRequest(HttpConfigUrl.COMTYPE_SEND_SMS, phone, paramsType);
    }

    private CountDownTimer timer = new CountDownTimer(111000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            view.setText(millisUntilFinished / 1000 + "秒后可重发");
        }

        @Override
        public void onFinish() {
            view.setEnabled(true);
            view.setTextColor(ContextCompat.getColor(ICSOpenVPNApplication.getContext(), R.color.black));
            view.setText("发送验证码");
        }
    };

    public void startTimer(){
        timer.start();
    }
    public  void finishTimer(){
        timer.onFinish();
    }
    public  void endTime(){
        timer.cancel();
    }
}
