package de.bxservice.bxpos.logic.webservices;

import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.CompositeOperationRequest;
import org.idempiere.webservice.client.request.CreateDataRequest;
import org.idempiere.webservice.client.request.SetDocActionRequest;
import org.idempiere.webservice.client.response.CompositeResponse;
import org.idempiere.webservice.client.response.StandardResponse;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;

/**
 * Created by Diego Ruiz on 12/02/16.
 * Class in charge of creating the order
 * line and completing it in iDempiere
 */
public class CreateOrderWebServiceAdapter extends AbstractWSObject {

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "CompositeCreateOrder";
    private static final String DOCUMENT_NO_PREFIX = "POS";
    private boolean success;
    private POSOrder order;

    public CreateOrderWebServiceAdapter(POSOrder order) {
        super(order);
    }


    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        order = (POSOrder) getParameter();
        int C_Order_ID = 0;

        CreateDataRequest createOrder = new CreateDataRequest();
        createOrder.setLogin(getLogin());
        createOrder.setServiceType("CreateSalesOrder");

        DataRow data = new DataRow();
        //TODO: get the real data
        data.addField("C_BPartner_ID", "121"); //TODO: Change
        data.addField("M_Warehouse_ID", "103");
        data.addField("AD_Org_ID", "11");
        data.addField("C_Currency_ID", "102"); //Burned €
        data.addField("C_DocTypeTarget_ID", "135"); //PosOrder
        data.addField("C_DocType_ID", "135"); //PosOrder
        data.addField("Description", order.getOrderRemark());
        data.addField("DocumentNo", DOCUMENT_NO_PREFIX + order.getOrderId());
        data.addField("IsSOTrx", "Y"); //Sales OrderPaymentRule
        data.addField("PaymentRule", "B"); //Cash
        data.addField("M_PriceList_ID", "101"); //TODO: Get the real one
        data.addField("SalesRep_ID", "101"); //TODO: USerName
        createOrder.setDataRow(data);

        WebServiceClient client = getClient();

        try {
            StandardResponse response = client.sendRequest(createOrder);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
                success=false;
                return;
            } else {

                System.out.println("RecordID: " + response.getRecordID());
                System.out.println();

                for (int i = 0; i < response.getOutputFields().getFieldsCount(); i++) {
                    C_Order_ID = Integer.parseInt(response.getOutputFields().getField(i).getValue());
                    System.out.println("Column" + (i + 1) + ": " + response.getOutputFields().getField(i).getColumn() + " = " + response.getOutputFields().getField(i).getValue());
                }
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
            success=false;
            return;
        }

        CompositeOperationRequest compositeOperation = new CompositeOperationRequest();
        compositeOperation.setLogin(getLogin());
        compositeOperation.setServiceType(SERVICE_TYPE);

        for(POSOrderLine orderLine : order.getOrderLines()) {
            CreateDataRequest createOrderLine = new CreateDataRequest();
            createOrderLine.setServiceType("CreateSalesOrderLine");
            DataRow dataLine = new DataRow();
            dataLine.addField("AD_Org_ID", "11");
            dataLine.addField("C_Order_ID", String.valueOf(C_Order_ID));
            dataLine.addField("M_Product_ID", String.valueOf(orderLine.getProduct().getProductID()));
            dataLine.addField("Description", orderLine.getProductRemark());
            dataLine.addField("QtyOrdered", String.valueOf(orderLine.getQtyOrdered()));
            dataLine.addField("QtyEntered", String.valueOf(orderLine.getQtyOrdered()));
            createOrderLine.setDataRow(dataLine);

            compositeOperation.addOperation(createOrderLine);
        }


/*        SetDocActionRequest docAction = new SetDocActionRequest();
        docAction.setDocAction(Enums.DocAction.Complete);
        docAction.setServiceType("DocActionOrderPOS");
        docAction.setRecordIDVariable("@M_Movement.M_Movement_ID");

        compositeOperation.addOperation(docAction);*/


        try {
            CompositeResponse response = client.sendRequest(compositeOperation);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
                success = false;
            } else {
                for (int i = 0; i < response.getResponsesCount(); i++) {
                    if (response.getResponse(i).getStatus() == Enums.WebServiceResponseStatus.Error) {
                        System.out.println(response.getResponse(i).getErrorMessage());
                    } else {
                        System.out.println("order line created succesfully");
                    }
                    System.out.println();
                }
                success = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            success=false;
        }

    }

    public boolean isSuccess() {
        return success;
    }

}
