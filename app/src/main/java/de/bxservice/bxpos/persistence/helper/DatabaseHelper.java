package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.persistence.dbcontract.GroupTableContract;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderContract;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderLineContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductCategoryContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductPriceContract;
import de.bxservice.bxpos.persistence.dbcontract.TableContract;
import de.bxservice.bxpos.persistence.dbcontract.UserContract;
import de.bxservice.bxpos.ui.adapter.TableGridItem;

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
                    PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_BY + " VARCHAR(64) NOT NULL" + //TODO:FK to users
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
        return db.update(Tables.TABLE_TABLE, values, TableContract.TableDB.COLUMN_NAME_TABLE_ID + " = ?",
                new String[] { String.valueOf(table.getTableID()) });
    }

    /*
    * Creating a table group
    */
    public long createTableGroup (TableGroup tableGroup) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID, tableGroup.getTableGroupID());
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_GROUP_TABLE_NAME, tableGroup.getName());
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_VALUE, tableGroup.getValue());

        // insert row
        long tablegroupId = db.insert(Tables.TABLE_TABLE_GROUP, null, values);

        return tablegroupId;
    }

    /*
    * get single table group
    */
    public TableGroup getTableGroup(long tablegroup_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TABLE_GROUP + " WHERE "
                + GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID + " = " + tablegroup_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        TableGroup tableGroup = new TableGroup();
        tableGroup.setTableGroupID(c.getInt(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID)));
        tableGroup.setValue((c.getString(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_VALUE))));
        tableGroup.setName(c.getString(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_GROUP_TABLE_NAME)));

        return tableGroup;
    }

    /*
    * Updating a table group
    */
    public int updateTableGroup (TableGroup tableGroup) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID, tableGroup.getTableGroupID());
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_GROUP_TABLE_NAME, tableGroup.getName());
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_VALUE, tableGroup.getValue());

        // updating row
        return db.update(Tables.TABLE_TABLE_GROUP, values, GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID + " = ?",
                new String[] { String.valueOf(tableGroup.getTableGroupID()) });
    }

    /*
    * Creating a product category
    */
    public long createProductCategory (ProductCategory productCategory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID, productCategory.getProductCategoryID());
        values.put(ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_NAME, productCategory.getName());

        // insert row
        long productCategoryId = db.insert(Tables.TABLE_PRODUCT_CATEGORY, null, values);

        return productCategoryId;
    }

    /*
    * get single product category
    */
    public ProductCategory getProductCategory (long productCategory_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_PRODUCT_CATEGORY + " WHERE "
                + ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + " = " + productCategory_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        ProductCategory productCategory = new ProductCategory(c.getInt(c.getColumnIndex(ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID)),
                c.getString(c.getColumnIndex(ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_NAME)));

        return productCategory;
    }

    /*
    * Updating a product category
    */
    public int updateProductCategory (ProductCategory productCategory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_NAME, productCategory.getName());

        // updating row
        return db.update(Tables.TABLE_PRODUCT_CATEGORY, values, ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + " = ?",
                new String[] { String.valueOf(productCategory.getProductCategoryID()) });
    }

    /*
    * Creating a product price
    */
    public long createProductPrice (ProductPrice productPrice) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_PRICE_ID, productPrice.getProductPriceID());
        values.put(ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_ID, productPrice.getProductID());
        values.put(ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRICE_LIST_VERSION_ID, productPrice.getPriceListVersionID());
        values.put(ProductPriceContract.ProductPriceDB.COLUMN_NAME_STD_PRICE, productPrice.getStdPrice().toString()); //TODO: check sql lite price

        // insert row
        long productPriceId = db.insert(Tables.TABLE_PRODUCT_PRICE, null, values);

        return productPriceId;
    }

    /*
    * get single product price
    */
    public ProductPrice getProductPrice (long productPrice_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_PRODUCT_PRICE + " WHERE "
                + ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_PRICE_ID + " = " + productPrice_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        ProductPrice productPrice = new ProductPrice();
        productPrice.setProductPriceID(c.getInt(c.getColumnIndex(ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_PRICE_ID)));
        productPrice.setProductID(c.getInt(c.getColumnIndex(ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_ID)));
        productPrice.setPriceListVersionID(c.getInt(c.getColumnIndex(ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRICE_LIST_VERSION_ID)));
        //productPrice.setStdPrice(); // TODO: SOLVE THIS AND SET PRODUCT????

        return productPrice;
    }

    /*
    * Updating a product price
    */
    public int updateProductPrice (ProductPrice productPrice) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRICE_LIST_VERSION_ID, productPrice.getPriceListVersionID());
        values.put(ProductPriceContract.ProductPriceDB.COLUMN_NAME_STD_PRICE, productPrice.getStdPrice().toString());

        // updating row
        return db.update(Tables.TABLE_PRODUCT_PRICE, values, ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_PRICE_ID + " = ?",
                new String[] { String.valueOf(productPrice.getProductPriceID()) });
    }

    /*
    * Creating a product
    */
    public long createProduct (MProduct product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID, product.getProductID());
        values.put(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_CATEGORY_ID, product.getProductCategoryId());
        values.put(ProductContract.ProductDB.COLUMN_NAME_NAME, product.getProductName());

        // insert row
        long productId = db.insert(Tables.TABLE_PRODUCT, null, values);

        return productId;
    }

    /*
    * get single product
    */
    public MProduct getProduct (long product_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_PRODUCT + " WHERE "
                + ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID + " = " + product_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        MProduct product = new MProduct();
        product.setProductID(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID)));
        product.setProductCategoryId(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_CATEGORY_ID)));
        product.setProductName(c.getString(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_NAME)));

        return product;
    }

    /*
    * Updating a product
    */
    public int updateProduct (MProduct product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_CATEGORY_ID, product.getProductCategoryId());
        values.put(ProductContract.ProductDB.COLUMN_NAME_NAME, product.getProductName());

        // updating row
        return db.update(Tables.TABLE_PRODUCT, values, ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID + " = ?",
                new String[] { String.valueOf(product.getProductID()) });
    }

    /*
    * Creating a pos Order
    */
    public long createOrder (POSOrder order) {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ContentValues values = new ContentValues();
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_AT, dateFormat.format(date));
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_BY, ""); //TODO: Get current user
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS, order.getStatus());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID, order.getTable().getTableID());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS, order.getGuestNumber());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK, order.getOrderRemark());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES, order.getTotallinesInteger());

        // insert row
        long orderId = db.insert(Tables.TABLE_POSORDER, null, values);

        for (POSOrderLine orderLine: order.getOrderLines())
            createOrderLine(orderLine, orderId);

        return orderId;
    }

    /*
    * get single order
    */
    public POSOrder getOrder (long order_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER + " WHERE "
                + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID + " = " + order_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        POSOrder order = new POSOrder();
        order.setOrderId(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID)));
        order.setStatus(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS)));
        order.setGuestNumber(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS)));
        order.setOrderRemark(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK)));
        order.setTotalFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES)));
        //order.setTableId(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID))); //TODO; table ID set table

        return order;
    }

    /*
    * Updating an order
    */
    public int updateOrder (POSOrder order) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS, order.getStatus());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID, order.getTable().getTableID());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS, order.getGuestNumber());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK, order.getOrderRemark());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES, order.getTotallinesInteger());

        // updating row
        return db.update(Tables.TABLE_POSORDER, values, PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID + " = ?",
                new String[] { String.valueOf(order.getOrderId()) });
    }

    /*
    * Creating a pos Order line
    */
    public long createOrderLine (POSOrderLine orderLine, long orderlineId) {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ContentValues values = new ContentValues();
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_CREATED_AT, dateFormat.format(date));
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_CREATED_BY, ""); //TODO: Get current user
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID, orderlineId);
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS, orderLine.getLineStatus());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID, orderLine.getProduct().getProductID());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY, orderLine.getQtyOrdered());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENO, orderLine.getLineNo());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK, orderLine.getProductRemark());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT, orderLine.getLineNetAmtInteger());

        // insert row
        long orderLineId = db.insert(Tables.TABLE_POSORDER_LINE, null, values);

        return orderLineId;
    }

    /*
    * get single order line
    */
    public POSOrderLine getOrderLine (long orderline_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER_LINE + " WHERE "
                + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID + " = " + orderline_id;

        Log.e(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        POSOrderLine orderLine = new POSOrderLine();
        orderLine.setOrderLineId(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID)));
        //orderLine.setOrderId(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID))); //TODO: Create method
        orderLine.setLineStatus(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS)));
        orderLine.setProductRemark(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK)));
        orderLine.setQtyOrdered(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY)));
        orderLine.setLineTotalFromInt(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT)));
        //orderLine.setProductId(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID))); //TODO: Create method

        return orderLine;
    }

    /*
    * Updating a order line
    */
    public int updateOrderLine (POSOrderLine orderLine) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID, orderLine.getOrder().getOrderId());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS, orderLine.getLineStatus());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID, orderLine.getProduct().getProductID());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY, orderLine.getQtyOrdered());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK, orderLine.getProductRemark());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT, orderLine.getLineNetAmtInteger());

        // updating row
        return db.update(Tables.TABLE_POSORDER_LINE, values,PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID + " = ?",
                new String[] { String.valueOf(orderLine.getOrderLineId()) });
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

}
