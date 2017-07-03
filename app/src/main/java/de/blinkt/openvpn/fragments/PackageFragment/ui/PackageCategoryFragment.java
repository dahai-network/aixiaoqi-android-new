//package de.blinkt.openvpn.fragments.PackageFragment.ui;
//
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import cn.com.aixiaoqi.R;
//import cn.com.johnson.adapter.OrderAdapter;
//import cn.com.johnson.model.BoughtPackageEntity;
//import de.blinkt.openvpn.constant.Constant;
//import de.blinkt.openvpn.fragments.PackageFragment.presenter.PackageCategoryPresenter;
//import de.blinkt.openvpn.fragments.PackageFragment.view.PackageCategoryView;
//import de.blinkt.openvpn.views.xrecycler.XRecyclerView;
//
///**
// * Created by kim
// * on 2017/4/10 0010.
// */
//
//public class PackageCategoryFragment extends Fragment implements XRecyclerView.LoadingListener, PackageCategoryView {
//    Activity activity;
//    String channel_id;
//    @BindView(R.id.activite_rv)
//    XRecyclerView activiteRv;
//    @BindView(R.id.retryTextView)
//    TextView retryTextView;
//    @BindView(R.id.NoNetRelativeLayout)
//    RelativeLayout NoNetRelativeLayout;
//    @BindView(R.id.noDataTextView)
//    TextView noDataTextView;
//    @BindView(R.id.NodataRelativeLayout)
//    RelativeLayout NodataRelativeLayout;
//    OrderAdapter orderAdapter;
//    LinearLayoutManager manager;
//    PackageCategoryPresenter packageCategoryPresenter;
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        Bundle args = getArguments();
//        channel_id = args != null ? args.getString("id") : "";
//        super.onCreate(savedInstanceState);
//    }
//    @Override
//    public void onAttach(Activity activity) {
//        // TODO Auto-generated method stub
//        this.activity = activity;
//        super.onAttach(activity);
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        View rootView = inflater.inflate(R.layout.fragment_package_category, container, false);
//        ButterKnife.bind(this, rootView);
//        packageCategoryPresenter = new PackageCategoryPresenter(this);
//        initView();
//        return rootView;
//    }
//
//    private void initView() {
//        manager = new LinearLayoutManager(getActivity());
//        activiteRv.setLayoutManager(manager);
//        activiteRv.setArrowImageView(R.drawable.iconfont_downgrey);
//        activiteRv.setLoadingListener(this);
//        orderAdapter = new OrderAdapter(getActivity(), R.layout.item_order);
//        activiteRv.setAdapter(orderAdapter);
//        addData(false);
//    }
//    int page = 1;
//    @Override
//    public void onRefresh() {
//        activiteRv.canMoreLoading();
//        page = 1;
//        addData(true);
//    }
//    @Override
//    public void onLoadMore() {
//        page++;
//        addData(true);
//    }
//    public void addData(boolean isLoadMore) {
//        packageCategoryPresenter.addData(page, Constant.PAGESIZE, -1, channel_id,isLoadMore);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//    }
//
//    @Override
//    public void loadSuccessView(BoughtPackageEntity bean) {
//
//        activiteRv.loadMoreComplete();
//        activiteRv.refreshComplete();
//
//        if (bean != null) {
//            if (bean.getList().size() != 0) {
//                //有数据则显示
//                NoNetRelativeLayout.setVisibility(View.GONE);
//                activiteRv.setVisibility(View.VISIBLE);
//                if (page == 1) {
//                    //页码为1且没有数据，则显示无数据页面
//                    if (bean.getList().size() < Constant.PAGESIZE) {
//                        orderAdapter.addAll(bean.getList());
//                        activiteRv.noMoreLoading();
//                    } else {
//                        orderAdapter.addAll(bean.getList());
//                    }
//
//                } else {
//                    orderAdapter.add(bean.getList());
//                }
//            } else {
//                if (page == 1) {
//                    activiteRv.setVisibility(View.GONE);
//                    NodataRelativeLayout.setVisibility(View.VISIBLE);
//                    if (getActivity() != null)
//                        noDataTextView.setText(getActivity().getResources().getString(R.string.no_order));
//                }
//                activiteRv.noMoreLoading();
//            }
//        }
//        orderAdapter.notifyDataSetChanged();
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        packageCategoryPresenter.releaseResource();
//    }
//}
