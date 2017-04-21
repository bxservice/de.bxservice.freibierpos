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

import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
import de.bxservice.bxpos.persistence.dbcontract.GroupTableContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 28/12/15.
 */
public class PosTableGroupHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Table Group Helper";

    public PosTableGroupHelper(Context mContext) {
        super(mContext);
    }

    /*
    * Creating a table group
    */
    public long createTableGroup (TableGroup tableGroup) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID, tableGroup.getTableGroupID());
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_GROUP_TABLE_NAME, tableGroup.getName());
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_VALUE, tableGroup.getValue());

        // insert row
        return db.insert(Tables.TABLE_TABLE_GROUP, null, values);
    }

    /*
    * get single table group
    */
    public TableGroup getTableGroup(long tablegroup_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TABLE_GROUP + " WHERE "
                + GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID + " =?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(tablegroup_id) });

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else {
            if (c != null)
                c.close();
            return null;
        }

        TableGroup tableGroup = new TableGroup();
        tableGroup.setTableGroupID(c.getInt(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID)));
        tableGroup.setValue((c.getString(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_VALUE))));
        tableGroup.setName(c.getString(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_GROUP_TABLE_NAME)));

        c.close();

        return tableGroup;
    }

    /*
    * Updating a table group
    */
    public int updateTableGroup (TableGroup tableGroup) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID, tableGroup.getTableGroupID());
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_GROUP_TABLE_NAME, tableGroup.getName());
        values.put(GroupTableContract.GroupTableDB.COLUMN_NAME_VALUE, tableGroup.getValue());

        // updating row
        return db.update(Tables.TABLE_TABLE_GROUP, values, GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID + " = ?",
                new String[] { String.valueOf(tableGroup.getTableGroupID()) });
    }

    /**
     * Getting all table groups
     */
    public List<TableGroup> getAllTableGroups() {
        List<TableGroup> tableGroups = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TABLE_GROUP;

        Log.d(LOG_TAG, selectQuery);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            PosTableHelper tableHelper = new PosTableHelper(mContext);
            do {
                TableGroup tableGroup = new TableGroup();
                tableGroup.setTableGroupID(c.getInt(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID)));
                tableGroup.setValue((c.getString(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_VALUE))));
                tableGroup.setName(c.getString(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_GROUP_TABLE_NAME)));
                tableGroup.setTables(tableHelper.getAllTables(tableGroup));

                // adding to orders list
                tableGroups.add(tableGroup);
            } while (c.moveToNext());
        }

        if (c != null)
            c.close();

        return tableGroups;
    }

    /**
     * getting total of rows in the table groups table
     */
    public long getTotalTableGroups() {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, Tables.TABLE_TABLE_GROUP);
    }

}
