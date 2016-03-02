package de.bxservice.bxpos.logic.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import de.bxservice.bxpos.logic.DataWriter;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.ui.MainActivity;
import de.bxservice.bxpos.ui.PayOrderActivity;

/**
 * Represents an asynchronous creating task used to send
 * the order to iDempiere
 * Created by Diego Ruiz on 2/03/16.
 */
public class CreateOrderTask extends AsyncTask<POSOrder, Void, Boolean> {

    private Activity mActivity;

    public CreateOrderTask(Activity callerActivity) {
        //this.order = order;
        mActivity = callerActivity;
    }

    @Override
    protected Boolean doInBackground(POSOrder... orders) {

        DataWriter write;
        boolean success = true;

        for(POSOrder order : orders) {
            write = new DataWriter(order);
            order.payOrder(true, mActivity.getBaseContext());
            success = write.isSuccess();
        }

        return success;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (mActivity instanceof PayOrderActivity)
            ((PayOrderActivity) mActivity).postExecuteTask(success);

        if (mActivity instanceof MainActivity)
            ((MainActivity) mActivity).postExecuteTask(success);
    }
}