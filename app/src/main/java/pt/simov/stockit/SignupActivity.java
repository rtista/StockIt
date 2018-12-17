package pt.simov.stockit;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pt.simov.stockit.core.ApiHandler;
import pt.simov.stockit.core.http.HttpClient;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * HTTP client allows network requests for data.
     */
    private OkHttpClient client = HttpClient.getInstance();

    /**
     * Main method runs on activity start.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setTitle(R.string.title_signup);
        EditText name = findViewById(R.id.input_username);


        Button signupBtn = findViewById(R.id.btn_signup);
        signupBtn.setOnClickListener(this);
    }

    /**
     * Create account button handling.
     *
     * @param v -The View.
     */
    @Override
    public void onClick(View v) {

        String username = ((EditText) findViewById(R.id.input_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.input_password)).getText().toString();
        String email = ((EditText) findViewById(R.id.input_email)).getText().toString();

        try {
            Request req = ApiHandler.getInstance().createAccount(username, password, email);

            // Execute the request
            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    Log.e("ACCOUNT_CREATION_FAIL", "Exception: " + e.getMessage());
                    Toast.makeText(SignupActivity.this, "Could not connect to the internet." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) {

                    // Login successful
                    switch (response.code()) {

                        // Successful login
                        case 201:
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignupActivity.this, "Welcome to StockIt", Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Create Login Activity
                            try {
                                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            } catch (ActivityNotFoundException ex) {
                                Toast.makeText(SignupActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                            }
                            break;

                        // Wrong credentials
                        case 400:
                            Log.e("ACCOUNT_CREATION_400", "Wrong credentials");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignupActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;

                        // Server error (5xx)
                        default:
                            Log.e("ACCOUNT_CREATION_500", "Server communication error. Please report at github.");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignupActivity.this, "Server communication error. Please report at github.", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }
                }
            });

        } catch (JSONException e) {

            Toast.makeText(SignupActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
        }
    }
}
