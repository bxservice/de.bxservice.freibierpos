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