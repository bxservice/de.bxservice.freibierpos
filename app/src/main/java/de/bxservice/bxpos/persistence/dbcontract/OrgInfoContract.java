package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 27/04/16.
 */
public class OrgInfoContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public OrgInfoContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class OrgInfoDB implements BaseColumns {
        public static final String TABLE_NAME = "org_info";
        public static final String COLUMN_NAME_ORG_INFO_ID = "org_info_id";
        public static final String COLUMN_NAME_NAME     = "Name";
        public static final String COLUMN_NAME_ADDRESS1 = "Address1";
        public static final String COLUMN_NAME_ADDRESS2 = "Address2";
        public static final String COLUMN_NAME_CITY     = "city";
        public static final String COLUMN_NAME_POSTAL   = "postal";
        public static final String COLUMN_NAME_PHONE    = "phone";
    }
}
