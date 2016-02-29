package de.bxservice.bxpos.logic.model.idempiere;

/**
 * Default pos data read from iDempiere
 * POS Terminal
 * Created by Diego Ruiz on 29/02/16.
 */
public class DefaultPosData {

    private static volatile DefaultPosData instance = null;
    private int defaultBPartner = 0;
    private int defaultPriceList = 0;

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
}
