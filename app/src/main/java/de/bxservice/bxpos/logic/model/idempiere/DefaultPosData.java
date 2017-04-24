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

import android.content.Context;

import de.bxservice.bxpos.logic.daomanager.PosDefaultDataManagement;

/**
 * Default pos data read from iDempiere
 * POS Terminal
 * Created by Diego Ruiz on 29/02/16.
 */
public class DefaultPosData {

    //Manager in charge to communicate with the database - not mixing the model and db layers
    private PosDefaultDataManagement dataManager;

    private int defaultBPartner = 0;
    private int defaultBPartnerToGo = 0;
    private int defaultPriceList = 0;
    private int defaultCurrency = 0;
    private int defaultWarehouse = 0;
    private int discountId = 0;
    private int surchargeId = 0;
    private int defaultPOSID = 0;
    private int pin = 0;
    private int stdPrecision = 0;
    private boolean printAfterSent = false;
    private boolean combineReceiptItems = false;
    private boolean separateOrderItems = false;
    private boolean isTaxIncluded = false;
    private boolean showGuestDialog = false;
    private String currencyIsoCode  = "";
    private String clientAdLanguage  = "";
    private String receiptFooter  = "";

    public int getDefaultBPartner() {
        return defaultBPartner;
    }

    public void setDefaultBPartner(int defaultBPartner) {
        this.defaultBPartner = defaultBPartner;
    }

    public int getDefaultBPartnerToGo() {
        return defaultBPartnerToGo;
    }

    public void setDefaultBPartnerToGo(int defaultBPartnerToGo) {
        this.defaultBPartnerToGo = defaultBPartnerToGo;
    }

    public int getDefaultPriceList() {
        return defaultPriceList;
    }

    public void setDefaultPriceList(int defaultPriceList) {
        this.defaultPriceList = defaultPriceList;
    }

    public int getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(int defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public int getDefaultWarehouse() {
        return defaultWarehouse;
    }

    public void setDefaultWarehouse(int defaultWarehouse) {
        this.defaultWarehouse = defaultWarehouse;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public int getSurchargeId() {
        return surchargeId;
    }

    public void setSurchargeId(int surchargeId) {
        this.surchargeId = surchargeId;
    }

    public boolean isCombineReceiptItems() {
        return combineReceiptItems;
    }

    public void setCombineReceiptItems(boolean combineReceiptItems) {
        this.combineReceiptItems = combineReceiptItems;
    }

    public boolean isPrintAfterSent() {
        return printAfterSent;
    }

    public void setPrintAfterSent(boolean printAfterSent) {
        this.printAfterSent = printAfterSent;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getStdPrecision() {
        return stdPrecision;
    }

    public void setStdPrecision(int stdPrecision) {
        this.stdPrecision = stdPrecision;
    }

    public boolean isTaxIncluded() {
        return isTaxIncluded;
    }

    public void setTaxIncluded(boolean taxIncluded) {
        isTaxIncluded = taxIncluded;
    }

    public String getCurrencyIsoCode() {
        return currencyIsoCode;
    }

    public void setCurrencyIsoCode(String currencyIsoCode) {
        this.currencyIsoCode = currencyIsoCode;
    }

    public String getClientAdLanguage() {
        return clientAdLanguage;
    }

    public void setClientAdLanguage(String clientAdLanguage) {
        this.clientAdLanguage = clientAdLanguage;
    }

    public String getReceiptFooter() {
        return receiptFooter;
    }


    public boolean isShowGuestDialog() {
        return showGuestDialog;
    }

    public void setShowGuestDialog(boolean showGuestDialog) {
        this.showGuestDialog = showGuestDialog;
    }

    public void setReceiptFooter(String receiptFooter) {
        this.receiptFooter = receiptFooter;
    }

    public boolean isSeparateOrderItems() {
        return separateOrderItems;
    }

    public void setSeparateOrderItems(boolean separateOrderItems) {
        this.separateOrderItems = separateOrderItems;
    }

    public int getDefaultPOSID() {
        return defaultPOSID;
    }

    public void setDefaultPOSID(int defaultPOSID) {
        this.defaultPOSID = defaultPOSID;
    }

    public static int getPrecision(Context ctx) {
        DefaultPosData posData = get(ctx);
        return posData.getStdPrecision();
    }

    public static DefaultPosData get(Context ctx) {
        PosDefaultDataManagement dataManager = new PosDefaultDataManagement(ctx);
        return dataManager.getDefaultData();
    }

    /**
     * Save data - if the default data was not previously saved it creates it
     * otherwise it updates it
     * @param ctx
     * @return
     */
    public boolean saveData (Context ctx) {
        dataManager = new PosDefaultDataManagement(ctx);

        if (dataManager.get(1) == null)
            return createData();
        else
            return updateData();
    }

    private boolean createData () {
        return dataManager.create(this);
    }

    private boolean updateData () {
        return dataManager.update(this);
    }

}
