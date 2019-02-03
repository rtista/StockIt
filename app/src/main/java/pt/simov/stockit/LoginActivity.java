package pt.simov.stockit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.domain.AuthToken;
import pt.simov.stockit.core.http.HttpClient;
import pt.simov.stockit.warehouse.WarehouseListActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * HTTP client allows network requests for data.
     */
    private OkHttpClient client = HttpClient.getInstance();

    /**
     * API Handler allows StockIt backend API communication.
     */
    private final ApiHandler handler = ApiHandler.getInstance();

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
            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    Log.e("AUTH_FAIL", "Exception: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) {

                    // Login successful
                    final int code = response.code();
                    switch (code) {

                        // Successful login
                        case 201:

                            try {
                                JSONObject resp = new JSONObject(response.body().string());

                                handler.auth().setAuthToken(
                                        new AuthToken(
                                                resp.getString("auth_type"),
                                                resp.getString("token"),
                                                resp.getInt("expires_on")
                                        )
                                );

                                // Save token on default app settings
                                sharedprefs.edit().putString(
                                        "USER_AUTH_TOKEN",
                                        new StringBuilder()
                                                .append(resp.getString("auth_type"))
                                                .append(":")
                                                .append(resp.getString("token"))
                                                .append(":")
                                                .append(resp.getString("expires_on"))
                                                .toString()
                                ).apply();

                            } catch (JSONException e) {
                                Log.e("AUTH_SUCCESS_FAIL", "How is this even possible");
                            } catch (IOException e) {
                                Log.e("AUTH_SUCCESS_FAIL", "How is this even possible");
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Welcome to StockIt", Toast.LENGTH_SHORT).show();
                                }
                            });

                            try {

                                Intent i = new Intent(LoginActivity.this, WarehouseListActivity.class);
                                startActivity(i);
                                finish();

                            } catch (ActivityNotFoundException ex) {
                                Toast.makeText(LoginActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                            }
                            break;

                        // Wrong credentials
                        case 400:
                            Log.e("AUTH_400", "Missing parameters.");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Please fill all the fields.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;

                        // Wrong credentials
                        case 401:
                            Log.e("AUTH_401", "Wrong credentials");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;

                        // Server error (5xx)
                        default:
                            Log.e("AUTH_500", "Server communication error. Please report at github.");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Server communication error. Please report at github. Code: " + code, Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }
                }
            });

        } catch (JSONException e) {

            Toast.makeText(LoginActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
        }
    }
}
