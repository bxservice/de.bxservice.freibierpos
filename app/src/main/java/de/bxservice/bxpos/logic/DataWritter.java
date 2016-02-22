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
public class DataWritter {

    static final String LOG_TAG = "Data Writter";

    private boolean success = false;
    private Context mContext;


    public DataWritter(Context ctx, final POSOrder order) {

        Log.i(LOG_TAG, "Data Writter accessed");

        mContext = ctx;

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
