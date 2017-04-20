package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;

import java.io.Serializable;
import java.math.BigDecimal;

import de.bxservice.bxpos.logic.daomanager.TaxManagement;

/**
 * Created by Diego Ruiz on 11/11/16.
 */
public class Tax implements Serializable {

    private TaxManagement taxManager;
    private int taxID;
    private String name;
    private int taxCategoryID;
    private BigDecimal rate;
    private String postal;

    public int getTaxID() {
        return taxID;
    }

    public void setTaxID(int taxID) {
        this.taxID = taxID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTaxCategoryID() {
        return taxCategoryID;
    }

    public void setTaxCategoryID(int taxCategoryID) {
        this.taxCategoryID = taxCategoryID;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Integer getIntRate() {
        return rate.multiply(BigDecimal.valueOf(100)).intValue(); //total * 100
    }

    public void setRateFromInt(Integer total) {
        double doubleValue = (double) total / 100;
        rate = BigDecimal.valueOf(doubleValue);
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    /**
     * Is Zero Tax
     * @return true if tax rate is 0
     */
    public boolean isZeroTax()
    {
        return rate.signum() == 0;
    }	//	isZeroTax

    public boolean save(Context ctx) {

        taxManager = new TaxManagement(ctx);

        if (taxManager.get(taxID) == null)
            return createTax();
        else
            return updateTax();
    }

    private boolean updateTax() {
        return taxManager.update(this);
    }

    private boolean createTax() {
        return taxManager.create(this);
    }

    public static Tax getTax(long id, Context ctx) {
        TaxManagement taxManager = new TaxManagement(ctx);
        return taxManager.get(id);
    }

    public static Tax getTax(long taxCategoryID, boolean toGo) {
        TaxManagement taxManager = new TaxManagement(null);
        return taxManager.get(taxCategoryID, toGo);
    }

    /**
     * 	Calculate Tax - no rounding
     *	@param amount amount
     *	@param taxIncluded if true tax is calculated from gross otherwise from net
     *	@param scale scale
     *	@return  tax amount
     */
    public BigDecimal calculateTax (BigDecimal amount, boolean taxIncluded, int scale)
    {
        //	Null Tax
        if (isZeroTax())
            return BigDecimal.ZERO;

        BigDecimal multiplier = rate.divide(BigDecimal.valueOf(100.0), 12, BigDecimal.ROUND_HALF_UP);

        BigDecimal tax;
        if (!taxIncluded)	//	$100 * 6 / 100 == $6 == $100 * 0.06
        {
            tax = amount.multiply (multiplier);
        }
        else			//	$106 - ($106 / (100+6)/100) == $6 == $106 - ($106/1.06)
        {
            multiplier = multiplier.add(BigDecimal.ONE);
            BigDecimal base = amount.divide(multiplier, 12, BigDecimal.ROUND_HALF_UP);
            tax = amount.subtract(base);
        }

        return tax.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }	//	calculateTax

}
