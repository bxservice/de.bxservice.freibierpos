package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.print.POSOutputDevice;

/**
 * Created by Diego Ruiz on 28/04/16.
 */
public class OutputDeviceWebServiceAdapter extends AbstractWSObject{

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryPOSDevice";

    private List<POSOutputDevice> outputDeviceList;

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        QueryDataRequest ws = new QueryDataRequest();
        ws.setServiceType(getServiceType());
        ws.setLogin(getLogin());

        WebServiceClient client = getClient();

        outputDeviceList = new ArrayList<>();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e("Error ws response", response.getErrorMessage());
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());
                POSOutputDevice device;

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));
                    device = new POSOutputDevice();

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        if("BXS_POSOutputDevice_ID".equalsIgnoreCase(field.getColumn()))
                            device.setOutputDeviceId(Integer.parseInt(field.getValue()));
                        else if("BXS_DeviceType".equalsIgnoreCase(field.getColumn()))
                            device.setDeviceType(field.getValue());
                        else if("BXS_OutputTarget".equalsIgnoreCase(field.getColumn()))
                            device.setDocTarget(field.getValue());
                        else if("BXS_ConnectionType".equalsIgnoreCase(field.getColumn()))
                            device.setConnectionType(field.getValue());
                        else if("BXS_POSPageWidth".equalsIgnoreCase(field.getColumn()))
                            device.setPageWidth(Integer.parseInt(field.getValue()));
                        else if("BXS_PrinterLanguage".equalsIgnoreCase(field.getColumn()))
                            device.setPrinterLanguage(field.getValue());
                        else if("PrinterName".equalsIgnoreCase(field.getColumn()))
                            device.setPrinterName(field.getValue());
                    }

                    outputDeviceList.add(device);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<POSOutputDevice> getOutputDeviceList() {
        return outputDeviceList;
    }

}
