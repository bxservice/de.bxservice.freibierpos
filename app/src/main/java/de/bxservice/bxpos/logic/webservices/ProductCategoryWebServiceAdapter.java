package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;

/**
 * This class brings the product category data from iDempiere
 * Created by Diego Ruiz on 4/11/15.
 */
public class ProductCategoryWebServiceAdapter extends AbstractWSObject{

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryProductCategory";

    List<ProductCategory> productCategoryList;

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
        productCategoryList = new ArrayList<ProductCategory>();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e("Error ws response", response.getErrorMessage());
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());
                String categoryName;
                int categoryID;

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));
                    categoryName = null;
                    categoryID = 0;

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        if("Name".equalsIgnoreCase(field.getColumn()))
                            categoryName = field.getValue();
                        else if (ProductCategory.M_Product_Category_ID.equalsIgnoreCase(field.getColumn()))
                            categoryID = Integer.valueOf(field.getValue());

                    }

                    if(categoryName != null &&  categoryID!= 0){
                        ProductCategory p = new ProductCategory(categoryID, categoryName);
                        productCategoryList.add(p);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ProductCategory> getProductCategoryList() {
        return productCategoryList;
    }


}
