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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.print.POSOutputDevice;
import de.bxservice.bxpos.logic.print.POSOutputDeviceValues;
import de.bxservice.bxpos.logic.tasks.PrintOrderTask;
import de.bxservice.bxpos.ui.fragment.ReviewOrderFragment;

public class ClosedOrderDetailActivity extends AppCompatActivity {

    private POSOrder order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.closed_toolbar);
        if (toolbar != null)
            setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        order = (POSOrder) getIntent().getSerializableExtra(ReviewOrderFragment.REVIEWED_ORDER);

        if (savedInstanceState == null) {
            ReviewOrderFragment fragment =
                    ReviewOrderFragment.newInstance(order);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.closedorder_detail_container, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.print_order:
                printOrder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void printOrder() {
        POSOutputDevice printReceiptDevice = POSOutputDevice.getDevice(getBaseContext(), POSOutputDeviceValues.TARGET_RECEIPT);

        if (printReceiptDevice != null && printReceiptDevice.getDeviceType().equalsIgnoreCase(POSOutputDeviceValues.DEVICE_PRINTER)) {
            PrintOrderTask createOrderTask = new PrintOrderTask(this, printReceiptDevice);
            createOrderTask.execute(order);
        }
    }

}
