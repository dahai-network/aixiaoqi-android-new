package de.blinkt.openvpn.activities.Set;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Set.PresenterImpl.ContactUsPersenterImpl;
import de.blinkt.openvpn.util.CommonTools;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTOURUSEEMAIL;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTOURUSEPHONE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKCONTACTOURUSEWEIXINHAO;

/**
 * Created by Administrator on 2016/8/27.
 */
public class ContactUsActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.emailLinearLayout)
    TextView emailLinearLayout;
    @BindView(R.id.phoneLinearLayout)
    TextView phoneLinearLayout;
    @BindView(R.id.weixinLinearLayout)
    TextView weixinLinearLayout;
    Unbinder unbinder;
    private  ContactUsPersenterImpl contactUsPersenterImpl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSet();
    }

    private void initSet() {
        setContentView(R.layout.activity_contact_us);
        unbinder= ButterKnife.bind(this);
        hasLeftViewTitle(R.string.connect_us, 0);
        emailLinearLayout.setOnClickListener(this);
        phoneLinearLayout.setOnClickListener(this);
        weixinLinearLayout.setOnClickListener(this);
        contactUsPersenterImpl=new ContactUsPersenterImpl(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emailLinearLayout:
                contactUsPersenterImpl.clickEmail();
                break;
            case R.id.phoneLinearLayout:
                contactUsPersenterImpl.clickPhone();
                break;
            case R.id.weixinLinearLayout:
                contactUsPersenterImpl.clickWeiXin();
                break;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder!=null){
            unbinder.unbind();
        }
        if(contactUsPersenterImpl!=null){
            contactUsPersenterImpl=null;
        }
    }
}
