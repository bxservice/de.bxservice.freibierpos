package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.logic.print.POSOutputDevice;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 28/04/16.
 */
public class PosOutputDeviceManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosOutputDeviceManagement(Context ctx) {
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
    public POSOutputDevice get(long id) {
        return dataMapper.getOutputDevice(id);
    }

    public POSOutputDevice getDevice(String target) {
        return dataMapper.getOutputDevice(target);
    }

    @Override
    public boolean remove(Object object) {
        return dataMapper.remove(object);
    }

}
