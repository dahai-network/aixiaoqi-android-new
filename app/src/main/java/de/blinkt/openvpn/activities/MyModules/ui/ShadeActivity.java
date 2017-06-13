package de.blinkt.openvpn.activities.MyModules.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.MyModules.presenter.ShadePresenter;
import de.blinkt.openvpn.activities.MyModules.view.ShadeView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;


public class ShadeActivity extends Activity implements ShadeView {
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
    ShadePresenter shadePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shade);
        ICSOpenVPNApplication.shadeActivity = this;
        initView();
        shadePresenter = new ShadePresenter(this);
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
        setBackground();
    }
    /**
     * 设置背景
     */
    public void setBackground() {
        if (Build.MANUFACTURER.toLowerCase().equals(Constant.SAMSUNG))
            ll_root.setBackgroundResource(R.color.transparent_66A2A2A2);
        else
            ll_root.setBackgroundResource(R.color.transparent_00A2A2A2);

    }
    /**
     * 初始化数据
     */
    private void initData() {
        shadePresenter.adjustDifferentPhoneView();
    }

    @Override
    public ImageView getHand() {
        return hand;
    }

    @Override
    public TextView getTextView1() {
        return textView1;
    }

    @Override
    public ImageView getIv_01() {
        return iv_01;
    }

    @Override
    public LinearLayout getLl_01() {
        return ll_01;
    }

    @Override
    public LinearLayout getLl_02() {
        return ll_02;
    }

    @Override
    public TextView getTextView2() {
        return textView2;
    }

    @Override
    public ImageView getIv_02() {
        return iv_02;
    }

    @Override
    public LinearLayout getLl_03() {
        return ll_03;
    }

    @Override
    public TextView getTextView3() {
        return textView3;
    }

    @Override
    public ImageView getIv_03() {
        return iv_03;
    }
}
