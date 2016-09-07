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
package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 8/04/16.
 */
public class PosPaymentContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PosPaymentContract() {}

    /* Inner class that defines the table contents */
    public static abstract class POSPaymentDB implements BaseColumns {
        public static final String TABLE_NAME                 = "pos_payment";
        public static final String COLUMN_NAME_PAYMENT_ID     = "paymentid";
        public static final String COLUMN_NAME_CREATED_AT     = "created";
        public static final String COLUMN_NAME_UPDATED_AT     = "updated";
        public static final String COLUMN_NAME_CREATED_BY     = "createdBy";
        public static final String COLUMN_NAME_TENDER_TYPE    = "tenderType";
        public static final String COLUMN_NAME_ORDER_ID       = "orderid";
        public static final String COLUMN_NAME_PAYMENT_AMOUNT = "paymentAmount";

    }
}
