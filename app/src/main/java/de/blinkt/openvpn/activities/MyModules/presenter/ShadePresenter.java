package de.blinkt.openvpn.activities.MyModules.presenter;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import cn.com.johnson.model.PhoneAuthonCountEntity;
import de.blinkt.openvpn.activities.MyModules.ui.ShadeActivity;
import de.blinkt.openvpn.activities.MyModules.view.ShadeView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
/**
 * Created by kim
 * on 2017/6/8.
 */

public class ShadePresenter {

    private final static int ONE_STEP = 1;
    private final static int TWO_STEP = 2;
    private final static int THREE_STEP = 3;
    private ShadeActivity instance;
    private ShadeView shadeView;
    int[] ids = null;

    public ShadePresenter(ShadeView shadeView) {
        this.shadeView = shadeView;
        instance = ICSOpenVPNApplication.shadeActivity;
        ids = new int[3];
        initControlView();

    }

    ImageView hand;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    ImageView iv_01;
    ImageView iv_02;
    ImageView iv_03;
    LinearLayout ll_01;
    LinearLayout ll_02;
    LinearLayout ll_03;

    /**
     * 初始化控件
     */
    private void initControlView() {

        hand = shadeView.getHand();
        iv_01 = shadeView.getIv_01();
        iv_02 = shadeView.getIv_02();
        iv_03 = shadeView.getIv_03();
        ll_01 = shadeView.getLl_01();
        ll_02 = shadeView.getLl_02();
        ll_03 = shadeView.getLl_03();
        textView1 = shadeView.getTextView1();
        textView2 = shadeView.getTextView2();
        textView3 = shadeView.getTextView3();

    }

    String[] text;

    public void adjustDifferentPhoneView() {
        String phoneType = Build.MANUFACTURER.toLowerCase();
        int version = Build.VERSION.SDK_INT;
        switch (phoneType) {
            case Constant.LEMOBILE:
                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    case ONE_STEP:
                        ids[0] = R.string.lemobile_test_1_1;
                        text = getText(ids);
                        setResourceOne(false, text[0], R.drawable.lemobile_image_1_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);
                        break;
                    case TWO_STEP:
                        ids[0] = R.string.lemobile_test_2_1;
                        text = getText(ids);
                        setResourceOne(false, text[0], R.drawable.lemobile_image_2_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);
                        break;
                    case THREE_STEP:
                        ids[0] = R.string.lemobile_test_3_1;
                        ids[1] = R.string.lemobile_test_3_2;
                        ids[2] = R.string.lemobile_test_3_3;
                        text = getText(ids);
                        setResourceOne(true, text[0], R.drawable.lemobile_image_3_1);
                        setResourceTwo(false, text[1], R.drawable.lemobile_image_3_2);
                        setResourceThree(false, text[2], R.drawable.lemobile_image_3_3);
                        break;
                }

                break;
            case Constant.LENOVO:
                ids[0] = R.string.lenovo_test_1_0;
                ids[1] = R.string.lenovo_test_1_1;
                ids[2] = R.string.lenovo_test_1_2;
                text = getText(ids);
                setResourceOne(true, text[0], R.drawable.lenovo_image_1_0);
                setResourceTwo(false, text[1], R.drawable.lenovo_image_1_1);
                setResourceThree(false, text[2], R.drawable.lenovo_image_1_2);
                break;

            case Constant.MEIZU:
                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    case ONE_STEP://保持后台运行
                       Log.d("ShadePresenter", "adjustDifferentPhoneView: "+ids.length );
                        ids[0] = R.string.meizu_test_1_1;
                        ids[1] = R.string.meizu_test_1_2;
                        ids[2] = R.string.meizu_test_1_3;

                        text = getText(ids);
                      //  Log.d("ShadePresenter", "adjustDifferentPhoneView:  text[0]=" + text[0]+" text[1]="+ text[1] +" text[2]="+ text[2]);
                        setResourceOne(true, text[0], R.drawable.meizu_image_1_1);
                        setResourceTwo(false, text[1], R.drawable.meizu_image_1_2);
                        setResourceThree(false, text[2], R.drawable.meizu_image_1_3);
                        break;
                    case TWO_STEP://自启动
                        ids[0] = R.string.meizu_test_2_1;
                        text = getText(ids);
                        setResourceOne(false, text[0], R.drawable.meizu_note2_image_2_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);
                        break;
                    case THREE_STEP://网络
                        ids[0] = R.string.meizu_test_3_1;
                        ids[1] = R.string.meizu_test_3_2;
                        text = getText(ids);
                        setResourceOne(true, text[0], R.drawable.meizu_image_3_1);
                        setResourceTwo(false, text[1], R.drawable.meizu_image_3_2);
                        setResourceThree(true, null, 0);
                        break;
                }

