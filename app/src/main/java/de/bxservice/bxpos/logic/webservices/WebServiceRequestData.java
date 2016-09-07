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
package de.bxservice.bxpos.logic.webservices;

import android.content.Context;

import java.util.Properties;

import de.bxservice.bxpos.logic.AssetsPropertyReader;
import de.bxservice.bxpos.logic.PosProperties;

/**
 * Created by Diego Ruiz on 6/11/15.
 */
public class WebServiceRequestData {

    private static volatile WebServiceRequestData instance = null;

    private String username        = null;
    private String password        = null;
    private String clientId        = null;
    private String roleId          = null;
    private String orgId           = null;
    private String attemptsNo      = null;
    private String timeout         = null;
    private String attemptsTimeout = null;
    private String urlBase         = null;
    private String warehouseId = null;

    private WebServiceRequestData() {

    }

    public static synchronized WebServiceRequestData getInstance() {
        if (instance == null) {
            instance = new WebServiceRequestData();
        }

        return instance;
    }

    public void readValues(Context context) {
        //Properties reader
        AssetsPropertyReader assetsPropertyReader = new AssetsPropertyReader(context);
        Properties properties = assetsPropertyReader.getProperties();

        //clientId        = properties.getProperty(PosProperties.CLIENT_PROPERTY);
        //roleId          = properties.getProperty(role);
        //orgId           = properties.getProperty(PosProperties.ORG_PROPERTY);
        attemptsNo      = properties.getProperty(PosProperties.ATTEMPTS_PROPERTY);
        timeout         = properties.getProperty(PosProperties.TIMEOUT_PROPERTY);
        attemptsTimeout = properties.getProperty(PosProperties.ATTEMPTS_TIMEOUT_PROPERTY);

    }

    public boolean isDataComplete() {
        if (username != null && !username.isEmpty() &&
                password != null        && !password.isEmpty() &&
                clientId != null        && !clientId.isEmpty() &&
                roleId != null          && !roleId.isEmpty() &&
                orgId != null           && !orgId.isEmpty() &&
                attemptsNo != null      && !attemptsNo.isEmpty() &&
                attemptsNo != null      && !attemptsNo.isEmpty() &&
                timeout != null         && !timeout.isEmpty() &&
                attemptsTimeout != null && !attemptsTimeout.isEmpty() &&
                urlBase != null         && !urlBase.isEmpty())
            return true;

        return false;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getAttemptsNo() {
        return attemptsNo;
    }

    public void setAttemptsNo(String attemptsNo) {
        this.attemptsNo = attemptsNo;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getAttemptsTimeout() {
        return attemptsTimeout;
    }

    public void setAttemptsTimeout(String attemptsTimeout) {
        this.attemptsTimeout = attemptsTimeout;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }
}
