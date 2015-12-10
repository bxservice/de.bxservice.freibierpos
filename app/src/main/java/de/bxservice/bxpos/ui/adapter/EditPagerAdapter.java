package de.bxservice.bxpos.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.POSOrder;
import de.bxservice.bxpos.ui.fragment.OrderedItemsFragment;
import de.bxservice.bxpos.ui.fragment.OrderingItemsFragment;


/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * Created by Diego Ruiz on 27/11/15.
 */
public class EditPagerAdapter extends FragmentPagerAdapter {

    Context context;
    POSOrder order;

    public EditPagerAdapter(FragmentManager fm, Context context, POSOrder order) {
        super(fm);
        this.context = context;
        this.order = order;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                return OrderingItemsFragment.newInstance(position, order);

            case 1:
                return OrderedItemsFragment.newInstance(position, order);

            default:
                return OrderedItemsFragment.newInstance(position, order);
        }

    }

    @Override
    public int getCount() {
        // Show 2 total pages. Ordered - Ordering.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.ordering);
            case 1:
                return context.getResources().getString(R.string.ordered);
        }
        return null;
    }

}