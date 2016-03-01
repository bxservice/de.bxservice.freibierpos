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

import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
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
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS, order.getGuestNumber());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK, order.getOrderRemark());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES, order.getTotallinesInteger());

        if(order.getTable() != null)
            values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID, order.getTable().getTableID());

        int flag = (order.isSync()) ? 1 : 0;
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED, flag);

        // insert row
        long orderId = database.insert(Tables.TABLE_POSORDER, null, values);

        order.setOrderId(orderId);

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
        order.setOrderingLines(orderLineHelper.getAllOrderLines(order, POSOrderLine.ORDERING));
        order.setOrderedLines(orderLineHelper.getAllOrderLines(order, POSOrderLine.ORDERED));

        return order;
    }

    /*
    * Updating an order
    */
    public int updateOrder (POSOrder order) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS, order.getStatus());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS, order.getGuestNumber());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK, order.getOrderRemark());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES, order.getTotallinesInteger());

        int flag = (order.isSync()) ? 1 : 0;
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED, flag);

        if(order.getTable() != null)
            values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID, order.getTable().getTableID());

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
                order.setOrderingLines(orderLineHelper.getAllOrderLines(order, POSOrderLine.ORDERING));
                order.setOrderedLines(orderLineHelper.getAllOrderLines(order, POSOrderLine.ORDERED));

                // adding to orders list
                orders.add(order);
            } while (c.moveToNext());
        }

        return orders;
    }

    public int deleteOrder(POSOrder order) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(Tables.TABLE_POSORDER, PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID + " = ?", new String[] { String.valueOf(order.getOrderId()) });
    }

    /*
    * get single order by table
    */
    public POSOrder getOrder (Table table) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER + " WHERE "
                + PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID + " = ? AND "
                + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS + " = ? ";

        Log.e(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(table.getTableID()), POSOrder.SENT_STATUS});

        if (c != null)
            c.moveToFirst();

        POSOrder order = new POSOrder();
        order.setOrderId(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID)));
        order.setStatus(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS)));
        order.setGuestNumber(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS)));
        order.setOrderRemark(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK)));
        order.setTotalFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES)));
        order.setTable(table);

        PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);
        order.setOrderingLines(orderLineHelper.getAllOrderLines(order, POSOrderLine.ORDERING));
        order.setOrderedLines(orderLineHelper.getAllOrderLines(order, POSOrderLine.ORDERED));

        return order;
    }

    /**
     * Get all open orders
     * @return
     */
    public ArrayList<POSOrder> getOpenOrders() {
        ArrayList<POSOrder> orders = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER +
                " WHERE " + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS + " <> ? ";


        Log.e(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(POSOrder.COMPLETE_STATUS)});

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
                if(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID) != -1 &&
                        c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)) != 0)
                    order.setTable(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)));
                order.setOrderingLines(orderLineHelper.getAllOrderLines(order, POSOrderLine.ORDERING));
                order.setOrderedLines(orderLineHelper.getAllOrderLines(order, POSOrderLine.ORDERED));

                Boolean flag = (c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED)) != 0);
                order.setSync(flag);

                // adding to orders list
                orders.add(order);
            } while (c.moveToNext());
        }

        return orders;
    }

    /**
     * Get all orders that were paid but were not sent to iDempiere
     * @return
     */
    public ArrayList<POSOrder> getUnsynchronizedOrders() {
        ArrayList<POSOrder> orders = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER +
                " WHERE " + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS + " = ? " +
                " AND " + PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED + " = ?" ;


        Log.e(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(POSOrder.COMPLETE_STATUS), Integer.toString(0)});

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
                if(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID) != -1 &&
                        c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)) != 0)
                    order.setTable(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)));
                order.setOrderingLines(orderLineHelper.getAllOrderLines(order, POSOrderLine.ORDERING));
                order.setOrderedLines(orderLineHelper.getAllOrderLines(order, POSOrderLine.ORDERED));

                Boolean flag = (c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED)) != 0);
                order.setSync(flag);

                // adding to orders list
                orders.add(order);
            } while (c.moveToNext());
        }

        return orders;
    }

}
