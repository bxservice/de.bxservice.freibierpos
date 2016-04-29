package de.bxservice.bxpos.logic.print;

import android.app.Activity;

import java.io.IOException;

/**
 * Created by Diego Ruiz on 29/04/16.
 */
public abstract class AbstractPOSPrinterService implements POSPrinterService {

    protected Activity activity;

    public AbstractPOSPrinterService(Activity mActivity, String printerName) {
        activity = mActivity;
        try {
            findDevice(printerName);
            openConnection();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
