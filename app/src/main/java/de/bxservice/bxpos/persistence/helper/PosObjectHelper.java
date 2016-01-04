package de.bxservice.bxpos.persistence.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public abstract class PosObjectHelper {

    static final String LOG_TAG = "Object Helper";
    Context mContext;

    PosObjectHelper (Context mContext) {
        this.mContext = mContext;
    }

    protected SQLiteDatabase getReadableDatabase() {
        return PosDatabaseHelper.getInstance(mContext).getReadableDatabase();
    }
    protected SQLiteDatabase getWritableDatabase() {
        try {
            return PosDatabaseHelper.getInstance(mContext).getWritableDatabase();
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, "Cannot open writable database", e);
            return null;
        }
    }

    // closing database
    public void closeDB() {
        /*PosDatabaseHelper.getInstance(mContext).closeDB();*/
    }

}
