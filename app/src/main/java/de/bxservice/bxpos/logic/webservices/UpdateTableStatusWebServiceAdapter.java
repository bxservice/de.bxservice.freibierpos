package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.DataRow;
import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.exceptions.WebServiceException;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.UpdateDataRequest;
import org.idempiere.webservice.client.response.StandardResponse;

import de.bxservice.bxpos.logic.model.idempiere.Table;

/**
 * Created by Diego Ruiz on 5/24/16.
 */
public class UpdateTableStatusWebServiceAdapter extends AbstractWSObject {

    private static final String TAG = "UpdataTableWS";

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "UpdateBXSTable";
    private boolean success;
    private boolean connectionError;

    public UpdateTableStatusWebServiceAdapter(Table table) {
        super(table);
        queryPerformed();
    }


    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        Table table = (Table) getParameter();

        UpdateDataRequest updateData = new UpdateDataRequest();
        updateData.setLogin(getLogin());
        updateData.setWebServiceType(SERVICE_TYPE);
        updateData.setRecordID((int) table.getTableID());

        DataRow data = new DataRow();
        Boolean tableStatus = table.getStatus().equals(Table.FREE_STATUS) ? false : true;
        data.addField("BXS_IsBusy", tableStatus.toString());
        updateData.setDataRow(data);

        WebServiceConnection client = getClient();

        try {
            StandardResponse response = client.sendRequest(updateData);
            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e(TAG, "Error in web service " + response.getErrorMessage());
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