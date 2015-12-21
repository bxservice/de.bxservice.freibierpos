package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.bxservice.bxpos.logic.model.POSUser;
import de.bxservice.bxpos.persistence.dbcontract.UserContract;

/**
 * contains all the methods to perform database operations
 * Created by Diego Ruiz on 15/12/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "PosDatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "freibier_pos.db";

    // Table Names
    public interface Tables {
        String TABLE_USER = UserContract.User.TABLE_NAME;

    }

    public interface UserColumns {

        //User Table
        String USER_ID = UserContract.User.COLUMN_NAME_USER_ID;
        String USERNAME = UserContract.User.COLUMN_NAME_USERNAME;
        String PASSWORD = UserContract.User.COLUMN_NAME_PASSWORD;

    }

    // Table Create Statements
    private static final String CREATE_USER_TABLE =
            "CREATE TABLE " + Tables.TABLE_USER +
                    "(" +
                    UserColumns.USER_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    UserColumns.USERNAME + " VARCHAR(64) NOT NULL UNIQUE" +
                    ", " +
                    UserColumns.PASSWORD + " VARCHAR(64) NOT NULL" +
                    ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static DatabaseHelper sSingleton;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new DatabaseHelper(context);
        }
        return sSingleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        bootstrapDB(db);
    }

    /*@Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.i(TAG, "Using schema version: " + db.getVersion());

        if (!Build.VERSION.INCREMENTAL.equals(getBuildVersion(db))) {
            Log.w(TAG, "Index needs to be rebuilt as build-version is not the same");
            // We need to drop the tables and recreate them
            reconstruct(db);
        } else {
            Log.i(TAG, "Tables are fine");
        }
    }*/

    /*@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DATABASE_VERSION) {
            Log.w(TAG, "Detected schema version '" +  oldVersion + "'. " +
                    "Index needs to be rebuilt for schema version '" + newVersion + "'.");
            // We need to drop the tables and recreate them
            reconstruct(db);
        }

    private String getBuildVersion(SQLiteDatabase db) {
        String version = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SELECT_BUILD_VERSION, null);
            if (cursor.moveToFirst()) {
                version = cursor.getString(0);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Cannot get build version from Index metadata");
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return version;
    }

        @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Detected schema version '" +  oldVersion + "'. " +
                "Index needs to be rebuilt for schema version '" + newVersion + "'.");
        // We need to drop the tables and recreate them
        reconstruct(db);
    }

    }*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        reconstruct(db);
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_USER);

    }

    private void bootstrapDB(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);

        Log.i(TAG, "Bootstrapped database");
    }

    private void reconstruct(SQLiteDatabase db) {
        dropTables(db);
        bootstrapDB(db);
    }

    /*
    * Creating a user
    */
    public long createUser (POSUser user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserColumns.USERNAME, user.getUsername());
        values.put(UserColumns.PASSWORD, user.getPassword());

        // insert row
        long todo_id = db.insert(Tables.TABLE_USER, null, values);

        return todo_id;
    }

    /*
    * get single user
    */
    public POSUser getUser(long todo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_USER + " WHERE "
                + UserColumns.USER_ID + " = " + todo_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        POSUser td = new POSUser();
        td.setId(c.getInt(c.getColumnIndex(UserColumns.USER_ID)));
        td.setUsername((c.getString(c.getColumnIndex(UserColumns.USERNAME))));
        td.setPassword(c.getString(c.getColumnIndex(UserColumns.PASSWORD)));

        return td;
    }

    /*
    * Updating a user
    */
    public int updateUser(POSUser user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserColumns.USERNAME, user.getUsername());
        values.put(UserColumns.PASSWORD, user.getPassword());

        // updating row
        return db.update(Tables.TABLE_USER, values, UserColumns.USER_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
