package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 21/12/15.
 */
public class GroupTableContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public GroupTableContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class GroupTableDB implements BaseColumns {
        public static final String TABLE_NAME = "pos_tablegroup";
        public static final String COLUMN_NAME_TABLE_GROUP_ID = "tablegroupid";
        public static final String COLUMN_NAME_GROUP_TABLE_NAME = "name";
        public static final String COLUMN_NAME_VALUE = "value";
    }
}
