package de.blinkt.openvpn.activities.MyModules.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.FragmentAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.fragments.PackageFragment.ui.PackageCategoryFragment;
import de.blinkt.openvpn.util.CommonTools;
import de.blinkt.openvpn.util.PageChangeListener;

/**
 * Created by kim
 * on 2017/4/10 0010.
 */

public class PackageCategoryActivity extends BaseActivity {

    @BindView(R.id.titlebar_iv_lefttext)
    TextView titlebarIvLefttext;
    @BindView(R.id.mRadioGroup_content)
    LinearLayout mRadioGroupContent;
    @BindView(R.id.mViewPager)
    ViewPager mViewPager;

    /**
     *
     */
    private ArrayList<String> userChannelList = new ArrayList<String>();
    /**
     *
     */
    private int columnSelectIndex = 0;
    /**
     *
     */
    private int mScreenWidth = 0;
    /**
     * Item宽度
     */
    private int mItemWidth = 0;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_category);
        ICSOpenVPNApplication.packageCategoryActivity = this;
        ButterKnife.bind(this);
        mScreenWidth = CommonTools.getScreenWidth(this);
        mItemWidth = mScreenWidth / 4;// 一个Item宽度为屏幕的1/7
        initView();
        titlebarIvLefttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    /**
     * 初始化layout控件
     */
    private void initView() {
        initColumnData();
        setChangelView();
    }

    /**
     * 当栏目项发生变化时候调用
     */
    private void setChangelView() {
        initTabColumn();
        initFragment();
    }

    /**
     * 获取Column栏目 数据
     */
    private void initColumnData() {
        userChannelList.add(getString(R.string.no_activite));
        userChannelList.add(getString(R.string.activited));
        userChannelList.add(getString(R.string.expire));
    }

    /**
     * 初始化Column栏目项
     */
    Drawable drawable;

    private void initTabColumn() {
        mRadioGroupContent.removeAllViews();
        int count = userChannelList.size();

        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth, LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams paramstext = new LinearLayout.LayoutParams(mItemWidth, 0);
            LinearLayout.LayoutParams paramsImage = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            paramstext.weight = 1;
            params.leftMargin = 5;
            params.rightMargin = 5;
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(params);
            linearLayout.setGravity(Gravity.CENTER);
            TextView columnTextView = new TextView(this);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(paramsImage);
            imageView.setVisibility(View.INVISIBLE);
            imageView.setImageResource(R.drawable.image_slidethetriangle);
            columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
//            if (drawable == null) {
//                drawable = getResources().getDrawable(R.drawable.image_slidethetriangle);
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//            }
            columnTextView.setGravity(Gravity.CENTER);
            columnTextView.setLayoutParams(paramstext);
            linearLayout.setId(i);
            columnTextView.setText(userChannelList.get(i));
            if (columnSelectIndex == i) {
                imageView.setVisibility(View.VISIBLE);
            }
            linearLayout.addView(columnTextView);
            linearLayout.addView(imageView);
            linearLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mRadioGroupContent.getChildCount(); i++) {
                        LinearLayout localView = (LinearLayout) mRadioGroupContent.getChildAt(i);
                        if (localView != v) {
                            localView.setSelected(false);
                            if (localView.getChildAt(1) instanceof ImageView) {
                                localView.getChildAt(1).setVisibility(View.INVISIBLE);
                            }
                        } else {
                            localView.setSelected(true);
                            mViewPager.setCurrentItem(i);
                            if (localView.getChildAt(1) instanceof ImageView) {
                                localView.getChildAt(1).setVisibility(View.VISIBLE);
                            }
                        }
                    }

                }
            });
            mRadioGroupContent.addView(linearLayout, i, params);
        }
    }


    /**
     * 选择的Column里面的Tab
     */
    private void selectTab(int tab_postion) {
        columnSelectIndex = tab_postion;
        //判断是否选中
        for (int j = 0; j < mRadioGroupContent.getChildCount(); j++) {
            LinearLayout checkView = (LinearLayout) mRadioGroupContent.getChildAt(j);
            boolean ischeck;
            if (j == tab_postion) {
                ischeck = true;
                if (checkView.getChildAt(1) instanceof ImageView) {
                    checkView.getChildAt(1).setVisibility(View.VISIBLE);
                }
            } else {
                ischeck = false;
                if (checkView.getChildAt(1) instanceof ImageView) {
                    checkView.getChildAt(1).setVisibility(View.INVISIBLE);
                }
            }
            checkView.setSelected(ischeck);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (isDestory) {
            onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        if (isDestory) {
            onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ((PackageCategoryFragment) fragments.get(0)).addData(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public boolean isDestory;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestory = true;
    }


    /**
     * 初始化Fragment
     */
    private void initFragment() {
        fragments.clear();//清空
        int count = userChannelList.size();
        for (int i = 0; i < count; i++) {
            Bundle data = new Bundle();
            data.putString("id", i + "");
            PackageCategoryFragment packageCategoryFragment = new PackageCategoryFragment();
            packageCategoryFragment.setArguments(data);
            fragments.add(packageCategoryFragment);
        }
        FragmentAdapter mAdapetr = new FragmentAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapetr);
        new PageChangeListener(mViewPager) {
            @Override
            public void pageSelected(int position) {
                mViewPager.setCurrentItem(position);
                selectTab(position);
            }
        };
    }

}
