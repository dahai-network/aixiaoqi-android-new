package de.blinkt.openvpn.fragments.ProMainTabFragment.View;

import java.util.List;

import cn.com.johnson.model.HotPackageEntity;
import cn.com.johnson.model.IndexBannerEntity;
import de.blinkt.openvpn.model.ProductEntity;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public interface IndexView {
   void  initBanner(List<IndexBannerEntity> bannerEntityList);
    void initHotPackage(List<HotPackageEntity> hotPackageEntityList);
    void initHardWareProduct(List<ProductEntity>productEntityList);
}
