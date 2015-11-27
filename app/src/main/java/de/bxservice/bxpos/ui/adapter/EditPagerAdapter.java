package de.bxservice.bxpos.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.Order;
import de.bxservice.bxpos.logic.model.ProductPrice;
import de.bxservice.bxpos.persistence.OrderDataExample;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * Created by Diego Ruiz on 27/11/15.
 */
public class EditPagerAdapter extends FragmentPagerAdapter {

    Context context;

    public EditPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a FoodMenuFragment (defined as a static inner class below).
        return EditOrderFragment.newInstance(position);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class EditOrderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        ListView listView;
        OrderArrayAdapter<String> mAdapter;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static EditOrderFragment newInstance(int sectionNumber) {
            EditOrderFragment fragment = new EditOrderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public EditOrderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_edit_order, container, false);

            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            if( sectionNumber == 0 ) {
                listView = (ListView) rootView.findViewById(R.id.lista);
                List items = new ArrayList<Order>();
                
                mAdapter = new OrderArrayAdapter<>(this.getContext(), OrderDataExample.ORDERS);


                listView.setAdapter(mAdapter);
            }
            else if ( sectionNumber == 1 ) {

            }

            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("Caesar Salad  €10");

            TextView textView1 = (TextView) rootView.findViewById(R.id.section_label1);
            textView1.setText("Africola  €3");

            TextView textView2 = (TextView) rootView.findViewById(R.id.section_label2);
            textView2.setText("Desert €2");*/
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }
    }

}