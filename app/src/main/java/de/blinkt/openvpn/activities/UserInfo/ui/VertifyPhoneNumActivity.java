package de.blinkt.openvpn.activities.UserInfo.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.UserInfo.Presenter.VertifyPhoneNumPresenter;
import de.blinkt.openvpn.activities.UserInfo.View.VertifyPhoneNumView;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.util.CommonTools;

public class VertifyPhoneNumActivity extends BaseActivity implements VertifyPhoneNumView {

    @BindView(R.id.phoneNumEditText)
    EditText phoneNumEditText;
    @BindView(R.id.nextBtn)
    Button nextBtn;
    VertifyPhoneNumPresenter vertifyPhoneNumPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertify_phone_num);
        ICSOpenVPNApplication.vertifyPhoneNumActivity = this;
        ButterKnife.bind(this);
        initSet();

        vertifyPhoneNumPresenter = new VertifyPhoneNumPresenter(this);
    }

    private void initSet() {
        hasLeftViewTitle(R.string.phone_vertification, 0);
    }

    @OnClick(R.id.nextBtn)
    public void onViewClicked() {
        //进行服务器验证
        vertifyPhoneNumPresenter.confirmedPNumber(phoneNumEditText.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        vertifyPhoneNumPresenter.releaseResource();
    }

    @Override
    public void showToast(String msg) {
        CommonTools.showShortToast(VertifyPhoneNumActivity.this, msg);
    }
}
