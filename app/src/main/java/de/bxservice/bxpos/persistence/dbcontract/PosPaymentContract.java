package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 8/04/16.
 */
public class PosPaymentContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PosPaymentContract() {}

    /* Inner class that defines the table contents */
    public static abstract class POSPaymentDB implements BaseColumns {
        public static final String TABLE_NAME                 = "pos_payment";
        public static final String COLUMN_NAME_PAYMENT_ID     = "paymentid";
        public static final String COLUMN_NAME_CREATED_AT     = "created";
        public static final String COLUMN_NAME_UPDATED_AT     = "updated";
        public static final String COLUMN_NAME_CREATED_BY     = "createdBy";
        public static final String COLUMN_NAME_TENDER_TYPE    = "tenderType";
        public static final String COLUMN_NAME_ORDER_ID       = "orderid";
        public static final String COLUMN_NAME_PAYMENT_AMOUNT = "paymentAmount";

    }
}
