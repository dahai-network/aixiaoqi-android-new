package de.blinkt.openvpn.fragments.ProMainTabFragment.PresenterImpl;

import java.util.List;

import cn.com.johnson.model.HotPackageEntity;
import cn.com.johnson.model.IndexBannerEntity;
import de.blinkt.openvpn.activities.NetPresenterBaseImpl;
import de.blinkt.openvpn.constant.HttpConfigUrl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl.BannerModelImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl.HardWareModelImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.ModelImpl.HotPackageModelImpl;
import de.blinkt.openvpn.fragments.ProMainTabFragment.Presenter.IndexPresenter;
import de.blinkt.openvpn.fragments.ProMainTabFragment.View.IndexView;
import de.blinkt.openvpn.http.BannerHttp;
import de.blinkt.openvpn.http.CommonHttp;
import de.blinkt.openvpn.http.GetHotHttp;
import de.blinkt.openvpn.http.GetProductHttp;
import de.blinkt.openvpn.model.ProductEntity;

import static de.blinkt.openvpn.constant.HttpConfigUrl.COMTYPE_GET_PRODUCTS;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class IndexPresenterImpl extends NetPresenterBaseImpl implements IndexPresenter {
    IndexView indexView;
    HotPackageModelImpl hotPackageModel;
    HardWareModelImpl hardWareModel;
    BannerModelImpl bannerModel;
    public IndexPresenterImpl(IndexView indexView){
        this.indexView=indexView;
        hotPackageModel=new HotPackageModelImpl(this);
        hardWareModel=new HardWareModelImpl(this);
        bannerModel=new BannerModelImpl(this);
    }

    @Override
    public void requestHotPackageModel(String hotCount) {
        hotPackageModel.requestHotPackageModel(hotCount);
    }

    @Override
    public void requestHardWare() {
        hardWareModel.requestHardWare();
    }

    @Override
    public void requestBanner() {
        bannerModel.requestBanner();
    }

    @Override
    public void rightLoad(int cmdType, CommonHttp object) {
        if (cmdType == HttpConfigUrl.COMTYPE_INDEX_BANNER) {
            BannerHttp http = (BannerHttp) object;
            List<IndexBannerEntity> bannerData = http.getBannerList();
            if (bannerData != null && bannerData.size() != 0) {
                indexView.initBanner(bannerData);
            }
        } else if (cmdType == HttpConfigUrl.COMTYPE_GET_HOT) {
            GetHotHttp http = (GetHotHttp) object;
            List<HotPackageEntity> hotList = http.getHotPackageEntityList();
            indexView.initHotPackage(hotList);
        } else if (cmdType == COMTYPE_GET_PRODUCTS) {
            GetProductHttp http = (GetProductHttp) object;
            List<ProductEntity> bean = http.getProductEntity();
            indexView.initHardWareProduct(bean);
        }

    }

    @Override
    public void onDestroy() {
        indexView=null;
        bannerModel=null;
        hardWareModel=null;
        hotPackageModel=null;
    }
}
