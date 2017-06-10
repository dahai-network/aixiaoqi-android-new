package de.blinkt.openvpn.activities.Set.Presenter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.ParticularAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.Set.Model.BalanceParticularsMode;
import de.blinkt.openvpn.activities.Set.ModelImpl.BalanceParticularsImpl;
import de.blinkt.openvpn.activities.Set.View.BalanceParticularsView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.ParticularHttp;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * Created by kim
 * on 2017/6/5.
 */

public class BalanceParticularsPresenter extends BaseNetActivity {

    private BalanceParticularsMode balanceParticularsMode;
    private BalanceParticularsView balanceParticularsView;

    private XRecyclerView particularsRecyclerView;
    private RelativeLayout NoNetRelativeLayout;
    ParticularAdapter particularAdapter;
    int pageNumber;
    public BaseNetActivity baseNetActivity;
    RelativeLayout NodataRelativeLayout;
    TextView noDataTextView;
    public Context context;

    public BalanceParticularsPresenter(Context context, BalanceParticularsView balanceParticularsView) {
        this.context = context;
        this.balanceParticularsView = balanceParticularsView;
        balanceParticularsMode = new BalanceParticularsImpl();

        initViewData();
    }

    /**
     * 初始化一些控件和固定的数据
     */
    private void initViewData() {
        particularsRecyclerView = balanceParticularsView.getParticularsRecyclerView();
        NoNetRelativeLayout = balanceParticularsView.getNoNetRelativeLayout();
        particularAdapter = balanceParticularsView.getParticularAdapter();
        NodataRelativeLayout = balanceParticularsView.getNodataRelativeLayout();
        noDataTextView = balanceParticularsView.getNoDataTextView();

    }

    /**
     * 加载订单数据
     */
    public void addData() {
        pageNumber = balanceParticularsView.getPageNumber();

        balanceParticularsMode.getAccountData(balanceParticularsView.getPageNumber(), Constant.PAGESIZE, this);

    }


    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        Log.e("Presenter", "rightComplete: " + cmdType);
        particularsRecyclerView.loadMoreComplete();
        particularsRecyclerView.refreshComplete();
        ParticularHttp http = (ParticularHttp) object;
        if (http.getParticularEntity().getList().size() != 0) {
            particularsRecyclerView.setVisibility(View.VISIBLE);
            NoNetRelativeLayout.setVisibility(View.GONE);
            if (pageNumber == 1) {

                if (http.getParticularEntity().getList().size() < Constant.PAGESIZE) {
                    particularAdapter.add(http.getParticularEntity().getList());
                    particularsRecyclerView.noMoreLoading();
                } else {
                    particularAdapter.add(http.getParticularEntity().getList());
                    particularsRecyclerView.canMoreLoading();
                }
            } else {
                particularAdapter.addAll(http.getParticularEntity().getList());
            }
        } else {
            if (pageNumber == 1) {
                particularsRecyclerView.setVisibility(View.GONE);
                NodataRelativeLayout.setVisibility(View.VISIBLE);
                noDataTextView.setText(context.getResources().getString(R.string.no_balance_detail));
            }
            particularsRecyclerView.noMoreLoading();
        }
        particularAdapter.notifyDataSetChanged();

    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        balanceParticularsView.showToast(errorMessage);
    }

    @Override
    public void noNet() {
        NoNetRelativeLayout.setVisibility(View.VISIBLE);
        particularsRecyclerView.setVisibility(View.GONE);
        particularsRecyclerView.noMoreLoading();
    }
}
