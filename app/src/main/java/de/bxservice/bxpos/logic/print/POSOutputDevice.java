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
