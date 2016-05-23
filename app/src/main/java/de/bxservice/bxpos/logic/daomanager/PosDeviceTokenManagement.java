package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.fcm.DeviceToken;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 5/23/16.
 */
public class PosDeviceTokenManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosDeviceTokenManagement(Context ctx) {
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
    public DeviceToken get(long id) {
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return dataMapper.remove(object);
    }

    public DeviceToken getDeviceToken() {
        return dataMapper.getDeviceToken();
    }
}