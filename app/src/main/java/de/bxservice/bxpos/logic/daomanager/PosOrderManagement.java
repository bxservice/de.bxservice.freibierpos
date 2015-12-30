package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
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
        return true;
    }

    public Table getTable(long id){
        return dataMapper.getTable(id);
    }

}
