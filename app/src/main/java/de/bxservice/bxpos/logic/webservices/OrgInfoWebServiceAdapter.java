package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import de.bxservice.bxpos.logic.model.idempiere.OrgInfo;

/**
 * Created by Diego Ruiz on 27/04/16.
 */
public class OrgInfoWebServiceAdapter extends AbstractWSObject {

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryOrgInfo";
    private OrgInfo orgInfo;

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

        DataRow data = new DataRow();
        data.addField("AD_Org_ID", getLogin().getOrgID().toString());
        ws.setDataRow(data);

        WebServiceClient client = getClient();
        orgInfo = new OrgInfo();

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

                        //Org info from iDempiere
                        if ("Address1".equalsIgnoreCase(field.getColumn()))
                            orgInfo.setAddress1(field.getValue());
                        else if ("Address2".equalsIgnoreCase(field.getColumn()))
                            orgInfo.setAddress2(field.getValue());
                        else if ("City".equalsIgnoreCase(field.getColumn()))
                            orgInfo.setCity(field.getValue());
                        else if ("Name".equalsIgnoreCase(field.getColumn()))
                            orgInfo.setName(field.getValue());
                        else if ("Phone".equalsIgnoreCase(field.getColumn()))
                            orgInfo.setPhone(field.getValue());
                        else if ("Postal".equalsIgnoreCase(field.getColumn()))
                            orgInfo.setPostalCode(field.getValue());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OrgInfo getOrgInfo() {
        return orgInfo;
    }
}
