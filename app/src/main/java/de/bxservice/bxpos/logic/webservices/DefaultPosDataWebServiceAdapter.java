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
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;

/**
 * Created by Diego Ruiz on 1/03/16.
 */
public class DefaultPosDataWebServiceAdapter extends AbstractWSObject {

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryPOSData";
    private DefaultPosData defaultPosData;

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

        WebServiceConnection client = getClient();
        defaultPosData = new DefaultPosData();

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

                        //Default data from C_POS
                        if ("C_BPartnerCashTrx_ID".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setDefaultBPartner(Integer.valueOf(field.getStringValue()));
                        else if ("M_PriceList_ID".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setDefaultPriceList(Integer.valueOf(field.getStringValue()));
                        else if ("C_Currency_ID".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setDefaultCurrency(Integer.valueOf(field.getStringValue()));
                        else if ("M_Warehouse_ID".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setDefaultWarehouse(Integer.valueOf(field.getStringValue()));
                        else if ("BXS_POSDiscount_ID".equalsIgnoreCase(field.getColumn()) && !field.getStringValue().isEmpty())
                            defaultPosData.setDiscountId(Integer.valueOf(field.getStringValue()));
                        else if ("BXS_POSSurcharge_ID".equalsIgnoreCase(field.getColumn()) && !field.getStringValue().isEmpty())
                            defaultPosData.setSurchargeId(Integer.valueOf(field.getStringValue()));
                        else if ("BXS_CombineItems".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setCombineItems("Y".equalsIgnoreCase(field.getStringValue()));
                        else if ("BXS_PrintAfterSend".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setPrintAfterSent("Y".equalsIgnoreCase(field.getStringValue()));
                        else if ("PIN".equalsIgnoreCase(field.getColumn())) {
                            try {
                                defaultPosData.setPin(Integer.parseInt(field.getStringValue()));
                            } catch (NumberFormatException e) {
                                Log.e(SERVICE_TYPE, "PIN contains non-numeric values");
                            }
                        }
                        else if ("IsTaxIncluded".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setTaxIncluded("Y".equalsIgnoreCase(field.getStringValue()));
                        else if ("ISO_Code".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setCurrencyIsoCode(field.getStringValue());
                        else if ("AD_Language".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setClientAdLanguage(field.getStringValue());

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DefaultPosData getDefaultPosData() {
        return defaultPosData;
    }
}
