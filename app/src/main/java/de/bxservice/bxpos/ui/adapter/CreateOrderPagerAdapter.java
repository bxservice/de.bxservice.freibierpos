package de.bxservice.bxpos.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.model.MProduct;
import de.bxservice.bxpos.logic.model.NewOrderGridItem;
import de.bxservice.bxpos.logic.model.ProductCategory;
import de.bxservice.bxpos.logic.model.ProductPrice;
import de.bxservice.bxpos.ui.CreateOrderActivity;

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
        private GridView grid;
        private ArrayList<NewOrderGridItem> mGridData;
        private GridOrderViewAdapter mGridAdapter;
        private List<ProductCategory> productCategoryList;
        private HashMap<NewOrderGridItem, MProduct> itemProductHashMap;

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

            mGridData = new ArrayList<>();
            itemProductHashMap = new HashMap<NewOrderGridItem, MProduct>();

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataMediator.LOCALE);

            NewOrderGridItem item;
            ProductPrice productPrice;
            int qtyOrdered;
            for( MProduct product : pc.getProducts() ){
                item = new NewOrderGridItem();
                item.setName(product.getProductName());

                productPrice = DataMediator.getInstance().getProductPriceHashMap().get(product.getProductID());
                item.setPrice(currencyFormat.format(productPrice.getStdPrice()));

                //When you navigate through the tabs it paints again everything - this lets the number stay
                qtyOrdered = ((CreateOrderActivity) getActivity()).getProductQtyOrdered(product);
                if( qtyOrdered != 0 )
                    item.setQty("x"+Integer.toString(qtyOrdered));
                else
                    item.setQty("");

                mGridData.add(item);
                itemProductHashMap.put(item,product);
            }

            grid.setGravity(Gravity.CENTER_HORIZONTAL);
            mGridAdapter = new GridOrderViewAdapter(this.getContext(), R.layout.food_menu_grid_item_layout, mGridData);

            grid.setAdapter(mGridAdapter);

            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    //Get item at position
                    NewOrderGridItem item = (NewOrderGridItem) parent.getItemAtPosition(position);
                    TextView qtyOrderedTextView = (TextView) v.findViewById(R.id.qtyOrdered_text);

                    MProduct product = itemProductHashMap.get(item);

                    ((CreateOrderActivity) getActivity()).addOrderItem(product);

                    int productQty = ((CreateOrderActivity) getActivity()).getProductQtyOrdered(product);

                    qtyOrderedTextView.setText("x" + Integer.toString(productQty));
                }
            });

            return rootView;
        }
    }

}

