package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 22/12/15.
 */
public class ProductPriceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ProductPriceContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class ProductPriceDB implements BaseColumns {
        public static final String TABLE_NAME = "pos_productprice";
        public static final String COLUMN_NAME_PRODUCT_PRICE_ID = "productpriceid";
        public static final String COLUMN_NAME_PRODUCT_ID = "productid";
        public static final String COLUMN_NAME_PRICE_LIST_VERSION_ID = "pricelistversionid";
        public static final String COLUMN_NAME_STD_PRICE = "stdprice";
    }
}
