package pt.simov.stockit.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import pt.simov.stockit.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_settings, rootKey);
    }
}
