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

import de.bxservice.bxpos.logic.model.idempiere.PosTenderType;
import de.bxservice.bxpos.persistence.dbcontract.PosTenderTypeContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 9/14/16.
 */
public class PosTenderTypeHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Tender Type Helper";

    public PosTenderTypeHelper(Context mContext) {
        super(mContext);
    }

    public long createTenderType(PosTenderType tenderType) {
        SQLiteDatabase database = getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(PosTenderTypeContract.PosTenderTypeDB.COLUMN_NAME_TENDER_TYPE_ID, tenderType.getC_POSTenderType_ID());
        values.put(PosTenderTypeContract.PosTenderTypeDB.COLUMN_NAME_TENDER_TYPE, tenderType.getTenderType());

        // insert row
        return database.insert(Tables.TABLE_POSTENDER_TYPE, null, values);
    }

    /*
     * Updating a payment
     */
    public int updatePayment(PosTenderType tenderType) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PosTenderTypeContract.PosTenderTypeDB.COLUMN_NAME_TENDER_TYPE_ID, tenderType.getC_POSTenderType_ID());
        values.put(PosTenderTypeContract.PosTenderTypeDB.COLUMN_NAME_TENDER_TYPE, tenderType.getTenderType());

        // updating row
        return db.update(Tables.TABLE_POSTENDER_TYPE, values, PosTenderTypeContract.PosTenderTypeDB.COLUMN_NAME_TENDER_TYPE_ID + " = ?",
                new String[] { String.valueOf(tenderType.getC_POSTenderType_ID()) });
    }

    /**
     * Getting tender type ID by its type
     * @param tenderType
     * @return
     */
    public PosTenderType getPosTenderType(String tenderType) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSTENDER_TYPE + " WHERE "
                + PosTenderTypeContract.PosTenderTypeDB.COLUMN_NAME_TENDER_TYPE + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { tenderType });

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        PosTenderType posTenderType = new PosTenderType();
        posTenderType.setC_POSTenderType_ID(c.getInt(c.getColumnIndex(PosTenderTypeContract.PosTenderTypeDB.COLUMN_NAME_TENDER_TYPE_ID)));
        posTenderType.setTenderType(c.getString(c.getColumnIndex(PosTenderTypeContract.PosTenderTypeDB.COLUMN_NAME_TENDER_TYPE)));

        c.close();

        return posTenderType;
    }

    public PosTenderType getPosTenderType(long tenderType_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSTENDER_TYPE + " WHERE "
                + PosTenderTypeContract.PosTenderTypeDB.COLUMN_NAME_TENDER_TYPE_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(tenderType_id) });

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        PosTenderType posTenderType = new PosTenderType();
        posTenderType.setC_POSTenderType_ID(c.getInt(c.getColumnIndex(PosTenderTypeContract.PosTenderTypeDB.COLUMN_NAME_TENDER_TYPE_ID)));
        posTenderType.setTenderType(c.getString(c.getColumnIndex(PosTenderTypeContract.PosTenderTypeDB.COLUMN_NAME_TENDER_TYPE)));

        c.close();

        return posTenderType;
    }

}
