package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * This is class is used to control the read and write from the database
 * is a facade
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosOrderLineManagement implements ObjectManagement, Serializable {

    private DataMapper dataMapper;

    public PosOrderLineManagement(Context ctx) {
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
    public POSOrderLine get(long id){
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return dataMapper.remove(object);
    }

    public List<POSOrderLine> getVoidedItems(long fromDate, long toDate) {
        return dataMapper.getVoidedItems(fromDate, toDate);
    }

}
