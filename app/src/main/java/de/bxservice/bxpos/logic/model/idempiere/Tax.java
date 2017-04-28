/**********************************************************************
 * This file is part of Freibier POS                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
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
