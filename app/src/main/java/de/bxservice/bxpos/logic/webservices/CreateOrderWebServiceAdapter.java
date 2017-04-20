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
import android.util.Log;

import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.CompositeOperationRequest;
import org.idempiere.webservice.client.request.CreateDataRequest;
import org.idempiere.webservice.client.request.SetDocActionRequest;
import org.idempiere.webservice.client.response.CompositeResponse;

import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.idempiere.IOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.logic.model.pos.POSPayment;

/**
 * Created by Diego Ruiz on 12/02/16.
 * Class in charge of creating the order
 * line and completing it in iDempiere
 */
public class CreateOrderWebServiceAdapter extends AbstractWSObject {

    private static final String TAG = "CreateOrderWebService";

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "CompositeCreateOrder";
    private static final String CREATE_ORDER_SERVICE_TYPE = "CreateSalesOrder";
    private static final String CREATE_ORDER_LINE_SERVICE_TYPE = "CreateSalesOrderLine";
    private static final String CREATE_PAYMENT_SERVICE_TYPE = "CreatePosPayment";
    private static final String DOC_ACTION_SERVICE_TYPE = "DocActionOrder";

    private static final String DOCUMENT_NO_PREFIX = "BXS-POS";
    private boolean success;
    private boolean connectionError;
    private String errorMessage = "";
    private Context mContext;

