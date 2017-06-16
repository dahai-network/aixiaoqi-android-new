package de.blinkt.openvpn.activities.SetFlowCard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.LaunchActivity;

/**
 * Created by Administrator on 2017/4/19 0019.
 */

public class OutsideFourStepActivity extends BaseActivity {
    @BindView(R.id.outside_step_four_bt)
    Button outsideStepFourBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside_item04);
        ButterKnife.bind(this);
        hasAllViewTitle(R.string.outside_use_guide,R.string.close, R.string.last_step,false);
    }

    @Override
    protected void onClickRightView() {
        setResult(200);
        onBackPressed();
    }

    @OnClick(R.id.outside_step_four_bt)
    public void onClick() {
			Intent intent = new Intent(application.getApplicationContext(), LaunchActivity.class);


			PendingIntent restartIntent = PendingIntent.getActivity(
					application.getApplicationContext(), 0, intent,
					Intent.FLAG_ACTIVITY_NEW_TASK);
			//退出程序
			AlarmManager mgr = (AlarmManager)application.getSystemService(ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500,
					restartIntent); // 1秒钟后重启应用
        application.AppExit();

    }
}
