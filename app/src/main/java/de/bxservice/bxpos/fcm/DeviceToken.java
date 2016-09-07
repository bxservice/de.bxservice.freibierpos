/**********************************************************************
 * This file is part of FreiBier POS                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
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
