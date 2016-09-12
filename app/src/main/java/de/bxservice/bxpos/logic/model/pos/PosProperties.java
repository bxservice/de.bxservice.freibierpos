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
package de.bxservice.bxpos.logic.model.pos;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import de.bxservice.bxpos.logic.daomanager.PosDefaultDataManagement;
import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;

/**
 * Created by Diego Ruiz on 9/12/16.
 */
public class PosProperties {


    private static PosProperties instance;
    private Locale locale;
    private NumberFormat currencyFormat;

    private PosProperties() {
    }

    public static PosProperties getInstance() {
        if (instance == null)
            instance = new PosProperties();
        return instance;
    }

    public Locale getLocale() {
        if (locale == null)
            setLocale();

        return locale;
    }

    private void setLocale() {

        DefaultPosData defaultPosData = new PosDefaultDataManagement(null).getDefaultData();

        //Language from iDempiere usually comes in the form en_US for example
        int i = defaultPosData.getClientAdLanguage().indexOf("_");
        if(i != -1) {
            String lang = defaultPosData.getClientAdLanguage().substring(0, i);
            String country = defaultPosData.getClientAdLanguage().substring(i+1, defaultPosData.getClientAdLanguage().length());
            locale = new Locale(lang, country);
        } else
            locale = new Locale(defaultPosData.getClientAdLanguage());

        if (locale == null)
            locale = Locale.getDefault();
    }

    public NumberFormat getCurrencyFormat() {

        if (currencyFormat == null)
            setCurrencyFormat();

        return currencyFormat;
    }

    private void setCurrencyFormat() {

        currencyFormat = NumberFormat.getCurrencyInstance(PosProperties.getInstance().getLocale());

        DefaultPosData defaultPosData = new PosDefaultDataManagement(null).getDefaultData();

        String currencyCode = defaultPosData.getCurrencyIsoCode();
        //If the currency code from iDempiere is empty or the same as the one from the Locale
        if (currencyCode.isEmpty() || currencyFormat.getCurrency().getCurrencyCode().equals(currencyCode))
            return;

        currencyFormat.setCurrency(Currency.getInstance(currencyCode));
    }

}