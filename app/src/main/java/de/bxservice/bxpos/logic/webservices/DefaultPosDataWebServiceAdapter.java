package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;

/**
 * Created by Diego Ruiz on 1/03/16.
 */
public class DefaultPosDataWebServiceAdapter extends AbstractWSObject {

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryPOSData";

    QueryDataRequest ws = new QueryDataRequest();
    DefaultPosData defaultPosData;

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        QueryDataRequest ws = new QueryDataRequest();
        ws.setServiceType(getServiceType());
        ws.setLogin(getLogin());
        ws.setLimit(1); //Only one row

        WebServiceClient client = getClient();
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
                            defaultPosData.setDefaultBPartner(Integer.valueOf(field.getValue()));
                        else if ("M_PriceList_ID".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setDefaultPriceList(Integer.valueOf(field.getValue()));
                        else if ("C_Currency_ID".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setDefaultCurrency(Integer.valueOf(field.getValue()));
                        else if ("M_Warehouse_ID".equalsIgnoreCase(field.getColumn()))
                            defaultPosData.setDefaultWarehouse(Integer.valueOf(field.getValue()));
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
