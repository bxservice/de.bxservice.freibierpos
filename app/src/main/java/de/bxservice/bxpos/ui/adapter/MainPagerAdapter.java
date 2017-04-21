/**********************************************************************
 * This file is part of FreiBier POS                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
package de.bxservice.bxpos.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
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
    private List<Fragment> createdFragments = new ArrayList<>();

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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

        //Save a reference of the existing fragments
        if (createdFragments != null && !createdFragments.contains(createdFragment))
            createdFragments.add(createdFragment);

        return createdFragment;
    }

    /**
     * Update table status in the data set in the fragment
     * @param selectedTable
     */
    public void updateStatus(Table selectedTable) {

        if (createdFragments != null) {
            boolean found = false;

            for (Fragment fragment : createdFragments) {

                if (fragment instanceof MainTableFragment && !found) {
                    MainTableFragment tableFragment = (MainTableFragment) fragment;

                    for (Table table : tableFragment.getmGridData()) {

                        if (table.getTableID() == selectedTable.getTableID()) {
                            tableFragment.updateTableStatus(tableFragment.getmGridData().indexOf(table), selectedTable);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
    }

    public void updateAllTables() {

        if (createdFragments != null) {
            for (Fragment fragment : createdFragments) {

                if (fragment instanceof MainTableFragment) {
                    MainTableFragment menuFragment = (MainTableFragment) fragment;
                    menuFragment.refreshAllTables();
                }
            }
        }
    }

}

