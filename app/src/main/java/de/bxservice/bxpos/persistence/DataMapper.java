package de.bxservice.bxpos.persistence;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.logic.model.report.ReportGenericObject;
import de.bxservice.bxpos.persistence.helper.PosDefaultDataHelper;
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

    private static final String LOG_TAG = "Data Mapper";

    private boolean success = false;
    private transient Context mContext;

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
        if(object instanceof DefaultPosData)
            success = createDefaultData((DefaultPosData) object);

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
        if(object instanceof DefaultPosData)
            success = updateDefaultData((DefaultPosData) object);
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

    /**
     * Generic method that receives all the requests to save data
     * and manage it to redirect it to the corresponding method
     * @param object
     * @return
     */
    public boolean remove(Object object) {

        /*if(object instanceof PosUser)
            success = createPosUser((PosUser) object);*/
        if(object instanceof POSOrder)
            success = removePosOrder((POSOrder) object);
        if(object instanceof POSOrderLine)
            success = removePosOrderLine((POSOrderLine) object);
        /*if(object instanceof Table)
            success = createTable((Table) object);
        if(object instanceof TableGroup)
            success = createTableGroup((TableGroup) object);
        if(object instanceof ProductCategory)
            success = createProductCategory((ProductCategory) object);
        if(object instanceof ProductPrice)
            success = createProductPrice((ProductPrice) object);
        if(object instanceof MProduct)
            success = createProduct((MProduct) object);*/

        return success;
    }

    /**
     * Return User from id
     * @param id
     * @return
     */
    public PosUser getUser(long id) {
        PosUserHelper posUserHelper = new PosUserHelper(mContext);
        return posUserHelper.getUser(id);
    }

    /**
     * Return user from username
     * @param username
     * @return
     */
    public PosUser getUser(String username) {
        PosUserHelper posUserHelper = new PosUserHelper(mContext);
        return posUserHelper.getUser(username);
    }

    private boolean createPosOrder(POSOrder order) {

        PosOrderHelper orderHelper = new PosOrderHelper(mContext);

        long orderId = orderHelper.createOrder(order);

        if (orderId != -1) {
            Log.i(LOG_TAG, "order created");
            for (POSOrderLine orderLine: order.getOrderingLines()) {
                orderLine.getOrder().setOrderId(orderId);
                if(!createPosOrderLine(orderLine))
                    return false;
            }
        }
        else {
            Log.e(LOG_TAG, "Cannot create order");
            return false;
        }

        return true;
    }

    private boolean createPosOrderLine(POSOrderLine orderLine) {

        PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);

        long orderLineId = orderLineHelper.createOrderLine(orderLine);

        if (orderLineId == -1) {
            Log.e(LOG_TAG, "Cannot create order line");
            return false;
        }
        orderLine.setOrderLineId((int) orderLineId);
        Log.i(LOG_TAG, "order line created");
        return true;
    }

    private boolean createPosUser(PosUser user) {

        PosUserHelper posUserHelper = new PosUserHelper(mContext);

        if (posUserHelper.createUser(user) == -1) {
            Log.e(LOG_TAG, "Cannot create user " + user.getUsername());
            return false;
        }
        Log.i(LOG_TAG, user.getUsername() + " created");
        return true;
    }

    private boolean createTable(Table table) {

        PosTableHelper tableHelper = new PosTableHelper(mContext);

        if (tableHelper.createTable(table) == -1) {
            Log.e(LOG_TAG, "Cannot create table " + table.getTableName());
            return false;
        }
        Log.i(LOG_TAG, table.getTableName() + " created");
        return true;
    }

    private boolean createTableGroup(TableGroup tableGroup) {

        PosTableGroupHelper tableGroupHelper = new PosTableGroupHelper(mContext);

        if (tableGroupHelper.createTableGroup(tableGroup) == -1) {
            Log.e(LOG_TAG, "Cannot create table group " + tableGroup.getName());
            return false;
        }
        Log.i(LOG_TAG, tableGroup.getName() + " created");
        return true;
    }

    private boolean createProductCategory(ProductCategory productCategory) {

        PosProductCategoryHelper productCategoryHelper = new PosProductCategoryHelper(mContext);

        if (productCategoryHelper.createProductCategory(productCategory) == -1) {
            Log.e(LOG_TAG, "Cannot create category " + productCategory.getName());
            return false;
        }
        Log.i(LOG_TAG, productCategory.getName() + " created");
        return true;
    }

    private boolean createProductPrice(ProductPrice productPrice) {

        PosProductPriceHelper productPriceHelper = new PosProductPriceHelper(mContext);

        if (productPriceHelper.createProductPrice(productPrice) == -1) {
            Log.e(LOG_TAG, "Cannot create price for " + productPrice.getProduct().getProductName());
            return false;
        }
        Log.i(LOG_TAG, productPrice.getProduct().getProductName() + " price created");
        return true;
    }

    private boolean createProduct(MProduct product) {

        PosProductHelper productHelper = new PosProductHelper(mContext);

        if (productHelper.createProduct(product) == -1) {
            Log.e(LOG_TAG, "Cannot create product " + product.getProductName());
            return false;
        }
        Log.i(LOG_TAG, product.getProductName() + " created");
        return true;
    }

    private boolean updateTable(Table table) {

        PosTableHelper tableHelper = new PosTableHelper(mContext);

        if (tableHelper.updateTable(table) == -1) {
            Log.e(LOG_TAG, "Cannot update " + table.getTableName());
            return false;
        }
        Log.i(LOG_TAG, table.getTableName() + " updated");
        return true;
    }

    private boolean updatePosOrder(POSOrder order) {

        PosOrderHelper orderHelper = new PosOrderHelper(mContext);

        if (orderHelper.updateOrder(order) != -1) {
            Log.i(LOG_TAG, "order updated");
        }
        else {
            Log.e(LOG_TAG, "Cannot update order");
            return false;
        }

        return true;
    }

    private boolean updatePosUser(PosUser user) {

        PosUserHelper posUserHelper = new PosUserHelper(mContext);

        if (posUserHelper.updateUser(user) == -1) {
            Log.e(LOG_TAG, "Cannot update user " + user.getUsername());
            return false;
        }
        Log.i(LOG_TAG, user.getUsername() + " updated");
        return true;
    }

    private boolean updatePosOrderLine(POSOrderLine orderLine) {

        PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);

        if (orderLineHelper.updateOrderLine(orderLine) == -1) {
            Log.e(LOG_TAG, "Cannot update order line");
            return false;
        }
        Log.i(LOG_TAG, "order line updated");
        return true;
    }

    public long getTotalTableGroups() {
        PosTableGroupHelper tableGroupHelper = new PosTableGroupHelper(mContext);

        long numRows = tableGroupHelper.getTotalTableGroups();
        if (numRows == -1) {
            Log.e(LOG_TAG, "No group tables found");
            return 0;
        }
        Log.i(LOG_TAG, numRows + " table group found");
        return numRows;
    }

    public List<TableGroup> getAllTableGroups() {
        PosTableGroupHelper tableGroupHelper = new PosTableGroupHelper(mContext);
        return tableGroupHelper.getAllTableGroups();
    }

    public List<Table> getAllTables() {
        PosTableHelper tableHelper = new PosTableHelper(mContext);
        return tableHelper.getAllTables();
    }

    public Table getTable(long id) {
        PosTableHelper tableHelper = new PosTableHelper(mContext);
        return tableHelper.getTable(id);
    }

    public long getTotalCategories() {
        PosProductCategoryHelper productCategoryHelper = new PosProductCategoryHelper(mContext);

        long numRows = productCategoryHelper.getTotalCategories();
        if (numRows == -1) {
            Log.e(LOG_TAG, "No product categories found");
            return 0;
        }
        Log.i(LOG_TAG, numRows + " product categories found");
        return numRows;
    }

    public List<ProductCategory> getAllCategories() {
        PosProductCategoryHelper productCategoryHelper = new PosProductCategoryHelper(mContext);
        return productCategoryHelper.getAllProductCategories();
    }

    public List<MProduct> getAllProducts() {
        PosProductHelper productHelper = new PosProductHelper(mContext);
        return productHelper.getAllProducts();
    }

    public List<POSOrder> getOpenOrders() {
        PosOrderHelper orderHelper = new PosOrderHelper(mContext);
        return orderHelper.getOpenOrders();
    }

    public List<POSOrder> getUnsynchronizedOrders() {
        PosOrderHelper orderHelper = new PosOrderHelper(mContext);
        return orderHelper.getUnsynchronizedOrders();
    }

    public List<POSOrder> getPaidOrders(long fromDate, long toDate) {
        PosOrderHelper orderHelper = new PosOrderHelper(mContext);
        return orderHelper.getPaidOrders(fromDate, toDate);
    }

    public ProductPrice getProductPriceByProduct(MProduct product) {
        PosProductPriceHelper productPriceHelper = new PosProductPriceHelper(mContext);
        return productPriceHelper.getProductPriceByProduct(product);
    }

    private boolean removePosOrder(POSOrder order) {
        PosOrderHelper orderHelper = new PosOrderHelper(mContext);

        if (orderHelper.deleteOrder(order) != -1) {
            Log.i(LOG_TAG, "order deleted " + order.getOrderId());
        } else {
            Log.e(LOG_TAG, "Cannot delete order");
            return false;
        }
        return true;
    }

    private boolean removePosOrderLine(POSOrderLine orderLine) {

        PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);

        if (orderLineHelper.deleteOrderLine(orderLine) == -1) {
            Log.e(LOG_TAG, "Cannot delete order line");
            return false;
        }
        Log.i(LOG_TAG, "order line deleted " + orderLine.getOrderLineId());
        return true;
    }

    public POSOrder getOpenPosOrder(Table table) {

        PosOrderHelper orderHelper = new PosOrderHelper(mContext);
        return orderHelper.getOrder(table);
    }

    public List<POSOrder> getTableOrders(Table table) {
        PosOrderHelper orderHelper = new PosOrderHelper(mContext);
        return orderHelper.getTableOrders(table);
    }

    private boolean createDefaultData(DefaultPosData defaultPosData) {

        PosDefaultDataHelper posDefaultDataHelper = new PosDefaultDataHelper(mContext);

        if (posDefaultDataHelper.createData(defaultPosData) == -1) {
            Log.e(LOG_TAG, "Cannot create default data ");
            return false;
        }
        Log.i(LOG_TAG, "Default data for web services created");
        return true;
    }

    private boolean updateDefaultData(DefaultPosData data) {

        PosDefaultDataHelper posDefaultDataHelper = new PosDefaultDataHelper(mContext);

        if (posDefaultDataHelper.updateData(data) != -1) {
            Log.i(LOG_TAG, "Default data updated");
        }
        else {
            Log.e(LOG_TAG, "Cannot update default data");
            return false;
        }

        return true;
    }

    /**
     * Return default data if exists
     * @param id
     * @return
     */
    public DefaultPosData getDefaultData(long id) {
        PosDefaultDataHelper posDefaultDataHelper = new PosDefaultDataHelper(mContext);
        return posDefaultDataHelper.getData(id);
    }

    public boolean isTableFree(Table table) {
        PosTableHelper tableHelper = new PosTableHelper(mContext);
        return tableHelper.isTableFree(table);
    }

    /**
     * Use for reports
     * @param fromDate selected initial date
     * @param toDate   selected final date
     * @return         array with all the ordering lines void within the time frame
     */
    public List<POSOrderLine> getVoidedItems(long fromDate, long toDate) {
        PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);
        return orderLineHelper.getVoidedItems(fromDate, toDate);
    }

    public List<ReportGenericObject> getVoidedReportRows(long fromDate, long toDate) {
        PosOrderLineHelper orderLineHelper = new PosOrderLineHelper(mContext);
        return orderLineHelper.getVoidedReportRows(fromDate, toDate);
    }

    public List<ReportGenericObject> getTableSalesReportRows(long fromDate, long toDate) {
        PosOrderHelper orderHelper = new PosOrderHelper(mContext);
        return orderHelper.getTableSalesReportRows(fromDate, toDate);
    }

}
