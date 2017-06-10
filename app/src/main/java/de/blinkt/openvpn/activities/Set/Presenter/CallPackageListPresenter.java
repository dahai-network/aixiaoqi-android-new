package de.blinkt.openvpn.activities.Set.Presenter;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.CallPacketAdapter;
import de.blinkt.openvpn.activities.Base.BaseNetActivity;
import de.blinkt.openvpn.activities.Set.Model.CallPackageListMode;
import de.blinkt.openvpn.activities.Set.ModelImpl.CallPackageListModeImpl;
import de.blinkt.openvpn.activities.Set.View.CallPackageListView;
import de.blinkt.openvpn.constant.Constant;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetPakcetHttp;
import de.blinkt.openvpn.model.PacketEntity;
import de.blinkt.openvpn.views.xrecycler.XRecyclerView;

/**
 * Created by kim
 * on 2017/6/6.
 */

public class CallPackageListPresenter extends BaseNetActivity {

    private CallPackageListMode callPackageListMode;
    private CallPackageListView callPackageListView;
    public XRecyclerView callListRecylerView;
    RelativeLayout NoNetRelativeLayout;
    CallPacketAdapter callPacketAdapter;
    RelativeLayout NodataRelativeLayout;
    TextView noDataTextView;
    int pageNumber;

    public CallPackageListPresenter(CallPackageListView callPackageListView) {
        this.callPackageListView = callPackageListView;
        callPackageListMode = new CallPackageListModeImpl();

    }

    public void getCallPackageListData() {
        pageNumber = callPackageListView.getPageNumber();
        String pageSize = callPackageListView.getPageSize();
        String category = callPackageListView.getCategory();
        callListRecylerView = callPackageListView.getCallListRecylerView();
        NoNetRelativeLayout = callPackageListView.getNoNetRelativeLayout();
        callPacketAdapter = callPackageListView.getCallPacketAdapter();
        NodataRelativeLayout = callPackageListView.getNodataRelativeLayout();
        noDataTextView = callPackageListView.getNoDataTextView();
//获取数据
        callPackageListMode.getPackageListData(pageNumber + "", pageSize, category, this);
    }

    @Override
    public void rightComplete(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_PACKET_GET) {
            callListRecylerView.loadMoreComplete();
            callListRecylerView.refreshComplete();
            GetPakcetHttp http = (GetPakcetHttp) object;
            PacketEntity bean = http.getPacketEntity();
            if (bean != null) {
                if (bean.getList().size() != 0) {
                    //有数据则显示
                    NoNetRelativeLayout.setVisibility(View.GONE);
                    callListRecylerView.setVisibility(View.VISIBLE);
                    if (pageNumber == 1) {
                        //页码为1且没有数据，则显示无数据页面
                        if (bean.getList().size() < Constant.PAGESIZE) {
                            callPacketAdapter.add(bean.getList());
                            callListRecylerView.noMoreLoading();
                        } else
                            callPacketAdapter.add(bean.getList());
                    } else
                        callPacketAdapter.addAll(bean.getList());
                } else {
                    if (pageNumber == 1) {
                        callListRecylerView.setVisibility(View.GONE);
                        NodataRelativeLayout.setVisibility(View.VISIBLE);
                        noDataTextView.setText(getResources().getString(R.string.no_packet));
                    }
                    callListRecylerView.noMoreLoading();
                }
            }
        }
        callPacketAdapter.notifyDataSetChanged();
    }

    @Override
    public void noNet() {
        callListRecylerView.setVisibility(View.GONE);
        NoNetRelativeLayout.setVisibility(View.VISIBLE);
    }
}
