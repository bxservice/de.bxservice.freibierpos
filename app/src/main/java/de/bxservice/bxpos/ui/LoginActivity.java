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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.fcm.BxPosFirebaseInstanceIDService;
import de.bxservice.bxpos.logic.daomanager.PosUserManagement;
import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.logic.model.pos.PosRoles;
import de.bxservice.bxpos.logic.tasks.CreateDeviceTokenTask;
import de.bxservice.bxpos.logic.tasks.ReadServerDataTask;
import de.bxservice.bxpos.logic.webservices.AuthenticationWebService;
import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity  {

    private static final String LOG_TAG = "Login Activity";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ArrayList<String> roles;
    private Spinner rolesSpinner;
    private ImageButton settingsButton;

    // Reference to the role code in the properties file
    private HashMap<String, String> roleCodes;

    //Web service request data
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        getRoleNames();

        rolesSpinner = (Spinner) findViewById(R.id.roles_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.selected_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolesSpinner.setAdapter(adapter);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        settingsButton = (ImageButton) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettings();
                return;
            }
        });

        checkPlayServices();
    }

    /**
     * Check play services for push notifications
     * @return
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.showErrorNotification(this, result);
            }

            return false;
        }

        return true;
    }

    /**
     * Checks if the logged credentials were introduced before
     * @param username
     * @return
     */
    private PosUser getLoggedUser(String username) {
        PosUserManagement userManager = new PosUserManagement(getApplicationContext());

        return userManager.get(username);
    }

    /**
     * Sets the role name in the corresponding language
     * set the hashmap for inner references
     */
    private void getRoleNames() {
        roles = new ArrayList<>();
        roleCodes = new HashMap<>();

        String roleName;
        try {
            Class res = R.string.class;
            for( String role : PosRoles.getRoles() ){
                Field field = res.getField(role);
                int drawableId = field.getInt(null);
                roleName = getString(drawableId);
                roles.add(roleName);
                roleCodes.put(roleName, role);
            }
        }
        catch (Exception e) {
            Log.e(LOG_TAG, "Failure to get drawable id.", e);
        }
    }

    /**
     * Attempts to sign in .
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String roleName = rolesSpinner.getSelectedItem().toString();
        String role     = roleCodes.get(roleName);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            //Check if network connection is available
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                showProgress(true);

                mAuthTask = new UserLoginTask(username, password, role);
                mAuthTask.execute((Void) null);

            } else {

                String syncConnPref = sharedPref.getString(OfflineAdminSettingsActivity.KEY_PREF_SYNC_CONN, "");

                // If the sync configuration chosen was Always offline login not allowed
                if("0".equals(syncConnPref)) {
                    Snackbar snackbar = Snackbar
                            .make(mLoginFormView, getString(R.string.error_no_connection), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.action_retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    attemptLogin();
                                }
                            });

                    // Changing message text color
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                } else {

                    PosUser loggedUser = getLoggedUser(username);

                    //Username does not exist and no internet connection
                    if(loggedUser == null) {
                        Snackbar snackbar = Snackbar
                                .make(mLoginFormView, getString(R.string.error_no_connection_username), Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.action_retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        attemptLogin();
                                    }
                                });

                        // Changing message text color
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                    } else {
                        //No internet connection but the user is known
                        if (loggedUser.authenticateHash(password)) {
                            offlineLogin(loggedUser, password);
                        }
                    }
                }
            }
        }
    }

    /**
     * If there is no internet connection and the user is known
     * login loading the data that was synchronized before
     * @param loggedUser
     */
    private void offlineLogin(PosUser loggedUser, String plainPwd) {

        setWsDataPreferences(loggedUser.getUsername(), plainPwd);

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void setWsDataPreferences(String username, String password) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(WebServiceRequestData.DATA_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(WebServiceRequestData.USERNAME_SYNC_PREF, username);
        editor.putString(WebServiceRequestData.PASSWORD_SYNC_PREF, password);
        editor.commit();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            settingsButton.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            settingsButton.setVisibility(show ? View.GONE : View.VISIBLE);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void openSettings() {
        //Check if the device has a large screen or not
        if ((getBaseContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            Intent intent = new Intent(getBaseContext(), HeadersActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(getBaseContext(), OfflineAdminSettingsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private final String mRole;

        UserLoginTask(String email, String password, String role) {
            mUsername = email;
            mPassword = password;
            mRole     = role;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            setWsDataPreferences(mUsername, mPassword);
            WebServiceRequestData wsData = new WebServiceRequestData(getBaseContext());

            //If the data to connect to the server has not been set up before - error
            if (!wsData.isDataComplete()) {
                Snackbar snackbar = Snackbar
                        .make(mLoginFormView, getString(R.string.error_no_server_data_configured), Snackbar.LENGTH_LONG)
                        .setAction("", null);
                snackbar.show();
                return false;
            } else {
                AuthenticationWebService auth = new AuthenticationWebService(wsData);

                return auth.isSuccess();
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {

                //New user -> create it in the database
                if (getLoggedUser(mUsername) == null) {
                    PosUser newUser = new PosUser();
                    newUser.setUsername(mUsername);
                    newUser.setPassword(mPassword);
                    newUser.createUser(getBaseContext());

                    Log.i(LOG_TAG, "New user created " + mUsername);
                }

                // Read the data needed - Products. MProduct Category - Table ...
                ReadServerDataTask initiateData = new ReadServerDataTask(LoginActivity.this);
                initiateData.execute();

            } else {

                String syncConnPref = sharedPref.getString(OfflineAdminSettingsActivity.KEY_PREF_SYNC_CONN, "");

                // If the sync configuration chosen was Always offline login not allowed
                if (!"0".equals(syncConnPref)) {
                    PosUser loggedUser = getLoggedUser(mUsername);

                    //Username does not exist and no internet connection
                    if(loggedUser != null) {
                        //No connection to the server but the user is known
                        if (loggedUser.authenticateHash(mPassword)) {
                            Log.i(LOG_TAG, "No connection to the server - offline login");
                            offlineLogin(loggedUser, mPassword);
                            return;
                        }
                    }
                }

                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
                onCancelled();

            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * Called when the read data task finishes
     * @param result
     */
    public void postExecuteReadDataTask(Boolean result) {
        showProgress(false);

        if (result) {

            //Check if the device has already subscribed to the server for push notifications
            SharedPreferences tokenSharedPref = getApplicationContext().getSharedPreferences(BxPosFirebaseInstanceIDService.TOKEN_SHARED_PREF, Context.MODE_PRIVATE);
            boolean isTokenSync = tokenSharedPref.getBoolean(BxPosFirebaseInstanceIDService.TOKEN_SYNC_PREF, false);

            //If it has not been sync -> send it to the server
            if (!isTokenSync) {
                String token = BxPosFirebaseInstanceIDService.getToken();
                Log.d(LOG_TAG, "Registering -> " + token);

                //Send the token to the server
                CreateDeviceTokenTask createDeviceTokenTask = new CreateDeviceTokenTask(tokenSharedPref, this.getBaseContext());
                createDeviceTokenTask.execute(token);
            }

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Snackbar snackbar = Snackbar
                    .make(mLoginFormView, getString(R.string.error_config_server), Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

}

