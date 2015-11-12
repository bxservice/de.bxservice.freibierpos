package de.bxservice.bxpos.logic.webservices;

import android.content.Context;
import android.util.Log;

import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

/**
 * Created by Diego Ruiz on 12/11/15.
 */
public class AuthenticationWebService extends AbstractWSObject{

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "ReadUser";
    private String username;
    private String password;

    public AuthenticationWebService(Context ctx) {
        super(ctx);
    }

    public AuthenticationWebService(Context ctx, String username) {
        super(ctx, username);
    }


    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        QueryDataRequest ws = new QueryDataRequest();
        ws.setServiceType(getServiceType());
        ws.setLogin(getLogin());
        ws.setLimit(1);  //Limit number of rows

        DataRow data = new DataRow();
        data.addField("EMail", username);
        ws.setDataRow(data);

        WebServiceClient client = getClient();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if ( response.getStatus() == Enums.WebServiceResponseStatus.Error ) {
                System.out.println(response.getErrorMessage());
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));
                    password = null;

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        if( "Password".equalsIgnoreCase(field.getColumn()) )
                            password = field.getValue();

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPassword(){
        return password;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setParameter(String parameter){
        username = parameter;
    }


}
