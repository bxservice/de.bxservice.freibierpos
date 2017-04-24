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
 * Created by Diego Ruiz on 22/12/15.
 */
public class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ProductContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class ProductDB implements BaseColumns {
        public static final String TABLE_NAME = "pos_product";
        public static final String COLUMN_NAME_PRODUCT_ID = "productid";
        public static final String COLUMN_NAME_PRODUCT_CATEGORY_ID = "productcategoryid";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_OUTPUT_DEVICE_ID = "outputdevice_id";
        public static final String COLUMN_IS_ACTIVE = "isActive";
        public static final String COLUMN_NAME_VALUE = "keyValue";
        public static final String COLUMN_NAME_TAX_CATEGORY_ID = "tax_category_id";
        public static final String COLUMN_NAME_IS_SOLD = "isSold";
    }
}
