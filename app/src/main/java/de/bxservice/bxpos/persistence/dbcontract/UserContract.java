package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 15/12/15.
 */
public final class UserContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public UserContract() {}

    /* Inner class that defines the table contents */
    public static abstract class User implements BaseColumns {
        public static final String TABLE_NAME = "pos_user";
        public static final String COLUMN_NAME_USER_ID = "userid";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_PASSWORD = "password";

    }
}
