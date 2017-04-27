package de.blinkt.openvpn.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.AuthorityAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.AuthorityEntity;
import de.blinkt.openvpn.model.IntentEntity;
import de.blinkt.openvpn.util.IntentWrapper;

public class ImportantAuthorityActivity extends BaseActivity {

    private static final String TAG = "aixiaoqi__";
    @BindView(R.id.authorityRecyclerView)
    RecyclerView authorityRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_authority);
        ButterKnife.bind(this);
        initSet();
    }

    private void initSet() {
        hasLeftViewTitle(R.string.important_autohrity, 0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        authorityRecyclerView.setLayoutManager(layoutManager);
        AuthorityAdapter adapter = new AuthorityAdapter(this, getPhoneTypeEntity());
        authorityRecyclerView.setAdapter(adapter);
    }

    public ArrayList<AuthorityEntity> getPhoneTypeEntity() {
        ArrayList<AuthorityEntity> data = new ArrayList<>();
        setPhoneTypeEntity(data);
        if (data.size() == 0) {
            IntentWrapper.whiteListMatters(ProMainActivity.instance, "服务的持续运行");
            finish();
        }
        return data;
    }

    AuthorityEntity entity;
    Intent shadeIntent;

    public void setPhoneTypeEntity(ArrayList<AuthorityEntity> data) {
        int version = Build.VERSION.SDK_INT;
        entity = new AuthorityEntity();
        shadeIntent = new Intent(this, ShadeActivity.class);

        String phoneType = Build.MANUFACTURER.toLowerCase();
        Log.d(TAG, "phoneType: " + phoneType);
        switch (phoneType) {
            case Constant.LEMOBILE:
                if (version > 18) {

                    appPertectSet(entity);
                    Intent letvIntent = new Intent();
                    letvIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                    dataSave(letvIntent, data);

                    autoRunSet(entity);
                    Intent letvGodIntent = new Intent();
                    letvGodIntent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.BackgroundAppManageActivity"));
                    dataSave(letvGodIntent, data);

                    wifiSet(entity);
                    Intent netWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    dataSave(netWorkIntent, data);
                }
                break;
            //  case Constant.LENOVO
            case Constant.LENOVO:
                if (version > 18) {
                    wifiSet(entity);
                    Intent netWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    dataSave(netWorkIntent, data);
                }
                break;
            case Constant.MEIZU:
                if (version > 18) {

                    //保持后台运行
                    keepStandbySet(entity);
                    Intent meizuGodIntent = new Intent();
                    meizuGodIntent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.SecurityCenterActivity"));
                    dataSave(meizuGodIntent, data);


                    //自启动
                    autoRunSet(entity);
                    Intent meizuIntent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
                    meizuIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    meizuIntent.putExtra("packageName", ICSOpenVPNApplication.getInstance().getPackageName());
                    dataSave(meizuIntent, data);


                    wifiSet(entity);
                    Intent netWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    dataSave(netWorkIntent, data);
                }
                break;
            case Constant.SAMSUNG:

                if (version > 18) {
                    autoRunSet(entity);
                    Intent samsungLIntent = ICSOpenVPNApplication.getInstance().getPackageManager().getLaunchIntentForPackage("com.samsung.android.sm");
                    dataSave(samsungLIntent, data);

                    wifiSet(entity);
                    Intent netWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    dataSave(netWorkIntent, data);
                }
                break;
            case Constant.ONEPLUS:
                wifiSet(entity);
                Intent netWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                dataSave(netWorkIntent, data);
                break;

            //华为
            case Constant.HUAWEI:
                //lock screen clear white list
                if (version > 18) {
                    //锁屏清理
                    lockScreenSet(entity);
                    Intent huaweiGodIntent = new Intent();
                    huaweiGodIntent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                    dataSave(huaweiGodIntent, data);
                    //auto running
                    autoRunSet(entity);
                    Intent huaweiIntent = new Intent();
                    huaweiIntent.setAction("huawei.intent.action.HSM_BOOTAPP_MANAGER");
                    dataSave(huaweiIntent, data);


                    wifiSet(entity);
                    Intent huaweinetWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    dataSave(huaweinetWorkIntent, data);
                }


                break;

            //金立
            case Constant.GIONEE:
                if (version > 18) {
                    //auto running
                    autoRunSet(entity);
                    Intent gioneeIntent = new Intent();
                    gioneeIntent.setComponent(new ComponentName("com.gionee.softmanager", "com.gionee.softmanager.MainActivity"));
                    dataSave(gioneeIntent, data);

                    wifiSet(entity);
                    Intent gioneNetWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    dataSave(gioneNetWorkIntent, data);

                }
                break;

            //vivo
            case Constant.VIVO:

                if (version > 18) {
                    //background high power
                    highPowerSet(entity);
                    Intent vivoGodIntent = new Intent();
                    vivoGodIntent.setComponent(new ComponentName("com.vivo.abe", "com.vivo.applicationbehaviorengine.ui.ExcessivePowerManagerActivity"));
                    dataSave(vivoGodIntent, data);

                    wifiSet(entity);
                    Intent vivoNetWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    dataSave(vivoNetWorkIntent, data);
                }
                break;
            //oppo
            case Constant.OPPO:
                if (version > 19) {
                    ShutDownBackground(entity);
                    Intent oppoIntent = new Intent();
                    oppoIntent.setComponent(new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity"));
                    dataSave(oppoIntent, data);


                    OpenSystemSuspendWindow(entity);
                    Intent oppoOpenSysIntent = new Intent();
                    oppoOpenSysIntent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity"));
                    dataSave(oppoOpenSysIntent, data);


                } else if (version == 19) {
                    autoRunSet(entity);
                    Intent oppoIntent = new Intent();
                    oppoIntent.setComponent(new ComponentName("com.color.safecenter", "com.color.safecenter.permission.startup.StartupAppListActivity"));
                    dataSave(oppoIntent, data);
                }


                wifiSet(entity);
                Intent vivoNetWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                dataSave(vivoNetWorkIntent, data);

                break;
            //小米
            case Constant.XIAOMI:
                //小米 神隐模式 (建议只在 App 的核心功能需要后台连接网络/后台定位的情况下使用)
                SpiritAwayMode(entity);
                Intent xiaomiGodIntent = new Intent();
                xiaomiGodIntent.setComponent(new ComponentName("com.miui.powerkeeper", "com.miui.powerkeeper.ui.HiddenAppsContainerManagementActivity"));
                dataSave(xiaomiGodIntent, data);

                //小米 自启动管理
                autoRunSet(entity);
                Intent xiaomiIntent = new Intent();
                xiaomiIntent.setAction("miui.intent.action.OP_AUTO_START");
                xiaomiIntent.addCategory(Intent.CATEGORY_DEFAULT);
                dataSave(xiaomiIntent, data);

                wifiSet(entity);
                Intent xiaomiNetWorkIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                dataSave(xiaomiNetWorkIntent, data);

                break;


        }
    }

    /**
     * @param intent 需要跳转的Intent
     * @param data   容器
     */
    public void dataSave(Intent intent, ArrayList<AuthorityEntity> data) {
        if (intent != null) {
            try {
                entity.setintentEntity(new IntentEntity(intent, shadeIntent));
                data.add(new AuthorityEntity(entity));
            } catch (Exception e) {


            }


        }

    }


    private void appPertectSet(AuthorityEntity entity) {
        entity.setTitle(Constant.APP_PERTECT);
        entity.setTip(Constant.PHONE_NO_OMIT);
    }

    private void keepStandbySet(AuthorityEntity entity) {
        entity.setTitle(Constant.KEEP_RUN_IN_STANDBY);
        entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);
    }

    private void autoRunSet(AuthorityEntity entity) {
        entity.setTitle(Constant.AUTO_RUN);
        entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);
    }

    private void wifiSet(AuthorityEntity entity) {
        entity.setTitle(Constant.KEEP_WLAN_CONNECT);
        entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);
    }

    private void lockScreenSet(AuthorityEntity entity) {
        entity.setTitle(Constant.LOCK_SCREEN_CLEAR_WHITE_LIST);
        entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);

    }

    public void highPowerSet(AuthorityEntity entity) {
        entity.setTitle(Constant.BACKGROUND_HIGH_POWER);
        entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);

    }

    public void SpiritAwayMode(AuthorityEntity entity) {
        entity.setTitle(Constant.SPIRIT_AWAY_MODE);
        entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);
    }

    /***
     * 设置 关闭后台冻结 标题
     * @param entity
     */
    public void ShutDownBackground(AuthorityEntity entity) {
        entity.setTitle(Constant.SHUT_DOWN_BACKGROUND);
        entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);

    }

    /**
     * 开启系统悬浮窗
     */
    public void OpenSystemSuspendWindow(AuthorityEntity entity) {
        entity.setTitle(Constant.OPEN_SUSPEND_WINDOW);
        entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);

    }


}
