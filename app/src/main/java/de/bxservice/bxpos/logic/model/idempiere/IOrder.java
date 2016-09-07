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
