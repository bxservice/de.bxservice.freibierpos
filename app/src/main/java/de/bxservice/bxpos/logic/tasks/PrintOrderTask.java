package de.bxservice.bxpos.logic.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.IOException;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.daomanager.PosOrgInfoManagement;
import de.bxservice.bxpos.logic.model.idempiere.OrgInfo;
import de.bxservice.bxpos.logic.print.BluetoothPrinterService;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.print.POSPrinter;
import de.bxservice.bxpos.logic.print.POSPrinterFactory;
import de.bxservice.bxpos.ui.EditOrderActivity;
import de.bxservice.bxpos.ui.PayOrderActivity;

/**
 * Created by Diego Ruiz on 20/04/16.
 */
public class PrintOrderTask extends AsyncTask<POSOrder, Void, Boolean> {

    private Activity mActivity;
    private BluetoothPrinterService bt;


    public PrintOrderTask(Activity callerActivity) {
        mActivity = callerActivity;
    }

    @Override
    protected Boolean doInBackground(POSOrder... orders) {

        boolean success = true;
        POSPrinterFactory printerFactory = new POSPrinterFactory();
        bt = new BluetoothPrinterService(mActivity);
        for(POSOrder order : orders) {
            if(bt.isConnected()) {
                POSPrinter printer = printerFactory.getPrinter("CPCL", order);
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
                    OrgInfo orgInfo = orgInfoManager.get(1); //Get org info to print in the receipt
                    //TODO: Get footer from iDempiere
                    bt.sendData(String.format(printer.printReceipt(),
                            new Object[] { orgInfo.getName(),
                                    orgInfo.getAddress1(),
                                    orgInfo.getPostalCode() + " " + orgInfo.getCity(),
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