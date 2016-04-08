package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSPayment;
import de.bxservice.bxpos.persistence.dbcontract.PosPaymentContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 8/04/16.
 */
public class PosPaymentHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Payment Helper";

    public PosPaymentHelper(Context mContext) {
        super(mContext);
    }

    public long createPayment(POSPayment payment) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_CREATED_AT, Long.parseLong(getCurrentDate()));
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_CREATED_BY, ""); //TODO: Get current user
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_ORDER_ID, payment.getOrder().getOrderId());
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_AMOUNT, payment.getPaymentAmtInteger());
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_TENDER_TYPE, payment.getPOSTenderTypeID());

        // insert row
        return database.insert(Tables.TABLE_POSPAYMENT, null, values);
    }

    /*
     * Updating a payment
     */
    public int updatePayment(POSPayment payment) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_CREATED_AT, Long.parseLong(getCurrentDate()));
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_CREATED_BY, ""); //TODO: Get current user
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_ORDER_ID, payment.getOrder().getOrderId());
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_AMOUNT, payment.getPaymentAmtInteger());
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_TENDER_TYPE, payment.getPOSTenderTypeID());

        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_UPDATED_AT, Long.parseLong(getCurrentDate()));

        // updating row
        return db.update(Tables.TABLE_POSPAYMENT, values, PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_ID + " = ?",
                new String[] { String.valueOf(payment.getPaymentId()) });
    }

    public int deletePayment(POSPayment payment)
    {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(Tables.TABLE_POSPAYMENT, PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_ID + " = ?",
                new String[] { String.valueOf(payment.getPaymentId()) });
    }

    /**
     * Getting all payments belonging to an order
     * @param order
     * @return
     */
    public ArrayList<POSPayment> getAllPayments(POSOrder order) {
        ArrayList<POSPayment> payments = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSPAYMENT + " payment " +
                " WHERE payment." + PosPaymentContract.POSPaymentDB.COLUMN_NAME_ORDER_ID
                + " = ?";

        Log.d(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(order.getOrderId())});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                POSPayment posPayment = new POSPayment();
                posPayment.setPaymentId(c.getInt(c.getColumnIndex(PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_ID)));
                posPayment.setOrder(order);
                posPayment.setPOSTenderTypeID(c.getInt(c.getColumnIndex(PosPaymentContract.POSPaymentDB.COLUMN_NAME_TENDER_TYPE)));
                posPayment.setPaymentAmountFromInt(c.getInt(c.getColumnIndex(PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_AMOUNT)));

                payments.add(posPayment);
            } while (c.moveToNext());
            c.close();
        }

        return payments;
    }

    /**
     * Returns the current date in format
     * yyyymmddhhmm
     * @return
     */
    private String getCurrentDate() {
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

}
