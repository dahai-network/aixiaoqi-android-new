package de.blinkt.openvpn.activities.SimOption.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.activities.SimOption.PresenterImpl.ContactPresenterImpl;
import de.blinkt.openvpn.activities.SimOption.View.ContactView;
import de.blinkt.openvpn.util.ExditTextWatcher;
import de.blinkt.openvpn.util.SetPermission;
import de.blinkt.openvpn.views.contact.SideBar;
import de.blinkt.openvpn.views.contact.TouchableRecyclerView;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class ContactActivity extends BaseActivity implements ContactView{
    @BindView(R.id.searchEditText)
    EditText searchEditText;
    @BindView(R.id.contact_member)
    TouchableRecyclerView mRecyclerView;
    @BindView(R.id.contact_dialog)
    TextView mUserDialog;
    @BindView(R.id.contact_sidebar)
    SideBar mSideBar;
    @BindView(R.id.tv_no_permission)
    TextView tvNoPermission;
    @BindView(R.id.jump_permission)
    Button jumpPermission;
    @BindView(R.id.rl_no_permission)
    RelativeLayout rlNoPermission;
    ContactPresenterImpl contactPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        contactPresenter=new ContactPresenterImpl(this,this);
        initView();
        hasLeftViewTitle(R.string.address_list, 0);
        setSearchLinstener();
    }

    @Override
    public void rlNoPermission(int isVisible) {
        rlNoPermission.setVisibility(isVisible);
    }
    private void initView() {
        tvNoPermission.setText(String.format(getString(R.string.no_permission), getString(R.string.address_list)));
        int orientation = LinearLayoutManager.VERTICAL;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, false);
        mRecyclerView.setLayoutManager(layoutManager);
        jumpPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetPermission(ContactActivity.this);
            }
        });
        mRecyclerView.setAdapter(contactPresenter.getSelectContactAdapter());
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = contactPresenter.getSelectContactAdapter().getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRecyclerView.scrollToPosition(position);
                }
            }
        });
        mSideBar.setTextView(mUserDialog);
    }

    private void setSearchLinstener() {
        new ExditTextWatcher(searchEditText, R.id.searchEditText) {
            @Override
            public void textChanged(CharSequence s, int id) {
                contactPresenter.searchContact(s.toString().trim());
            }
        };
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                setResult(RESULT_OK, data);
                finish();
                break;
        }
    }
}
