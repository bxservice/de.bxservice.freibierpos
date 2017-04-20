package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import de.bxservice.bxpos.logic.model.idempiere.Tax;

public class TaxManagement extends AbstractObjectManagement {

    public TaxManagement(Context ctx) {
        super(ctx);
    }

    @Override
    public Tax get(long id){
        return dataMapper.getTax(id);
    }

    @Override
    public boolean remove(Object object) {
        return true;
    }

    public Tax get(long taxCategoryID, boolean toGo){
        return dataMapper.getTax(taxCategoryID, toGo);
    }

}
