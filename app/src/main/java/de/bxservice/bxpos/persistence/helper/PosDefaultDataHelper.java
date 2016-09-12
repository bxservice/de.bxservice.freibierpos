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

import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.persistence.dbcontract.DefaultPosDataContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 1/03/16.
 */
public class PosDefaultDataHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Default data Helper";

    public PosDefaultDataHelper(Context mContext) {
        super(mContext);
    }

    public long createData(DefaultPosData data) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_BPARTNER, data.getDefaultBPartner());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_PRICE_LIST, data.getDefaultPriceList());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_CURRENCY, data.getDefaultCurrency());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_WAREHOUSE, data.getDefaultWarehouse());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_DISCOUNT_ID, data.getDiscountId());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_SURCHARGE_ID, data.getSurchargeId());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_PIN, data.getPin());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_AD_LANGUAGE, data.getClientAdLanguage());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_ISO_CODE, data.getCurrencyIsoCode());

        int flag = (data.isCombineItems()) ? 1 : 0;
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_COMBINE_ITEMS, flag);

        flag = (data.isPrintAfterSent()) ? 1 : 0;
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_PRINT_AFTER_SEND, flag);

        flag = (data.isTaxIncluded()) ? 1 : 0;
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_IS_TAX_INCLUDED, flag);

        // insert row
        return database.insert(Tables.TABLE_DEFAULT_POS_DATA, null, values);
    }

    /*
    * Updating the default data
    */
    public int updateData (DefaultPosData data) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_BPARTNER, data.getDefaultBPartner());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_PRICE_LIST, data.getDefaultPriceList());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_CURRENCY, data.getDefaultCurrency());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_WAREHOUSE, data.getDefaultWarehouse());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_DISCOUNT_ID, data.getDiscountId());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_SURCHARGE_ID, data.getSurchargeId());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_PIN, data.getPin());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_AD_LANGUAGE, data.getClientAdLanguage());
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_ISO_CODE, data.getCurrencyIsoCode());

        int flag = (data.isCombineItems()) ? 1 : 0;
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_COMBINE_ITEMS, flag);

        flag = (data.isPrintAfterSent()) ? 1 : 0;
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_PRINT_AFTER_SEND, flag);

        flag = (data.isTaxIncluded()) ? 1 : 0;
        values.put(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_IS_TAX_INCLUDED, flag);

        // updating row
        return db.update(Tables.TABLE_DEFAULT_POS_DATA, values, DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_DEFAULT_DATA_ID + " = ?",
                new String[] { String.valueOf(1) });
    }

    /*
    * get single instance
    */
    public DefaultPosData getData(long data_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_DEFAULT_POS_DATA + " WHERE "
                + DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_DEFAULT_DATA_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(data_id) });

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        DefaultPosData defaultData = new DefaultPosData();
        defaultData.setDefaultBPartner(c.getInt(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_BPARTNER)));
        defaultData.setDefaultWarehouse(c.getInt(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_WAREHOUSE)));
        defaultData.setDefaultCurrency(c.getInt(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_CURRENCY)));
        defaultData.setDefaultPriceList(c.getInt(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_PRICE_LIST)));
        defaultData.setDiscountId(c.getInt(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_DISCOUNT_ID)));
        defaultData.setSurchargeId(c.getInt(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_SURCHARGE_ID)));
        defaultData.setPin(c.getInt(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_PIN)));
        defaultData.setClientAdLanguage(c.getString(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_AD_LANGUAGE)));
        defaultData.setCurrencyIsoCode(c.getString(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_ISO_CODE)));

        Boolean flag = (c.getInt(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_COMBINE_ITEMS)) != 0);
        defaultData.setCombineItems(flag);

        flag = (c.getInt(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_PRINT_AFTER_SEND)) != 0);
        defaultData.setPrintAfterSent(flag);

        flag = (c.getInt(c.getColumnIndex(DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_IS_TAX_INCLUDED)) != 0);
        defaultData.setTaxIncluded(flag);

        c.close();

        return defaultData;
    }

}
