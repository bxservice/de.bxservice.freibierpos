package de.bxservice.bxpos.logic.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.IOException;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.daomanager.PosOrgInfoManagement;
import de.bxservice.bxpos.logic.model.idempiere.RestaurantInfo;
import de.bxservice.bxpos.logic.print.BluetoothPrinterService;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.print.POSOutputDevice;
import de.bxservice.bxpos.logic.print.POSOutputDeviceValues;
import de.bxservice.bxpos.logic.print.POSPrinter;
import de.bxservice.bxpos.logic.print.POSPrinterFactory;
import de.bxservice.bxpos.ui.EditOrderActivity;
import de.bxservice.bxpos.ui.PayOrderActivity;

/**
 * Created by Diego Ruiz on 20/04/16.
 */
public class PrintOrderTask extends AsyncTask<POSOrder, Void, Boolean> {

    private Activity mActivity;
    private POSOutputDevice printerDevice;
    private BluetoothPrinterService bt;


    public PrintOrderTask(Activity callerActivity, POSOutputDevice device) {
        mActivity = callerActivity;
        printerDevice = device;
    }

    @Override
    protected Boolean doInBackground(POSOrder... orders) {

        boolean success = true;
        POSPrinterFactory printerFactory = new POSPrinterFactory();

        //If bluetooth device
        if(printerDevice.getConnectionType().equals(POSOutputDeviceValues.CONNECTION_BLUETOOTH))
            bt = new BluetoothPrinterService(mActivity, printerDevice.getPrinterName());

        for(POSOrder order : orders) {
            if(bt != null && bt.isConnected()) {
                POSPrinter printer = printerFactory.getPrinter(printerDevice.getPrinterLanguage(), order);
                if(mActivity instanceof EditOrderActivity) {
                    bt.sendData(String.format(printer.printKitchen(),
                            new Object[] { mActivity.getResources().getString(R.string.order),
                                    mActivity.getResources().getString(R.string.table),
                                    order.getTable() != null ? order.getTable().getTableName() : mActivity.getResources().getString(R.string.unset_table),
                                    mActivity.getResources().getString(R.string.waiter_role),
                                    mActivity.getResources().getString(R.string.guests)}).getBytes());
                }
                else if(mActivity instanceof PayOrderActivity) {
                    PosOrgInfoManagement orgInfoManager = new PosOrgInfoManagement(mActivity.getBaseContext());
                    RestaurantInfo restaurantInfo = orgInfoManager.get(1); //Get org info to print in the receipt
                    //TODO: Get footer from iDempiere
                    bt.sendData(String.format(printer.printReceipt(),
                            new Object[] { restaurantInfo.getName(),
                                    restaurantInfo.getAddress1(),
                                    restaurantInfo.getPostalCode() + " " + restaurantInfo.getCity(),
                                    mActivity.getResources().getString(R.string.receipt),
                                    order.getOrderId(),
                                    mActivity.getResources().getString(R.string.table),
                                    order.getTable() != null ? order.getTable().getTableName() : mActivity.getResources().getString(R.string.unset_table),
                                    mActivity.getResources().getString(R.string.waiter_role),
                                    mActivity.getResources().getString(R.string.guests),
                                    mActivity.getResources().getString(R.string.total),
                                    mActivity.getResources().getString(R.string.cash),
                                    mActivity.getResources().getString(R.string.change),
                                    "We hope to see you soon again"}).getBytes());
                }
            }
        }

        return success;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if(bt != null) {
            try {
                //TODO: Fix the problem of closing the thread
                Thread.sleep(5000);
                if(bt.isConnected())
                    bt.closeBT();
            } catch(IOException e) {

            } catch(InterruptedException ex) {

            }
        }
    }
}