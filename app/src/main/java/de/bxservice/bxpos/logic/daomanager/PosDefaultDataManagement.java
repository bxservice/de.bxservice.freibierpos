package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 1/03/16.
 */
public class PosDefaultDataManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosDefaultDataManagement(Context ctx) {
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
    public DefaultPosData get(long id){
        return dataMapper.getDefaultData(id);
    }

    @Override
    public boolean remove(Object object) {
        return dataMapper.remove(object);
    }


}
