package com.geoit.climbapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setIcon(R.drawable.ic_icon3);
//            actionBar.hide();
        }
    }

//    /**
//     * Called when the activity has detected the user's press of the back
//     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
//     * chance to handle the back button before the default behavior of
//     * {@link Activity#onBackPressed()} is invoked.
//     *
//     * @see #getOnBackPressedDispatcher()
//     */
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        System.out.println("BACK");
//    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

    }
}