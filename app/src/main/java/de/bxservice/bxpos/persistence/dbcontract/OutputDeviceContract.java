package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 28/04/16.
 */
public class OutputDeviceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public OutputDeviceContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class OutputDeviceDB implements BaseColumns {
        public static final String TABLE_NAME = "output_device";
        public static final String COLUMN_NAME_OUTPUT_DEVICE_ID = "outputdevice_id";
        public static final String COLUMN_NAME_PRINTER_NAME     = "printerName";
        public static final String COLUMN_NAME_PRINTER_LANGUAGE = "printerLanguage";
        public static final String COLUMN_NAME_TARGET           = "target";
        public static final String COLUMN_NAME_DEVICE_TYPE      = "deviceType";
        public static final String COLUMN_NAME_CONNECTION       = "connection";
        public static final String COLUMN_NAME_PAGE_WIDTH       = "pageWidth";
    }
}
