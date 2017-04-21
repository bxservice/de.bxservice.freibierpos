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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.OpenOrderGridItem;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.PosProperties;
import de.bxservice.bxpos.ui.adapter.GridOpenOrderViewAdapter;

public class ViewOpenOrdersActivity extends AppCompatActivity {

    private static final int EDIT_ORDER_REQUEST = 2;  // The request code

    private GridView gridview;
    private ArrayList<OpenOrderGridItem> mGridData;
    private GridOpenOrderViewAdapter mGridAdapter;
    private HashMap<OpenOrderGridItem, POSOrder> orderItemHashMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_open_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.open_order_toolbar);
        setSupportActionBar(toolbar);

        gridview = (GridView) findViewById(R.id.open_orders_gridview);

        mGridData = new ArrayList<>();
        orderItemHashMap = new HashMap<>();

        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();

        OpenOrderGridItem item;
        BigDecimal totalLines;

        for(POSOrder order : POSOrder.getOpenOrders(getBaseContext())) {
            item = new OpenOrderGridItem();
            item.setOrderNo(getString(R.string.order) + ": " + order.getDocumentNo());

            totalLines = order.getTotallines();
            item.setPrice(getString(R.string.total) + ": " + currencyFormat.format(totalLines));

            String table;

            if(order.getTable() == null)
                table = getString(R.string.unset_table);
            else
                table = order.getTable().getTableName();

            item.setTable(getString(R.string.table) + ": " + table);

            mGridData.add(item);
            orderItemHashMap.put(item,order);
        }

        mGridAdapter = new GridOpenOrderViewAdapter(this, R.layout.open_order_grid_item_layout, mGridData);

        gridview.setAdapter(mGridAdapter);
        gridview.setGravity(Gravity.CENTER_HORIZONTAL);


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Get item at position
                OpenOrderGridItem item = (OpenOrderGridItem) parent.getItemAtPosition(position);
                POSOrder order = orderItemHashMap.get(item);
                openEditOrder(order);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_order_toolbar, menu);
        return true;
    }

    public void openEditOrder(POSOrder order) {
        Intent intent = new Intent(this, EditOrderActivity.class);
        intent.putExtra(EditOrderActivity.CALLER_ACTIVITY,"ViewOpenOrdersActivity");
        intent.putExtra(EditOrderActivity.DRAFT_ORDER, order);
        startActivityForResult(intent, EDIT_ORDER_REQUEST);
    }

    /**
     * When it comes back from the edit order Activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
         if (requestCode == EDIT_ORDER_REQUEST) {
            //When coming from edit order and items where added and sent
            if (resultCode == 2 || resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
            //When on back button - table changed. Update the UI
            else if (resultCode == 3) {
                this.recreate();
            }
        }
    }

}
