package de.blinkt.openvpn.activities.MyModules.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.AuthorityAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.MyModules.presenter.ImportantAuthorityPresenter;
import de.blinkt.openvpn.activities.Device.ui.ProMainActivity;
import de.blinkt.openvpn.activities.UserInfo.ui.LoginMainActivity;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.model.AuthorityEntity;
import de.blinkt.openvpn.util.IntentWrapper;
import de.blinkt.openvpn.util.SharedUtils;

public class ImportantAuthorityActivity extends BaseActivity {

    @BindView(R.id.authorityRecyclerView)
    RecyclerView authorityRecyclerView;
    ImportantAuthorityPresenter importantAuthorityPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_authority);
        ICSOpenVPNApplication.importantAuthorityActivity = this;
        ButterKnife.bind(this);
        importantAuthorityPresenter = new ImportantAuthorityPresenter();
        initSet();
    }

    @Override
    public void onBackPressed() {
        if (!SharedUtils.getInstance().readBoolean(IntentPutKeyConstant.IS_START_UP)) {
            SharedUtils.getInstance().writeBoolean(IntentPutKeyConstant.IS_START_UP, true);
            toActivity(LoginMainActivity.class);
        } else {
            super.onBackPressed();
        }
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
        importantAuthorityPresenter.setPhoneTypeEntity(data);
        if (data.size() == 0) {
            //Log.d(TAG, "getPhoneTypeEntity: "+IntentWrapper);
            IntentWrapper.whiteListMatters(ProMainActivity.instance, "服务的持续运行");
            finish();
        }
        data.get(0).setCanClick(true);

        if (SharedUtils.getInstance().readBoolean(IntentPutKeyConstant.IS_START_UP)) {
            for (int i = 1; i < data.size(); i++) {
                data.get(i).setCanClick(true);
            }
        }
        return data;
    }



}
