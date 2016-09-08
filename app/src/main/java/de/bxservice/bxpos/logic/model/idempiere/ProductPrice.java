/**********************************************************************
 * This file is part of FreiBier POS                                   *
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

import java.math.BigDecimal;

import de.bxservice.bxpos.logic.daomanager.PosProductPriceManagement;

/**
 * Created by Diego Ruiz on 9/11/15.
 */
public class ProductPrice {

    public static final String M_ProductPrice_ID = "M_ProductPrice_ID";

    private int priceListVersionID;
    private int productPriceID;
    private MProduct product;
    private BigDecimal stdPrice;
    private BigDecimal priceLimit;
    private int productID;
    private PosProductPriceManagement productPriceManager;

    public int getPriceListVersionID() {
        return priceListVersionID;
    }

    public void setPriceListVersionID(int priceListVersionID) {
        this.priceListVersionID = priceListVersionID;
    }

    public int getProductPriceID() {
        return productPriceID;
    }

    public void setProductPriceID(int productPriceID) {
        this.productPriceID = productPriceID;
    }

    public MProduct getProduct() {
        return product;
    }

    public void setProduct(MProduct product) {
        this.product = product;
    }

    public BigDecimal getStdPrice() {
        return stdPrice;
    }

    public void setStdPrice(BigDecimal stdPrice) {
        this.stdPrice = stdPrice;
    }

    public BigDecimal getPriceLimit() {
        return priceLimit;
    }

    public void setPriceLimit(BigDecimal priceLimit) {
        this.priceLimit = priceLimit;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    /**
     * Returns the std price of the product
     * in an integer to be save in the database
     * @return
     */
    public Integer getIntegerStdPrice() {
        return stdPrice.multiply(BigDecimal.valueOf(100)).intValue(); //total * 100
    }

    /**
     * Gets an integer value from the db and converts it to a BigDecimal
     * last two digits are decimals
     * @param total
     */
    public void setStdPriceFromInt(Integer total) {
        double doubleValue = (double) total / 100;
        stdPrice = BigDecimal.valueOf(doubleValue);
    }

    /**
     * Returns the price limit of the product
     * in an integer to be save in the database
     * @return
     */
    public Integer getIntegerPriceLimit() {
        return priceLimit.multiply(BigDecimal.valueOf(100)).intValue(); //total * 100
    }

    /**
     * Gets an integer value from the db and converts it to a BigDecimal
     * last two digits are decimals
     * @param total
     */
    public void setPriceLimitFromInt(Integer total) {
        double doubleValue = (double) total / 100;
        priceLimit = BigDecimal.valueOf(doubleValue);
    }

    /**
     * Save if the object does not exist it creates it
     * otherwise it updates it
     * @param ctx
     * @return
     */
    public boolean save(Context ctx) {
        productPriceManager = new PosProductPriceManagement(ctx);

        if (productPriceManager.get(productPriceID) == null)
            return createProductPrice();
        else
            return updateProductPrice();
    }

    private boolean updateProductPrice() {
        return productPriceManager.update(this);
    }

    private boolean createProductPrice() {
        return productPriceManager.create(this);
    }
}
