package de.bxservice.bxpos.logic.print;

import java.io.IOException;

/**
 * Created by Diego Ruiz on 29/04/16.
 */
public interface POSPrinterService {

    void sendData(byte[] out);
    boolean isConnected();
    void findDevice(String printerName);
    void openConnection() throws IOException;
    void closeConnection() throws IOException;
}
