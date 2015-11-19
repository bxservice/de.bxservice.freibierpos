package de.bxservice.bxpos.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.model.Product;
import de.bxservice.bxpos.logic.model.ProductCategory;

/**
 * Created by Diego Ruiz on 19/11/15.
 */
public class CreateOrderPagerAdapter extends FragmentPagerAdapter {

    DataMediator dataProvider = DataMediator.getInstance();

    public CreateOrderPagerAdapter(FragmentManager fm) {
        super(fm);
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

        return dataProvider.getProductCategoryList().size();
    }

    @Override
    /**
     * Return the titles of each tab
     */
    public CharSequence getPageTitle(int position) {

        return dataProvider.getProductCategoryList().get(position).getName();
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class FoodMenuFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        List<String> list;
        GridView grid;
        GridTableViewAdapter mGridAdapter;
        ArrayList<GridItem> mGridData;
        private List<ProductCategory> productCategoryList;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FoodMenuFragment newInstance(int sectionNumber) {
            FoodMenuFragment fragment = new FoodMenuFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public FoodMenuFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_create_order_activity, container, false);

            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            grid = (GridView) rootView.findViewById(R.id.create_order_gridview);

            productCategoryList = DataMediator.getInstance().getProductCategoryList();

            ProductCategory pc = productCategoryList.get(sectionNumber);

            list=new ArrayList<String>();

            for( Product p : pc.getProducts() ){
                list.add(p.getProductName());
            }

            grid.setGravity(Gravity.CENTER_HORIZONTAL);

            ArrayAdapter<String> adp = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_dropdown_item_1line,list);
            grid.setAdapter(adp);

            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
            return rootView;
        }
    }

}

