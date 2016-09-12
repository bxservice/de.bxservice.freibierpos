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
package de.bxservice.bxpos.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.daomanager.PosProductCategoryManagement;
import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.pos.NewOrderGridItem;
import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;
import de.bxservice.bxpos.ui.CreateOrderActivity;
import de.bxservice.bxpos.ui.adapter.GridOrderViewAdapter;

/**
 * A placeholder fragment containing a simple view.
 * Created by Diego Ruiz on 18/12/15.
 */
public class FoodMenuFragment extends Fragment {
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
    private PosProductCategoryManagement categoryManager;

    public ArrayList<NewOrderGridItem> getmGridData() {
        return mGridData;
    }

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

        categoryManager = new PosProductCategoryManagement(getActivity().getBaseContext());

        productCategoryList = categoryManager.getAllCategories();

        ProductCategory productCategory = productCategoryList.get(sectionNumber);

        mGridData = new ArrayList<>();
        itemProductHashMap = new HashMap<>();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DefaultPosData.LOCALE);

        NewOrderGridItem item;
        ProductPrice productPrice;
        int qtyOrdered;
        for(MProduct product : productCategory.getProducts()) {
            item = new NewOrderGridItem();
            item.setName(product.getProductName());

            productPrice = product.getProductPrice(getActivity().getBaseContext());
            item.setPrice(currencyFormat.format(productPrice.getStdPrice()));

            //When you navigate through the tabs it paints again everything - this lets the number stay
            qtyOrdered = ((CreateOrderActivity) getActivity()).getProductQtyOrdered(product);
            if(qtyOrdered != 0)
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
                MProduct product = itemProductHashMap.get(item);

                ((CreateOrderActivity) getActivity()).addOrderItem(product);

                int productQty = ((CreateOrderActivity) getActivity()).getProductQtyOrdered(product);

                updateQtyOnClick(position, productQty);
            }
        });

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                NewOrderGridItem item = (NewOrderGridItem) parent.getItemAtPosition(position);
                MProduct product = itemProductHashMap.get(item);

                ((CreateOrderActivity) getActivity()).addMultipleItems(product);

                return true;
            }
        });

        return rootView;
    }

    public void updateQtyOnClick(int position, int quantity) {
        mGridData.get(position).setQty("x"+Integer.toString(quantity));
        mGridAdapter.setGridData(mGridData);
    }

    /**
     * Refresh the quantity in the orderItems
     */
    public void refreshAllQty() {
        for (int i = 0; i < mGridData.size(); i++) {
            MProduct product = itemProductHashMap.get(mGridData.get(i));
            int productQty = ((CreateOrderActivity) getActivity()).getProductQtyOrdered(product);
            if (productQty != 0)
                mGridData.get(i).setQty("x"+Integer.toString(productQty));
            else
                mGridData.get(i).setQty("");
        }
        mGridAdapter.setGridData(mGridData);
    }

}