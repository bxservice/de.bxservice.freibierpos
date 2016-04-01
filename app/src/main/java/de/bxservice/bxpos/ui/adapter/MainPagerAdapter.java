package de.bxservice.bxpos.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.ui.fragment.MainTableFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * Created by Diego Ruiz on 18/11/15.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private DataProvider dataProvider;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        dataProvider = new DataProvider(context);
    }

    @Override
    public Fragment getItem(int position) {
        return MainTableFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return (int) dataProvider.getTotalTableGroups();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return dataProvider.getAllTableGroups().get(position).getName();
    }

}

