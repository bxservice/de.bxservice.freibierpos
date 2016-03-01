package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.persistence.dbcontract.DefaultPosDataContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 1/03/16.
 */
public class PosDefaultDataHelper extends PosObjectHelper {

    static final String LOG_TAG = "Default data Helper";

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

        // insert row
        long dataId = database.insert(Tables.TABLE_DEFAULT_POS_DATA, null, values);

        return dataId;
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

        // updating row
        return db.update(Tables.TABLE_DEFAULT_POS_DATA, values, DefaultPosDataContract.DefaultDataDB.COLUMN_NAME_DEFAULT_DATA_ID + " = ?",
                new String[] { String.valueOf(1) });
    }

}
