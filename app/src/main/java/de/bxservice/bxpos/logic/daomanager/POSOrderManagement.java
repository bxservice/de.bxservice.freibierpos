package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class POSOrderManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public POSOrderManagement(Context ctx) {
        dataMapper = new DataMapper(ctx);
    }

    @Override
    public boolean update(Object object) {
        return true;
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

}
