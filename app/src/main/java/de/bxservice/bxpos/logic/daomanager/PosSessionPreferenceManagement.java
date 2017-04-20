package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

/**
 * Created by Diego Ruiz on 10/19/16.
 */

public class PosSessionPreferenceManagement extends AbstractObjectManagement {

    public PosSessionPreferenceManagement(Context ctx) {
        super(ctx);
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
