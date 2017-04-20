package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;

import de.bxservice.bxpos.logic.model.pos.POSOrder;

/**
 * Based on org.compiere.model.MInvoice.ITaxProvider
 * from the iDempiere project
 * Created by Diego Ruiz on 14/11/16.
 */
public interface ITaxProvider {

    boolean calculateOrderTaxTotal(Context ctx, POSOrder order);

}
