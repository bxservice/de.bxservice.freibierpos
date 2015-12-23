package de.bxservice.bxpos.persistence;

import android.content.Context;
import android.util.Log;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.persistence.helper.PosOrderHelper;
import de.bxservice.bxpos.persistence.helper.PosOrderLineHelper;

/**
 * This class is used to map the data into the database
 * Created by Diego Ruiz on 23/12/15.
 */
public class DataMapper {

    private boolean success = false;
    private Context mContext;

    public DataMapper(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Generic method that receives all the requests to save data
     * and manage it to redirect it to the corresponding method
     * @param object
     * @return
     */
    public boolean save(Object object) {

        if(object instanceof POSOrder)
            success = createPosOrder((POSOrder) object);

        return success;
    }




    private boolean createPosOrder(POSOrder order) {

        PosOrderHelper orderHelper = new PosOrderHelper(mContext);

        long orderId = orderHelper.createOrder(order);

        if (orderId != -1) {
            Log.i("Order: ", "order created");
            for (POSOrderLine orderLine: order.getOrderLines()) {
                orderLine.getOrder().setOrderId(orderId);
                createPosOrderLine(orderLine);
            }
        }
        else {
            Log.e("Error: ", "Cannot create order");
            orderHelper.closeDB();
            return false;
        }

        orderHelper.closeDB();
        return true;
    }

    private boolean createPosOrderLine(POSOrderLine orderLine) {

        PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);

        if (orderLineHelper.createOrderLine(orderLine) == -1) {
            Log.e("Error: ", "Cannot create order line");
            orderLineHelper.closeDB();
            return false;
        }
        Log.i("OrderLine: ", "order line created");
        orderLineHelper.closeDB();
        return true;
    }

}
