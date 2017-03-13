package de.blinkt.openvpn.activities.permission;

import android.content.ComponentName;
import android.content.Intent;

import de.blinkt.openvpn.activities.Base.BaseActivity;

/**
 * Created by Administrator on 2017/3/11 0011.
 */

public class MeiZuPermissionActivity extends PermissionActivity {

    @Override
    protected Intent backgroundStart() {
        Intent meizuGodIntent = new Intent();
          meizuGodIntent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.powerui.AppPowerManagerActivity"));

        return meizuGodIntent;
    }

    @Override
    protected Intent trustApp() {
        Intent meizuIntent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            meizuIntent.addCategory(Intent.CATEGORY_DEFAULT);
        return meizuIntent;
    }
}
