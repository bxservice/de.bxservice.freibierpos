package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.bxservice.bxpos.logic.model.idempiere.OrgInfo;
import de.bxservice.bxpos.persistence.dbcontract.OrgInfoContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 27/04/16.
 */
public class PosOrgInfoDataHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Default data Helper";

    public PosOrgInfoDataHelper(Context mContext) {
        super(mContext);
    }

    public long createData(OrgInfo data) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_NAME, data.getName());
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_ADDRESS1, data.getAddress1());
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_ADDRESS2, data.getAddress2());
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_CITY, data.getCity());
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_PHONE, data.getPhone());
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_POSTAL, data.getPostalCode());

        // insert row
        return database.insert(Tables.TABLE_ORG_INFO, null, values);
    }

    /*
    * Updating the default data
    */
    public int updateData (OrgInfo data) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_NAME, data.getName());
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_ADDRESS1, data.getAddress1());
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_ADDRESS2, data.getAddress2());
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_CITY, data.getCity());
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_PHONE, data.getPhone());
        values.put(OrgInfoContract.OrgInfoDB.COLUMN_NAME_POSTAL, data.getPostalCode());

        // updating row
        return db.update(Tables.TABLE_ORG_INFO, values, OrgInfoContract.OrgInfoDB.COLUMN_NAME_ORG_INFO_ID + " = ?",
                new String[] { String.valueOf(1) });
    }

    /*
    * get single instance of org info
    */
    public OrgInfo getOrgInfo(long data_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_ORG_INFO + " WHERE "
                + OrgInfoContract.OrgInfoDB.COLUMN_NAME_ORG_INFO_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(data_id) });

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        OrgInfo orgInfo = new OrgInfo();
        orgInfo.setName(c.getString(c.getColumnIndex(OrgInfoContract.OrgInfoDB.COLUMN_NAME_NAME)));
        orgInfo.setAddress1(c.getString(c.getColumnIndex(OrgInfoContract.OrgInfoDB.COLUMN_NAME_ADDRESS1)));
        orgInfo.setAddress2(c.getString(c.getColumnIndex(OrgInfoContract.OrgInfoDB.COLUMN_NAME_ADDRESS2)));
        orgInfo.setCity(c.getString(c.getColumnIndex(OrgInfoContract.OrgInfoDB.COLUMN_NAME_CITY)));
        orgInfo.setPhone(c.getString(c.getColumnIndex(OrgInfoContract.OrgInfoDB.COLUMN_NAME_PHONE)));
        orgInfo.setPostalCode(c.getString(c.getColumnIndex(OrgInfoContract.OrgInfoDB.COLUMN_NAME_POSTAL)));

        c.close();

        return orgInfo;
    }

}
