package de.bxservice.bxpos.logic;

import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

/**
 * Created by diego on 4/11/15.
 */
public class MProductCategory extends AbstractWSObject{

    QueryDataRequest ws = new QueryDataRequest();
    String temp="";

    @Override
    public String getServiceType() {
        return "QueryProductCategory";
    }

    @Override
    public void queryPerformed() {
        QueryDataRequest ws = new QueryDataRequest();
        ws.setServiceType(getServiceType());
        ws.setLogin(getLogin());
        ws.setLimit(3);

        WebServiceClient client = getClient();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                System.out.println(response.getErrorMessage());
            } else {
                Log.i("info", "Total rows: " + response.getNumRows());
                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {
                    Log.i("info", "Row: " + (i + 1));
                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {
                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());
                        temp += field.getColumn() + " = " + field.getValue() + "\n";
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTemp(){
        return temp;
    }
}
