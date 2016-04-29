package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosOrderManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosOrderManagement(Context ctx) {
        dataMapper = new DataMapper(ctx);
    }

    @Override
    public boolean update(Object object) {
        return dataMapper.update(object);
    }

    @Override
    public boolean create(Object object) {
        return dataMapper.save(object);
    }

    @Override
    public POSOrder get(long id){
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return dataMapper.remove(object);
    }

    public Table getTable(long id){
        return dataMapper.getTable(id);
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

    public POSOrder getPosOrder(Table table) {
        return dataMapper.getOpenPosOrder(table);
    }

    public List<POSOrder> getTableOrders (Table table) {
        return dataMapper.getTableOrders(table);
    }

    public List<POSOrderLine> getPrintKitchenLines(POSOrder order) {
        return dataMapper.getPrintKitchenLines(order);
    }

    public List<POSOrderLine> getPrintBarLines(POSOrder order) {
        return dataMapper.getPrintBarLines(order);
    }
}
