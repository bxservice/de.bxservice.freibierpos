package de.bxservice.bxpos.logic.print;

import de.bxservice.bxpos.logic.model.pos.POSOrder;

/**
 * Created by Diego Ruiz on 21/04/16.
 */
public class CPCLPrinter extends AbstractPOSPrinter {

    public CPCLPrinter(POSOrder order) {
        super(order);
    }

    public String print() {

        //Hello world example
        String message = "! 0 200 200 210 1 \r\n" +
                "TEXT 4 0 30 40 " + order.getStatus() +" \r\n" +
                "FORM \r\n\r\n" +
                "PRINT \r\n";

        //Alignment example
        /*message = "! 0 200 200 210 1 \r\n"+
                "CENTER 383 \r\n"+
                "TEXT 4 0 0 75 C \r\n"+
                "LEFT \r\n"+
                "CONCAT 4 0 \r\n" +
                "4 2 5 Type: \r\n" +
                "4 3 0 Dine \r\n" +
                "4 2 5 in \r\n" +
                "ENDCONCAT \r\n" +
                //"TEXT 4 0 0 75 L \r\n"+
                "RIGHT 383 \r\n" +
                "TEXT 4 0 0 75 R \r\n"+
                "FORM \r\n\r\n"+
                "PRINT\r\n";*/

        /*String message = "! 0 200 200 210 1 \r\n" +
                "ML 47 \r\n" +
                "TEXT 4 0 10 20 \r\n" +
                "1st line of text \r\n" +
                "2nd line of text \r\n" +
                "Nth line of text \r\n" +
                "ENDML \r\n" +
                "FORM \r\n" +
                "PRINT \r\n";**/

        return message;
    }
}
