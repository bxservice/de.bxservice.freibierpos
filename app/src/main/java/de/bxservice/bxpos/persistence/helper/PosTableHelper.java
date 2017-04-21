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
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderContract;
import de.bxservice.bxpos.persistence.dbcontract.TableContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 28/12/15.
 */
public class PosTableHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Table Helper";

    public PosTableHelper(Context mContext) {
        super(mContext);
    }

    /*
    * Creating a table
    */
    public long createTable(Table table) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_ID, table.getTableID());
        values.put(TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID, table.getBelongingGroup().getTableGroupID());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_NAME, table.getTableName());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_STATUS, table.getStatus());
        values.put(TableContract.TableDB.COLUMN_NAME_VALUE, table.getValue());
        values.put(TableContract.TableDB.COLUMN_NAME_UPDATED_AT, Long.parseLong(getCurrentDate()));

        // insert row
        return db.insert(Tables.TABLE_TABLE, null, values);
    }

    /*
    * get single table
    */
    public Table getTable(long table_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TABLE + " WHERE "
                + TableContract.TableDB.COLUMN_NAME_TABLE_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(table_id)});

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else {
            if (c != null)
                c.close();
            return null;
        }

        PosTableGroupHelper tableGroupHelper = new PosTableGroupHelper(mContext);

        Table table = new Table();
        table.setTableID(c.getInt(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_ID)));
        table.setBelongingGroup(tableGroupHelper.getTableGroup(c.getInt(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID))));
        table.setStatus((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_STATUS))));
        table.setTableName(c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_NAME)));
        table.setValue((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_VALUE))));
        table.setStatusChangeTime(c.getLong(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_UPDATED_AT)));
        table.setServerName((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_SERVER_NAME))));

        c.close();

        return table;
    }

    /*
    * Updating a table
    */
    public int updateTable(Table table) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID, table.getBelongingGroup().getTableGroupID());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_NAME, table.getTableName());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_STATUS, table.getStatus());
        values.put(TableContract.TableDB.COLUMN_NAME_VALUE, table.getValue());

        //Only update this value when the status is changed
        if (table.isStatusChanged()) {
            values.put(TableContract.TableDB.COLUMN_NAME_UPDATED_AT, Long.parseLong(getCurrentDate()));
            values.put(TableContract.TableDB.COLUMN_NAME_SERVER_NAME, table.getServerName());
        }

        // updating row
        return db.update(Tables.TABLE_TABLE, values, TableContract.TableDB.COLUMN_NAME_TABLE_ID + " = ?",
                new String[] { String.valueOf(table.getTableID()) });
    }

    /**
     * Getting all orders
     */
    public List<Table> getAllTables() {
        List<Table> tables = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TABLE;

        Log.d(LOG_TAG, selectQuery);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            PosTableGroupHelper tableGroupHelper = new PosTableGroupHelper(mContext);
            do {
                Table table = new Table();
                table.setTableID(c.getInt(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_ID)));
                table.setBelongingGroup(tableGroupHelper.getTableGroup(c.getInt(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID))));
                table.setStatus((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_STATUS))));
                table.setTableName(c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_NAME)));
                table.setValue((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_VALUE))));
                table.setStatusChangeTime(c.getLong(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_UPDATED_AT)));
                table.setServerName((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_SERVER_NAME))));

                // adding to orders list
                tables.add(table);
            } while (c.moveToNext());
        }

        if (c != null)
            c.close();

        return tables;
    }

    /**
     * Getting all tables belonging to a group
     * @param tableGroup
     * @return
     */
    public ArrayList<Table> getAllTables(TableGroup tableGroup) {
        ArrayList<Table> tables = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Tables.TABLE_TABLE +
                " WHERE " + TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID
                + " = ?";

        Log.d(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(tableGroup.getTableGroupID())});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Table table = new Table();
                table.setTableID(c.getInt(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_ID)));
                table.setBelongingGroup(tableGroup);
                table.setStatus((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_STATUS))));
                table.setTableName(c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_NAME)));
                table.setValue((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_VALUE))));
                table.setStatusChangeTime(c.getLong(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_UPDATED_AT)));
                table.setServerName((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_SERVER_NAME))));

                tables.add(table);
            } while (c.moveToNext());
        }

        if (c != null)
            c.close();

        return tables;
    }

    /**
     * Check if there are no more orders in the table
     * if there are more -> return false
     * @param table
     * @return
     */
    public boolean isTableFree(Table table) {

        int orders = 0;

        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER + " WHERE "
                + PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID + " = ? AND "
                + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS + " NOT IN (?,?) ";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(table.getTableID()), POSOrder.COMPLETE_STATUS, POSOrder.VOID_STATUS});

        if (c != null) {
            orders = c.getCount();
            c.close();
        }

        //If there is at least 1 order in the table -> table is occupied
        return orders <= 0;
    }
}
