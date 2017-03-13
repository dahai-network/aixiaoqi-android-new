package de.blinkt.openvpn.activities.permission;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;

/**
 * Created by Administrator on 2017/3/11 0011.
 */

public class PermissionActivity extends BaseActivity {
    @BindView(R.id.auto_start)
    TextView autoStart;
    @BindView(R.id.background_start)
    TextView backgroundStart;
    @BindView(R.id.trust_app)
    TextView trustApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_huawei);
        ButterKnife.bind(this);
        initTitle();
        controlView();
    }
    private  void initTitle(){
        hasLeftViewTitle(R.string.permission_set,0);
    }
    protected  void controlView(){

    }
    protected Intent wifiSet(){
        //                Intent intent =  new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
//                startActivity(intent);ACTION_WIFI_SETTINGS  ：
        return new Intent(Settings.ACTION_WIFI_SETTINGS);
    }

    protected Intent backgroundStart(){
        return null;
    }

    protected Intent trustApp(){
        return null;
    }
    @OnClick({R.id.auto_start,
            R.id.background_start,
            R.id.trust_app})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.auto_start:
                intent=  wifiSet();

                break;
            case R.id.background_start:
                intent= backgroundStart();

                break;
            case R.id.trust_app:
                intent=trustApp();
                break;
            default:
                intent =  new Intent();
                break;

        }
        startActivity(intent);
    }
}
