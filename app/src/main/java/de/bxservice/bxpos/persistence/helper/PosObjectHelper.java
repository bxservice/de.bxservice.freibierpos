package de.bxservice.bxpos.persistence.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public abstract class PosObjectHelper {

    private static final String LOG_TAG = "Object Helper";
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
    public static void closeDB(Context mContext) {
        PosDatabaseHelper.getInstance(mContext).closeDB();
    }

    /**
     * Returns the current date in format
     * yyyymmddhhmm
     * @return
     */
    protected String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; //Calendar month returns the position of the month 0 being January
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        StringBuilder date = new StringBuilder();
        date.append(year);
        if(month < 10)
            date.append("0");
        date.append(month);
        if(day < 10)
            date.append("0");
        date.append(day);
        if(hour < 10)
            date.append("0");
        date.append(hour);
        if(minutes < 10)
            date.append("0");
        date.append(minutes);

        return date.toString();
    }

}
