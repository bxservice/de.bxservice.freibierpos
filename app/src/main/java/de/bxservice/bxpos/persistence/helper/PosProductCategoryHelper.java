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
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.persistence.dbcontract.ProductCategoryContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 28/12/15.
 */
public class PosProductCategoryHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Product Category Helper";

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
        if (productCategory.getOutputDeviceId() != 0)
            values.put(ProductCategoryContract.ProductCategoryDB.COLUMN_OUTPUT_DEVICE_ID, productCategory.getOutputDeviceId());

        // insert row
        return db.insert(Tables.TABLE_PRODUCT_CATEGORY, null, values);
    }

    /*
    * get single product category
    */
    public ProductCategory getProductCategory(long productCategory_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_PRODUCT_CATEGORY + " WHERE "
                + ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(productCategory_id) });

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else {
            if (c != null)
                c.close();
            return null;
        }

        ProductCategory productCategory = new ProductCategory(c.getInt(c.getColumnIndex(ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID)),
                c.getString(c.getColumnIndex(ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_NAME)));
        productCategory.setOutputDeviceId(c.getInt(c.getColumnIndex(ProductCategoryContract.ProductCategoryDB.COLUMN_OUTPUT_DEVICE_ID)));

        c.close();

        return productCategory;
    }

    /*
    * Updating a product category
    */
    public int updateProductCategory(ProductCategory productCategory) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_NAME, productCategory.getName());
        if (productCategory.getOutputDeviceId() != 0)
            values.put(ProductCategoryContract.ProductCategoryDB.COLUMN_OUTPUT_DEVICE_ID, productCategory.getOutputDeviceId());

        // updating row
        return db.update(Tables.TABLE_PRODUCT_CATEGORY, values, ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + " = ?",
                new String[] { String.valueOf(productCategory.getProductCategoryID()) });
    }

    /**
     * Getting all product category
     */
    public List<ProductCategory> getAllProductCategories() {
        List<ProductCategory> productCategories = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Tables.TABLE_PRODUCT_CATEGORY;

        Log.d(LOG_TAG, selectQuery);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            PosProductHelper productHelper = new PosProductHelper(mContext);
            do {
                ProductCategory productCategory = new ProductCategory();
                productCategory.setProductCategoryID(c.getInt(c.getColumnIndex(ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID)));
                productCategory.setName(c.getString(c.getColumnIndex(ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_NAME)));
                productCategory.setProducts(productHelper.getAllProducts(productCategory));
                productCategory.setOutputDeviceId(c.getInt(c.getColumnIndex(ProductCategoryContract.ProductCategoryDB.COLUMN_OUTPUT_DEVICE_ID)));

                // adding to category list
                productCategories.add(productCategory);
            } while (c.moveToNext());
        }

        if (c != null)
            c.close();

        return productCategories;
    }

    /**
     * getting total of rows in the table product category
     */
    public long getTotalCategories() {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, Tables.TABLE_PRODUCT_CATEGORY);
    }

}
