/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.core;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.ui.UILifecycleListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cn.com.aixiaoqi.R;
import cn.qfishphone.sipengine.SipEngineCore;
import de.blinkt.openvpn.activities.Device.ui.ProMainActivity;
import de.blinkt.openvpn.activities.MyModules.ui.BalanceParticularsActivity;
import de.blinkt.openvpn.activities.MyModules.ui.BindRechargeCardActivity;
import de.blinkt.openvpn.activities.MyModules.ui.ImportantAuthorityActivity;
import de.blinkt.openvpn.activities.MyModules.ui.PackageCategoryActivity;
import de.blinkt.openvpn.activities.MyModules.ui.RechargeActivity;
import de.blinkt.openvpn.activities.MyModules.ui.ShadeActivity;
import de.blinkt.openvpn.activities.MyModules.ui.ActivateActivity;
import de.blinkt.openvpn.activities.ShopModules.ui.CommitOrderActivity;
import de.blinkt.openvpn.activities.ShopModules.ui.MyOrderDetailActivity;
import de.blinkt.openvpn.activities.ShopModules.ui.PackageDetailActivity;
import de.blinkt.openvpn.activities.UserInfo.ui.EditNameActivity;
import de.blinkt.openvpn.activities.UserInfo.ui.VertifyPhoneNumActivity;
import de.blinkt.openvpn.bluetooth.service.UartService;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.exceptionlog.CrashHandler;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.model.ContactBean;
import de.blinkt.openvpn.util.querylocaldatebase.AsyncQueryContactHandler;
import de.blinkt.openvpn.util.querylocaldatebase.FindContactUtil;
import de.blinkt.openvpn.util.querylocaldatebase.QueryCompleteListener;

public class ICSOpenVPNApplication extends Application implements QueryCompleteListener<ContactBean> {

    private static ICSOpenVPNApplication instance;
    private static final String TAG = "OnUILifecycleListener";
    List<ContactBean> mAllList;
    public static  String random8NumberString="0102030405060708";
    /**
     * 记录当前的activity对象
     */
    public static ActivateActivity activateInstance;

    public static BindRechargeCardActivity bindRechargeCardInstance;

    public static PackageDetailActivity packageDetailInstance;

    public static CommitOrderActivity commitOrderActivity;

    public static RechargeActivity rechargeActivity;

    public static ShadeActivity shadeActivity;

    public static VertifyPhoneNumActivity vertifyPhoneNumActivity;

    public static ImportantAuthorityActivity importantAuthorityActivity;

    public static EditNameActivity editNameActivity;
    public static MyOrderDetailActivity myOrderDetailActivity;

    public static PackageCategoryActivity packageCategoryActivity;


    //接口通过空卡序列号返回的订单特定写卡数据
    public static String cardData = null;

