package pt.simov.stockit.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pt.simov.stockit.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.pref_container, new SettingsFragment())
                .commit();
    }
}
