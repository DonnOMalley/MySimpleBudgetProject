package com.omalleyland.mysimplebudget;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Activity which displays a mainmenu screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements IBackgroundProcessor {

    /* For Managing Preferences/Log Messages */
    private SharedPreferences prefs;
    private String serverAddress;
    private String loginValidationPage;
    private String className;

    // UI references.
    private EditText mUserNameView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        className = getClass().toString();

        //Get Validation Server to pass along to the ValidateLoginCall
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        serverAddress       = prefs.getString(Common.SERVER_ADDRESS_PREFERENCE, "");
        loginValidationPage = prefs.getString(Common.SERVER_LOGIN_ADDRESS_PREFERENCE, "");

        if(!serverAddress.endsWith("/")) {
            serverAddress += "/";
        }

        // Set up the login form.
        mUserNameView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        mUserNameView.setText(prefs.getString(Common.USER_NAME_PREFERENCE, ""));
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

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mainmenu, menu);

        //Hide Menu Items that are not usable from Login Activity
        //Leaves the 'Preferences' Menu Item ONLY
        menu.findItem(R.id.action_logout).setVisible(false);
        menu.findItem(R.id.action_login).setVisible(false);
        menu.findItem(R.id.action_create_category).setVisible(false);
        menu.findItem(R.id.action_create_store).setVisible(false);
        menu.findItem(R.id.action_upload_debits).setVisible(false);
        menu.findItem(R.id.action_full_sync).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //Only need to respond to Preferences Menu Item Selection - All others are hidden
            case R.id.action_login_preferences:
                startActivityForResult(new Intent(LoginActivity.this, Preferences.class), Common.PREFERENCE_RESULT_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String userName;
        String password;

        if(requestCode == Common.PREFERENCE_RESULT_CODE) {
            Log.v(className, "Preference Activity Result :: " + Integer.toString(resultCode));
            userName = prefs.getString(Common.USER_NAME_PREFERENCE, "");
            password = prefs.getString(Common.PASSWORD_PREFERENCE, "");
            if(userName.length() > 0 && password.length() > 0) {
                mUserNameView.setText(userName);
                mPasswordView.setText(password);
                attemptLogin();
            }
            else if (userName.length() > 0) {
                mUserNameView.setText(userName);
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the mainmenu form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual mainmenu attempt is made.
     */
    public void attemptLogin() {
        ValidateLogin validateLogin;
        String userName;
        String password;

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the mainmenu attempt.
        userName = mUserNameView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (password.length() < Common.MIN_PASSWORD_LENGTH) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt mainmenu and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            String validationServer;
            //Write User Preferences to Save login information
            prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            validateLogin = new ValidateLogin(this, this, serverAddress.concat(loginValidationPage), userName, password);
            validateLogin.executeValidation();
        }
    }

    /*
        IBackgroundProcessor Call Back Implementation
     */
    //For handling Login Call Back
    public void processLoginResult(int resultCode, String passwordHash) {
        String userName;
        String password;
        if(resultCode == Common.LOGIN_SUCCESSFUL) {

            //Store user/pass to preferences since login succeeded
            userName = mUserNameView.getText().toString();
            password = mPasswordView.getText().toString();
            prefs.edit().putString(Common.USER_NAME_PREFERENCE, userName).commit();
            prefs.edit().putString(Common.PASSWORD_PREFERENCE, password).commit();

            Intent i = new Intent();
            i.putExtra(Common.LOGIN_RESULT_USER_EXTRA, userName);
            i.putExtra(Common.LOGIN_RESULT_PASSWORD_EXTRA, passwordHash);

            //Pass result code, username and password hash back to calling activity
            setResult(resultCode, i);
            finish();
        }
        else if(resultCode == Common.LOGIN_FAILED) {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
        else if(resultCode == Common.LOGIN_CANCELED) {
            //Do Nothing, User can try again
        }
        else if(resultCode == Common.LOGIN_CONNECTION_ERROR) {
            Toast.makeText(this, "Validation Server Connectoin Error", Toast.LENGTH_LONG).show();
            setResult(Common.LOGIN_CONNECTION_ERROR, new Intent());
            finish();
        }
    }

    //For Updating UI Controls
    public void updateUIControls(ArrayList<Boolean> updateUIControlList) {
        //DO NOTHING - Implemented for IBackgroundProcessor Interface implementation
    }
}
