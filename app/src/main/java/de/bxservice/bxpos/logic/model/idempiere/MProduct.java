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

import java.io.Serializable;
import java.math.BigDecimal;

import de.bxservice.bxpos.logic.daomanager.PosProductManagement;

/**
 * This is the product that is read from iDempiere
 * Created by Diego Ruiz on 9/11/15.
 */
public class MProduct implements Serializable {

    public static final String M_Product_ID = "M_Product_ID";

    private int productID;
    private String productName;
    private String productKey;
    private int productCategoryId;
    private int outputDeviceId;
    private boolean isActive;
    private PosProductManagement productManager;

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(int productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public BigDecimal getProductPriceValue() {
        ProductPrice productPrice = getProductPrice(null);
        return productPrice.getStdPrice();
    }

    public boolean isComplimentaryAllow() {
        ProductPrice productPrice = getProductPrice(null);
        return productPrice.getPriceLimit().compareTo(BigDecimal.ZERO) == 0 || productPrice.getPriceLimit().compareTo(BigDecimal.ZERO) < 0;
    }

    public ProductPrice getProductPrice(Context ctx) {
        productManager = new PosProductManagement(ctx);
        return productManager.getProductPrice(this);
    }

    public int getOutputDeviceId() {
        return outputDeviceId;
    }

    public void setOutputDeviceId(int outputDeviceId) {
        this.outputDeviceId = outputDeviceId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Save if the object does not exist it creates it
     * otherwise it updates it
     * @param ctx
     * @return
     */
    public boolean save(Context ctx) {
        productManager = new PosProductManagement(ctx);

        if (productManager.get(productID) == null)
            return createProduct();
        else
            return updateProduct();
    }

    private boolean updateProduct() {
        return productManager.update(this);
    }

    /**
     * Communicates with the manager to create the product in the database
     * @return
     */
    private boolean createProduct() {
        return productManager.create(this);
    }

}
