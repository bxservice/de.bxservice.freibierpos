package de.bxservice.bxpos.ui;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;


import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.persistence.helper.PosDatabaseHelper;


/**
 * Created by Diego Ruiz on 16/12/15.
 */
public class OfflineAdminSettingsActivity extends AppCompatPreferenceActivity {

    private static final String LOG_TAG = "Admin Settings";

    //Restaurant
    public static final String KEY_ORDER_PREFIX = "pref_order_prefix";

    //General
    public static final String KEY_PREF_URL = "pref_serverurl";
    public static final String KEY_ORG_ID = "pref_org";
    public static final String KEY_CLIENT_ID = "pref_client";
    public static final String KEY_ROLE_ID = "pref_role";
    public static final String KEY_WAREHOUSE_ID = "pref_warehouse";

    //Sync & Data
    public static final String KEY_PREF_SYNC_CONN = "sync_frequency";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                finish();
                return true;
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
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
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || OfflinePreferenceFragment.class.getName().equals(fragmentName);
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
                if (KEY_PREF_URL.equals(preference.getKey())) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext().getApplicationContext());
                    String urlConnPref = sharedPref.getString(KEY_PREF_URL, "");

                    if (stringValue != null && urlConnPref != null && !urlConnPref.equals(getString(R.string.pref_default_display_name))
                            && !stringValue.equals(urlConnPref)) {

                        final Preference urlPref = preference;
                        new AlertDialog.Builder(OfflineAdminSettingsActivity.this)
                                .setTitle(R.string.change_url)
                                .setNegativeButton(R.string.no, null)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Log.d(LOG_TAG, "URL Changed");
                                        deleteDatabase();
                                        setUrl(urlPref, stringValue);
                                    }
                                }).create().show();

                        //Return false always to control the confirmation dialog
                        return false;
                    }
                }

                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);

            }
            return true;
        }
    };

    private void deleteDatabase() {
        Log.d(LOG_TAG, "Deleting database");
        PosDatabaseHelper databaseHelper = PosDatabaseHelper.getInstance(getBaseContext());
        databaseHelper.deleteDatabase(getBaseContext());
    }

    private void setUrl(Preference preference, String newUrl) {
        //Because I return false anyway, I change the preference manually here
        ((EditTextPreference)preference).setText(newUrl);
        preference.setSummary(newUrl);
    }

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
