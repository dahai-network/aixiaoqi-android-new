package de.blinkt.openvpn.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.FragmentAdapter;
import de.blinkt.openvpn.activities.Base.BaseActivity;
import de.blinkt.openvpn.fragments.PackageCategoryFragment;
import de.blinkt.openvpn.util.CommonTools;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class PackageCategoryActivity extends BaseActivity {



    @BindView(R.id.titlebar_iv_lefttext)
    TextView titlebarIvLefttext;
    @BindView(R.id.mRadioGroup_content)
    LinearLayout mRadioGroupContent;
    @BindView(R.id.mViewPager)
    ViewPager mViewPager;

    /**
     * 用户选择的新闻分类列表
     */
    private ArrayList<String> userChannelList = new ArrayList<String>();
    /**
     * 当前选中的栏目
     */
    private int columnSelectIndex = 0;
    /**
     * 屏幕宽度
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
            params.leftMargin = 5;
            params.rightMargin = 5;
            TextView columnTextView = new TextView(this);
            columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
            if (drawable == null) {
                drawable = getResources().getDrawable(R.drawable.image_slidethetriangle);
                drawable.setBounds(0, 0, drawable.getMinimumWidth()/2, drawable.getMinimumHeight()/2);
            }
//            columnTextView.setGravity(Gravity.CENTER);
            columnTextView.setPadding(105, 45, 15, 0);
            columnTextView.setLayoutParams(params);
            columnTextView.setId(i);
            columnTextView.setText(userChannelList.get(i));
            if (columnSelectIndex == i) {
                columnTextView.setSelected(true);
                columnTextView.setCompoundDrawables(null, null, null, drawable);
            }
            columnTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mRadioGroupContent.getChildCount(); i++) {
                        TextView localView = (TextView) mRadioGroupContent.getChildAt(i);
                        if (localView != v) {
                            localView.setSelected(false);
                            localView.setCompoundDrawables(null, null, null, null);
                        } else {
                            localView.setSelected(true);
                            mViewPager.setCurrentItem(i);
                            localView.setCompoundDrawables(null, null, null, drawable);
                        }
                    }

                }
            });
            mRadioGroupContent.addView(columnTextView, i, params);
        }
    }

    /**
     * 选择的Column里面的Tab
     */
    private void selectTab(int tab_postion) {
        columnSelectIndex = tab_postion;
        //判断是否选中
        for (int j = 0; j < mRadioGroupContent.getChildCount(); j++) {
            TextView checkView = (TextView) mRadioGroupContent.getChildAt(j);
            boolean ischeck;
            if (j == tab_postion) {
                ischeck = true;
                checkView.setCompoundDrawables(null, null, null, drawable);

            } else {
                ischeck = false;
                checkView.setCompoundDrawables(null, null, null, null);
            }
            checkView.setSelected(ischeck);
        }
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
        mViewPager.setOnPageChangeListener(pageListener);
    }

    /**
     * ViewPager切换监听方法
     */
    public ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub
            mViewPager.setCurrentItem(position);
            selectTab(position);
        }
    };
}
