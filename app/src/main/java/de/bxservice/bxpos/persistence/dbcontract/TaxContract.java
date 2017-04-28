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
package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

public class TaxContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public TaxContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class TaxDB implements BaseColumns {
        public static final String TABLE_NAME = "tax";
        public static final String COLUMN_NAME_TAX_CATEGORY_ID = "tax_category_id";
        public static final String COLUMN_NAME_NAME            = "name";
        public static final String COLUMN_NAME_TAX_ID          = "tax_id";
        public static final String COLUMN_NAME_RATE            = "rate";
        public static final String COLUMN_NAME_POSTAL          = "postal";
    }

}
