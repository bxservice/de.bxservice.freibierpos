package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.persistence.dbcontract.UserContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosUserHelper extends PosObjectHelper {

    static final String LOG_TAG = "User Helper";

    public PosUserHelper(Context mContext) {
        super(mContext);
    }

    public long createUser(PosUser user) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserContract.User.COLUMN_NAME_USERNAME, user.getUsername());
        values.put(UserContract.User.COLUMN_NAME_PASSWORD, user.getPassword());

        // insert row
        long userId = database.insert(Tables.TABLE_USER, null, values);

        return userId;
    }

    /*
    * get single user
    */
    public PosUser getUser(long todo_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_USER + " WHERE "
                + UserContract.User.COLUMN_NAME_USER_ID + " = " + todo_id;

        Log.e(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        PosUser td = new PosUser();
        td.setId(c.getInt(c.getColumnIndex(UserContract.User.COLUMN_NAME_USER_ID)));
        td.setUsername((c.getString(c.getColumnIndex(UserContract.User.COLUMN_NAME_USERNAME))));
        td.setPassword(c.getString(c.getColumnIndex(UserContract.User.COLUMN_NAME_PASSWORD)));

        return td;
    }

    /*
    * Updating a user
    */
    public int updateUser(PosUser user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserContract.User.COLUMN_NAME_USERNAME, user.getUsername());
        values.put(UserContract.User.COLUMN_NAME_PASSWORD, user.getPassword());

        // updating row
        return db.update(Tables.TABLE_USER, values, UserContract.User.COLUMN_NAME_USER_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

}