                break;
            case Constant.SAMSUNG:
                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    case ONE_STEP:
                        ids[0] = R.string.samsung_test_1_1;
                        ids[1] = R.string.samsung_test_1_2;
                        text = getText(ids);
                        setResourceOne(true, text[0], R.drawable.samsung_image_1_1);
                        setResourceTwo(false, text[1], R.drawable.samsung_image_1_2);
                        setResourceThree(true, null, 0);
                        break;
                    case TWO_STEP:
                        ids[0] = R.string.samsung_test_2_1;
                        ids[1] = R.string.samsung_test_2_2;
                        text = getText(ids);
                        setResourceOne(true, text[0], R.drawable.sansung_image_2_1);
                        setResourceTwo(false, text[1], R.drawable.samsung_image_2_2);
                        setResourceThree(true, null, 0);
                        break;
                }
                break;
            case Constant.ONEPLUS:
                ids[0] = R.string.oneplus_test1;
                ids[1] = R.string.oneplus_test2;
                text = getText(ids);
                setResourceOne(true, text[0], R.drawable.oneplus_image_01);
                setResourceTwo(false, text[1], R.drawable.oppo_image_1_2);
                setResourceThree(true, null, 0);

                break;
            case Constant.HUAWEI:

                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    case ONE_STEP: //锁屏清理
                        ids[0] = R.string.huawei_mate9_test_1_1;
                        text = getText(ids);
                        setResourceOne(false, text[0], R.drawable.huawei_mate9_image_1_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);
                        break;

                    case TWO_STEP://自启动
                        ids[0] = R.string.huawei_mate9_test_1_1;
                        text = getText(ids);
                        setResourceOne(false, text[0], R.drawable.huawei_mate9_image_2_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);
                        break;

                    case THREE_STEP://p8
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            ids[0] = R.string.huawei_mateP8_test_3_1;
                            ids[1] = R.string.huawei_mateP8_test_3_2;
                            ids[2] = R.string.huawei_mateP8_test_3_3;
                            text = getText(ids);
                            setResourceOne(true, text[0], R.drawable.huawei_p8_image_3_1);
                            setResourceTwo(false, text[1], R.drawable.huawei_p8_image_3_2);
                            setResourceThree(false, text[2], R.drawable.huawei_p8_image_3_3);

                        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
                            ids[0] = R.string.huawei_mate9_test_3_1;
                            ids[1] = R.string.huawei_mate9_test_3_2;
                            text = getText(ids);
                            setResourceOne(true, text[0], R.drawable.huawei_mate9_image_3_1);
                            setResourceTwo(false, text[1], R.drawable.huawei_mate9_image_3_2);
                            setResourceThree(true, null, 0);

                        }
                        break;

                }
                break;
            case Constant.GIONEE:
                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    //权限
                    case ONE_STEP:
                        ids[0] = R.string.gionee_test_1_1;
                        ids[1] = R.string.gionee_test_1_2;
                        ids[2] = R.string.gionee_test_1_3;
                        text = getText(ids);
                        setResourceOne(true, text[0], R.drawable.goinee_image_1_1);
                        setResourceTwo(false, text[1], R.drawable.goinee_image_1_2);
                        setResourceThree(false, text[2], R.drawable.goinee_image_1_3);
                        break;
                    case TWO_STEP:
                        ids[0] = R.string.gionee_test_2_1;
                        ids[1] = R.string.gionee_test_2_2;
                        ids[2] = R.string.gionee_test_2_3;
                        text = getText(ids);

                        setResourceOne(true, text[0], R.drawable.goinee_image_2_1);
                        setResourceTwo(false, text[1], R.drawable.goinee_image_2_2);
                        setResourceThree(false, text[2], R.drawable.goinee_image_2_3);
                        break;
                }

                break;
            case Constant.VIVO:
                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    case ONE_STEP:
                        ids[0] = R.string.vivo_test_1_1;
                        text = getText(ids);
                        setResourceOne(false, text[0], R.drawable.vivo_image_1_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);
                        break;

                    case TWO_STEP:
                        ids[0] = R.string.vivo_test_2_1;
                        ids[1] = R.string.vivo_test_2_2;
                        text = getText(ids);
                        setResourceOne(true, text[0], R.drawable.vivo_image_2_1);
                        setResourceTwo(false, text[1], R.drawable.vivo_image_2_2);
                        setResourceThree(true, null, 0);
                        break;
                }
                break;
            //OPPO
            case Constant.OPPO:

                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    case ONE_STEP:
                        if (version > Build.VERSION_CODES.KITKAT) {
                            ids[0] = R.string.oppo_test_1_1;
                            ids[1] = R.string.oppo_test_1_2;
                            text = getText(ids);
                            setResourceOne(true, text[0], R.drawable.oppo_image_2_1);
                            setResourceTwo(false, text[1], R.drawable.oppo_image_2_2);
                            setResourceThree(true, null, 0);

                        } else if (version == Build.VERSION_CODES.KITKAT) {
                            ids[0] = R.string.oppoR7_test_1_1;
                            text = getText(ids);
                            setResourceOne(false, text[0], R.drawable.oppor7_image_2_1);
                            setResourceTwo(true, null, 0);
                            setResourceThree(true, null, 0);
                        }
                        break;

                    case TWO_STEP:
                        if (version == Build.VERSION_CODES.KITKAT) {
                            ids[0] = R.string.oppo_test_3_1;
                            ids[1] = R.string.oppo_test_3_2;
                            text = getText(ids);
                            setResourceOne(true, text[0], R.drawable.oppo_image_1_1);
                            setResourceTwo(false, text[1], R.drawable.oppo_image_1_2);
                            setResourceThree(true, null, 0);
                        } else {
                            ids[0] = R.string.oppo_test_2_1;
                            text = getText(ids);
                            setResourceOne(false, text[0], R.drawable.oppo_r9_image_2_1);
                            setResourceTwo(true, null, 0);
                            setResourceThree(true, null, 0);
                        }

                        break;
                    case THREE_STEP:
                        if (version > Build.VERSION_CODES.KITKAT) {
                            ids[0] = R.string.oppoR7_test_1_1;
                            text = getText(ids);
                            setResourceOne(false, text[0], R.drawable.oppo_r9_image_2_1);
                            setResourceTwo(true, null, 0);
                            setResourceThree(true, null, 0);
                        }

                        break;
                }
                break;
            case Constant.XIAOMI:

                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    //第一个权限
                    case ONE_STEP:
                        ids[0] = R.string.xiaomi_test_1_1;
                        ids[1] = R.string.xiaomi_test_1_2;
                        ids[2] = R.string.xiaomi_spirit_test_1_3;
                        text = getText(ids);
                        setResourceOne(true, text[0], R.drawable.xiaomi_image_01);
                        setResourceTwo(false, text[1], R.drawable.xiaomi_image_02);
                        setResourceThree(false, text[2], R.drawable.xiaomi_image_1_3);

                        break;
                    case TWO_STEP:
                        ids[0] = R.string.xiaomi_spirit_test_2_1;
                        text = getText(ids);
                        setResourceOne(false, text[0], R.drawable.xiaomi_image_2_2);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);

                        break;
                    case THREE_STEP:
                        ids[0] = R.string.xiaomi_test_3_1;
                        ids[1] = R.string.xiaomi_test_3_2;
                        text = getText(ids);
                        setResourceOne(true, text[0], R.drawable.xiaomi_image_3_1);
                        setResourceTwo(false, text[1], R.drawable.xiaomi_image_3_2);
                        setResourceThree(true, null, 0);

                        break;
                }
                break;
        }


    }

    /**
     * @param flg 是否显示手点击胡图标
     * @param s   文字描述
     * @param iv1 图片
     */
    public void setResourceOne(boolean flg, String s, int iv1) {
        if (flg)
            hand.setVisibility(View.GONE);
        textView1.setText(s);
        iv_01.setBackgroundResource(iv1);
    }

    /**
     * @param flg 是否隐藏该条目
     * @param s2  文字描述
     * @param iv2 图片设置
     */
    public void setResourceTwo(boolean flg, String s2, int iv2) {
        if (flg) {
            ll_02.setVisibility(View.GONE);
        } else {
            ll_02.setVisibility(View.VISIBLE);
            textView2.setText(s2);
            iv_02.setBackgroundResource(iv2);
        }

    }

    /**
     * @param flg 是否隐藏该条目
     * @param s   文字描述
     * @param iv3 图片设置
     */
    public void setResourceThree(boolean flg, String s, int iv3) {
        if (flg) {
            ll_03.setVisibility(View.GONE);
        } else {
            ll_03.setVisibility(View.VISIBLE);
            textView3.setText(s);
            iv_03.setBackgroundResource(iv3);
        }

    }

    /**
     * 获取文本信息
     *
     * @param ids 文本id
     * @return 返回文本信息
     */
    public String[] getText(int[] ids) {
        String[] strings = new String[3];
        if (ids.length > 0) {
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] != 0)
                    strings[i] = instance.getResources().getString(ids[i]);
            }

        }
        return strings;

    }


}
