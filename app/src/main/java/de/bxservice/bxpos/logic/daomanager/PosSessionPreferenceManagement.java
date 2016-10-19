package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 10/19/16.
 */

public class PosSessionPreferenceManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosSessionPreferenceManagement(Context ctx) {
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
    public String get(long id){
        return "";
    }

    @Override
    public boolean remove(Object object) {
        return true;
    }

    public String getPreferenceValue(String preferenceName) {
        return dataMapper.getSessionPreferenceValue(preferenceName);
    }

    public void cleanSession() {
        dataMapper.cleanSessionPreferenceData();
    }

}
