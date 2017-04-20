package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

public class TaxCategoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TaxCategoryContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class TaxCategoryDB implements BaseColumns {
        public static final String TABLE_NAME = "tax_category";
        public static final String COLUMN_NAME_TAX_CATEGORY_ID = "tax_category_id";
        public static final String COLUMN_NAME_NAME            = "name";
    }
}
