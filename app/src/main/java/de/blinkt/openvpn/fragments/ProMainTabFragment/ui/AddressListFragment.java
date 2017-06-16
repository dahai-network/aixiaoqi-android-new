package de.blinkt.openvpn.fragments.ProMainTabFragment.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.fragments.ProMainTabFragment.PresenterImpl.AddressPresenterImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.View.AddressListView;
import de.blinkt.openvpn.fragments.base.BaseStatusFragment;
import de.blinkt.openvpn.util.ExditTextWatcher;
import de.blinkt.openvpn.util.SetPermission;
import de.blinkt.openvpn.views.TitleBar;
import de.blinkt.openvpn.views.TopProgressView;
import de.blinkt.openvpn.views.contact.SideBar;
import de.blinkt.openvpn.views.contact.TouchableRecyclerView;

public class AddressListFragment extends BaseStatusFragment implements AddressListView {

    @BindView(R.id.title)
    TitleBar title;
    @BindView(R.id.top_view)
    TopProgressView topView;
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
    Unbinder unbinder;
    AddressPresenterImpl addressPresenter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setLayoutId(R.layout.fragment_selection_common);
        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        addressPresenter=new AddressPresenterImpl(this,getActivity());
        initData();
        return rootView;
    }

    @Override
    public void rlNoPermission(int isVisible) {
        rlNoPermission.setVisibility(isVisible);
    }

    private void initData( ) {
        title.setTextTitle(getString(R.string.address_list));
        tvNoPermission.setText(String.format(getString(R.string.no_permission), getString(R.string.address_list)));
        mSideBar.setTextView(mUserDialog);
        jumpPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetPermission(getActivity());
            }
        });
        int orientation = LinearLayoutManager.VERTICAL;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), orientation, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(addressPresenter.getmAdapter());
        setSearchLinstener();
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(mSideBar!=null)
            mUserDialog.setText("");
        if (isVisibleToUser) {
            addressPresenter.visibleFragment();
        }
    }

    private void setSearchLinstener() {

        new ExditTextWatcher(searchEditText, R.id.searchEditText) {
            @Override
            public void textChanged(CharSequence s, int id) {
                addressPresenter.searchContact(s.toString().trim());
            }
        };

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = addressPresenter.getmAdapter().getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRecyclerView.scrollToPosition(position);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
