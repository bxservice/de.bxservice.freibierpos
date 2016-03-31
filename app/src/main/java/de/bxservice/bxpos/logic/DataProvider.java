package de.bxservice.bxpos.logic;

import android.content.Context;

import java.util.List;
import java.util.Locale;

import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.logic.model.report.ReportGenericObject;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Class in charge to provide the necessary objects to the UI
 * it communicates to the database via Data Mapper
 * Created by Diego Ruiz on 30/12/15.
 */
public class DataProvider {

    private DataMapper dataMapper;
    public static final Locale LOCALE = Locale.GERMANY;

    public DataProvider(Context ctx) {
        dataMapper = new DataMapper(ctx);
    }

    public long getTotalTableGroups() {
        return dataMapper.getTotalTableGroups();
    }

    public List<TableGroup> getAllTableGroups() {
        return dataMapper.getAllTableGroups();
    }

    public List<Table> getAllTables() {
        return dataMapper.getAllTables();
    }

    public long getTotalCategories() {
        return dataMapper.getTotalCategories();
    }

    public List<ProductCategory> getAllCategories() {
        return dataMapper.getAllCategories();
    }

    public List<MProduct> getAllProducts() {
        return dataMapper.getAllProducts();
    }

    public List<POSOrder> getAllOpenOrders() {
        return dataMapper.getOpenOrders();
    }

    public List<POSOrder> getUnsynchronizedOrders() {
        return dataMapper.getUnsynchronizedOrders();
    }

    public List<POSOrder> getPaidOrders(long fromDate, long toDate) {
        return dataMapper.getPaidOrders(fromDate, toDate);
    }

    public List<POSOrderLine> getVoidedItems(long fromDate, long toDate) {
        return dataMapper.getVoidedItems(fromDate, toDate);
    }

    public List<ReportGenericObject> getVoidedReportRows(long fromDate, long toDate) {
        return dataMapper.getVoidedReportRows(fromDate, toDate);
    }

    public List<ReportGenericObject> getTableSalesReportRows(long fromDate, long toDate) {
        return dataMapper.getTableSalesReportRows(fromDate, toDate);
    }

    public POSOrder getPosOrder(Table table) {
        return dataMapper.getOpenPosOrder(table);
    }

    public List<POSOrder> getTableOrders (Table table) {
        return dataMapper.getTableOrders(table);
    }

    public DefaultPosData getDefaultData() {
        return dataMapper.getDefaultData(1);
    }

}
