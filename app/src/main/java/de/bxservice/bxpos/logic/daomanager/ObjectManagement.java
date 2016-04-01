package de.bxservice.bxpos.logic.daomanager;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
interface ObjectManagement {

    boolean update(Object object);
    boolean create(Object object);
    Object get(long id);
    boolean remove(Object object);

}
