/**********************************************************************
 * This file is part of FreiBier POS                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.persistence.dbcontract.ProductContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 28/12/15.
 */
public class PosProductHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Product Helper";

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
        values.put(ProductContract.ProductDB.COLUMN_NAME_VALUE, product.getProductkey());
        if (product.getOutputDeviceId() != 0)
            values.put(ProductContract.ProductDB.COLUMN_OUTPUT_DEVICE_ID, product.getOutputDeviceId());

        int flag = (product.isActive()) ? 1 : 0;
        values.put(ProductContract.ProductDB.COLUMN_IS_ACTIVE, flag);

        // insert row
        return db.insert(Tables.TABLE_PRODUCT, null, values);
    }

    /*
    * get single product
    */
    public MProduct getProduct (long product_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_PRODUCT + " WHERE "
                + ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(product_id) });

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        MProduct product = new MProduct();
        product.setProductID(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID)));
        product.setProductCategoryId(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_CATEGORY_ID)));
        product.setProductName(c.getString(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_NAME)));
        product.setProductkey(c.getString(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_VALUE)));
        product.setOutputDeviceId(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_OUTPUT_DEVICE_ID)));

        Boolean flag = (c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_IS_ACTIVE)) != 0);
        product.setActive(flag);

        c.close();

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
        values.put(ProductContract.ProductDB.COLUMN_NAME_VALUE, product.getProductkey());
        if (product.getOutputDeviceId() != 0)
            values.put(ProductContract.ProductDB.COLUMN_OUTPUT_DEVICE_ID, product.getOutputDeviceId());

        int flag = (product.isActive()) ? 1 : 0;
        values.put(ProductContract.ProductDB.COLUMN_IS_ACTIVE, flag);

        // updating row
        return db.update(Tables.TABLE_PRODUCT, values, ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID + " = ?",
                new String[] { String.valueOf(product.getProductID()) });
    }

    /**
     * Get products from a product category
     * @param productCategory
     * @return
     */
    public ArrayList<MProduct> getAllProducts(ProductCategory productCategory) {
        ArrayList<MProduct> products = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_PRODUCT + " product " +
                " WHERE product." + ProductContract.ProductDB.COLUMN_NAME_PRODUCT_CATEGORY_ID
                + " = ? AND product." + ProductContract.ProductDB.COLUMN_IS_ACTIVE + " = 1";

        Log.d(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(productCategory.getProductCategoryID())});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                MProduct product = new MProduct();
                product.setProductID(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID)));
                product.setProductCategoryId(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_CATEGORY_ID)));
                product.setProductName(c.getString(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_NAME)));
                product.setProductkey(c.getString(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_VALUE)));
                product.setOutputDeviceId(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_OUTPUT_DEVICE_ID)));

                Boolean flag = (c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_IS_ACTIVE)) != 0);
                product.setActive(flag);

                products.add(product);
            } while (c.moveToNext());
            c.close();
        }

        return products;
    }

    /**
     * Get all products
     * @return
     */
    public ArrayList<MProduct> getAllProducts() {
        ArrayList<MProduct> products = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_PRODUCT +
                " WHERE " + ProductContract.ProductDB.COLUMN_IS_ACTIVE + " = 1" +
                " ORDER BY " + ProductContract.ProductDB.COLUMN_NAME_NAME;

        Log.d(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                MProduct product = new MProduct();
                product.setProductID(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID)));
                product.setProductCategoryId(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_PRODUCT_CATEGORY_ID)));
                product.setProductName(c.getString(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_NAME)));
                product.setProductkey(c.getString(c.getColumnIndex(ProductContract.ProductDB.COLUMN_NAME_VALUE)));
                product.setOutputDeviceId(c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_OUTPUT_DEVICE_ID)));

                Boolean flag = (c.getInt(c.getColumnIndex(ProductContract.ProductDB.COLUMN_IS_ACTIVE)) != 0);
                product.setActive(flag);

                products.add(product);
            } while (c.moveToNext());
            c.close();
        }

        return products;
    }

}
