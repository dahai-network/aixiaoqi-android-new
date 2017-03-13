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

public class HuaWeiPermissionActivity extends PermissionActivity {


    @Override
    protected Intent backgroundStart() {
        Intent     intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//               ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
        //   ComponentName comp = new ComponentName("com.huawei.systemmanager",
        //      "com.huawei.permissionmanager.ui.SingleAppActivity");//华为权限管理，跳转到本app的权限管理页面,这个需要华为接口权限，未解决
//            ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//悬浮窗管理页面
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
        intent.setComponent(comp);
        return intent;
//                Intent intent =  new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
//                startActivity(intent);
    }

    @Override
    protected Intent trustApp() {
return   new Intent(Settings.ACTION_WIFI_SETTINGS);
    }
}
