package de.bxservice.bxpos.logic.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import de.bxservice.bxpos.logic.DataWriter;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.ui.PayOrderActivity;

/**
 * Represents an asynchronous creating task used to send
 * the order to iDempiere
 * Created by Diego Ruiz on 2/03/16.
 */
public class CreateOrderTask extends AsyncTask<Void, Void, Boolean> {


    private POSOrder order;
    private Activity mActivity;

    public CreateOrderTask(POSOrder order, Activity callerActivity) {
        this.order = order;
        mActivity = callerActivity;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        DataWriter write = new DataWriter(mActivity.getBaseContext(), order);
        return write.isSuccess();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        order.payOrder(true, mActivity.getBaseContext());

        if (mActivity instanceof PayOrderActivity)
            ((PayOrderActivity) mActivity).postExecuteTask(success);
    }
}