package de.bxservice.bxpos.logic.webservices;

import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.CompositeOperationRequest;
import org.idempiere.webservice.client.request.CreateDataRequest;
import org.idempiere.webservice.client.request.SetDocActionRequest;
import org.idempiere.webservice.client.response.CompositeResponse;

/**
 * Created by Diego Ruiz on 12/02/16.
 * Class in charge of creating the order
 * line and completing it in iDempiere
 */
public class CreateOrderWebServiceAdapter extends AbstractWSObject {

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "CompositeCreateOrder";


    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        CompositeOperationRequest compositeOperation = new CompositeOperationRequest();
        compositeOperation.setLogin(getLogin());
        compositeOperation.setServiceType(SERVICE_TYPE);

        CreateDataRequest createOrder = new CreateDataRequest();
        createOrder.setServiceType("CreateSalesOrder");
        DataRow data = new DataRow();
        //TODO: get the real data
        data.addField("AD_Org_ID", "");
        //C_BPartner_ID -
        //C_Currency_ID if needed burn €
        /**
         * C_DocType_ID
         * Description - might be remark
         * DocumentNo - ???
         * TotalLines - ??? needed
         * IsSOTrx - Y always
         * M_PriceList_ID
         * SalesRep_ID ---> logged user
         *
         */
        data.addField("C_BPartner_ID", "121"); //TODO: Change
        data.addField("AD_Org_ID", "11");
        data.addField("C_DocTypeTarget_ID", "135"); //PosOrder
        data.addField("C_DocType_ID", "135"); //PosOrder
        data.addField("Description", "Test");
        data.addField("DocumentNo", "Android Test");
        data.addField("Totallines", "");
        data.addField("IsSOTrx", "Y");
        data.addField("M_PriceList_ID", "101");
        data.addField("SalesRep_ID", "101");


        createOrder.setDataRow(data);

        /*CreateDataRequest createOrderLine = new CreateDataRequest();
        createOrderLine.setServiceType("CreateMovementLineTest");
        DataRow dataLine = new DataRow();
        dataLine.addField("M_Movement_ID", "@M_Movement.M_Movement_ID");
        dataLine.addField("M_Product_ID", "138");
        dataLine.addField("MovementQty", "1");
        dataLine.addField("M_Locator_ID", "50001");
        dataLine.addField("M_LocatorTo_ID", "50000");
        dataLine.addField("AD_Org_ID", "11");
        createOrderLine.setDataRow(dataLine);

        SetDocActionRequest docAction = new SetDocActionRequest();
        docAction.setDocAction(Enums.DocAction.Complete);
        docAction.setServiceType("DocActionMovementTest");
        docAction.setRecordIDVariable("@M_Movement.M_Movement_ID");*/

        compositeOperation.addOperation(createOrder);
        /*compositeOperation.addOperation(createOrderLine);
        compositeOperation.addOperation(docAction);*/

        WebServiceClient client = getClient();

        try {
            CompositeResponse response = client.sendRequest(compositeOperation);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
            } else {
                for (int i = 0; i < response.getResponsesCount(); i++) {
                    if (response.getResponse(i).getStatus() == Enums.WebServiceResponseStatus.Error) {
                        System.out.println(response.getResponse(i).getErrorMessage());
                    } else {
                        System.out.println("Response: " + response.getResponse(i).getStatus());
                        System.out.println("Request: " + response.getResponse(i).getWebServiceResponseType());
                    }
                    System.out.println();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
