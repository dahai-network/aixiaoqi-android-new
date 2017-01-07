package de.blinkt.openvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.RecyclerBaseAdapter;
import cn.com.johnson.adapter.SelectNumberAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.constant.IntentPutKeyConstant;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.SelectNumberHttp;
import de.blinkt.openvpn.model.SelectNumberEntity;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * Created by Administrator on 2016/11/28 0028.
 */
public class SelectNumberActivity extends BaseNetActivity implements XRecyclerView.LoadingListener, RecyclerBaseAdapter.OnItemClickListener {
    @BindView(R.id.number_list_rv)
    XRecyclerView numberListRv;
    List<SelectNumberEntity.SelectInfo> list = new ArrayList<>();
    SelectNumberAdapter selectNumberAdapter;
    @BindView(R.id.noDataTextView)
    TextView noDataTextView;
    @BindView(R.id.NodataRelativeLayout)
    RelativeLayout NodataRelativeLayout;
    @BindView(R.id.retryTextView)
    TextView retryTextView;
    @BindView(R.id.NoNetRelativeLayout)
    RelativeLayout NoNetRelativeLayout;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasLeftViewTitle(R.string.info_number_list, 0);
        setContentView(R.layout.activity_select_number);
        ButterKnife.bind(this);
        initView();
        httpPhoneNumber();
    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        numberListRv.setLayoutManager(layoutManager);
        numberListRv.setArrowImageView(R.drawable.iconfont_downgrey);
        numberListRv.setLoadingListener(this);
        selectNumberAdapter = new SelectNumberAdapter(this, list);
        selectNumberAdapter.setOnItemClickListener(this);
        numberListRv.setAdapter(selectNumberAdapter);
    }

    @Override
    public void onItemClick(View view, Object data) {
        Intent intent=new Intent();
        SelectNumberEntity.SelectInfo selectInfo=(  SelectNumberEntity.SelectInfo)  data;
        intent.putExtra(IntentPutKeyConstant.SELECT_NUMBER_FEE,selectInfo.getPrice());
        intent.putExtra(IntentPutKeyConstant.SELECT_NUMBER,selectInfo.getMobileNumber());
        setResult(Constant.SAVE_SELECT_NUMBER,intent);
        finish();
    }

    private void httpPhoneNumber() {
        String province=getIntent().getStringExtra(IntentPutKeyConstant.PROVINCE);
        String city=getIntent().getStringExtra(IntentPutKeyConstant.CITY);
        if(!TextUtils.isEmpty(province)&&!TextUtils.isEmpty(city)){
        SelectNumberHttp selectNumberHttp = new SelectNumberHttp(this, HttpConfigUrl.COMTYPE_GET_SELECT_NUMBER, page, Constant.PAGESIZE,province , city, "");
        new Thread(selectNumberHttp).start();
        }
    }

    @Override
    public void onLoadMore() {
        page++;
        httpPhoneNumber();
    }

    @Override
    public void onRefresh() {
        page = 1;
        httpPhoneNumber();
    }
    List<SelectNumberEntity.SelectInfo>    mAllTempLists=new ArrayList<>() ;
    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        super.rightComplete(cmdType, object);
        numberListRv.loadMoreComplete();
        numberListRv.refreshComplete();
        NoNetRelativeLayout.setVisibility(View.GONE);
        if (cmdType == HttpConfigUrl.COMTYPE_GET_SELECT_NUMBER) {
            SelectNumberHttp selectNumberHttp = (SelectNumberHttp) object;
            if (selectNumberHttp.getStatus() == 1) {
                List<SelectNumberEntity.SelectInfo>    selectInfoList=      selectNumberHttp.getSelectNumberEntity().getList();
                if (selectNumberHttp.getSelectNumberEntity().getList().size() != 0) {
                    numberListRv.setVisibility(View.VISIBLE);
                    NodataRelativeLayout.setVisibility(View.GONE);
                    if (page == 1) {
                        mAllTempLists.clear();
                        mAllTempLists.addAll(selectInfoList);
                    } else {
                        mAllTempLists.addAll(selectInfoList);
                    }
                    selectNumberAdapter.addAll(mAllTempLists);
                    if (selectInfoList.size() < Constant.PAGESIZE)
                        numberListRv.noMoreLoading();
                } else {
                    if (page == 1) {
                        numberListRv.setVisibility(View.GONE);
                        noDataTextView.setText(getString(R.string.no_number));
                        NodataRelativeLayout.setVisibility(View.VISIBLE);
                    } else {
                        CommonTools.showShortToast(this, getString(R.string.no_more_content));
                    }
                    numberListRv.noMoreLoading();
                }
            } else {
                CommonTools.showShortToast(this, selectNumberHttp.getMsg());
            }
        }
    }
}
