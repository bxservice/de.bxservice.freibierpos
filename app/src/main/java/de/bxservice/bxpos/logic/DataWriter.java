package de.bxservice.bxpos.logic;

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

    public DataWriter(final POSOrder order) {

        Log.i(LOG_TAG, "Data Writer accessed");

        Thread createOrderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                CreateOrderWebServiceAdapter createOrderWS = new CreateOrderWebServiceAdapter(order);
                success = createOrderWS.isSuccess();

            }
        });

        createOrderThread.run();

    }

    public boolean isSuccess() {
        return success;
    }
}
