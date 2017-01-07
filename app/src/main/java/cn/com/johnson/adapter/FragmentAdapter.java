package cn.com.johnson.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.ArrayList;

public class FragmentAdapter extends FragmentPagerAdapter {
	ArrayList<Fragment> list;


	public FragmentAdapter(FragmentManager fm, ArrayList<Fragment> list) {
		super(fm);

        if (list==null)
        {
            this.list=new ArrayList<>();
        }else
        {
            this.list=list;
        }

	}

	@Override
	public Fragment getItem(int i) {
		return list.get(i);
	}

	@Override
	public int getCount() {
		return list.size();
	}
}
