package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 28/12/15.
 */
public class PosProductCategoryManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosProductCategoryManagement(Context ctx) {
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
    public ProductCategory get(long id){
        return dataMapper.getProductCategory(id);
    }

    @Override
    public boolean remove(Object object) {
        return true;
    }

    public long getTotalCategories() {
        return dataMapper.getTotalCategories();
    }

    public List<ProductCategory> getAllCategories() {
        return dataMapper.getAllCategories();
    }
}
