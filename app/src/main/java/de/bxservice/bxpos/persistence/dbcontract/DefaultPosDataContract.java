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
 * Created by Diego Ruiz on 1/03/16.
 */
public class DefaultPosDataContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public DefaultPosDataContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class DefaultDataDB implements BaseColumns {
        public static final String TABLE_NAME = "pos_defaultdata";
        public static final String COLUMN_NAME_DEFAULT_DATA_ID = "defaultdataid";
        public static final String COLUMN_NAME_BPARTNER = "defaultBPartner";
        public static final String COLUMN_NAME_PRICE_LIST = "defaultPriceList";
        public static final String COLUMN_NAME_CURRENCY = "defaultCurrency";
        public static final String COLUMN_NAME_WAREHOUSE = "defaultWarehouse";
        public static final String COLUMN_NAME_DISCOUNT_ID = "discountID";
        public static final String COLUMN_NAME_SURCHARGE_ID = "surchargeID";
        public static final String COLUMN_NAME_COMBINE_ITEMS = "combineItems";
        public static final String COLUMN_NAME_PRINT_AFTER_SEND = "printAfter";
        public static final String COLUMN_NAME_IS_TAX_INCLUDED = "IsTaxIncluded";
        public static final String COLUMN_NAME_PIN = "pin";
        public static final String COLUMN_NAME_ISO_CODE = "iso_code";
        public static final String COLUMN_NAME_AD_LANGUAGE = "ad_language";
        public static final String COLUMN_NAME_RECEIPT_FOOTER = "receiptFooter";
        public static final String COLUMN_NAME_STDPRECISION = "stdPrecision";
        public static final String COLUMN_NAME_BPARTNER_TOGO = "defaultBPartnerToGo";
        public static final String COLUMN_NAME_SHOW_GUEST_DIALOG = "showGuestDialog";
        public static final String COLUMN_NAME_SEPARATE_ORDER_ITEMS = "separateOrderItems";
        public static final String COLUMN_NAME_C_POS_ID = "C_POS_ID";
    }

}
