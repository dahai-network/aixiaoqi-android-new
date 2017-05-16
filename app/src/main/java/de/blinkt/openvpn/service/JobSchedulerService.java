package de.blinkt.openvpn.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.aixiaoqi.socket.SocketConstant;

import de.blinkt.openvpn.activities.ProMainActivity;

/**
 * Created by kim
 * on 07/7/16
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    private Handler mJobHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            Log.d("JobSchedulerService", "handleMessage: 发送心跳包");

            if (ProMainActivity.sendYiZhengService != null) {
                ProMainActivity.sendYiZhengService.sendGoip(SocketConstant.UPDATE_CONNECTION);
            } else {
                Log.e("JobSchedulerService", "AutoReceiver 异常！");
            }
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });

    @Override
    public boolean onStartJob(JobParameters params) {
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, params));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mJobHandler.removeMessages(1);
        return false;
    }

}
