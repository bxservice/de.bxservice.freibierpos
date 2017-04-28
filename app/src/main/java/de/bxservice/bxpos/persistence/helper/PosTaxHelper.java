/**********************************************************************
 * This file is part of Freibier POS                                   *
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
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.Tax;
import de.bxservice.bxpos.logic.model.idempiere.TaxCategory;
import de.bxservice.bxpos.persistence.dbcontract.TaxContract;
import de.bxservice.bxpos.persistence.definition.Tables;

public class PosTaxHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Tax Helper";

    public PosTaxHelper(Context mContext) {
        super(mContext);
    }

    /*
    * Creating a tax
    */
    public long createTax(Tax tax) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaxContract.TaxDB.COLUMN_NAME_TAX_ID, tax.getTaxID());
        values.put(TaxContract.TaxDB.COLUMN_NAME_TAX_CATEGORY_ID, tax.getTaxCategoryID());
        values.put(TaxContract.TaxDB.COLUMN_NAME_RATE, tax.getIntRate());
        values.put(TaxContract.TaxDB.COLUMN_NAME_NAME, tax.getName());
        if (tax.getPostal() != null)
            values.put(TaxContract.TaxDB.COLUMN_NAME_POSTAL, tax.getPostal());

        // insert row
        return db.insert(Tables.TABLE_TAX, null, values);
    }

    /*
    * get single tax
    */
    public Tax getTaxes(long taxID) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TAX + " WHERE "
                + TaxContract.TaxDB.COLUMN_NAME_TAX_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(taxID) });

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else {
            if (c != null)
                c.close();
            return null;
        }

        Tax tax = new Tax();
        tax.setTaxID(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_TAX_ID)));
        tax.setName(c.getString(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_NAME)));
        tax.setRateFromInt(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_RATE)));
        tax.setTaxCategoryID(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_TAX_CATEGORY_ID)));
        tax.setPostal(c.getString(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_POSTAL)));

        c.close();

        return tax;
    }

    /*
    * get single tax by category
    */
    public List<Tax> getTaxes(TaxCategory taxCategory) {

        List<Tax> taxes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TAX + " WHERE "
                + TaxContract.TaxDB.COLUMN_NAME_TAX_CATEGORY_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(taxCategory.getTaxCategoryID()) });

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Tax tax = new Tax();
                tax.setTaxID(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_TAX_ID)));
                tax.setName(c.getString(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_NAME)));
                tax.setRateFromInt(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_RATE)));
                tax.setTaxCategoryID(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_TAX_CATEGORY_ID)));
                tax.setPostal(c.getString(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_POSTAL)));

                taxes.add(tax);
            } while (c.moveToNext());
        }

        if (c != null)
            c.close();

        return taxes;
    }

    public Tax getTaxes(long taxCategoryID, boolean toGo) {

        Tax tax = null;

        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TAX + " WHERE "
                + TaxContract.TaxDB.COLUMN_NAME_TAX_CATEGORY_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(taxCategoryID) });

        if (c.moveToFirst()) {
            //If there's only one tax in the category - return that one
            if (c.getCount() == 1) {
                tax = new Tax();
                tax.setTaxID(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_TAX_ID)));
                tax.setName(c.getString(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_NAME)));
                tax.setRateFromInt(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_RATE)));
                tax.setTaxCategoryID(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_TAX_CATEGORY_ID)));
                tax.setPostal(c.getString(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_POSTAL)));
            } else {

                boolean found = false;
                do {
                    String postal = c.getString(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_POSTAL));

                    if (toGo) {
                        //If to go the tax must have filled Postal
                        if (postal != null && !postal.isEmpty()) {
                            tax = new Tax();
                            tax.setTaxID(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_TAX_ID)));
                            tax.setName(c.getString(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_NAME)));
                            tax.setRateFromInt(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_RATE)));
                            tax.setTaxCategoryID(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_TAX_CATEGORY_ID)));
                            tax.setPostal(c.getString(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_POSTAL)));

                            found = true;
                        }
                    } else {

                        if (postal == null || "".equals(postal)) {
                            tax = new Tax();
                            tax.setTaxID(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_TAX_ID)));
                            tax.setName(c.getString(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_NAME)));
                            tax.setRateFromInt(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_RATE)));
                            tax.setTaxCategoryID(c.getInt(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_TAX_CATEGORY_ID)));
                            tax.setPostal(c.getString(c.getColumnIndex(TaxContract.TaxDB.COLUMN_NAME_POSTAL)));

                            found = true;
                        }
                    }
                } while (c.moveToNext() && !found);
            }
        }

        if (c != null)
            c.close();

        return tax;
    }

    /*
    * Updating a tax
    */
    public int updateTax(Tax tax) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaxContract.TaxDB.COLUMN_NAME_TAX_ID, tax.getTaxID());
        values.put(TaxContract.TaxDB.COLUMN_NAME_TAX_CATEGORY_ID, tax.getTaxCategoryID());
        values.put(TaxContract.TaxDB.COLUMN_NAME_RATE, tax.getIntRate());
        values.put(TaxContract.TaxDB.COLUMN_NAME_NAME, tax.getName());
        if (tax.getPostal() != null)
            values.put(TaxContract.TaxDB.COLUMN_NAME_POSTAL, tax.getPostal());

        // updating row
        return db.update(Tables.TABLE_TAX, values, TaxContract.TaxDB.COLUMN_NAME_TAX_ID + " = ?",
                new String[] { String.valueOf(tax.getTaxID()) });
    }

}
