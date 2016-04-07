package de.bxservice.bxpos.logic.model.idempiere;

/**
 * Created by Diego Ruiz on 7/04/16.
 */
public interface IOrder {

    /** Document Type Sales Order*/
    String DocTypeSO = "135";
    /** Cash = B */
    String PAYMENTRULE_Cash = "B";
    /** Credit Card = K */
    String PAYMENTRULE_CreditCard = "K";
    /** Direct Deposit = T */
    String PAYMENTRULE_DirectDeposit = "T";
    /** Check = S */
    String PAYMENTRULE_Check = "S";
    /** On Credit = P */
    String PAYMENTRULE_OnCredit = "P";
    /** Direct Debit = D */
    String PAYMENTRULE_DirectDebit = "D";
    /** Mixed POS Payment = M */
    String PAYMENTRULE_MixedPOSPayment = "M";
}
