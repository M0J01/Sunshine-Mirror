package com.example.andriud.sunshine.app;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by M0J0 on 11/10/2017.
 */

public class SettingsActivity extends PreferenceActivity
            implements Preference.OnPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            // Add 'general preferences, defined in the XML file
            addPreferencesFromResource(R.xml.pref_general);

            // For all preferences, attach an inPrefferenceChangeListener, so the UI cna be updated when they change
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));

            // Attach an onPreferenceChangeListener to each one so the UI can be updated when pref changes
        }

        /**
         * Attache a listener so summary is always updated with value
         * Fires the listener once, to initialize summary, so that value shows up before changed
         *
         */

        private void bindPreferenceSummaryToValue(Preference preference){
            // Set listener to listen for changes
            preference.setOnPreferenceChangeListener(this);

            //Trigger listener immediately with current value used
            onPreferenceChange(preference,
                    PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value){
            String stringValue = value.toString();

            if(preference instanceof ListPreference) {
                // For list preferences, look up display value in preference's entry list
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0){
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }else {
                preference.setSummary(stringValue);
            }
            return true;
        }



}