    public CreateOrderWebServiceAdapter(WebServiceRequestData wsData, POSOrder order, Context ctx) {
        super(wsData, order);
        mContext = ctx;
        queryPerformed();
    }


    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {
        
        POSOrder order = (POSOrder) getParameter();

        CompositeOperationRequest compositeOperation = new CompositeOperationRequest();
        compositeOperation.setLogin(getLogin());
        compositeOperation.setWebServiceType(SERVICE_TYPE);

        CreateDataRequest createOrder = new CreateDataRequest();
        createOrder.setWebServiceType(CREATE_ORDER_SERVICE_TYPE);

        String orgId = getLogin().getOrgID().toString();

        DefaultPosData defaultPosData = DefaultPosData.get(mContext);

        DataRow data = new DataRow();

        int C_BPartner_ID;
        if (order.getTable() == null && defaultPosData.getDefaultBPartnerToGo() !=  0)
            C_BPartner_ID = defaultPosData.getDefaultBPartnerToGo();
        else
            C_BPartner_ID = defaultPosData.getDefaultBPartner();

        data.addField("C_BPartner_ID", String.valueOf(C_BPartner_ID));
        data.addField("M_Warehouse_ID", String.valueOf(defaultPosData.getDefaultWarehouse()));
        data.addField("AD_Org_ID", orgId);
        data.addField("C_Currency_ID", String.valueOf(defaultPosData.getDefaultCurrency()));
        data.addField("C_DocTypeTarget_ID", IOrder.DocTypeSO); //PosOrder
        data.addField("C_DocType_ID", IOrder.DocTypeSO); //PosOrder
        data.addField("Description", order.getOrderRemark());
        data.addField("DocumentNo", order.getDocumentNo());
        data.addField("IsSOTrx", "Y"); //Sales Order
        data.addField("PaymentRule", order.getPaymentRule());
        data.addField("M_PriceList_ID", String.valueOf(defaultPosData.getDefaultPriceList()));
        data.addField("IsTaxIncluded", defaultPosData.isTaxIncluded() ? "Y" : "N");

        if (order.getTable() != null)
            data.addField("BAY_Table_ID", String.valueOf(order.getTable().getTableID()));

        createOrder.setDataRow(data);

        compositeOperation.addOperation(createOrder);


        for (POSOrderLine orderLine : order.getOrderedLines()) {
            CreateDataRequest createOrderLine = new CreateDataRequest();
            createOrderLine.setWebServiceType(CREATE_ORDER_LINE_SERVICE_TYPE);
            DataRow dataLine = new DataRow();
            dataLine.addField("AD_Org_ID", orgId);
            dataLine.addField("C_Order_ID", "@C_Order.C_Order_ID");
            dataLine.addField("M_Product_ID", String.valueOf(orderLine.getProduct().getProductID()));
            dataLine.addField("Description", orderLine.getProductRemark());
            dataLine.addField("QtyOrdered", String.valueOf(orderLine.getQtyOrdered()));
            dataLine.addField("QtyEntered", String.valueOf(orderLine.getQtyOrdered()));

            //Send the price to iDempiere - the price to zero for complimentary products
            dataLine.addField("PriceEntered", String.valueOf(orderLine.getPriceActual()));
            //Send these two values to avoid iDempiere of setting the price in the beforeSave method
            dataLine.addField("PriceActual", String.valueOf(orderLine.getPriceActual()));
            dataLine.addField("PriceList", String.valueOf(orderLine.getProduct().getProductPriceValue()));

            createOrderLine.setDataRow(dataLine);

            compositeOperation.addOperation(createOrderLine);
        }

        //If there is surcharge paid and the surcharge is send to iDempiere
        if(order.getSurchargeInteger() != 0 && defaultPosData.getSurchargeId() != 0) {
            CreateDataRequest createOrderLine = new CreateDataRequest();
            createOrderLine.setWebServiceType(CREATE_ORDER_LINE_SERVICE_TYPE);

            DataRow dataLine = new DataRow();
            dataLine.addField("AD_Org_ID", orgId);
            dataLine.addField("C_Order_ID", "@C_Order.C_Order_ID");
            dataLine.addField("C_Charge_ID", String.valueOf(defaultPosData.getSurchargeId()));
            dataLine.addField("PriceEntered", String.valueOf(order.getSurcharge()));
            dataLine.addField("PriceActual", String.valueOf(order.getSurcharge()));
            dataLine.addField("QtyOrdered", String.valueOf(1));
            dataLine.addField("QtyEntered", String.valueOf(1));
            createOrderLine.setDataRow(dataLine);

            compositeOperation.addOperation(createOrderLine);
        }

        //If there is discount applied and the discount is send to iDempiere
        if(order.getDiscountInteger() != 0 && defaultPosData.getDiscountId() != 0) {
            CreateDataRequest createOrderLine = new CreateDataRequest();
            createOrderLine.setWebServiceType(CREATE_ORDER_LINE_SERVICE_TYPE);
            DataRow dataLine = new DataRow();
            dataLine.addField("AD_Org_ID", orgId);
            dataLine.addField("C_Order_ID", "@C_Order.C_Order_ID");
            dataLine.addField("C_Charge_ID", String.valueOf(defaultPosData.getDiscountId()));
            dataLine.addField("PriceEntered", String.valueOf(order.getDiscount().negate()));
            dataLine.addField("PriceActual", String.valueOf(order.getDiscount().negate()));
            dataLine.addField("QtyOrdered", String.valueOf(1));
            dataLine.addField("QtyEntered", String.valueOf(1));
            dataLine.addField("Description", order.getDiscountReason());
            createOrderLine.setDataRow(dataLine);

            compositeOperation.addOperation(createOrderLine);
        }

        if(IOrder.PAYMENTRULE_MixedPOSPayment.equals(order.getPaymentRule())) {

            for(POSPayment payment : order.getPayments()) {
                CreateDataRequest createPayment = new CreateDataRequest();
                createPayment.setWebServiceType(CREATE_PAYMENT_SERVICE_TYPE);
                DataRow dataLine = new DataRow();
                dataLine.addField("AD_Org_ID", orgId);
                dataLine.addField("C_Order_ID", "@C_Order.C_Order_ID");
                dataLine.addField("PayAmt", String.valueOf(payment.getPaymentAmount()));
                dataLine.addField("C_POSTenderType_ID", String.valueOf(payment.getPOSTenderTypeID()));
                dataLine.addField("TenderType", payment.getPaymentTenderType());
                createPayment.setDataRow(dataLine);

                compositeOperation.addOperation(createPayment);
            }
        }

        SetDocActionRequest docAction = new SetDocActionRequest();

        if (POSOrder.VOID_STATUS.equals(order.getStatus()))
            docAction.setDocAction(Enums.DocAction.Void);
        else
            docAction.setDocAction(Enums.DocAction.Complete);

        docAction.setWebServiceType(DOC_ACTION_SERVICE_TYPE);
        docAction.setRecordIDVariable("@C_Order.C_Order_ID");

        compositeOperation.addOperation(docAction);

        WebServiceConnection client = getClient();

        try {
            CompositeResponse response = client.sendRequest(compositeOperation);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e(TAG, "Error in web service" + response.getErrorMessage());
                success = false;
                connectionError = false;
                errorMessage = response.getErrorMessage();
            } else {
                for (int i = 0; i < response.getResponsesCount(); i++) {
                    if (response.getResponse(i).getStatus() == Enums.WebServiceResponseStatus.Error) {
                        Log.e(TAG, "Error in web service" + response.getResponse(i).getErrorMessage());
                    } else {
                        Log.i(TAG, "Web service ran successfully");
                    }
                }
                success = true;
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

    public String getErrorMessage() {
        return errorMessage;
    }
}
