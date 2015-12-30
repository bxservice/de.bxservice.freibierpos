package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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

    static final String LOG_TAG = "Table Group Helper";

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
        long tablegroupId = db.insert(Tables.TABLE_TABLE_GROUP, null, values);

        return tablegroupId;
    }

    /*
    * get single table group
    */
    public TableGroup getTableGroup(long tablegroup_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TABLE_GROUP + " WHERE "
                + GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID + " = " + tablegroup_id;

        Log.e(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        TableGroup tableGroup = new TableGroup();
        tableGroup.setTableGroupID(c.getInt(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_TABLE_GROUP_ID)));
        tableGroup.setValue((c.getString(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_VALUE))));
        tableGroup.setName(c.getString(c.getColumnIndex(GroupTableContract.GroupTableDB.COLUMN_NAME_GROUP_TABLE_NAME)));

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
     * Getting all orders
     */
    public List<TableGroup> getAllTableGroups() {
        List<TableGroup> tableGroups = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TABLE_GROUP;

        Log.e(LOG_TAG, selectQuery);

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

        return tableGroups;
    }

}
