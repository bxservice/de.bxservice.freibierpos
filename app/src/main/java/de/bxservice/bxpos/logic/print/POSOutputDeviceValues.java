package de.bxservice.bxpos.logic.print;

/**
 * Printer settings defined in iDempiere
 * Values of the lists in the window
 * POS Output Device
 * Created by Diego Ruiz on 28/04/16.
 */
public interface POSOutputDeviceValues {

    /** Connection type */
    String CONNECTION_BLUETOOTH = "B";
    String CONNECTION_WLAN      = "w";

    /** Device Type */
    String DEVICE_PRINTER = "P";
    String DEVICE_DISPLAY = "D";

    /** Output Target */
    String TARGET_KITCHEN     = "K";
    String TARGET_BAR         = "B";
    String TARGET_RECEIPT     = "R";

    /** Printer Language */
    String LANGUAGE_CPCL = "C";
    String LANGUAGE_ZPL  = "Z";
    String LANGUAGE_ESC  = "E";

}
