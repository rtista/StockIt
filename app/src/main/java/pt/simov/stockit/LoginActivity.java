package pt.simov.stockit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
     * On create activity.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get shared preferences
        this.sharedprefs = this.getSharedPreferences(
                "general_prefs_" + String.valueOf(BuildConfig.APPLICATION_ID), MODE_PRIVATE);

        // Check if authentication token is in the shared preferences
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
        }

        // Auto Login on valid token
        if (!handler.auth().isTokenExpired()) {

            try {

                Intent i = new Intent(LoginActivity.this, WarehouseListActivity.class);
                startActivity(i);
                finish();

            } catch (ActivityNotFoundException ex) {
                Toast.makeText(LoginActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
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
    }

    /**
     * Login button on click listener.
     *
     * @param v The view.
     */
    @Override
    public void onClick(View v) {

        String username = ((EditText) findViewById(R.id.input_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.input_password)).getText().toString();

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

                        // Save token on default app settings
                        sharedprefs.edit().putString(
                                "USER_AUTH_TOKEN",
                                new StringBuilder().append(type).append(":").append(token)
                                        .append(":").append(expiration).toString()
                        ).apply();

                    } catch (JSONException e) {

                        Log.e("AUTH_SUCCESS_FAIL", "JSON Exception: " + e.getMessage());
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
