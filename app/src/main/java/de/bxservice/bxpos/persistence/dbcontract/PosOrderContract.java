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
 * Created by Diego Ruiz on 18/12/15.
 */
public class PosOrderContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public PosOrderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class POSOrderDB implements BaseColumns {
        public static final String TABLE_NAME               = "pos_order";
        public static final String COLUMN_NAME_ORDER_ID     = "orderid";
        public static final String COLUMN_NAME_CREATED_AT   = "created";
        public static final String COLUMN_NAME_UPDATED_AT   = "updated";
        public static final String COLUMN_NAME_CREATED_BY   = "createdBy";
        public static final String COLUMN_NAME_ORDER_STATUS = "status";
        public static final String COLUMN_NAME_TABLE_ID     = "table_id";
        public static final String COLUMN_NAME_GUESTS       = "guestno";
        public static final String COLUMN_NAME_REMARK       = "remark";
        public static final String COLUMN_NAME_TOTALLINES   = "totallines";
        public static final String COLUMN_NAME_SYNCHRONIZED = "isSynchronized";
        public static final String COLUMN_NAME_SURCHARGE    = "surcharge";
        public static final String COLUMN_NAME_DISCOUNT     = "discount";
        public static final String COLUMN_NAME_DISCOUNT_REASON = "discountReason";
        public static final String COLUMN_NAME_PAYMENT_RULE = "paymentRule";

    }

}
