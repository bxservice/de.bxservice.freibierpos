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

/**
 * Printer settings defined in iDempiere
 * Values of the lists in the window
 * POS Output Device
 * Created by Diego Ruiz on 28/04/16.
 */
public interface POSOutputDeviceValues {

    /** Connection type */
    String CONNECTION_BLUETOOTH = "B";
    String CONNECTION_WLAN      = "W";

    /** Device Type */
    String DEVICE_PRINTER = "P";
    String DEVICE_DISPLAY = "D";

    /** Output Target */
    String TARGET_KITCHEN     = "K";
    String TARGET_BAR         = "B";
    String TARGET_RECEIPT     = "R";

    /** Printer Language */
    String LANGUAGE_CPCL = "C";
    String LANGUAGE_ZPL  = "Z";
    String LANGUAGE_ESC  = "E";

}
