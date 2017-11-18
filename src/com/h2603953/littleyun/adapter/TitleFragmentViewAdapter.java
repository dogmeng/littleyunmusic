package com.h2603953.littleyun.adapter;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TitleFragmentViewAdapter extends FragmentStatePagerAdapter{
	 private final List<Fragment> mFragments = new ArrayList<>();
     private final List<String> mFragmentTitles = new ArrayList<>();
     
     public void addFragment(Fragment fragment, String title) {
         mFragments.add(fragment);
         mFragmentTitles.add(title);
     }

	public TitleFragmentViewAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		 if(mFragments.size() > position)
	     return mFragments.get(position);
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		 return mFragments.size();
	}
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        // don't super !
    }

}
