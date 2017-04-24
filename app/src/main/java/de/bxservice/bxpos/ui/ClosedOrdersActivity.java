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
package de.bxservice.bxpos.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.print.POSOutputDevice;
import de.bxservice.bxpos.logic.print.POSOutputDeviceValues;
import de.bxservice.bxpos.logic.tasks.PrintOrderTask;
import de.bxservice.bxpos.ui.adapter.ClosedOrderAdapter;
import de.bxservice.bxpos.ui.fragment.ReviewOrderFragment;

public class ClosedOrdersActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //If the app is running on a large-screen device
    private boolean mTablet;

    private RecyclerView mRecyclerView;
    private ClosedOrderAdapter mAdapter;
    private SearchView mSearchView;

    private boolean mShowPrintItem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.closed_orderd_toolbar);

        if (toolbar != null)
            setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.closed_orders_list);
        assert mRecyclerView != null;
        setupRecyclerView();

        GridLayoutManager mLayoutManager = new GridLayoutManager(getBaseContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemsListener(getBaseContext(), mRecyclerView, new RecyclerItemsListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        int idx = mRecyclerView.getChildAdapterPosition(view);
                        viewDetailFragment(mAdapter.getOrders().get(idx));
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }
                })
        );

        ViewGroup fragmentContainer = (ViewGroup) findViewById(R.id.closed_order_detail_fragment);
        mTablet = (fragmentContainer != null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_closed_order, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView = (SearchView) menu.findItem(R.id.items_search).getActionView();

        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        setupSearchView();

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {

        MenuItem printItem = menu.findItem(R.id.print_order);

        if (printItem != null) {
            if (mTablet) {
                printItem.setEnabled(mShowPrintItem);
                printItem.setVisible(mShowPrintItem);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.print_order) {
            printOrder();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void printOrder() {
        POSOutputDevice printReceiptDevice = POSOutputDevice.getDevice(getBaseContext(), POSOutputDeviceValues.TARGET_RECEIPT);

        if (printReceiptDevice != null && printReceiptDevice.getDeviceType().equalsIgnoreCase(POSOutputDeviceValues.DEVICE_PRINTER)) {
            ReviewOrderFragment fragment =
                    (ReviewOrderFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.closed_order_detail_fragment);
            if (fragment != null) {
                PrintOrderTask createOrderTask = new PrintOrderTask(this, printReceiptDevice);
                createOrderTask.execute(fragment.getOrder());
            }
        }
    }

    private void setupSearchView() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
    }

    private void setupRecyclerView() {
        mAdapter = new ClosedOrderAdapter(POSOrder.getClosedOrders(getBaseContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void viewDetailFragment(POSOrder order) {

        if (mTablet) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            ReviewOrderFragment fragment =
                    ReviewOrderFragment.newInstance(order);

            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.closed_order_detail_fragment, fragment)
                    .commit();

            mShowPrintItem = true;
            invalidateOptionsMenu();
        } else {
            Intent intent = new Intent(this, ClosedOrderDetailActivity.class);
            intent.putExtra(ReviewOrderFragment.REVIEWED_ORDER, order);
            startActivity(intent);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ClosedOrderAdapter adapter = (ClosedOrderAdapter) mRecyclerView.getAdapter();

        if (TextUtils.isEmpty(newText)) {
            adapter.getFilter().filter("");
        } else {
            adapter.getFilter().filter(newText);
        }
        return true;
    }
}
