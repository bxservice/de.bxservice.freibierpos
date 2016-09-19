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
import org.idempiere.webservice.client.request.UpdateDataRequest;
import org.idempiere.webservice.client.response.StandardResponse;

import de.bxservice.bxpos.logic.model.idempiere.Table;

/**
 * Created by Diego Ruiz on 5/24/16.
 */
public class UpdateTableStatusWebServiceAdapter extends AbstractWSObject {

    private static final String TAG = "UpdataTableWS";

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "UpdateBXSTable";
    private boolean success;
    private boolean connectionError;

    public UpdateTableStatusWebServiceAdapter(WebServiceRequestData wsData, Table table) {
        super(wsData, table);
        queryPerformed();
    }


    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        Log.d(TAG, "Sending request to iDempiere");

        Table table = (Table) getParameter();

        UpdateDataRequest updateData = new UpdateDataRequest();
        updateData.setLogin(getLogin());
        updateData.setWebServiceType(SERVICE_TYPE);
        updateData.setRecordID((int) table.getTableID());

        DataRow data = new DataRow();
        Boolean tableStatus = table.getStatus().equals(Table.FREE_STATUS) ? false : true;
        data.addField("BXS_IsBusy", tableStatus.toString());
        updateData.setDataRow(data);

        WebServiceConnection client = getClient();

        try {
            StandardResponse response = client.sendRequest(updateData);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e(TAG, "Error in web service " + response.getErrorMessage());
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