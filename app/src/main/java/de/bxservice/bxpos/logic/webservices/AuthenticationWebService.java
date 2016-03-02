package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.CompositeOperationRequest;
import org.idempiere.webservice.client.response.CompositeResponse;

/**
 * Created by Diego Ruiz on 12/11/15.
 */
public class AuthenticationWebService extends AbstractWSObject{

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "TestLogin";

    private boolean success;

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        CompositeOperationRequest compositeOperation = new CompositeOperationRequest();
        compositeOperation.setLogin(getLogin());
        compositeOperation.setServiceType(getServiceType());

        WebServiceClient client = getClient();

        try {
            CompositeResponse response = client.sendRequest(compositeOperation);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.i("Error: ", response.getErrorMessage());
                success = false;
            } else {
                success = true;
            }

        } catch (Exception e) {
            Log.i("Exception: ", e.getMessage());
            success = false;
        }
    }

    public boolean isSuccess() {
        return success;
    }

}
