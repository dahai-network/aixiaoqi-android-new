package de.blinkt.openvpn.activities.MyModules.presenter;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;

import de.blinkt.openvpn.activities.MyModules.ui.ImportantAuthorityActivity;
import de.blinkt.openvpn.activities.MyModules.ui.ShadeActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.AuthorityEntity;
import de.blinkt.openvpn.model.IntentEntity;
import de.blinkt.openvpn.util.CreateFiles;

/**
 * Created by kim
 * on 2017/6/9.
 */

public class ImportantAuthorityPresenter {

    AuthorityEntity entity;
    Intent shadeIntent;
    private ImportantAuthorityActivity instance;
    CreateFiles createFiles;

    public ImportantAuthorityPresenter() {
        instance = ICSOpenVPNApplication.importantAuthorityActivity;
        if (createFiles == null)
            createFiles = new CreateFiles("aixiaoqi_author");
    }

    public void setPhoneTypeEntity(ArrayList<AuthorityEntity> data) {
        int version = Build.VERSION.SDK_INT;
        entity = new AuthorityEntity();
        shadeIntent = new Intent(instance, ShadeActivity.class);
        String phoneType = Build.MANUFACTURER.toLowerCase();
        Log.d("setPhoneTypeEntity", "phoneType=: " + phoneType);
        createFiles.print("phoneType" + phoneType);

        switch (phoneType) {
            case Constant.LEMOBILE:
                if (version > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    appPertectSet(entity);
                    handleData(Constant.LETVPACKAGENAME_APPPROTECTOR, Constant.LETVPACKAGE_APPPROTECTOR, data);
                    //auto run
                    autoRunSet(entity);
                    handleData(Constant.LETVPACKAGENAME_AUTORUN, Constant.LETVPACKAGE_AUTORUN, data);
                    //wife
                    wifiSet(entity, data);
                }
                break;
            //  case Constant.LENOVO
            case Constant.LENOVO:
                if (version > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    wifiSet(entity, data);
                }
                break;
            case Constant.MEIZU:
                if (version > Build.VERSION_CODES.JELLY_BEAN_MR2) {

                    //保持后台运行
                    keepStandbySet(entity);
                    handleData(Constant.MEIZU_PACKAGENAME_KEEPSTAND, Constant.MEIZU_PACKAGE_KEEPSTAND, data);

                    //自启动
                    autoRunSet(entity);
                    Intent meizuIntent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
                    meizuIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    meizuIntent.putExtra("packageName", ICSOpenVPNApplication.getInstance().getPackageName());
                    dataSave(meizuIntent, data);
//wife
                    wifiSet(entity, data);
                }
                break;
          case Constant.SAMSUNG:
                if (version > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    autoRunSet(entity);
                    Intent samsungLIntent = ICSOpenVPNApplication.getInstance().getPackageManager().getLaunchIntentForPackage("com.samsung.android.sm");
                    dataSave(samsungLIntent, data);

                    wifiSet(entity, data);
                }

                break;
            case Constant.ONEPLUS:
                wifiSet(entity, data);
                break;

            //华为
            case Constant.HUAWEI:
                if (version > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    //锁屏清理
                    lockScreenSet(entity);
                    handleData(Constant.HUAWEI_PACKAGENAME_LOCKSCREEN, Constant.HUAWEI_PACKAGE_LOCKSCREEN, data);
                    //auto running
                    autoRunSet(entity);
                    handleData(Constant.HUAWEI_PACKAGE_AUTORUN, data);
                    //wife
                    wifiSet(entity, data);
                }
                break;
            //金立
            case Constant.GIONEE:
                if (version > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    //auto running
                    autoRunSet(entity);
                    handleData(Constant.GIONEE_PACKAGENAME_AUTORUN, Constant.GIONEE_PACKAGE_AUTORUN, data);
                    //wife
                    wifiSet(entity, data);
                }
                break;
            //vivo
            case Constant.VIVO:
                if (version > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    //background high power
                    highPowerSet(entity);
                    handleData(Constant.VIVO_PACKAGENAME_HIGHPOWER, Constant.VIVO_PACKAGE_HIGHPOWER, data);
                    //wife
                    wifiSet(entity, data);
                }
                break;
            //oppo
            case Constant.OPPO:
                if (version > Build.VERSION_CODES.KITKAT) {
                    //关闭后台冻结
                    ShutDownBackground(entity);
                    handleData(Constant.OPPO_PACKAGENAME_SHUTDOWN, Constant.OPPO_PACKAGE_SHUTDOWN, data);
                    //开启系统悬浮窗
                    OpenSystemSuspendWindow(entity);
                    handleData(Constant.OPPO_PACKAGENAME_OPENSYSTEMSUSPEND, Constant.OPPO_PACKAGE_OPENSYSTEMSUSPEND, data);
                }
                //自启动
                if (version == Build.VERSION_CODES.KITKAT) {
                    autoRunSet(entity);
                    handleData(Constant.OPPO_PACKAGENAME_AUTORUN_KITKAT, Constant.OPPO_PACKAGE_AUTORUN_KITKAT, data);

                } else if (version > Build.VERSION_CODES.KITKAT) {
                    autoRunSet(entity);
                    handleData(Constant.OPPO_PACKAGENAME_AUTORUN, Constant.OPPO_PACKAGE_AUTORUN, data);
                }
                if (!(version == Build.VERSION_CODES.LOLLIPOP_MR1)) {
                    wifiSet(entity, data);
                }
                break;
            //小米
            case Constant.XIAOMI:
                //小米 神隐模式 (建议只在 App 的核心功能需要后台连接网络/后台定位的情况下使用)
                SpiritAwayMode(entity);
                handleData(Constant.XIAOMI_PACKAGENAME_KEEPSTAND, Constant.XIAOMI_PACKAGE_KEEPSTAND, data);

                //小米 自启动管理
                autoRunSet(entity);
                Intent xiaomiIntent = new Intent();
                xiaomiIntent.setAction("miui.intent.action.OP_AUTO_START");
                xiaomiIntent.addCategory(Intent.CATEGORY_DEFAULT);
                dataSave(xiaomiIntent, data);
                //wife
                wifiSet(entity, data);
                break;

          /*  //乐视2手机
            //letv
            case Constant.LETV:
                wifiSet(entity, data);
                break;*/
            default:
                //保持后台运行
                keepStandbySet(entity);
                handleData(Constant.MEIZU_PACKAGENAME_KEEPSTAND, Constant.MEIZU_PACKAGE_KEEPSTAND, data);

                //自启动
                autoRunSet(entity);
                Intent meizuIntent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
                meizuIntent.addCategory(Intent.CATEGORY_DEFAULT);
                meizuIntent.putExtra("packageName", ICSOpenVPNApplication.getInstance().getPackageName());
                dataSave(meizuIntent, data);
//wife
                wifiSet(entity, data);

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


    private void wifiSet(AuthorityEntity entity, ArrayList<AuthorityEntity> data) {
        entity.setTitle(Constant.KEEP_WLAN_CONNECT);
        entity.setTip(Constant.PERTECT_AIXIAOQI_RUN_NORMAL);
        handleData(Settings.ACTION_WIFI_SETTINGS, data);
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

    /**
     * @param action
     * @param data
     */
    public void handleData(String action, ArrayList<AuthorityEntity> data) {
        Intent netWorkIntent = new Intent(action);
        dataSave(netWorkIntent, data);
    }

    /**
     * @param packageName  包名
     * @param activityPath activity路径
     * @param data         数据
     */
    public void handleData(String packageName, String activityPath, ArrayList<AuthorityEntity> data) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityPath));
        dataSave(intent, data);
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
}
