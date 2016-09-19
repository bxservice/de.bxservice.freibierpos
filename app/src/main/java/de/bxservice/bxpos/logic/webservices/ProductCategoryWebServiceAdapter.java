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

import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;

/**
 * This class brings the product category data from iDempiere
 * Created by Diego Ruiz on 4/11/15.
 */
public class ProductCategoryWebServiceAdapter extends AbstractWSObject{

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryProductCategory";

    private List<ProductCategory> productCategoryList;

    public ProductCategoryWebServiceAdapter(WebServiceRequestData wsData) {
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
        productCategoryList = new ArrayList<>();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e("Error ws response", response.getErrorMessage());
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());
                String categoryName;
                int categoryID;
                int outputDeviceID;

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));
                    categoryName = null;
                    categoryID = 0;
                    outputDeviceID = 0;

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        if("Name".equalsIgnoreCase(field.getColumn()))
                            categoryName = field.getStringValue();
                        else if (ProductCategory.M_Product_Category_ID.equalsIgnoreCase(field.getColumn()))
                            categoryID = Integer.valueOf(field.getStringValue());
                        else if ("BXS_POSOutputDevice_ID".equalsIgnoreCase(field.getColumn()) && !field.getStringValue().isEmpty())
                            outputDeviceID = Integer.valueOf(field.getStringValue());

                    }

                    if(categoryName != null &&  categoryID!= 0){
                        ProductCategory p = new ProductCategory(categoryID, categoryName);
                        p.setOutputDeviceId(outputDeviceID);
                        productCategoryList.add(p);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ProductCategory> getProductCategoryList() {
        return productCategoryList;
    }


}
