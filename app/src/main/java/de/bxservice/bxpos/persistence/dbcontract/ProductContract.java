package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 22/12/15.
 */
public class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ProductContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class ProductDB implements BaseColumns {
        public static final String TABLE_NAME = "pos_product";
        public static final String COLUMN_NAME_PRODUCT_ID = "productid";
        public static final String COLUMN_NAME_PRODUCT_CATEGORY_ID = "productcategoryid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_OUTPUT_DEVICE_ID = "outputdevice_id";
        public static final String COLUMN_IS_ACTIVE = "isActive";
    }
}
