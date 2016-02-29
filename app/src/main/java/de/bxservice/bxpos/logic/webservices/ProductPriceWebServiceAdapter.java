package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceClient;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;

/**
 * Brings the info about the products - prices
 * and also the default values from the C_POS table
 * Created by Diego Ruiz on 9/11/15.
 */
public class ProductPriceWebServiceAdapter extends AbstractWSObject {

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryProductPrice";

    QueryDataRequest ws = new QueryDataRequest();
    List<ProductPrice> productPriceList;

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
        productPriceList = new ArrayList<>();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e("Error ws response", response.getErrorMessage());
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());
                int priceListVersionId;
                int productId;
                int productPriceId;
                BigDecimal price;

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));
                    priceListVersionId = 0;
                    productId = 0;
                    productPriceId = 0;
                    price = null;
                    DefaultPosData defaultData = DefaultPosData.getInstance();

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        if("M_PriceList_Version_ID".equalsIgnoreCase(field.getColumn()))
                            priceListVersionId = Integer.valueOf(field.getValue());
                        else if (ProductPrice.M_ProductPrice_ID.equalsIgnoreCase(field.getColumn()))
                            productPriceId = Integer.valueOf(field.getValue());
                        else if (MProduct.M_Product_ID.equalsIgnoreCase(field.getColumn()))
                            productId = Integer.valueOf(field.getValue());
                        else if ("PriceStd".equalsIgnoreCase(field.getColumn()))
                            price = new BigDecimal(field.getValue());
                        //Default data from C_POS
                        else if ("C_BPartnerCashTrx_ID".equalsIgnoreCase(field.getColumn()))
                            defaultData.setDefaultBPartner(Integer.valueOf(field.getValue()));
                        else if ("M_PriceList_ID".equalsIgnoreCase(field.getColumn()))
                            defaultData.setDefaultPriceList(Integer.valueOf(field.getValue()));
                        else if ("C_Currency_ID".equalsIgnoreCase(field.getColumn()))
                            defaultData.setDefaultCurrency(Integer.valueOf(field.getValue()));
                        else if ("M_Warehouse_ID".equalsIgnoreCase(field.getColumn()))
                            defaultData.setDefaultWarehouse(Integer.valueOf(field.getValue()));

                    }

                    if(priceListVersionId != 0 &&  productPriceId!= 0 &&
                            productId != 0 && price != null) {
                        ProductPrice p = new ProductPrice();
                        p.setProductID(productId);
                        p.setPriceListVersionID(priceListVersionId);
                        p.setProductPriceID(productPriceId);
                        p.setStdPrice(price);
                        productPriceList.add(p);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ProductPrice> getProductPriceList() {
        return productPriceList;
    }

}
