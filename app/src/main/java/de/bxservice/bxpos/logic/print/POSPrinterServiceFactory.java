package de.bxservice.bxpos.logic.print;

import android.app.Activity;

/**
 * Created by Diego Ruiz on 29/04/16.
 */
public class POSPrinterServiceFactory {

    public POSPrinterService getPrinterService(String target, Activity activity, String printerName) {
        if(target == null)
            return null;

        if(target.equalsIgnoreCase(POSOutputDeviceValues.CONNECTION_BLUETOOTH))
            return new BluetoothPrinterService(activity, printerName);

        return null;
    }

}
