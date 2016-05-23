package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 5/23/16.
 */
public class DeviceTokenContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DeviceTokenContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class DeviceTokenDB implements BaseColumns {
        public static final String TABLE_NAME = "DeviceToken";
        public static final String COLUMN_NAME_DEVICE_TOKEN_ID    = "DeviceToken_id";
        public static final String COLUMN_NAME_DEVICE_TOKEN_VALUE = "DeviceTokenValue";
        public static final String COLUMN_NAME_CREATED_AT         = "createdAt";
        public static final String COLUMN_NAME_SYNCHRONIZED       = "isSynchronized";
    }
}
