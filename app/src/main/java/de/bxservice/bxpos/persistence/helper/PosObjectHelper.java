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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.Calendar;

import de.bxservice.bxpos.logic.daomanager.PosSessionPreferenceManagement;
import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public abstract class PosObjectHelper {

    private static final String LOG_TAG = "Object Helper";
    Context mContext;

    PosObjectHelper (Context mContext) {
        this.mContext = mContext;
    }

    protected SQLiteDatabase getReadableDatabase() {
        return PosDatabaseHelper.getInstance(mContext).getReadableDatabase();
    }

    protected SQLiteDatabase getWritableDatabase() {
        try {
            return PosDatabaseHelper.getInstance(mContext).getWritableDatabase();
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, "Cannot open writable database", e);
            return null;
        }
    }

    // closing database
    public static void closeDB(Context mContext) {
        PosDatabaseHelper.getInstance(mContext).closeDB();
    }

    /**
     * Returns the current date in format
     * yyyymmddhhmm
     * @return
     */
    protected String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; //Calendar month returns the position of the month 0 being January
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        StringBuilder date = new StringBuilder();
        date.append(year);
        if(month < 10)
            date.append("0");
        date.append(month);
        if(day < 10)
            date.append("0");
        date.append(day);
        if(hour < 10)
            date.append("0");
        date.append(hour);
        if(minutes < 10)
            date.append("0");
        date.append(minutes);

        return date.toString();
    }

    protected int getLoggedUser() {
        PosUserHelper userHelper = new PosUserHelper(mContext);
        PosSessionPreferenceManagement preferenceManager = new PosSessionPreferenceManagement(mContext);
        return userHelper.getUserId(preferenceManager.getPreferenceValue(WebServiceRequestData.USERNAME_SYNC_PREF));
    }

}
