package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.bxservice.bxpos.logic.print.POSOutputDevice;
import de.bxservice.bxpos.persistence.dbcontract.OutputDeviceContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 28/04/16.
 */
public class PosOutputDeviceHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Output device Helper";

    public PosOutputDeviceHelper(Context mContext) {
        super(mContext);
    }

    public long createData(POSOutputDevice device) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_OUTPUT_DEVICE_ID, device.getOutputDeviceId());
        values.put(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_CONNECTION, device.getConnectionType());
        values.put(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_DEVICE_TYPE, device.getDeviceType());
        values.put(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_PRINTER_LANGUAGE, device.getPrinterLanguage());
        values.put(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_PRINTER_NAME, device.getPrinterName());
        values.put(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_TARGET, device.getDocTarget());

        // insert row
        return database.insert(Tables.TABLE_OUTPUT_DEVICE, null, values);
    }

    /*
    * Updating the default data
    */
    public int updateData (POSOutputDevice device) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_CONNECTION, device.getConnectionType());
        values.put(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_DEVICE_TYPE, device.getDeviceType());
        values.put(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_PRINTER_LANGUAGE, device.getPrinterLanguage());
        values.put(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_PRINTER_NAME, device.getPrinterName());
        values.put(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_TARGET, device.getDocTarget());

        // updating row
        return db.update(Tables.TABLE_OUTPUT_DEVICE, values, OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_OUTPUT_DEVICE_ID + " = ?",
                new String[] { String.valueOf(device.getOutputDeviceId()) });
    }

    /*
    * get device by id
    */
    public POSOutputDevice getDevice(long data_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_OUTPUT_DEVICE + " WHERE "
                + OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_OUTPUT_DEVICE_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(data_id) });

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else {
            Log.i(LOG_TAG, "No output device found");
            return null;
        }

        POSOutputDevice device = new POSOutputDevice();
        device.setOutputDeviceId((int) data_id);
        device.setConnectionType(c.getString(c.getColumnIndex(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_CONNECTION)));
        device.setDeviceType(c.getString(c.getColumnIndex(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_DEVICE_TYPE)));
        device.setDocTarget(c.getString(c.getColumnIndex(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_TARGET)));
        device.setPrinterLanguage(c.getString(c.getColumnIndex(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_PRINTER_LANGUAGE)));
        device.setPrinterName(c.getString(c.getColumnIndex(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_PRINTER_NAME)));

        c.close();

        return device;
    }

    /*
    * get kitchen device if configured
    */
    public POSOutputDevice getDevice(String target) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_OUTPUT_DEVICE + " WHERE "
                + OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_TARGET + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] {target});

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else {
            Log.i(LOG_TAG, "No output device found for the target: " + target);
            return null;
        }

        POSOutputDevice device = new POSOutputDevice();
        device.setOutputDeviceId(c.getInt(c.getColumnIndex(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_OUTPUT_DEVICE_ID)));
        device.setConnectionType(c.getString(c.getColumnIndex(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_CONNECTION)));
        device.setDeviceType(c.getString(c.getColumnIndex(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_DEVICE_TYPE)));
        device.setDocTarget(c.getString(c.getColumnIndex(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_TARGET)));
        device.setPrinterLanguage(c.getString(c.getColumnIndex(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_PRINTER_LANGUAGE)));
        device.setPrinterName(c.getString(c.getColumnIndex(OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_PRINTER_NAME)));

        c.close();

        return device;
    }

}
