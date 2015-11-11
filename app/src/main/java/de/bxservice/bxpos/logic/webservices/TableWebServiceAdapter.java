package de.bxservice.bxpos.logic.webservices;

import android.content.Context;
import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.Table;

/**
 * Created by Diego Ruiz on 9/11/15.
 */
public class TableWebServiceAdapter extends AbstractWSObject{

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryTable";

    List<Table> tableList;

    public TableWebServiceAdapter(Context ctx) {
        super(ctx);
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

        WebServiceClient client = getClient();

        tableList = new ArrayList<Table>();


        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if ( response.getStatus() == Enums.WebServiceResponseStatus.Error ) {
                System.out.println(response.getErrorMessage());
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());
                String categoryName;
                int categoryID;

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));
                    /*categoryName = null;
                    categoryID = 0;*/

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        /*if( "Name".equalsIgnoreCase(field.getColumn()) )
                            categoryName = field.getValue();
                        else if ( Table.M_Product.equalsIgnoreCase(field.getColumn()) )
                            categoryID = Integer.valueOf(field.getValue());*/

                    }

                   /* if( categoryName != null &&  categoryID!= 0 ){
                        Table p = new Table();
                        tableList.add(p);
                    }*/

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Table> getTableList() {
        return tableList;
    }

    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }
}
