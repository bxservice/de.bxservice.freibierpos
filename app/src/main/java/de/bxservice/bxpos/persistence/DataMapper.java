package de.bxservice.bxpos.persistence;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;

import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.persistence.helper.PosOrderHelper;
import de.bxservice.bxpos.persistence.helper.PosOrderLineHelper;
import de.bxservice.bxpos.persistence.helper.PosProductCategoryHelper;
import de.bxservice.bxpos.persistence.helper.PosProductHelper;
import de.bxservice.bxpos.persistence.helper.PosProductPriceHelper;
import de.bxservice.bxpos.persistence.helper.PosTableGroupHelper;
import de.bxservice.bxpos.persistence.helper.PosTableHelper;
import de.bxservice.bxpos.persistence.helper.PosUserHelper;

/**
 * This class is used to map the data into the database
 * Created by Diego Ruiz on 23/12/15.
 */
public class DataMapper implements Serializable {

    static final String LOG_TAG = "Data Mapper";

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

        if(object instanceof PosUser)
            success = createPosUser((PosUser) object);
        if(object instanceof POSOrder)
            success = createPosOrder((POSOrder) object);
        if(object instanceof POSOrderLine)
            success = createPosOrderLine((POSOrderLine) object);
        if(object instanceof Table)
            success = createTable((Table) object);
        if(object instanceof TableGroup)
            success = createTableGroup((TableGroup) object);
        if(object instanceof ProductCategory)
            success = createProductCategory((ProductCategory) object);
        if(object instanceof ProductPrice)
            success = createProductPrice((ProductPrice) object);
        if(object instanceof MProduct)
            success = createProduct((MProduct) object);

