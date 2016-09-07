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

import android.content.Context;

import de.bxservice.bxpos.logic.daomanager.PosOutputDeviceManagement;

/**
 * Created by Diego Ruiz on 28/04/16.
 */
public class POSOutputDevice {

    private PosOutputDeviceManagement deviceManager;

    private int outputDeviceId;
    private String connectionType;
    private String deviceType;
    private String docTarget;
    private String printerLanguage;
    private String printerName;
    private int    pageWidth;

    public int getOutputDeviceId() {
        return outputDeviceId;
    }

    public void setOutputDeviceId(int outputDeviceId) {
        this.outputDeviceId = outputDeviceId;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDocTarget() {
        return docTarget;
    }

    public void setDocTarget(String docTarget) {
        this.docTarget = docTarget;
    }

    public String getPrinterLanguage() {
        return printerLanguage;
    }

    public void setPrinterLanguage(String printerLanguage) {
        this.printerLanguage = printerLanguage;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }

    public boolean save(Context ctx) {
        deviceManager = new PosOutputDeviceManagement(ctx);

        if (deviceManager.get(outputDeviceId) == null)
            return createOutputDevice();
        else
            return updateOutputDevice();
    }

    private boolean updateOutputDevice() {
        return deviceManager.update(this);
    }

    private boolean createOutputDevice() {
        return deviceManager.create(this);
    }
}
