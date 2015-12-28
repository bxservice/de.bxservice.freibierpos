package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;
import de.bxservice.bxpos.persistence.dbcontract.ProductPriceContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 28/12/15.
 */
public class PosProductPriceHelper extends PosObjectHelper {

    static final String LOG_TAG = "Product Price Helper";

    public PosProductPriceHelper(Context mContext) {
        super(mContext);
    }

    /*
    * Creating a product price
    */
    public long createProductPrice (ProductPrice productPrice) {
        SQLiteDatabase db = getWritableDatabase();

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
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_PRODUCT_PRICE + " WHERE "
                + ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_PRICE_ID + " = " + productPrice_id;

        Log.e(LOG_TAG, selectQuery);

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
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRICE_LIST_VERSION_ID, productPrice.getPriceListVersionID());
        values.put(ProductPriceContract.ProductPriceDB.COLUMN_NAME_STD_PRICE, productPrice.getStdPrice().toString());

        // updating row
        return db.update(Tables.TABLE_PRODUCT_PRICE, values, ProductPriceContract.ProductPriceDB.COLUMN_NAME_PRODUCT_PRICE_ID + " = ?",
                new String[] { String.valueOf(productPrice.getProductPriceID()) });
    }

}
