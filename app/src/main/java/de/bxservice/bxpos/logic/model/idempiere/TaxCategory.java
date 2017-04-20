package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.daomanager.TaxCategoryManagement;

/**
 * Created by Diego Ruiz on 11/11/16.
 */
public class TaxCategory {

    private List<Tax> taxes = new ArrayList<>();
    private int taxCategoryID;
    private String name;
    private TaxCategoryManagement taxCategoryManager;

    public int getTaxCategoryID() {
        return taxCategoryID;
    }

    public void setTaxCategoryID(int taxCategoryID) {
        this.taxCategoryID = taxCategoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<Tax> taxes) {
        this.taxes = taxes;
    }

    public boolean save(Context ctx) {

        //No save in the database if the Product Category does not have products
        if (taxes == null || taxes.isEmpty())
            return false;

        taxCategoryManager = new TaxCategoryManagement(ctx);

        if (taxCategoryManager.get(taxCategoryID) == null)
            return createTaxCategory();
        else
            return updateTaxCategory();
    }

    private boolean updateTaxCategory() {
        return taxCategoryManager.update(this);
    }

    private boolean createTaxCategory() {
        return taxCategoryManager.create(this);
    }
}
