package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.logic.model.idempiere.RestaurantInfo;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 27/04/16.
 */
public class PosOrgInfoManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosOrgInfoManagement(Context ctx) {
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
    public RestaurantInfo get(long id){
        return dataMapper.getOrgInfo(id);
    }

    @Override
    public boolean remove(Object object) {
        return dataMapper.remove(object);
    }


}
