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

        OfflineAdminSettingsActivity activity = (OfflineAdminSettingsActivity) getActivity();

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        activity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_ORDER_PREFIX));
        activity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_PREF_URL));
        activity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_PREF_SYNC_CONN));
        activity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_ORG_ID));
        activity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_CLIENT_ID));
        activity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_ROLE_ID));
        activity.bindPreferenceSummaryToValue(findPreference(OfflineAdminSettingsActivity.KEY_WAREHOUSE_ID));

    }
}