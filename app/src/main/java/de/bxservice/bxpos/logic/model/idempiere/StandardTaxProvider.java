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
