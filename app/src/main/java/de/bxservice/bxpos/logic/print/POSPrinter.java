package de.bxservice.bxpos.logic.print;

/**
 * Created by Diego Ruiz on 21/04/16.
 */
public interface POSPrinter {

    String KITCHEN_RECEIPT = POSOutputDeviceValues.TARGET_KITCHEN;
    String BAR_RECEIPT     = POSOutputDeviceValues.TARGET_BAR;

    String printTicket(String target);
    String printReceipt();
}
