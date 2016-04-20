package de.bxservice.bxpos.logic.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.IOException;

import de.bxservice.bxpos.logic.BluetoothPrinterService;
import de.bxservice.bxpos.logic.model.pos.POSOrder;

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

        for(POSOrder order : orders) {
            bt = new BluetoothPrinterService(mActivity);
        }

        return success;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if(bt != null) {
            try {
                Thread.sleep(4000);
                bt.closeBT();
            } catch(IOException e) {

            } catch(InterruptedException ex) {

            }
        }

    }
}