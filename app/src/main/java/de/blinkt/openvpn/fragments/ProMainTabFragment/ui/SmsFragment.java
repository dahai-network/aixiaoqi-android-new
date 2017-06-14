package de.blinkt.openvpn.fragments.ProMainTabFragment.ui;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.com.aixiaoqi.R;
import de.blinkt.openvpn.activities.SMSAcivity;
import de.blinkt.openvpn.fragments.ProMainTabFragment.PresenterImpl.SmsPresenterImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.View.SmsView;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.NetworkUtils;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;
import static de.blinkt.openvpn.constant.UmengContant.CLICKEDITSMS;
/**
 * 会话列表fragment
 */

public class SmsFragment extends Fragment implements XRecyclerView.LoadingListener, View.OnKeyListener,SmsView {

    @BindView(R.id.recyclerview)
    XRecyclerView mRecyclerView;
    @BindView(R.id.noDataTextView)
    TextView noDataTextView;
    @BindView(R.id.NodataRelativeLayout)
    RelativeLayout NodataRelativeLayout;
    @BindView(R.id.NoNetRelativeLayout)
    RelativeLayout NoNetRelativeLayout;
    @BindView(R.id.editSmsImageView)
    ImageView editSmsImageView;
    Unbinder unbinder;
    private int requestNetCount = 0;


    SmsPresenterImpl smsPresenter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sms, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        smsPresenter=new SmsPresenterImpl(this,getActivity());
        initView();
        initData();
        addListener();
        smsPresenter.registerMessageReceiver();
        return rootView;
    }

    @Override
    public void showToast(String toastContent) {
        CommonTools.showShortToast(getActivity(),toastContent);
    }

    @Override
    public void showToast(int toastId) {
        CommonTools.showShortToast(getActivity(),getString(toastId));
    }

    @Override
    public void nodataRelativeLayout(int isVisible) {
        noDataTextView.setText(isVisible==View.VISIBLE?getString(R.string.no_sms):"");
        NodataRelativeLayout.setVisibility(isVisible);
    }

    @Override
    public void noNetRelativeLayout(int isVisible) {
        NoNetRelativeLayout.setVisibility(isVisible);
    }

    @Override
    public void recyclerView(int isVisible) {
        mRecyclerView.setVisibility(isVisible);
    }

    @Override
    public void refreshComplete() {
        mRecyclerView.refreshComplete();
    }

    public void loadMoreComplete(){
        mRecyclerView.loadMoreComplete();
    }
    public void noMoreLoad(){
        mRecyclerView.noMoreLoading();
    }

    @Override
    public void onDataRefresh() {
        onRefresh();
    }

    @Override
    public void editSmsBackground(int colorId) {
        editSmsImageView.setBackground(getResources().getDrawable(colorId));
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {  //表示按返回键 时的操作
                if (!smsPresenter.backButton()) {
                    return false;
                }
                return true;    //已处理
            }
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder!=null)
            unbinder.unbind();
    }

    private void initView() {
        onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        smsPresenter.onDestory();
    }

    private void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        mRecyclerView.setLoadingListener(this);
        mRecyclerView.setAdapter(smsPresenter.getSmsListAdapter());
        smsListHttp();
    }

    private void addListener() {
        mRecyclerView.setOnKeyListener(this);
    }

    int pageNumber = 1;

    private void smsListHttp() {
        requestNetCount++;
        smsPresenter.requestSmsList(pageNumber,requestNetCount);
    }

    @Override
    public void onRefresh() {
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            return;
        }
        mRecyclerView.canMoreLoading();
        pageNumber = 1;
        smsListHttp();
    }

    @OnClick({R.id.NoNetRelativeLayout, R.id.editSmsImageView})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.NoNetRelativeLayout:
                smsListHttp();
                break;
            case R.id.editSmsImageView:
                if (!CommonTools.isFastDoubleClick(3000)) {
                    if (!smsPresenter.getSmsListAdapter().isDeleteState()) {
                        //友盟方法统计
                        MobclickAgent.onEvent(getActivity(), CLICKEDITSMS);
                        Intent intent = new Intent(getActivity(), SMSAcivity.class);
                        startActivity(intent);
                    } else {
                        smsPresenter.selectDeleteSms();
                    }
                }
                break;
        }
    }

    @Override
    public void onLoadMore() {
        if (!NetworkUtils.isNetworkAvailable(getActivity())) {
            return;
        }
        pageNumber = pageNumber + 1;
        smsListHttp();
    }

}
