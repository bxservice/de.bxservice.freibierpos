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
package de.bxservice.bxpos.logic.webservices;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Properties;

import de.bxservice.bxpos.logic.AssetsPropertyReader;
import de.bxservice.bxpos.logic.PosProperties;
import de.bxservice.bxpos.logic.daomanager.PosSessionPreferenceManagement;
import de.bxservice.bxpos.ui.OfflineAdminSettingsActivity;

/**
 * Created by Diego Ruiz on 6/11/15.
 */
public class WebServiceRequestData {

    public static final String USERNAME_SYNC_PREF = "de.bxservice.username";
    public static final String PASSWORD_SYNC_PREF = "de.bxservice.password";

    //Web services variables
    private String username        = null;
    private String password        = null;
    private String clientId        = null;
    private String roleId          = null;
    private String orgId           = null;
    private String attemptsNo      = null;
    private String timeout         = null;
    private String attemptsTimeout = null;
    private String urlBase         = null;
    private String warehouseId     = null;

    public WebServiceRequestData(Context context) {

        //Variables from preferences
        readPreferenceVariables(context);

        //Variables in the properties file
        readValues(context);

        //username and password
        readCredentials(context);
    }

    /**
     * Variables read from the preference Menu
     */
    private void readPreferenceVariables(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        urlBase =  sharedPref.getString(OfflineAdminSettingsActivity.KEY_PREF_URL, "");
        orgId = sharedPref.getString(OfflineAdminSettingsActivity.KEY_ORG_ID, "");
        clientId = sharedPref.getString(OfflineAdminSettingsActivity.KEY_CLIENT_ID, "");
        roleId = sharedPref.getString(OfflineAdminSettingsActivity.KEY_ROLE_ID, "");
        warehouseId = sharedPref.getString(OfflineAdminSettingsActivity.KEY_WAREHOUSE_ID, "");
    }

    private void readValues(Context context) {
        //Properties reader
        AssetsPropertyReader assetsPropertyReader = new AssetsPropertyReader(context);
        Properties properties = assetsPropertyReader.getProperties();

        attemptsNo      = properties.getProperty(PosProperties.ATTEMPTS_PROPERTY);
        timeout         = properties.getProperty(PosProperties.TIMEOUT_PROPERTY);
        attemptsTimeout = properties.getProperty(PosProperties.ATTEMPTS_TIMEOUT_PROPERTY);

    }

    private void readCredentials(Context context) {
        PosSessionPreferenceManagement preferenceManager = new PosSessionPreferenceManagement(context);

        username = preferenceManager.getPreferenceValue(USERNAME_SYNC_PREF);
        password = preferenceManager.getPreferenceValue(PASSWORD_SYNC_PREF);
    }

    public boolean isDataComplete() {
        if (username != null && !username.isEmpty() &&
                password != null        && !password.isEmpty() &&
                clientId != null        && !clientId.isEmpty() &&
                roleId != null          && !roleId.isEmpty() &&
                orgId != null           && !orgId.isEmpty() &&
                attemptsNo != null      && !attemptsNo.isEmpty() &&
                attemptsNo != null      && !attemptsNo.isEmpty() &&
                timeout != null         && !timeout.isEmpty() &&
                attemptsTimeout != null && !attemptsTimeout.isEmpty() &&
                urlBase != null         && !urlBase.isEmpty())
            return true;

        return false;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRoleId() {
        return roleId;
    }

    public String getOrgId() {
        return orgId;
    }

    public String getAttemptsNo() {
        return attemptsNo;
    }

    public String getTimeout() {
        return timeout;
    }

    public String getAttemptsTimeout() {
        return attemptsTimeout;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public static String getLoggedUsername(Context context) {
        PosSessionPreferenceManagement preferenceManager = new PosSessionPreferenceManagement(context);
        return preferenceManager.getPreferenceValue(USERNAME_SYNC_PREF);
    }

}
