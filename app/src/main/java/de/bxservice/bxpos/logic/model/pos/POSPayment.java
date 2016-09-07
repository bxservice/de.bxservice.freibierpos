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

import de.bxservice.bxpos.logic.daomanager.PosPaymentManagement;
import de.bxservice.bxpos.logic.model.idempiere.IOrder;

/**
 * Created by Diego Ruiz on 8/04/16.
 */
public class POSPayment implements Serializable {

    //IDs from iDempiere
    private static final int CASH_TENDER_TYPE_ID        = 1000000;
    private static final int CREDIT_CARD_TENDER_TYPE_ID = 1000001;
    private static final String CREDIT_CARD_PAYMENT_TENDER_TYPE_VALUE = "C";
    private static final String CASH_PAYMENT_TENDER_TYPE_VALUE        = "X";

    private PosPaymentManagement paymentManager;

    private BigDecimal paymentAmount = BigDecimal.ZERO;
    private int POSTenderTypeID;
    private int paymentId;
    //Order that the payment belongs to
    private POSOrder order;

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public int getPOSTenderTypeID() {
        return POSTenderTypeID;
    }

    public void setPOSTenderTypeID(int POSTenderTypeID) {
        this.POSTenderTypeID = POSTenderTypeID;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public POSOrder getOrder() {
        return order;
    }

    public void setOrder(POSOrder order) {
        this.order = order;
    }

    public Integer getPaymentAmtInteger() {
        return paymentAmount.multiply(BigDecimal.valueOf(100)).intValue();//total * 100
    }

    public void setPaymentAmountFromInt(Integer total) {
        double doubleValue = (double) total / 100;
        paymentAmount = BigDecimal.valueOf(doubleValue);
    }

    public void setCashTenderTypeId(String paymentType) {
        if(IOrder.PAYMENTRULE_Cash.equals(paymentType)) {
            POSTenderTypeID = CASH_TENDER_TYPE_ID;
        } else if(IOrder.PAYMENTRULE_CreditCard.equals(paymentType)) {
            POSTenderTypeID = CREDIT_CARD_TENDER_TYPE_ID;
        }
    }

    public String getTenderType() {
        switch(POSTenderTypeID) {
            case CASH_TENDER_TYPE_ID:
                return IOrder.PAYMENTRULE_Cash;
            case CREDIT_CARD_TENDER_TYPE_ID:
                return IOrder.PAYMENTRULE_CreditCard;
            default:
                return IOrder.PAYMENTRULE_Cash;
        }
    }

    public String getPaymentTenderType() {
        switch(POSTenderTypeID) {
            case CASH_TENDER_TYPE_ID:
                return CASH_PAYMENT_TENDER_TYPE_VALUE;
            case CREDIT_CARD_TENDER_TYPE_ID:
                return CREDIT_CARD_PAYMENT_TENDER_TYPE_VALUE;
            default:
                return CASH_PAYMENT_TENDER_TYPE_VALUE;
        }
    }

    public boolean updatePayment(Context ctx) {
        paymentManager = new PosPaymentManagement(ctx);
        return paymentManager.update(this);
    }

    public boolean createPayment(Context ctx) {
        paymentManager = new PosPaymentManagement(ctx);
        return paymentManager.create(this);
    }

    public boolean remove(Context ctx) {
        paymentManager = new PosPaymentManagement(ctx);
        return paymentManager.remove(this);
    }
}