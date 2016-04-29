package de.bxservice.bxpos.persistence.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import de.bxservice.bxpos.persistence.dbcontract.DefaultPosDataContract;
import de.bxservice.bxpos.persistence.dbcontract.GroupTableContract;
import de.bxservice.bxpos.persistence.dbcontract.OrgInfoContract;
import de.bxservice.bxpos.persistence.dbcontract.OutputDeviceContract;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderContract;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderLineContract;
import de.bxservice.bxpos.persistence.dbcontract.PosPaymentContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductCategoryContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductPriceContract;
import de.bxservice.bxpos.persistence.dbcontract.TableContract;
import de.bxservice.bxpos.persistence.dbcontract.UserContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * contains all the methods to perform database operations
 * Created by Diego Ruiz on 15/12/15.
 */
public class PosDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "PosDatabaseHelper";

    // Database Version - change this value when you change the database model
    private static final int DATABASE_VERSION = 27;
    private static final String DATABASE_NAME = "freibier_pos.db";

    public interface MetaColumns {
        String BUILD = "build";
    }

    // Table Create Statements

    private static final String CREATE_META_TABLE =
            "CREATE TABLE " + Tables.TABLE_META_INDEX +
                    "(" +
                    MetaColumns.BUILD + " VARCHAR(32) NOT NULL" +
                    ")";

    private static final String CREATE_USER_TABLE =
            "CREATE TABLE " + Tables.TABLE_USER +
                    "(" +
                    UserContract.User.COLUMN_NAME_USER_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    UserContract.User.COLUMN_NAME_USERNAME + " VARCHAR(64) NOT NULL UNIQUE" +
                    ", " +
                    UserContract.User.COLUMN_NAME_PASSWORD + " VARCHAR(256) NOT NULL" +
                    ", " +
                    UserContract.User.COLUMN_NAME_SALT + " VARCHAR(256) " +
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
                    TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID + " INTEGER REFERENCES "
                        + Tables.TABLE_TABLE_GROUP + "(" + GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID + ") ON DELETE CASCADE" +  //FK to the group
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
                    PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_BY + " INTEGER REFERENCES "
                        + Tables.TABLE_USER + "(" + UserContract.User.COLUMN_NAME_USER_ID + ") ON DELETE CASCADE" +  //FK to user
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_AT + " INTEGER" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_UPDATED_AT + " INTEGER" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID + " INTEGER REFERENCES "
                        + Tables.TABLE_TABLE + "(" + TableContract.TableDB.COLUMN_NAME_TABLE_ID + ") ON DELETE CASCADE" +  //FK to the table
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS + " INTEGER" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES + " NUMERIC" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_SURCHARGE + " NUMERIC" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_DISCOUNT + " NUMERIC" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_PAYMENT_RULE + " VARCHAR(2)" +
                    ", " +
                    PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED + " INTEGER" +
                    ")";

    private static final String CREATE_POSORDER_LINE_TABLE =
            "CREATE TABLE " + Tables.TABLE_POSORDER_LINE +
                    "(" +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_CREATED_BY + " INTEGER REFERENCES "
                    + Tables.TABLE_USER + "(" + UserContract.User.COLUMN_NAME_USER_ID + ") ON DELETE CASCADE" +  //FK to user
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK + " VARCHAR(64)" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS + " VARCHAR(64)" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_CREATED_AT + " INTEGER" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_UPDATED_AT + " INTEGER" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID + " INTEGER REFERENCES "
                        + Tables.TABLE_POSORDER + "(" + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID + ") ON DELETE CASCADE" +  //FK to the order
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID + " INTEGER REFERENCES "
                    + Tables.TABLE_PRODUCT + "(" + ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID + ") " +  //FK to the product
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY + " NUMERIC" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENO + " INTEGER" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT + " NUMERIC" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_COMPLIMENTARY + " INTEGER" +
                    ", " +
                    PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ISPRINTED + " INTEGER" +
                    ")";

    private static final String CREATE_POSPAYMENT_TABLE =
            "CREATE TABLE " + Tables.TABLE_POSPAYMENT +
                    "(" +
                    PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    PosPaymentContract.POSPaymentDB.COLUMN_NAME_CREATED_BY + " INTEGER REFERENCES "
                    + Tables.TABLE_USER + "(" + UserContract.User.COLUMN_NAME_USER_ID + ") ON DELETE CASCADE" +  //FK to user
                    ", " +
                    PosPaymentContract.POSPaymentDB.COLUMN_NAME_TENDER_TYPE + " INTEGER" +
                    ", " +
                    PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_AMOUNT + " NUMERIC" +
                    ", " +
                    PosPaymentContract.POSPaymentDB.COLUMN_NAME_CREATED_AT + " INTEGER" +
                    ", " +
                    PosPaymentContract.POSPaymentDB.COLUMN_NAME_UPDATED_AT + " INTEGER" +
                    ", " +
                    PosPaymentContract.POSPaymentDB.COLUMN_NAME_ORDER_ID + " INTEGER REFERENCES "
                    + Tables.TABLE_POSORDER + "(" + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID + ") ON DELETE CASCADE" +  //FK to the order
                    ")";

    private static final String CREATE_PRODUCT_TABLE =
            "CREATE TABLE " + Tables.TABLE_PRODUCT +
                    "(" +
                    ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    ProductContract.ProductDB.COLUMN_NAME_NAME + " VARCHAR(64) NOT NULL" +
                    ", " +
                    ProductContract.ProductDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + " INTEGER REFERENCES "
                        + Tables.TABLE_PRODUCT_CATEGORY + "(" + ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + ") ON DELETE CASCADE" +  //FK to the product category
                    ", " +
                    ProductContract.ProductDB.COLUMN_OUTPUT_DEVICE_ID + " INTEGER REFERENCES "
                    + Tables.TABLE_OUTPUT_DEVICE + "(" + OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_OUTPUT_DEVICE_ID + ") " +  //FK to the output device
                    ")";

    private static final String CREATE_PRODUCT_CATEGORY_TABLE =
            "CREATE TABLE " + Tables.TABLE_PRODUCT_CATEGORY +
                    "(" +
                    ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_NAME + " VARCHAR(64) NOT NULL" +
                    ", " +
                    ProductCategoryContract.ProductCategoryDB.COLUMN_OUTPUT_DEVICE_ID + " INTEGER REFERENCES "
                    + Tables.TABLE_OUTPUT_DEVICE + "(" + OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_OUTPUT_DEVICE_ID + ") " +  //FK to the output device
                    ")";

    private static final String CREATE_PRODUCT_PRICE_TABLE =
            "CREATE TABLE " + Tables.TABLE_PRODUCT_PRICE +
                    "(" +
                    ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_PRICE_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_ID + " INTEGER NOT NULL REFERENCES "
                    + Tables.TABLE_PRODUCT + "(" + ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID + ") ON DELETE CASCADE" +  //FK to the product
                    ", " +
                    ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRICE_LIST_VERSION_ID + " INTEGER NOT NULL" +
                    ", " +
                    ProductPriceContract.ProductPriceDB.COLUMN_NAME_STD_PRICE + " NUMERIC" +
                    ")";

    private static final String CREATE_DEFAULT_DATA_TABLE =
            "CREATE TABLE " + Tables.TABLE_DEFAULT_POS_DATA +
                    "(" +
                    DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_DEFAULT_DATA_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_BPARTNER + " INTEGER NOT NULL" +
                    ", " +
                    DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_PRICE_LIST + " INTEGER NOT NULL" +
                    ", " +
                    DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_CURRENCY + " INTEGER NOT NULL" +
                    ", " +
                    DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_WAREHOUSE + " INTEGER NOT NULL" +
                    ")";

    private static final String CREATE_ORG_INFO_TABLE =
            "CREATE TABLE " + Tables.TABLE_ORG_INFO +
                    "(" +
                    OrgInfoContract.OrgInfoDB.COLUMN_NAME_ORG_INFO_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    OrgInfoContract.OrgInfoDB.COLUMN_NAME_NAME + " VARCHAR(64) NOT NULL" +
                    ", " +
                    OrgInfoContract.OrgInfoDB.COLUMN_NAME_ADDRESS1 + " VARCHAR(64)" +
                    ", " +
                    OrgInfoContract.OrgInfoDB.COLUMN_NAME_ADDRESS2 + " VARCHAR(64)" +
                    ", " +
                    OrgInfoContract.OrgInfoDB.COLUMN_NAME_CITY + " VARCHAR(64)" +
                    ", " +
                    OrgInfoContract.OrgInfoDB.COLUMN_NAME_PHONE + " VARCHAR(64)" +
                    ", " +
                    OrgInfoContract.OrgInfoDB.COLUMN_NAME_POSTAL + " VARCHAR(64)" +
                    ")";

    private static final String CREATE_OUTPUT_DEVICE_TABLE =
            "CREATE TABLE " + Tables.TABLE_OUTPUT_DEVICE +
                    "(" +
                    OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_OUTPUT_DEVICE_ID + " INTEGER PRIMARY KEY" +
                    ", " +
                    OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_TARGET + " VARCHAR(3) NOT NULL" +
                    ", " +
                    OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_DEVICE_TYPE + " VARCHAR(3) NOT NULL" +
                    ", " +
                    OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_PRINTER_NAME + " VARCHAR(64)" +
                    ", " +
                    OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_PRINTER_LANGUAGE + " VARCHAR(3)" +
                    ", " +
                    OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_CONNECTION + " VARCHAR(3)" +
                    ", " +
                    OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_PAGE_WIDTH + " INTEGER" +
                    ")";

    //Control database version
    private static final String INSERT_BUILD_VERSION =
            "INSERT INTO " + Tables.TABLE_META_INDEX +
                    " VALUES ('" + Build.VERSION.INCREMENTAL + "');";

    private static final String SELECT_BUILD_VERSION =
            "SELECT " + MetaColumns.BUILD + " FROM " + Tables.TABLE_META_INDEX + " LIMIT 1;";


    private PosDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static PosDatabaseHelper sSingleton;

    public static synchronized PosDatabaseHelper getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new PosDatabaseHelper(context);
        }
        return sSingleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        bootstrapDB(db);
    }

    /**
     * When open the database and the version changed, recreate it
     * @param db
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.setForeignKeyConstraintsEnabled(true);
        Log.i(TAG, "Using schema version: " + db.getVersion());

        if (!Build.VERSION.INCREMENTAL.equals(getBuildVersion(db))) {
            Log.w(TAG, "Index needs to be rebuilt as build-version is not the same");
            // We need to drop the tables and recreate them
            reconstruct(db);
        } else {
            Log.i(TAG, "Tables are fine");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DATABASE_VERSION) {
            Log.w(TAG, "Detected schema version '" + oldVersion + "'. " +
                    "Index needs to be rebuilt for schema version '" + newVersion + "'.");
            // We need to drop the tables and recreate them
            reconstruct(db);
        }
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

    private void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_META_INDEX);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_POSORDER);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_POSORDER_LINE);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_POSPAYMENT);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_PRODUCT_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_PRODUCT_PRICE);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_TABLE_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_DEFAULT_POS_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_ORG_INFO);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_OUTPUT_DEVICE);

    }

    private void bootstrapDB(SQLiteDatabase db) {
        db.execSQL(CREATE_META_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_GROUPTABLE_TABLE);
        db.execSQL(CREATE_TABLE_TABLE);
        db.execSQL(CREATE_OUTPUT_DEVICE_TABLE);
        db.execSQL(CREATE_PRODUCT_CATEGORY_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_PRODUCT_PRICE_TABLE);
        db.execSQL(CREATE_POSORDER_TABLE);
        db.execSQL(CREATE_POSORDER_LINE_TABLE);
        db.execSQL(CREATE_POSPAYMENT_TABLE);
        db.execSQL(CREATE_DEFAULT_DATA_TABLE);
        db.execSQL(CREATE_ORG_INFO_TABLE);
        db.execSQL(INSERT_BUILD_VERSION);

        Log.i(TAG, "Bootstrapped database");
    }

    private void reconstruct(SQLiteDatabase db) {
        dropTables(db);
        bootstrapDB(db);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
