package com.couchbase.todo.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.couchbase.todo.TasksFragment;

public class ListFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT;
    private String tabTitles[] = new String[]{"Usuarios"};

    public ListFragmentPagerAdapter(FragmentManager fm) {
        super(fm);

        PAGE_COUNT = 1;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
                return new TasksFragment();

    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
