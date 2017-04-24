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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;

/**
 * Brings the info about the products - prices
 * and also the default values from the C_POS table
 * Created by Diego Ruiz on 9/11/15.
 */
public class ProductPriceWebServiceAdapter extends AbstractWSObject {

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryProductPrice";

    private List<ProductPrice> productPriceList;

    public ProductPriceWebServiceAdapter(WebServiceRequestData wsData) {
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
        productPriceList = new ArrayList<>();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e("Error ws response", response.getErrorMessage());
                success = false;
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());
                int priceListVersionId;
                int productId;
                int productPriceId;
                BigDecimal price;
                BigDecimal priceLimit;

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));
                    priceListVersionId = 0;
                    productId = 0;
                    productPriceId = 0;
                    price = null;
                    priceLimit = null;

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        if("M_PriceList_Version_ID".equalsIgnoreCase(field.getColumn()))
                            priceListVersionId = Integer.valueOf(field.getStringValue());
                        else if (ProductPrice.M_ProductPrice_ID.equalsIgnoreCase(field.getColumn()))
                            productPriceId = Integer.valueOf(field.getStringValue());
                        else if (MProduct.M_Product_ID.equalsIgnoreCase(field.getColumn()))
                            productId = Integer.valueOf(field.getStringValue());
                        else if ("PriceStd".equalsIgnoreCase(field.getColumn()))
                            price = new BigDecimal(field.getStringValue());
                        else if ("PriceLimit".equalsIgnoreCase(field.getColumn()))
                            priceLimit = new BigDecimal(field.getStringValue());

                    }

                    if(priceListVersionId != 0 &&  productPriceId!= 0 &&
                            productId != 0 && price != null) {
                        ProductPrice p = new ProductPrice();
                        p.setProductID(productId);
                        p.setPriceListVersionID(priceListVersionId);
                        p.setProductPriceID(productPriceId);
                        p.setStdPrice(price);
                        p.setPriceLimit(priceLimit);
                        productPriceList.add(p);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
    }

    public List<ProductPrice> getProductPriceList() {
        return productPriceList;
    }

}
