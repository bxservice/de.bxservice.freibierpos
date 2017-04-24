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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.HashMap;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.fcm.BxPosFirebaseInstanceIDService;
import de.bxservice.bxpos.logic.daomanager.PosSessionPreferenceManagement;
import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.logic.tasks.CreateDeviceTokenTask;
import de.bxservice.bxpos.logic.util.SecureEngine;
import de.bxservice.bxpos.logic.webservices.AuthenticationWebService;
import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;
import de.bxservice.bxpos.ui.fragment.AsyncFragment;
import de.bxservice.bxpos.ui.utilities.PreferenceActivityHelper;

/**
 * A login screen that offers login via username/password.
 * Created by Diego Ruiz
 */
public class LoginActivity extends AppCompatActivity implements AsyncFragment.ParentActivity {

    private static final String LOG_TAG = "Login Activity";
    private static final String ASYNC_FRAGMENT_TAG = "LOGIN_ASYNC_FRAGMENT_TAG";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private AsyncFragment mAsyncFragment;
    private CreateDeviceTokenTask createDeviceTokenTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ImageButton settingsButton;

    //Web service request data
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        //// Get the string array
        ArrayList<String> usernameList = PosUser.getUsernameList(getApplicationContext());

        if (usernameList != null && usernameList.size() > 0) {
            // Create the adapter and set it to the AutoCompleteTextView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, usernameList);
            mUsernameView.setAdapter(adapter);

            //The app might have crashed and the data remain there -> Force to clean
            PosSessionPreferenceManagement sessionPreferenceManager = new PosSessionPreferenceManagement(getBaseContext());
            sessionPreferenceManager.cleanSession();
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
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

        mAsyncFragment = (AsyncFragment) getSupportFragmentManager().findFragmentByTag(ASYNC_FRAGMENT_TAG);
        if (mAsyncFragment == null) {
            mAsyncFragment = new AsyncFragment();
            getSupportFragmentManager().beginTransaction().add(mAsyncFragment, ASYNC_FRAGMENT_TAG).commit();
        }

        if (mAsyncFragment.isTaskRunning())
            showProgress(true);
        else {
            //The app might have crashed and the data remain there -> Force to clean
            PosSessionPreferenceManagement sessionPreferenceManager = new PosSessionPreferenceManagement(getBaseContext());
            sessionPreferenceManager.cleanSession();
        }
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

                mAuthTask = new UserLoginTask(username, password);
                mAuthTask.execute((Void) null);

            } else {

                String syncConnPref = sharedPref.getString(PreferenceActivityHelper.KEY_PREF_SYNC_CONN, "");

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

                    PosUser loggedUser = PosUser.getUser(getBaseContext(), username);

                    if (loggedUser != null && loggedUser.authenticateHash(password)) {
                        offlineLogin(loggedUser, password);
                    }
                    //Username does not exist and no internet connection
                    else {
                        //Close soft keyboard to see the message
                        try {
                            View view = this.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
        PosSessionPreferenceManagement sessionPreferenceManager = new PosSessionPreferenceManagement(getBaseContext());

        HashMap<String, String> requestData = new HashMap<>();
        requestData.put(WebServiceRequestData.USERNAME_SYNC_PREF, username);
        requestData.put(WebServiceRequestData.PASSWORD_SYNC_PREF, SecureEngine.encryptIt(password));

        sessionPreferenceManager.create(requestData);
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

    private boolean isCreateTokenTaskRunning() {
        return (createDeviceTokenTask != null) && (createDeviceTokenTask.getStatus() == AsyncTask.Status.RUNNING);
    }

    private boolean isAuthTaskRunning() {
        return (mAuthTask != null) && (    mAuthTask.getStatus() == AsyncTask.Status.RUNNING);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cancel the task if it's running
        if (isAuthTaskRunning()) {
            mAuthTask.cancel(true);
        }
        if (isCreateTokenTaskRunning()) {
            createDeviceTokenTask.cancel(true);
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mUsername = email;
            mPassword = password;
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
                PosUser user = PosUser.getUser(getBaseContext(), mUsername);
                if (user == null) {
                    PosUser newUser = new PosUser();
                    newUser.setUsername(mUsername);
                    newUser.setPassword(mPassword);
                    newUser.createUser(getBaseContext());

                    Log.i(LOG_TAG, "New user created " + mUsername);
                } else if (!user.authenticateHash(mPassword)) {
                    user.setPassword(mPassword);
                    user.updateUser(getBaseContext());

                    Log.i(LOG_TAG, "Password updated for " + mUsername);
                }

                // Read the data needed - Products. MProduct Category - Table ...
                mAsyncFragment.runAsyncTask();

            } else {

                String syncConnPref = sharedPref.getString(PreferenceActivityHelper.KEY_PREF_SYNC_CONN, "");

                // If the sync configuration chosen was Always offline login not allowed
                if (!"0".equals(syncConnPref)) {
                    PosUser loggedUser = PosUser.getUser(getBaseContext(), mUsername);

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

                //Wrong login -> Clean stored data
                PosSessionPreferenceManagement sessionPreferenceManager = new PosSessionPreferenceManagement(getBaseContext());
                sessionPreferenceManager.cleanSession();

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

    @Override
    public void handleReadDataTaskFinish(boolean success) {
        showProgress(false);

        if (success) {

            //Check if the device has already subscribed to the server for push notifications
            SharedPreferences tokenSharedPref = getApplicationContext().getSharedPreferences(BxPosFirebaseInstanceIDService.TOKEN_SHARED_PREF, Context.MODE_PRIVATE);
            boolean isTokenSync = tokenSharedPref.getBoolean(BxPosFirebaseInstanceIDService.TOKEN_SYNC_PREF, false);

            //If it has not been sync -> send it to the server
            if (!isTokenSync) {
                String token = BxPosFirebaseInstanceIDService.getToken();
                Log.d(LOG_TAG, "Registering -> " + token);

                //Send the token to the server
                createDeviceTokenTask = new CreateDeviceTokenTask(tokenSharedPref, this.getBaseContext());
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

