package de.bxservice.bxpos.logic.webservices;


import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.Tax;
import de.bxservice.bxpos.logic.model.idempiere.TaxCategory;

/**
 * Brings the info about the taxes to be used in the POS
 * Created by Diego Ruiz on 11/11/16.
 */
public class TaxInfoWebServiceAdapter extends AbstractWSObject {

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryTaxInfo";

    private List<TaxCategory> taxCategoryList;

    public TaxInfoWebServiceAdapter(WebServiceRequestData wsData) {
        super(wsData);
    }

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        QueryDataRequest ws = new QueryDataRequest();
        ws.setWebServiceType(SERVICE_TYPE);
        ws.setLogin(getLogin());

        WebServiceConnection client = getClient();

        taxCategoryList = new ArrayList<>();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e("Error ws response", response.getErrorMessage());
                success = false;
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());
                HashMap<Integer, TaxCategory> taxCategoryHashMap = new HashMap<>();
                Tax tax;
                int taxCategoryID = 0;
                String categoryName = null;
                int taxID = 0;
                String taxName = null;
                String postal = null;
                BigDecimal rate = null;

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        if("category_name".equalsIgnoreCase(field.getColumn()))
                            categoryName = field.getStringValue();
                        else if ("category_categoryid".equalsIgnoreCase(field.getColumn()))
                            taxCategoryID = Integer.valueOf(field.getStringValue());
                        else if ("tax_taxid".equalsIgnoreCase(field.getColumn()))
                            taxID = Integer.valueOf(field.getStringValue());
                        else if ("tax_rate".equalsIgnoreCase(field.getColumn()) && !field.getStringValue().isEmpty())
                            rate = new BigDecimal(field.getStringValue());
                        else if ("tax_name".equalsIgnoreCase(field.getColumn()))
                            taxName = field.getStringValue();
                        else if ("Postal".equalsIgnoreCase(field.getColumn()))
                            postal = field.getStringValue();
                    }

                    if (taxCategoryID  != 0 &&  categoryName != null &&
                            taxID != 0 && rate != null && taxName != null) {

                        TaxCategory category = taxCategoryHashMap.get(taxCategoryID);

                        if (category == null) {
                            category = new TaxCategory();
                            category.setName(categoryName);
                            category.setTaxCategoryID(taxCategoryID);
                            taxCategoryHashMap.put(taxCategoryID, category);
                        }

                        tax = new Tax();
                        tax.setName(taxName);
                        tax.setTaxCategoryID(taxCategoryID);
                        tax.setRate(rate);
                        tax.setTaxID(taxID);
                        tax.setPostal(postal);
                        category.getTaxes().add(tax);

                        if (!taxCategoryList.contains(category))
                            taxCategoryList.add(category);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
    }

    public List<TaxCategory> getTaxCategoryList() {
        return taxCategoryList;
    }

}
