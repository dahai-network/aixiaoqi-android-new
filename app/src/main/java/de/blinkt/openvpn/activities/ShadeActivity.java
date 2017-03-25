package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import cn.com.johnson.model.PhoneAuthonCountEntity;
import de.blinkt.openvpn.constant.Constant;


public class ShadeActivity extends Activity {
    TextView textView1;
    LinearLayout ll_01;
    ImageView iv_01;
    LinearLayout ll_02;
    TextView textView2;
    ImageView iv_02;
    LinearLayout ll_03;
    TextView textView3;
    ImageView iv_03;
    ImageView hand;
    LinearLayout ll_root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shade);
        initView();
        initData();
        initEvent();

    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {
        ll_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化界面
     */
    private void initView() {
        ll_root = (LinearLayout) findViewById(R.id.ll_root);

        //第一列
        ll_01 = (LinearLayout) findViewById(R.id.ll_01);
        textView1 = (TextView) findViewById(R.id.textView1);
        iv_01 = (ImageView) findViewById(R.id.iv_01);
        hand = (ImageView) findViewById(R.id.iv_hand);

        //第二 列
        ll_02 = (LinearLayout) findViewById(R.id.ll_02);
        textView2 = (TextView) findViewById(R.id.textView2);
        iv_02 = (ImageView) findViewById(R.id.iv_02);

        //第三列
        ll_03 = (LinearLayout) findViewById(R.id.ll_03);
        iv_03 = (ImageView) findViewById(R.id.iv_03);
        textView3 = (TextView) findViewById(R.id.textView3);


    }

    /**
     * 初始化数据
     */
    private void initData() {

        String phoneType = Build.MANUFACTURER.toLowerCase();
        int version = Build.VERSION.SDK_INT;
        switch (phoneType) {
            case Constant.LEMOBILE:
                String le_s1;
                String le_s2;
                String le_s3;
                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    case 1:
                        le_s1 = getResources().getString(R.string.lemobile_test_1_1);
                        setResourceOne(false, le_s1, R.drawable.lemobile_image_1_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);
                        break;
                    case 2:
                        le_s1 = getResources().getString(R.string.lemobile_test_2_1);
                        setResourceOne(false, le_s1, R.drawable.lemobile_image_2_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);
                        break;
                    case 3:
                        le_s1 = getResources().getString(R.string.lemobile_test_3_1);
                        le_s2 = getResources().getString(R.string.lemobile_test_3_2);
                        le_s3 = getResources().getString(R.string.lemobile_test_3_3);
                        setResourceOne(true, le_s1, R.drawable.lemobile_image_3_1);
                        setResourceTwo(false, le_s2, R.drawable.lemobile_image_3_2);
                        setResourceThree(false, le_s3, R.drawable.lemobile_image_3_3);
                        break;
                }

                break;
            case Constant.LENOVO:
                String lenovn_s1 = getResources().getString(R.string.lenovo_test_1_0);
                String lenovn_s2 = getResources().getString(R.string.lenovo_test_1_1);
                String lenovn_s3 = getResources().getString(R.string.lenovo_test_1_2);
                setResourceOne(true, lenovn_s1, R.drawable.lenovo_image_1_0);
                setResourceTwo(false, lenovn_s2, R.drawable.lenovo_image_1_1);
                setResourceThree(false, lenovn_s3, R.drawable.lenovo_image_1_2);

                break;

            case Constant.MEIZU:
                String meizu_s1;
                String meizu_s2;
                String meizu_s3;
                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {

                    case 1://保持后台运行
                        meizu_s1 = getResources().getString(R.string.meizu_test_1_1);
                        meizu_s2 = getResources().getString(R.string.meizu_test_1_2);
                        meizu_s3 = getResources().getString(R.string.meizu_test_1_3);
                        setResourceOne(true, meizu_s1, R.drawable.meizu_image_1_1);
                        setResourceTwo(false, meizu_s2, R.drawable.meizu_image_1_2);
                        setResourceThree(false, meizu_s3, R.drawable.meizu_image_1_3);
                        break;
                    case 2://自启动
                        meizu_s1 = getResources().getString(R.string.meizu_test_2_1);
                        setResourceOne(false, meizu_s1, R.drawable.meizu_note2_image_2_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);
                        break;
                    case 3://网络
                        meizu_s1 = getResources().getString(R.string.meizu_test_3_1);
                        meizu_s2 = getResources().getString(R.string.meizu_test_3_2);
                        setResourceOne(true, meizu_s1, R.drawable.meizu_image_3_1);
                        setResourceTwo(false, meizu_s2, R.drawable.meizu_image_3_2);
                        setResourceThree(true, null, 0);
                        break;
                }

                break;
            case Constant.SAMSUNG:
                String samsung_s1;
                String samsung_s2;

                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    case 1:
                        samsung_s1 = getResources().getString(R.string.samsung_test_1_1);
                        samsung_s2 = getResources().getString(R.string.samsung_test_1_2);
                        setResourceOne(true, samsung_s1, R.drawable.samsung_image_1_1);
                        setResourceTwo(false, samsung_s2, R.drawable.samsung_image_1_2);
                        setResourceThree(true, null, 0);
                        break;
                    case 2:

                        samsung_s1 = getResources().getString(R.string.samsung_test_2_1);
                        samsung_s2 = getResources().getString(R.string.samsung_test_2_2);
                        setResourceOne(true, samsung_s1, R.drawable.sansung_image_2_1);
                        setResourceTwo(false, samsung_s2, R.drawable.samsung_image_2_2);
                        setResourceThree(true, null, 0);

                        break;

                }


                break;
            case Constant.ONEPLUS:
                String oneplus_s1 = getResources().getString(R.string.oneplus_test1);
                String oneplus_s2 = getResources().getString(R.string.oneplus_test2);
                setResourceOne(true, oneplus_s1, R.drawable.oneplus_image_01);
                setResourceTwo(false, oneplus_s1, R.drawable.oppo_image_1_2);
                setResourceThree(true, null, 0);

                break;
            case Constant.HUAWEI:
                String huawei_s1;
                String huawei_s2;
                String huawei_s3;
                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {

                    case 1: //锁屏清理

                        huawei_s1 = getResources().getString(R.string.huawei_mate9_test_1_1);
                        setResourceOne(false, huawei_s1, R.drawable.huawei_mate9_image_1_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);
                        break;

                    case 2://自启动
                        huawei_s1 = getResources().getString(R.string.huawei_mate9_test_1_1);
                        setResourceOne(false, huawei_s1, R.drawable.huawei_mate9_image_2_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);

                        break;

                    case 3://p8

                        if (Build.VERSION.SDK_INT > 18 && Build.VERSION.SDK_INT < 24) {
                            huawei_s1 = getResources().getString(R.string.huawei_mateP8_test_3_1);
                            huawei_s2 = getResources().getString(R.string.huawei_mateP8_test_3_2);
                            huawei_s3 = getResources().getString(R.string.huawei_mateP8_test_3_3);
                            setResourceOne(true, huawei_s1, R.drawable.huawei_p8_image_3_1);
                            setResourceTwo(false, huawei_s2, R.drawable.huawei_p8_image_3_2);
                            setResourceThree(false, huawei_s3, R.drawable.huawei_p8_image_3_3);
                        } else if (Build.VERSION.SDK_INT == 24) {
                            huawei_s1 = getResources().getString(R.string.huawei_mate9_test_3_1);
                            huawei_s2 = getResources().getString(R.string.huawei_mate9_test_3_2);
                            setResourceOne(true, huawei_s1, R.drawable.huawei_mate9_image_3_1);
                            setResourceTwo(false, huawei_s2, R.drawable.huawei_mate9_image_3_2);
                            setResourceThree(true, null, 0);

                        }
                        break;

                }


                break;
            case Constant.GIONEE:
                String gionee_s1;
                String gionee_s2;
                String gionee_s3;
                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    //权限
                    case 1:
                        gionee_s1 = getResources().getString(R.string.gionee_test_1_1);
                        gionee_s2 = getResources().getString(R.string.gionee_test_1_2);
                        gionee_s3 = getResources().getString(R.string.gionee_test_1_3);
                        setResourceOne(true, gionee_s1, R.drawable.goinee_image_1_1);
                        setResourceTwo(false, gionee_s2, R.drawable.goinee_image_1_2);
                        setResourceThree(false, gionee_s3, R.drawable.goinee_image_1_3);
                        break;
                    case 2:
                        gionee_s1 = getResources().getString(R.string.gionee_test_2_1);
                        gionee_s2 = getResources().getString(R.string.gionee_test_2_2);
                        gionee_s3 = getResources().getString(R.string.gionee_test_2_3);

                        setResourceOne(true, gionee_s1, R.drawable.goinee_image_2_1);
                        setResourceTwo(false, gionee_s2, R.drawable.goinee_image_2_2);
                        setResourceThree(false, gionee_s3, R.drawable.goinee_image_2_3);
                        break;
                }

                break;
            case Constant.VIVO:

                String vivo_test;
                String vivo_test1;

                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    case 1:
                        vivo_test = getResources().getString(R.string.vivo_test_1_1);
                        setResourceOne(false, vivo_test, R.drawable.vivo_image_1_1);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);
                        break;

                    case 2:
                        vivo_test = getResources().getString(R.string.vivo_test_2_1);
                        vivo_test1 = getResources().getString(R.string.vivo_test_2_2);
                        setResourceOne(true, vivo_test, R.drawable.vivo_image_2_1);
                        setResourceTwo(false, vivo_test1, R.drawable.vivo_image_2_2);
                        setResourceThree(true, null, 0);
                        break;

                }

                break;

            //OPPO
            case Constant.OPPO:
                String oppo_test1;
                String oppo_test2;

                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {
                    case 1:
                        if (version > 19) {
                            oppo_test1 = getResources().getString(R.string.oppo_test_1_1);
                            oppo_test2 = getResources().getString(R.string.oppo_test_1_2);
                            setResourceOne(true, oppo_test1, R.drawable.oppo_image_2_1);
                            setResourceTwo(false, oppo_test2, R.drawable.oppo_image_2_2);
                            setResourceThree(true, null, 0);
                        } else if (version == 19) {

                            oppo_test1 = getResources().getString(R.string.oppoR7_test_1_1);
                            setResourceOne(false, oppo_test1, R.drawable.oppor7_image_2_1);
                            setResourceTwo(true, null, 0);
                            setResourceThree(true, null, 0);

                        }
                        break;
                    case 2:

                        oppo_test1 = getResources().getString(R.string.oppo_test_2_1);
                        oppo_test2 = getResources().getString(R.string.oppo_test_2_2);
                        setResourceOne(true, oppo_test1, R.drawable.oppo_image_1_1);
                        setResourceTwo(false, oppo_test2, R.drawable.oppo_image_1_2);
                        setResourceThree(true, null, 0);

                        break;


                }

                break;

            case Constant.XIAOMI:
                String xiaomi_s1;
                String xiaomi_s2;
                String xiaomi_s3;
                switch (PhoneAuthonCountEntity.getInstance().getPosition()) {

                    //第一个权限
                    case 1:
                        xiaomi_s1 = getResources().getString(R.string.xiaomi_test_1_1);
                        xiaomi_s2 = getResources().getString(R.string.xiaomi_test_1_2);
                        xiaomi_s3 = getResources().getString(R.string.xiaomi_spirit_test_1_3);
                        setResourceOne(true, xiaomi_s1, R.drawable.xiaomi_image_01);
                        setResourceTwo(false, xiaomi_s2, R.drawable.xiaomi_image_02);
                        setResourceThree(false, xiaomi_s3, R.drawable.xiaomi_image_1_3);
                        break;
                    case 2:
                        xiaomi_s1 = getResources().getString(R.string.xiaomi_spirit_test_2_1);
                        setResourceOne(false, xiaomi_s1, R.drawable.xiaomi_image_2_2);
                        setResourceTwo(true, null, 0);
                        setResourceThree(true, null, 0);

                        break;
                    case 3:

                        xiaomi_s1 = getResources().getString(R.string.xiaomi_test_3_1);
                        xiaomi_s2 = getResources().getString(R.string.xiaomi_test_3_2);
                        setResourceOne(true, xiaomi_s1, R.drawable.xiaomi_image_3_1);
                        setResourceTwo(false, xiaomi_s2, R.drawable.xiaomi_image_3_2);
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

}
