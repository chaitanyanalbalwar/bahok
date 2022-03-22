package com.app.bahokrider.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.bahokrider.adapters.ViewPagerAdapter;
import com.app.bahokrider.R;


public class OrdersFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static OrdersFragment newInstance(Bundle argument) {
        OrdersFragment fragment = new OrdersFragment();
        if (null != argument) {
            fragment.setArguments(argument);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        setAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();

        Fragment fragment = getChildFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
        if (fragment != null)
            fragment.onResume();
    }

    private void setAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initViews(View view) {
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tabs);
    }

}
