package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

public class TaxContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TaxContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class TaxDB implements BaseColumns {
        public static final String TABLE_NAME = "tax";
        public static final String COLUMN_NAME_TAX_CATEGORY_ID = "tax_category_id";
        public static final String COLUMN_NAME_NAME            = "name";
        public static final String COLUMN_NAME_TAX_ID          = "tax_id";
        public static final String COLUMN_NAME_RATE            = "rate";
        public static final String COLUMN_NAME_POSTAL          = "postal";
    }

}
