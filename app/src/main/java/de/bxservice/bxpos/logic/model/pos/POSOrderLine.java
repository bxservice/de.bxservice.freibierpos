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
package de.bxservice.bxpos.logic.model.pos;

import android.content.Context;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;

import de.bxservice.bxpos.logic.daomanager.PosOrderLineManagement;
import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.idempiere.MProduct;

/**
 * This is the line that is added to the orders
 * it has extra attributes that will not be sent to iDempiere
 * Created by Diego Ruiz on 25/11/15.
 */
public class POSOrderLine implements Serializable {

    //Order Line status
    public static final String ORDERING    = "ORDERING";
    public static final String ORDERED     = "ORDERED";
    public static final String VOIDED = "VOIDED";

    private PosOrderLineManagement lineManager;
    //Order that the line belongs to
    private POSOrder order;

    private int orderLineId;
    private int lineNo;
    private MProduct product;
    private int qtyOrdered;
    private String productRemark = "";
    private boolean marked = false;
    private boolean printed = false;
    private String lineStatus;
    private BigDecimal lineNetAmt = BigDecimal.ZERO; //qty*StpPrice

    //Defines if the order line is a free product
    private boolean isComplimentaryProduct = false;


    public MProduct getProduct() {
        return product;
    }

    public void setProduct(MProduct product) {
        this.product = product;
    }

    public int getQtyOrdered() {
        return qtyOrdered;
    }

    public void setQtyOrdered(int qtyOrdered) {
        this.qtyOrdered = qtyOrdered;
    }

    public String getProductRemark() {
        return productRemark;
    }

    public void setProductRemark(String productRemark) {
        this.productRemark = productRemark;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    public String getLineStatus() {
        return lineStatus;
    }

    public void setLineStatus(String lineStatus) {
        this.lineStatus = lineStatus;
    }

    public BigDecimal getLineNetAmt() {

        if(isComplimentaryProduct)
            lineNetAmt = BigDecimal.ZERO;
        else if(lineNetAmt.equals(BigDecimal.ZERO) && product != null) //If the amt was not calculated before
            lineNetAmt = product.getProductPriceValue().multiply(BigDecimal.valueOf(qtyOrdered));

        return lineNetAmt;
    }

    public BigDecimal getPriceActual() {

        //return unitary value to be sent to iDempiere
        if (product != null && !isComplimentaryProduct)
            return product.getProductPriceValue();

        //if it is a complimentary line -> return zero
        return BigDecimal.ZERO;
    }

    /**
     * Returns the total amt of the line
     * in an integer to be save in the database
     * @return
     */
    public Integer getLineNetAmtInteger() {
        return getLineNetAmt().multiply(BigDecimal.valueOf(100)).intValue(); //total * 100
    }

    /**
     * Gets an integer value from the db and converts it to a BigDecimal
     * last two digits are decimals
     * @param total
     */
    public void setLineTotalFromInt(Integer total) {
        double doubleValue = (double) total / 100;
        lineNetAmt = BigDecimal.valueOf(doubleValue);
    }

    public String getLineTotalAmt() {

        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();
        return currencyFormat.format(getLineNetAmt());
    }

    public POSOrder getOrder() {
        return order;
    }

    public void setOrder(POSOrder order) {
        this.order = order;
    }

    public int getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(int orderLineId) {
        this.orderLineId = orderLineId;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public boolean isComplimentaryProduct() {
        return isComplimentaryProduct;
    }

    public void setComplimentaryProduct(boolean complimentaryProduct) {
        isComplimentaryProduct = complimentaryProduct;
    }

    public void completeLine() {
        lineStatus = ORDERED;
    }

    public void uncompleteLine() {
        lineStatus = ORDERING;
    }

    public void voidLine(String reason) {
        lineStatus = VOIDED;
        productRemark = productRemark + " ***VOIDED*** " + reason; //Description in iDempiere
    }

    public boolean updateLine(Context ctx) {

        lineManager = new PosOrderLineManagement(ctx);
        return lineManager.update(this);

    }

    public boolean createLine(Context ctx) {
        lineManager = new PosOrderLineManagement(ctx);
        return lineManager.create(this);
    }

    public boolean remove(Context ctx) {
        lineManager = new PosOrderLineManagement(ctx);
        return lineManager.remove(this);
    }

    /**
     * Return if a line is voidable or not
     * based on the conditions of not having
     * VOID status and qty greater than 0
     * @return
     */
    public boolean isVoidable() {
        if(lineStatus.equals(VOIDED) || qtyOrdered < 0)
            return false;

        return true;
    }

}
