package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosOrderHelper extends PosObjectHelper {

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

}
