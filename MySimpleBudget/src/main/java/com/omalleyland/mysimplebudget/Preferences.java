package com.omalleyland.mysimplebudget;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by omal310371 on 6/6/13.
 */
public class Preferences extends PreferenceActivity {

    private static String className;

    private SharedPreferences prefs;
    private String serverAddress;
    private String loginValidationPage;
    private String debitSyncPage;
    private String categorySyncPage;
    private String storeSyncPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        className = getClass().toString();
        Log.v(className, "Creating Preferences");
        super.onCreate(savedInstanceState);

        prefs               = PreferenceManager.getDefaultSharedPreferences(this);
        serverAddress       = prefs.getString(Common.SERVER_ADDRESS_PREFERENCE, "http://www.omalleyland.com");
        loginValidationPage = prefs.getString(Common.SERVER_LOGIN_ADDRESS_PREFERENCE, "userValidation.php");
        debitSyncPage       = prefs.getString(Common.SERVER_DEBIT_ADDRESS_PREFERENCE, "debits.php");
        categorySyncPage    = prefs.getString(Common.SERVER_CATEGORY_ADDRESS_PREFERENCE, "categories.php");
        storeSyncPage       = prefs.getString(Common.SERVER_STORE_ADDRESS_PREFERENCE, "stores.php");

        prefs.edit().putString(Common.SERVER_ADDRESS_PREFERENCE, serverAddress).commit();
        prefs.edit().putString(Common.SERVER_LOGIN_ADDRESS_PREFERENCE, loginValidationPage).commit();
        prefs.edit().putString(Common.SERVER_DEBIT_ADDRESS_PREFERENCE, debitSyncPage).commit();
        prefs.edit().putString(Common.SERVER_CATEGORY_ADDRESS_PREFERENCE, categorySyncPage).commit();
        prefs.edit().putString(Common.SERVER_STORE_ADDRESS_PREFERENCE, storeSyncPage).commit();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            Log.d(className, "Generating Preference Fragment");
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
