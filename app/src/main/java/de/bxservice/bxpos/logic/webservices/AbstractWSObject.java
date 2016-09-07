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

import org.idempiere.webservice.client.base.LoginRequest;
import org.idempiere.webservice.client.net.WebServiceConnection;

/**
 * Created by Diego Ruiz on 4/11/15.
 */
abstract class AbstractWSObject {

    private LoginRequest login;
    private WebServiceConnection client;
    private WebServiceRequestData wsData;
    private Object parameter;

    protected AbstractWSObject() {

        wsData = WebServiceRequestData.getInstance();

        if (wsData.isDataComplete()) {
            initLogin();
            initClient();
            runWebService();
        }
    }

    public AbstractWSObject(Object parameter) {

        wsData = WebServiceRequestData.getInstance();

        if (wsData.isDataComplete()) {
            initLogin();
            initClient();
            setParameter(parameter);
            //runWebService();
        }

    }

    private void initLogin(){
        login = new LoginRequest();

        login.setUser(wsData.getUsername());
        login.setPass(wsData.getPassword());
        login.setClientID(Integer.parseInt(wsData.getClientId()));
        login.setRoleID(Integer.parseInt(wsData.getRoleId()));
        login.setOrgID(Integer.parseInt(wsData.getOrgId()));
        login.setWarehouseID(Integer.parseInt(wsData.getWarehouseId()));
    }

    private void initClient(){
        client = new WebServiceConnection();

        client.setAttempts(Integer.parseInt(wsData.getAttemptsNo()));
        client.setTimeout(Integer.parseInt(wsData.getTimeout()));
        client.setAttemptsTimeout(Integer.parseInt(wsData.getAttemptsTimeout()));
        client.setUrl(wsData.getUrlBase());
        client.setAppName("FreiBier POS");
    }

    protected LoginRequest getLogin() {
        return login;
    }

    protected WebServiceConnection getClient() {
        return client;
    }

    private void runWebService() {
        queryPerformed();
        /*saveRequestResponse();
        printTotal();
        System.out.println();*/
    }

    public abstract String getServiceType();

    protected abstract void queryPerformed();

    private void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    protected Object getParameter() {
        return parameter;
    }

}
