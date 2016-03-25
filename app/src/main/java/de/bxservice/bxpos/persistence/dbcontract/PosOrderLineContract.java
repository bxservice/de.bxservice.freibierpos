package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by diego on 18/12/15.
 */
public class PosOrderLineContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PosOrderLineContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class POSOrderLineDB implements BaseColumns {
        public static final String TABLE_NAME                   = "pos_orderline";
        public static final String COLUMN_NAME_ORDERLINE_ID     = "orderLineid";
        public static final String COLUMN_NAME_ORDER_ID         = "orderid";
        public static final String COLUMN_NAME_CREATED_AT       = "created";
        public static final String COLUMN_NAME_UPDATED_AT       = "updated";
        public static final String COLUMN_NAME_CREATED_BY       = "createdBy";
        public static final String COLUMN_NAME_ORDERLINE_STATUS = "status";
        public static final String COLUMN_NAME_PRODUCT_ID       = "product_id";
        public static final String COLUMN_NAME_QUANTITY         = "quantity";
        public static final String COLUMN_NAME_LINENO           = "lineno";
        public static final String COLUMN_NAME_REMARK           = "remark";
        public static final String COLUMN_NAME_LINENETAMT       = "lineNetAmt";

    }
}