package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.IOrder;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.report.ReportGenericObject;
import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosOrderHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Order Helper";

    public PosOrderHelper(Context mContext) {
        super(mContext);
    }

    public long createOrder(POSOrder order) {
        SQLiteDatabase database = getWritableDatabase();

        PosUserHelper userHelper = new PosUserHelper(mContext);
        int userId = userHelper.getUserId(WebServiceRequestData.getInstance().getUsername());

        ContentValues values = new ContentValues();
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_AT, Long.parseLong(getCurrentDate()));
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_BY, userId);
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS, order.getStatus());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS, order.getGuestNumber());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK, order.getOrderRemark());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES, order.getTotallinesInteger());
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_PAYMENT_RULE, order.getPaymentRule());

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
        order.setOrderingLines(orderLineHelper.getAllOrderingLines(order));
        order.setOrderedLines(orderLineHelper.getAllOrderedLines(order));

        c.close();

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
        //These two values are only updated when paid
        if (order.getStatus().equals(POSOrder.COMPLETE_STATUS)) {
            values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_SURCHARGE, order.getSurchargeInteger());
            values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_DISCOUNT, order.getDiscountInteger());
        }
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_PAYMENT_RULE, order.getPaymentRule());

        int flag = (order.isSync()) ? 1 : 0;
        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED, flag);

        if(order.getTable() != null)
            values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID, order.getTable().getTableID());

        values.put(PosOrderContract.POSOrderDB.COLUMN_NAME_UPDATED_AT, Long.parseLong(getCurrentDate()));

        // updating row
        return db.update(Tables.TABLE_POSORDER, values, PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID + " = ?",
                new String[] { String.valueOf(order.getOrderId()) });
    }

    /**
     * Getting all orders
     */
    public List<POSOrder> getAllOrders() {
        List<POSOrder> orders = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER;

        Log.d(LOG_TAG, selectQuery);

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
                order.setOrderingLines(orderLineHelper.getAllOrderingLines(order));
                order.setOrderedLines(orderLineHelper.getAllOrderedLines(order));

                // adding to orders list
                orders.add(order);
            } while (c.moveToNext());

            c.close();
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

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(table.getTableID()), POSOrder.SENT_STATUS});

        if (c != null)
            c.moveToFirst();

        c.getCount();

        POSOrder order = new POSOrder();
        order.setOrderId(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID)));
        order.setStatus(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS)));
        order.setGuestNumber(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS)));
        order.setOrderRemark(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK)));
        order.setTotalFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES)));
        order.setTable(table);
        order.setPaymentRule(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_PAYMENT_RULE)));

        PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);
        order.setOrderingLines(orderLineHelper.getAllOrderingLines(order));
        order.setOrderedLines(orderLineHelper.getAllOrderedLines(order));
        order.setCurrentLineNo();

        c.close();

        return order;
    }

    /*
    * get all orders by table
    */
    public ArrayList<POSOrder> getTableOrders (Table table) {
        ArrayList<POSOrder> orders = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER + " WHERE "
                + PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID + " = ? AND "
                + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS + " = ? ";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(table.getTableID()), POSOrder.SENT_STATUS});

        if (c.moveToFirst()) {
            PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);
            do {
                POSOrder order = new POSOrder();
                order.setOrderId(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID)));
                order.setStatus(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS)));
                order.setGuestNumber(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_GUESTS)));
                order.setOrderRemark(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_REMARK)));
                order.setTotalFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES)));
                order.setPaymentRule(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_PAYMENT_RULE)));
                order.setTable(table);
                order.setOrderingLines(orderLineHelper.getAllOrderingLines(order));
                order.setOrderedLines(orderLineHelper.getAllOrderedLines(order));

                Boolean flag = (c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED)) != 0);
                order.setSync(flag);

                order.setCurrentLineNo();

                // adding to orders list
                orders.add(order);
            } while (c.moveToNext());

            c.close();
        }

        return orders;
    }

    /**
     * Get all open orders
     * @return
     */
    public ArrayList<POSOrder> getOpenOrders() {
        ArrayList<POSOrder> orders = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER +
                " WHERE " + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS + " NOT IN (?,?)";


        Log.d(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[] {POSOrder.COMPLETE_STATUS, POSOrder.VOID_STATUS});

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
                order.setOrderingLines(orderLineHelper.getAllOrderingLines(order));
                order.setOrderedLines(orderLineHelper.getAllOrderedLines(order));
                order.setPaymentRule(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_PAYMENT_RULE)));

                Boolean flag = (c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED)) != 0);
                order.setSync(flag);

                order.setCurrentLineNo();

                // adding to orders list
                orders.add(order);
            } while (c.moveToNext());

            c.close();
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


        Log.d(LOG_TAG, selectQuery);

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
                order.setSurchargeFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_SURCHARGE)));
                order.setDiscountFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_DISCOUNT)));
                if(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID) != -1 &&
                        c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)) != 0)
                    order.setTable(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)));
                order.setOrderingLines(orderLineHelper.getAllOrderingLines(order));
                order.setOrderedLines(orderLineHelper.getAllOrderedLines(order));
                order.setPaymentRule(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_PAYMENT_RULE)));

                Boolean flag = (c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED)) != 0);
                order.setSync(flag);

                if(IOrder.PAYMENTRULE_MixedPOSPayment.equals(order.getPaymentRule())) {
                    PosPaymentHelper paymentHelper = new PosPaymentHelper(mContext);
                    order.setPayments(paymentHelper.getAllPayments(order));
                }

                // adding to orders list
                orders.add(order);
            } while (c.moveToNext());

            c.close();
        }

        return orders;
    }

    /**
     * Get all paid orders within a time frame
     * group by tables
     * @param fromDate
     * @param toDate
     * @return
     */
    public ArrayList<ReportGenericObject> getTableSalesReportRows(long fromDate, long toDate) {

        ArrayList<ReportGenericObject> orders = new ArrayList<>();
        StringBuilder selectQuery = new StringBuilder();

        selectQuery.append("SELECT ");
        selectQuery.append(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID + ",");
        selectQuery.append("COUNT(" + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_ID + "), ");
        selectQuery.append("SUM(" + PosOrderContract.POSOrderDB.COLUMN_NAME_TOTALLINES + ") ");
        selectQuery.append("FROM ");
        selectQuery.append(Tables.TABLE_POSORDER);
        selectQuery.append(" WHERE ");
        selectQuery.append(PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS);
        selectQuery.append(" = ? AND ");
        selectQuery.append(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID);
        selectQuery.append(" NOT NULL AND ");
        selectQuery.append(PosOrderContract.POSOrderDB.COLUMN_NAME_UPDATED_AT);
        selectQuery.append(" BETWEEN ? AND ? ");
        selectQuery.append("GROUP BY " + PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID);

        Log.d(LOG_TAG, selectQuery.toString());

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery.toString(), new String[] {String.valueOf(POSOrder.COMPLETE_STATUS), String.valueOf(fromDate), String.valueOf(toDate)});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            PosTableHelper tableHelper = new PosTableHelper(mContext);
            do {
                ReportGenericObject reportObject = new ReportGenericObject();

                if(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID) != -1 &&
                        c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)) != 0)
                    reportObject.setDescription(tableHelper.getTable(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID))).getTableName());
                reportObject.setQuantity(String.valueOf(c.getInt(1))); //SUM Qty
                reportObject.setAmount(c.getInt(2));//SUM Amount

                orders.add(reportObject);
            } while (c.moveToNext());
            c.close();
        }

        return orders;
    }

    /**
     * Get all paid orders within a time frame
     * @param fromDate
     * @param toDate
     * @return array list of paid orders in a time frame
     */
    public ArrayList<POSOrder> getPaidOrders(long fromDate, long toDate) {
        ArrayList<POSOrder> orders = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER +
                " WHERE " + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS + " = ? " +
                " AND " + PosOrderContract.POSOrderDB.COLUMN_NAME_UPDATED_AT + " BETWEEN ? AND ? ";

        Log.d(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(POSOrder.COMPLETE_STATUS), String.valueOf(fromDate), String.valueOf(toDate)});

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
                order.setSurchargeFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_SURCHARGE)));
                order.setDiscountFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_DISCOUNT)));
                order.setPaymentRule(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_PAYMENT_RULE)));
                if(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID) != -1 &&
                        c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)) != 0)
                    order.setTable(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)));
                order.setOrderingLines(orderLineHelper.getAllOrderingLines(order));
                order.setOrderedLines(orderLineHelper.getAllOrderedLines(order));

                Boolean flag = (c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED)) != 0);
                order.setSync(flag);

                order.setCurrentLineNo();

                // adding to orders list
                orders.add(order);
            } while (c.moveToNext());

            c.close();
        }

        return orders;
    }

    /**
     * Get all paid orders within a time frame by the current user
     * @param fromDate
     * @param toDate
     * @return array list of paid orders in a time frame
     */
    public ArrayList<POSOrder> getUserPaidOrders(long fromDate, long toDate) {
        ArrayList<POSOrder> orders = new ArrayList<>();

        PosUserHelper userHelper = new PosUserHelper(mContext);
        int userId = userHelper.getUserId(WebServiceRequestData.getInstance().getUsername());

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER +
                " WHERE " + PosOrderContract.POSOrderDB.COLUMN_NAME_ORDER_STATUS + " = ? " +
                " AND " + PosOrderContract.POSOrderDB.COLUMN_NAME_CREATED_BY + " = ? " +
                " AND " + PosOrderContract.POSOrderDB.COLUMN_NAME_UPDATED_AT + " BETWEEN ? AND ? ";

        Log.d(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(POSOrder.COMPLETE_STATUS), String.valueOf(userId), String.valueOf(fromDate), String.valueOf(toDate)});

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
                order.setSurchargeFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_SURCHARGE)));
                order.setDiscountFromInt(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_DISCOUNT)));
                order.setPaymentRule(c.getString(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_PAYMENT_RULE)));
                if(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID) != -1 &&
                        c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)) != 0)
                    order.setTable(c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_TABLE_ID)));
                order.setOrderingLines(orderLineHelper.getAllOrderingLines(order));
                order.setOrderedLines(orderLineHelper.getAllOrderedLines(order));

                Boolean flag = (c.getInt(c.getColumnIndex(PosOrderContract.POSOrderDB.COLUMN_NAME_SYNCHRONIZED)) != 0);
                order.setSync(flag);

                order.setCurrentLineNo();

                // adding to orders list
                orders.add(order);
            } while (c.moveToNext());

            c.close();
        }

        return orders;
    }

}
