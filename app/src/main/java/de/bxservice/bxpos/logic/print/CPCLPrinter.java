package de.bxservice.bxpos.logic.print;

/**
 * Created by Diego Ruiz on 21/04/16.
 */
public class CPCLPrinter implements POSPrinter {



    public String print() {
        String message = "! 0 200 200 210 1 \r\n" +
                "TEXT 4 0 30 40 Hello World \r\n" +
                "FORM \r\n\r\n" +
                "PRINT \r\n";

        return message;
    }
}
