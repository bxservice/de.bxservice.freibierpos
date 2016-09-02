package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 1/03/16.
 */
public class DefaultPosDataContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DefaultPosDataContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class DefaultDataDB implements BaseColumns {
        public static final String TABLE_NAME = "pos_defaultdata";
        public static final String COLUMN_NAME_DEFAULT_DATA_ID = "defaultdataid";
        public static final String COLUMN_NAME_BPARTNER = "defaultBPartner";
        public static final String COLUMN_NAME_PRICE_LIST = "defaultPriceList";
        public static final String COLUMN_NAME_CURRENCY = "defaultCurrency";
        public static final String COLUMN_NAME_WAREHOUSE = "defaultWarehouse";
        public static final String COLUMN_NAME_DISCOUNT_ID = "discountID";
        public static final String COLUMN_NAME_SURCHARGE_ID = "surchargeID";
        public static final String COLUMN_NAME_COMBINE_ITEMS = "combineItems";
        public static final String COLUMN_NAME_PRINT_AFTER_SEND = "printAfter";
        public static final String COLUMN_NAME_IS_TAX_INCLUDED = "IsTaxIncluded";
        public static final String COLUMN_NAME_PIN = "pin";

    }

}
