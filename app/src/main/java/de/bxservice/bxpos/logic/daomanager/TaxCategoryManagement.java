package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import de.bxservice.bxpos.logic.model.idempiere.TaxCategory;

/**
 * Created by Diego Ruiz on 11/11/16.
 */
public class TaxCategoryManagement extends AbstractObjectManagement {

    public TaxCategoryManagement(Context ctx) {
        super(ctx);
    }

    @Override
    public TaxCategory get(long id){
        return dataMapper.getTaxCategory(id);
    }

    @Override
    public boolean remove(Object object) {
        return true;
    }

}
