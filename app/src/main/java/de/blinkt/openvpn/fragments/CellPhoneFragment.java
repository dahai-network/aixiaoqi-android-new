package de.blinkt.openvpn.fragments;

/**
 * Created by Administrator on 2016/9/8 0008.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cn.com.aixiaoqi.R;
import cn.com.johnson.adapter.CellPhoneFragmentPagerAdapter;
import de.blinkt.openvpn.activities.ProMainActivity;
import de.blinkt.openvpn.activities.SMSAcivity;

import static de.blinkt.openvpn.constant.UmengContant.CLICKEDITSMS;
import static de.blinkt.openvpn.constant.UmengContant.CLICKTITLEPHONE;
import static de.blinkt.openvpn.constant.UmengContant.CLICKTITLESMS;


public class CellPhoneFragment extends Fragment  {
    public static RadioGroup operation_rg;
    RadioButton cell_phone_rb;
    RadioButton message_rb;
    public static  EditText dial_input_edit_text;
    TextView editTv;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    Activity activity;
    ViewPager mViewPager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view= inflater.inflate(R.layout.cell_phone_fragment, container, false);
        initView(view);
        addListener();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            operation_rg.check(cell_phone_rb.getId());
            ClickPhone();
            if (CellPhoneFragment.dial_input_edit_text.getVisibility() == View.VISIBLE) {
                showPhoneBottomBar();
            }
            else{
                hidePhoneBottomBar();
            }
        }

    }

    @Override
    public void onDestroy() {
        operation_rg=null;
        dial_input_edit_text=null;
        phoneFragment=null;
        super.onDestroy();
    }

    public void hidePhoneBottomBar() {
        ProMainActivity.bottom_bar_linearLayout.setVisibility(View.VISIBLE);
        ProMainActivity.phone_linearLayout.setVisibility(View.GONE);
    }
    public void showPhoneBottomBar() {
        ProMainActivity.bottom_bar_linearLayout.setVisibility(View.GONE);
        ProMainActivity.phone_linearLayout.setVisibility(View.VISIBLE);
    }
    private void addListener(){
        dial_input_edit_text
                .setCursorVisible(false);
        dial_input_edit_text.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //友盟方法统计
                MobclickAgent.onEvent(getActivity(), CLICKEDITSMS);
                Intent intent=new Intent(getActivity(), SMSAcivity.class);
                startActivity(intent);
            }
        });
    }
    private void initView(View view) {
        operation_rg = ((RadioGroup) view.findViewById(R.id.operation_rg));
        cell_phone_rb = ((RadioButton) view.findViewById(R.id.cell_phone_rb));
        dial_input_edit_text = ((EditText) view.findViewById(R.id.dial_input_edit_text));
        message_rb = ((RadioButton) view.findViewById(R.id.message_rb));
        editTv = ((TextView) view.findViewById(R.id.edit_tv));
        mViewPager = (ViewPager) view.findViewById(R.id.mViewPager);
        initFragment();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    ClickPhone();
                }else{
                    ClickMessage();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        this.activity = activity;
        super.onAttach(activity);
    }
    public void setFragment_Phone( Fragment_Phone    phoneFragment){
        CellPhoneFragment.phoneFragment =phoneFragment;
    }
    static Fragment_Phone  phoneFragment;
    private void initFragment() {
        fragments.clear();//清空
        fragments.add(phoneFragment);
        fragments.add(new SmsFragment());
        CellPhoneFragmentPagerAdapter mAdapter = new CellPhoneFragmentPagerAdapter(getChildFragmentManager(), fragments);
        mAdapter.setFragments(fragments);
        mViewPager.setAdapter(mAdapter);
        operation_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.cell_phone_rb:
                        //友盟方法统计
                        MobclickAgent.onEvent(getActivity(), CLICKTITLEPHONE);
                        ClickPhone();
                        break;
                    case R.id.message_rb:
                        //友盟方法统计
                        MobclickAgent.onEvent(getActivity(), CLICKTITLESMS);
                        ClickMessage();
                        break;
                }
            }
        });
    }

    private void ClickMessage() {
        cell_phone_rb.setBackgroundResource(R.drawable.default_top_cell);
        message_rb.setBackgroundResource(R.drawable.select_top_cell_sms);
        cell_phone_rb.setTextColor(Color.BLACK);
        message_rb.setTextColor(Color.WHITE);
        mViewPager.setCurrentItem(1);
        editTv.setVisibility(View.VISIBLE);
    }

    private void ClickPhone() {
        cell_phone_rb.setBackgroundResource(R.drawable.select_top_cell);
        message_rb.setBackgroundResource(R.drawable.default_top_cell_sms);
        cell_phone_rb.setTextColor(Color.WHITE);
        message_rb.setTextColor(Color.BLACK);
        editTv.setVisibility(View.GONE);
        mViewPager.setCurrentItem(0);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
