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
package de.bxservice.bxpos.logic.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.IOException;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.daomanager.PosOrgInfoManagement;
import de.bxservice.bxpos.logic.model.idempiere.RestaurantInfo;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.print.POSOutputDevice;
import de.bxservice.bxpos.logic.print.POSPrinter;
import de.bxservice.bxpos.logic.print.POSPrinterFactory;
import de.bxservice.bxpos.logic.print.POSPrinterService;
import de.bxservice.bxpos.logic.print.POSPrinterServiceFactory;
import de.bxservice.bxpos.ui.EditOrderActivity;
import de.bxservice.bxpos.ui.OfflineAdminSettingsActivity;
import de.bxservice.bxpos.ui.PayOrderActivity;

/**
 * Created by Diego Ruiz on 20/04/16.
 */
public class PrintOrderTask extends AsyncTask<POSOrder, Void, Boolean> {

    private Activity mActivity;
    private POSOutputDevice printerDevice;
    private POSPrinterService bt;


    public PrintOrderTask(Activity callerActivity, POSOutputDevice device) {
        mActivity = callerActivity;
        printerDevice = device;
    }

    @Override
    protected Boolean doInBackground(POSOrder... orders) {

        boolean success = true;
        POSPrinterServiceFactory printerServiceFactory = new POSPrinterServiceFactory();

        bt = printerServiceFactory.getPrinterService(printerDevice.getConnectionType(), mActivity, printerDevice.getPrinterName());

        for(POSOrder order : orders) {
            if(bt != null && bt.isConnected()) {
                POSPrinterFactory printerFactory = new POSPrinterFactory();
                POSPrinter printer = printerFactory.getPrinter(printerDevice.getPrinterLanguage(), order, printerDevice.getPageWidth());

                if(mActivity instanceof EditOrderActivity) {
                    bt.sendData(printer.printTicket(printerDevice.getDocTarget(),
                            mActivity.getResources().getString(R.string.order),
                            mActivity.getResources().getString(R.string.table),
                            order.getTable() != null ? order.getTable().getTableName() : mActivity.getResources().getString(R.string.unset_table),
                            mActivity.getResources().getString(R.string.waiter_role),
                            mActivity.getResources().getString(R.string.guests)));
                } else if(mActivity instanceof PayOrderActivity) {

                    PosOrgInfoManagement orgInfoManager = new PosOrgInfoManagement(mActivity.getBaseContext());
                    RestaurantInfo restaurantInfo = orgInfoManager.get(1); //Get org info to print in the receipt

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity.getBaseContext());
                    String orderPrefix = sharedPref.getString(OfflineAdminSettingsActivity.KEY_ORDER_PREFIX, "");

                    bt.sendData(printer.printReceipt(restaurantInfo.getName(),
                            restaurantInfo.getAddress1(),
                            restaurantInfo.getPostalCode() + " " + restaurantInfo.getCity(),
                            mActivity.getResources().getString(R.string.receipt),
                            orderPrefix,
                            mActivity.getResources().getString(R.string.table),
                            order.getTable() != null ? order.getTable().getTableName() : mActivity.getResources().getString(R.string.unset_table),
                            mActivity.getResources().getString(R.string.waiter_role),
                            mActivity.getResources().getString(R.string.guests),
                            mActivity.getResources().getString(R.string.subtotal),
                            mActivity.getResources().getString(R.string.set_extra),
                            mActivity.getResources().getString(R.string.total),
                            mActivity.getResources().getString(R.string.cash),
                            mActivity.getResources().getString(R.string.change)));
                }
            }
        }

        return success;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if(bt != null) {
            try {
                Thread.sleep(5000);
                if(bt.isConnected())
                    bt.closeConnection();
            } catch(IOException e) {

            } catch(InterruptedException ex) {

            }
        }
    }
}