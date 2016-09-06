package de.bxservice.bxpos.logic;

import android.content.Context;
import android.util.Log;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.webservices.CreateDeviceTokenWebServiceAdapter;
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
    private String  errorMessage = "";

    public void writeOrder(final POSOrder order, final Context context) {
        Log.i(LOG_TAG, "Write order");

        Thread createOrderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                CreateOrderWebServiceAdapter createOrderWS = new CreateOrderWebServiceAdapter(order, context);
                success = createOrderWS.isSuccess();
                connectionError = createOrderWS.isConnectionError();
                errorMessage = createOrderWS.getErrorMessage();
            }
        });

        createOrderThread.run();
    }

    public void writeDeviceToken(final String token) {
        Log.i(LOG_TAG, "Write token");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                CreateDeviceTokenWebServiceAdapter createTokenWS = new CreateDeviceTokenWebServiceAdapter(token);
                success = createTokenWS.isSuccess();
                connectionError = createTokenWS.isConnectionError();
            }
        });

        thread.run();
    }

    public boolean isConnectionError() {
        return connectionError;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
