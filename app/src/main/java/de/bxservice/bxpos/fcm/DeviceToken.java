package de.bxservice.bxpos.fcm;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.logic.daomanager.PosDeviceTokenManagement;

/**
 * Created by Diego Ruiz on 5/23/16.
 */
public class DeviceToken implements Serializable {

    private PosDeviceTokenManagement tokenManager;

    private boolean synchonized;
    private String deviceToken;

    public boolean isSynchonized() {
        return synchonized;
    }

    public void setSynchonized(boolean synchonized) {
        this.synchonized = synchonized;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public boolean createToken(Context ctx) {
        tokenManager = new PosDeviceTokenManagement(ctx);
        return tokenManager.create(this);
    }

    public boolean update(Context ctx) {
        if(tokenManager == null)
            tokenManager = new PosDeviceTokenManagement(ctx);
        return tokenManager.update(this);
    }
}
