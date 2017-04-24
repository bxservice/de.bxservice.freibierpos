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

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.KeyEvent;

import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.ui.utilities.PreferenceActivityHelper;

/**
 * Created by Diego Ruiz on 9/9/16.
 */
public class HeadersActivity extends PreferenceActivity {

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || OfflineHeadersPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
            finish();
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            final String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if ((preference instanceof CheckBoxPreference) ||
                    (preference instanceof EditTextPreference
                            && ((EditTextPreference) preference).getEditText().getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)) ) {
                // For all checkboxes show the defined summary or password types
                preference.setSummary(preference.getSummary());
            } else {

                //If the key changed is the URL and the value was really changed -> not only selected
                if (PreferenceActivityHelper.KEY_PREF_URL.equals(preference.getKey()) || PreferenceActivityHelper.KEY_CLIENT_ID.equals(preference.getKey())) {
                    if (!PreferenceActivityHelper.validateServerChange(HeadersActivity.this, preference.getKey(), stringValue, preference))
                        return false;
                }

                //Check if the Document No is valid
                if (PreferenceActivityHelper.KEY_ORDER_NUMBER.equals(preference.getKey())) {
                    if (!PreferenceActivityHelper.checkDocumentNo(HeadersActivity.this, stringValue))
                        return false;
                }

                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);

            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    public void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        if (preference instanceof CheckBoxPreference)
        {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(
                    preference,
                    PreferenceManager.getDefaultSharedPreferences(
                            preference.getContext()).getBoolean(preference.getKey(), true));
        }
        else
            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));


    }

}
