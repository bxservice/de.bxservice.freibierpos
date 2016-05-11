package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;
import de.bxservice.bxpos.persistence.dbcontract.KitchenNoteContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 11/05/16.
 */
public class PosKitchenNoteHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Kitchen note Helper";

    public PosKitchenNoteHelper(Context mContext) {
        super(mContext);
    }

    public long createKitchenNote(String note) {
        SQLiteDatabase database = getWritableDatabase();

        PosUserHelper userHelper = new PosUserHelper(mContext);
        int userId = userHelper.getUserId(WebServiceRequestData.getInstance().getUsername());

        ContentValues values = new ContentValues();
        values.put(KitchenNoteContract.KitchenNoteDB.COLUMN_NAME_CREATED_AT, Long.parseLong(getCurrentDate()));
        values.put(KitchenNoteContract.KitchenNoteDB.COLUMN_NAME_CREATED_BY, userId);
        values.put(KitchenNoteContract.KitchenNoteDB.COLUMN_NAME_NOTE, note);

        return database.insert(Tables.TABLE_KITCHEN_NOTE, null, values);
    }

    /*
    * note exist
    */
    public boolean noteExist(String note) {

        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT " + KitchenNoteContract.KitchenNoteDB.COLUMN_NAME_KITCHEN_NOTE_ID +
                " FROM " + Tables.TABLE_KITCHEN_NOTE +
                " WHERE LOWER(" + KitchenNoteContract.KitchenNoteDB.COLUMN_NAME_NOTE + ") =?";

        Log.i(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] {note.toLowerCase()});

        if (c != null && c.getCount() > 0) {
            c.close();
            return true;
        }
        else
            return false;
    }

    /**
    * Get all kitchen notes
    * @return
    */
    public ArrayList<String> getKitchenNotes() {
        ArrayList<String> notes = new ArrayList<>();

        String selectQuery = "SELECT " + KitchenNoteContract.KitchenNoteDB.COLUMN_NAME_NOTE +
                " FROM " + Tables.TABLE_KITCHEN_NOTE;


        Log.d(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null /*new String[] {POSOrder.COMPLETE_STATUS, POSOrder.VOID_STATUS}*/);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                notes.add(c.getString(c.getColumnIndex(KitchenNoteContract.KitchenNoteDB.COLUMN_NAME_NOTE)));
            } while (c.moveToNext());
            c.close();
        }

        return notes;
    }

}
