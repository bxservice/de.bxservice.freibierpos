package de.bxservice.bxpos.logic.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.IOException;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.print.BluetoothPrinterService;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.print.CPCLPrinter;
import de.bxservice.bxpos.logic.print.POSPrinter;
import de.bxservice.bxpos.logic.print.POSPrinterFactory;

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
                /*bt.sendData(String.format(printer.printKitchen(),
                        new Object[] { mActivity.getResources().getString(R.string.order),
                                mActivity.getResources().getString(R.string.table),
                                order.getTable() != null ? order.getTable().getTableName() : mActivity.getResources().getString(R.string.unset_table),
                                mActivity.getResources().getString(R.string.waiter_role),
                                mActivity.getResources().getString(R.string.guests)}).getBytes());*/
                bt.sendData(printer.printReceipt().getBytes());
            }
        }

        return success;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if(bt != null) {
            try {
                Thread.sleep(20000);
                if(bt.isConnected())
                    bt.closeBT();
            } catch(IOException e) {

            } catch(InterruptedException ex) {

            }
        }

    }
}