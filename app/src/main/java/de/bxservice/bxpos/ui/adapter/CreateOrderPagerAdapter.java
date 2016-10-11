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
import java.util.List;

import de.bxservice.bxpos.logic.daomanager.PosProductCategoryManagement;
import de.bxservice.bxpos.logic.model.pos.NewOrderGridItem;
import de.bxservice.bxpos.ui.fragment.FoodMenuFragment;

/**
 * Created by Diego Ruiz on 19/11/15.
 */
public class CreateOrderPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private PosProductCategoryManagement dataProvider;

    public CreateOrderPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        dataProvider = new PosProductCategoryManagement(mContext);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a FoodMenuFragment (defined as a static inner class below).
        return FoodMenuFragment.newInstance(position);
    }

    @Override
    /**
     * Total number of tabs
     */
    public int getCount() {

        return (int) dataProvider.getTotalCategories();
    }

    @Override
    /**
     * Return the titles of each tab
     */
    public CharSequence getPageTitle(int position) {

        return dataProvider.getAllCategories().get(position).getName();
    }

    /**
     * Updates the quantity when the item is selected from the search list
     * @param fm
     * @param selectedItem
     * @param quantity
     */
    public void updateQty (FragmentManager fm, NewOrderGridItem selectedItem, int quantity) {

        List<Fragment> allFragments = fm.getFragments();
        boolean found = false;

        for(Fragment fragment : allFragments) {

            if(fragment instanceof  FoodMenuFragment && !found) {
                FoodMenuFragment menuFragment = (FoodMenuFragment) fragment;

                for(NewOrderGridItem item : menuFragment.getmGridData()) {

                    if (item.getKey().equals(selectedItem.getKey())) {
                        menuFragment.updateQtyOnClick(menuFragment.getmGridData().indexOf(item), quantity);
                        found = true;
                    }
                }

            }

        }
    }

    /**
     * Refresh the quantity when some items have been deleted
     * @param fm
     */
    public void refreshAllQty (FragmentManager fm) {

        List<Fragment> allFragments = fm.getFragments();

        for (Fragment fragment : allFragments) {

            if (fragment instanceof FoodMenuFragment) {
                FoodMenuFragment menuFragment = (FoodMenuFragment) fragment;
                menuFragment.refreshAllQty();
            }
        }
    }

}

