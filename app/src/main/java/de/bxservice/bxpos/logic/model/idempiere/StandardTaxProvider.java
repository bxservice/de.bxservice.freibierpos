package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;

import java.math.BigDecimal;
import java.util.ArrayList;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.logic.model.pos.POSOrderTax;

/**
 * Based on the class org.compiere.mode.StandardTaxProvider
 * from the iDempiere project
 * Created by Diego Ruiz on 14/11/16.
 */
public class StandardTaxProvider implements ITaxProvider {

    @Override
    public boolean calculateOrderTaxTotal(Context ctx, POSOrder order) {
        //	Lines
        BigDecimal totalLines = BigDecimal.ZERO;
        ArrayList<Integer> taxList = new ArrayList<>();

        for (POSOrderLine line : order.getOrderedLines())
        {
            totalLines = totalLines.add(line.getLineNetAmt());

            if (line.getLineTax() != null) {
                Integer taxID = new Integer(line.getLineTax().getTaxID());
                if (!taxList.contains(taxID)) {
                    Tax tax = Tax.getTax(taxID, ctx);
                    POSOrderTax oTax = new POSOrderTax();
                    oTax.setTax(tax);
                    oTax.setOrder(order);
                    oTax.setPrecision(DefaultPosData.getPrecision(ctx));

                    order.getOrderTaxes().add(oTax);
                    if (!oTax.calculateTaxFromLines())
                        return false;
                    taxList.add(taxID);
                }
            }
        }

        return true;
    }
}