    public static ICSOpenVPNApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }

    public List<ContactBean> getContactList() {
        if (mAllList == null) {
            mAllList = new ArrayList<>();
        }
        return mAllList;
    }

    public void setmAllList(List<ContactBean> mAllList) {
        this.mAllList = mAllList;
    }

    public static SipEngineCore the_sipengineReceive;

    //蓝牙Service
    public static UartService uartService;

    //判断是否再次重连的标记,如果不是主动断开则重新连接，如果是主动断开则不需要连接
    public static boolean isConnect;

    public void showMsg(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        CommonHttp.setContext(getApplicationContext());
//		if (Constant.IS_DEBUG) {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext(), this);
//		}

        initUpgrade();
        searchContact();
    }

    private void searchContact() {
        AsyncQueryContactHandler asyncQueryHandler = new AsyncQueryContactHandler(this, getContentResolver());
        FindContactUtil.queryContactData(asyncQueryHandler);
    }

    @Override
    public void queryComplete(List<ContactBean> mAllLists) {
        this.mAllList = mAllLists;
    }

    private void initUpgrade() {
        /**** Beta高级设置*****/
        /**
         * true表示app启动自动初始化升级模块；
         * false不好自动初始化
         * 开发者如果担心sdk初始化影响app启动速度，可以设置为false
         * 在后面某个时刻手动调用
         */
        Beta.autoInit = true;

        /**
         * true表示初始化时自动检查升级
         * false表示不会自动检查升级，需要手动调用Beta.checkUpgrade()方法
         */
        Beta.autoCheckUpgrade = true;

        /**
         * 设置升级周期为60s（默认检查周期为0s），60s内SDK不重复向后天请求策略
         */
        Beta.initDelay = 1000;

        /**
         * 设置通知栏大图标，largeIconId为项目中的图片资源；
         */
        Beta.largeIconId = R.drawable.login_icon;

        /**
         * 设置状态栏小图标，smallIconId为项目中的图片资源id;
         */
        Beta.smallIconId = R.drawable.login_icon;


        /**
         * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
         * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
         */
        Beta.defaultBannerId = R.drawable.login_icon;

        /**
         * 设置sd卡的Download为更新资源保存目录;
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        /**
         * 点击过确认的弹窗在APP下次启动自动检查更新时会再次显示;
         */
        Beta.showInterruptedStrategy = true;

        /**
         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
         * 不设置会默认所有activity都可以显示弹窗;
         */
        Beta.canShowUpgradeActs.add(ProMainActivity.class);


        /**
         *  设置自定义升级对话框UI布局
         *  注意：因为要保持接口统一，需要用户在指定控件按照以下方式设置tag，否则会影响您的正常使用：
         *  标题：beta_title，如：android:tag="beta_title"
         *  升级信息：beta_upgrade_info  如： android:tag="beta_upgrade_info"
         *  更新属性：beta_upgrade_feature 如： android:tag="beta_upgrade_feature"
         *  取消按钮：beta_cancel_button 如：android:tag="beta_cancel_button"
         *  确定按钮：beta_confirm_button 如：android:tag="beta_confirm_button"
         *  详见layout/upgrade_dialog.xml
         */
        Beta.upgradeDialogLayoutId = R.layout.upgrade_dialog;

        /**
         * 设置自定义tip弹窗UI布局
         * 注意：因为要保持接口统一，需要用户在指定控件按照以下方式设置tag，否则会影响您的正常使用：
         *  标题：beta_title，如：android:tag="beta_title"
         *  提示信息：beta_tip_message 如： android:tag="beta_tip_message"
         *  取消按钮：beta_cancel_button 如：android:tag="beta_cancel_button"
         *  确定按钮：beta_confirm_button 如：android:tag="beta_confirm_button"
         *  详见layout/tips_dialog.xml
         */
        Beta.tipsDialogLayoutId = R.layout.tips_dialog;

        /**
         *  如果想监听升级对话框的生命周期事件，可以通过设置OnUILifecycleListener接口
         *  回调参数解释：
         *  context - 当前弹窗上下文对象
         *  view - 升级对话框的根布局视图，可通过这个对象查找指定view控件
         *  upgradeInfo - 升级信息
         */
        Beta.upgradeDialogLifecycleListener = new UILifecycleListener<UpgradeInfo>() {
            @Override
            public void onCreate(Context context, View view, UpgradeInfo upgradeInfo) {
                Log.d(TAG, "onCreate");
                // 注：可通过这个回调方式获取布局的控件，如果设置了id，可通过findViewById方式获取，如果设置了tag，可以通过findViewWithTag，具体参考下面例子:


            }

            @Override
            public void onStart(Context context, View view, UpgradeInfo upgradeInfo) {

                Log.d(TAG, "onStart");
            }

            @Override
            public void onResume(Context context, View view, UpgradeInfo upgradeInfo) {
                Log.d(TAG, "onResume");
                TextView tvTitle = (TextView) view.findViewWithTag(Beta.TAG_TITLE);

                // 更多的操作：比如设置控件的点击事件
                tvTitle.setText("新版本" + upgradeInfo.versionName);
            }

            @Override
            public void onPause(Context context, View view, UpgradeInfo upgradeInfo) {
                Log.d(TAG, "onPause");
            }

            @Override
            public void onStop(Context context, View view, UpgradeInfo upgradeInfo) {
                Log.d(TAG, "onStop");
            }

            @Override
            public void onDestroy(Context context, View view, UpgradeInfo upgradeInfo) {
                Log.d(TAG, "onDestory");
            }
        };


        /**
         * 已经接入Bugly用户改用上面的初始化方法,不影响原有的crash上报功能;
         * init方法会自动检测更新，不需要再手动调用Beta.checkUpdate(),如需增加自动检查时机可以使用Beta.checkUpdate(false,false);
         * 参数1： applicationContext
         * 参数2：appId
         * 参数3：是否开启debug
         */

        if (!Constant.IS_DEBUG) {
            Bugly.init(getApplicationContext(), Constant.APPID, Constant.IS_DEBUG);
        }
        /**
         * 如果想自定义策略，按照如下方式设置
         */

        /***** Bugly高级设置 *****/
        //        BuglyStrategy strategy = new BuglyStrategy();
        /**
         * 设置app渠道号
         */
        //        strategy.setAppChannel(APP_CHANNEL);

        //        Bugly.init(getApplicationContext(), APP_ID, true, strategy);
    }

    /**
     *         * 用来判断服务是否运行.
     *         * @param context
     *         * @param className 判断的服务名字
     *         * @return true 在运行 false 不在运行
     *         
     */
    public boolean isServiceRunning(String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    Stack<Activity> stack;

    public void addTask(Activity activity) {
        if (stack == null) {
            stack = new Stack<>();
        }
        stack.add(activity);
    }

    public Stack<Activity> getActivitys() {
        return stack;
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = stack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = stack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            stack.remove(activity);
            activity.finish();

        }

    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        try {
            for (Activity activity : stack) {
                if (activity.getClass().equals(cls)) {
                    finishActivity(activity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断实例中是否有当前activity
    public boolean hasActivity(Class<?> cls) {
        for (Activity activity : stack) {
            if (activity.getClass().equals(cls)) {
                return true;
            }
        }
        return false;
    }

    public Activity getActivity(Class<?> cls) {
        for (Activity activity : stack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        try {
            if (stack.size() != 0) {
                for (int i = 0, size = stack.size(); i < size; i++) {
                    if (null != stack.get(i)) {
                        stack.get(i).finish();
                    }
                }
            }
            stack.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finishOtherActivity() {
        try {
            if (stack.size() != 0) {
                for (int i = 0, size = stack.size(); i < size; i++) {
                    if (null != stack.get(i) && !(stack.get(i) instanceof ProMainActivity)) {
                        stack.get(i).finish();
                    }
                }
            }
            stack.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

}
