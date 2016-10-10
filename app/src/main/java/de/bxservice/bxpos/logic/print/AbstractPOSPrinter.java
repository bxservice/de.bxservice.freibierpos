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
package de.bxservice.bxpos.logic.print;

import de.bxservice.bxpos.logic.model.pos.POSOrder;

/**
 * Created by Diego Ruiz on 22/04/16.
 */
public abstract class AbstractPOSPrinter implements POSPrinter {

    protected POSOrder order;
    protected int      pageWidth;

    public AbstractPOSPrinter(POSOrder order, int pageWidth) {
        this.order = order;
        this.pageWidth = pageWidth;
    }

    protected abstract String getTicketText(String target, String orderLabel, String tableLabel, String tableName, String serverLabel, String guestsLabel);
    protected abstract String getReceiptText(String restaurantName,
                                             String address,
                                             String city,
                                             String receiptLabel,
                                             String tableLabel,
                                             String tableName,
                                             String serverLabel,
                                             String guestsLabel,
                                             String subtotalLabel,
                                             String surchargeLabel,
                                             String totalLabel,
                                             String cashLabel,
                                             String changeLabel);

}
