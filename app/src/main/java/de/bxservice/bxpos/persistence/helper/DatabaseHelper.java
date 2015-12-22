package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.persistence.dbcontract.GroupTableContract;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderContract;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderLineContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductCategoryContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductPriceContract;
import de.bxservice.bxpos.persistence.dbcontract.TableContract;
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
        //Access tables
        String TABLE_USER          = UserContract.User.TABLE_NAME;

        //Physical space tables
        String TABLE_TABLE         = TableContract.TableDB.TABLE_NAME;
        String TABLE_TABLE_GROUP   = GroupTableContract.GroupTableDB.TABLE_NAME;

        //Order management tables
        String TABLE_POSORDER      = PosOrderContract.POSOrderDB.TABLE_NAME;
        String TABLE_POSORDER_LINE = PosOrderLineContract.POSOrderLineDB.TABLE_NAME;

        //Product management tables
        String TABLE_PRODUCT = ProductContract.ProductDB.TABLE_NAME;
        String TABLE_PRODUCT_CATEGORY = ProductCategoryContract.ProductCategoryDB.TABLE_NAME;
        String TABLE_PRODUCT_PRICE = ProductPriceContract.ProductPriceDB.TABLE_NAME;

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
                    UserContract.User.COLUMN_NAME_USER_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    UserContract.User.COLUMN_NAME_USERNAME + " VARCHAR(64) NOT NULL UNIQUE" +
                    ", " +
                    UserContract.User.COLUMN_NAME_PASSWORD + " VARCHAR(64) NOT NULL" +
                    ")";

    private static final String CREATE_TABLE_TABLE =
            "CREATE TABLE " + Tables.TABLE_TABLE +
                    "(" +
                    TableContract.TableDB.COLUMN_NAME_TABLE_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    TableContract.TableDB.COLUMN_NAME_TABLE_NAME + " VARCHAR(64) NOT NULL" +
                    ", " +
                    TableContract.TableDB.COLUMN_NAME_TABLE_STATUS + " VARCHAR(64)" +
                    ", " +
                    TableContract.TableDB.COLUMN_NAME_VALUE + " VARCHAR(64)" +
                    ", " +
                    TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID + " INTEGER" +
                    ")";

    private static final String CREATE_GROUPTABLE_TABLE =
            "CREATE TABLE " + Tables.TABLE_TABLE_GROUP +
                    "(" +
                    GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    GroupTableContract.GroupTableDB.COLUMN_NAME_GROUP_TABLE_NAME + " VARCHAR(64) NOT NULL" +
                    ", " +
                    GroupTableContract.GroupTableDB.COLUMN_NAME_VALUE + " VARCHAR(64)" +
                    ")";

    private static final String CREATE_POSORDER_TABLE =
            "CREATE TABLE " + Tables.TABLE_POSORDER +
                    "(" +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS + " VARCHAR(64) NOT NULL" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK + " VARCHAR(64)" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_BY + " VARCHAR(64)" + //TODO:FK to users
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_AT + " TEXT" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID + " INTEGER" + //TODO: FK to table
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS + " INTEGER" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES + " NUMERIC" +
                    ")";

    private static final String CREATE_POSORDER_LINE_TABLE =
            "CREATE TABLE " + Tables.TABLE_POSORDER_LINE +
                    "(" +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_CREATED_BY + " VARCHAR(64) NOT NULL" + //TODO: FK
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK + " VARCHAR(64)" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS + " VARCHAR(64)" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_CREATED_AT + " TEXT" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID + " INTEGER" + //TODO: FK to table
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID + " INTEGER" + //TODO: FK To table
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY + " NUMERIC" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENO + " INTEGER" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT + " NUMERIC" +
                    ")";

    private static final String CREATE_PRODUCT_TABLE =
            "CREATE TABLE " + Tables.TABLE_PRODUCT +
                    "(" +
                    ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    ProductContract.ProductDB.COLUMN_NAME_NAME + " VARCHAR(64) NOT NULL" +
                    ", " +
                    ProductContract.ProductDB.COLUMN_NAME_VALUE + " VARCHAR(64)" +
                    ", " +
                    ProductContract.ProductDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + " INTEGER" + //TODO: FK to table
                    ")";

    private static final String CREATE_PRODUCT_CATEGORY_TABLE =
            "CREATE TABLE " + Tables.TABLE_PRODUCT_CATEGORY +
                    "(" +
                    ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_NAME + " VARCHAR(64) NOT NULL" +
                    ")";

    private static final String CREATE_PRODUCT_PRICE_TABLE =
            "CREATE TABLE " + Tables.TABLE_PRODUCT_PRICE +
                    "(" +
                    ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_PRICE_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_ID + " INTEGER NOT NULL" + //TODO: FK
                    ", " +
                    ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRICE_LIST_VERSION_ID + " INTEGER NOT NULL" +
                    ", " +
                    ProductPriceContract.ProductPriceDB.COLUMN_NAME_STD_PRICE + " NUMERIC" +
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
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_TABLE_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_POSORDER);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_POSORDER_LINE);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_PRODUCT_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_PRODUCT_PRICE);

    }

    private void bootstrapDB(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_TABLE_TABLE);
        db.execSQL(CREATE_GROUPTABLE_TABLE);
        db.execSQL(CREATE_POSORDER_TABLE);
        db.execSQL(CREATE_POSORDER_LINE_TABLE);
        db.execSQL(CREATE_PRODUCT_CATEGORY_TABLE);
        db.execSQL(CREATE_PRODUCT_PRICE_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);

        Log.i(TAG, "Bootstrapped database");
    }

    private void reconstruct(SQLiteDatabase db) {
        dropTables(db);
        bootstrapDB(db);
    }

    /*
    * Creating a user
    */
    public long createUser (PosUser user) {
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
    public PosUser getUser(long todo_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_USER + " WHERE "
                + UserColumns.USER_ID + " = " + todo_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        PosUser td = new PosUser();
        td.setId(c.getInt(c.getColumnIndex(UserColumns.USER_ID)));
        td.setUsername((c.getString(c.getColumnIndex(UserColumns.USERNAME))));
        td.setPassword(c.getString(c.getColumnIndex(UserColumns.PASSWORD)));

        return td;
    }

    /*
    * Updating a user
    */
    public int updateUser(PosUser user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserColumns.USERNAME, user.getUsername());
        values.put(UserColumns.PASSWORD, user.getPassword());

        // updating row
        return db.update(Tables.TABLE_USER, values, UserColumns.USER_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    /*
    * Creating a table
    */
    public long createTable (Table table) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_ID, table.getTableID());
        values.put(TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID, table.getBelongingGroup().getTableGroupID());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_NAME, table.getTableName());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_STATUS, table.getStatus());
        values.put(TableContract.TableDB.COLUMN_NAME_VALUE, table.getValue());

        // insert row
        long tableId = db.insert(Tables.TABLE_TABLE, null, values);

        return tableId;
    }

    /*
    * get single table
    */
    public Table getTable(long table_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TABLE + " WHERE "
                + TableContract.TableDB.COLUMN_NAME_TABLE_ID + " = " + table_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Table table = new Table();
        table.setTableID(c.getInt(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_ID)));
        table.setTableGroup(c.getInt(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID)));
        table.setStatus((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_STATUS))));
        table.setTableName(c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_NAME)));
        table.setValue((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_VALUE))));

        return table;
    }

    /*
    * Updating a table
    */
    public int updateTable (Table table) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID, table.getBelongingGroup().getTableGroupID());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_NAME, table.getTableName());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_STATUS, table.getStatus());
        values.put(TableContract.TableDB.COLUMN_NAME_VALUE, table.getValue());

        // updating row
        return db.update(Tables.TABLE_USER, values, UserColumns.USER_ID + " = ?",
                new String[] { String.valueOf(table.getTableID()) });
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
