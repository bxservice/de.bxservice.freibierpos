package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 28/12/15.
 */
public class PosProductPriceManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosProductPriceManagement(Context ctx) {
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
    public ProductPrice get(long id){
        return dataMapper.getProductPrice(id);
    }

    @Override
    public boolean remove(Object object) {
        return true;
    }
}
