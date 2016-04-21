package de.bxservice.bxpos.logic.print;

/**
 * Factory that creates the string object to be
 * printed based on the selected printer
 * Created by Diego Ruiz on 21/04/16.
 */
public class POSPrinterFactory {

    public POSPrinter getPrinter(String printerType) {
        if(printerType == null)
            return null;

        if(printerType.equalsIgnoreCase("CPCL"))
            return new CPCLPrinter();

        return null;
    }

}
