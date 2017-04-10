package de.blinkt.openvpn.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class PackageCategoryFragment extends Fragment implements XRecyclerView.LoadingListener{
    Activity activity;
    String channel_id;
    @BindView(R.id.activite_rv)
    XRecyclerView activiteRv;
    ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle args = getArguments();
        channel_id = args != null ? args.getString("id") : "";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        this.activity = activity;
        super.onAttach(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_package_category, container, false);
        ButterKnife.bind(this, rootView);
        activiteRv.setLoadingListener(this);
        return rootView;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
