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

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.print.POSOutputDevice;

/**
 * Created by Diego Ruiz on 28/04/16.
 */
public class OutputDeviceWebServiceAdapter extends AbstractWSObject{

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryPOSDevice";

    private List<POSOutputDevice> outputDeviceList;

    public OutputDeviceWebServiceAdapter(WebServiceRequestData wsData) {
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

        WebServiceConnection client = getClient();

        outputDeviceList = new ArrayList<>();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e("Error ws response", response.getErrorMessage());
                success = false;
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());
                POSOutputDevice device;

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));
                    device = new POSOutputDevice();

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        if("BXS_POSOutputDevice_ID".equalsIgnoreCase(field.getColumn()))
                            device.setOutputDeviceId(Integer.parseInt(field.getStringValue()));
                        else if("BXS_DeviceType".equalsIgnoreCase(field.getColumn()))
                            device.setDeviceType(field.getStringValue());
                        else if("BXS_OutputTarget".equalsIgnoreCase(field.getColumn()))
                            device.setDocTarget(field.getStringValue());
                        else if("BXS_ConnectionType".equalsIgnoreCase(field.getColumn()))
                            device.setConnectionType(field.getStringValue());
                        else if("BXS_POSPageWidth".equalsIgnoreCase(field.getColumn()))
                            device.setPageWidth(Integer.parseInt(field.getStringValue()));
                        else if("BXS_PrinterLanguage".equalsIgnoreCase(field.getColumn()))
                            device.setPrinterLanguage(field.getStringValue());
                        else if("PrinterName".equalsIgnoreCase(field.getColumn()))
                            device.setPrinterName(field.getStringValue());
                    }

                    outputDeviceList.add(device);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
    }

    public List<POSOutputDevice> getOutputDeviceList() {
        return outputDeviceList;
    }

}
