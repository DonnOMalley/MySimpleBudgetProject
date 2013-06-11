package com.omalleyland.mysimplebudget;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by omal310371 on 6/6/13.
 */
public class Preferences extends PreferenceActivity {

    private static String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        className = getClass().toString();
        Log.v(className, "Creating Preferences");
        super.onCreate(savedInstanceState);
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
