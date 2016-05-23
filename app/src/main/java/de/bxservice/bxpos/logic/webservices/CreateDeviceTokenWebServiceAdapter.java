package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.CreateDataRequest;
import org.idempiere.webservice.client.response.StandardResponse;

/**
 * Created by Diego Ruiz on 5/23/16.
 */
public class CreateDeviceTokenWebServiceAdapter extends AbstractWSObject {

    private static final String TAG = "DeviceTokenWebService";

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "CreateDeviceToken";
    private boolean success;
    private boolean connectionError;

    public CreateDeviceTokenWebServiceAdapter(String deviceToken) {
        super(deviceToken);
        queryPerformed();
    }


    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        String deviceToken = (String) getParameter();

        CreateDataRequest createToken = new CreateDataRequest();
        createToken.setLogin(getLogin());
        createToken.setWebServiceType(SERVICE_TYPE);

        String orgId = getLogin().getOrgID().toString();

        DataRow data = new DataRow();
        data.addField("AD_Org_ID", orgId);
        data.addField("BXS_DeviceToken", deviceToken);
        createToken.setDataRow(data);

        WebServiceConnection client = getClient();

        try {
            StandardResponse response = client.sendRequest(createToken);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e(TAG, "Error in web service" + response.getErrorMessage());
                success = false;
                connectionError = false;
            } else {
                Log.d(TAG, "RecordID: " + response.getRecordID());
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
