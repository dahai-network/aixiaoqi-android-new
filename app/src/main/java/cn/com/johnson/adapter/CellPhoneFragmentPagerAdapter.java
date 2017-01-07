package cn.com.johnson.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.ArrayList;

public class CellPhoneFragmentPagerAdapter extends FragmentStatePagerAdapter {
	private ArrayList<Fragment> fragments;
	private FragmentManager fm;

	public CellPhoneFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;
	}

	public CellPhoneFragmentPagerAdapter(FragmentManager fm,
										 ArrayList<Fragment> fragments) {
		super(fm);
		this.fm = fm;
		this.fragments = fragments;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void setFragments(ArrayList<Fragment> fragments) {
		ArrayList<Fragment> localFragments = this.fragments;
		if (localFragments != null) {
			FragmentTransaction ft = fm.beginTransaction();
			for (Fragment f : localFragments) {
				ft.remove(f);
			}
			ft.commit();

			if (fm != null)
				fm.executePendingTransactions();
		}
		this.fragments = fragments;
		notifyDataSetChanged();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		Object obj = super.instantiateItem(container, position);
		return obj;
	}

}
