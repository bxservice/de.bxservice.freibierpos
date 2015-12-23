package de.bxservice.bxpos.logic.daomanager;

import java.io.Serializable;

import de.bxservice.bxpos.logic.model.pos.POSOrderLine;

/**
 * This is class is used to control the read and write from the database
 * is a facade
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosOrderLineManagement implements ObjectManagement, Serializable {

    @Override
    public boolean update(Object object) {
        return true;
    }

    @Override
    public boolean create(Object object) {
        return true;
    }

    @Override
    public POSOrderLine get(long id){
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return true;
    }

}
