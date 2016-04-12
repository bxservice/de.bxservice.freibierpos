package de.bxservice.bxpos.logic;

import android.content.Context;
import android.util.Log;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.webservices.CreateOrderWebServiceAdapter;

/**
 * Class that writes in iDempiere the data
 * and call the necessary methods
 * to persist it in the database
 * Created by Diego Ruiz on 6/11/15.
 */
public class DataWriter {

    private static final String LOG_TAG = "Data Writer";

    private boolean success = false;
    private boolean connectionError = false;

    public DataWriter(final POSOrder order, final Context context) {

        Log.i(LOG_TAG, "Data Writer accessed");

        Thread createOrderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                CreateOrderWebServiceAdapter createOrderWS = new CreateOrderWebServiceAdapter(order, context);
                success = createOrderWS.isSuccess();
                connectionError = createOrderWS.isConnectionError();
            }
        });

        createOrderThread.run();

    }

    public boolean isConnectionError() {
        return connectionError;
    }

    public boolean isSuccess() {
        return success;
    }
}
