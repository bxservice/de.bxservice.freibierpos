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
package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import de.bxservice.bxpos.logic.model.pos.PosProperties;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public abstract class Report {

    final static String HTML_EURO_SYMBOL = " &euro;";
    String name;
    int code;
    long fromDate, toDate;
    Context mContext;
    boolean isSelected;
    StringBuilder htmlResult;
    NumberFormat currencyFormat;

    public Report(Context mContext) {
        this.mContext = mContext;
        htmlResult = new StringBuilder();
    }

    public void runReport() {
        performReport();
        setReportResult();
    }

    protected void setCurrencyFormat() {

        //Create a clone to change the currency symbol without affecting the original object
        currencyFormat = (NumberFormat) PosProperties.getInstance().getCurrencyFormat().clone();

        //If the currency code is Euro
        if (Currency.getInstance(Locale.GERMANY).getCurrencyCode().equals(currencyFormat.getCurrency().getCurrencyCode())) {
            //Here is to replace the € sign because it has problems in HTML
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormat).getDecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol(HTML_EURO_SYMBOL);
            ((DecimalFormat) currencyFormat).setDecimalFormatSymbols(decimalFormatSymbols);
        }
    }

    protected String getFormattedValue(BigDecimal number) {

        if (currencyFormat == null)
            setCurrencyFormat();

        String formattedValue = currencyFormat.format(number);

        //If the string has garbage in it, replace it
        if(formattedValue.indexOf(String.valueOf((char) 160)) != -1)
            formattedValue = formattedValue.replace(String.valueOf((char) 160), "");

        //remove empty spaces
        formattedValue = formattedValue.trim();

        return formattedValue;
    }

    protected abstract void performReport();

    protected abstract void setReportResult();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public StringBuilder getHtmlResult() {
        return htmlResult;
    }

    public void setHtmlResult(StringBuilder htmlResult) {
        this.htmlResult = htmlResult;
    }
}