        return success;
    }

    /**
     * Generic method that receives all the requests to save data
     * and manage it to redirect it to the corresponding method
     * @param object
     * @return
     */
    public boolean update(Object object) {

        if(object instanceof PosUser)
            success = updatePosUser((PosUser) object);
        if(object instanceof POSOrder)
            success = updatePosOrder((POSOrder) object);
        if(object instanceof POSOrderLine)
            success = updatePosOrderLine((POSOrderLine) object);
        if(object instanceof Table)
            success = updateTable((Table) object);
        /*if(object instanceof TableGroup)
            success = createTableGroup((TableGroup) object);
        if(object instanceof ProductCategory)
            success = createProductCategory((ProductCategory) object);
        if(object instanceof ProductPrice)
            success = createProductPrice((ProductPrice) object);
        if(object instanceof MProduct)
            success = createProduct((MProduct) object);*/

        return success;
    }


    public PosUser getUser(long id) {
        PosUserHelper posUserHelper = new PosUserHelper(mContext);
        return posUserHelper.getUser(id);
    }

    private boolean createPosOrder(POSOrder order) {

        PosOrderHelper orderHelper = new PosOrderHelper(mContext);

        long orderId = orderHelper.createOrder(order);

        if (orderId != -1) {
            Log.i(LOG_TAG, "order created");
            for (POSOrderLine orderLine: order.getOrderLines()) {
                orderLine.getOrder().setOrderId(orderId);
                createPosOrderLine(orderLine);
            }
        }
        else {
            Log.e(LOG_TAG, "Cannot create order");
            orderHelper.closeDB();
            return false;
        }

        orderHelper.closeDB();
        return true;
    }

    private boolean createPosOrderLine(POSOrderLine orderLine) {

        PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);

        if (orderLineHelper.createOrderLine(orderLine) == -1) {
            Log.e(LOG_TAG, "Cannot create order line");
            orderLineHelper.closeDB();
            return false;
        }
        Log.i(LOG_TAG, "order line created");
        orderLineHelper.closeDB();
        return true;
    }

    private boolean createPosUser(PosUser user) {

        PosUserHelper posUserHelper = new PosUserHelper(mContext);

        if (posUserHelper.createUser(user) == -1) {
            Log.e(LOG_TAG, "Cannot create user " + user.getUsername());
            posUserHelper.closeDB();
            return false;
        }
        Log.i(LOG_TAG, user.getUsername() + " created");
        posUserHelper.closeDB();
        return true;
    }

    private boolean createTable(Table table) {

        PosTableHelper tableHelper = new PosTableHelper(mContext);

        if (tableHelper.createTable(table) == -1) {
            Log.e(LOG_TAG, "Cannot create table " + table.getTableName());
            tableHelper.closeDB();
            return false;
        }
        Log.i(LOG_TAG, table.getTableName() + " created");
        tableHelper.closeDB();
        return true;
    }

    private boolean createTableGroup(TableGroup tableGroup) {

        PosTableGroupHelper tableGroupHelper = new PosTableGroupHelper(mContext);

        if (tableGroupHelper.createTableGroup(tableGroup) == -1) {
            Log.e(LOG_TAG, "Cannot create table group " + tableGroup.getName());
            tableGroupHelper.closeDB();
            return false;
        }
        Log.i(LOG_TAG, tableGroup.getName() + " created");
        tableGroupHelper.closeDB();
        return true;
    }

    private boolean createProductCategory(ProductCategory productCategory) {

        PosProductCategoryHelper productCategoryHelper = new PosProductCategoryHelper(mContext);

        if (productCategoryHelper.createProductCategory(productCategory) == -1) {
            Log.e(LOG_TAG, "Cannot create category " + productCategory.getName());
            productCategoryHelper.closeDB();
            return false;
        }
        Log.i(LOG_TAG, productCategory.getName() + " created");
        productCategoryHelper.closeDB();
        return true;
    }

    private boolean createProductPrice(ProductPrice productPrice) {

        PosProductPriceHelper productPriceHelper = new PosProductPriceHelper(mContext);

        if (productPriceHelper.createProductPrice(productPrice) == -1) {
            Log.e(LOG_TAG, "Cannot create price for " + productPrice.getProduct().getProductName());
            productPriceHelper.closeDB();
            return false;
        }
        Log.i(LOG_TAG, productPrice.getProduct().getProductName() + " price created");
        productPriceHelper.closeDB();
        return true;
    }

    private boolean createProduct(MProduct product) {

        PosProductHelper productHelper = new PosProductHelper(mContext);

        if (productHelper.createProduct(product) == -1) {
            Log.e(LOG_TAG, "Cannot create product " + product.getProductName());
            productHelper.closeDB();
            return false;
        }
        Log.i(LOG_TAG, product.getProductName() + " created");
        productHelper.closeDB();
        return true;
    }

    private boolean updateTable(Table table) {

        PosTableHelper tableHelper = new PosTableHelper(mContext);

        if (tableHelper.updateTable(table) == -1) {
            Log.e(LOG_TAG, "Cannot update " + table.getTableName());
            tableHelper.closeDB();
            return false;
        }
        Log.i(LOG_TAG, table.getTableName() + " updated");
        tableHelper.closeDB();
        return true;
    }

    private boolean updatePosOrder(POSOrder order) {

        PosOrderHelper orderHelper = new PosOrderHelper(mContext);

        if (orderHelper.updateOrder(order) != -1) {
            Log.i(LOG_TAG, "order updated");
        }
        else {
            Log.e(LOG_TAG, "Cannot update order");
            orderHelper.closeDB();
            return false;
        }

        orderHelper.closeDB();
        return true;
    }

    private boolean updatePosUser(PosUser user) {

        PosUserHelper posUserHelper = new PosUserHelper(mContext);

        if (posUserHelper.updateUser(user) == -1) {
            Log.e(LOG_TAG, "Cannot update user " + user.getUsername());
            posUserHelper.closeDB();
            return false;
        }
        Log.i(LOG_TAG, user.getUsername() + " updated");
        posUserHelper.closeDB();
        return true;
    }

    private boolean updatePosOrderLine(POSOrderLine orderLine) {

        PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);

        if (orderLineHelper.updateOrderLine(orderLine) == -1) {
            Log.e(LOG_TAG, "Cannot update order line");
            orderLineHelper.closeDB();
            return false;
        }
        Log.i(LOG_TAG, "order line updated");
        orderLineHelper.closeDB();
        return true;
    }

}
