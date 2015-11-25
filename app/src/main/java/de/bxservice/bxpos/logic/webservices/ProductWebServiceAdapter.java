package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.MProduct;
import de.bxservice.bxpos.logic.model.ProductCategory;

/**
 * Created by Diego Ruiz on 9/11/15.
 */
public class ProductWebServiceAdapter extends AbstractWSObject{

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryProduct";

    QueryDataRequest ws = new QueryDataRequest();
    List<MProduct> productList;

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

        productList = new ArrayList<MProduct>();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if ( response.getStatus() == Enums.WebServiceResponseStatus.Error ) {
                System.out.println(response.getErrorMessage());
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());
                String productName;
                int categoryID;
                int productId;

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));
                    productId = 0;
                    productName = null;
                    categoryID = 0;

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        if( "Name".equalsIgnoreCase(field.getColumn()) )
                            productName = field.getValue();
                        else if ( MProduct.M_Product_ID.equalsIgnoreCase(field.getColumn()) )
                            productId = Integer.valueOf(field.getValue());
                        else if ( ProductCategory.M_Product_Category_ID.equalsIgnoreCase(field.getColumn()) )
                            categoryID = Integer.valueOf(field.getValue());

                    }

                    if( productName != null && productId != 0 && categoryID != 0 ){
                        MProduct p = new MProduct();
                        p.setProductCategoryId(categoryID);
                        p.setProductID(productId);
                        p.setProductName(productName);
                        productList.add(p);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MProduct> getProductList() {
        return productList;
    }

}
