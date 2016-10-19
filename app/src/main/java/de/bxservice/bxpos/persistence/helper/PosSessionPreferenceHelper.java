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

import java.util.HashMap;
import java.util.Map;

import de.bxservice.bxpos.persistence.dbcontract.SessionPreferenceContract;
import de.bxservice.bxpos.persistence.definition.Tables;

public class PosSessionPreferenceHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Session preference";

    public PosSessionPreferenceHelper(Context mContext) {
        super(mContext);
    }

    public boolean createSessionPreference(HashMap<String, String> keyValuePair) {
        SQLiteDatabase database = getWritableDatabase();

        for (Map.Entry<String, String> entry : keyValuePair.entrySet()) {
            ContentValues values = new ContentValues();
            values.put(SessionPreferenceContract.SessionPreferenceDB.COLUMN_NAME_PREF_NAME, entry.getKey());
            values.put(SessionPreferenceContract.SessionPreferenceDB.COLUMN_NAME_PREF_VALUE, entry.getValue());

            if (database.insert(Tables.TABLE_SESSION_PREFERENCE, null, values) == -1)
                return false;
        }

        return true;
    }

    public String getPreferenceValue(String preferenceName) {

        SQLiteDatabase db = getReadableDatabase();

        StringBuilder selectQuery = new StringBuilder();

        selectQuery.append("SELECT ");
        selectQuery.append(SessionPreferenceContract.SessionPreferenceDB.COLUMN_NAME_PREF_VALUE);
        selectQuery.append(" FROM ");
        selectQuery.append(Tables.TABLE_SESSION_PREFERENCE);
        selectQuery.append(" WHERE ");
        selectQuery.append(SessionPreferenceContract.SessionPreferenceDB.COLUMN_NAME_PREF_NAME);
        selectQuery.append(" = ?");

        Log.d(LOG_TAG, selectQuery.toString());

        Cursor c = db.rawQuery(selectQuery.toString(),  new String[] {preferenceName});

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        String preferenceValue = c.getString(c.getColumnIndex(SessionPreferenceContract.SessionPreferenceDB.COLUMN_NAME_PREF_VALUE));

        c.close();

        return preferenceValue;
    }

    public void cleanSessionPreferenceData() {
        SQLiteDatabase database = getWritableDatabase();

        database.delete(Tables.TABLE_SESSION_PREFERENCE, null , null);
        database.execSQL("VACUUM");
    }

}
