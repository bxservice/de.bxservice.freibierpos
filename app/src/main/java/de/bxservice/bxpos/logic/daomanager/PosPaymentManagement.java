package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.logic.model.pos.POSPayment;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 8/04/16.
 */
public class PosPaymentManagement implements ObjectManagement, Serializable {

    private DataMapper dataMapper;

    public PosPaymentManagement(Context ctx) {
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
    public POSPayment get(long id){
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return dataMapper.remove(object);
    }

}
