package de.bxservice.bxpos.logic.print;

import de.bxservice.bxpos.logic.model.pos.POSOrder;

/**
 * Created by Diego Ruiz on 21/04/16.
 */
public class CPCLPrinter extends AbstractPOSPrinter {

    public CPCLPrinter(POSOrder order) {
        super(order);
    }


    @Override
    public String print() {

        //Hello world example
        /*String message = "! 0 200 200 210 1 \r\n" +
                "TEXT 4 0 30 40 " + order.getStatus() +" \r\n" +
                "FORM \r\n\r\n" +
                "PRINT \r\n";/* +
                "PRINT \r\n";

        return message;*/

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

        /*return "! 0 200 200 210 1 \r\n" +
                "MULTILINE 47 \r\n" +
                "TEXT 4 0 10 20 \r\n" +
                "1st line of text \r\n" +
                "2nd line of text \r\n" +
                "Nth line of text \r\n" +
                "ENDMULTILINE \r\n" +
                "FORM \r\n\r\n"+
                "PRINT \r\n";*/

        /* Zebra example for a parking ticket
        return String.format("! 0 200 200 930 1\r\n" +
                "PW 384\r\n" +
                "TONE 0\r\n" +
                "SPEED 2\r\n" +
                "ON-FEED IGNORE\r\n" +
                "NO-PACE\r\n" +
                "POSTFEED 152\r\n" +
                "JOURNAL\r\n" +
                "BOX 0 0 376 797 5\r\n" +
                "T 5 1 85 27 Traffic Ticket Demo\r\n" +
                "T 5 1 75 89 %s\r\n" +
                "T 5 0 35 522 Priors:\r\n" +
                "T 5 0 35 421 Address:\r\n" +
                "T 5 0 35 342 Last Name:\r\n" +
                "T 5 0 35 257 First Name:\r\n" +
                "T 5 0 35 175 Plate #:\r\n" +
                "T 5 0 64 210 %s\r\n" +
                "T 5 0 64 290 %s\r\n" +
                "T 5 0 64 374 %s\r\n" +
                "T 5 0 64 466 %s\r\n" +
                "T 5 0 64 558 %s\r\n" +
                "B PDF-417 54 639 XD 3 YD 4 C 1 S 0\r\n" +
                "Zebra Technologies makes printing from Smart Phones easy!\r\n" +
                "ENDPDF\r\n" +
                "BOX 11 789 376 920 8\r\n\r\n " +
                "FORM \r\n\r\n"+
                "PRINT\r\n", new Object[] { "Thsi", "KRUIS", "Carlos", "Ruiz", "AAA 2356", "None" });*/

        return String.format("! 0 200 200 600 1\r\n" +
                "PW 550\r\n" +
                "TONE 0\r\n" +
                "SPEED 2\r\n" +
                "ON-FEED IGNORE\r\n" +
                "NO-PACE\r\n" +
                "POSTFEED 50\r\n" +
                "JOURNAL\r\n" +
                //"BOX 0 0 376 797 5\r\n" +
                "T 5 1 25 20 Type:\r\n" +
                "T 5 1 90 20 %s\r\n" +
                "T 5 1 25 75 Server:\r\n" +
                "T 5 1 110 75 %s\r\n" +
                "T 5 1 25 130 Guest:\r\n" +
                "T 5 1 100 130 %s\r\n" +
                "T 5 1 300 20 Table\r\n" +
                "T 5 3 300 75 %s\r\n" +
                "LINE 25 175 530 175 1\r\n" +
                "T 5 0 35 421 Address:\r\n" +
                "T 5 0 35 342 Last Name:\r\n" +
                "T 5 0 35 257 First Name:\r\n" +
                "T 5 0 35 175 Plate #:\r\n" +
                "T 5 0 64 290 %s\r\n" +
                "T 5 0 64 374 %s\r\n" +
                "T 5 0 64 466 %s\r\n" +
                "T 5 0 64 558 %s\r\n" +
                "FORM \r\n\r\n"+
                "PRINT\r\n", new Object[] { "Dine-in", "Garden", "5", "Table 1","Carlos", "Ruiz", "AAA 2356", "None" });

        //return m;
    }

    public void sendCommand(String paramString) {

    }

    @Override
    public String printKitchen() {
        return null;
    }

    @Override
    public String printBar() {
        return null;
    }

    @Override
    public String printReceipt() {
        return null;
    }
}
