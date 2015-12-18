package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 18/12/15.
 */
public class PosOrderContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PosOrderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class POSOrderDB implements BaseColumns {
        public static final String TABLE_NAME               = "pos_order";
        public static final String COLUMN_NAME_ORDER_ID     = "orderid";
        public static final String COLUMN_NAME_CREATED_AT   = "created";
        public static final String COLUMN_NAME_CREATED_BY   = "createdBy";
        public static final String COLUMN_NAME_ORDER_STATUS = "status";
        public static final String COLUMN_NAME_TABLE_ID     = "table_id";
        public static final String COLUMN_NAME_GUESTS       = "guestno";
        public static final String COLUMN_NAME_REMARK       = "remark";
        public static final String COLUMN_NAME_TOTALLINES   = "totallines";

    }

}
