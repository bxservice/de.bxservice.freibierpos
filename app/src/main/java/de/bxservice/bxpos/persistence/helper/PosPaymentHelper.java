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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSPayment;
import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;
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

        PosUserHelper userHelper = new PosUserHelper(mContext);
        int userId = 0;
        if (mContext != null) {
            SharedPreferences sharedPref = mContext.getSharedPreferences(WebServiceRequestData.DATA_SHARED_PREF, Context.MODE_PRIVATE);
            userId = userHelper.getUserId(sharedPref.getString(WebServiceRequestData.USERNAME_SYNC_PREF, ""));
        }

        ContentValues values = new ContentValues();
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_CREATED_AT, Long.parseLong(getCurrentDate()));
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_CREATED_BY, userId);
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_ORDER_ID, payment.getOrder().getOrderId());
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_AMOUNT, payment.getPaymentAmtInteger());
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_TENDER_TYPE_ID, payment.getTenderType().getC_POSTenderType_ID());

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
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_ORDER_ID, payment.getOrder().getOrderId());
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_AMOUNT, payment.getPaymentAmtInteger());
        values.put(PosPaymentContract.POSPaymentDB.COLUMN_NAME_TENDER_TYPE_ID, payment.getTenderType().getC_POSTenderType_ID());

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
            PosTenderTypeHelper tenderTypeHelper = new PosTenderTypeHelper(mContext);
            do {
                POSPayment posPayment = new POSPayment();
                posPayment.setPaymentId(c.getInt(c.getColumnIndex(PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_ID)));
                posPayment.setOrder(order);
                posPayment.setTenderType(tenderTypeHelper.getPosTenderType(c.getInt(c.getColumnIndex(PosPaymentContract.POSPaymentDB.COLUMN_NAME_TENDER_TYPE_ID))));
                posPayment.setPaymentAmountFromInt(c.getInt(c.getColumnIndex(PosPaymentContract.POSPaymentDB.COLUMN_NAME_PAYMENT_AMOUNT)));

                payments.add(posPayment);
            } while (c.moveToNext());
            c.close();
        }

        return payments;
    }

}
