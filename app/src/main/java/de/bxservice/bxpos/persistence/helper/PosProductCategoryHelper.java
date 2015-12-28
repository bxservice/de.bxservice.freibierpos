package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.persistence.dbcontract.ProductCategoryContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 28/12/15.
 */
public class PosProductCategoryHelper extends PosObjectHelper {

    static final String LOG_TAG = "Product Category Helper";

    public PosProductCategoryHelper(Context mContext) {
        super(mContext);
    }

    /*
    * Creating a product category
    */
    public long createProductCategory (ProductCategory productCategory) {
        SQLiteDatabase db = getWritableDatabase();

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
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_PRODUCT_CATEGORY + " WHERE "
                + ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + " = " + productCategory_id;

        Log.e(LOG_TAG, selectQuery);

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
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_NAME, productCategory.getName());

        // updating row
        return db.update(Tables.TABLE_PRODUCT_CATEGORY, values, ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + " = ?",
                new String[] { String.valueOf(productCategory.getProductCategoryID()) });
    }
}