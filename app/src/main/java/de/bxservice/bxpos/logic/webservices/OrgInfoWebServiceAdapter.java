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
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import de.bxservice.bxpos.logic.model.idempiere.RestaurantInfo;

/**
 * Created by Diego Ruiz on 27/04/16.
 */
public class OrgInfoWebServiceAdapter extends AbstractWSObject {

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryOrgInfo";
    private RestaurantInfo restaurantInfo;

    public OrgInfoWebServiceAdapter(WebServiceRequestData wsData) {
        super(wsData);
    }

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        QueryDataRequest ws = new QueryDataRequest();
        ws.setWebServiceType(getServiceType());
        ws.setLogin(getLogin());
        ws.setLimit(1); //Only one row

        DataRow data = new DataRow();
        data.addField("AD_Org_ID", getLogin().getOrgID().toString());
        ws.setDataRow(data);

        WebServiceConnection client = getClient();
        restaurantInfo = new RestaurantInfo();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e("Error ws response", response.getErrorMessage());
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        //Org info from iDempiere
                        if ("Address1".equalsIgnoreCase(field.getColumn()))
                            restaurantInfo.setAddress1(field.getStringValue());
                        else if ("Address2".equalsIgnoreCase(field.getColumn()))
                            restaurantInfo.setAddress2(field.getStringValue());
                        else if ("City".equalsIgnoreCase(field.getColumn()))
                            restaurantInfo.setCity(field.getStringValue());
                        else if ("Name".equalsIgnoreCase(field.getColumn()))
                            restaurantInfo.setName(field.getStringValue());
                        else if ("Phone".equalsIgnoreCase(field.getColumn()))
                            restaurantInfo.setPhone(field.getStringValue());
                        else if ("Postal".equalsIgnoreCase(field.getColumn()))
                            restaurantInfo.setPostalCode(field.getStringValue());
                        else if ("Description".equalsIgnoreCase(field.getColumn()))
                            restaurantInfo.setDescription(field.getStringValue());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RestaurantInfo getRestaurantInfo() {
        return restaurantInfo;
    }
}
