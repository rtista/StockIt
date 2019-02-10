package pt.simov.stockit.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import pt.simov.stockit.R;
import pt.simov.stockit.core.ApiHandler;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Inflate preference screen
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.pref_container, new SettingsFragment())
                .commit();

        // Register on preference change listener
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * On preference change.
     *
     * @param sharedPreferences The shared preferences object.
     * @param key               The key changed.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {

            case "api-location":
                ApiHandler.setBaseUrl(
                        "http", sharedPreferences.getString(key, "127.0.0.1:8000"));
                break;
        }
    }
}
