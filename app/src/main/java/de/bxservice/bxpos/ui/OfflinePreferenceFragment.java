package de.bxservice.bxpos.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.bxservice.bxpos.R;

/**
 * Created by Diego Ruiz on 16/12/15.
 */
public class OfflinePreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        OfflineAdminSettingsActivity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_PREF_URL));
        OfflineAdminSettingsActivity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_PREF_SYNC_CONN));
        OfflineAdminSettingsActivity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_ORG_ID));
        OfflineAdminSettingsActivity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_CLIENT_ID));
        OfflineAdminSettingsActivity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_ROLE_ID));
        OfflineAdminSettingsActivity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_WAREHOUSE_ID));
        OfflineAdminSettingsActivity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_VOID_BLOCKED));
        OfflineAdminSettingsActivity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_PIN_CODE));

    }
}