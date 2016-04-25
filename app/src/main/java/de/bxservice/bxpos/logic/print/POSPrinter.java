package de.bxservice.bxpos.logic.print;

/**
 * Created by Diego Ruiz on 21/04/16.
 */
public interface POSPrinter {
    String print();
    String printKitchen();
    String printBar();
    String printReceipt();
}
