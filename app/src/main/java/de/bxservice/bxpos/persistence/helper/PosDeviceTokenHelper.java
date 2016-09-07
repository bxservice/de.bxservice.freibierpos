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

import de.bxservice.bxpos.fcm.DeviceToken;
import de.bxservice.bxpos.persistence.dbcontract.DeviceTokenContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 5/23/16.
 */
public class PosDeviceTokenHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Default data Helper";

    public PosDeviceTokenHelper(Context mContext) {
        super(mContext);
    }

    public long createToken(DeviceToken token) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DeviceTokenContract.DeviceTokenDB.COLUMN_NAME_CREATED_AT, getCurrentDate());
        values.put(DeviceTokenContract.DeviceTokenDB.COLUMN_NAME_DEVICE_TOKEN_VALUE, token.getDeviceToken());

        int flag = (token.isSynchonized()) ? 1 : 0;
        values.put(DeviceTokenContract.DeviceTokenDB.COLUMN_NAME_SYNCHRONIZED, flag);

        // insert row
        return database.insert(Tables.TABLE_DEVICE_TOKEN, null, values);
    }

    /*
    * Updating the default data
    */
    public int updateData (DeviceToken token) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DeviceTokenContract.DeviceTokenDB.COLUMN_NAME_DEVICE_TOKEN_ID, getCurrentDate());
        values.put(DeviceTokenContract.DeviceTokenDB.COLUMN_NAME_DEVICE_TOKEN_VALUE, token.getDeviceToken());

        int flag = (token.isSynchonized()) ? 1 : 0;
        values.put(DeviceTokenContract.DeviceTokenDB.COLUMN_NAME_SYNCHRONIZED, flag);

        // updating row
        return db.update(Tables.TABLE_DEVICE_TOKEN, values, DeviceTokenContract.DeviceTokenDB.COLUMN_NAME_DEVICE_TOKEN_ID + " = ?",
                new String[] { String.valueOf(1) });
    }


    /*
    * get single instance
    */
    public DeviceToken getToken() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + Tables.TABLE_DEVICE_TOKEN + " WHERE "
                + DeviceTokenContract.DeviceTokenDB.COLUMN_NAME_DEVICE_TOKEN_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(1) });

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setDeviceToken(c.getString(c.getColumnIndex(DeviceTokenContract.DeviceTokenDB.COLUMN_NAME_DEVICE_TOKEN_VALUE)));

        Boolean flag = (c.getInt(c.getColumnIndex(DeviceTokenContract.DeviceTokenDB.COLUMN_NAME_SYNCHRONIZED)) != 0);
        deviceToken.setSynchonized(flag);
        c.close();

        return deviceToken;

    }

}
