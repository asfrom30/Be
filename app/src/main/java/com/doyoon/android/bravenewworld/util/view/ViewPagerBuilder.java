package com.doyoon.android.bravenewworld.util.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.doyoon.android.bravenewworld.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DOYOON on 7/15/2017.
 */

public class ViewPagerBuilder {

    private Context context;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;

    public static ViewPagerBuilder getInstance(ViewPager viewPager, Context context){
        viewPager.getId();
        return new ViewPagerBuilder(viewPager, context);
    }

    private ViewPagerBuilder(ViewPager viewPager, Context context) {
        this.context = context;
        this.viewPager = viewPager;
        this.fragmentList = new ArrayList<>();
    }

    public ViewPagerBuilder linkTabLayout(TabLayout tabLayout){
        this.tabLayout = tabLayout;
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int tabIconColor = ContextCompat.getColor(context, R.color.custom_slate_grey);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                int tabIconColor = ContextCompat.getColor(context, R.color.custom_pinkish_grey);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
            }
        });
        return this;
    }

    public ViewPagerBuilder setFirstSelectedTabColor(){
        if(tabLayout == null) throw new RuntimeException("You must call this method after linkTabLayout()");

        TabLayout.Tab tab = tabLayout.getTabAt(0);
        int tabIconColor = ContextCompat.getColor(context, R.color.custom_slate_grey);
        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

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
