package de.blinkt.openvpn.activities.MyModules.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.OrderAdapter;
import cn.com.johnson.model.BoughtPackageEntity;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.fragments.PackageFragment.presenter.PackageCategoryPresenter;

import de.blinkt.openvpn.fragments.PackageFragment.view.PackageCategoryView;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * Created by kim
 * on 2017/4/10 0010.
 */

public class PackageCategoryActivity extends BaseActivity implements XRecyclerView.LoadingListener, PackageCategoryView {

    @BindView(R.id.activite_rv)
    XRecyclerView activiteRv;
    @BindView(R.id.retryTextView)
    TextView retryTextView;
    @BindView(R.id.NoNetRelativeLayout)
    RelativeLayout NoNetRelativeLayout;
    @BindView(R.id.noDataTextView)
    TextView noDataTextView;
    @BindView(R.id.NodataRelativeLayout)
    RelativeLayout NodataRelativeLayout;
    OrderAdapter orderAdapter;
    LinearLayoutManager manager;
    PackageCategoryPresenter packageCategoryPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_package_category);
        ButterKnife.bind(this);
        hasLeftViewTitle(R.string.my_pakages,-1);
        packageCategoryPresenter = new PackageCategoryPresenter(this);
        initView();
    }


    private void initView() {
        manager = new LinearLayoutManager(this);
        activiteRv.setLayoutManager(manager);
        activiteRv.setArrowImageView(R.drawable.iconfont_downgrey);
        activiteRv.setLoadingListener(this);
        orderAdapter = new OrderAdapter(this, R.layout.item_order);
        activiteRv.setAdapter(orderAdapter);
        addData(false);
    }
    int page = 1;
    @Override
    public void onRefresh() {
        activiteRv.canMoreLoading();
        page = 1;
        addData(true);
    }

    @Override
    public void showProgress(int id) {
        super.showProgress(id);
    }

    @Override
    public void showToast(String showContent) {
        super.showToast(showContent);
    }

    @Override
    public void showToast(int showContentId) {
        super.showToast(showContentId);
    }

    @Override
    public void dismissProgress() {
        super.dismissProgress();
    }

    @Override
    public void onLoadMore() {
        page++;
        addData(true);
    }
    public void addData(boolean isLoadMore) {
        packageCategoryPresenter.addData(page, Constant.PAGESIZE, 0,isLoadMore);
    }



    @Override
    public void loadSuccessView(BoughtPackageEntity bean) {

        activiteRv.loadMoreComplete();
        activiteRv.refreshComplete();

        if (bean != null) {
            if (bean.getList().size() != 0) {
                //有数据则显示
                NoNetRelativeLayout.setVisibility(View.GONE);
                activiteRv.setVisibility(View.VISIBLE);
                if (page == 1) {
                    //页码为1且没有数据，则显示无数据页面
                    if (bean.getList().size() < Constant.PAGESIZE) {
                        orderAdapter.addAll(bean.getList());
                        activiteRv.noMoreLoading();
                    } else {
                        orderAdapter.addAll(bean.getList());
                    }

                } else {
                    orderAdapter.add(bean.getList());
                }
            } else {
                if (page == 1) {
                    activiteRv.setVisibility(View.GONE);
                    NodataRelativeLayout.setVisibility(View.VISIBLE);
                        noDataTextView.setText(getResources().getString(R.string.no_order));
                }
                activiteRv.noMoreLoading();
            }
        }
        orderAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        packageCategoryPresenter.releaseResource();
    }
}
