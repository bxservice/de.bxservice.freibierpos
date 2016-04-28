package de.bxservice.bxpos.logic.print;

import de.bxservice.bxpos.logic.model.pos.POSOrder;

/**
 * Factory that creates the string object to be
 * printed based on the selected printer
 * Created by Diego Ruiz on 21/04/16.
 */
public class POSPrinterFactory {

    public POSPrinter getPrinter(String printerType, POSOrder order) {
        if(printerType == null)
            return null;

        if(printerType.equalsIgnoreCase(POSOutputDeviceValues.LANGUAGE_CPCL))
            return new CPCLPrinter(order);

        return null;
    }

}
