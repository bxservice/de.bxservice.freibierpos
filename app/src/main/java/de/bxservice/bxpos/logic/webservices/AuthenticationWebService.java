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

import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.CompositeOperationRequest;
import org.idempiere.webservice.client.response.CompositeResponse;

/**
 * Created by Diego Ruiz on 12/11/15.
 */
public class AuthenticationWebService extends AbstractWSObject {

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "TestLogin";

    private boolean success;

    public AuthenticationWebService(WebServiceRequestData wsData) {
        super(wsData);
    }

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        CompositeOperationRequest compositeOperation = new CompositeOperationRequest();
        compositeOperation.setLogin(getLogin());
        compositeOperation.setWebServiceType(getServiceType());

        WebServiceConnection client = getClient();

        try {
            CompositeResponse response = client.sendRequest(compositeOperation);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e("Error: ", response.getErrorMessage());
                success = false;
            } else {
                success = true;
            }

        } catch (Exception e) {
            Log.e("Exception: ", e.getMessage());
            success = false;
        }
    }

    public boolean isSuccess() {
        return success;
    }

}
