package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.CompositeOperationRequest;
import org.idempiere.webservice.client.request.CreateDataRequest;
import org.idempiere.webservice.client.request.SetDocActionRequest;
import org.idempiere.webservice.client.response.CompositeResponse;

import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;

/**
 * Created by Diego Ruiz on 12/02/16.
 * Class in charge of creating the order
 * line and completing it in iDempiere
 */
public class CreateOrderWebServiceAdapter extends AbstractWSObject {

    private static final String TAG = "CreateOrderWebService";

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "CompositeCreateOrder";
    private static final String DOCUMENT_NO_PREFIX = "BX*POS";
    private boolean success;
    private boolean connectionError;

    public CreateOrderWebServiceAdapter(POSOrder order) {
        super(order);
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
        compositeOperation.setServiceType(SERVICE_TYPE);

        CreateDataRequest createOrder = new CreateDataRequest();
        createOrder.setServiceType("CreateSalesOrder");

        String orgId = getLogin().getOrgID().toString();

        DefaultPosData defaultPosData = new DataProvider(null).getDefaultData();

        DataRow data = new DataRow();
        data.addField("C_BPartner_ID", String.valueOf(defaultPosData.getDefaultBPartner()));
        data.addField("M_Warehouse_ID", String.valueOf(defaultPosData.getDefaultWarehouse()));
        data.addField("AD_Org_ID", orgId);
        data.addField("C_Currency_ID", String.valueOf(defaultPosData.getDefaultCurrency()));
        data.addField("C_DocTypeTarget_ID", "135"); //PosOrder
        data.addField("C_DocType_ID", "135"); //PosOrder
        data.addField("Description", order.getOrderRemark());
        data.addField("DocumentNo", DOCUMENT_NO_PREFIX + order.getOrderId());
        data.addField("IsSOTrx", "Y"); //Sales OrderPaymentRule
        data.addField("PaymentRule", "B"); //Cash //TODO: Multi payment type
        data.addField("M_PriceList_ID", String.valueOf(defaultPosData.getDefaultPriceList()));
        //data.addField("SalesRep_ID", "101"); //Removed because before save puts the context user that send the ws request
        createOrder.setDataRow(data);

        compositeOperation.addOperation(createOrder);


        for(POSOrderLine orderLine : order.getOrderedLines()) {
            CreateDataRequest createOrderLine = new CreateDataRequest();
            createOrderLine.setServiceType("CreateSalesOrderLine");
            DataRow dataLine = new DataRow();
            dataLine.addField("AD_Org_ID", orgId);
            dataLine.addField("C_Order_ID", "@C_Order.C_Order_ID");
            dataLine.addField("M_Product_ID", String.valueOf(orderLine.getProduct().getProductID()));
            dataLine.addField("Description", orderLine.getProductRemark());
            dataLine.addField("QtyOrdered", String.valueOf(orderLine.getQtyOrdered()));
            dataLine.addField("QtyEntered", String.valueOf(orderLine.getQtyOrdered()));
            createOrderLine.setDataRow(dataLine);

            compositeOperation.addOperation(createOrderLine);
        }

        SetDocActionRequest docAction = new SetDocActionRequest();
        docAction.setDocAction(Enums.DocAction.Complete);
        docAction.setServiceType("DocActionOrder");
        docAction.setRecordIDVariable("@C_Order.C_Order_ID");

        compositeOperation.addOperation(docAction);

        WebServiceClient client = getClient();

        try {
            CompositeResponse response = client.sendRequest(compositeOperation); //TODO: Find the problem that calls iDempiere several times

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e(TAG, "Error in web service" + response.getErrorMessage());
                success = false;
                connectionError = false;
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

}
