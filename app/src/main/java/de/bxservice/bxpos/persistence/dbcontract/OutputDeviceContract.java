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
 * Created by Diego Ruiz on 28/04/16.
 */
public class OutputDeviceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public OutputDeviceContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class OutputDeviceDB implements BaseColumns {
        public static final String TABLE_NAME = "output_device";
        public static final String COLUMN_NAME_OUTPUT_DEVICE_ID = "outputdevice_id";
        public static final String COLUMN_NAME_PRINTER_NAME     = "printerName";
        public static final String COLUMN_NAME_PRINTER_LANGUAGE = "printerLanguage";
        public static final String COLUMN_NAME_TARGET           = "target";
        public static final String COLUMN_NAME_DEVICE_TYPE      = "deviceType";
        public static final String COLUMN_NAME_CONNECTION       = "connection";
        public static final String COLUMN_NAME_PAGE_WIDTH       = "pageWidth";
    }
}
