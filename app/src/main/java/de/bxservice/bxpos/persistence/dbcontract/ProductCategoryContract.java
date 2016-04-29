package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 22/12/15.
 */
public class ProductCategoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ProductCategoryContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class ProductCategoryDB implements BaseColumns {
        public static final String TABLE_NAME = "pos_productcategory";
        public static final String COLUMN_NAME_PRODUCT_CATEGORY_ID = "productcategoryid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_OUTPUT_DEVICE_ID = "outputdevice_id";
    }
}
