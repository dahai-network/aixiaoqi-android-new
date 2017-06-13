package de.blinkt.openvpn.activities.MyModules.presenter;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.OrderAdapter;
import cn.com.johnson.model.BoughtPackageEntity;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.MyModules.model.MyPackageMode;
import de.blinkt.openvpn.activities.MyModules.modelImple.MyPackageImpl;
import de.blinkt.openvpn.activities.MyModules.view.MyPackageView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.BoughtPacketHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * Created by kim
 * on 2017/6/9.
 */

public class MyPackagePresenter extends BaseNetActivity {
    private MyPackageMode myPackageMode;
    private MyPackageView myPackageView;
    XRecyclerView orderListRecylerView;
    private RelativeLayout NoNetRelativeLayout, NodataRelativeLayout;
    private OrderAdapter orderAdapter;
    TextView noDataTextView;
    public  int pageNumber;

    public MyPackagePresenter(MyPackageView myPackageView) {
        this.myPackageView = myPackageView;
        myPackageMode = new MyPackageImpl();
        initControlView();
    }

    private void initControlView() {

        orderListRecylerView = myPackageView.getOrderListRecylerView();
        NoNetRelativeLayout = myPackageView.getNoNetRelativeLayout();
        orderAdapter = myPackageView.getOrderAdapter();
        NodataRelativeLayout = myPackageView.getNodataRelativeLayout();
        noDataTextView = myPackageView.getNoDataTextView();
    }

    /**
     * 获取订单数据
     *
     * @param pageNumber
     * @param pageSize
     * @param type
     */
    public void getOrderData(int pageNumber, int pageSize, int type) {

        this.pageNumber = pageNumber;

        myPackageMode.getOrder(this, pageNumber + "", pageSize + "", type + "");

    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_GET_ORDER) {
            orderListRecylerView.loadMoreComplete();
            orderListRecylerView.refreshComplete();
            BoughtPacketHttp http = (BoughtPacketHttp) object;
            BoughtPackageEntity bean = http.getBoughtPackageEntity();
            if (bean != null) {
                if (bean.getList().size() != 0) {
                    //有数据则显示
                    NoNetRelativeLayout.setVisibility(View.GONE);
                    orderListRecylerView.setVisibility(View.VISIBLE);
                    if (pageNumber == 1) {
                        //页码为1且没有数据，则显示无数据页面
                        if (bean.getList().size() < Constant.PAGESIZE) {
                            orderAdapter.addAll(bean.getList());
                            orderListRecylerView.noMoreLoading();
                        } else {
                            orderAdapter.addAll(bean.getList());
                        }

                    } else {
                        orderAdapter.add(bean.getList());
                    }
                } else {
                    if (pageNumber == 1) {
                        orderListRecylerView.setVisibility(View.GONE);
                        NodataRelativeLayout.setVisibility(View.VISIBLE);
                        noDataTextView.setText(getResources().getString(R.string.no_order));
                    }
                    orderListRecylerView.noMoreLoading();
                }
            }
        }
        orderAdapter.notifyDataSetChanged();
    }

    @Override
    public void errorComplete(int cmdType, String errorMessage) {
        myPackageView.showToast(errorMessage);
    }

    @Override
    public void noNet() {
        orderListRecylerView.setVisibility(View.GONE);
        NoNetRelativeLayout.setVisibility(View.VISIBLE);
    }
}
