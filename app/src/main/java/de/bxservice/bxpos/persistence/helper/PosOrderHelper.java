package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosOrderHelper extends PosObjectHelper {

    static final String LOG_TAG = "Order Helper";

    public PosOrderHelper(Context mContext) {
        super(mContext);
    }

    public long createOrder(POSOrder order) {
        SQLiteDatabase database = getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ContentValues values = new ContentValues();
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_AT, dateFormat.format(date));
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_BY, ""); //TODO: Get current user
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS, order.getStatus());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID, order.getTable().getTableID());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS, order.getGuestNumber());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK, order.getOrderRemark());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES, order.getTotallinesInteger());

        // insert row
        long orderId = database.insert(Tables.TABLE_POSORDER, null, values);

        return orderId;
    }

    /*
    * get single order
    */
    public POSOrder getOrder (long order_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER + " WHERE "
                + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID + " = " + order_id;

        Log.e(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        POSOrder order = new POSOrder();
        order.setOrderId(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID)));
        order.setStatus(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS)));
        order.setGuestNumber(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS)));
        order.setOrderRemark(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK)));
        order.setTotalFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES)));
        order.setTable(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)));

        PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);
        order.setOrderLines(orderLineHelper.getAllOrderLines(order));

        return order;
    }

    /*
    * Updating an order
    */
    public int updateOrder (POSOrder order) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS, order.getStatus());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID, order.getTable().getTableID());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS, order.getGuestNumber());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK, order.getOrderRemark());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES, order.getTotallinesInteger());

        // updating row
        return db.update(Tables.TABLE_POSORDER, values, PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID + " = ?",
                new String[] { String.valueOf(order.getOrderId()) });
    }

    /**
     * Getting all orders
     */
    public List<POSOrder> getAllOrders() {
        List<POSOrder> orders = new ArrayList<POSOrder>();
        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER;

        Log.e(LOG_TAG, selectQuery);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);
            do {
                POSOrder order = new POSOrder();
                order.setOrderId(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID)));
                order.setStatus(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS)));
                order.setGuestNumber(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS)));
                order.setOrderRemark(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK)));
                order.setTotalFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES)));
                order.setTable(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)));
                order.setOrderLines(orderLineHelper.getAllOrderLines(order));

                // adding to orders list
                orders.add(order);
            } while (c.moveToNext());
        }

        return orders;
    }
}
