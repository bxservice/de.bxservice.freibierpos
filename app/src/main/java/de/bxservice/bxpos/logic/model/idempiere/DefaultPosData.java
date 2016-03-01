package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;

import de.bxservice.bxpos.logic.daomanager.PosDefaultDataManagement;

/**
 * Default pos data read from iDempiere
 * POS Terminal
 * Created by Diego Ruiz on 29/02/16.
 */
public class DefaultPosData {

    private static volatile DefaultPosData instance = null;

    //Manager in charge to communicate with the database - not mixing the model and db layers
    private PosDefaultDataManagement dataManager;

    private int defaultBPartner = 0;
    private int defaultPriceList = 0;
    private int defaultCurrency = 0;
    private int defaultWarehouse = 0;

    public static synchronized DefaultPosData getInstance() {
        if (instance == null) {
            instance = new DefaultPosData();
        }
        return instance;
    }


    public int getDefaultBPartner() {
        return defaultBPartner;
    }

    public void setDefaultBPartner(int defaultBPartner) {
        this.defaultBPartner = defaultBPartner;
    }

    public int getDefaultPriceList() {
        return defaultPriceList;
    }

    public void setDefaultPriceList(int defaultPriceList) {
        this.defaultPriceList = defaultPriceList;
    }

    public int getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(int defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public int getDefaultWarehouse() {
        return defaultWarehouse;
    }

    public void setDefaultWarehouse(int defaultWarehouse) {
        this.defaultWarehouse = defaultWarehouse;
    }

    public boolean createData (Context ctx) {
        dataManager = new PosDefaultDataManagement(ctx);
        return dataManager.create(this);
    }

    public boolean updateData (Context ctx) {
        dataManager = new PosDefaultDataManagement(ctx);
        return dataManager.update(this);
    }

}
