package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 28/12/15.
 */
public class PosProductManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosProductManagement(Context ctx) {
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
    public MProduct get(long id){
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return true;
    }

    public ProductPrice getProductPrice(MProduct product) {
        return dataMapper.getProductPriceByProduct(product);
    }
}
