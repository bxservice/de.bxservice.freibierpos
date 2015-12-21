package de.bxservice.bxpos.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.ui.fragment.MainTableFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * Created by Diego Ruiz on 18/11/15.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    DataMediator dataProvider = DataMediator.getInstance();

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a FoodMenuFragment (defined as a static inner class below).
        return MainTableFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return dataProvider.getTableGroupList().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return dataProvider.getTableGroupList().get(position).getName();
    }

}

