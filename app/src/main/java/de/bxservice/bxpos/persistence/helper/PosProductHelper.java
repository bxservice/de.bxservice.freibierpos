package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.persistence.dbcontract.ProductContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 28/12/15.
 */
public class PosProductHelper extends PosObjectHelper {

    static final String LOG_TAG = "Product Helper";

    public PosProductHelper(Context mContext) {
        super(mContext);
    }

    /*
    * Creating a product
    */
    public long createProduct (MProduct product) {
        SQLiteDatabase db = getWritableDatabase();

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
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_PRODUCT + " WHERE "
                + ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID + " = " + product_id;

        Log.e(LOG_TAG, selectQuery);

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
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_CATEGORY_ID, product.getProductCategoryId());
        values.put(ProductContract.ProductDB.COLUMN_NAME_NAME, product.getProductName());

        // updating row
        return db.update(Tables.TABLE_PRODUCT, values, ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID + " = ?",
                new String[] { String.valueOf(product.getProductID()) });
    }

}
