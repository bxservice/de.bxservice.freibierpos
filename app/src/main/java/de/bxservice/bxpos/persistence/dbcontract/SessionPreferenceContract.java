package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 10/19/16.
 */

public class SessionPreferenceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public SessionPreferenceContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class SessionPreferenceDB implements BaseColumns {
        public static final String TABLE_NAME = "pos_sessionPreference";
        public static final String COLUMN_NAME_SESSION_PREF_ID  = "sessionPreferenceid";
        public static final String COLUMN_NAME_PREF_NAME        = "name";
        public static final String COLUMN_NAME_PREF_VALUE       = "value";
    }
}
