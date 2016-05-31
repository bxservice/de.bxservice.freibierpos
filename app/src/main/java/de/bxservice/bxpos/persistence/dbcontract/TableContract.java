package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 21/12/15.
 */
public class TableContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TableContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class TableDB implements BaseColumns {
        public static final String TABLE_NAME = "pos_table";
        public static final String COLUMN_NAME_TABLE_ID       = "tableid";
        public static final String COLUMN_NAME_GROUP_TABLE_ID = "grouptableid";
        public static final String COLUMN_NAME_TABLE_STATUS   = "status";
        public static final String COLUMN_NAME_TABLE_NAME     = "name";
        public static final String COLUMN_NAME_VALUE          = "value";
        public static final String COLUMN_NAME_UPDATED_AT     = "updatedAt";
        public static final String COLUMN_NAME_SERVER_NAME    = "serverName";
    }
}
