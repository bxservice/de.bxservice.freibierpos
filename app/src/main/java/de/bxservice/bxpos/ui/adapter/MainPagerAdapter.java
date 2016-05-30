package de.bxservice.bxpos.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import de.bxservice.bxpos.logic.daomanager.PosTableGroupManagement;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.ui.fragment.MainTableFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * Created by Diego Ruiz on 18/11/15.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private PosTableGroupManagement dataProvider;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        dataProvider = new PosTableGroupManagement(context);
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

    /**
     * Update table status in the data set in the fragment
     * @param fm
     * @param selectedTable
     * @param status
     */
    public void updateStatus(FragmentManager fm, Table selectedTable, String status) {

        List<Fragment> allFragments = fm.getFragments();
        boolean found = false;

        for(Fragment fragment : allFragments) {

            if(fragment instanceof  MainTableFragment && !found) {
                MainTableFragment tableFragment = (MainTableFragment) fragment;

                for(Table table : tableFragment.getmGridData()) {

                    if (table.getTableID() == selectedTable.getTableID()) {
                        tableFragment.updateTableStatus(tableFragment.getmGridData().indexOf(table), status);
                        found = true;
                        break;
                    }
                }

            }

        }
    }

}

