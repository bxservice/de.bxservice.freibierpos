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
package de.bxservice.bxpos.ui.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.persistence.helper.PosDatabaseHelper;

public class PreferenceActivityHelper {

    //Restaurant
    public static final String KEY_ORDER_PREFIX = "pref_order_prefix";
    public static final String KEY_ORDER_NUMBER = "pref_order_number";

    //General
    public static final String KEY_PREF_URL = "pref_serverurl";
    public static final String KEY_ORG_ID = "pref_org";
    public static final String KEY_CLIENT_ID = "pref_client";
    public static final String KEY_ROLE_ID = "pref_role";
    public static final String KEY_WAREHOUSE_ID = "pref_warehouse";

    //Sync & Data
    public static final String KEY_PREF_SYNC_CONN = "sync_frequency";

    private static void deleteDatabase(Context context) {
        Log.d(context.getClass().getSimpleName(), "Deleting database");
        PosDatabaseHelper databaseHelper = PosDatabaseHelper.getInstance(context);
        databaseHelper.deleteDatabase(context);
    }

    private static void setChangedPreference(Preference preference, String newValue) {
        //Because I return false anyway, I change the preference manually here
        ((EditTextPreference)preference).setText(newValue);
        preference.setSummary(newValue);
    }

    /**
     * Checks the changes on the fields that force the db to be deleted
     * If the value was really changed -> not only selected
     */
    public static boolean validateServerChange(final Activity activity, String preferenceKey, final String stringValue, final Preference preference) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext().getApplicationContext());
        String connPref = sharedPref.getString(preferenceKey, "");

        String defaultPref = "";
        if (KEY_PREF_URL.equals(preferenceKey))
            defaultPref = activity.getString(R.string.pref_default_display_name);
        else if (KEY_PREF_URL.equals(preferenceKey))
            defaultPref = activity.getString(R.string.client);

        if (stringValue != null && connPref != null && !connPref.equals(defaultPref)
                && !stringValue.equals(connPref)) {

            if (isUnsyncOrders(activity.getBaseContext())) {
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.pending_orders)
                        .setMessage(R.string.pref_change_error)
                        .setPositiveButton(android.R.string.ok, null).create().show();
            } else {
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.change_url)
                        .setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteDatabase(activity.getBaseContext());
                                setChangedPreference(preference, stringValue);
                            }
                        }).create().show();
            }

            //Return false always to control the confirmation dialog
            return false;
        }

        return true;
    }

    public static boolean checkDocumentNo(Activity activity, String stringValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String orderPrefix = sharedPref.getString(KEY_ORDER_PREFIX, "");
        String orderNumber = sharedPref.getString(KEY_ORDER_NUMBER, "");

        if (stringValue != null && !stringValue.equals(orderNumber)) {
            int maxDocumentNo = POSOrder.getMaxDocumentNo(activity.getBaseContext(), orderPrefix);
            if (Integer.parseInt(stringValue) <= maxDocumentNo) {

                new AlertDialog.Builder(activity)
                        .setMessage(activity.getString(R.string.wrong_order_number, maxDocumentNo))
                        .setPositiveButton(android.R.string.ok, null).create().show();
                return false;
            }
        }

        return true;
    }

    public static boolean isUnsyncOrders(Context context) {
        List<POSOrder> pendingOrders = POSOrder.getUnsynchronizedOrders(context);
        return pendingOrders != null && pendingOrders.size() != 0;
    }

}
