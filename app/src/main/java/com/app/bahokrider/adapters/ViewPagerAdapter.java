package com.app.bahokrider.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.bahokrider.fragments.OrderHistoryFragment;
import com.app.bahokrider.fragments.OrderListFragment;

/**
 * @author Rohit Kadam
 * Date 21-11-2019.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final String[] title = {"Orders", "History"};

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new OrderListFragment();
                break;
            case 1:
                fragment = new OrderHistoryFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
