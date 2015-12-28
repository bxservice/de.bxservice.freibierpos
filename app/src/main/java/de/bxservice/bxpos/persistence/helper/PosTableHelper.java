package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.persistence.dbcontract.TableContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 28/12/15.
 */
public class PosTableHelper extends PosObjectHelper {

    static final String LOG_TAG = "Table Helper";

    public PosTableHelper(Context mContext) {
        super(mContext);
    }

    /*
    * Creating a table
    */
    public long createTable (Table table) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_ID, table.getTableID());
        values.put(TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID, table.getBelongingGroup().getTableGroupID());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_NAME, table.getTableName());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_STATUS, table.getStatus());
        values.put(TableContract.TableDB.COLUMN_NAME_VALUE, table.getValue());

        // insert row
        long tableId = db.insert(Tables.TABLE_TABLE, null, values);

        return tableId;
    }

    /*
    * get single table
    */
    public Table getTable(long table_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TABLE + " WHERE "
                + TableContract.TableDB.COLUMN_NAME_TABLE_ID + " = " + table_id;

        Log.e(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Table table = new Table();
        table.setTableID(c.getInt(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_ID)));
        table.setTableGroup(c.getInt(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID)));
        table.setStatus((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_STATUS))));
        table.setTableName(c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_TABLE_NAME)));
        table.setValue((c.getString(c.getColumnIndex(TableContract.TableDB.COLUMN_NAME_VALUE))));

        return table;
    }

    /*
    * Updating a table
    */
    public int updateTable (Table table) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableContract.TableDB.COLUMN_NAME_GROUP_TABLE_ID, table.getBelongingGroup().getTableGroupID());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_NAME, table.getTableName());
        values.put(TableContract.TableDB.COLUMN_NAME_TABLE_STATUS, table.getStatus());
        values.put(TableContract.TableDB.COLUMN_NAME_VALUE, table.getValue());

        // updating row
        return db.update(Tables.TABLE_TABLE, values, TableContract.TableDB.COLUMN_NAME_TABLE_ID + " = ?",
                new String[] { String.valueOf(table.getTableID()) });
    }

}
