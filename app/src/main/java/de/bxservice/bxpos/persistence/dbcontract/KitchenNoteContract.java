package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 11/05/16.
 */
public class KitchenNoteContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public KitchenNoteContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class KitchenNoteDB implements BaseColumns {
        public static final String TABLE_NAME = "kitchenNote";
        public static final String COLUMN_NAME_KITCHEN_NOTE_ID = "kitchenNote_id";
        public static final String COLUMN_NAME_NOTE            = "note";
        public static final String COLUMN_NAME_CREATED_AT      = "created";
        public static final String COLUMN_NAME_CREATED_BY      = "createdBy";

    }
}
