package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;

/**
 * Created by Diego Ruiz on 9/11/15.
 */
public class ProductWebServiceAdapter extends AbstractWSObject{

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryProduct";

    private List<MProduct> productList;

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        QueryDataRequest ws = new QueryDataRequest();
        ws.setWebServiceType(getServiceType());
        ws.setLogin(getLogin());

        WebServiceConnection client = getClient();

        productList = new ArrayList<>();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e("Error ws response", response.getErrorMessage());
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());
                String productName;
                int categoryID;
                int productId;
                int outputDeviceID;
                boolean isActive;

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));
                    productId = 0;
                    productName = null;
                    categoryID = 0;
                    outputDeviceID = 0;
                    isActive = false;

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        if("Name".equalsIgnoreCase(field.getColumn()))
                            productName = field.getStringValue();
                        else if (MProduct.M_Product_ID.equalsIgnoreCase(field.getColumn()))
                            productId = Integer.valueOf(field.getStringValue());
                        else if (ProductCategory.M_Product_Category_ID.equalsIgnoreCase(field.getColumn()))
                            categoryID = Integer.valueOf(field.getStringValue());
                        else if ("BXS_POSOutputDevice_ID".equalsIgnoreCase(field.getColumn()) && !field.getStringValue().isEmpty())
                            outputDeviceID = Integer.valueOf(field.getStringValue());
                        else if ("IsActive".equalsIgnoreCase(field.getColumn()))
                            isActive = "Y".equalsIgnoreCase(field.getStringValue());

                    }

                    if(productName != null && productId != 0 && categoryID != 0) {
                        MProduct p = new MProduct();
                        p.setProductCategoryId(categoryID);
                        p.setProductID(productId);
                        p.setProductName(productName);
                        p.setOutputDeviceId(outputDeviceID);
                        p.setActive(isActive);
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
