package com.intelliworkz.admin.admingujaratabroad;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bhatt on 1/17/2017.
 */
public class Pager extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();


    public Pager(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);

    }

    @Override
    public Fragment getItem(int position) {
        String a=NewsActivity.tabTitlesId.get(position);
        return NewsListFragment.newInstance(position, a);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}
