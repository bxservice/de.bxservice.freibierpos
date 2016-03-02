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
import java.util.Locale;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderLineContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosOrderLineHelper extends PosObjectHelper {

    static final String LOG_TAG = "Order Line Helper";

    public PosOrderLineHelper(Context mContext) {
        super(mContext);
    }

    public long createOrderLine(POSOrderLine orderLine) {
        SQLiteDatabase database = getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        ContentValues values = new ContentValues();
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_CREATED_AT, dateFormat.format(date));
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_CREATED_BY, ""); //TODO: Get current user
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID, orderLine.getOrder().getOrderId());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS, orderLine.getLineStatus());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID, orderLine.getProduct().getProductID());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY, orderLine.getQtyOrdered());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENO, orderLine.getLineNo());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK, orderLine.getProductRemark());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT, orderLine.getLineNetAmtInteger());

        // insert row
        long orderLineId = database.insert(Tables.TABLE_POSORDER_LINE, null, values);

        return orderLineId;
    }

    /*
   * get single order line
   */
    public POSOrderLine getOrderLine (long orderline_id) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER_LINE + " WHERE "
                + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID + " = " + orderline_id;

        Log.e(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        PosProductHelper productHelper = new PosProductHelper(mContext);

        POSOrderLine orderLine = new POSOrderLine();
        orderLine.setOrderLineId(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID)));
        orderLine.setLineStatus(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS)));
        orderLine.setProductRemark(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK)));
        orderLine.setQtyOrdered(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY)));
        orderLine.setLineTotalFromInt(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT)));
        orderLine.setProduct(productHelper.getProduct(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID))));

        return orderLine;
    }

    /*
    * Updating a order line
    */
    public int updateOrderLine (POSOrderLine orderLine) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID, orderLine.getOrder().getOrderId());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS, orderLine.getLineStatus());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID, orderLine.getProduct().getProductID());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY, orderLine.getQtyOrdered());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK, orderLine.getProductRemark());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT, orderLine.getLineNetAmtInteger());

        // updating row
        return db.update(Tables.TABLE_POSORDER_LINE, values,PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID + " = ?",
                new String[] { String.valueOf(orderLine.getOrderLineId()) });
    }

    /**
     * Getting all lines belonging to an order
     * @param order
     * @return
     */
    public ArrayList<POSOrderLine> getAllOrderLines(POSOrder order) {
        ArrayList<POSOrderLine> lines = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER_LINE + " orderline " +
                " WHERE orderline." + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID
                + " = ?";

        Log.e(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(order.getOrderId())});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            PosProductHelper productHelper = new PosProductHelper(mContext);
            do {
                POSOrderLine orderLine = new POSOrderLine();
                orderLine.setOrderLineId(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID)));
                orderLine.setOrder(order);
                orderLine.setLineStatus(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS)));
                orderLine.setProductRemark(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK)));
                orderLine.setQtyOrdered(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY)));
                orderLine.setLineTotalFromInt(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT)));
                orderLine.setProduct(productHelper.getProduct(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID))));

                lines.add(orderLine);
            } while (c.moveToNext());
        }

        return lines;
    }

    /**
     * Getting all lines belonging to an order in a
     * status
     * @param order
     * @return
     */
    public ArrayList<POSOrderLine> getAllOrderLines(POSOrder order, String status) {
        ArrayList<POSOrderLine> lines = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_POSORDER_LINE + " orderline " +
                " WHERE orderline." + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID + " = ? AND " +
                " orderline." + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS + " = ?";

        Log.e(LOG_TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[] {String.valueOf(order.getOrderId()), status});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            PosProductHelper productHelper = new PosProductHelper(mContext);
            do {
                POSOrderLine orderLine = new POSOrderLine();
                orderLine.setOrderLineId(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID)));
                orderLine.setOrder(order);
                orderLine.setLineStatus(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS)));
                orderLine.setProductRemark(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK)));
                orderLine.setQtyOrdered(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY)));
                orderLine.setLineTotalFromInt(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT)));
                orderLine.setProduct(productHelper.getProduct(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID))));

                lines.add(orderLine);
            } while (c.moveToNext());
        }

        return lines;
    }

    public int deleteOrderLine(POSOrderLine orderLine)
    {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(Tables.TABLE_POSORDER_LINE, PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID + " = ?", new String[] { String.valueOf(orderLine.getOrderLineId()) });
    }

}