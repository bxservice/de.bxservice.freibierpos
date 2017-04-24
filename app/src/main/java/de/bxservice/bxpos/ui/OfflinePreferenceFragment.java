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
package de.bxservice.bxpos.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.ui.utilities.PreferenceActivityHelper;

/**
 * Created by Diego Ruiz on 16/12/15.
 */
public class OfflinePreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);



        String settings = "";
        if(getArguments() != null)
            settings = getArguments().getString("settings");
        if ("order_settings".equals(settings)) {
            addPreferencesFromResource(R.xml.pref_order_settings);
        } else if ("data_sync".equals(settings)) {
            addPreferencesFromResource(R.xml.pref_data_sync);
        } else if ("server".equals(settings)) {
            addPreferencesFromResource(R.xml.pref_server);
        } else
            addPreferencesFromResource(R.xml.preferences);

        OfflineAdminSettingsActivity activity = (OfflineAdminSettingsActivity) getActivity();

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        activity.bindPreferenceSummaryToValue(findPreference(PreferenceActivityHelper.KEY_ORDER_PREFIX));
        activity.bindPreferenceSummaryToValue(findPreference(PreferenceActivityHelper.KEY_ORDER_NUMBER));
        activity.bindPreferenceSummaryToValue(findPreference(PreferenceActivityHelper.KEY_PREF_URL));
        activity.bindPreferenceSummaryToValue(findPreference(PreferenceActivityHelper.KEY_PREF_SYNC_CONN));
        activity.bindPreferenceSummaryToValue(findPreference(PreferenceActivityHelper.KEY_ORG_ID));
        activity.bindPreferenceSummaryToValue(findPreference(PreferenceActivityHelper.KEY_CLIENT_ID));
        activity.bindPreferenceSummaryToValue(findPreference(PreferenceActivityHelper.KEY_ROLE_ID));
        activity.bindPreferenceSummaryToValue(findPreference(PreferenceActivityHelper.KEY_WAREHOUSE_ID));

    }
}