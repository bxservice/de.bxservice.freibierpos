package de.bxservice.bxpos.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.daomanager.PosUserManagement;
import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.logic.model.pos.PosRoles;
import de.bxservice.bxpos.logic.webservices.AuthenticationWebService;
import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity  {

    static final String LOG_TAG = "Login Activity";

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

    // Reference to the role code in the properties file
    private HashMap<String, String> roleCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.selected_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolesSpinner.setAdapter(adapter);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        createDummyUser();

    }

    private void createDummyUser() {

        if (getOfflineUser() == null /*&& !getOfflineUser().getUsername().equals("FreiBierAdmin")*/) {
            PosUser dummyUser = new PosUser();
            dummyUser.setUsername("FreiBierAdmin");
            dummyUser.setPassword("FreiBierAdmin");
            dummyUser.createUser(getApplicationContext());
        }
        else {
            Log.e(LOG_TAG, "Dummy user exists FreiBierAdmin");
        }
    }

    private PosUser getOfflineUser() {
        PosUserManagement userManager = new PosUserManagement(getApplicationContext());
        return userManager.get(1);
    }

    /**
     * Sets the role name in the corresponding language
     * set the hashmap for inner references
     */
    private void getRoleNames() {
        roles = new ArrayList<String>();
        roleCodes = new HashMap<String, String>();

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

            // If the credentials are the offline user - show the corresponding activity
            PosUser offlineUser = getOfflineUser();
            if(username.equals(offlineUser.getUsername())) {
                Intent intent = new Intent(getBaseContext(), OfflineAdminSettingsActivity.class);
                startActivity(intent);
                finish();
                return;
            }
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
            }
        }
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
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            String syncConnPref = sharedPref.getString(OfflineAdminSettingsActivity.KEY_PREF_URL, "");

            // Sets the data to create a ws request to iDempiere
            WebServiceRequestData wsData = WebServiceRequestData.getInstance();
            wsData.setUsername(mUsername);
            wsData.setPassword(mPassword);
            wsData.setRole(mRole);
            wsData.setUrlBase(syncConnPref);

            wsData.readValues(getBaseContext());


            AuthenticationWebService auth = new AuthenticationWebService();

            if (auth.isSuccess())
                return true;

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                // Read the data needed - Products. MProduct Category - Table ...
                new InitiateData().execute(getBaseContext());

            } else {
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
     * params, progress, result
     */
    private class InitiateData extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... contexts) {

            DataMediator data = DataMediator.getInstance();

            if(data.isDataComplete() && !data.isError())
                return true;

            return false;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Boolean result) {
            showProgress(false);

            if(result){
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Snackbar snackbar = Snackbar
                        .make(mLoginFormView, getString(R.string.error_config_server), Snackbar.LENGTH_LONG);

                snackbar.show();
            }
        }
    }
}

