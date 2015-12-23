package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderLineContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by diego on 23/12/15.
 */
public class PosOrderLineHelper extends PosObjectHelper {

    public PosOrderLineHelper(Context mContext) {
        super(mContext);
    }

    public long createOrderLine(POSOrderLine orderLine) {
        SQLiteDatabase database = getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

}