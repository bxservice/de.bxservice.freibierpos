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

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.logic.util.SecureEngine;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderContract;
import de.bxservice.bxpos.persistence.dbcontract.UserContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosUserHelper extends PosObjectHelper {

    private static final String LOG_TAG = "User Helper";

    public PosUserHelper(Context mContext) {
        super(mContext);
    }

    public long createUser(PosUser user) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserContract.User.COLUMN_NAME_USERNAME, user.getUsername());

        SecureEngine secureEngine = new SecureEngine();
        String hashedPassword = secureEngine.protectText(user.getPassword());

        if(hashedPassword != null) {
            values.put(UserContract.User.COLUMN_NAME_PASSWORD, hashedPassword); //Save the encrypted password in the database
            values.put(UserContract.User.COLUMN_NAME_SALT, secureEngine.getSalt()); //Save the encrypted password in the database
        } else {
            values.put(UserContract.User.COLUMN_NAME_PASSWORD, user.getPassword()); //Save the encrypted password in the database
        }

        return database.insert(Tables.TABLE_USER, null, values);
    }

    /*
    * get single user
    */
    public PosUser getUser(long todo_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_USER + " WHERE "
                + UserContract.User.COLUMN_NAME_USER_ID + " = " + todo_id;

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        PosUser td = new PosUser();
        td.setId(c.getInt(c.getColumnIndex(UserContract.User.COLUMN_NAME_USER_ID)));
        td.setUsername((c.getString(c.getColumnIndex(UserContract.User.COLUMN_NAME_USERNAME))));
        td.setPassword(c.getString(c.getColumnIndex(UserContract.User.COLUMN_NAME_PASSWORD))); //Read the encrypted password
        td.setSalt(c.getString(c.getColumnIndex(UserContract.User.COLUMN_NAME_SALT)));

        c.close();

        return td;
    }

    /*
    * get single user from username
    */
    public PosUser getUser(String username) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_USER + " WHERE "
                + UserContract.User.COLUMN_NAME_USERNAME + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] {username});

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        PosUser td = new PosUser();
        td.setId(c.getInt(c.getColumnIndex(UserContract.User.COLUMN_NAME_USER_ID)));
        td.setUsername((c.getString(c.getColumnIndex(UserContract.User.COLUMN_NAME_USERNAME))));
        td.setPassword(c.getString(c.getColumnIndex(UserContract.User.COLUMN_NAME_PASSWORD))); //Read the encrypted password
        td.setSalt(c.getString(c.getColumnIndex(UserContract.User.COLUMN_NAME_SALT)));

        c.close();

        return td;
    }

    /*
     * get single user from username
     */
    public int getUserId(String username) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT "+ UserContract.User.COLUMN_NAME_USER_ID +" FROM " + Tables.TABLE_USER + " WHERE "
                + UserContract.User.COLUMN_NAME_USERNAME + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] {username});

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return -1;

        int userId = c.getInt(c.getColumnIndex(UserContract.User.COLUMN_NAME_USER_ID));
        c.close();

        return userId;
    }

    /*
    * Updating a user
    */
    public int updateUser(PosUser user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserContract.User.COLUMN_NAME_USERNAME, user.getUsername());

        SecureEngine secureEngine = new SecureEngine();
        String hashedPassword = secureEngine.protectText(user.getPassword());
        if(hashedPassword != null) {
            values.put(UserContract.User.COLUMN_NAME_PASSWORD, hashedPassword); //Save the encrypted password in the database
            values.put(UserContract.User.COLUMN_NAME_SALT, secureEngine.getSalt()); //Save the encrypted password in the database
        } else {
            values.put(UserContract.User.COLUMN_NAME_PASSWORD, user.getPassword()); //Save the encrypted password in the database
        }

        // updating row
        return db.update(Tables.TABLE_USER, values, UserContract.User.COLUMN_NAME_USER_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    /*
    * get username by order
    */
    public String getUserName(POSOrder order) {
        SQLiteDatabase db = getReadableDatabase();

        StringBuilder selectQuery = new StringBuilder();

        selectQuery.append("SELECT u.");
        selectQuery.append(UserContract.User.COLUMN_NAME_USERNAME);
        selectQuery.append(" FROM ");
        selectQuery.append(Tables.TABLE_USER + " u");
        selectQuery.append(" JOIN ");
        selectQuery.append(Tables.TABLE_POSORDER + " o");
        selectQuery.append(" ON");
        selectQuery.append(" u." + UserContract.User.COLUMN_NAME_USER_ID + " = o." + PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_BY);
        selectQuery.append(" AND o." + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID + " = ?");

        Log.d(LOG_TAG, selectQuery.toString());

        Cursor c = db.rawQuery(selectQuery.toString(),  new String[] {String.valueOf(order.getOrderId())});

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        String userName = c.getString(c.getColumnIndex(UserContract.User.COLUMN_NAME_USERNAME));

        c.close();

        return userName;
    }

}
