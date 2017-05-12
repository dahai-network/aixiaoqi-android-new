package de.blinkt.openvpn.util;

import android.support.v4.view.ViewPager;

/**
 * Created by Administrator on 2017/5/12 0012.
 */

public abstract class PageChangeListener implements ViewPager.OnPageChangeListener {
    public PageChangeListener(ViewPager mViewPage){
        mViewPage.addOnPageChangeListener(this);
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        pageSelected(position);
    }
    public abstract void  pageSelected(int position);
}
