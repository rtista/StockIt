package pt.simov.stockit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.domain.AuthToken;
import pt.simov.stockit.core.http.HttpClient;
import pt.simov.stockit.core.http.StockItCallback;
import pt.simov.stockit.settings.SettingsActivity;
import pt.simov.stockit.warehouse.WarehouseListActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * API Handler allows StockIt backend API communication.
     */
    private final ApiHandler handler = ApiHandler.getInstance();

    /**
     * HTTP client allows network requests for data.
     */
    private OkHttpClient client = HttpClient.getInstance();

    /**
     * The default shared preferences object.
     */
    private SharedPreferences sharedprefs = null;

    /**
     * The "Remember My Credentials" checkbox.
     */
    private CheckBox rememberMeCheckbox;

    /**
     * Credentials edit texts.
     */
    private EditText et_username;
    private EditText et_password;

    /**
     * On create activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Read API Location from Settings
        String location = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("api-location", "127.0.0.1:8000");

        // Initialize API Handler URL
        ApiHandler.setBaseUrl("http", location);

        // Get layout components
        this.et_username = findViewById(R.id.input_username);
        this.et_password = findViewById(R.id.input_password);
        this.rememberMeCheckbox = findViewById(R.id.remember_me_check);

        // Get shared preferences
        this.sharedprefs = this.getSharedPreferences(
                "general_prefs_" + String.valueOf(BuildConfig.APPLICATION_ID), MODE_PRIVATE);

        // Check if authentication token is in the shared preferences and login
        if (this.sharedprefs.contains("USER_AUTH_TOKEN")
                && !this.sharedprefs.getString("USER_AUTH_TOKEN", "").isEmpty()) {

            String[] tokensettings = this.sharedprefs.getString("USER_AUTH_TOKEN", "").split(":");

            // Set authentication token
            this.handler.auth().setAuthToken(
                    new AuthToken(
                            tokensettings[0],
                            tokensettings[1],
                            Long.parseLong(tokensettings[2])
                    )
            );

            // Auto Login on valid token
            if (!this.handler.auth().isTokenExpired()) {

                try {

                    Intent i = new Intent(LoginActivity.this, WarehouseListActivity.class);
                    startActivity(i);
                    finish();

                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(LoginActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Register account text view
        TextView tv = findViewById(R.id.link_signup);
        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                try {
                    Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(i);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(LoginActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Login button
        Button loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(this);

        // Check if user credentials are on the shared preferences and login
        if (this.sharedprefs.contains("USER_CREDENTIALS")
                && !this.sharedprefs.getString("USER_CREDENTIALS", "").isEmpty()) {

            // Set checkbox checked
            this.rememberMeCheckbox.setChecked(true);

            // Get credentials from shared preferences
            String[] credentials = this.sharedprefs
                    .getString("USER_CREDENTIALS", "").split(":");

            // Set values on fields
            this.et_username.setText(credentials[0]);
            this.et_password.setText(credentials[1]);
        }
    }

    /**
     * Inflate the options menu.
     *
     * @param menu Options menu.
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_signup_options, menu);
        return true;
    }

    /**
     * On Option Menu item selection.
     *
     * @param item The selected item.
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {

            // On Add warehouse option
            case R.id.lom_settings:

                // Start settings activity
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);

                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     * Login button on click listener.
     *
     * @param v The view.
     */
    @Override
    public void onClick(View v) {

        final String username = this.et_username.getText().toString();
        final String password = this.et_password.getText().toString();

        try {
            Request req = handler.auth().bearer(username, password);

            // Execute the request
            client.newCall(req).enqueue(new StockItCallback() {

                @Override
                public void onCreated(JSONObject body) {

                    try {

                        // Get parameters from body
                        String type = body.getString("auth_type");
                        String token = body.getString("token");
                        long expiration = body.getInt("expires_on");

                        // Set authentication token on API handler
                        handler.auth().setAuthToken(
                                new AuthToken(type, token, expiration)
                        );

                        // Save token on default app app_settings
                        sharedprefs.edit().putString(
                                "USER_AUTH_TOKEN",
                                new StringBuilder().append(type).append(":").append(token)
                                        .append(":").append(expiration).toString()
                        ).commit();

                    } catch (JSONException e) {

                        Log.e("AUTH_SUCCESS_FAIL", "JSON Exception: " + e.getMessage());
                    }

                    // Save credentials in shared preferences on allow
                    if (LoginActivity.this.rememberMeCheckbox.isChecked()) {

                        // Save token on default app app_settings
                        sharedprefs.edit().putString(
                                "USER_CREDENTIALS",
                                new StringBuilder().append(username)
                                        .append(":").append(password).toString()
                        ).apply();

                    } else {

                        sharedprefs.edit().remove("USER_CREDENTIALS").apply();
                    }

                    // Toast user with welcome
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Welcome to StockIt", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Launch WarehouseList activity
                    try {

                        Intent i = new Intent(LoginActivity.this, WarehouseListActivity.class);
                        startActivity(i);
                        finish();

                    } catch (ActivityNotFoundException ex) {

                        Log.e("ACTIVITY_NOT_FOUND", "Exception: " + ex.getMessage());
                    }
                }

                // Wrong credentials
                @Override
                public void onUnauthorized(JSONObject body) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                // Internal Server Error
                @Override
                public void onInternalServerError(JSONObject body) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Server communication error. Please report at github.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (JSONException e) {

            Toast.makeText(LoginActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
        }
    }
}
