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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.logic.model.report.ReportGenericObject;
import de.bxservice.bxpos.logic.print.POSOutputDeviceValues;
import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;
import de.bxservice.bxpos.persistence.dbcontract.OutputDeviceContract;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderLineContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductCategoryContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosOrderLineHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Order Line Helper";

    public PosOrderLineHelper(Context mContext) {
        super(mContext);
    }

    public long createOrderLine(POSOrderLine orderLine) {
        SQLiteDatabase database = getWritableDatabase();

        PosUserHelper userHelper = new PosUserHelper(mContext);
        int userId = userHelper.getUserId(WebServiceRequestData.getInstance().getUsername());

        ContentValues values = new ContentValues();
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_CREATED_AT, Long.parseLong(getCurrentDate()));
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_CREATED_BY, userId);
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID, orderLine.getOrder().getOrderId());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS, orderLine.getLineStatus());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID, orderLine.getProduct().getProductID());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY, orderLine.getQtyOrdered());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENO, orderLine.getLineNo());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK, orderLine.getProductRemark());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT, orderLine.getLineNetAmtInteger());

        int flag = (orderLine.isComplimentaryProduct()) ? 1 : 0;
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_COMPLIMENTARY, flag);

        flag = (orderLine.isPrinted()) ? 1 : 0;
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ISPRINTED, flag);

        // insert row
        return database.insert(Tables.TABLE_POSORDER_LINE, null, values);
    }

    /*
   * get single order line
   */
    public POSOrderLine getOrderLine(long orderline_id) {
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

        c.close();

        return orderLine;
    }

    /*
    * Updating a order line
    */
    public int updateOrderLine(POSOrderLine orderLine) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID, orderLine.getOrder().getOrderId());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS, orderLine.getLineStatus());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID, orderLine.getProduct().getProductID());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY, orderLine.getQtyOrdered());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK, orderLine.getProductRemark());
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT, orderLine.getLineNetAmtInteger());

        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_UPDATED_AT, Long.parseLong(getCurrentDate()));

        int flag = (orderLine.isComplimentaryProduct()) ? 1 : 0;
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_COMPLIMENTARY, flag);

        flag = (orderLine.isPrinted()) ? 1 : 0;
        values.put(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ISPRINTED, flag);

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

        Log.d(LOG_TAG, selectQuery);

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

                Boolean flag = (c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_COMPLIMENTARY)) != 0);
                orderLine.setComplimentaryProduct(flag);

                flag = (c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ISPRINTED)) != 0);
                orderLine.setPrinted(flag);

                lines.add(orderLine);
            } while (c.moveToNext());
            c.close();
        }

        return lines;
    }

    /**
     * Returns all the ordering lines from an order
     * @param order
     * @return
     */
    public ArrayList<POSOrderLine> getAllOrderingLines(POSOrder order) {

        String whereClause = " WHERE " + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID + " = ? AND " +
                PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS + " = ?";

        return getAllOrderLines(order, whereClause, new String[] {String.valueOf(order.getOrderId()), POSOrderLine.ORDERING});
    }

    /**
     * Returns all the ordered lines in an order
     * including voided items
     * @param order
     * @return
     */
    public ArrayList<POSOrderLine> getAllOrderedLines(POSOrder order) {

        String whereClause = " WHERE " + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID + " = ? AND " +
                PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS + " IN (?,?)";

        return getAllOrderLines(order, whereClause, new String[] {String.valueOf(order.getOrderId()), POSOrderLine.ORDERED, POSOrderLine.VOIDED});
    }


    /**
     * Getting all lines belonging to an order in a
     * status
     * @param order
     * @return
     */
    private ArrayList<POSOrderLine> getAllOrderLines(POSOrder order, String whereClause, String[] args) {
        ArrayList<POSOrderLine> lines = new ArrayList<>();

        StringBuilder selectQuery = new StringBuilder();

        selectQuery.append("SELECT  * FROM ");
        selectQuery.append(Tables.TABLE_POSORDER_LINE);
        selectQuery.append(whereClause);
        selectQuery.append(" ORDER BY " + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENO);

        Log.d(LOG_TAG, selectQuery.toString());

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery.toString(), args);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            PosProductHelper productHelper = new PosProductHelper(mContext);
            do {
                POSOrderLine orderLine = new POSOrderLine();
                orderLine.setOrderLineId(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID)));
                orderLine.setOrder(order);
                orderLine.setLineStatus(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS)));
                orderLine.setLineNo(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENO)));
                orderLine.setProductRemark(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK)));
                orderLine.setQtyOrdered(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY)));
                orderLine.setLineTotalFromInt(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT)));
                orderLine.setProduct(productHelper.getProduct(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID))));

                Boolean flag = (c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_COMPLIMENTARY)) != 0);
                orderLine.setComplimentaryProduct(flag);

                flag = (c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ISPRINTED)) != 0);
                orderLine.setPrinted(flag);

                lines.add(orderLine);
            } while (c.moveToNext());
            c.close();
        }

        return lines;
    }

    public int deleteOrderLine(POSOrderLine orderLine)
    {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(Tables.TABLE_POSORDER_LINE, PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID + " = ?", new String[] { String.valueOf(orderLine.getOrderLineId()) });
    }

    /**
     * Used for reports
     * @param fromDate
     * @param toDate
     * @return Array list with all the voided lines within the time frame
     */
    public ArrayList<POSOrderLine> getVoidedItems(long fromDate, long toDate) {

        ArrayList<POSOrderLine> lines = new ArrayList<>();
        StringBuilder selectQuery = new StringBuilder();

        selectQuery.append("SELECT  * FROM ");
        selectQuery.append(Tables.TABLE_POSORDER_LINE);
        selectQuery.append(" WHERE ");
        selectQuery.append(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS);
        selectQuery.append(" = ? AND ");
        selectQuery.append(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_UPDATED_AT);
        selectQuery.append(" BETWEEN ? AND ? ");

        Log.d(LOG_TAG, selectQuery.toString());

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery.toString(), new String[] {String.valueOf(POSOrderLine.VOIDED), String.valueOf(fromDate), String.valueOf(toDate)});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            PosProductHelper productHelper = new PosProductHelper(mContext);
            do {
                POSOrderLine orderLine = new POSOrderLine();
                orderLine.setOrderLineId(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID)));
                orderLine.setLineStatus(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS)));
                orderLine.setLineNo(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENO)));
                orderLine.setProductRemark(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK)));
                orderLine.setQtyOrdered(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY)));
                orderLine.setLineTotalFromInt(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT)));
                orderLine.setProduct(productHelper.getProduct(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID))));

                lines.add(orderLine);
            } while (c.moveToNext());
            c.close();
        }

        return lines;
    }

    public ArrayList<POSOrderLine> getPrintKitchenLines(POSOrder order) {
        return getToPrintLines(order, POSOutputDeviceValues.TARGET_KITCHEN);
    }

    public ArrayList<POSOrderLine> getPrintBarLines(POSOrder order) {
        return getToPrintLines(order, POSOutputDeviceValues.TARGET_BAR);
    }

    /**
     *
     * @param order order to be printed
     * @param target printer target = kitchen or bar
     * @return arraylist with the lines to be printed in the target printer
     */
    private ArrayList<POSOrderLine> getToPrintLines(POSOrder order, String target) {

        ArrayList<POSOrderLine> lines = new ArrayList<>();
        StringBuilder selectQuery = new StringBuilder();

        //Check for the product printer
        selectQuery.append("SELECT ol.* FROM ");
        selectQuery.append(Tables.TABLE_POSORDER_LINE + " ol");
        selectQuery.append(" JOIN ");
        selectQuery.append(Tables.TABLE_PRODUCT + " p");
        selectQuery.append(" ON ol." + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID + " = p." + ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID);
        selectQuery.append(" JOIN ");
        selectQuery.append(Tables.TABLE_OUTPUT_DEVICE + " d");
        selectQuery.append(" ON p." + ProductContract.ProductDB.COLUMN_OUTPUT_DEVICE_ID + " = d." + OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_OUTPUT_DEVICE_ID);
        selectQuery.append(" AND d." + OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_TARGET + " =?");
        selectQuery.append(" WHERE ");
        selectQuery.append(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID + " = ?");
        selectQuery.append(" AND " + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ISPRINTED + " = ?");
        selectQuery.append(" UNION ");
        //Check for printer in product category
        selectQuery.append("SELECT  ol.* FROM ");
        selectQuery.append(Tables.TABLE_POSORDER_LINE + " ol");
        selectQuery.append(" JOIN ");
        selectQuery.append(Tables.TABLE_PRODUCT + " p");
        selectQuery.append(" ON ol." + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID + " = p." + ProductContract.ProductDB.COLUMN_NAME_PRODUCT_ID);
        selectQuery.append(" JOIN ");
        selectQuery.append(Tables.TABLE_PRODUCT_CATEGORY + " c");
        selectQuery.append(" ON c." + ProductCategoryContract.ProductCategoryDB.COLUMN_NAME_PRODUCT_CATEGORY_ID + " = p." + ProductContract.ProductDB.COLUMN_NAME_PRODUCT_CATEGORY_ID);
        selectQuery.append(" JOIN ");
        selectQuery.append(Tables.TABLE_OUTPUT_DEVICE + " d");
        selectQuery.append(" ON c." + ProductCategoryContract.ProductCategoryDB.COLUMN_OUTPUT_DEVICE_ID + " = d." + OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_OUTPUT_DEVICE_ID);
        selectQuery.append(" AND d." + OutputDeviceContract.OutputDeviceDB.COLUMN_NAME_TARGET + " =?");
        selectQuery.append(" WHERE ");
        selectQuery.append(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDER_ID + " = ?");
        selectQuery.append(" AND " + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ISPRINTED + " = ?");

        Log.d(LOG_TAG, selectQuery.toString());

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery.toString(), new String[] {target, String.valueOf(order.getOrderId()), String.valueOf(0),
                target, String.valueOf(order.getOrderId()), String.valueOf(0)});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            PosProductHelper productHelper = new PosProductHelper(mContext);
            do {
                POSOrderLine orderLine = new POSOrderLine();
                orderLine.setOrderLineId(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_ID)));
                orderLine.setLineStatus(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS)));
                orderLine.setLineNo(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENO)));
                orderLine.setProductRemark(c.getString(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_REMARK)));
                orderLine.setQtyOrdered(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY)));
                orderLine.setLineTotalFromInt(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT)));
                orderLine.setProduct(productHelper.getProduct(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID))));

                Boolean flag = (c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ISPRINTED)) != 0);
                orderLine.setPrinted(flag);

                orderLine.setOrder(order);

                lines.add(orderLine);
            } while (c.moveToNext());
            c.close();
        }

        return lines;
    }

    public ArrayList<ReportGenericObject> getVoidedReportRows(long fromDate, long toDate) {

        ArrayList<ReportGenericObject> lines = new ArrayList<>();
        StringBuilder selectQuery = new StringBuilder();

        selectQuery.append("SELECT ");
        selectQuery.append(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID + ",");
        selectQuery.append("SUM(" + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_QUANTITY + "), ");
        selectQuery.append("SUM(" + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_LINENETAMT + ") ");
        selectQuery.append("FROM ");
        selectQuery.append(Tables.TABLE_POSORDER_LINE);
        selectQuery.append(" WHERE ");
        selectQuery.append(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_ORDERLINE_STATUS);
        selectQuery.append(" = ? AND ");
        selectQuery.append(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_UPDATED_AT);
        selectQuery.append(" BETWEEN ? AND ? ");
        selectQuery.append("GROUP BY " + PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID);

        Log.d(LOG_TAG, selectQuery.toString());

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery.toString(), new String[] {String.valueOf(POSOrderLine.VOIDED), String.valueOf(fromDate), String.valueOf(toDate)});

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            PosProductHelper productHelper = new PosProductHelper(mContext);
            do {
                ReportGenericObject reportObject = new ReportGenericObject();

                reportObject.setDescription(productHelper.getProduct(c.getInt(c.getColumnIndex(PosOrderLineContract.POSOrderLineDB.COLUMN_NAME_PRODUCT_ID))).getProductName());
                reportObject.setQuantity(String.valueOf(c.getInt(1))); //SUM Qty
                reportObject.setAmount(c.getInt(2));//SUM Amount

                lines.add(reportObject);
            } while (c.moveToNext());
            c.close();
        }

        return lines;
    }

}