package de.blinkt.openvpn.activities.Set.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.Set.Presenter.ContactUsPresenter;


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
    private ContactUsPresenter contactUsPersenter;
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
        contactUsPersenter=new ContactUsPresenter(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emailLinearLayout:
                contactUsPersenter.clickEmail();
                break;
            case R.id.phoneLinearLayout:
                contactUsPersenter.clickPhone();
                break;
            case R.id.weixinLinearLayout:
                contactUsPersenter.clickWeiXin();
                break;
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder!=null){
            unbinder.unbind();
        }
        if(contactUsPersenter!=null){
            contactUsPersenter=null;
        }
    }
}
