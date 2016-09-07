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

import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.CreateDataRequest;
import org.idempiere.webservice.client.response.StandardResponse;

/**
 * Created by Diego Ruiz on 5/23/16.
 */
public class CreateDeviceTokenWebServiceAdapter extends AbstractWSObject {

    private static final String TAG = "DeviceTokenWebService";

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "CreateDeviceToken";
    private boolean success;
    private boolean connectionError;

    public CreateDeviceTokenWebServiceAdapter(String deviceToken) {
        super(deviceToken);
        queryPerformed();
    }


    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        String deviceToken = (String) getParameter();

        CreateDataRequest createToken = new CreateDataRequest();
        createToken.setLogin(getLogin());
        createToken.setWebServiceType(SERVICE_TYPE);

        String orgId = getLogin().getOrgID().toString();

        DataRow data = new DataRow();
        data.addField("AD_Org_ID", orgId);
        data.addField("BXS_DeviceToken", deviceToken);
        createToken.setDataRow(data);

        WebServiceConnection client = getClient();

        try {
            StandardResponse response = client.sendRequest(createToken);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e(TAG, "Error in web service" + response.getErrorMessage());
                success = false;
                connectionError = false;
            } else {
                Log.d(TAG, "RecordID: " + response.getRecordID());
            }

        } catch (WebServiceException e) {
            Log.e(TAG, "No connection to iDempiere error");
            e.printStackTrace();
            success=false;
            connectionError = true;
        } catch (Exception e) {
            e.printStackTrace();
            success=false;
            connectionError = false;
        }

    }

    public boolean isConnectionError() {
        return connectionError;
    }

    public boolean isSuccess() {
        return success;
    }

}
