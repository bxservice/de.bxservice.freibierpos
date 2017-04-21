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
import de.bxservice.bxpos.logic.model.idempiere.PosTenderType;

/**
 * Created by Diego Ruiz on 8/04/16.
 */
public class POSPayment implements Serializable {

    private PosPaymentManagement paymentManager;

    private BigDecimal paymentAmount = BigDecimal.ZERO;
    private PosTenderType tenderType;
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
        return tenderType.getC_POSTenderType_ID();
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

    public void setTenderType(String paymentType, Context ctx) {
        if(IOrder.PAYMENTRULE_Cash.equals(paymentType)) {
           tenderType = PosTenderType.get(ctx, PosTenderType.CASH_PAYMENT_TENDER_TYPE_VALUE);
        } else if(IOrder.PAYMENTRULE_CreditCard.equals(paymentType)) {
            tenderType = PosTenderType.get(ctx, PosTenderType.CREDIT_CARD_PAYMENT_TENDER_TYPE_VALUE);
        }
    }

    public void setTenderType(PosTenderType tenderType) {
        this.tenderType = tenderType;
    }

    public PosTenderType getTenderType() {
        return tenderType;
    }

    public String getPaymentRule() {

        //Cash by default
        String paymentRule = IOrder.PAYMENTRULE_Cash;

        if (tenderType != null) {
            switch(tenderType.getTenderType()) {
                case PosTenderType.CASH_PAYMENT_TENDER_TYPE_VALUE:
                    return IOrder.PAYMENTRULE_Cash;
                case PosTenderType.CREDIT_CARD_PAYMENT_TENDER_TYPE_VALUE:
                    return IOrder.PAYMENTRULE_CreditCard;
            }
        }

        return paymentRule;
    }

    public String getPaymentTenderType() {
        if (tenderType != null)
            return tenderType.getTenderType();

        return PosTenderType.CASH_PAYMENT_TENDER_TYPE_VALUE;
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