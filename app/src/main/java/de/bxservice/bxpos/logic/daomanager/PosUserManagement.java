package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosUserManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosUserManagement(Context ctx) {
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
    public PosUser get(long id){
        return dataMapper.getUser(id);
    }

    @Override
    public boolean remove(Object object) {
        return true;
    }

    /**
     * Get the user from the username
     * @param username
     * @return
     */
    public PosUser get(String username) {
        return dataMapper.getUser(username);
    }

}
