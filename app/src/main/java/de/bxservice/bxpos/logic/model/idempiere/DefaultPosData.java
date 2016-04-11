package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;

import java.util.Locale;

import de.bxservice.bxpos.logic.daomanager.PosDefaultDataManagement;

/**
 * Default pos data read from iDempiere
 * POS Terminal
 * Created by Diego Ruiz on 29/02/16.
 */
public class DefaultPosData {

    //Manager in charge to communicate with the database - not mixing the model and db layers
    private PosDefaultDataManagement dataManager;

    public static final Locale LOCALE = Locale.GERMANY;

    private int defaultBPartner = 0;
    private int defaultPriceList = 0;
    private int defaultCurrency = 0;
    private int defaultWarehouse = 0;

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

    /**
     * Save data - if the default data was not previously saved it creates it
     * otherwise it updates it
     * @param ctx
     * @return
     */
    public boolean saveData (Context ctx) {
        dataManager = new PosDefaultDataManagement(ctx);

        if (dataManager.get(1) == null)
            return createData(ctx);
        else
            return updateData(ctx);
    }

    private boolean createData (Context ctx) {
        return dataManager.create(this);
    }

    private boolean updateData (Context ctx) {
        return dataManager.update(this);
    }

}
