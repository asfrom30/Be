package com.doyoon.android.bravenewworld.util.view;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOYOON on 7/15/2017.
 */

public class ViewPagerBuilder {

    private ViewPager viewPager;
    private List<Fragment> fragmentList;

    public static ViewPagerBuilder getInstance(ViewPager viewPager){
        viewPager.getId();
        return new ViewPagerBuilder(viewPager);
    }

    private ViewPagerBuilder(ViewPager viewPager) {
        this.viewPager = viewPager;
        this.fragmentList = new ArrayList<>();
    }

    public ViewPagerBuilder linkTabLayout(TabLayout tabLayout){
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        return this;
    }

    public ViewPagerBuilder addFragment(Fragment fragment) {
        fragmentList.add(fragment);
        return this;
    }

    public ViewPagerBuilder build(FragmentManager manager) {
        this.viewPager.setAdapter(new CustomPageAdapter(manager, this.fragmentList));
        return this;
    }

    class CustomPageAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragmentList;

        public CustomPageAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
}
